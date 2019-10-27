package com.hanslv.test.machine.learning.encog.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hanslv.test.machine.learning.encog.util.JdbcUtil;

public class TestJdbcConnection {
	static String sql = "SELECT COUNT(*) FROM tab_stock_info";
	
	public static void main(String[] args) {
		try {
			PreparedStatement pstmt = JdbcUtil.getJdbcConnection().prepareStatement(sql);
			ResultSet resultSet = pstmt.executeQuery();
			while(resultSet.next()) System.out.println("获取到数据：" + resultSet.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.closeJdbcConnection();
		}
	}
}
