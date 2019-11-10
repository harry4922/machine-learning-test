package com.hanslv.test.machine.learning.encog.test;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

public class TestGetStockEndPrice {
	static String stockId = "1";
	static String endDate = "2019-11-10";
	
	public static void main(String[] args) {
		for(String stockEndPrice : DbUtil.getAllStockPriceInfoEndByDate(stockId , endDate)) {
			System.out.println(stockEndPrice);
		}
	}
}
