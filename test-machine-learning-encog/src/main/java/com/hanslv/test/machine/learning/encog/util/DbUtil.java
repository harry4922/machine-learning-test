package com.hanslv.test.machine.learning.encog.util;

import java.math.BigDecimal;
import java.sql.Connection;
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
	static int dayCounter;//天数计数器
	static int index;//索引计数器
	static BigDecimal maxBuffer;//最大值缓存
	static BigDecimal minBuffer;//最小值缓存
	
	
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
	
	/**
	 * 获取指定股票指定时间之前的指定数量数据
	 * @param stockId
	 * @param endDate
	 * @param dataSize
	 * @return
	 */
	public static List<String> getPriceInfo(String stockId , String endDate , int dataSize){
		List<String> resultList = new ArrayList<>();
		String sql = 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price , stock_price_turnover_rate FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price , stock_price_turnover_rate FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price , stock_price_turnover_rate FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price , stock_price_turnover_rate FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price , stock_price_turnover_rate FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_date , stock_price_volume , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price , stock_price_end_price , stock_price_turnover_rate FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"ORDER BY stock_price_date DESC LIMIT ?";
		Connection conn = JdbcUtil.getJdbcConnection();
		try(PreparedStatement pstmt = conn.prepareStatement(sql);){
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
					String stockPriceDate = resultSet.getString(1);
					String stockPriceVolume = resultSet.getString(2);
					String stockPriceHighestPrice = resultSet.getString(3);
					String stockPriceLowestPrice = resultSet.getString(4);
					String stockPriceStartPrice = resultSet.getString(5);
					String stockPriceEndPrice = resultSet.getString(6);
					String stockTurnoverRate = resultSet.getString(7);
					
					String result = stockPriceDate + "," + stockPriceVolume + "," + stockPriceHighestPrice + "," + stockPriceLowestPrice + "," + stockPriceStartPrice + "," + stockPriceEndPrice + "," + stockTurnoverRate;
					resultList.add(result);
				}
			}
		}catch(SQLException e) {e.printStackTrace();
		}finally {JdbcUtil.closeJdbcConnection();}
		
		Collections.reverse(resultList);
		return resultList;
	}
	
	
	/**
	 * 根据股票ID获取预测股票矩形面积List
	 * @param stockId
	 * @param stepLong = 训练步长+测试步长
	 * @param rectangleLong = 每个步长所包含的数据量
	 * @return
	 */
	public static List<String> getRectangleArea(String stockId , int stepLong , String endDate , int batchSize , int singleBatchSize , boolean testOrNot){
		List<String> resultList = new ArrayList<>();
		List<String> resultListBuffer = new ArrayList<>();
		
		/**
		 * 获取5天内的最大值、最小值
		 */
		for(String maxAndLowStr : getRectangleMaxAndLow(stockId , stepLong , endDate , batchSize , singleBatchSize , testOrNot)) {
			String[] maxAndLowArray = maxAndLowStr.split(",");
			BigDecimal[] maxAndMinArray = {new BigDecimal(maxAndLowArray[0]) , new BigDecimal(maxAndLowArray[1])};
			resultListBuffer.add(doGetRectangleArea(maxAndMinArray , batchSize * singleBatchSize));
		}
		
		/*
		 * 将后一天结果拼接到前一天
		 */
		String buffer = "";
		for(String areaSize : resultListBuffer) {
			if("".equals(buffer)) {
				if(testOrNot) buffer = areaSize;
				else {
					resultList.add(areaSize + "," + areaSize);//非测试信息，以当前信息补位
					buffer = areaSize;
				}
			}else {
				resultList.add(areaSize + "," + buffer);
				buffer = areaSize;
			}
		}
		
		
		Collections.reverse(resultList);
		return resultList;
	}
	
	/**
	 * 根据所给最高价与最低价差值百分比、矩形长度获取矩形面积
	 * @param data
	 * @return
	 */
	private static String doGetRectangleArea(BigDecimal[] maxAndMinArray , int rectangleLong) {
		BigDecimal rectangleWidth = maxAndMinArray[0].subtract(maxAndMinArray[1]).divide(maxAndMinArray[1] , 2 , BigDecimal.ROUND_HALF_UP);//矩形宽度=(最大值-最小值)/最小值
		return rectangleWidth.multiply(new BigDecimal(rectangleLong)).setScale(2 , BigDecimal.ROUND_HALF_UP).toString();
	}
	
	
	
	/**
	 * 获取最高价、最低价
	 * @param stockId
	 * @param stepLong
	 * @param endDate
	 * @param batchSize
	 * @param rectangleLong
	 * @return
	 */
	public static List<String> getRectangleMaxAndLow(String stockId , int stepLong , String endDate , int batchSize , int singleBatchSize , boolean testOrNot){
		List<String> resultList = new ArrayList<>();
		/*
		 * 如果是执行测试，不是正式预测
		 */
		if(testOrNot) {
			/*
			 * 将日期向以后推移一个步长
			 */
			endDate = changeDate(stockId , endDate , batchSize , false);
			
			/*
			 * 数据量增加1步长
			 */
			stepLong += 1;
		}
		
		/*
		 * 获取每个矩形中包含的价格信息
		 */
		Connection conn = JdbcUtil.getJdbcConnection();
		for(int i = 0 ; i < stepLong ; i++) {
			String sql = 
					"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
					"UNION " + 
					"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
					"UNION " + 
					"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
					"UNION " + 
					"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
					"UNION " + 
					"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
					"UNION " + 
					"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
					"ORDER BY stock_price_date DESC LIMIT ?";
			try(PreparedStatement pstmt = conn.prepareStatement(sql)){
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
				pstmt.setInt(13 , batchSize * singleBatchSize);
				try(ResultSet resultSet = pstmt.executeQuery()){
					while(resultSet.next()) {
						/*
						 * 获取当前矩形价格的最高、最低
						 */
						dayCounter++;
						BigDecimal currentMax = new BigDecimal(resultSet.getString(1));
						BigDecimal currentMin = new BigDecimal(resultSet.getString(2));
						if(maxBuffer == null || currentMax.compareTo(maxBuffer) > 0) maxBuffer = currentMax;
						if(minBuffer == null || currentMin.compareTo(minBuffer) < 0) minBuffer = currentMin;
						if(dayCounter == batchSize * singleBatchSize) {
							/*
							 * 获取结果
							 */
							resultList.add(maxBuffer + "," + minBuffer);
							/*
							 * 复位计数器、buffer
							 */
							dayCounter = 0;
							maxBuffer = null;
							minBuffer = null;
						}
					}
				}
			}catch(SQLException e) {e.printStackTrace();}
			
			/*
			 * 将日期向以前推移一个步长
			 */
			endDate = changeDate(stockId , endDate , batchSize , true);
		}
		JdbcUtil.closeJdbcConnection();
		return resultList;
	}
	
	
	/**
	 * 获取最大值、最小值并前后拼接
	 * @param stockId
	 * @param stepLong
	 * @param endDate
	 * @param batchSize
	 * @param singleBatchSize
	 * @param testOrNot
	 * @return
	 */
	public static List<String> parseMaxAndLow(String stockId , int stepLong , String endDate , int batchSize , int singleBatchSize , boolean testOrNot){
		List<String> resultList = new ArrayList<>();
		List<String> resultListBuffer = getRectangleMaxAndLow(stockId , stepLong , endDate , batchSize , singleBatchSize , testOrNot);
		
		/*
		 * 将后一天结果拼接到前一天
		 */
		for(int i = 0 ; i < resultListBuffer.size() ; i++) {
			if(!testOrNot && i == 0) resultList.add(resultListBuffer.get(i) + "," + resultListBuffer.get(i));//包含当前日信息，并以任意值补位
			if((i + 1) < resultListBuffer.size())
				resultList.add(resultListBuffer.get(i + 1) + "," + resultListBuffer.get(i));
		}
		
		Collections.reverse(resultList);
		return resultList;
	}
	
	
	/**
	 * 获取股票指定日期的N天均线值和斜率
	 * @param stockId
	 * @param endDate
	 * @return
	 */
	public static String[] getAverage(String stockId , String endDate , int averageType) {
		List<String> stockPriceInfoList = new ArrayList<>();
		String sql = 
				"SELECT stock_price_end_price , stock_price_date FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_end_price , stock_price_date FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_end_price , stock_price_date FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_end_price , stock_price_date FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_end_price , stock_price_date FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_end_price , stock_price_date FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"ORDER BY stock_price_date DESC LIMIT ?";
		/*
		 * 获取当前股票90天内的价格
		 */
		Connection conn = JdbcUtil.getJdbcConnection();
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
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
			pstmt.setInt(13 , averageType + 1);
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) stockPriceInfoList.add(resultSet.getString(1));
			}
		}catch(SQLException e) {e.printStackTrace();}finally {JdbcUtil.closeJdbcConnection();}
		
		/*
		 * 获取当天的89天均线值和前5天的89天均线值
		 */
		BigDecimal currentTotal = new BigDecimal(0);
		BigDecimal lastTotal = new BigDecimal(0);
		if(stockPriceInfoList.size() != averageType + 1) return null;
		for(int i = 0 ; i < averageType ; i++) currentTotal = currentTotal.add(new BigDecimal(stockPriceInfoList.get(i)));
		for(int i = 1 ; i < stockPriceInfoList.size() ; i++) lastTotal = lastTotal.add(new BigDecimal(stockPriceInfoList.get(i)));
		BigDecimal currentAverage = currentTotal.divide(new BigDecimal(averageType) , 2 , BigDecimal.ROUND_HALF_UP);
		BigDecimal lastAverage = lastTotal.divide(new BigDecimal(averageType) , 2 , BigDecimal.ROUND_HALF_UP);
		
		BigDecimal slope = currentAverage.subtract(lastAverage).divide(currentAverage , 4 , BigDecimal.ROUND_HALF_UP);
		
		return new String[]{currentAverage.toString() , slope.toString()};
	}
	
	
	
	/**
	 * 将日期向前或后推进limit个数据长度
	 * @param stockId
	 * @param currentDate
	 * @param limit
	 * @param forwardOrBackward true-日期向前移动，false-日期向后移动
	 * @return
	 */
	public static String changeDate(String stockId , String currentDate , int limit , boolean forwardOrBackward) {
		if(limit == 0) return currentDate;
		String resultDate = "";
		String sql = forwardOrBackward ?
				"SELECT stock_price_date FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date < ? " + 
				"UNION " + 
				"SELECT stock_price_date FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date < ? " + 
				"UNION " + 
				"SELECT stock_price_date FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date < ? " + 
				"UNION " + 
				"SELECT stock_price_date FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date < ? " + 
				"UNION " + 
				"SELECT stock_price_date FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date < ? " + 
				"UNION " + 
				"SELECT  stock_price_date FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date < ? " + 
				"ORDER BY stock_price_date DESC LIMIT ?"
				: 
				"SELECT stock_price_date FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date > ? " + 
				"UNION " + 
				"SELECT stock_price_date FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date > ? " + 
				"UNION " + 
				"SELECT stock_price_date FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date > ? " + 
				"UNION " + 
				"SELECT stock_price_date FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date > ? " + 
				"UNION " + 
				"SELECT stock_price_date FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date > ? " + 
				"UNION " + 
				"SELECT  stock_price_date FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date > ? " + 
				"ORDER BY stock_price_date ASC LIMIT ?";
		Connection conn = JdbcUtil.getJdbcConnection();
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1 , stockId);
			pstmt.setString(2 , currentDate);
			pstmt.setString(3 , stockId);
			pstmt.setString(4 , currentDate);
			pstmt.setString(5 , stockId);
			pstmt.setString(6 , currentDate);
			pstmt.setString(7 , stockId);
			pstmt.setString(8 , currentDate);
			pstmt.setString(9 , stockId);
			pstmt.setString(10 , currentDate);
			pstmt.setString(11 , stockId);
			pstmt.setString(12 , currentDate);
			pstmt.setInt(13 , limit);
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) resultDate = resultSet.getString(1);
			}
		}catch(SQLException e) {e.printStackTrace();}
		return resultDate;
	}
	
	
	/**
	 * 
	 * @param stockId
	 * @param endDate
	 * @param limit
	 * @return
	 */
	public static List<String> getInfo(String stockId , String endDate , int limit){
		List<String> resultListBuffer = new ArrayList<>();
		String sql = 
			"SELECT stock_id , stock_price_date , stock_price_start_price , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_volume , stock_price_turnover , stock_price_amplitude , stock_price_turnover_rate FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
			"SELECT stock_id , stock_price_date , stock_price_start_price , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_volume , stock_price_turnover , stock_price_amplitude , stock_price_turnover_rate FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
			"SELECT stock_id , stock_price_date , stock_price_start_price , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_volume , stock_price_turnover , stock_price_amplitude , stock_price_turnover_rate FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
			"SELECT stock_id , stock_price_date , stock_price_start_price , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_volume , stock_price_turnover , stock_price_amplitude , stock_price_turnover_rate FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
			"SELECT stock_id , stock_price_date , stock_price_start_price , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_volume , stock_price_turnover , stock_price_amplitude , stock_price_turnover_rate FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
			"SELECT stock_id , stock_price_date , stock_price_start_price , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_volume , stock_price_turnover , stock_price_amplitude , stock_price_turnover_rate FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
			"ORDER BY stock_price_date DESC LIMIT ?";
		Connection conn = JdbcUtil.getJdbcConnection();
		try(PreparedStatement pstmt = conn.prepareStatement(sql);){
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
			pstmt.setInt(13 , limit);
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) {
					String currentEndPrice = resultSet.getString(4);
					String turnover = resultSet.getString(10);
					String highestPrice = resultSet.getString(5);
					String lowestPrice = resultSet.getString(6);
					resultListBuffer.add(currentEndPrice + "," + turnover + "," + highestPrice + "," + lowestPrice);
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
		
		Collections.reverse(resultListBuffer);
		
		List<String> resultList = new ArrayList<>();
		String lastEndPrice = "";
		for(String data : resultListBuffer) {
			String[] dataArray = data.split(",");
			if("".equals(lastEndPrice)) lastEndPrice = dataArray[0];
			else {
				String result = data + "," + lastEndPrice;
				lastEndPrice = dataArray[0];
				resultList.add(result);
			}
		}
		
		return resultList;
	}
	
	
	/**
	 * 判断预测是否准确
	 * @param stockId
	 * @param date 执行预测日期
	 * @param dayCount 预测天数
	 * @param rate 盈利比例
	 * @return
	 */
	public static String stockCheck(String stockId , String date , int dayCount , BigDecimal rate) {
		List<String> buyPriceList = getPriceInfo(stockId , changeDate(stockId , date , 1 , false) , 1);
		if(buyPriceList.size() == 0) return "exclude";
		BigDecimal buyPrice = new BigDecimal(buyPriceList.get(0).split(",")[4]);
		List<String> sellPriceList = getPriceInfo(stockId , changeDate(stockId , date , dayCount , false) , dayCount);
		if(sellPriceList.size() != dayCount) return "exclude";
		BigDecimal lastEndPrice = null;
		for(String sellPriceStr : sellPriceList) {
			String[] sellPriceArray = sellPriceStr.split(",");
			BigDecimal highestPrice = new BigDecimal(sellPriceArray[2]);
			lastEndPrice = new BigDecimal(sellPriceArray[5]);
			if(highestPrice.compareTo(buyPrice.multiply(rate.add(BigDecimal.ONE))) > 0) return "true";
		}
		if(lastEndPrice.compareTo(buyPrice) > 0) return "lastEndPrice=" + lastEndPrice + ",buyPrice=" + buyPrice; 
		return "false";
	}
	
	
	
	/**
	 * 判断前几日成交量是否大于当前
	 * @param stockId
	 * @param endDate
	 * @param dayCount
	 * @return
	 */
	public static boolean turnoverCheck(String stockId , String endDate , int dayCount , BigDecimal limit) {
		String sql = 
				"SELECT stock_id , stock_price_date , stock_price_turnover FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_turnover FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_turnover FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_turnover FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_turnover FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_turnover FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"ORDER BY stock_price_date DESC LIMIT ?";
		Connection conn = JdbcUtil.getJdbcConnection();
		BigDecimal currentTurnover = null;
		BigDecimal maxTurnover = BigDecimal.ZERO;
		try(PreparedStatement pstmt = conn.prepareStatement(sql);){
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
			pstmt.setInt(13 , dayCount);
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) {
					BigDecimal turnover = new BigDecimal(resultSet.getString(3));
					if(currentTurnover == null) currentTurnover = turnover;
					else {
						if(maxTurnover.compareTo(turnover) < 0) maxTurnover = turnover;
					}
				}
			}
		}catch(SQLException e) {
				e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
		if(currentTurnover.compareTo(maxTurnover.multiply(limit.add(BigDecimal.ONE))) > 0) return false;
		return true;
	}
	
	
	/**
	 * 判断当前股票是否上涨
	 * @param stockId
	 * @param date
	 * @return
	 */
	public static boolean riseCheck(String stockId , String date) {
		String sql = 
				"SELECT stock_id , stock_price_date , stock_price_end_price FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_end_price FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_end_price FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_end_price FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_end_price FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_end_price FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"ORDER BY stock_price_date DESC LIMIT 2";
		Connection conn = JdbcUtil.getJdbcConnection();
		try(PreparedStatement pstmt = conn.prepareStatement(sql);){
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
			try(ResultSet resultSet = pstmt.executeQuery()){
				if(resultSet.next()) {
					BigDecimal currentPrice = new BigDecimal(resultSet.getString(3));
					if(resultSet.next()) {
						BigDecimal lastPrice = new BigDecimal(resultSet.getString(3));
						if(currentPrice.compareTo(lastPrice) > 0) return true;
					}
				}
			}
		}catch(SQLException e) {
				e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
		return false;
	}
	
	
	public static List<String> jaegerBDataSource(String stockId , String endDate , int dayCount){
		String sql = 
				"SELECT stock_id , stock_price_date , stock_price_turnover_rate , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_turnover_rate , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_turnover_rate , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_turnover_rate , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_turnover_rate , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? UNION " + 
				"SELECT stock_id , stock_price_date , stock_price_turnover_rate , stock_price_end_price , stock_price_highest_price , stock_price_lowest_price , stock_price_start_price FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"ORDER BY stock_price_date DESC LIMIT ?";
		Connection conn = JdbcUtil.getJdbcConnection();
		List<String> resultListBuffer = new ArrayList<>();
		try(PreparedStatement pstmt = conn.prepareStatement(sql);){
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
			pstmt.setInt(13 , dayCount + 1);
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) resultListBuffer.add(resultSet.getString(3) + "," + resultSet.getString(4) + "," + resultSet.getString(5) + "," + resultSet.getString(6) + "," + resultSet.getString(7));
			}
		}catch(SQLException e) {
				e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
		
		Collections.reverse(resultListBuffer);
		
		List<String> resultList = new ArrayList<>();
		String lastData = null;
		for(String data : resultListBuffer) {
			if(lastData == null) lastData = data;
			else {
				resultList.add(data + "," + lastData);
				lastData = data;
			}
		}
		return resultList;
	}
	
	/**
	 * 查询股票基本信息
	 * @param stockId
	 * @return
	 */
	public static String findStockInfo(String stockId) {
		String stockInfo = "";
		String sql = "SELECT * FROM tab_stock_info WHERE stock_id = ?";
		Connection conn = JdbcUtil.getJdbcConnection();
		try(PreparedStatement pstmt = conn.prepareStatement(sql);){
			pstmt.setString(1 , stockId);
			try(ResultSet resultSet = pstmt.executeQuery();){
				int count = 0;
				while(resultSet.next()) {
					if(count++ > 1) return null;
					String stockCode = resultSet.getString(2);
					String stockName = resultSet.getString(3);
					stockInfo = stockId + "," + stockCode + "," + stockName;
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
		return stockInfo;
	}
	
	
	/**
	 * 获取当前股票在日期之前指定长度区间内的最大值、最小值
	 * @param stockId
	 * @param endDate
	 * @param dayLength
	 * @return
	 */
	public static BigDecimal[] jaegerMaxAndLow(String stockId , String endDate , int dayLength) {
		Connection conn = JdbcUtil.getJdbcConnection();
		String sql = 
				"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"UNION " + 
				"SELECT stock_price_highest_price , stock_price_lowest_price , stock_price_date FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date <= ? " + 
				"ORDER BY stock_price_date DESC LIMIT ?";
		BigDecimal max = null;
		BigDecimal min = null;
		int counter = 0;
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
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
			pstmt.setInt(13 , dayLength);
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) {
					counter++;
					/*
					 * 获取当前矩形价格的最高、最低
					 */
					BigDecimal currentMax = new BigDecimal(resultSet.getString(1));
					BigDecimal currentMin = new BigDecimal(resultSet.getString(2));
					if(max == null || currentMax.compareTo(max) > 0) max = currentMax;
					if(min == null || currentMin.compareTo(min) < 0) min = currentMin;
				}
			}
		}catch(SQLException e) {e.printStackTrace();}
		
		if(counter != dayLength) return new BigDecimal[] {null , null};
		
		return new BigDecimal[]{max , min};
	}
	
	
	/**
	 * 获取当前股票的分类
	 * @param stockId
	 * @return
	 */
	public static String getStockSort(String stockId) {
		StringBuffer stockSortId = new StringBuffer();
		Connection conn = JdbcUtil.getJdbcConnection();
		String sql = "SELECT sort_id , stock_id FROM tab_stock_label WHERE stock_id = ?";
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1 , stockId);
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) {
					stockSortId.append(resultSet.getString(1) + ",");
				}
			}
		}catch(SQLException e) {e.printStackTrace();}finally {JdbcUtil.closeJdbcConnection();}
		if(stockSortId.length() == 0) return "";
		stockSortId.deleteCharAt(stockSortId.length() - 1);
		return stockSortId.toString();
	}
	
	/**
	 * 获取指定分类ID的全部股票
	 * @param sortId
	 * @return
	 */
	public static String getStockIdBySortId(String sortId) {
		StringBuffer sort = new StringBuffer();
		Connection conn = JdbcUtil.getJdbcConnection();
		String sql = "SELECT stock_id FROM tab_stock_label WHERE sort_id = ?";
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1 , sortId);
			try(ResultSet resultSet = pstmt.executeQuery()){
				while(resultSet.next()) {
					sort.append(resultSet.getString(1) + ",");
				}
			}
		}catch(SQLException e) {e.printStackTrace();}finally {JdbcUtil.closeJdbcConnection();}
		sort.deleteCharAt(sort.length() - 1);
		return sort.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//String stockId , int stepLong , String endDate , int rectangleLong , int singleBatchSize , boolean testOrNot
	public static void main(String[] args) {
//		for(String maxAndLowStr : getRectangleArea("463" , 2 , "2019-10-25" , 5 , 5 , false)) System.out.println(maxAndLowStr);
//		for(String maxAndLowStr : getRectangleMaxAndLow("463" , 2 , "2019-10-25" , 5 , 5 , false)) System.out.println(maxAndLowStr);
//		System.out.println(changeDate("1" , "2019-11-06" , 5 , false));
//		System.out.println(getAverage("1" , "2019-12-20" , 89)[0]);
//		for(String parsedMaxAndLow : parseMaxAndLow("1" , 5 , "2019-11-29" , 1 , 5 , true)) System.out.println(parsedMaxAndLow);
//		for(String result : getInfo("1" , "2019-12-31" , 10))System.out.println(result);
//		for(String result : jaegerBDataSource("1" , "2019-12-31" , 10)) System.out.println(result);
		for(String result : getRectangleArea("1" , 2 , "2019-06-21" , 5 , 5 , false)) System.out.println(result);
		//String stockId , int stepLong , String endDate , int batchSize , int singleBatchSize , boolean testOrNot
	}
}
