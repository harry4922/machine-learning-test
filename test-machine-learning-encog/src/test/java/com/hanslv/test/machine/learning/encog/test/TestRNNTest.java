package com.hanslv.test.machine.learning.encog.test;

import org.deeplearning4j.eval.Evaluation;

import com.hanslv.test.machine.learning.dl4j.rnn.RNNTestTrainer;


/**
 * 测试RNNTest模型
 * @author hanslv
 *
 */
public class TestRNNTest {
	public static void main(String[] args) {
		Evaluation evaluation =  RNNTestTrainer.train();
		System.out.println(evaluation.accuracy());
		System.out.println("------------------------------------");
		System.out.println(evaluation.f1());
		System.out.println("-------------------------------------");
		System.out.println(evaluation.stats());
	}
}
