package com.hanslv.test.machine.learning.dl4j.stock.rectangle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;

import com.hanslv.test.machine.learning.dl4j.rnn.NNFactory;
import com.hanslv.test.machine.learning.encog.util.DbUtil;

/**
 * 股票LSTM模拟
 * 
 * http://localhost:9000/train
 * 
 * @author hanslv
 *
 */
public class RectangleLSTMTrainerNew {
	static final String TRAIN_DATE = "2019-10-05";//训练日期
	
	/*
	 * 训练日期					测试输入				实际输出
	 * 
	 */
	static final double CHECK_INPUT = 4.0;
	static final double CHECK_OUTPUT = 1.75;
	
	
	static final int STEP_LONG = 350;//训练步长
	static final int SINGLE_BATCH_SIZE = 5;//数据块中包含的数据量
	static final int EPOCH = 10000;//训练纪元
	
	static final int INPUT_SIZE = 5;//输入神经元数量
	static final int IDEAL_OUTPUT_SIZE = 2;//输出神经元数量
	static int HIDELAYER_A_RIGHT = 50;//隐藏层A权重
	static int HIDELAYER_B_RIGHT = 100;//隐藏层B权重
	static int HIDELAYER_C_RIGHT = 50;//隐藏层C权重
	static double DROP_OUT = 0.02;//损失率
	static Activation ACTIVATION = Activation.RELU;//激活函数
	static Map<String , Integer> hideLayerMap;
	
	static {
		hideLayerMap = new HashMap<>();
		hideLayerMap.put(RectangleLSTMBuilder.INPUT_SIZE , INPUT_SIZE);
		hideLayerMap.put(RectangleLSTMBuilder.IDEALOUTPUT_SIZE , IDEAL_OUTPUT_SIZE);
		hideLayerMap.put(RectangleLSTMBuilder.HIDELAYERA_RIGHT , HIDELAYER_A_RIGHT);
		hideLayerMap.put(RectangleLSTMBuilder.HIDELAYERB_RIGHT , HIDELAYER_B_RIGHT);
		hideLayerMap.put(RectangleLSTMBuilder.HIDELAYERC_RIGHT , HIDELAYER_C_RIGHT);
	}
	
