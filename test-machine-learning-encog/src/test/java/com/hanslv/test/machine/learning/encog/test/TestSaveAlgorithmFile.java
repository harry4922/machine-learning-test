package com.hanslv.test.machine.learning.encog.test;

import java.io.File;

import org.encog.Encog;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import com.hanslv.test.machine.learning.encog.perceptron.PerceptronModel;
import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

public class TestSaveAlgorithmFile {
	public static void main(String[] args) {
		String filePath = "D:" + File.separator + "data" + File.separator + "test" + File.separator + "test.eg";
		
		
		double[][] inputData = {{ 0.0, 0.0 }, { 1.0, 0.0 },{ 0.0, 1.0 }, { 1.0, 1.0 }};
		double[][] idealOutputData = {{ 0.0 }, { 1.0 }, { 1.0 }, { 0.0 }};
		double errorLimit = 0.0001;
		
		double[] testData1 = {0.0, 0.0};
		double testData1IdealOutput = 0.0;
		double[] testData2 = {1.0, 0.0};
		double testData2IdealOutput = 1.0;
		
		BasicMLData testBasicData1 = new BasicMLData(testData1);
		BasicMLData testBasicData2 = new BasicMLData(testData2);
			
		BasicNetwork preceptron = PerceptronModel.train(inputData , idealOutputData , errorLimit);
		
		SourceDataParser.saveAlgorithm(filePath, preceptron);
		
		BasicNetwork loadNetwork = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(filePath));
		
		System.out.println("--------------------------------------------" + System.lineSeparator() + "运算结果：");
		System.out.println("测试数据1：" + testData1[0] + "," + testData1[1] + "，期望输出值：" + testData1IdealOutput + "，实际输出值：" + loadNetwork.compute(testBasicData1).getData(0));
		System.out.println("测试数据2：" + testData2[0] + "," + testData2[1] + "，期望输出值：" + testData2IdealOutput + "，实际输出值：" + loadNetwork.compute(testBasicData2).getData(0));
			
		Encog.getInstance().shutdown();
	}
}
