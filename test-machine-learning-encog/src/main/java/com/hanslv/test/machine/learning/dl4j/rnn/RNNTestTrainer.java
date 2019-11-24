package com.hanslv.test.machine.learning.dl4j.rnn;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

/**
 * 训练RNNTest模型
 * @author hanslv
 *
 */
public class RNNTestTrainer {
	static int idealOutputSize = 1;
	static List<String> trainDataList = new ArrayList<>();
	static List<String> testDataList = new ArrayList<>();
	
	/**
	 * 训练数据、测试数据初始化
	 */
	static {
		String trainData1 = "0,0,0";
		String trainData2 = "1,1,1";
		String trainData3 = "32,32,32";
		String trainData4 = "23,23,23";
		String trainData5 = "14,14,14";
//		String trainData6 = "";
//		String trainData7 = "";
		
		trainDataList.add(trainData1);
		trainDataList.add(trainData2);
		trainDataList.add(trainData3);
		trainDataList.add(trainData4);
		trainDataList.add(trainData5);
//		trainDataList.add(trainData6);
//		trainDataList.add(trainData7);
		
		
		
		
		String testData1 = "10,10,10";
		String testData2 = "13,13,13";
		String testData3 = "14,14,14";
//		String testData4 = "";
//		String testData5 = "";
		
		testDataList.add(testData1);
		testDataList.add(testData2);
		testDataList.add(testData3);
//		testDataList.add(testData4);
//		testDataList.add(testData5);
	}
	
	
	/**
	 * 执行训练
	 */
	public static Evaluation train() {
		/*
		 * 获取训练数据和测试数据
		 */
		List<DataSetIterator> dataSetIteratorList = SourceDataParser.dl4jDataNormalizer(trainDataList, testDataList , idealOutputSize);
		
		/*
		 * 获取RNN模型
		 */
		MultiLayerNetwork testNetwork = RNNTestBuilder.build();
		
		/*
		 * 训练模型
		 */
		testNetwork.fit(dataSetIteratorList.get(0));
		
		/*
		 * 评价模型
		 */
		return testNetwork.evaluate(dataSetIteratorList.get(1));
	}
}
