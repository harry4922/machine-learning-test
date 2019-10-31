package com.hanslv.test.machine.learning.encog.test;

import org.encog.Encog;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import com.hanslv.test.machine.learning.encog.perceptron.PerceptronModel;

/**
 * 测试感知机
 * @author hanslv
 *
 */
public class TestPerceptronModel {
	static double[][] inputData = {{ 0.0, 0.0 }, { 1.0, 0.0 },{ 0.0, 1.0 }, { 1.0, 1.0 }};
	static double[][] idealOutputData = {{ 0.0 }, { 1.0 }, { 1.0 }, { 0.0 }};
	static double errorLimit = 0.0001;
	
	static double[] testData1 = {0.0, 0.0};
	static double testData1IdealOutput = 0.0;
	static double[] testData2 = {1.0, 0.0};
	static double testData2IdealOutput = 1.0;
	
	public static void main(String[] args) {
		BasicMLData testBasicData1 = new BasicMLData(testData1);
		BasicMLData testBasicData2 = new BasicMLData(testData2);
		
		BasicNetwork preceptron = PerceptronModel.train(inputData , idealOutputData , errorLimit);
		System.out.println("--------------------------------------------" + System.lineSeparator() + "运算结果：");
		System.out.println("测试数据1：" + testData1[0] + "," + testData1[1] + "，期望输出值：" + testData1IdealOutput + "，实际输出值：" + preceptron.compute(testBasicData1).getData(0));
		System.out.println("测试数据2：" + testData2[0] + "," + testData2[1] + "，期望输出值：" + testData2IdealOutput + "，实际输出值：" + preceptron.compute(testBasicData2).getData(0));
		
		Encog.getInstance().shutdown();
	}
}
