package com.hanslv.test.machine.learning.der.jaeger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

public class JaegerI {
	static final String START_DATE = "2019-12-06";//计算开始时间2019-12-06
	static final int STOCK_ID = 3550;//参与计算的股票数量
	static final int TEST_COUNT = 100;//计算次数
	static final int SLEEP_SECOND = 0;//休眠时间
	
	static final BigDecimal SUCCESS_RATE = new BigDecimal(0.4);//盈利比例
	static final int HOLD_DAY_COUNT = 60;//持有天数
	
	
	static final int DAY_RANGE = 400;//最大值、最小值取值时间范围
	static final BigDecimal CURRENT_LOW_DIFF_LIMIT = new BigDecimal(0.1);//当前股价与最小值差异范围
	static final BigDecimal CURRENT_HIGH_DIFF_LIMIT = new BigDecimal(1);//当前股价与最小值差异范围
	
	static int mainCounter;//记录全部符合要求的股票数量
	static int trueCounter;//记录全部预测成功的股票数量
	static int equalCounter;//记录不亏损的股票数量
	
	/**
	 * 判断股票是否符合要求
	 * @param stockId
	 * @param currentDate
	 * @return
	 */
	private static boolean stockCheck(String stockId , String currentDate) {
		/*
		 * 获取时间区间内最大值、最小值
		 */
		BigDecimal[] maxAndLow = getStockMaxAndLow(stockId , currentDate);
		Optional<BigDecimal> max = Optional.ofNullable(maxAndLow[0]);
		Optional<BigDecimal> low = Optional.ofNullable(maxAndLow[1]);
		
		/*
		 * 获取当前股票收盘价
		 */
		List<String> currentStockPriceList = DbUtil.getPriceInfo(stockId , currentDate , 1);
		if(currentStockPriceList.size() == 0) return false;
		BigDecimal currentEndPrice = new BigDecimal(currentStockPriceList.get(0).split(",")[5]);
		
		/*
		 * 判断1：当前价格接近于历史最低价
		 */
		if(currentEndPrice.subtract(low.orElse(BigDecimal.ZERO)).compareTo(low.orElse(new BigDecimal(-1)).multiply(CURRENT_LOW_DIFF_LIMIT)) > 0) return false;
		
		/*
		 * 判断2：当前价格与历史最高价相差较大
		 */
		if(max.orElse(BigDecimal.ZERO).subtract(currentEndPrice).compareTo(currentEndPrice.multiply(CURRENT_HIGH_DIFF_LIMIT)) < 0) return false;
		
		return true;
	}
	
	
	
	
	/**
	 * 获取当前股票的历史最大值、最小值
	 * @param stockId
	 * @param currentDate
	 * @return
	 */
	private static BigDecimal[] getStockMaxAndLow(String stockId , String currentDate) {
		BigDecimal max = null;
		BigDecimal low = null;
		
		/*
		 * 获取当前股票在指定区间内全部信息
		 */
		List<String> stockDataList = DbUtil.getPriceInfo(stockId , currentDate , DAY_RANGE);
		for(String stockData : stockDataList) {
			String[] stockDataArray = stockData.split(",");
			BigDecimal currentMax = new BigDecimal(stockDataArray[2]);
			BigDecimal currentLow = new BigDecimal(stockDataArray[3]);
			if(max == null || currentMax.compareTo(max) > 0) max = currentMax;
			if(low == null || currentLow.compareTo(low) < 0) low = currentLow;
		}
		return new BigDecimal[] {max , low};
	}
	
	
	/**
	 * 验证股票是否成功
	 * @param stockId
	 * @param currentTestDate
	 * @param holdDayCount
	 * @param successRate
	 */
	private static void successCheck(String stockId , String currentTestDate ,  int holdDayCount , BigDecimal successRate) {
		String stockInfo[] = DbUtil.findStockInfo(stockId).split(",");
		String currentStockCode = stockInfo[1];
		String stockName = stockInfo[2];
		if(stockName.contains("ST")) return;

//		System.out.println("关注：stockCode = " + currentStockCode + "，date = " + currentTestDate);
		
//		String result = DbUtil.stockCheck(stockId , currentTestDate , holdDayCount , successRate);
//		if("true".equals(result)) System.out.println("预测成功：stockCode = " + currentStockCode + "，date=" + currentTestDate);
//		else if(!"false".equals(result)) System.out.println("不亏损卖出：stockCode = " + currentStockCode + "，date=" + currentTestDate + "：" + result);
//		else System.err.println("预测失败：stockCode = " + currentStockCode + "，date=" + currentTestDate);
		
		
		String result = DbUtil.stockCheck(stockId , currentTestDate , holdDayCount , successRate);
		mainCounter++;
		if("true".equals(result)) trueCounter++;
		else if(!"false".equals(result)) equalCounter++;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws InterruptedException {
		for(int j = 0 ; j < TEST_COUNT ; j++) {
			System.out.println("---------------------------------Test" + (j + 1) + "---------------------------------");
			String currentStockTestDate = "";
			for(int i = 1 ; i < STOCK_ID ; i++) {
				String stockId = i + "";
				currentStockTestDate = DbUtil.changeDate(stockId , START_DATE ,  j * 5 , true);
				if(stockCheck(stockId , currentStockTestDate)) successCheck(stockId , currentStockTestDate , HOLD_DAY_COUNT , SUCCESS_RATE);
			}
			
//			System.out.println("当前日期成功率：" + new BigDecimal(trueCounter).divide(new BigDecimal(mainCounter) , 3 , BigDecimal.ROUND_HALF_UP) + "，date = " + currentStockTestDate);
			System.out.println("成功：" + trueCounter + "，不亏损：" + equalCounter + "，总数：" + mainCounter + "，日期：" + currentStockTestDate);
			mainCounter = 0;
			trueCounter = 0;
			equalCounter = 0;
			TimeUnit.SECONDS.sleep(SLEEP_SECOND);
		}
	}
}
