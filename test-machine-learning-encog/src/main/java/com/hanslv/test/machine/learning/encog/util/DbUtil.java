package com.hanslv.test.machine.learning.encog.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 从数据库获取指定数据
 * @author hanslv
 *
 */
public class DbUtil {
	/**
	 * 获取一只股票指定日期之后指定数量的日期-成交量信息
	 * @param stockId
	 * @param startDate
	 * @param limit
	 * @return
	 */
	public static List<String> getDataAndVolumeMap(String stockId , String startDate , Integer limit){
		List<String> dataList = new ArrayList<>();
		
//		String sql = "SELECT stock_price_date , stock_price_volume FROM tab_stock_price WHERE stockId = ? AND stock_price_date > ? LIMIT ?";
		String sql = 
		"SELECT stock_price_date , stock_price_volume " + 
		"FROM tab_stock_price_shangzheng_0001 WHERE stock_id = ? AND stock_price_date >= ?" + 
		"UNION " + 
		"SELECT stock_price_date , stock_price_volume " +  
		"FROM tab_stock_price_shangzheng_0002 WHERE stock_id = ? AND stock_price_date >= ?" + 
		"UNION " + 
		"SELECT stock_price_date , stock_price_volume " +  
		"FROM tab_stock_price_shangzheng_0003 WHERE stock_id = ? AND stock_price_date >= ?" + 
		"UNION " + 
		"SELECT stock_price_date , stock_price_volume " +  
		"FROM tab_stock_price_shenzheng_0001 WHERE stock_id = ? AND stock_price_date >= ?" + 
		"UNION " + 
		"SELECT stock_price_date , stock_price_volume " +  
		"FROM tab_stock_price_shenzheng_0002 WHERE stock_id = ? AND stock_price_date >= ?" + 
		"UNION " + 
		"SELECT stock_price_date , stock_price_volume " +  
		"FROM tab_stock_price_shenzheng_0003 WHERE stock_id = ? AND stock_price_date >= ?" + 
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
					String volume = resultSet.getString(2);
					String dataStr = date[0] + "," + Integer.parseInt(date[1]) + "," + Integer.parseInt(date[2]) + "," + volume;
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
}
