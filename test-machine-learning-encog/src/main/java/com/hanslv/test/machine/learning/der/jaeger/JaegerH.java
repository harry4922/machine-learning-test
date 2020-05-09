package com.hanslv.test.machine.learning.der.jaeger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

/**
 * 实验：
 * 1、获取近2周涨幅超过20%的股票
 * 2、根据板块将股票分类
 * 
 * 板块选择逻辑：
 * 1、
 * 
 * 选股逻辑：
 * 1、89日均线上扬
 * 2、当前价格位于89日均线附近（5%浮动）
 * 3、近20日换手率增大
 * 
 * @author hanslv
 */
public class JaegerH {
	/*
	 * 1、获取全部股票信息
	 * 2、获取每只股票从指定日期往前10个交易日的涨跌幅
	 * 3、判断涨跌幅是否大于0.2
	 * 4、若true则获取当前股票板块
	 * 5、将信息存入Map -> 股票Code:板块信息(以,分割)
	 */
	private static final String END_DATE = "2019-12-13";//开始日期
	private static final int DAY_COUNT = 10;//时间跨度
	private static final BigDecimal RISE_RATE_LIMIT = new BigDecimal(0.2);
	
	private static final int STOCK_ID_START = 1;//参与计算股票ID开始
	private static final int STOCK_ID_COUNT = 3550;//参与计算股票ID结尾
	
	
	private static final int AVERAGE_TYPE = 89;//均线类型
	private static final BigDecimal AVERAGE_DIFF_RATE_LIMIT = new BigDecimal(0.01);
	
	
	public static void main(String[] args) {
//		doTest();
		getStock();
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 执行股票选择
	 */
	static void getStock() {
		for(int i = STOCK_ID_START ; i < STOCK_ID_COUNT ; i++) {
			String stockId = i + "";
			if(stockSelecter(stockId)) {
				String stockCode = DbUtil.findStockInfo(stockId).split(",")[1];//获取股票信息
				System.out.println(stockCode);
			}
		}
	}
	
	/**
	 * 根据条件判断当前股票是否符合要求
	 * @param stockId
	 * @return
	 */
	static boolean stockSelecter(String stockId) {
		/*
		 * 判断当前股票89均线是否上涨
		 */
		String[] averageInfo = DbUtil.getAverage(stockId , END_DATE , AVERAGE_TYPE);
		if(averageInfo == null) return false;
		if(new BigDecimal(averageInfo[1]).compareTo(BigDecimal.ZERO) <= 0) return false;
		BigDecimal averagePrice = new BigDecimal(averageInfo[0]);
		
		/*
		 * 判断当前价格是否位于89均线上下
		 */
		List<String> currentPriceList = DbUtil.getPriceInfo(stockId , END_DATE , 1);
		BigDecimal currentEndPrice = new BigDecimal(currentPriceList.get(0).split(",")[5]);
		if(currentEndPrice.subtract(averagePrice).abs().compareTo(averagePrice.multiply(AVERAGE_DIFF_RATE_LIMIT)) >= 0) return false;		
		
		/*
		 * 判断近期换手率是否增大
		 */
		
		
		return true;
	}
	
	
	
	
	
	/**
	 * 执行测试，获取上涨20%股票所属板块
	 */
	static void doTest() {
		Map<String , String> resultMap = new HashMap<>();
		
		for(int i = STOCK_ID_START ; i <= STOCK_ID_COUNT ; i++) {
			String stockId = i + "";
			if(getCurrentRiseRate(stockId).compareTo(RISE_RATE_LIMIT) >= 0) {
				/*
				 * 获取当前股票信息
				 */
				String stockCode = DbUtil.findStockInfo(stockId).split(",")[1];
				
				/*
				 * 获取当前股票板块
				 */
				String stockSort = DbUtil.getStockSort(stockId);
				
				System.out.println("stockCode = " + stockCode + "，stockSort = [" + stockSort + "]");
				
				resultMap.put(stockCode , stockSort);
			}
		}
	}
	
	
	/**
	 * 获取当前股票涨跌幅
	 * @param stockId
	 * @return
	 */
	static BigDecimal getCurrentRiseRate(String stockId) {
		List<String> stockPriceInfoList = DbUtil.getPriceInfo(stockId , END_DATE , DAY_COUNT + 1);//获取DAY_COUNT + 1天股票信息，多出的一天为获取第-21天收盘价
		if(stockPriceInfoList.size() == 0) return BigDecimal.ZERO;
		BigDecimal startPrice = new BigDecimal(stockPriceInfoList.get(0).split(",")[5]);//21天前收盘价格
		BigDecimal endPrice = new BigDecimal(stockPriceInfoList.get(DAY_COUNT).split(",")[5]);//当前日期收盘价格
		return endPrice.subtract(startPrice).divide(startPrice  , 2 , BigDecimal.ROUND_HALF_UP);
	}
}
