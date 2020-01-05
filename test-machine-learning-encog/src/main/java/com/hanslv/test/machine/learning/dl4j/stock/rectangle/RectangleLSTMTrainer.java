package com.hanslv.test.machine.learning.dl4j.stock.rectangle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;

import com.hanslv.test.machine.learning.encog.util.DbUtil;
import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

/**
 * 矩形预测LSTM训练器
 * @author hanslv
 *
 */
public class RectangleLSTMTrainer {
	static int inputSize = 1;//输入神经元数量
	static int idealOutputSize = 1;//输出神经元数量
	static int hideLayerARight = 400;//隐藏层A权重
	static int hideLayerBRight = 200;//隐藏层B权重
	static int hideLayerCRight = 100;//隐藏层C权重
	
	static double dropOut = 0.2;//损失率
	static Activation activation = Activation.SOFTSIGN;//激活函数
	static Map<String , Integer> hideLayerMap;
	
	static {
		hideLayerMap = new HashMap<>();
		hideLayerMap.put(RectangleLSTMBuilder.INPUT_SIZE , inputSize);
		hideLayerMap.put(RectangleLSTMBuilder.IDEALOUTPUT_SIZE , idealOutputSize);
		hideLayerMap.put(RectangleLSTMBuilder.HIDELAYERA_RIGHT , hideLayerARight);
		hideLayerMap.put(RectangleLSTMBuilder.HIDELAYERB_RIGHT , hideLayerBRight);
		hideLayerMap.put(RectangleLSTMBuilder.HIDELAYERC_RIGHT , hideLayerCRight);
	}
	
	static int trainStepSize = 25;//训练数据步长，用于训练神经网络
	static int testStepSize = 3;//测试数据步长。神经网络训练完毕后用于测试神经网络是否准确
	static int singleBatchSize = 5;//单步长包含的数据量
	static int batchSize = 5;//批次长度
	static int epoch = 10;//训练次数
	static double errorLimit = 0.125;//误差容忍范围%
	static double buyErrorLimit = 1;//买入损失容忍范围
	static double suggestRate = 0.02;//建议盈利百分比
	
	
	public static ForecastResult train(String stockId , String endDate) {
//		System.out.println("正在计算：" + stockId + " endDate=" + endDate);
		/*
		 * 数据准备
		 */
		int stepLong = trainStepSize + testStepSize;//总步长=训练数据步长+测试数据步长
		List<String> allDataList = DbUtil.getRectangleArea(stockId, stepLong , endDate , batchSize , singleBatchSize , false);
		if(allDataList.size() != stepLong) {
//			System.err.println("当前数据有误：" + stockId + "," + endDate);
//			System.exit(0);
			return ForecastResult.EXCLUDE;
		}
		
		/*
		 * 拆分训练数据、测试数据
		 */
		List<String> trainDataList = new ArrayList<>();
		for(int i = 0 ; i < trainStepSize ; i++) trainDataList.add(allDataList.get(i));
		List<String> testDataList = new ArrayList<>();
		for(int i = trainStepSize ; i < allDataList.size() ; i++) testDataList.add(allDataList.get(i));
		
		/*
		 * 数据标准化
		 */
		List<DataSetIterator> iteratorList = SourceDataParser.dl4jDataNormalizer(trainDataList , testDataList , idealOutputSize);
		
		/*
		 * 获取LSTM
		 */
		MultiLayerNetwork lstmNetwork = RectangleLSTMBuilder.build(hideLayerMap , activation , dropOut);
		
		/*
		 * 训练模型
		 */
		for(int i = 0 ; i < epoch ; i++) {
			lstmNetwork.fit(iteratorList.get(0));
			lstmNetwork.rnnClearPreviousState();
		}
		
		/*
		 * 判断预测结果
		 */
		return docheck(testDataList , lstmNetwork , iteratorList.get(1) , endDate , stockId);
	}
	
	public static enum ForecastResult{
		TRUE,FALSE,EXCLUDE
	}
	
