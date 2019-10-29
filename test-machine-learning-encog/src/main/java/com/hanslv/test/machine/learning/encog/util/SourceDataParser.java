package com.hanslv.test.machine.learning.encog.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.normalize.AnalystNormalizeCSV;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.specific.CSVNeuralDataSet;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
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
	 *
	 * @param filePath 标准化文件路径，会同时生成raw文件，在使用前需要先创建MLConstants.RAW_DATA_FILE_PATH对应文件夹
	 * @param objectStringList
	 * @param inputSize
	 * @param idealOutputSize
	 * @param headers
	 * @return
	 */
	@Deprecated
	public static CSVNeuralDataSet dataAnalyze(String filePath , List<String> objectStringList , int inputSize , int idealOutputSize , boolean headers){
		String dataFilePath = parseRawData(filePath , objectStringList);
		return new CSVNeuralDataSet(dataFilePath , inputSize , idealOutputSize , headers);
	}
	
	
	public static MLDataSet dataAnalyze(List<String> objectStringList , String[] fieldNames , int rangeStart , int rangeEnd) {
		/*
		 * 返回的结果集合
		 */
		BasicMLDataSet paredDataSet = new BasicMLDataSet();
		
		/*
		 * 存放每个字段的NormalizedField对象，key=字段名称  value=对应的NormalizedField对象
		 */
		Map<String , NormalizedField> normalizedFieldMap = new HashMap<>();
		for(String fieldName : fieldNames) {
			NormalizedField currentNormalizedField = new NormalizedField();
			currentNormalizedField.setAction(NormalizationAction.Normalize);
			currentNormalizedField.setNormalizedHigh(0.9);
			currentNormalizedField.setNormalizedLow(-0.9);
			normalizedFieldMap.put(fieldName , currentNormalizedField);
		}
		
		/*
		 * 遍历数据List
		 */
		for(String objectString : objectStringList) {
			String[] objectStringArray = objectString.split(",");
			/*
			 * 遍历每条数据字符串中的每个字段，
			 * 与当前NormalizedField对象中最大值对比
			 */
			for(int i = 0 ; i < objectStringArray.length ; i++) {
				/*
				 * 当前字段分析器
				 */
				NormalizedField currentNormalizedField = normalizedFieldMap.get(fieldNames[i]);
				Double currentVal = new Double(objectStringArray[i]);
				if(currentNormalizedField.getActualHigh() < currentVal) currentNormalizedField.setActualHigh(currentVal);
				if(currentNormalizedField.getActualLow() > currentVal) currentNormalizedField.setActualLow(currentVal);
			}
		}
		
		return paredDataSet;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 将数据实体写入到CSV文件中
	 * 
	 * 在调用前需要先在系统中创建MLConstants.RAW_DATA_FILE_PATH对应文件夹
	 * 
	 * @param filePath 文件名称，创建后的文件名称会添加4raw后缀
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
	private static String parseRawData(String filePath , List<String> objectStringList) {
		/*
		 * 非标准化数据文件
		 */
		File rawDataFile = new File(writeRawToCSV(filePath , objectStringList));
		
		/*
		 * 标准化数据文件路径
		 */
		String dataFilePath = MLConstants.DATA_FILE_PATH + filePath + MLConstants.DATA_FILE_TYPE;
		try {
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
			normalizer.normalize(new File(dataFilePath));
		}finally {
			/*
			 * 删除非标准化数据文件
			 */
			rawDataFile.delete();
		}
		
		return dataFilePath;
	}
}
