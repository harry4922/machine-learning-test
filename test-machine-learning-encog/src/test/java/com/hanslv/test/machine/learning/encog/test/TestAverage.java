package com.hanslv.test.machine.learning.encog.test;

import java.math.BigDecimal;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

public class TestAverage {
	static String date = "2019-12-13";
	static int mainCounter;
	static int goalCounter;
	
	public static void main(String[] args) {
		for(int i = 1 ; i <= 3550 ; i ++) {
			/*
			 * 获取每只89天均线斜率大于0的股票
			 */
			BigDecimal averageSlope = new BigDecimal(DbUtil.get89Average(i + "" , date)[1]);
			if(averageSlope.compareTo(BigDecimal.ZERO) > 0) {
				mainCounter++;
				/*
				 * 获取当前价格
				 */
				BigDecimal currentPrice = new BigDecimal(DbUtil.getPriceInfo(i + "" , date , 1).get(0).split(",")[5]);
				
				/*
				 * 获取5天后价格
				 */
				String newDate = DbUtil.changeDate(i + "" , date , 5 , false);
				BigDecimal lastPrice = new BigDecimal(DbUtil.getPriceInfo(i + "" , newDate , 1).get(0).split(",")[5]);
				if(currentPrice.compareTo(lastPrice) <= 0) {
					goalCounter++;
					System.out.println("stockId=" + i + " date=" + date + " currentPrice=" + currentPrice + " lastPrice=" + lastPrice);
				}
			}
		}
		if(mainCounter != 0 && goalCounter != 0)
			System.out.println(new BigDecimal(goalCounter).divide(new BigDecimal(mainCounter) , 2 , BigDecimal.ROUND_HALF_UP));
		else
			System.out.println("没有结果集");
	}
}
