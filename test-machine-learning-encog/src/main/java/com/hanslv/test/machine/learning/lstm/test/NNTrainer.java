package com.hanslv.test.machine.learning.lstm.test;

import java.util.List;
import java.util.Random;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;

public class NNTrainer {
	
	static final int INPUT_SIZE = 1;//输入神经元数量
	static final int IDEAL_OUTPUT_SIZE = 1;//输出神经元数量
	
	/*
	 * 10				10 * 10 * 1.5 + 10 * 2.0 + 4					= 174
	 * 5				5 * 5 * 1.5 + 5 * 2 + 4							= 51.5
	 * 34				34 * 34 * 1.5 + 34 * 2 + 4						= 1806
	 * 1				1 * 1 * 1.5 + 1 * 2 + 4							= 7.5
	 */
	static final double A = 1.5;
	static final double B = 2.0;
	static final double C = 4;
	static final double TEST_INPUT = 34;
	
	static final int EPOCH = 100;//训练纪元
	static final int TRAIN_DATA_SIZE = 20000;//训练数据量
	static final int RANDOM_MIN_RANGE = -40;//随机数最小限制
	static final int RANDOM_MAX_RANGE = 40;//随机数最大限制
	
	
	/**
	 * 执行训练
	 * @return
	 */
	public static void doTrain() {
		/*
		 * 获取训练数据
		 */
		train(getTrainData(A , B , C));		
		
	}
	
	
	/**
	 * 训练LSTM
	 * @param trainDataList 训练数据集合
	 * @param testDataList 测试数据集合
	 * @return
	 */
	private static void train(DataSetIterator trainDataSetIterator) {
		
		/*
		 * 数据归一化处理
		 */
		normalize(trainDataSetIterator);
		DataNormalization normalize = (DataNormalization) trainDataSetIterator.getPreProcessor();
		
		/*
		 * 获取LSTM神经网络
		 */
		MultiLayerNetwork lstmNetwork = NNBuilder.build(INPUT_SIZE , IDEAL_OUTPUT_SIZE);
		System.out.println(lstmNetwork.summary());
		
		/*
		 * 执行预测
		 */
		for(int i = 0 ; i < EPOCH ; i++) {
			lstmNetwork.fit(trainDataSetIterator);
			trainDataSetIterator.reset();
		}
		
		/*
		 * 测试
		 */
		INDArray checkInput = Nd4j.create(new double[] {TEST_INPUT} , new int[] {1 , 1});
		INDArray checkOutput = Nd4j.create(new double[] {0} , new int[] {1 , 1});
		DataSet checkDataSet = new DataSet(checkInput , checkOutput);
		normalize.transform(checkDataSet);
		
		INDArray forcastOutput = lstmNetwork.output(checkInput , false);
		DataSet resultDataSet = new DataSet(checkDataSet.getFeatures() , forcastOutput);
		normalize.revert(resultDataSet);
		System.out.println(resultDataSet);
		
		Evaluation eval = lstmNetwork.evaluate(trainDataSetIterator);
		System.out.println(eval);
	}
	
	
	
	/**
	 * 获取训练源数据
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	private static DataSetIterator getTrainData(double a , double b , double c){
		Random random = new Random();
		
//		double[] inputArray = new double[TRAIN_DATA_SIZE];
//		double[] outputArray = new double[TRAIN_DATA_SIZE];
//		/*
//		 * 生成训练次数个随机样本数据
//		 */
//		for(int i = 0 ; i < TRAIN_DATA_SIZE ; i++) {
//			double randomVal = RANDOM_MIN_RANGE + (RANDOM_MAX_RANGE - RANDOM_MIN_RANGE) * random.nextDouble();
////			double randomVal = i + random.nextDouble();
//			inputArray[i] = randomVal;
//			outputArray[i] = getY(a , b , c , randomVal);
//		}
//		INDArray input = Nd4j.create(inputArray , new int[] {TRAIN_DATA_SIZE , INPUT_SIZE});
//		INDArray output = Nd4j.create(outputArray , new int[] {TRAIN_DATA_SIZE , IDEAL_OUTPUT_SIZE});
//		
//		List<DataSet> dataSetList = new DataSet(input , output).asList();
		
		
		double[][] inputArray = new double[TRAIN_DATA_SIZE][INPUT_SIZE];
		double[][] outputArray = new double[TRAIN_DATA_SIZE][IDEAL_OUTPUT_SIZE];
		
