package com.hanslv.test.machine.learning.encog.test;

import com.hanslv.test.machine.learning.encog.stock.DateVolumeNNTrainer;

public class TestTrainDateVolumeNN {
	static String stockId = "1";
//	static String startDate = "2019-09-10";
	static String startDate = "2019-06-27";
	static int checkDataSize = 5;
//	static double limit = 0.003;
//	static double limit = 0.0057;
	static double limit = 0.000005;
//	static double limit = 0.0004;
//	static double limit = 0.00005;
	static double checkLimit = 0.005;
//	static double limit = 0.0034;
	
	public static void main(String[] args) {
		DateVolumeNNTrainer.trainNN(stockId, startDate, checkDataSize, limit , checkLimit);
	}
}
