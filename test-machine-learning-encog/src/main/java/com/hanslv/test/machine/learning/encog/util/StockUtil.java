package com.hanslv.test.machine.learning.encog.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 股票工具类
 * --------------------------------------------------------
 * 1、计算给定股票集合的DIFF								public static List<BigDecimal> diffCalculation(List<String> stockEndPriceList)
 * 2、计算给定股票集合的DEA								public static List<BigDecimal> deaCalculation(List<BigDecimal> stockDifList)
 * 3、计算给定股票集合的MACD								public static List<BigDecimal> macdCalculation(List<BigDecimal> stockDifList , List<BigDecimal> stockDeaList)
 * --------------------------------------------------------
 * @author hanslv
 *
 */
public class StockUtil {
	
	/**
	 * 1、计算给定股票集合的DIFF
	 * @param stockEndPriceList
	 * @return
	 */
	public static List<BigDecimal> diffCalculation(List<String> stockEndPriceList){
		List<BigDecimal> ema12ResultList = ema12Calculation(stockEndPriceList);
		List<BigDecimal> ema26ResultList = ema26Calculation(stockEndPriceList);
		List<BigDecimal> difResultList = new ArrayList<>();
		
		
		for(int i = 0 ; i < stockEndPriceList.size() ; i++) {
			BigDecimal currentEma12 = ema12ResultList.get(i);
			BigDecimal currentEma26 = ema26ResultList.get(i);
			BigDecimal currentDif = currentEma12.subtract(currentEma26);
			difResultList.add(currentDif);
		}
		return difResultList;
	}
	
	
	/**
	 * 2、计算给定股票集合的DEA
	 * @param stockDifList
	 * @return
	 */
	public static List<BigDecimal> deaCalculation(List<BigDecimal> stockDiffList){
		List<BigDecimal> deaResultList = new ArrayList<>();
		
		for(int i = 0 ; i < stockDiffList.size() ; i++) {
			if(i == 0) deaResultList.add(new BigDecimal(0));
			else {
				deaResultList.add(new BigDecimal(0.2).multiply(stockDiffList.get(i)).setScale(2 , BigDecimal.ROUND_HALF_EVEN).add(
						new BigDecimal(0.8).multiply(deaResultList.get(i - 1)).setScale(2 , BigDecimal.ROUND_HALF_EVEN)));
			}
		}
		
		return deaResultList;
	}
	
	
	/**
	 * 3、计算给定股票集合的MACD
	 * @param stockDifList
	 * @param stockDeaList
	 * @return
	 */
	public static List<BigDecimal> macdCalculation(List<BigDecimal> stockDiffList , List<BigDecimal> stockDeaList){
		List<BigDecimal> macdResultList = new ArrayList<>();
		
		for(int i = 0 ; i < stockDiffList.size() ; i++) {
			if(i == 0)macdResultList.add(new BigDecimal(0));
			else {
				BigDecimal currentDif = stockDiffList.get(i);
				BigDecimal currentDea = stockDeaList.get(i);
				
				macdResultList.add(currentDif.subtract(currentDea).multiply(new BigDecimal(2)));
			}
		}
		return macdResultList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 计算给定股票集合的EMA12
	 * @param stockEndPriceList
	 * @return
	 */
	private static List<BigDecimal> ema12Calculation(List<String> stockEndPriceList){
		List<BigDecimal> ema12ResultList = new ArrayList<>();
		
		for(int i  = 0 ; i < stockEndPriceList.size() ; i++) {
			if(i == 0) ema12ResultList.add(new BigDecimal(0));
			else {
				/*
				 * 上一个交易日的ema12数值
				 */
				BigDecimal lastEma12Result = ema12ResultList.get(i - 1);
				
				BigDecimal currentEma12Result = lastEma12Result.multiply(new BigDecimal(11)).divide(new BigDecimal(13), BigDecimal.ROUND_HALF_EVEN).add(
						new BigDecimal(stockEndPriceList.get(i)).multiply(new BigDecimal(2)).divide(new BigDecimal(13), BigDecimal.ROUND_HALF_EVEN)).setScale(2);
				
				ema12ResultList.add(currentEma12Result);
			}
		}
		return ema12ResultList;
	}
	
	/**
	 * 计算给定股票集合的EMA26
	 * @param stockEndPriceList
	 * @return
	 */
	private static List<BigDecimal> ema26Calculation(List<String> stockEndPriceList){
		List<BigDecimal> ema26ResultList = new ArrayList<>();
		
		for(int i  = 0 ; i < stockEndPriceList.size() ; i++) {
			if(i == 0) ema26ResultList.add(new BigDecimal(0));
			else {
				/*
				 * 上一个交易日的ema12数值
				 */
				BigDecimal lastEma12Result = ema26ResultList.get(i - 1);
				
				BigDecimal currentEma12Result = lastEma12Result.multiply(new BigDecimal(25)).divide(new BigDecimal(27), BigDecimal.ROUND_HALF_EVEN).add(
						new BigDecimal(stockEndPriceList.get(i)).multiply(new BigDecimal(2)).divide(new BigDecimal(27), BigDecimal.ROUND_HALF_EVEN)).setScale(2);
				
				ema26ResultList.add(currentEma12Result);
			}
		}
		return ema26ResultList;
	}
}
