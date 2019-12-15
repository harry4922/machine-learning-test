package com.hanslv.test.machine.learning.dl4j.stock.rectangle;

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
	
	
	public static ForecastResult train(String stockId , String endDate) {
		/*
		 * 数据准备
		 */
		int stepLong = trainStepSize + testStepSize;//总步长=训练数据步长+测试数据步长
		List<String> allDataList = DbUtil.getRectangleArea(stockId, stepLong , endDate , batchSize , singleBatchSize , true);
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
		return docheck(testDataList , lstmNetwork , iteratorList.get(1));
	}
	
	public static enum ForecastResult{
		TRUE,FALSE,EXCLUDE
	}
	
	/**
	 * 判断预测结果是否准确
	 * @return
	 */
	private static ForecastResult docheck(List<String> testDataList , MultiLayerNetwork lstmNetwork , DataSetIterator forecastData) {
		/*
		 * 获取当前标准化器
		 */
		NormalizerMinMaxScaler normalizerStandardize = (NormalizerMinMaxScaler) forecastData.getPreProcessor();
		
		int trueCounter = 0;
		int testTotal = forecastData.numExamples() - 1;
		
		/*
		 * 首先执行训练步长-1次预测，并判断结果是否准确
		 */
		for(int i = 0 ; i < testTotal ; i++) {
			double result = doForecast(lstmNetwork , forecastData.next(1) , normalizerStandardize);
			double checkData = Double.parseDouble(testDataList.get(i).split(",")[1]);
//			System.out.println("Test: " + result + "," + checkData);
			if(Math.abs(result - checkData) <= errorLimit) trueCounter++;
		}
		
		/*
		 * 前几次预测都准确则预测当前时间的价格
		 */
		if(trueCounter == testTotal) {
			double result = doForecast(lstmNetwork , forecastData.next(1) , normalizerStandardize);
			double checkData = Double.parseDouble(testDataList.get(testTotal).split(",")[1]);
			System.out.println("!!!: " + result + "," + checkData);
			if(Math.abs(result - checkData) <= errorLimit) return ForecastResult.TRUE;
			else return ForecastResult.FALSE;
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
