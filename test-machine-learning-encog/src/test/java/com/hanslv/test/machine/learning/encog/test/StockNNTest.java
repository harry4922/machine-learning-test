package com.hanslv.test.machine.learning.encog.test;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.joda.time.LocalDate;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;

import com.hanslv.test.machine.learning.dl4j.stock.TestStockLSTMBuilder;
import com.hanslv.test.machine.learning.encog.util.DbUtil;
import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

public class StockNNTest {
	static String resultFilePath = "E:\\Java\\eclipse\\stock-selector\\test\\testresult2019-12-01.txt";
	static String stockId = "7";
	static String trainEndDate = "2019-08-23";
	static int epoch = 20;
	static int trainDataSize = 10;
	static int testDataSize = 1;
	static int inputSize = 3;
	static int idealOutputSize = 2;
	static int trainTimes = 100;
	static int mainCounter = 0;
	static int goalCounter = 0;
	
	public static void main(String[] args) throws Exception{
		/*
		 * 结果输出文件
		 */
		File resultFile = new File(resultFilePath);
		if(resultFile.exists()) resultFile.delete();
		resultFile.createNewFile();
		
		
		LocalDate currentDate = LocalDate.now();
		try(FileOutputStream fileOutputStream = new FileOutputStream(resultFile);){
//			System.setOut(new PrintStream(fileOutputStream , true));
			for(int i = 1 ; i <= 3555 ; i++) {
				for(int j = 0 ; j < trainTimes ; j++) {
					String trainDate = currentDate.minusDays(j * trainDataSize * idealOutputSize).toString();//训练日期
					doTest(i + "" , trainDate , epoch , trainDataSize , testDataSize , inputSize , idealOutputSize);
					try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("final score：" + (goalCounter == 0 ? 0 : new BigDecimal(goalCounter).divide(new BigDecimal(mainCounter) , 2 , BigDecimal.ROUND_HALF_UP)));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static void doTest(String stockId , String trainEndDate , int epoch , int trainDataSize , int testDataSize , int inputSize , int idealOutputSize) {
		/*
		 * 获取全部训练数据、测试数据
		 */
		List<String> mainDataList = DbUtil.deeplearning4jData(stockId , trainEndDate , (trainDataSize + testDataSize) * 5);
		if(mainDataList == null) {
			System.err.println("数据集合小于预期");
			return;
		}
		
		/*
		 * 拆分训练集合、测试集合并执行标准化
		 */
		List<String> trainDataList = mainDataList.subList(0 , trainDataSize * 5);
		List<String> testDataList = mainDataList.subList(trainDataSize * 5 , mainDataList.size());
		List<DataSetIterator> iteratorList = SourceDataParser.dl4jDataNormalizer(trainDataList , testDataList , idealOutputSize);
		
		/*
		 * 获取训练模型
		 */
		MultiLayerNetwork lstmNetwork = TestStockLSTMBuilder.build(inputSize , idealOutputSize);
		
		/*
		 * 训练模型
		 */
		for(int i = 0 ; i < epoch ; i++) {
			lstmNetwork.fit(iteratorList.get(0));
			lstmNetwork.rnnClearPreviousState();
		}
		
		/*
		 * 预测并评判结果
		 */
		Map<Boolean , INDArray> resultMap = checkData(lstmNetwork , iteratorList.get(1));
		Entry<Boolean , INDArray> result = resultMap.entrySet().iterator().next();
		
		if(result.getKey()) {
			mainCounter++;
			if(compareIdealOutputAndtest(result.getValue() , testDataList)) {
				goalCounter++;
				System.out.println("-----------------------------------------------");
				System.out.println("找到符合要求股票：" + stockId);
				System.out.println("日期：" + trainEndDate);
				System.out.println("预测：");
				System.out.println(result.getValue());
				System.out.println("实际：");
				for(String testData : testDataList) {
					String[] testDataArray = testData.split(",");
					System.out.println(testDataArray[testDataArray.length - 2] + "," + testDataArray[testDataArray.length - 1]);
				}
			}
			System.out.println("current score：" + (goalCounter == 0 ? 0 : new BigDecimal(goalCounter).divide(new BigDecimal(mainCounter) , 2 , BigDecimal.ROUND_HALF_UP)));
		}
	}
	
	/**
	 * 预测未来数据并判断是否符合标准
	 * @param lstmNetwork
	 * @param forecastData
	 * @return 返回是否符合标准，预测输出数据
	 */
	private static Map<Boolean , INDArray> checkData(MultiLayerNetwork lstmNetwork , DataSetIterator forecastData) {
		/*
		 * 结果Map
		 */
		Map<Boolean , INDArray> resultMap = new HashMap<>();
		
		/*
		 * 获取当前标准化器
		 */
		NormalizerMinMaxScaler normalizerStandardize = (NormalizerMinMaxScaler) forecastData.getPreProcessor();
		
		/*
		 * 执行预测
		 */
		DataSet input = forecastData.next();
		INDArray output = lstmNetwork.rnnTimeStep(input.getFeatures());
		DataSet resultDataSet = new DataSet(input.getFeatures() , output);
		
		/*
		 * 反标准化结果并判断是否符合标准
		 */
		normalizerStandardize.revert(resultDataSet);
		INDArray unNormalizerOutput = resultDataSet.getLabels();
		resultMap.put(doCheckData(unNormalizerOutput) , unNormalizerOutput);
		return resultMap;
	}
	/**
	 * 判断当前反标准化结果集是否符合标准
	 * @param unNormalizerOutput
	 * @return
	 */
	private static boolean doCheckData(INDArray unNormalizerOutput) {
		Set<BigDecimal> setA = new HashSet<>();
		Set<BigDecimal> setB = new HashSet<>();
		for(int i = 0 ; i < 5 ; i ++) {
			for(int j = 0 ; j < 2 ; j++) {
				if(j == 0) setA.add(new BigDecimal(unNormalizerOutput.getDouble(i , j)).setScale(1 , BigDecimal.ROUND_HALF_UP));
				else setB.add(new BigDecimal(unNormalizerOutput.getDouble(i , j)).setScale(1 , BigDecimal.ROUND_HALF_UP));
			}
		}
		return (setA.size() > 1 || setB.size() > 1) ? false : true;
	}
	
	/**
	 * 比较实际输出是否包含于预测输出
	 * @param testOutput
	 * @param idealOutput
	 * @return
	 */
	private static boolean compareIdealOutputAndtest(INDArray testOutput , List<String> idealOutput) {
		double[] idealMaxAndLow = new double[2];
		double[] testMaxAndLow = new double[2];
		
		double idealMaxBuffer = 0;
		double idealMinBuffer = 0;
		for(String ideal : idealOutput) {
			String[] idealArray = ideal.split(",");
			String idealMaxStr = idealArray[idealArray.length - 2];
			String idealMinStr = idealArray[idealArray.length - 1];
			if(idealMaxBuffer != 0) {
				if(new Double(idealMaxStr).compareTo(idealMaxBuffer) > 0) idealMaxBuffer = new Double(idealMaxStr);
			}else idealMaxBuffer = new Double(idealMaxStr);
			
			if(idealMinBuffer != 0) {
				if(new Double(idealMinStr).compareTo(idealMinBuffer) < 0) idealMinBuffer = new Double(idealMinStr);
			}else idealMinBuffer = new Double(idealMinStr);
		}
		idealMaxAndLow[0] = idealMaxBuffer;
		idealMaxAndLow[1] = idealMinBuffer;
		
		
		double testMaxBuffer = 0;
		for(int i = 0 ; i < 5 ; i++) {
			if(testMaxBuffer != 0) {
				if(testOutput.getDouble(i , 0) > testMaxBuffer) testMaxBuffer = testOutput.getDouble(i , 0);
			}else testMaxBuffer = testOutput.getDouble(i , 0);
		}
		double testMinBuffer = 0;
		for(int i = 0 ; i < 5 ; i++) {
			if(testMinBuffer != 0) {
				if(testOutput.getDouble(i , 1) < testMinBuffer) testMinBuffer = testOutput.getDouble(i , 1);
			}else testMinBuffer = testOutput.getDouble(i , 1);
		}
		testMaxAndLow[0] = testMaxBuffer;
		testMaxAndLow[1] = testMinBuffer;
		
		double errorLimit = 0.02;
		
		if(testMaxAndLow[0] * (1-errorLimit) <= testMaxAndLow[1]) return false;
		if(testMaxAndLow[1] * (1+errorLimit) >= testMaxAndLow[0]) return false;
		return (idealMaxAndLow[0] >= testMaxAndLow[0] * (1-errorLimit) && idealMaxAndLow[1] <= testMaxAndLow[1] * (1+errorLimit)) ? true : false;
	}
	
}
