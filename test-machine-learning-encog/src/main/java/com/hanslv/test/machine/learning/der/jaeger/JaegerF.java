package com.hanslv.test.machine.learning.der.jaeger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.nd4j.linalg.primitives.Optional;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

/**
 * 1、股票没有下跌
 * 2、当前均线不可下跌
 * 3、近期每天分数都大于非近期分数平均值
 * 4、当前价格位于近期最高价附近
 * 
 * 
 * 人工筛选条件：
 * 1、不可为二次上涨
 * 2、当前成交量不可过高于前5日成交量
 * @author hanslv
 *
 */
public class JaegerF {
	static final String START_DATE = "2019-03-22";//计算开始日期
	static final int TEST_COUNT = 1;//执行次数
	static final int STOCK_ID_LIMIT = 3550;//测试的股票
	static final int SLEEP_SECOND = 0;//休眠时间
	static final int HOLD_DAY_COUNT = 10;//持有天数
	static final BigDecimal SUCCESS_RATE = new BigDecimal(0.1);//盈利比例
	
	static final int RECENT_DAY_LENGTH = 20;//近期天数限制，计算该时间内的换手率平均值
	static final int NOT_RECENT_DAY_LENGTH = 60;//非近期天数限制，计算该时间内换手率的平均值
	static final int AVERAGE_TYPE = 89;//均线类型
	static final BigDecimal RECENT_NOTRECENT_SCORE_DIFF_MIN = new BigDecimal(0.4);//近期分数与非近期分数差异最小限制
	static final BigDecimal SCORE_MIN_LIMIT = new BigDecimal(1);//分数最低限制
	static final BigDecimal CURRENT_END_DIFF_TO_RECENT_MAX = new BigDecimal(0.05);//当前价格与近期最高价差异限制
	
	
	/**
	 * 验证一只股票是否符合要求
	 * @param stockId
	 * @param date
	 * @return
	 */
	private static boolean checkStock(String stockId , String date) {
		List<String> currentStockPriceInfoList = DbUtil.getPriceInfo(stockId, date , 1);
		if(currentStockPriceInfoList.size() != 1) return false;
		
		/*
		 * 判断1：当前股票不可下跌
		 */
		if(!DbUtil.riseCheck(stockId, date)) return false;
		
		/*
		 * 判断2：均线不可下跌
		 */
		String[] averageInfo = DbUtil.getAverage(stockId , date , AVERAGE_TYPE);
		BigDecimal averageScope = new BigDecimal(averageInfo[1]);
		if(averageScope.compareTo(BigDecimal.ZERO) <= 0) return false;
		
		/*
		 * 判断3：近期每天分数都大于非近期分数平均值
		 */
//		List<BigDecimal> recentScoreList = getRecentScoreList(stockId , date);
//		if(recentScoreList == null) return false;
//		String notRecentEndDate = DbUtil.changeDate(stockId , date , RECENT_DAY_LENGTH , true);
//		BigDecimal notRecentScore = getScoreAverage(stockId , notRecentEndDate , NOT_RECENT_DAY_LENGTH);
//		for(BigDecimal recentScore : recentScoreList) {
//			if(recentScore.subtract(notRecentScore).compareTo(notRecentScore.multiply(RECENT_NOTRECENT_SCORE_DIFF_MIN)) < 0) return false;
//		}
		
		/*
		 * 判断3：近期平均分大于非近期平均分
		 */
		BigDecimal recentAverageScore = getScoreAverage(stockId , date , RECENT_DAY_LENGTH);
		String notRecentEndDate = DbUtil.changeDate(stockId , date , RECENT_DAY_LENGTH , true);
		BigDecimal notRecentAverageScore = getScoreAverage(stockId , notRecentEndDate , NOT_RECENT_DAY_LENGTH);
		if(recentAverageScore.subtract(notRecentAverageScore).compareTo(notRecentAverageScore.multiply(RECENT_NOTRECENT_SCORE_DIFF_MIN)) < 0) return false;
		
		
		/*
		 * 判断4：当前价格位于近期最高价附近
		 */
		BigDecimal[] recentMaxAndLow = DbUtil.jaegerMaxAndLow(stockId , date , RECENT_DAY_LENGTH);
		Optional<BigDecimal> recentMax = Optional.ofNullable(recentMaxAndLow[0]);
		String[] currentPriceInfoArray = currentStockPriceInfoList.get(0).split(",");
		BigDecimal currentEndPrice = new BigDecimal(currentPriceInfoArray[5]);
		if(recentMax.orElse(BigDecimal.ZERO).subtract(currentEndPrice).compareTo(currentEndPrice.multiply(CURRENT_END_DIFF_TO_RECENT_MAX)) > 0) return false;
		
		return true;
	}
	
	
	
