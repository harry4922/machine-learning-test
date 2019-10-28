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
	public static List<List<String>> getDataAndVolumeMap(String stockId , String startDate , String limit){
		List<List<String>> dateVolumeList = new ArrayList<>();
		List<String> dateList = new ArrayList<>();
		List<String> volumeList = new ArrayList<>();
		String sql = "SELECT stock_price_date , stock_price_volume FROM tab_stock_price WHERE stockId = ? AND stock_price_date > ? LIMIT ?";
		
		try(PreparedStatement pstmt = JdbcUtil.getJdbcConnection().prepareStatement(sql);){
			pstmt.setString(1 , stockId);
			pstmt.setString(2 , startDate);
			pstmt.setString(3 , limit);
			try(ResultSet resultSet = pstmt.executeQuery();){
				while(resultSet.next()) {
					String date = resultSet.getString(1);
					String volume = resultSet.getString(2);
					dateList.add(date);
					volumeList.add(volume);
				}
				dateVolumeList.add(dateList);
				dateVolumeList.add(volumeList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
		return dateVolumeList;
	} 
}
