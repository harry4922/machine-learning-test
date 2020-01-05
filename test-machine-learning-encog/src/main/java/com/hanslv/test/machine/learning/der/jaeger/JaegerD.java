package com.hanslv.test.machine.learning.der.jaeger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

/**
 * 关注点：
 * 1、排除已经启动的股票（100天内最高价为最低价的2倍则视为启动）
 * 2、100天内每日，若当天为上涨则计算：涨幅*换手率变化
 * 
 * @author hanslv
 *
 */
public class JaegerD {
	/*
	 * 通用参数
	 */
	static final String START_DATE = "2019-12-13";//实验开始日期
	static final int TEST_COUNT = 100;//实验次数
	static final int WINNER_SIZE = 1;
	static final int HOLD_DAY_COUNT = 5;//持有天数
	static final BigDecimal SUCCESS_RATE = new BigDecimal(0.05);//盈利比例
	static final int SLEEP_SECOND_COUNT = 0;//休眠时间
	
	/*
	 * 实验参数
	 */
//	static final int CHECK_DAY_COUNT = 30;//实验天数
//	static final BigDecimal CHECK_TIMES = new BigDecimal(0.5);//符合实验要求股票为几倍
	
	
	/*
	 * 排除股票参数
	 */
	static final int EXCLUDE_CHECK_DAY_COUNT = 80;//排除计算时间区间
	static final BigDecimal EXCLUDE_CHECK_DIFF = new BigDecimal(1.55);//排除计算最大值、最小值差异范围
	static final BigDecimal EXCLUDE_SCORE_LIMIT = new BigDecimal(9);//股票入选分值下线
	
	/*
	 * 分值计算参数
	 */
	static final int SCORE_DAY_COUNT = 20;//分数计算天数
	static final int AVERAGE_SCORE_DAY_COUNT = 100;//平均分数计算天数，开始日期=当前日期-分数计算天数个交易日
	static final BigDecimal SCORE_AVERAGE_FLOAT_RATE = new BigDecimal(2.5);//分值计算，当前分数低于平均分*该值则不积分
	static final BigDecimal RISE_MAX_LIMIT = new BigDecimal(0.09);//积分上涨限制，当前上涨幅度不可超过当前比例
	
	
	
	/**
	 * 排除股票
	 * @param stockId
	 * @param date
	 * @return
	 */
	private static boolean excludeStockCheck(String stockId , String date) {
		/*
		 * 获取当前时间区间的最高价、最低价
		 */
		BigDecimal[] maxAndLow = DbUtil.jaegerMaxAndLow(stockId , date , EXCLUDE_CHECK_DAY_COUNT);
		
		/*
		 * 判空
		 */
		if(maxAndLow[1] == null || maxAndLow[0] == null) return false;
		
		/*
		 * 若最大值与最小值差异超过范围则排除
		 */
		if(maxAndLow[1].multiply(EXCLUDE_CHECK_DIFF).compareTo(maxAndLow[0]) < 0) return false;
		
		/*
		 * 当前价格高于平均值计算区间最高价
		 */
		BigDecimal currentEndPrice = new BigDecimal(DbUtil.getPriceInfo(stockId , date , 1).get(0).split(",")[5]);
		String averageDateEndDate = DbUtil.changeDate(stockId , date , SCORE_DAY_COUNT , true);
		BigDecimal[] averageDateMaxAndLow = DbUtil.jaegerMaxAndLow(stockId , averageDateEndDate , AVERAGE_SCORE_DAY_COUNT);
		if(currentEndPrice.compareTo(averageDateMaxAndLow[0]) < 0) return false;
		
		
		return true;
	}
	
	
	
