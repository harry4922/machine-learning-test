package com.hanslv.test.machine.learning.encog.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 从数据库获取指定数据
 * 
 * ----------------------------------------------
 * 1、获取一只股票指定日期之后指定数量的日期-成交量信息					public static List<String> getDataAndVolumeMap(String stockId , String startDate , Integer limit)
 * 2、获取某一只股票从上市到指定日期的全部股票价格信息数据					public static List<String> getAllStockPriceInfoEndByDate(String stockId , String endDate)
 * 3、获取指定股票指定日期开始指定天数的MACD信息							public static List<String> getStockMacdInfoByLimitAndBeforeDate(String stockId , String date , Integer Limit)
 * 4、获取提供给DeepLearning4j的股票数据									public static List<String> deeplearning4jData(String stockId , String endDate , int limit)
 * ----------------------------------------------
 * 
 * @author hanslv
 *
 */
public class DbUtil {
	/**
	 * 1、获取一只股票指定日期之后指定数量的日期-成交量信息
	 * @param stockId
	 * @param startDate
	 * @param limit
	 * @return
	 */
	public static List<String> getDataAndVolumeMap(String stockId , String startDate , Integer limit){
		List<String> dataList = new ArrayList<>();
		
		String sql = 
		"SELECT stock_price_date  , stock_price_start_price , stock_price_end_price " + 
		"FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date >= ? " + 
		"UNION " + 
		"SELECT stock_price_date  , stock_price_start_price , stock_price_end_price " +  
		"FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date >= ? " + 
		"UNION " + 
		"SELECT stock_price_date  , stock_price_start_price , stock_price_end_price " +  
		"FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date >= ? " + 
		"UNION " + 
		"SELECT stock_price_date  , stock_price_start_price , stock_price_end_price " +  
		"FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date >= ? " + 
		"UNION " + 
		"SELECT stock_price_date  , stock_price_start_price , stock_price_end_price " +  
		"FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date >= ? " + 
		"UNION " + 
		"SELECT stock_price_date  , stock_price_start_price , stock_price_end_price " +  
		"FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date >= ? " + 
		"ORDER BY stock_price_date ASC LIMIT ?";
		
		try(PreparedStatement pstmt = JdbcUtil.getJdbcConnection().prepareStatement(sql);){
			pstmt.setString(1 , stockId);
			pstmt.setString(2 , startDate);
			pstmt.setString(3 , stockId);
			pstmt.setString(4 , startDate);
			pstmt.setString(5 , stockId);
			pstmt.setString(6 , startDate);
			pstmt.setString(7 , stockId);
			pstmt.setString(8 , startDate);
			pstmt.setString(9 , stockId);
			pstmt.setString(10 , startDate);
			pstmt.setString(11 , stockId);
			pstmt.setString(12 , startDate);
			pstmt.setInt(13 , limit);
			try(ResultSet resultSet = pstmt.executeQuery();){
				while(resultSet.next()) {
					String date[] = resultSet.getString(1).split("-");
					String startPrice = resultSet.getString(2);
					String endPrice = resultSet.getString(3);
					String dataStr = Integer.parseInt(date[1]) + "," + Integer.parseInt(date[2]) + "," + startPrice + "," + endPrice;
					dataList.add(dataStr);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
		return dataList;
	}
	
	/**
	 * 2、获取某一只股票从上市到指定日期的全部股票价格信息数据
	 * @param stockId
	 * @param endDate
	 * @return
	 */
	public static List<String> getAllStockPriceInfoEndByDate(String stockId , String endDate){
		List<String> stockPriceInfoList = new ArrayList<>();
		String sql = 
		"SELECT stock_price_end_price , stock_price_date " + 
		"FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
		"UNION " + 
		"SELECT stock_price_end_price , stock_price_date " + 
		"FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
		"UNION " + 
		"SELECT stock_price_end_price , stock_price_date " + 
		"FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
		"UNION " + 
		"SELECT stock_price_end_price , stock_price_date " + 
		"FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
		"UNION " + 
		"SELECT stock_price_end_price , stock_price_date " + 
		"FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
		"UNION " + 
		"SELECT stock_price_end_price , stock_price_date " + 
		"FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
		"ORDER BY stock_price_date";
		
		
		try(PreparedStatement pstmt = JdbcUtil.getJdbcConnection().prepareStatement(sql)){
			pstmt.setString(1 , stockId);
			pstmt.setString(2 , endDate);
			pstmt.setString(3 , stockId);
			pstmt.setString(4 , endDate);
			pstmt.setString(5 , stockId);
			pstmt.setString(6 , endDate);
			pstmt.setString(7 , stockId);
			pstmt.setString(8 , endDate);
			pstmt.setString(9 , stockId);
			pstmt.setString(10 , endDate);
			pstmt.setString(11 , stockId);
			pstmt.setString(12 , endDate);
			
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) {
					stockPriceInfoList.add(resultSet.getString(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return stockPriceInfoList;
	}
	
	
	/**
	 * 3、获取指定股票指定日期开始指定天数的MACD信息
	 * @param stockId
	 * @param date
	 * @param Limit
	 * @return
	 */
	public static List<String> getStockMacdInfoByLimitAndBeforeDate(String stockId , String date , Integer Limit){
		List<String> macdInfoList = new ArrayList<>();
		String sql = 
		"SELECT diff , dea , macd , date " + 
		"FROM tab_stock_index_macd_shangzheng001 WHERE stock_id = ? AND date <= ? " + 
		"UNION " + 
		"SELECT diff , dea , macd , date " + 
		"FROM tab_stock_index_macd_shangzheng002 WHERE stock_id = ? AND date <= ? " + 
		"UNION " + 
		"SELECT diff , dea , macd , date " + 
		"FROM tab_stock_index_macd_shangzheng003 WHERE stock_id = ? AND date <= ? " + 
		"UNION " + 
		"SELECT diff , dea , macd , date " + 
		"FROM tab_stock_index_macd_shenzheng001 WHERE stock_id = ? AND date <= ? " + 
		"UNION " + 
		"SELECT diff , dea , macd , date " + 
		"FROM tab_stock_index_macd_shenzheng002 WHERE stock_id = ? AND date <= ? " + 
		"UNION " + 
		"SELECT diff , dea , macd , date " + 
		"FROM tab_stock_index_macd_shenzheng003 WHERE stock_id = ? AND date <= ? " + 
		"ORDER BY date DESC LIMIT ?";
		
		try(PreparedStatement pstmt = JdbcUtil.getJdbcConnection().prepareStatement(sql);){
			pstmt.setString(1 , stockId);
			pstmt.setString(2 , date);
			pstmt.setString(3 , stockId);
			pstmt.setString(4 , date);
			pstmt.setString(5 , stockId);
			pstmt.setString(6 , date);
			pstmt.setString(7 , stockId);
			pstmt.setString(8 , date);
			pstmt.setString(9 , stockId);
			pstmt.setString(10 , date);
			pstmt.setString(11 , stockId);
			pstmt.setString(12 , date);
			pstmt.setInt(13 , Limit);
			
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) {
					String diff = resultSet.getString(1);
					String dea = resultSet.getString(2);
					String macd = resultSet.getString(3);
					String[] dateArray = resultSet.getString(4).split("-");
					
					String macdInfoStr = dateArray[1] + "," + dateArray[2] + "," + diff + "," + dea + "," + macd;
					macdInfoList.add(macdInfoStr);
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return macdInfoList;
	}
	
	/**
	 * 4、获取提供给DeepLearning4j的股票数据
	 * @param stockId
	 * @param endDate
	 * @param limit
	 * @return
	 */
	public static List<String> deeplearning4jData(String stockId , String endDate , int dataSize){
		List<String> dataList = new ArrayList<>();
		String sql = //成交量、最高价、最低价、开盘价、收盘价
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"ORDER BY stock_price_date DESC LIMIT ?";
		try(PreparedStatement pstmt = JdbcUtil.getJdbcConnection().prepareStatement(sql)){
			pstmt.setString(1, stockId);
			pstmt.setString(2, endDate);
			pstmt.setString(3, stockId);
			pstmt.setString(4, endDate);
			pstmt.setString(5, stockId);
			pstmt.setString(6, endDate);
			pstmt.setString(7, stockId);
			pstmt.setString(8, endDate);
			pstmt.setString(9, stockId);
			pstmt.setString(10, endDate);
			pstmt.setString(11, stockId);
			pstmt.setString(12, endDate);
			pstmt.setInt(13 , dataSize);
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) {
					/*
					 * 成交量、最高价、最低价、开盘价、收盘价
					 */
					String volume = resultSet.getString(2);
					String highest = resultSet.getString(3);
					String lowest = resultSet.getString(4);
					String startPrice = resultSet.getString(5);
					String endPrice = resultSet.getString(6);
					String dataStr = volume + "," + startPrice + "," + endPrice + "," + highest + "," + lowest;
					dataList.add(dataStr);
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
		
		if(dataList.size() < dataSize) return null;
		
		/*
		 * 获取将5天内的最高价和最低价
		 */
		int counter = 0;
		String highestBuffer = "";
		String lowestBuffer = "";
		
		/*
		 * 记录每5天的最高价和最低价
		 */
		List<String> highAndLowList = new ArrayList<>();
		
		for(String data : dataList) {
			String[] dataArray = data.split(",");
			/*
			 * 当前最高价
			 */
			String currentHighest = dataArray[3];
			
			/*
			 * 当前最低价
			 */
			String currentLowest = dataArray[4];
			
			/*
			 * 初始化或比对5天内最高价
			 */
			if("".equals(highestBuffer)) highestBuffer = currentHighest;
			else if(new BigDecimal(highestBuffer).compareTo(new BigDecimal(currentHighest)) < 0) highestBuffer = currentHighest;
			/*
			 * 初始化或比对5天内最低价
			 */
			if("".equals(lowestBuffer)) lowestBuffer = currentLowest;
			else if(new BigDecimal(lowestBuffer).compareTo(new BigDecimal(currentLowest)) > 0) lowestBuffer = currentLowest;
			
			
			/*
			 * 为5天则添加到resultList中
			 */
			if(++counter == 5) {
				String result = highestBuffer + "," + lowestBuffer;
				highAndLowList.add(result);
				
				/*
				 * 复原Buffers
				 */
				highestBuffer = "";
				lowestBuffer = "";
				counter = 0;
			}
		}
		
		/*
		 * 匹配5日内的信息和后5日的最高价、最低价
		 */
		List<String> finalResultList = new ArrayList<>();
		for(int i = 0 ; i < dataList.size() ; i++) {
			String[] inputDataArray = dataList.get(i).split(",");
			String inputData = inputDataArray[0] + "," + inputDataArray[1] + "," + inputDataArray[2];
			String idealOutput = highAndLowList.get(i / 5);
			String finalResult = inputData + "," + idealOutput;
			finalResultList.add(finalResult);
		}
		
		/*
		 * 将排序改为正序
		 */
		Collections.reverse(finalResultList);
		
		return finalResultList;
	}
}
