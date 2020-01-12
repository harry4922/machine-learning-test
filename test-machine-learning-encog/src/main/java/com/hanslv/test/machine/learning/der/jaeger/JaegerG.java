package com.hanslv.test.machine.learning.der.jaeger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

/**
 * 1、当前价格为上涨
 * 2、当前89日均线上扬
 * 3、当前收盘价接近于近期最高价
 * 4、当前换手率高于近期换手率平均值
 * 5、当前换手率不可为最高换手率
 * 
 * 
 * 
 * @author hanslv
 *
 */
public class JaegerG {
	static final String START_DATE = "2020-01-10";//计算开始时间
	static final int TEST_COUNT = 1;//计算次数
	static final int SLEEP_SECOND = 0;//休眠时间
	static final int STOCK_ID_COUNT = 3550;//计算股票数量
	static final int HOLD_DAY_COUNT = 5;//持有天数
	static final BigDecimal SUCCESS_RATE = new BigDecimal(0.05);//盈利比例
	
	
	static final int AVERAGE_TYPE = 89;//均线类型
	static final int RECENT_DAY_LENGTH = 200;//近期天数
	static final BigDecimal HIGHEST_PRICE_DIFF_LIMIT = new BigDecimal(0.01);//当前股票收盘价与近期最高价差异限制
	static final BigDecimal TURNOVER_RATE_DIFF_LIMIT = new BigDecimal(0.5);//近期换手率差异最低限制
	static final BigDecimal RECENT_TURNOVER_RATE_DIFF_LIMIT = new BigDecimal(0.8);//当前换手率需要比近期换手率最大值小20%或更多
	
	
	/**
	 * 判断股票是否符合要求
	 * @param stockId
	 * @param endDate
	 * @return
	 */
	private static boolean stockCheck(String stockId , String endDate) {
		/*
		 * 判断1：当前股票上涨
		 */
		if(!DbUtil.riseCheck(stockId , endDate)) return false;
		
		/*
		 * 判断2：当前89均线上扬
		 */
		String[] averageInfo = DbUtil.getAverage(stockId , endDate , AVERAGE_TYPE);
		BigDecimal averageScope = Optional.ofNullable(averageInfo[1]).isPresent() ? new BigDecimal(averageInfo[1]) : BigDecimal.ZERO;
		if(averageScope.compareTo(BigDecimal.ZERO) <= 0) return false;
		
		/*
		 * 判断3：当前价格接近近期最高价
		 */
		List<String> currentStockPriceInfoList = DbUtil.getPriceInfo(stockId , endDate , 1);
		if(currentStockPriceInfoList.size() != 1) return false;
		String[] currentStockPriceInfoArray = currentStockPriceInfoList.get(0).split(",");
		BigDecimal currentStockEndPrice = new BigDecimal(currentStockPriceInfoArray[5]);
		BigDecimal[] recentMaxAndLow = DbUtil.jaegerMaxAndLow(stockId , endDate , RECENT_DAY_LENGTH);
		BigDecimal recentMax = Optional.ofNullable(recentMaxAndLow[0]).orElse(BigDecimal.ZERO);
		if(currentStockEndPrice.subtract(recentMax).abs().compareTo(recentMax.multiply(HIGHEST_PRICE_DIFF_LIMIT)) > 0) return false;
		
		/*
		 * 判断4：当前换手率接近于近期换手率平均值
		 */
		BigDecimal currentTurnoverRate = new BigDecimal(currentStockPriceInfoArray[6]);
		BigDecimal recentTurnoverRateAverage = getAverageTurnoverRate(stockId , endDate , RECENT_DAY_LENGTH);
		if(recentTurnoverRateAverage.compareTo(BigDecimal.ZERO) == 0) return false;
		if(currentTurnoverRate.subtract(recentTurnoverRateAverage).compareTo(recentTurnoverRateAverage.multiply(TURNOVER_RATE_DIFF_LIMIT)) < 0) return false;
		
		return true;
	}
	
	
	/**
	 * 获取平均换手率，同时判断当前换手率是否为最高换手率
	 * @param stockId
	 * @param endDate
	 * @param dayCount
	 * @return
	 */
	private static BigDecimal getAverageTurnoverRate(String stockId , String endDate , int dayCount) {
		BigDecimal recentTurnoverRateAverage = BigDecimal.ZERO;
		
		/*
		 * 获取近期股票信息
		 */
		List<String> recentStockPriceInfoList = DbUtil.jaegerBDataSource(stockId , endDate , dayCount);
		
		if(recentStockPriceInfoList.size() != dayCount) return BigDecimal.ZERO;
		BigDecimal currentTurnoverRate = new BigDecimal(recentStockPriceInfoList.get(recentStockPriceInfoList.size() - 1).split(",")[0]);
		BigDecimal maxRecentTurnoverRate = BigDecimal.ZERO;
		
		for(String recentStockPriceInfo : recentStockPriceInfoList) {
			String[] recentStockPriceInfoArray = recentStockPriceInfo.split(",");
			BigDecimal recentTurnoverRate = new BigDecimal(recentStockPriceInfoArray[0]);
			recentTurnoverRateAverage = recentTurnoverRateAverage.add(recentTurnoverRate);
			if(recentTurnoverRate.compareTo(maxRecentTurnoverRate) > 0) maxRecentTurnoverRate = recentTurnoverRate;
		}
		
		/*
		 * 判断当前换手率是否为近期最大
		 */
		if(currentTurnoverRate.compareTo(maxRecentTurnoverRate.multiply(RECENT_TURNOVER_RATE_DIFF_LIMIT)) >= 0) return BigDecimal.ZERO;
		
		
		return recentTurnoverRateAverage.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : recentTurnoverRateAverage.divide(new BigDecimal(dayCount) , 5 , BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 判断股票是否成功
	 * @param stockId
	 * @param currentStockStartDate
	 * @param holdDayCount
	 * @param successRate
	 */
	private static void successCheck(String stockId , String currentTestDate ,  int holdDayCount , BigDecimal successRate) {
		String stockInfo[] = DbUtil.findStockInfo(stockId).split(",");
		String currentStockCode = stockInfo[1];
		String stockName = stockInfo[2];
		if(stockName.contains("ST")) return;

		System.out.println("关注：stockCode = " + currentStockCode + "，date = " + currentTestDate);
		
//		String result = DbUtil.stockCheck(stockId , currentTestDate , holdDayCount , successRate);
//		if("true".equals(result)) System.out.println("预测成功：stockCode = " + currentStockCode + "，date=" + currentTestDate);
//		else if(!"false".equals(result)) System.out.println("不亏损卖出：stockCode = " + currentStockCode + "，date=" + currentTestDate + "：" + result);
//		else System.err.println("预测失败：stockCode = " + currentStockCode + "，date=" + currentTestDate);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		for(int i = 0 ; i < TEST_COUNT ; i++) {
			System.out.println("----------------------------------------正在计算：" + (i + 1) + "----------------------------------------");
			for(int j = 1 ; j < STOCK_ID_COUNT ; j++) {
				String stockId = j + "";
				String currentTestDate = DbUtil.changeDate(stockId , START_DATE , 5 * i , true);
				if(stockCheck(stockId , currentTestDate)) successCheck(stockId , currentTestDate , HOLD_DAY_COUNT , SUCCESS_RATE);
			}
			
			try {TimeUnit.SECONDS.sleep(SLEEP_SECOND);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
}