	/**
	 * 判断预测结果是否准确
	 * @return
	 */
	private static ForecastResult docheck(List<String> testDataList , MultiLayerNetwork lstmNetwork , DataSetIterator forecastData , String endDate , String stockId) {
		/*
		 * 获取当前标准化器
		 */
		NormalizerMinMaxScaler normalizerStandardize = (NormalizerMinMaxScaler) forecastData.getPreProcessor();
		
		int trueCounter = 0;
		int testTotal = forecastData.numExamples() - 1;
		double checkDataBuffer = 0;
		
		/*
		 * 首先执行训练步长-1次预测，并判断结果是否准确
		 */
		for(int i = 0 ; i < testTotal ; i++) {
			double result = doForecast(lstmNetwork , forecastData.next(1) , normalizerStandardize);
			checkDataBuffer = Double.parseDouble(testDataList.get(i).split(",")[1]);
			if(Math.abs(result - checkDataBuffer) <= errorLimit) trueCounter++;
		}
		
		/*
		 * 前几次预测都准确则预测当前时间的价格
		 */
		if(trueCounter == testTotal) {
			/*
			 * 2019-12-16日修改，在插入结果前先判断均线斜率
			 */
			BigDecimal averageSlope = new BigDecimal(DbUtil.getAverage(stockId, endDate , 89)[1]);
			if(averageSlope.compareTo(BigDecimal.ZERO) < 0) return ForecastResult.EXCLUDE;
			
			/*
			 * 计算结果是否准确
			 */
			double result = doForecast(lstmNetwork , forecastData.next(1) , normalizerStandardize);
			/*
			 * 2019-12-17修改，只保留预测矩形面积小于等于当前矩形面积的预测结果
			 */
			if(result <= checkDataBuffer) {
				/*
				 * 2019-12-17修改，筛选当前价格接近当前矩形最低价的结果
				 * 首先获取endDate之前的singleBatchSize * (batchSize - 1)个数据，
				 * 获取其中的最低价smallBatchLow
				 * 获取当前的价格currentStockPrice
				 * 判断(currentStockPrice-smallBatchLow)/currentStockPrice <= buyErrorLimit
				 */
				BigDecimal smallBatchLow = new BigDecimal(DbUtil.getRectangleMaxAndLow(stockId , 1 , endDate , batchSize - 1 , singleBatchSize , false).get(0).split(",")[1]);
				BigDecimal currentStockPrice = new BigDecimal(DbUtil.getPriceInfo(stockId, endDate, 1).get(0).split(",")[5]);
//				if(currentStockPrice.compareTo(smallBatchLow) >= 0 && currentStockPrice.multiply(BigDecimal.ONE.subtract(new BigDecimal(buyErrorLimit))).compareTo(smallBatchLow) >= 0) {
				if(currentStockPrice.subtract(smallBatchLow).divide(currentStockPrice , 3 , BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(buyErrorLimit)) <= 0) {
					/*
					 * 2019-12-17修改，判断预测矩形最终价格是否大于所给的建议买入价格
					 * 获取endDate + 1数据量日期的singleBatchSize - 1的最高价forcastHigh；改：因为无法预测到最高价，因此改为获取最后一天的收盘价
					 */
					String checkDate = DbUtil.changeDate(stockId, endDate, singleBatchSize , false);//日期向后移动singleBatchSize个数据量
					BigDecimal lastPrice = new BigDecimal(DbUtil.getPriceInfo(stockId, checkDate , 1).get(0).split(",")[5]);
					System.out.println("stockId=" + stockId + " Date=" + endDate + " suggestBuyPrice=" + currentStockPrice + " lastPrice=" + lastPrice);
					if(lastPrice.compareTo(currentStockPrice) >= 0) return ForecastResult.TRUE;
					else return ForecastResult.FALSE;
				}
			}
		}
		return ForecastResult.EXCLUDE;
	}

	/**
	 * 执行预测
	 * @param lstmNetwork
	 * @param input
	 * @param normalizerStandardize
	 * @return
	 */
	private static double doForecast(MultiLayerNetwork lstmNetwork , DataSet input , NormalizerMinMaxScaler normalizerStandardize) {
		INDArray output = lstmNetwork.rnnTimeStep(input.getFeatures());
		DataSet resultDataSet = new DataSet(input.getFeatures() , output);
		/*
		 * 反标准化结果并判断是否符合标准
		 */
		normalizerStandardize.revert(resultDataSet);
		INDArray resultOutput = resultDataSet.getLabels();
		return resultOutput.getDouble(0);
	}
}
