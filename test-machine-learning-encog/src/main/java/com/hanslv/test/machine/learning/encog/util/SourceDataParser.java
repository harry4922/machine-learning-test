package com.hanslv.test.machine.learning.encog.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.normalize.AnalystNormalizeCSV;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.specific.CSVNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
import org.encog.util.csv.CSVFormat;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;

import com.hanslv.test.machine.learning.encog.constants.MLConstants;

/**
 * 数据源信息格式化工具类
 * 
 * ----------------------------------------------------
 * 1、获取标准化的数据						public static MLDataSet dataAnalyze(List<String> objectStringList , String[] fieldNames , int targetColumnLength , double normalizedH , double normalizedL)
 * 2、神经网络返回单个数据时判断是否符合预期	public static boolean check(BasicMLData idealOutput , BasicMLData realOutput , double limit)
 * 3、将算法保存到文件						public static void saveAlgorithm(String filePath , BasicNetwork trainedNetwork)
 * 4、从文件加载算法							public static BasicNetwork loadAlgorithm(String filePath)
 * ----------------------------------------------------
 * @author hanslv
 *
 */
public class SourceDataParser {


	/**
	 * 1、获取标准化的数据
	 * @param objectStringList 非标准化数据集合，需要将idealOutput字段放在后边，只可传入数字格式的字符串
	 * @param fieldNames 字段名称数组
	 * @param normalizedH 标准化后的最大值
	 * @param normalizedL 标准化后的最小值
	 * @param targetColumnLength idealOutput字段数量
	 * @return
	 */
	public static Map<Map<String , NormalizedField> , MLDataSet> dataAnalyze(List<String> objectStringList , String[] fieldNames , int targetColumnLength , double normalizedH , double normalizedL) {
		/*
		 * 标准化后的输入样本数组和预测输出样本数组
		 */
		double[][] inputArray = new double[objectStringList.size()][];
		double[][] idealOutputArray = new double[objectStringList.size()][];
		
		/*
		 * 存放每个字段的NormalizedField对象，key=字段名称  value=对应的NormalizedField对象
		 */
		Map<String , NormalizedField> normalizedFieldMap = new HashMap<>();
		for(String fieldName : fieldNames) {
			NormalizedField currentNormalizedField = new NormalizedField(NormalizationAction.Normalize , fieldName , Integer.MIN_VALUE , Integer.MAX_VALUE , normalizedH , normalizedL);
			normalizedFieldMap.put(fieldName , currentNormalizedField);
		}
		
		/*
		 * 遍历数据List
		 */
		for(int i = 0 ; i < objectStringList.size() ; i++) {
			String[] objectStringArray = objectStringList.get(i).split(",");
			
			/*
			 * 遍历每条数据字符串中的每个字段，
			 * 更新当前NormalizedField的最大值和最小值
			 */
			for(int j = 0 ; j < objectStringArray.length ; j++) {
				/*
				 * 当前字段分析器
				 */
				NormalizedField currentNormalizedField = normalizedFieldMap.get(fieldNames[j]);
				Double currentVal = new Double(objectStringArray[j].trim());
				if(currentNormalizedField.getActualHigh() < currentVal) currentNormalizedField.setActualHigh(currentVal);
				if(currentNormalizedField.getActualLow() > currentVal) currentNormalizedField.setActualLow(currentVal);
			}
		}
		
		
		for(int i = 0 ; i < objectStringList.size() ; i++) {
			String[] objectStringArray = objectStringList.get(i).split(",");
			
			/*
			 * 本条数据输入样本数据数组
			 */
			double[] currentInputDataArray = new double[fieldNames.length - targetColumnLength];
			
			/*
			 * 本条数据预测输出样本数据数组
			 */
			double[] currentIdealOutputArray = new double[targetColumnLength];
			
			/*
			 * 获取标准化后的训练数据
			 */
			for(int j = 0 ; j < objectStringArray.length ; j++) {
				NormalizedField currentNormalizedField = normalizedFieldMap.get(fieldNames[j]);
				
				/*
				 * 判断当前数据是输入还是预测输出
				 * 本条数据列下标小于全部数据列数量-输出数据列数量
				 */
				if(j < (fieldNames.length - targetColumnLength)) currentInputDataArray[j] = new Double(objectStringArray[j].trim());
				else currentIdealOutputArray[j - fieldNames.length + targetColumnLength] = currentNormalizedField.normalize(new Double(objectStringArray[j].trim()));
				
			}
			
			/*
			 * 将本条数据添加到汇总数组中
			 */
			inputArray[i] = currentInputDataArray;
			idealOutputArray[i] = currentIdealOutputArray;
		}
		
		Map<Map<String , NormalizedField> , MLDataSet> resultMap = new HashMap<>();
		resultMap.put(normalizedFieldMap , new BasicMLDataSet(inputArray , idealOutputArray));
		
		return resultMap;
	}
	
	
	
	
	/**
	 * 2、神经网络返回单个数据时判断是否符合预期
	 * @param idealOutput
	 * @param realOutput
	 * @param limit
	 * @return
	 */
	public static boolean check(BasicMLData idealOutput , BasicMLData realOutput , double limit) {
		double idealOutputDouble = idealOutput.getData(0);
		double realOutputDouble = realOutput.getData(0);
		double checkFlag = 0;
		
		if(idealOutputDouble >=0 && realOutputDouble >= 0) {
			/*
			 * 二者都大于等于0
			 */
			if(idealOutputDouble > realOutputDouble) checkFlag = idealOutputDouble - realOutputDouble;
			else if(idealOutputDouble < realOutputDouble) checkFlag = realOutputDouble - idealOutputDouble;
			else return true;
			if(checkFlag / idealOutputDouble + realOutputDouble < limit) return true;
		}else if(idealOutputDouble >= 0 && realOutputDouble < 0) {
			/*
			 * 预计输出大于等于0，实际输出小于0
			 */
			checkFlag = idealOutputDouble - realOutputDouble;
			if(checkFlag / idealOutputDouble - realOutputDouble < limit) return true;
		}else if(idealOutputDouble < 0 && realOutputDouble >= 0) {
			/*
			 * 预计输出小于0，实际输出大于等于0
			 */
			checkFlag = realOutputDouble - idealOutputDouble;
			if(checkFlag / realOutputDouble - idealOutputDouble < limit) return true;
		}else {
			/*
			 * 二者都小于0
			 */
			if(idealOutputDouble > realOutputDouble) checkFlag = idealOutputDouble + realOutputDouble;
			else if(idealOutputDouble < realOutputDouble) checkFlag = realOutputDouble + idealOutputDouble;
			else return true;
			if(checkFlag / -(idealOutputDouble - realOutputDouble) < limit) return true;
		}
		
		return false;
	}
	
	
	/**
	 * 3、将算法保存到文件
	 * @param filePath
	 * @param trainedNetwork
	 */
	public static void saveAlgorithm(String filePath , BasicNetwork trainedNetwork) {
		File algorithmFile = new File(filePath);
		
		/*
		 * 先删除当前存在的算法文件
		 */
		if(algorithmFile.exists()) algorithmFile.delete();
		
		EncogDirectoryPersistence.saveObject(algorithmFile , trainedNetwork);
	}
	
	
	/**
	 * 4、从文件加载算法
	 * @param filePath
	 * @return
	 */
	public static BasicNetwork loadAlgorithm(String filePath) {
		return (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(filePath));
	}
	
	
	
	
	
	
	
