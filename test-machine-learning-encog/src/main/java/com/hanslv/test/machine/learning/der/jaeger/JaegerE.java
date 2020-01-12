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

import com.hanslv.test.machine.learning.encog.util.DbUtil;

/**
 * 1、当前不可下跌
 * 2、200日区间内最大值最小值差别不可大于限制
 * 3、当前89均线上涨
 * 4、当前价格高于均线价格
 * 5、当前价格高于200交易日内的最高价
 * 6、30日内的最高价、最低价差异不可高于0.3
 * 7、获取当前入选股票30天内得分，并按照得分排序(得分=当日涨跌 * 换手率)
 * 
 * 人工最终筛选条件：
 * 1、当日若涨停不可放量
 * 2、周线中上方近期无压力位
 * 3、周线中单周涨幅不可过大
 * 4、当前收盘价高于近期最高值
 * 5、当前不可为上涨中继
 * @author hanslv
 */
public class JaegerE {
	/*
	 * 通用参数
	 */
	static final String START_DATE = "2019-11-08";//计算起始日期2019-12-13
	static final int HOLD_DAY_COUNT = 40;//持有天数
	static final BigDecimal SUCCESS_RATE = new BigDecimal(1);//盈利比例
	static final int TEST_COUNT = 10;//测试次数
	static final int SLEEP_SECOND_COUNT = 120;//休眠时间
	static final int STOCK_ID_LIMIT = 3550;//参加查询的股票
	
	/*
	 * 判断参数
	 */
	static final int RECENT_MAX_AND_LOW_CHECK_END_DATE_LENGTH = 30;//近期最大值、最小值距离当前日期距离
	static final int CHECK_DAY_LENGTH = 200;//判断当前价格为该交易日数量范围内的最高价
	static final BigDecimal MAX_DIFF_LIMIT = new BigDecimal(0.03);//当前最高价和近期最高价差异
	static final BigDecimal RECENT_MAX_AND_LOW_DIFF_LIMIT = new BigDecimal(0.20);//近期最大值、最小值差异限制
	static final BigDecimal NOW_MAX_AND_LOW_DIFF_LIMIT = new BigDecimal(0.40);//30日内最高价、最低价差异限制
	static final BigDecimal NOW_CURRENT_DIFF_LIMIT = new BigDecimal(0.01);//当前价格与30日内最高价差异
	
