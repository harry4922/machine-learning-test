package com.hanslv.test.machine.learning.encog.test;

import java.util.ArrayList;
import java.util.List;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

public class TestDL4JParseData {
	static List<String> trainDataList = new ArrayList<>();
	static List<String> testDataList = new ArrayList<>();
	
	/**
	 * 训练数据、测试数据初始化
	 */
	static {
		String trainData1 = "0,0,0";
		String trainData2 = "4,4,4";
		String trainData3 = "3,3,3";
		String trainData4 = "1,1,1";
		String trainData5 = "2,2,2";
//		String trainData6 = "";
//		String trainData7 = "";
		
		trainDataList.add(trainData1);
		trainDataList.add(trainData2);
		trainDataList.add(trainData3);
		trainDataList.add(trainData4);
		trainDataList.add(trainData5);
//		trainDataList.add(trainData6);
//		trainDataList.add(trainData7);
		
		
		String testData1 = "10,10,10";
		String testData2 = "11,11,11";
		String testData3 = "12,12,12";
		
		testDataList.add(testData1);
		testDataList.add(testData2);
		testDataList.add(testData3);
	}
	
	
	public static void main(String[] args){
//		List<DataSet> dataSetList = SourceDataParser.dl4jDataParser(testData , 1);
//		for(DataSet dataSet : dataSetList) System.out.println(dataSet);
		List<DataSetIterator> iteratorList = SourceDataParser.dl4jDataNormalizer(trainDataList , testDataList , 1);
		
		while(iteratorList.get(0).hasNext()) {
			DataSet dataSet = iteratorList.get(0).next();
			System.out.println(dataSet);
		}
		
		System.out.println("--------------------------------------");
		
		while(iteratorList.get(1).hasNext()) {
			DataSet dataSet = iteratorList.get(1).next();
			System.out.println(dataSet);
		}
	}
}