	/**
	 * 将获取到的数据标准化并转换为DataSetIterator
	 * @param rawDataList
	 * @param idealOutputSize
	 * @return
	 */
	public static List<DataSetIterator> dl4jDataNormalizer(List<String> rawDataList , List<String> testDataList , int idealOutputSize){
		List<DataSetIterator> iteratorList = new ArrayList<>();
		
		/*
		 * 数据标准化器
		 */
//		NormalizerStandardize normalizer = new NormalizerStandardize();
		NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler(-1 , 1);
		
		/*
		 * 实例化数据集合迭代器
		 */
		DataSetIterator trainDataIterator = new ListDataSetIterator<>(dl4jDataParser(rawDataList, idealOutputSize) , rawDataList.size());
		DataSetIterator testDataIterator = new ListDataSetIterator<>(dl4jDataParser(testDataList , idealOutputSize) , testDataList.size());
		
		/*
		 * 初始化数据标准化器
		 */
		normalizer.fitLabel(true);//指定是否标准化idealOutput
		normalizer.fit(trainDataIterator);
//		normalizer.fit(testDataIterator);
		
		/*
		 * 给训练数据集合配置标准化器
		 */
		trainDataIterator.setPreProcessor(normalizer);
		testDataIterator.setPreProcessor(normalizer);
		
		iteratorList.add(trainDataIterator);
		iteratorList.add(testDataIterator);
		
		return iteratorList;
	}
	
	
	/**
	 * 将List<String>数据转换为List<DataSet>数据
	 * @param rawDataList
	 * @param idealOutputSize
	 * @return
	 */
	private static List<DataSet> dl4jDataParser(List<String> rawDataList , int idealOutputSize){
		/*
		 * 将数据封装成DataSet集合
		 */
		List<DataSet> dataSetList = new ArrayList<>();
		
		/*
		 * 遍历raw数据集
		 */
		for(String rawData : rawDataList) {
			
			/*
			 * 未标准化数据字符串数组
			 */
			String[] rawDataArray = rawData.split(",");
			
			/*
			 * 输入数据的double数组、期望输出数据数组
			 */
			double[] inputDataDoubleArray = new double[rawDataArray.length - idealOutputSize];
			double[] idealOutputDataDoubleArray = new double[idealOutputSize];


			
			/*
			 * 根据下标将数据插入到对应的double数组中
			 */
			for(int i = 0 ; i < rawDataArray.length ; i++) {
				if(i < rawDataArray.length - idealOutputSize) inputDataDoubleArray[i] = new Double(rawDataArray[i]);
				else idealOutputDataDoubleArray[i - rawDataArray.length + idealOutputSize] = new Double(rawDataArray[i]);
			}
			
			/*
			 * 输入数据向量数组、期望输出数据向量数组
			 * dimension 0 = number of examples in minibatch(训练批次长度)
			 * dimension 1 = size of each vector (i.e., number of characters)(训练数据列数)
			 * dimension 2 = length of each time series/example(训练数据行数)
			 * Why 'f' order here? See http://deeplearning4j.org/usingrnns.html#data section "Alternative: Implementing a custom DataSetIterator"
			 */
			INDArray inputScalerArray = Nd4j.create(inputDataDoubleArray);
			INDArray idealOutputScalerArray = Nd4j.create(idealOutputDataDoubleArray);
//			INDArray inputScalerArray = Nd4j.create(new int[] {1 , 3 , 1} , 'f');
//			INDArray idealOutputScalerArray = Nd4j.create(new int[] {1 , idealOutputSize , 1} , 'f');
			
			/*
			 * 实例化DataSet对象并存入List
			 */
			dataSetList.add(new DataSet(inputScalerArray , idealOutputScalerArray));
		}
		return dataSetList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
