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
 * 积分卡工具类
 * |项目										|权重
 * -------------------------------------------------------------------
 * |换手率*(收盘价-昨日收盘价)/昨日收盘价		|3
 * |(收盘价-最高价)/收盘价*换手率				|1
 * |(收盘价-最低价)/收盘价*换手率				|1
 * @author hanslv
 *
 */
public class Jaeger {
	private static final BigDecimal SCORE_RIGHT_ITEMA = new BigDecimal(3);
	private static final BigDecimal SCORE_RIGHT_ITEMB = new BigDecimal(1);
	private static final BigDecimal SCORE_RIGHT_ITEMC = new BigDecimal(1);
//	private static final BigDecimal SCORE_RIGHT_ITEMD = new BigDecimal(-5);
	private static final int SOURCE_DATA_SIZE = 45;//计算数据大小
	private static final String START_DATE = "2019-12-13";//开始日期 2019-12-13 2018-09-21
	private static final BigDecimal RATE = new BigDecimal(0.1);//盈利比例
	private static final int FORCAST_DAY_COUNT = 5;//预测天数
	private static final int FORCAST_COUNT = 100;//预测次数
	private static final int BATCH_SIZE = 2;//结果集大小
//	private static final BigDecimal SCORE_LIMIT = new BigDecimal(4.5);//分值限制
	private static final BigDecimal SCORE_LIMIT = new BigDecimal(0);
	
	
	private static final BigDecimal RISE_LIMIT = new BigDecimal(0.05);//上涨阈值
	private static final BigDecimal DOWN_LIMIT = new BigDecimal(-0.08);//下跌阈值
	private static final BigDecimal TURNOVER_CHECK_LIMIT = new BigDecimal(1);//换手率增幅限制阈值
	
	
	
