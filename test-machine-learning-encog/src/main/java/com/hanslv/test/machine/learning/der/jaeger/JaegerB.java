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

public class JaegerB {
	static final String START_DATE = "2019-10-18";//起始日期
	static final int TEST_COUNT = 200;//计算次数
	static final int SLEEP_SECONDS = 0;//计算间隔（休眠时间）
	
	static final BigDecimal TURNOVER_RATE_LIMIT = new BigDecimal(0.5).add(BigDecimal.ONE);//换手率阈值
	static final int DATA_SIZE = 89;//分数计算数据集大小
	static final int WINER_SIZE = 1;//取得分高的前几名
	static final int WAITING_DAY_COUNT = 5;//持有天数
	static final BigDecimal SELL_RATE = new BigDecimal(0.1);//盈利比例
	static final BigDecimal RISE_NOT_RIGHT = new BigDecimal(-5);//下跌权重
	static final BigDecimal SELECTED_SCORE_LIMIT = new BigDecimal(0);//被选中分数阈值
	
	/**
	 * 计算当前股票一天得分
	 * @param dataArray [当前换手率,当前收盘价,前一日换手率,上一日收盘价]
	 * @return
	 */
	private static BigDecimal dailyScore(String[] dataArray) {
		BigDecimal currentTurnoverRate = new BigDecimal(dataArray[0]);//当前换手率
		BigDecimal lastTurnoverRate = new BigDecimal(dataArray[2]);//前一日换手率
		BigDecimal currentEndPrice = new BigDecimal(dataArray[1]);//当前收盘价
		BigDecimal lastEndPrice = new BigDecimal(dataArray[3]);//上一日收盘价
		
		/*
		 * 换手率变化是否超过阈值
		 */
		BigDecimal scoreA = BigDecimal.ZERO;
		if(lastTurnoverRate.multiply(TURNOVER_RATE_LIMIT).compareTo(currentTurnoverRate) <= 0) scoreA = BigDecimal.ONE;
		
		/*
		 * 当前是否上涨
		 */
		BigDecimal scoreB = BigDecimal.ONE;
		if(currentEndPrice.compareTo(lastEndPrice) <= 0) scoreB = RISE_NOT_RIGHT;
		
		
		/*
		 * 是否涨停
		 */
		BigDecimal scoreC = BigDecimal.ZERO;
		if(currentEndPrice.subtract(lastEndPrice).divide(lastEndPrice , 2 , BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(0.1)) >= 0) scoreC = new BigDecimal(-1); 
		
		return scoreA.add(scoreC).multiply(scoreB);
	}
	
	
	/**
	 * 计算一只股票一天的得分
	 * @param stockId
	 * @param date
	 * @return
	 */
	private static BigDecimal stockScore(String stockId , String date) {
		/*
		 * 获取当前股票的信息
		 */
		List<String> currentStockPriceDataList = DbUtil.jaegerBDataSource(stockId , date , DATA_SIZE);
		BigDecimal finalScore = BigDecimal.ZERO;
		for(String currentStockPriceData : currentStockPriceDataList) finalScore = finalScore.add(dailyScore(currentStockPriceData.split(",")));
		
		return finalScore;
	}
	
	
	/**
	 * 计算指定日期
	 * @param date
	 */
	public static void doTest(String date) {
		Map<Integer , BigDecimal> scoreMap = new HashMap<>();
		
		/*
		 * 计算全部股票
		 */
		for(int i = 1 ; i < 3550 ; i++) scoreMap.put(i , stockScore(i + "" , date));
		
		/*
		 * 按照得分倒序排序
		 */
		List<Entry<Integer , BigDecimal>> scoreList = new ArrayList<>();
		scoreList.addAll(scoreMap.entrySet());
		Collections.sort(scoreList , new Comparator<Entry<Integer , BigDecimal>>() {
			@Override
			public int compare(Entry<Integer, BigDecimal> entry1, Entry<Integer, BigDecimal> entry2) {
				return entry2.getValue().compareTo(entry1.getValue());
			}
		});
		
		
		/*
		 * 获取得分高的股票并计算是否成功
		 */
		for(int i = 0 ; i < WINER_SIZE ; i++) {
			String stockId = scoreList.get(i).getKey() + "";
			BigDecimal score = scoreList.get(i).getValue();
			
			/*
			 * 判断分数是否达标
			 */
			if(score.compareTo(SELECTED_SCORE_LIMIT) < 0) return;
			
			/*
			 * 当日股票不可下跌
			 */
			if(!DbUtil.riseCheck(stockId , date)) return;
			
			String[] average89Detail = DbUtil.getAverage(stockId, date , 89);
			BigDecimal scope = new BigDecimal(average89Detail[1]);
			BigDecimal averagePrice = new BigDecimal(average89Detail[0]);
			/*
			 * 均线斜率判断
			 */
			if(scope.compareTo(BigDecimal.ZERO) <= 0) return;
			
			/*
			 * 判断当前价格是否在均线以上
			 */
			List<String> currentEndPriceInList = DbUtil.getPriceInfo(stockId , date , 1);
			if(currentEndPriceInList.size() == 0) return;
			BigDecimal currentEndPrice = new BigDecimal(currentEndPriceInList.get(0).split(",")[5]);
			if(averagePrice.compareTo(currentEndPrice) > 0) return;
			
			
			if("true".equals(DbUtil.stockCheck(stockId , date , WAITING_DAY_COUNT , SELL_RATE))) System.out.println(date + " 当前股票预测成功：stockId=" + stockId + "，score=" + score.toString());
			else System.err.println(date + " 当前股票预测失败：stockId=" + stockId + "，score=" + score.toString());
		}
	}
	
	
	public static void main(String[] args) {
		LocalDate startDate = LocalDate.parse(START_DATE);
		for(int i = 0 ; i < TEST_COUNT ; i++) {
			String testDate = startDate.minusWeeks(i).toString();
			doTest(testDate);
			try {
				TimeUnit.SECONDS.sleep(SLEEP_SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