	/**
	 * 计算当前股票一天得分
	 * @param stockPriceInfoArray [当前换手率,当前收盘价,当前最高价,昨日换手率,昨日收盘价,昨日最高价]
	 * @return
	 */
	private static BigDecimal score(String[] stockPriceInfoArray) {
		BigDecimal currentEndPrice = new BigDecimal(stockPriceInfoArray[1]);
		BigDecimal lastEndPrice = new BigDecimal(stockPriceInfoArray[4]);
		BigDecimal currentTurnoverRate = new BigDecimal(stockPriceInfoArray[0]);
		BigDecimal riseRate = currentEndPrice.subtract(lastEndPrice).divide(lastEndPrice , 3 , BigDecimal.ROUND_HALF_UP);
		return riseRate.multiply(currentTurnoverRate);
	}
	
	
	/**
	 * 计算股票平均分
	 * @param stockId
	 * @param date
	 * @return
	 */
	private static BigDecimal getScoreAverage(String stockId , String date , int dayLength) {
		BigDecimal finalScore = BigDecimal.ZERO;
		List<String> stockPriceInfoList = DbUtil.jaegerBDataSource(stockId , date , dayLength);
		
		if(stockPriceInfoList.size() < dayLength) return BigDecimal.ZERO;
		
		for(String stockPriceInfo : stockPriceInfoList) finalScore = finalScore.add(score(stockPriceInfo.split(",")));
		
		return finalScore.divide(new BigDecimal(dayLength) , 5 , BigDecimal.ROUND_HALF_UP);
	}
	
	
	/**
	 * 获取股票近期分数集合
	 * @param stockId
	 * @param date
	 * @return
	 */
//	private static List<BigDecimal> getRecentScoreList(String stockId , String date) {
//		List<BigDecimal> recentScoreList = new ArrayList<>();
//		
//		for(int i = 0 ; i < RECENT_DAY_LENGTH ; i++) {
//			String currentDate = DbUtil.changeDate(stockId , date , i , true);
//			List<String> currentStockPriceInfoList = DbUtil.jaegerBDataSource(stockId , currentDate , 1);
//			if(currentStockPriceInfoList.size() == 0) return null;
//			BigDecimal currentScore = score(currentStockPriceInfoList.get(0).split(","));
//			recentScoreList.add(currentScore);
//		}
//		return recentScoreList;
//	}
	
	
	/**
	 * 将全部得分按照降序排序
	 * @param scoreMap
	 * @return
	 */
	private static List<Entry<String , BigDecimal>> scoreSort(Map<String , BigDecimal> scoreMap){
		List<Entry<String , BigDecimal>> scoreEntryList = new ArrayList<>();
		scoreEntryList.addAll(scoreMap.entrySet());
		Collections.sort(scoreEntryList , new Comparator<Entry<String , BigDecimal>>() {
			@Override
			public int compare(Entry<String, BigDecimal> entry1, Entry<String, BigDecimal> entry2) {
				return entry2.getValue().compareTo(entry1.getValue());
			}
		});
		return scoreEntryList;
	}
	
	
	
	/**
	 * 判断股票是否成功
	 * @param stockId
	 * @param currentStockStartDate
	 * @param holdDayCount
	 * @param successRate
	 */
	private static void successCheck(Entry<String , BigDecimal> scoreEntry ,  int holdDayCount , BigDecimal successRate , BigDecimal scoreLimit) {
		String[] stockIdAndScore = scoreEntry.getKey().split(",");
		String stockId = stockIdAndScore[0];
		String currentTestDate = stockIdAndScore[1];
		BigDecimal score = scoreEntry.getValue();
		
		if(score.compareTo(scoreLimit) < 0) return;
		
		String stockInfo[] = DbUtil.findStockInfo(stockId).split(",");
		String currentStockCode = stockInfo[1];
		String stockName = stockInfo[2];
		if(stockName.contains("ST")) return;

//		System.out.println("关注：stockCode = " + currentStockCode + "，score = " + score + "，date = " + currentStockStartDate);
		
		String result = DbUtil.stockCheck(stockId , currentTestDate , holdDayCount , successRate);
		if("true".equals(result)) System.out.println("预测成功：stockCode = " + currentStockCode + "，date=" + currentTestDate + "，score=" + score.toString());
		else if(!"false".equals(result)) System.out.println("不亏损卖出：stockCode = " + currentStockCode + "，date=" + currentTestDate + "：" + result + "，score=" + score.toString());
		else System.err.println("预测失败：stockCode = " + currentStockCode + "，date=" + currentTestDate + "，score=" + score.toString());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		for(int i = 0 ; i < TEST_COUNT ; i++) {
			Map<String , BigDecimal> scoreMap = new HashMap<>();
			System.out.println("----------------------------------------正在计算：" + (i + 1) + "----------------------------------------");
			for(int j = 1 ; j < STOCK_ID_LIMIT ; j++) {
				String stockId = j + "";
				String currentTestDate = DbUtil.changeDate(stockId , START_DATE , 5 * i , true);
				if(checkStock(stockId , currentTestDate)) 
					scoreMap.put(stockId + "," + currentTestDate , getScoreAverage(stockId , currentTestDate , RECENT_DAY_LENGTH));
			}
			
			/*
			 * 将得分排序
			 */
			List<Entry<String , BigDecimal>> sortedList = scoreSort(scoreMap);
			
			/*
			 * 判断全部股票的成功率
			 */
			for(Entry<String , BigDecimal> scoreEntry : sortedList) successCheck(scoreEntry , HOLD_DAY_COUNT, SUCCESS_RATE , SCORE_MIN_LIMIT);
			
//			for(Entry<String , BigDecimal> scoreEntry : sortedList) {
//				String stockId = scoreEntry.getKey().split(",")[0];
//				String[] stockInfoArray = DbUtil.findStockInfo(stockId).split(",");
//				BigDecimal score = scoreEntry.getValue();
//				if(score.compareTo(SCORE_MIN_LIMIT) < 0) continue;
//				System.out.println("找到合适股票：stockCode = " + stockInfoArray[1] + "，score = " + scoreEntry.getValue());
//			}
			
			try {TimeUnit.SECONDS.sleep(SLEEP_SECOND);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
}
