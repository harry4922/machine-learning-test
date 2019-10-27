package com.hanslv.test.machine.learning.encog.constants;

import java.io.File;

/**
 * 机器学习相关常量
 * 
 * -------------------------------------------
 * 1、非标准化神经元输入数据文件根路径												RAW_DATA_FILE_PATH(E:\Java\eclipse\machine-learning-test\dataFiles\machine-learning-raw-data)
 * 2、非标准化神经元输入数据文件名称后缀												RAW_DATA_FILE_NAME_SUFFIX
 * 3、神经元输入数据文件类型后缀														DATA_FILE_TYPE
 * 4、标准化神经元输入数据文件根路径													DATA_FILE_PATH
 * -------------------------------------------
 * @author hanslv
 *
 */
public abstract class MLConstants {
	public static final String RAW_DATA_FILE_PATH = 
			"E:" + File.separator + 
			"Java" + File.separator + 
			"eclipse" + File.separator + 
			"machine-learning-test" + File.separator + 
			"dataFiles" + File.separator + 
			"machine-learning-raw-data" + File.separator;//非标准化神经元输入数据文件根路径
	public static final String RAW_DATA_FILE_NAME_SUFFIX = "_raw";//非标准化神经元输入数据文件名称后缀
	
	
	public static final String DATA_FILE_PATH = 
			"E:" + File.separator + 
			"Java" + File.separator + 
			"eclipse" + File.separator + 
			"machine-learning-test" + File.separator + 
			"dataFiles" + File.separator + 
			"machine-learning-data" + File.separator;//标准化神经元输入数据文件根路径
	

	public static final String DATA_FILE_TYPE = ".csv";//神经元输入数据文件类型后缀
}