	/**
	 * 计算当前股票在当前日期的分数
	 * @param stockId
	 * @param date
	 * @return
	 */
	private static BigDecimal stockFinalScore(String stockId , String date) {
		/*
		 * 计算当前股票平均分
		 */
		String averageScoreEndDate = DbUtil.changeDate(stockId , date , SCORE_DAY_COUNT , true);//将日期向前移动
		List<String> averageStockInfoList = DbUtil.jaegerBDataSource(stockId , averageScoreEndDate , AVERAGE_SCORE_DAY_COUNT);
		BigDecimal averageCheckScore = averageScore(averageStockInfoList).multiply(SCORE_AVERAGE_FLOAT_RATE);
		
		/*
		 * 计算每天的得分
		 */
		BigDecimal finalScore = BigDecimal.ZERO;
		List<String> scoreStockInfoList = DbUtil.jaegerBDataSource(stockId , date , SCORE_DAY_COUNT);
		for(String scoreStockInfo : scoreStockInfoList) {
			BigDecimal currentScore = score(scoreStockInfo.split(","));
			if(averageCheckScore.compareTo(currentScore) <= 0) finalScore = finalScore.add(BigDecimal.ONE);
		}
		
		return finalScore;
	}
	
	
	/**
	 * 计算平均分
	 * @param stockInfoList
	 * @return
	 */
	private static BigDecimal averageScore(List<String> averageStockInfoList) {
		BigDecimal average = BigDecimal.ZERO;
		int counter = 0;//记录当前分数大于0的数量
		for(String stockInfo : averageStockInfoList) {
			BigDecimal currentScore = score(stockInfo.split(","));
			if(currentScore.compareTo(BigDecimal.ZERO) > 0) {
				average = average.add(currentScore);
				counter++;
			}
		}
		return average.divide(new BigDecimal(counter) , 2 , BigDecimal.ROUND_HALF_UP);
	}
	
	
	/**
	 * 计算一天的分数
	 * @param stockInfoArray [当前收盘价,换手率,昨日收盘价]
	 * @return
	 */
	private static BigDecimal score(String[] stockInfoArray) {
		BigDecimal currentEndPrice = new BigDecimal(stockInfoArray[1]);//当前收盘价
		BigDecimal currentTurnoverRate = new BigDecimal(stockInfoArray[0]);//当前换手率
		BigDecimal lastEndPrice = new BigDecimal(stockInfoArray[3]);//昨日收盘价
		BigDecimal riseRate = currentEndPrice.subtract(lastEndPrice).divide(lastEndPrice , 3 , BigDecimal.ROUND_HALF_UP);//涨幅
		
		/*
		 * 排除涨停
		 */
//		if(riseRate.compareTo(RISE_MAX_LIMIT) > 0) return new BigDecimal(-10);
		
		/*
		 * 判断当前股票是否上涨，若不上涨则不积分
		 */
		if(riseRate.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
		
		return riseRate.multiply(currentTurnoverRate);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		LocalDate startDate = LocalDate.parse(START_DATE);
		for(int i = 0 ; i < TEST_COUNT ; i++) {
			Map<Integer , BigDecimal> scoreMap = new HashMap<>();
			String currentTestDate = startDate.minusWeeks(i).toString();
			System.out.println("正在计算：" + currentTestDate);
			for(int j = 1 ; j < 3550 ; j++) {
				if(excludeStockCheck(j + "" , currentTestDate)) {
					BigDecimal finalScore = stockFinalScore(j + "" , currentTestDate);
					if(finalScore.compareTo(EXCLUDE_SCORE_LIMIT) >= 0) scoreMap.put(j , finalScore);
				}
			}
			
			/*
			 * 将Map按照积分排序
			 */
			List<Entry<Integer , BigDecimal>> scoreEntryList = new ArrayList<>();
			scoreEntryList.addAll(scoreMap.entrySet());
			Collections.sort(scoreEntryList , new Comparator<Entry<Integer , BigDecimal>>() {
				@Override
				public int compare(Entry<Integer, BigDecimal> entry1, Entry<Integer, BigDecimal> entry2) {
					return entry2.getValue().compareTo(entry1.getValue());
				}
			});
			
			/*
			 * 找出前几名股票
			 */
			Loop1: for(int j = 0 ; j < scoreEntryList.size() ; j++) {
				if(j >= WINNER_SIZE) break Loop1;
				String currentStockId = scoreEntryList.get(j).getKey() + "";
				BigDecimal currentScore = scoreEntryList.get(j).getValue();
				String currentStockCode = DbUtil.findStockInfo(currentStockId).split(",")[1];
				
//				System.out.println("股票：stockCode=" + currentStockCode + "，score=" + currentScore + "，date=" + currentTestDate);
				
				/*
				 * 判断是否成功
				 */
				String result = DbUtil.stockCheck(currentStockId , currentTestDate , HOLD_DAY_COUNT , SUCCESS_RATE);
				if("true".equals(result)) System.out.println("预测成功：stockCode = " + currentStockCode + "，score=" + currentScore + "，date=" + currentTestDate);
				else System.err.println("预测失败：stockCode = " + currentStockCode + "，score=" + currentScore + "，date=" + currentTestDate);
			}
			
			/*
			 * 冷却
			 */
			try {
				TimeUnit.SECONDS.sleep(SLEEP_SECOND_COUNT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
