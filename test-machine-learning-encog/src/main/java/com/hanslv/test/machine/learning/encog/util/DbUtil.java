package com.hanslv.test.machine.learning.encog.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanslv.test.stock.analysis.dto.StockPriceDto;
import com.hanslv.test.stock.analysis.dto.StockRiseAndFallCountMonthDto;

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
	
	
	
	
	
	private static final String ID = "stock_id";
	private static final String CODE = "stock_code";
	private static final String NAME = "stock_name";
	private static final String PRICE_DATE = "stock_price_date";
	private static final String START_PRICE = "stock_price_start_price";
	private static final String END_PRICE = "stock_price_end_price";
	private static final String HIGHEST_PRICE = "stock_price_highest_price";
	private static final String LOWEST_PRICE = "stock_price_lowest_price";
	private static final String LAST_END_PRICE = "last_end_price";
	private static final String RISE_RATE = "rise_rate";
	
	private static final String DO_SELECT_STOCK_PRICE_INFO_SQL = 
					" SELECT "
					+ " stockInfo.stock_id," 
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price "
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shangzheng_0001 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND priceInfo.stock_price_date <= ?"
					+ " UNION"
					+ " SELECT "
					+ " stockInfo.stock_id, "
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price "
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shangzheng_0002 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND priceInfo.stock_price_date <= ?"
					+ " UNION"
					+ " SELECT "
					+ " stockInfo.stock_id, "
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price"
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shangzheng_0003 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND priceInfo.stock_price_date <= ?"
					+ " UNION"
					+ " SELECT"
					+ " stockInfo.stock_id, "
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price"
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shenzheng_0001 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND priceInfo.stock_price_date <= ?"
					+ " UNION"
					+ " SELECT"
					+ " stockInfo.stock_id, "
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price"
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shenzheng_0002 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND priceInfo.stock_price_date <= ?"
					+ " UNION"
					+ " SELECT"
					+ " stockInfo.stock_id, "
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price"
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shenzheng_0003 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND priceInfo.stock_price_date <= ?"
					+ " ORDER BY stock_price_date DESC LIMIT ?";
	private static final String DO_SELECT_STOCK_PRICE_INFO_BY_YEAR_SQL = 
					" SELECT "
					+ " stockInfo.stock_id," 
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price "
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shangzheng_0001 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND date_format(priceInfo.stock_price_date, '%Y') = ?"
					+ " UNION"
					+ " SELECT "
					+ " stockInfo.stock_id, "
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price "
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shangzheng_0002 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND date_format(priceInfo.stock_price_date, '%Y') = ?"
					+ " UNION"
					+ " SELECT "
					+ " stockInfo.stock_id, "
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price"
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shangzheng_0003 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND date_format(priceInfo.stock_price_date, '%Y') = ?"
					+ " UNION"
					+ " SELECT"
					+ " stockInfo.stock_id, "
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price"
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shenzheng_0001 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND date_format(priceInfo.stock_price_date, '%Y') = ?"
					+ " UNION"
					+ " SELECT"
					+ " stockInfo.stock_id, "
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price"
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shenzheng_0002 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND date_format(priceInfo.stock_price_date, '%Y') = ?"
					+ " UNION"
					+ " SELECT"
					+ " stockInfo.stock_id, "
					+ " stockInfo.stock_code, "
					+ " stockInfo.stock_name, "
					+ " priceInfo.stock_price_date , "
					+ " priceInfo.stock_price_start_price , "
					+ " priceInfo.stock_price_end_price , "
					+ " priceInfo.stock_price_highest_price , "
					+ " priceInfo.stock_price_lowest_price"
					+ " FROM tab_stock_info stockInfo LEFT JOIN tab_stock_price_shenzheng_0003 priceInfo ON stockInfo.stock_id = priceInfo.stock_id "
					+ " WHERE priceInfo.stock_id = ? AND date_format(priceInfo.stock_price_date, '%Y') = ?"
					+ " ORDER BY stock_price_date DESC ";
			
			
	
	
	
	//String stockId , int stepLong , String endDate , int rectangleLong , int singleBatchSize , boolean testOrNot
	public static void main(String[] args) {
//		for(String maxAndLowStr : getRectangleArea("463" , 2 , "2019-10-25" , 5 , 5 , false)) System.out.println(maxAndLowStr);
//		for(String maxAndLowStr : getRectangleMaxAndLow("463" , 2 , "2019-10-25" , 5 , 5 , false)) System.out.println(maxAndLowStr);
//		System.out.println(changeDate("1" , "2019-11-06" , 5 , false));
//		System.out.println(getAverage("1" , "2019-12-20" , 89)[0]);
//		for(String parsedMaxAndLow : parseMaxAndLow("1" , 5 , "2019-11-29" , 1 , 5 , true)) System.out.println(parsedMaxAndLow);
//		for(String result : getInfo("1" , "2019-12-31" , 10))System.out.println(result);
//		for(String result : jaegerBDataSource("1" , "2019-12-31" , 10)) System.out.println(result);
//		for(String result : getRectangleArea("1" , 2 , "2019-06-21" , 5 , 5 , false)) System.out.println(result);
		//String stockId , int stepLong , String endDate , int batchSize , int singleBatchSize , boolean testOrNot
//		List<StockPriceDto> resultList = selectStockPriceInfo("1", "2020-07-06", 1000);
//		List<StockPriceDto> resultList = selectStockPriceInfo("1" , "2019");
//		resultList.forEach(System.out::println);
//		System.out.println("2020-01-02".substring(5 , 7));
//		System.out.println(new BigDecimal(-1).divide(new BigDecimal(2)).stripTrailingZeros().toPlainString());
		List<String> yearList = new ArrayList<>();
		yearList.add("2019");
		yearList.add("2018");
		yearList.add("2017");
		yearList.add("2016");
		Map<String , List<StockRiseAndFallCountMonthDto>> montCountMap = getStockRiseAndFallCountMonth("1", yearList);
		montCountMap.forEach((key , value) -> {
			System.out.println(key + "年统计-------------------");
			System.out.println("月,上涨,下跌");
			value.forEach(result -> {
//				System.out.println(result.getMonth() + "月，上涨：" + result.getRiseCount() + "，下跌：" + result.getFallCount());
//				System.out.println(result.getMonth() + "," + result.getRiseCount() + "," + result.getFallCount());
				System.out.print(result.getFallCount() + ",");
			});
			System.out.println();
		});
	}
	
	/**
	 * 获取股票价格信息
	 * @param stockId
	 * @param endDate
	 * @param length
	 * @return
	 */
	public static List<StockPriceDto> selectStockPriceInfo(String stockId , String endDate , Integer length){
		List<Map<String , String>> rawResultList = doSelectStockPriceInfo(stockId, endDate, length);//获取股票信息
		return selectStockPriceSub(rawResultList);
	}
	
	/**
	 * 按照年份获取股票价格信息
	 * @param stockId
	 * @param year
	 * @return
	 */
	public static List<StockPriceDto> selectStockPriceInfo(String stockId , String year){
		List<Map<String , String>> rawResultList = doSelectStockPriceInfoYear(stockId, year);
		return selectStockPriceSub(rawResultList);
	}
	
	
	/**
	 * 获取指定年份中每个月上涨、下跌股票数量
	 * @param yearList
	 * @return
	 */
	public static Map<String , List<StockRiseAndFallCountMonthDto>> getStockRiseAndFallCountMonth(String stockId , List<String> yearList){
		Map<String , List<StockRiseAndFallCountMonthDto>> stockRiseCountResultMap = new HashMap<>();
		//根据年份获取每个月股票上涨、下跌数量
		yearList.forEach(year -> {
			List<StockRiseAndFallCountMonthDto> yearCountList = doGetStockRiseAndFallCountMonth(stockId, year);
			stockRiseCountResultMap.put(year, yearCountList);
		});
		return stockRiseCountResultMap;
	}
	
	/**
	 * 获取全部股票指定年份每月上涨、下跌统计
	 * @param yearList
	 * @return
	 */
	public static Map<String , List<StockRiseAndFallCountMonthDto>> getStockRiseAndFallCountMonth(List<String> yearList){
		Map<String , List<StockRiseAndFallCountMonthDto>> stockRiseCountResultMap = new HashMap<>();
		return stockRiseCountResultMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 获取股票价格信息
	 * @param stockId
	 * @param endDate
	 * @param length
	 * @return
	 */
	private static List<Map<String , String>> doSelectStockPriceInfo(String stockId , String endDate , Integer length){
		List<Map<String , String>> stockPriceInfoList = new ArrayList<>();
		try(PreparedStatement pstmt = JdbcUtil.getJdbcConnection().prepareStatement(DO_SELECT_STOCK_PRICE_INFO_SQL);){
			initSqlParam(pstmt, stockId, endDate, length);//初始化查询参数
			try(ResultSet searchResult = pstmt.executeQuery();){
				stockPriceInfoList = getStockPrice(searchResult);//获取结果List
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
		return stockPriceInfoList;
	}
	/**
	 * 初始化SQL查询参数
	 * @param pstmt
	 * @param stockId
	 * @param endDate
	 * @param length
	 * @throws SQLException 
	 */
	private static void initSqlParam(PreparedStatement pstmt , String stockId , String endDate , Integer length) throws SQLException {
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
		pstmt.setInt(13, length);
	}
	
	/**
	 * 按照年份获取股票价格信息
	 * @param stockId
	 * @param year
	 * @return
	 */
	private static List<Map<String , String>> doSelectStockPriceInfoYear(String stockId , String year){
		List<Map<String , String>> searchResultList = new ArrayList<>();
		try(PreparedStatement pstmt = JdbcUtil.getJdbcConnection().prepareStatement(DO_SELECT_STOCK_PRICE_INFO_BY_YEAR_SQL);){
			initSqlParam4Year(pstmt, stockId, year);//初始化查询参数
			try(ResultSet searchResult = pstmt.executeQuery();){
				searchResultList = getStockPrice(searchResult);//获取结果List
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
		return searchResultList;
	}
	
	/**
	 * 初始化根据年份获取股票价格信息的Preparestatement
	 * @param stockId
	 * @param year
	 * @throws SQLException 
	 */
	private static void initSqlParam4Year(PreparedStatement pstmt, String stockId , String year) throws SQLException {
		pstmt.setString(1, stockId);
		pstmt.setString(2, year);
		pstmt.setString(3, stockId);
		pstmt.setString(4, year);
		pstmt.setString(5, stockId);
		pstmt.setString(6, year);
		pstmt.setString(7, stockId);
		pstmt.setString(8, year);
		pstmt.setString(9, stockId);
		pstmt.setString(10, year);
		pstmt.setString(11, stockId);
		pstmt.setString(12, year);
	}
	
	
	/**
	 * 获取股票信息，获取涨幅、转换实体部分
	 * @param rawResultList
	 * @return
	 */
	private static List<StockPriceDto> selectStockPriceSub(List<Map<String , String>> rawResultList){
		riseRateCount(rawResultList);//计算当前涨幅
		List<StockPriceDto> resultList = parseToObj(rawResultList);//转换为实体
		return resultList;
	}


	/**
	 * 将ResultMap转换为Map
	 * @param searchResult
	 * @return
	 * @throws SQLException 
	 */
	private static List<Map<String , String>> getStockPrice(ResultSet searchResult) throws SQLException{
		List<Map<String , String>> resultList = new ArrayList<>();
		ResultSetMetaData metaData = searchResult.getMetaData();
		int columnCount = metaData.getColumnCount();//列数量
		String[] columnNames = new String[columnCount];//列名称数组
		for(int i = 0 ; i < columnCount ; i++) columnNames[i] = metaData.getColumnName(i+1);
		while(searchResult.next()) {
			Map<String , String> resultMap = new HashMap<>();
			for(int i = 0 ; i < columnCount ; i++) resultMap.put(columnNames[i] , searchResult.getString(i + 1));
			resultList.add(resultMap);
		}
		return resultList;
	} 
	/**
	 * 计算当日涨幅
	 * @param stockPriceInfoList
	 */
	private static void riseRateCount(List<Map<String , String>> stockPriceInfoList) {
		BigDecimal lastEndPrice = null;
		Collections.reverse(stockPriceInfoList);//转换顺序，便于获取上一交易日收盘价
		for(Map<String , String> priceInfo : stockPriceInfoList) {
			BigDecimal currentEndPrice = parseToBigDecimal((priceInfo.get("stock_price_end_price")));
			if(lastEndPrice != null && currentEndPrice != null){
				BigDecimal diff = currentEndPrice.subtract(lastEndPrice);
				BigDecimal riseRate = diff.divide(lastEndPrice , 2 , BigDecimal.ROUND_HALF_UP);
				priceInfo.put(RISE_RATE , riseRate.stripTrailingZeros().toPlainString());
				priceInfo.put(LAST_END_PRICE , lastEndPrice.stripTrailingZeros().toPlainString());
			}
			lastEndPrice = currentEndPrice;
		}
		Collections.reverse(stockPriceInfoList);//顺序还原
	}
	/**
	 * 转换为实体对象
	 * @param rawResultList
	 * @return
	 */
	private static List<StockPriceDto> parseToObj(List<Map<String , String>> rawResultList){
		List<StockPriceDto> objList = new ArrayList<>();
		rawResultList.forEach(rawResult -> {
			String stockId = rawResult.get(ID);
			String stockName = rawResult.get(NAME);
			String stockCode = rawResult.get(CODE);
			String currentDate = rawResult.get(PRICE_DATE);
			String startPrice = rawResult.get(START_PRICE);
			String endPrice = rawResult.get(END_PRICE);
			String lowestPrice = rawResult.get(LOWEST_PRICE);
			String highestPrice = rawResult.get(HIGHEST_PRICE);
			String lastDayEndPrice = rawResult.get(LAST_END_PRICE);
			String riseRate = rawResult.get(RISE_RATE);
			
			StockPriceDto obj = new StockPriceDto();
			obj.setStockId(stockId);
			obj.setStockName(stockName);
			obj.setStockCode(stockCode);
			obj.setCurrentDate(currentDate);
			obj.setStartPrice(parseToBigDecimal(startPrice));
			obj.setEndPrice(parseToBigDecimal(endPrice));
			obj.setLowestPrice(parseToBigDecimal(lowestPrice));
			obj.setHighestPrice(parseToBigDecimal(highestPrice));
			obj.setLastDayEndPrice(parseToBigDecimal(lastDayEndPrice));
			obj.setRiseRate(riseRate);
			objList.add(obj);
		});
		return objList;
	}
	
	/**
	 * 查询当年上涨、下跌股票数量
	 * @param stockId
	 * @param year
	 * @return
	 */
	private static List<StockRiseAndFallCountMonthDto> doGetStockRiseAndFallCountMonth(String stockId, String year) {
		List<StockPriceDto> currentYearStockPriceList = selectStockPriceInfo(stockId, year);//获取当年股票价格信息
		Map<String , StockRiseAndFallCountMonthDto> monthMap = initMonthMap(year);//月份Map
		doRiseAndFallCount(monthMap, currentYearStockPriceList);//计算当前年份涨跌
		List<StockRiseAndFallCountMonthDto> resultList = parseCountMapToList(monthMap);//转换为List
		return resultList;
	}
	
	/**
	 * 初始化月份Map
	 * @param year
	 * @return
	 */
	private static Map<String , StockRiseAndFallCountMonthDto> initMonthMap(String year){
		Map<String , StockRiseAndFallCountMonthDto> monthMap = new HashMap<>();
		for(int i = 1 ; i <= 12 ; i++) {
			StockRiseAndFallCountMonthDto monthData = new StockRiseAndFallCountMonthDto();
			monthData.setFallCount(0);
			monthData.setRiseCount(0);
			monthData.setYear(year);
			monthData.setMonth(i + "");
			monthMap.put(i + "" , monthData);
		}
		return monthMap;
	}
	
	/**
	 * 计算上涨股票数量和下跌股票数量
	 * @param monthMap
	 * @param currentYearStockPriceList
	 */
	private static void doRiseAndFallCount(Map<String , StockRiseAndFallCountMonthDto> monthMap , List<StockPriceDto> currentYearStockPriceList) {
		currentYearStockPriceList.forEach(stockPrice -> {
			BigDecimal riseRate = parseToBigDecimal(stockPrice.getRiseRate());
			String dateStr = stockPrice.getCurrentDate();
			if(dateStr != null) {
				String month = dateStr.substring(5 , 7);
				StockRiseAndFallCountMonthDto currentMonthCount = monthMap.get(Integer.parseInt(month) + "");
				if(riseRate != null) {
					if(riseRate.compareTo(BigDecimal.ZERO) > 0) currentMonthCount.setRiseCount(currentMonthCount.getRiseCount() + 1);
					else if(riseRate.compareTo(BigDecimal.ZERO) < 0) currentMonthCount.setFallCount(currentMonthCount.getFallCount() + 1);
				}
			}
		});
	}
	/**
	 * 将Map转换为List并排序，升序
	 * @param monthMap
	 * @return
	 */
	private static List<StockRiseAndFallCountMonthDto> parseCountMapToList(Map<String , StockRiseAndFallCountMonthDto> monthMap){
		List<StockRiseAndFallCountMonthDto> resultList = new ArrayList<>();
		monthMap.forEach((key , value) -> {
			resultList.add(value);
		});
		Collections.sort(resultList, (data1 , data2) -> {
			String monthA = data1.getMonth();
			String monthB = data2.getMonth();
			return Integer.parseInt(monthA) - Integer.parseInt(monthB);
		});
		return resultList;
	}

	/**
	 * 将字符串转换为BigDecimal
	 * @param value
	 * @return
	 */
	private static BigDecimal parseToBigDecimal(String value) {
		return value == null ? null : new BigDecimal(value);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
}
