package com.hanslv.test.machine.learning.der.jaeger;

import java.math.BigDecimal;
import java.util.List;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

/**
 * 根据89日均线、vr选择股票
 * @author hanslv
 *
 */
public class JaegerL {
	static final BigDecimal VR_LIMIT = new BigDecimal(70);//VR值需要小于当前值
	static final int VR_DAY_COUNT = 26;//VR计算周期
	static final int AVERAGE_DAY_COUNT = 89;//均线计算周期
	static final String CURRENT_DATE = "2020-05-08";//当前日期
	static final int MAX_STOCK_ID = 3400;
	
	
	
	public static void main(String[] args) {
		for(int i = 1 ; i <= MAX_STOCK_ID ; i++) {
//			System.out.println("正在计算：" + i);
			String stockId = String.valueOf(i);
			/*
			 * 执行判断
			 */
			if(checkStock(stockId , CURRENT_DATE , VR_DAY_COUNT + 1 , AVERAGE_DAY_COUNT , VR_LIMIT)) {
				/*
				 * 获取股票信息
				 */
				String stockInfo = DbUtil.findStockInfo(stockId);
				String[] stockInfoArray = stockInfo.split(",");
				System.out.println("--------------------------------------------------" + stockInfoArray[1] + "," + stockInfoArray[2] + "，符合要求");
			}
		}
		
//		System.out.println(checkStock("1" , CURRENT_DATE , VR_DAY_COUNT + 1 , AVERAGE_DAY_COUNT , VR_LIMIT));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 判断当前股票是否符合要求
	 * @param stockId
	 * @param currentDate
	 * @param dayCount
	 * @return
	 */
	static boolean checkStock(String stockId , String currentDate , int vrDayCount , int averageDayCount , BigDecimal vrLimit) {
		/*
		 * 获取均线信息
		 */
		String[] averageInfo = DbUtil.getAverage(stockId , currentDate , averageDayCount);
		if(averageInfo == null) return false;
		BigDecimal averageScope = new BigDecimal(averageInfo[1]);//均线斜率
		if(averageScope.compareTo(new BigDecimal(0.0005)) < 0) return false;
		
		/*
		 * 获取vr信息
		 */
		BigDecimal vr = vr(stockId , currentDate , vrDayCount);
		if(vr.compareTo(vrLimit) >= 0) return false;
		
		return true;
	}
	
	
	
	/**
	 * 计算vr = (avs + cvs/2) / (bvs + cvs/2) * 100
	 * avs = n天内上涨成交量
	 * bvs = n天内下跌成交量
	 * cvs = n天不涨不跌成交量
	 * @param stockId
	 * @param currentDate
	 * @param dayCount
	 * @return
	 */
	static BigDecimal vr(String stockId , String currentDate , int dayCount) {
		/*
		 * 获取指定天数内股票的价格信息
		 */
		List<String> stockPriceInfoList = DbUtil.getPriceInfo(stockId , currentDate , dayCount);
		if(stockPriceInfoList.size() == 0) return new BigDecimal(10000);
		
		BigDecimal avs = BigDecimal.ZERO;
		BigDecimal bvs = BigDecimal.ZERO;
		BigDecimal cvs = BigDecimal.ZERO;
		
		/*
		 * 遍历股票信息
		 */
		BigDecimal lastEndPrice = null;
		for(String stockPriceInfo : stockPriceInfoList) {
			String[] stockPriceInfoArray = stockPriceInfo.split(",");
			
			BigDecimal currentVolume = new BigDecimal(stockPriceInfoArray[1]);//当天成交量
			BigDecimal currentEndPrice = new BigDecimal(stockPriceInfoArray[5]);//当天收盘价
			
			if(lastEndPrice == null) {
				lastEndPrice = currentEndPrice;//初始化前一日收盘价
				continue;
			}
			
			if(lastEndPrice.compareTo(currentEndPrice) < 0) avs = avs.add(currentVolume);//当天上涨
			if(lastEndPrice.compareTo(currentEndPrice) > 0) bvs = bvs.add(currentVolume);//当天下跌
			if(lastEndPrice.compareTo(currentEndPrice) == 0) cvs = cvs.add(currentVolume);//当天平盘
			
			lastEndPrice = currentEndPrice;
		}
		
		/*
		 * 计算vr
		 */
		
		BigDecimal vr = cvs.compareTo(BigDecimal.ZERO) == 0 ? 
					avs.divide(bvs , 2 , BigDecimal.ROUND_HALF_UP)
					: avs.add(cvs.divide(new BigDecimal(2) , 2 , BigDecimal.ROUND_HALF_UP)).divide(bvs.add(cvs.divide(cvs , 2 , BigDecimal.ROUND_HALF_UP)) , 2 , BigDecimal.ROUND_HALF_UP);
		
		return vr.multiply(new BigDecimal(100));
	}
}
