package com.hanslv.test.machine.learning.encog.test;

import com.hanslv.test.machine.learning.encog.stock.DateVolumeNNTrainer;

public class TestTrainDateVolumeNN {
	static String stockId = "1";
	static String startDate = "1999-11-10";
	static int checkDataSize = 5;
	static double limit = 0.0005;
	
	public static void main(String[] args) {
		DateVolumeNNTrainer.trainNN(stockId, startDate, checkDataSize, limit);
	}
}
