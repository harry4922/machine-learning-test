package com.hanslv.test.machine.learning.encog.test;

import java.math.BigDecimal;
import java.util.List;

import com.hanslv.test.machine.learning.encog.util.DbUtil;
import com.hanslv.test.machine.learning.encog.util.StockUtil;

public class TestMacd {
	public static void main(String[] args) {
		List<String> stockEndPriceList = DbUtil.getAllStockPriceInfoEndByDate("1", "2019-11-10");
		List<BigDecimal> diffResultList = StockUtil.diffCalculation(stockEndPriceList);
		List<BigDecimal> deaResultList = StockUtil.deaCalculation(diffResultList);
		List<BigDecimal> macdResultList = StockUtil.macdCalculation(diffResultList , deaResultList);
		
		for(int i = 0 ; i < stockEndPriceList.size() ; i++) {
			System.out.print("dif：" + diffResultList.get(i) + ";");
			System.out.print("dea：" + deaResultList.get(i) + ";");
			System.out.print("macd：" + macdResultList.get(i));
			System.out.println();
		}
	}
}