	/*
	 * 分数参数
	 */
	static final BigDecimal SCORE_MIN_LIMIT = new BigDecimal(1);//分数最小限制
	
	
	/**
	 * 判断当前股票是否符合要求
	 * @param stockId
	 * @param date
	 * @return
	 */
	private static boolean checkStock(String stockId , String date) {
		/*
		 * 当前不可下跌
		 */
		if(!DbUtil.riseCheck(stockId , date)) return false;
		
		BigDecimal[] nowMaxAndLow = DbUtil.jaegerMaxAndLow(stockId , date , RECENT_MAX_AND_LOW_CHECK_END_DATE_LENGTH);
		BigDecimal nowMax = nowMaxAndLow[0];//30日内最高价
		BigDecimal nowMin = nowMaxAndLow[1];//30日内最低价
		
		BigDecimal[] recentMaxAndLow = DbUtil.jaegerMaxAndLow(stockId , DbUtil.changeDate(stockId , date , RECENT_MAX_AND_LOW_CHECK_END_DATE_LENGTH , true) , CHECK_DAY_LENGTH);
		BigDecimal recentMax = recentMaxAndLow[0];//近期最高价
		BigDecimal recentMin = recentMaxAndLow[1];//近期最低价
		
		List<String> currentPriceInfoList = DbUtil.getPriceInfo(stockId , date , 1);
		
		/*
		 * 判空
		 */
		if(currentPriceInfoList.size() == 0 || recentMax == null || recentMin == null || nowMax == null || nowMin == null) return false;
		String[] currentPriceInfoArray = currentPriceInfoList.get(0).split(",");//当前价格信息
		BigDecimal currentEndPrice = new BigDecimal(currentPriceInfoArray[5]);//当前收盘价
		BigDecimal currentMaxPrice = new BigDecimal(currentPriceInfoArray[2]);//当前最高价
		String[] averageInfo = DbUtil.getAverage(stockId , date , 89);//当前89均线信息
		BigDecimal averagePrice = new BigDecimal(averageInfo[0]);//当前均线价格
		BigDecimal averageScope = new BigDecimal(averageInfo[1]);//当前均线斜率
		
		/*
		 * 当前价格等于30日内最高价
		 */
		if(nowMax.subtract(currentMaxPrice).abs().compareTo(currentMaxPrice.multiply(NOW_CURRENT_DIFF_LIMIT)) > 0) return false;
		
		/*
		 * 30日内最高价、最低价差异不可高于限制
		 */
		if(nowMin.multiply(NOW_MAX_AND_LOW_DIFF_LIMIT.add(BigDecimal.ONE)).compareTo(nowMax) < 0) return false;
		
		/*
		 * 近期最大值最小值差异不可高于限制
		 */
		if(recentMin.multiply(RECENT_MAX_AND_LOW_DIFF_LIMIT.add(BigDecimal.ONE)).compareTo(recentMax) < 0) return false;
		
		/*
		 * 当前价格高于均线价格
		 */
		if(currentEndPrice.compareTo(averagePrice) <= 0) return false;
		
		/*
		 * 均线不可下滑
		 */
		if(averageScope.compareTo(BigDecimal.ZERO) <= 0) return false;
		
		/*
		 * 当前价格接近近期最高价
		 */
		if(currentMaxPrice.subtract(recentMax).abs().compareTo(currentMaxPrice.multiply(MAX_DIFF_LIMIT)) > 0) return false;

		
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
	 * 计算一只股票30天内得分
	 * @param stockId
	 * @param date
	 * @return
	 */
	private static BigDecimal finalScore(String stockId , String date) {
		BigDecimal finalScore = BigDecimal.ZERO;
		List<String> stockPriceInfoList = DbUtil.jaegerBDataSource(stockId , date , RECENT_MAX_AND_LOW_CHECK_END_DATE_LENGTH);
		
		if(stockPriceInfoList.size() < RECENT_MAX_AND_LOW_CHECK_END_DATE_LENGTH) return BigDecimal.ZERO;
		
		for(String stockPriceInfo : stockPriceInfoList) finalScore = finalScore.add(score(stockPriceInfo.split(",")));
		
		return finalScore;
	}
	
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		for(int TEST_TIME = 0 ; TEST_TIME < TEST_COUNT ; TEST_TIME++) {
			Map<String , BigDecimal> scoreMap = new HashMap<>();//存放股票得分
			
			System.out.println("TEST:" + (TEST_TIME + 1));
			for(int STOCK_ID = 0 ; STOCK_ID < STOCK_ID_LIMIT ; STOCK_ID++) {
				String currentStockStartDate = DbUtil.changeDate(STOCK_ID + "" , START_DATE , 5 * TEST_TIME , true);
				if(checkStock(STOCK_ID + "" , currentStockStartDate)) scoreMap.put(STOCK_ID + "," + currentStockStartDate , finalScore(STOCK_ID + "" , currentStockStartDate));
			}
			
			/*
			 * 将得分排序
			 */
			List<Entry<String , BigDecimal>> resultList = scoreSort(scoreMap);
			
			
			for(Entry<String , BigDecimal> resultEntry : resultList) {
				String[] stockIdAndDate = resultEntry.getKey().split(",");
				String stockId = stockIdAndDate[0];
				String currentStockStartDate = stockIdAndDate[1];
				BigDecimal score = resultEntry.getValue();
				
				/*
				 * 分数不可小于分数下线
				 */
				if(score.compareTo(SCORE_MIN_LIMIT) < 0) continue;
				
				String stockInfo = DbUtil.findStockInfo(stockId);
				String currentStockCode = stockInfo.split(",")[1];

//				System.out.println("关注：stockCode = " + currentStockCode + "，score = " + score + "，date = " + currentStockStartDate);
				
				String result = DbUtil.stockCheck(stockId , currentStockStartDate , HOLD_DAY_COUNT , SUCCESS_RATE);
				if("true".equals(result)) System.out.println("预测成功：stockCode = " + currentStockCode + "，date=" + currentStockStartDate + "，score=" + score.toString());
				else if(!"false".equals(result)) System.out.println("不亏损卖出：stockCode = " + currentStockCode + "，date=" + currentStockStartDate + "：" + result + "，score=" + score.toString());
				else System.err.println("预测失败：stockCode = " + currentStockCode + "，date=" + currentStockStartDate + "，score=" + score.toString());
			}
			
			try {TimeUnit.SECONDS.sleep(SLEEP_SECOND_COUNT);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
}
