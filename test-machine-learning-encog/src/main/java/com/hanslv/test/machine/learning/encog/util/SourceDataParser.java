package com.hanslv.test.machine.learning.encog.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.normalize.AnalystNormalizeCSV;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.util.csv.CSVFormat;

import com.hanslv.test.machine.learning.encog.constants.MLConstants;

/**
 * 数据源信息格式化工具类
 * 
 * ----------------------------------------------------
 * 1、获取标准化的数据										public static double[][] dataAnalyze(String filePath , List<String> objectStringList)
 * ----------------------------------------------------
 * @author hanslv
 *
 */
public class SourceDataParser {
	
	/**
	 * 1、获取标准化的数据
	 * @param filePath 标准化文件路径，会同时生成raw文件，在使用前需要先创建MLConstants.RAW_DATA_FILE_PATH对应文件夹
	 * @param objectStringList
	 * @return 返回double[][]
	 */
	public static double[][] dataAnalyze(String filePath , List<String> objectStringList){
		List<double[]> analyzedDataList = new ArrayList<>();
		try(FileInputStream fileInputStream = new FileInputStream(parseRawData(filePath , objectStringList));
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream , "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
			/*
			 * 跳过表头
			 */
			bufferedReader.readLine();
			
			/*
			 * 遍历读取标准化后的数据
			 */
			String dataLine = null;
			while((dataLine = bufferedReader.readLine()) != null) {
				String[] dataArray = dataLine.split(",");
				double[] dataDoubleArray = new double[dataArray.length];
				/*
				 * 将数据转换为double数组
				 */
				for(int i = 0 ; i < dataArray.length ; i++) dataDoubleArray[i] = new Double(dataArray[i]);
				
				/*
				 * 放入List
				 */
				analyzedDataList.add(dataDoubleArray);
			}
		}catch(IOException e) {
			e.printStackTrace();
			System.err.println("数据解析错误");
			return null;
		}
		
		return analyzedDataList.stream().toArray(double[][] :: new);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 将数据实体写入到CSV文件中
	 * 
	 * 在调用前需要先在系统中创建MLConstants.RAW_DATA_FILE_PATH对应文件夹
	 * 
	 * @param filePath 文件名称，创建后的文件名称会添加_raw后缀
	 * @param objectStringList 非标准化输入数据List，数据需要符合CSV文件格式(以,分隔每个属性)
	 * @return 返回创建后的文件全路径
	 */
	private static String writeRawToCSV(String filePath , List<String> objectStringList) {
		/*
		 * 当前非标准化神经元输入文件全路径
		 */
		String rawDataFilePath = MLConstants.RAW_DATA_FILE_PATH + filePath + MLConstants.RAW_DATA_FILE_NAME_SUFFIX + MLConstants.DATA_FILE_TYPE;
		
		/*
		 * 文件对象
		 */
		File rawDataFile = new File(rawDataFilePath);
		
		/*
		 * 创建文件
		 */
		if(!rawDataFile.exists()) {
			try {
				rawDataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("创建新文件失败");
				return null;
			}
		}else {
			System.err.println("当前文件已存在！");
			return null;
		}
		
		try(FileOutputStream fileOutputStream = new FileOutputStream(rawDataFilePath);
				OutputStreamWriter outputStreamRader = new OutputStreamWriter(fileOutputStream , "UTF-8");
				BufferedWriter bufferedReader = new BufferedWriter(outputStreamRader)){
			for(String objectString : objectStringList) bufferedReader.write(objectString + System.lineSeparator());
		}catch(IOException e) {
			e.printStackTrace();
			System.err.println("向新文件写入数据失败");
			return null;
		}
		
		System.out.println("创建了新数据文件：" + rawDataFilePath);
		return rawDataFilePath;
	}
	
	
	/**
	 * 将传入的Java对象数据格式化为(-1,1)区间的数据，并保存在对应的目录中
	 * 
	 * 在使用前需要创建RAW_DATA_FILE_PATH、DATA_FILE_PATH两个目录
	 * 
	 * @param filePath 目标文件名称
	 * @param objectStringList 非标准化数据List，第一列包含表头
	 */
	private static File parseRawData(String filePath , List<String> objectStringList) {
		/*
		 * 非标准化数据文件
		 */
		File rawDataFile = new File(writeRawToCSV(filePath , objectStringList));
		
		/*
		 * 标准化数据文件
		 */
		File dataFile = new File(MLConstants.DATA_FILE_PATH + filePath);

		/*
		 * 实例化Encog Analyst Script脚本运行器
		 */
		EncogAnalyst analyst = new EncogAnalyst();
		
		/*
		 * 实例化CSV分析器
		 */
		AnalystWizard wizard = new AnalystWizard(analyst);
		
		
		/*
		 * 执行Encog Analyst Script，分析非标准化数据文件
		 */
		wizard.wizard(rawDataFile , true , AnalystFileFormat.DECPNT_COMMA);
		
		/*
		 * 对当前CSV文件进行规范化处理
		 */
		final AnalystNormalizeCSV normalizer = new AnalystNormalizeCSV();
		normalizer.analyze(rawDataFile , true , CSVFormat.ENGLISH , analyst);
		normalizer.setProduceOutputHeaders(true);
		normalizer.normalize(dataFile);
		
		return dataFile;
	}
}
