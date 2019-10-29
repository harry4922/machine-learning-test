package com.hanslv.test.machine.learning.encog.test;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;

import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

public class TestParseRawData {
	static String title = "test1,test2";
	static String data1 = "5,2";
	static String data2 = "4,7";
	static String data3 = "4,3";
	static String data4 = "4,3";
//	static String data5 = "5,4,3.8";
//	static String data6 = "5,4,3.7";
	
	static String filePath = "testfile";
	
	public static void main(String[] args) {
		List<String> rawDataList = new ArrayList<>();
		rawDataList.add(title);
		rawDataList.add(data1);
		rawDataList.add(data2);
		rawDataList.add(data3);
		rawDataList.add(data4);
//		rawDataList.add(data5);
//		rawDataList.add(data6);
		
		for(MLDataPair checkDataPair : SourceDataParser.dataAnalyze(filePath, rawDataList , 1 , 1 , true)) {
			BasicMLData input = new BasicMLData(checkDataPair.getInput());
			BasicMLData output = new BasicMLData(checkDataPair.getIdeal());
			System.out.println("输入：" + input.toString() + ";" + "输出：" + output.toString());
		}
	}
}
