package com.hanslv.test.machine.learning.encog.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.hanslv.test.machine.learning.encog.constants.DbConstants;

/**
 * 数据库链接Util
 * 
 * ------------------------------------------
 * 1、获取或创建一个当前线程持有的Jdbc连接												public static Connection getJdbcConnection()
 * 2、关闭当前线程持有的Jdbc连接															public static void closeJdbcConnection()
 * ------------------------------------------
 * @author hanslv
 *
 */
public class JdbcUtil {
	private static ThreadLocal<Connection> connectionThreadLocal;
	
	static {
		try {
			Class.forName(DbConstants.databaseDriverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		connectionThreadLocal = new ThreadLocal<>();
	}
	
	/**
	 * 1、获取或创建一个当前线程持有的Jdbc连接
	 * @return
	 */
	public static Connection getJdbcConnection() {
		Connection conn = connectionThreadLocal.get();
		if(conn == null) {
			try {
				conn = DriverManager.getConnection(DbConstants.databaseUrl , DbConstants.databaseUserName , DbConstants.databasePassword);
				conn.setAutoCommit(false);
				connectionThreadLocal.set(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return conn;
	}
	
	/**
	 * 2、关闭当前线程持有的Jdbc连接
	 */
	public static void closeJdbcConnection() {
		Connection conn = connectionThreadLocal.get();
		if(conn != null) {
			try {
				connectionThreadLocal.remove();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
