package com.hanslv.test.machine.learning.encog.constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 数据库常量类
 * 
 * -------------------------------------------------
 * 1、数据库链接URL											databaseUrl
 * 2、数据库链接驱动名称										databaseDriverClassName
 * 3、数据库账号												databaseUserName
 * 4、数据库密码												databasePassword
 * -------------------------------------------------
 * @author hanslv
 *
 */
public abstract class DbConstants {
	public static String databaseUrl;//数据库链接URL
	public static String databaseDriverClassName;//数据库链接驱动名称
	public static String databaseUserName;//数据库账号
	public static String databasePassword;//数据库密码
	
	private static final String DB_PROP_PATH = "/props/database.properties";//数据库配置文件地址
	
	static {
		Properties databaseProp = new Properties();
		try(InputStream inputStream = DbConstants.class.getResourceAsStream(DB_PROP_PATH);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream , "UTF-8")){
			databaseProp.load(inputStreamReader);
			
			databaseUrl = databaseProp.getProperty("database.url");
			databaseDriverClassName = databaseProp.getProperty("database.driver");
			databaseUserName = databaseProp.getProperty("database.username");
			databasePassword = databaseProp.getProperty("database.password");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