	/**
	 * 计算一天的得分
	 * @param dataArray 数组格式为[当日收盘价,换手率,最高价,最低价,昨日收盘价]
	 * @return
	 */
	private static BigDecimal dayScore(String[] dataArray) {
		BigDecimal lastEndPrice = new BigDecimal(dataArray[4]);
		BigDecimal currentEndPrice = new BigDecimal(dataArray[0]);
		BigDecimal turnover = new BigDecimal(dataArray[1]);
		BigDecimal highestPrice = new BigDecimal(dataArray[2]);
		BigDecimal lowestPrice = new BigDecimal(dataArray[3]);
		BigDecimal riseRate = currentEndPrice.subtract(lastEndPrice).divide(lastEndPrice , 2 , BigDecimal.ROUND_HALF_UP);
		
//		BigDecimal scoreA = turnover.multiply(currentEndPrice.subtract(lastEndPrice)).multiply(SCORE_RIGHT_ITEMA).divide(lastEndPrice , 5 , BigDecimal.ROUND_HALF_UP).setScale(5 , BigDecimal.ROUND_HALF_UP);
//		BigDecimal scoreA = riseRate.multiply(turnover).multiply(SCORE_RIGHT_ITEMA);
		/*
		 * 不可出现上影线过长
		 */
		BigDecimal scoreB = currentEndPrice.subtract(highestPrice).divide(currentEndPrice , 5 , BigDecimal.ROUND_HALF_UP).multiply(turnover).multiply(SCORE_RIGHT_ITEMB).setScale(5 , BigDecimal.ROUND_HALF_UP);
//		BigDecimal scoreC = currentEndPrice.subtract(lowestPrice).divide(currentEndPrice , 5 , BigDecimal.ROUND_HALF_UP).multiply(turnover).multiply(SCORE_RIGHT_ITEMC).setScale(5 , BigDecimal.ROUND_HALF_UP);
//		BigDecimal scoreD = BigDecimal.ONE;
//		if(riseRate.compareTo(new BigDecimal(0.09)) >= 0) {
//			scoreD = SCORE_RIGHT_ITEMD;
//		}
		
//		return (scoreA.add(scoreB).add(scoreC)).multiply(scoreD);
//		return scoreA.add(scoreB).add(scoreC);
		
		/*
		 * 不可出现下跌过大或跌停
		 */
		BigDecimal scoreC = BigDecimal.ZERO;
		if(riseRate.compareTo(DOWN_LIMIT) <= 0) scoreC = new BigDecimal(-1000);
		
		
		/*
		 * 需要出现上涨
		 */
		BigDecimal scoreA = BigDecimal.ZERO;
		if(riseRate.compareTo(RISE_LIMIT) >= 0) scoreA = BigDecimal.ONE;
		
		return scoreA.add(scoreB).add(scoreC);
	}
	
	
	/**
	 * 获取指定股票的平均分
	 * @param stockId
	 * @param endDate
	 * @param limit
	 * @return
	 */
	public static BigDecimal getAverageScore(String stockId , String endDate , int limit) {
		List<String> stockPriceInfoList = DbUtil.getInfo(stockId, endDate, limit);
		BigDecimal finalScore = new BigDecimal(0);
		for(String stockPriceInfo : stockPriceInfoList) finalScore = finalScore.add(dayScore(stockPriceInfo.split(",")));
//		return finalScore.divide(new BigDecimal(limit - 1) , 5 , BigDecimal.ROUND_HALF_UP);
//		System.out.println(finalScore);
		return finalScore;
	}
	
	
	/**
	 * 测试一天全部股票
	 * @param date
	 * @param forcastDayCount
	 */
	public static void testAllByDate(String date , int forcastDayCount , BigDecimal rate) {
		Map<String , BigDecimal> resultMap = new HashMap<>();
		for(int i = 1 ; i < 3550 ; i ++) resultMap.put(i + "" , getAverageScore(i + "" , date , 10));
		List<Entry<String , BigDecimal>> compareList = new ArrayList<>();
		compareList.addAll(resultMap.entrySet());
		Collections.sort(compareList , new Comparator<Entry<String , BigDecimal>>() {
			@Override
			public int compare(Entry<String, BigDecimal> entry1, Entry<String, BigDecimal> entry2) {
				return entry2.getValue().compareTo(entry1.getValue());
			}
		});
		
//		for(Entry<String , BigDecimal> resultEntry : compareList) System.out.println("stockID：" + resultEntry.getKey() + "，value：" + resultEntry.getValue());
		int counter = 0;
		int mainCounter = 0;
		int trueCounter = 0;
		for(Entry<String , BigDecimal> resultEntry : compareList) {
			if(++counter > BATCH_SIZE) break;
			mainCounter++;
			String stockId = resultEntry.getKey();
			BigDecimal score = resultEntry.getValue();
			BigDecimal averageScope = new BigDecimal(DbUtil.getAverage(stockId , date , 10)[1]);
			
			/*
			 * 判断均线斜率是否大于0
			 */
			if(averageScope.compareTo(BigDecimal.ZERO) <= 0) {
				mainCounter--;
//				counter--;
				continue;
			}
			
			/*
			 * 判断当前成交量是否大于前几日成交量
			 */
			if(!DbUtil.turnoverCheck(stockId , date , SOURCE_DATA_SIZE , TURNOVER_CHECK_LIMIT)) {
				mainCounter--;
//				counter--;
				continue;
			}
			
			/*
			 * 判断当天股票是否上涨
			 */
			if(!DbUtil.riseCheck(stockId, date)) {
				mainCounter--;
//				counter--;
				continue;
			}
			
			/*
			 * 分值判断
			 */
			if(SCORE_LIMIT.compareTo(score) > 0) {
				mainCounter--;
//				counter--;
				continue;
			}
			
			
			String success = DbUtil.stockCheck(stockId , date , forcastDayCount , rate);
			if("true".equals(success)) {
				System.out.println("预测成功：" + stockId + "," + date + "," + score.toString());
				trueCounter++;
			}
			else if("exclude".equals(success)) {
				counter--;
				mainCounter--;
			}else {
				System.err.println("预测失败：" + stockId + "," + date + "," + score.toString());
			}
//			System.out.println("stockID：" + stockId + "，value：" + score + "，success：" + success);
		}
		if(mainCounter == 0) return;
//		System.out.println("日期：" + date +  "，成功率：" + (new BigDecimal(trueCounter).divide(new BigDecimal(mainCounter) , 5 , BigDecimal.ROUND_HALF_UP)));
	}
	
	
	
	public static void main(String[] args) {
		LocalDate runDate = LocalDate.parse(START_DATE);
		for(int i = 0 ; i < FORCAST_COUNT ; i++) {
			String currentDate = runDate.minusWeeks(i).toString();
			System.out.println("正在计算：" + currentDate);
			testAllByDate(currentDate , FORCAST_DAY_COUNT , RATE);
			try {
				TimeUnit.SECONDS.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		testAllByDate(runDate.toString() , FORCAST_DAY_COUNT , RATE);
	}
}