		for(int i = 0 ; i < TRAIN_DATA_SIZE ; i++) {
			double randomVal = RANDOM_MIN_RANGE + (RANDOM_MAX_RANGE - RANDOM_MIN_RANGE) * random.nextDouble();
			for(int j = 0 ; j < INPUT_SIZE ; j++) inputArray[i][j] = randomVal;
			for(int j = 0 ; j < IDEAL_OUTPUT_SIZE ; j++) outputArray[i][j] = getY(a , b , c , randomVal);
		}
		INDArray input = Nd4j.create(inputArray);
		INDArray output = Nd4j.create(outputArray);
		
		List<DataSet> dataSetList = new DataSet(input , output).asList();
		
		return new ListDataSetIterator<>(dataSetList);
	}
	
	
	
	/**
	 * 计算抛物线上当前点的Y值
	 * y=ax^2+bx+c
	 * @param a
	 * @param b
	 * @param c
	 * @param x
	 * @return
	 */
	private static double getY(double a , double b , double c , double x) {
		double paramA = a * x * x;
		double paramB = b * x;
		return paramA + paramB + c;
	}
	
	
	
	/**
	 * 数据标准化
	 * @param trainDataSet
	 * @return
	 */
	private static void normalize(DataSetIterator trainDataSetIterator) {
		DataNormalization normalizer = new NormalizerMinMaxScaler();
		normalizer.fitLabel(true);
		normalizer.fit(trainDataSetIterator);
		trainDataSetIterator.setPreProcessor(normalizer);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
//		Nd4j.ENFORCE_NUMERICAL_STABILITY = true;
//		System.out.println(getY(A , B , C , 4));
//		for(int i = 0 ; i < 15 ; i++) System.out.println(getY(A , B , C , i));
//		for(String data : getTrainData(1.5 , 2.5 , 4 , 10)) System.out.println(data);
//		System.out.println(doTrain());
		doTrain();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 获取全部训练数据
	 * [1,    		2,    		3]
	 * 0,0,0  		0,1,0 		0,0,1  
	 * [2,    		3,    		4]
	 * 1,0,0  		1,1,0 		1,0,1  
	 * @param trainDataList
	 * @return
	 */
//	static DataSet getTrainData(List<String> trainDataList){
//		INDArray input = Nd4j.zeros(TRAIN_DATA_SIZE , INPUT_SIZE);//输入神经元数据
//		INDArray idealOutput = Nd4j.zeros(TRAIN_DATA_SIZE , IDEAL_OUTPUT_SIZE);//输出神经元数据
//		/*
//		 * 将训练数据放入向量中
//		 */
//		for(int i = 0 ; i < TRAIN_DATA_SIZE ; i++) {
//			String[] trainDataArray = trainDataList.get(i).split(",");
//			/*
//			 * 遍历数据数组
//			 */
//			for(int j = 0 ; j < trainDataArray.length ; j++) {
//				/*
//				 * 输出神经元数据
//				 */
//				double currentVal = Double.parseDouble(trainDataArray[j]);
//				/*
//				 * 输出神经元向量数据
//				 */
//				if(j >= INPUT_SIZE) idealOutput.putScalar(i , j - INPUT_SIZE , currentVal);
//				/*
//				 * 输入神经元向量数据
//				 */
//				else input.putScalar(i , j , currentVal);				
//			}
//		}
//		return new DataSet(input , idealOutput);
//	}
}
