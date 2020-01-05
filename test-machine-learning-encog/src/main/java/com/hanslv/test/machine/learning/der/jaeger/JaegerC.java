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

import com.hanslv.test.machine.learning.encog.util.DbUtil;

/**
 * ***************
 * @author hanslv
 *
 */
public class JaegerC {
//	static final String START_DATE = "2020-01-03";//开始日期
	static final String START_DATE = "2019-12-13";//开始日期
	static final int TEST_COUNT = 200;//预测次数
	static final int TEST_STOCK_COUNT = 3550;//演练股票数量
	static final int WINNER_SIZE = 1;//使用前几名的股票
	static final int HOLD_DAY_COUNT = 5;//持有天数
	static final BigDecimal SUCCESS_RATE = new BigDecimal(0.05);//盈利比例
	
	static final int AVERAGE_TYPE = 21;//均线种类
	static final BigDecimal AVERAGE_CURRENT_ERROR_LIMIT = new BigDecimal(0.05);//均线、当前价格差异容忍范围
	static final BigDecimal SCORE_LIMIT = BigDecimal.ONE;//分数下线
	static final BigDecimal RISE_AS_GOAL_LIMIT = new BigDecimal(0.08);//可积分上涨幅度
	static final int TRACKING_DATA_COUNT = 60;//参与计算数据量

	/**
	 * 计算股票一天得分
	 * @param dataArray [当前换手率，当前收盘价，昨日换手率，昨日收盘价]
	 * @return
	 */
	private static BigDecimal dailyScore(String[] dataArray) {
		BigDecimal score = BigDecimal.ZERO;
		BigDecimal lastEndPrice = new BigDecimal(dataArray[3]);//昨日收盘价
		BigDecimal currentEndPrice = new BigDecimal(dataArray[1]);//当前收盘价
		BigDecimal riseRate = currentEndPrice.subtract(lastEndPrice).divide(lastEndPrice , 3 , BigDecimal.ROUND_HALF_UP);//上涨幅度
		
		/*
		 * 判断当前上涨幅度是否达到积分要求
		 */
		if(riseRate.compareTo(RISE_AS_GOAL_LIMIT) >= 0) score = BigDecimal.ONE;
		
		return score;
	}
	
	
	/**
	 * 计算一只股票从endDate到之前trackingDataCount天的分数
	 * @param stockId
	 * @param endDate
	 * @param trackingDataCount
	 * @return
	 */
	private static BigDecimal stockScore(String stockId , String endDate , int trackingDataCount) {
		String[] averageDetail = DbUtil.getAverage(stockId , endDate , AVERAGE_TYPE);
		BigDecimal averagePrice = new BigDecimal(averageDetail[0]);//当前均线价格
		BigDecimal averageScope = new BigDecimal(averageDetail[1]);//当前均线斜率
		
		/*
		 * 均线斜率大于0
		 */
		if(BigDecimal.ZERO.compareTo(averageScope) >= 0) return BigDecimal.ZERO;
		
		/*
		 * 判断当前价格与均线价格是否在容忍范围
		 */
		List<String> currentPriceList = DbUtil.getPriceInfo(stockId , endDate , 1);
		if(currentPriceList.size() == 0) return BigDecimal.ZERO;
		BigDecimal currentPrice = new BigDecimal(currentPriceList.get(0).split(",")[5]);
		if(currentPrice.subtract(averagePrice).abs().compareTo(AVERAGE_CURRENT_ERROR_LIMIT) > 0) return BigDecimal.ZERO;
		
		/*
		 * 获取区间范围内股票的总得分
		 */
		BigDecimal finalScore = BigDecimal.ZERO;
		List<String> trackingDataList = DbUtil.jaegerBDataSource(stockId , endDate , trackingDataCount);
		for(String trackingData : trackingDataList) finalScore = finalScore.add(dailyScore(trackingData.split(",")));
		
		return finalScore;
	}
	
	
	/**
	 * 按照日期执行计算演练
	 * @param date
	 */
	private static void doTestByDate(String date) {
		/*
		 * 积分Map
		 */
		Map<Integer , BigDecimal> scoreMap = new HashMap<>();
		
		/*
		 * 执行计算
		 */
		for(int i = 1 ; i <= TEST_STOCK_COUNT ; i++) {
			BigDecimal currentScore = stockScore(i + "" , date , TRACKING_DATA_COUNT);
			
			/*
			 * 判断分数是否达标
			 */
			if(SCORE_LIMIT.compareTo(currentScore) >= 0) scoreMap.put(i , currentScore);
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
		for(int i = 0 ; i < WINNER_SIZE ; i++) {
			String currentStockId = scoreEntryList.get(i).getKey() + "";
			BigDecimal currentScore = scoreEntryList.get(i).getValue();
			String currentStockCode = DbUtil.findStockInfo(currentStockId).split(",")[1];
//			System.out.println(currentStockCode + "," + currentScore);
			/*
			 * 判断是否成功
			 */
			String result = DbUtil.stockCheck(currentStockId , date , HOLD_DAY_COUNT , SUCCESS_RATE);
			if("true".equals(result)) System.out.println("预测成功：stockCode = " + currentStockCode + "，score=" + currentScore + "，date=" + date);
			else System.err.println("预测失败：stockCode = " + currentStockCode + "，score=" + currentScore + "，date=" + date);
		}
	}
	
	
	
	public static void main(String[] args) {
		LocalDate startDate = LocalDate.parse(START_DATE);
		for(int i = 0 ; i < TEST_COUNT ; i++) {
			String currentTestEndDate = startDate.minusWeeks(i).toString();
			System.out.println("正在计算");
//			String currentTestEndDate = startDate.toString();
			doTestByDate(currentTestEndDate);
		}
	}
}
