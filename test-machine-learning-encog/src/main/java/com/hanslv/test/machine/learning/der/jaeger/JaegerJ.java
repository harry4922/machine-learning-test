package com.hanslv.test.machine.learning.der.jaeger;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

/**
 * 猎人J计划
 * 
 * 获取当前价格位于89日均线附近的股票
 * 获取89日均线上扬的股票
 * 
 * @author hanslv
 *
 */
public class JaegerJ {
	static final int MAX_STOCK_ID = 3000;
	static final String START_DATE = "2020-3-20";
	static final int AVERAGE_TYPE = 89;
	
	public static void main(String[] args) {
		LocalDate localDate = LocalDate.parse(START_DATE);
		localDate.minusDays(1);
		for(int i = 0 ; i < 100 ; i++){
			String runDate = localDate.minusDays(i).toString();
			System.out.println("正在计算：" + runDate);
			doTest(runDate);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 执行测试
	 */
	private static void doTest(String date) {
		/*
		 * 根据股票ID获取当前股票信息
		 */
		for(int i = 1 ; i < MAX_STOCK_ID ; i++) {
			String stockId = String.valueOf(i);
			/*
			 * 执行判断
			 */
			if(stockSelector(stockId , date , AVERAGE_TYPE)) {
				/*
				 * 获取股票信息
				 */
				String stockInfo = DbUtil.findStockInfo(stockId);
				String[] stockInfoArray = stockInfo.split(",");
				System.out.println("--------------------------------------------------" + stockInfoArray[1] + "," + stockInfoArray[2] + "，符合要求");
			}
		}
	}
	

	/**
	 * 判断股票是否符合要求
	 * @param stockId 股票ID
	 * @param checkDate 执行日期
	 * @param averageType 均线种类
	 * @return
	 */
	private static boolean stockSelector(String stockId , String checkDate , int averageType) {
		/*
		 * 获取当前股票89天均线信息
		 */
		String[] averageInfo = DbUtil.getAverage(stockId , checkDate , averageType);
		if(averageInfo == null) return false;
		BigDecimal averageScope = new BigDecimal(averageInfo[1]);//均线斜率
		BigDecimal averagePrice = new BigDecimal(averageInfo[0]);//均线价格
		
		/*
		 * 获取股票当前价格
		 */
		List<String> stockInfoList = DbUtil.getPriceInfo(stockId , checkDate , 1);
		if(stockInfoList.size() == 0) return false;
		String[] stockInfoArray = stockInfoList.get(0).split(",");
		BigDecimal currentMaxPrice = new BigDecimal(stockInfoArray[2]);//股票最高价
		BigDecimal currentMinPrice = new BigDecimal(stockInfoArray[3]);//股票最低价
		
		/*
		 * 判断当日89均线价格是否位于最高价、最低价之间
		 */
		if(averagePrice.compareTo(currentMinPrice) < 0 || averagePrice.compareTo(currentMaxPrice) > 0) return false;
		
		/*
		 * 判断当前均线是否上扬
		 */
		if(averageScope.compareTo(new BigDecimal(0.0025)) <= 0) return false;
		
		return true;
	}
}