	public static void main(String[] args) {
		try {
			train(TRAIN_DATE , "1" , true);
		}finally {
			NNFactory.stopUI();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 执行训练
	 */
	private static void train(String startDate , String stockId , boolean testOrNot) {
		/*
		 * 获取全部数据List
		 */
		List<String> sourceDataList = getSourceData(stockId , startDate);
		if(sourceDataList.size() != STEP_LONG - 1) return;
		
		/*
		 * 转换为DataSetIterator
		 */
		DataSetIterator iterator = getTrainDataIterator(sourceDataList);
		
		/*
		 * 数据归一化处理
		 */
		normalize(iterator);
		DataNormalization normalize = (DataNormalization) iterator.getPreProcessor();
		
		/*
		 * 获取神经网络
		 */
		MultiLayerNetwork net = RectangleLSTMBuilder.build(hideLayerMap , ACTIVATION , DROP_OUT);
		NNFactory.initUI(net);
		System.out.println(net.summary());
		
		/*
		 * 对数据进行拟合处理
		 */
		for(int i = 0 ; i < EPOCH ; i++) {
			net.fit(iterator);
			iterator.reset();
		}
			
		/*
		 * 查看训练效果
		 */
		Evaluation eval = net.evaluate(iterator);
		System.out.println(eval);
			
//		double[][] checkInputDoubleArray = new double[1][1];
//		checkInputDoubleArray[0][0] = CHECK_INPUT;
//		INDArray checkInput = Nd4j.create(checkInputDoubleArray);
//		double[][] checkOutputDoubleArray = new double[1][1];
//		checkOutputDoubleArray[0][0] = CHECK_OUTPUT;
//		INDArray checkOutput = Nd4j.create(checkOutputDoubleArray);
//		DataSet checkDataSet = new DataSet(checkInput , checkOutput);
//		normalize.transform(checkDataSet);
//		
//		INDArray forcastOutput = net.output(checkInput , false);
//		DataSet forcasetDataSet = new DataSet(checkInput , forcastOutput);
//		normalize.revert(forcasetDataSet);
//		System.out.println(forcasetDataSet);
	}
	
	
	
	/**
	 * 将数据List转换为ListDataSetIterator
	 * @param trainDataList
	 * @return
	 */
	private static DataSetIterator getTrainDataIterator(List<String> trainDataList) {
		double[][] inputDoubleArray = new double[STEP_LONG][INPUT_SIZE];
		double[][] outputDoubleArray = new double[STEP_LONG][IDEAL_OUTPUT_SIZE];
		
		for(int i = 0 ; i < trainDataList.size() ; i++) {
			String[] trainDataArray = trainDataList.get(i).split(",");
			for(int j = 0 ; j < INPUT_SIZE ; j++) inputDoubleArray[i][j] = Double.parseDouble(trainDataArray[j]);
			for(int j = 0 ; j < IDEAL_OUTPUT_SIZE ; j++) outputDoubleArray[i][j] = Double.parseDouble(trainDataArray[j + INPUT_SIZE]);
		}
		
		INDArray inputArray = Nd4j.create(inputDoubleArray);
		INDArray outputArray = Nd4j.create(outputDoubleArray);
		
		List<DataSet> trainDataSetList = new DataSet(inputArray , outputArray).asList();
		
		return new ListDataSetIterator<>(trainDataSetList);
	}
	
	/**
	 * 数据归一化处理
	 * @param trainDataSetIterator
	 */
	private static void normalize(DataSetIterator trainDataSetIterator) {
		DataNormalization normalizer = new NormalizerMinMaxScaler();
//		DataNormalization normalizer = new NormalizerStandardize();
		normalizer.fitLabel(true);
		normalizer.fit(trainDataSetIterator);
		trainDataSetIterator.setPreProcessor(normalizer);
	}
	
	/**
	 * 获取训练数据List
	 * @param stockId
	 * @param date
	 * @return
	 */
	public static List<String> getSourceData(String stockId , String date){
		/*
		 * 获取标准数据
		 */
		List<String> rawDataList = DbUtil.getPriceInfo(stockId , date , STEP_LONG * SINGLE_BATCH_SIZE);
		BigDecimal max = null;//最大值
		BigDecimal min = null;//最小值
		BigDecimal startPrice = null;//开盘价
		BigDecimal endPrice = null;//收盘价
		BigDecimal turnoverRate = BigDecimal.ZERO;//换手率
		int counterA = 0;
		List<String> bufferList = new ArrayList<>();
		for(String rawData : rawDataList) {
			String[] rawDataArray = rawData.split(",");
			BigDecimal currentMax = new BigDecimal(rawDataArray[2]);//最大值
			BigDecimal currentMin = new BigDecimal(rawDataArray[3]);//最小值
			BigDecimal currentStartPrice = new BigDecimal(rawDataArray[4]);//开盘价
			BigDecimal currentEndPrice = new BigDecimal(rawDataArray[5]);//收盘价
			BigDecimal currentTurnoverRate = new BigDecimal(rawDataArray[6]);//换手率
			counterA++;
			
			if(max == null || max.compareTo(currentMax) < 0) max = currentMax;
			if(min == null || min.compareTo(currentMin) > 0) min = currentMin;
			if(counterA == 1) startPrice = currentStartPrice;
			if(counterA == SINGLE_BATCH_SIZE) endPrice = currentEndPrice;
			turnoverRate = turnoverRate.add(currentTurnoverRate);
			if(counterA == SINGLE_BATCH_SIZE) {
				String sourceData = 
						max + "," + 
						min + "," + 
						startPrice + "," + 
						endPrice + "," + 
						turnoverRate;
				bufferList.add(sourceData);
				max = null;
				min = null;
				startPrice = null;
				endPrice = null;
				turnoverRate = BigDecimal.ZERO;
				counterA = 0;
			}
		}
		
		/*
		 * 拼接实际结果
		 */
		List<String> sourceDataList = new ArrayList<>();
		for(int i = 0 ; i < bufferList.size() ; i++) {
			if(i + 1 < bufferList.size()) {
				String[] nextValArray = bufferList.get(i + 1).split(",");
				String nextMax = nextValArray[0];
				String nextMin = nextValArray[1];
				String sourceData = bufferList.get(i) + "," + nextMax + "," + nextMin;
				sourceDataList.add(sourceData);
			}
		}
		return sourceDataList;
	}
}
