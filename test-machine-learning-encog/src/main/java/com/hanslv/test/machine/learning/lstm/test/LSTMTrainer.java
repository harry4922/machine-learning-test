package com.hanslv.test.machine.learning.lstm.test;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;

public class LSTMTrainer {
	
	static final int INPUT_SIZE = 1;//输入神经元数量
	static final int IDEAL_OUTPUT_SIZE = 1;//输出神经元数量
	
	static final double A = 1.5;
	static final double B = 2.0;
	static final double C = 4;
	
	static final int EPOCH = 100;//训练纪元
	static final int TRAIN_DATA_SIZE = 5;//训练数据量
	
	
	static String realResult = "";//真实结果
	
	
	/**
	 * 执行训练
	 * @return
	 */
	public static void doTrain() {
		/*
		 * 获取训练数据
		 */
		List<String> trainSourceData = getSourceData(A , B , C);
		DataSet trainDataSet = getTrainData(trainSourceData);
		train(trainDataSet);
	}
	
	
	/**
	 * 训练LSTM
	 * @param trainDataList 训练数据集合
	 * @param testDataList 测试数据集合
	 * @return
	 */
	private static void train(DataSet trainDataSet) {
		
		/*
		 * 数据归一化处理
		 */
		DataNormalization normalize = normalize(trainDataSet);
		
		/*
		 * 获取LSTM神经网络
		 */
		MultiLayerNetwork lstmNetwork = LSTMBuilder.build(INPUT_SIZE , IDEAL_OUTPUT_SIZE);
		System.out.println(lstmNetwork.summary());
		
		/*
		 * 执行预测
		 */
		for(int i = 0 ; i < EPOCH ; i++) lstmNetwork.fit(trainDataSet);
		
		/*
		 * 获取训练结果
		 */
		INDArray input = trainDataSet.getFeatures();
		INDArray output = lstmNetwork.output(input);
//		System.out.println(output);
		
		DataSet result = new DataSet(input , output);
		
//		/*
//		 * 反归一化处理
//		 */
		normalize.revert(result);
		System.out.println(result);
		
		
		Evaluation eval = new Evaluation();
		eval.eval(trainDataSet.getLabels() , output);
		System.out.println(eval.stats());
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
	private static DataSet getTrainData(List<String> trainDataList){
		INDArray input = Nd4j.zeros(TRAIN_DATA_SIZE , INPUT_SIZE);//输入神经元数据
		INDArray idealOutput = Nd4j.zeros(TRAIN_DATA_SIZE , IDEAL_OUTPUT_SIZE);//输出神经元数据
		/*
		 * 将训练数据放入向量中
		 */
		for(int i = 0 ; i < TRAIN_DATA_SIZE ; i++) {
			String[] trainDataArray = trainDataList.get(i).split(",");
			/*
			 * 遍历数据数组
			 */
			for(int j = 0 ; j < trainDataArray.length ; j++) {
				/*
				 * 输出神经元数据
				 */
				if(j >= INPUT_SIZE) {
					int[] idealOutputScalarArray = new int[IDEAL_OUTPUT_SIZE];
					idealOutputScalarArray[0] = i;//数据的行号
					if(j == INPUT_SIZE) idealOutput.putScalar(idealOutputScalarArray , Double.parseDouble(trainDataArray[j]));
					else {
						idealOutputScalarArray[j] = 1;
						idealOutput.putScalar(idealOutputScalarArray , Double.parseDouble(trainDataArray[j]));
					}
				}else {
					/*
					 * 输入神经元数据
					 */
					int[] inputScalarArray = new int[INPUT_SIZE];
					inputScalarArray[0] = i;//数据的行号
					if(j == 0) input.putScalar(inputScalarArray , Double.parseDouble(trainDataArray[j]));
					else {
						inputScalarArray[j] = 1;
						input.putScalar(inputScalarArray , Double.parseDouble(trainDataArray[j]));
					}
				}
			}
		}
		return new DataSet(input , idealOutput);
	}
	
	
	/**
	 * 获取训练源数据
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	private static List<String> getSourceData(double a , double b , double c){
		List<String> trainDataList = new ArrayList<>();
		for(int i = 1 ; i <= TRAIN_DATA_SIZE ; i++) trainDataList.add(i + "," + getY(a , b , c , i));
		return trainDataList;
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
	private static DataNormalization normalize(DataSet trainDataSet) {
		DataNormalization normalizer = new NormalizerMinMaxScaler(-1 , 1);
		normalizer.fitLabel(true);
		normalizer.fit(trainDataSet);
		normalizer.transform(trainDataSet);
		return normalizer;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
//		Nd4j.ENFORCE_NUMERICAL_STABILITY = true;
//		System.out.println(getY(A , B , C , 4));
//		for(String data : getTrainData(1.5 , 2.5 , 4 , 10)) System.out.println(data);
//		System.out.println(doTrain());
		doTrain();
	}
}
