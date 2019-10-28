package com.hanslv.test.machine.learning.encog.test;

import java.util.ArrayList;
import java.util.List;

import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

public class TestParseRawData {
	static String title = "test1";
	static String data1 = "23";
	static String data2 = "56";
	static String data3 = "78";
	
	static String filePath = "test-file";
	
	public static void main(String[] args) {
		List<String> rawDataList = new ArrayList<>();
		rawDataList.add(title);
		rawDataList.add(data1);
		rawDataList.add(data2);
		rawDataList.add(data3);
		
		for(double[] analyzedDataArray : SourceDataParser.dataAnalyze(filePath, rawDataList)) {
			for(double analyzedData : analyzedDataArray) System.out.print(analyzedData);
			System.out.println();
		}
	}
}
