package com.hanslv.test.machine.learning.encog.test;

import java.util.ArrayList;
import java.util.List;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;

import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

public class TestParseRawData {
	static String title = "testa,testb,testc,testd,teste";
//	static String data1 = "5.1 , 3.5 , 1.4 , 2 , Iris-setosa";
//	static String data2 = "4.9 , 3.0 , 1.4 , 2 , Iris-setosa";
//	static String data3 = "4.7 , 3.2 , 1.3 , 2 , Iris-setosa";
//	static String data4 = "4.6 , 3.1 , 1.5 , 2 , Iris-setosa";
	
//	static String data1 = "1.0 , 2.0 , 3.0 , 5.0 , Iris-setosa";
//	static String data2 = "2.0 , 3.0 , 4.0 , 6.0 , Iris-setosb";
//	static String data3 = "3.0 , 4.0 , 5.0 , 7.0 , Iris-setosc";
//	static String data4 = "4.0 , 5.0 , 6.0 , 8.0 , Iris-setosd";
	
//	static String data1 = "1 , 2 , 3 , 5 , Iris-setosa";
//	static String data2 = "2 , 3 , 4 , 6 , Iris-setosa";
//	static String data3 = "3 , 4 , 5 , 7 , Iris-setosa";
//	static String data4 = "4 , 5 , 6 , 8 , Iris-setosa";
	
//	static String data1 = "1 , 2 , 3 , Iris-setosa , 5";
//	static String data2 = "2 , 3 , 4 , Iris-setosa , 6";
//	static String data3 = "3 , 4 , 5 , Iris-setosa , 7";
//	static String data4 = "4 , 5 , 6 , Iris-setosa , 8";
	
//	static String data1 = "1.0 , 2.0 , 3.0 , Iris-setosa , 5.0";
//	static String data2 = "2.0 , 3.0 , 4.0 , Iris-setosb , 6.0";
//	static String data3 = "3.0 , 4.0 , 5.0 , Iris-setosc , 7.0";
//	static String data4 = "4.0 , 5.0 , 6.0 , Iris-setosd , 8.0";
	
//	static String data1 = "1.0 , 2.0 , 3.0 , 1 , 5.0";
//	static String data2 = "2.0 , 3.0 , 4.0 , 1 , 6.0";
//	static String data3 = "3.0 , 4.0 , 5.0 , 1 , 7.0";
//	static String data4 = "4.0 , 5.0 , 6.0 , 1 , 8.0";
	
	static String data1 = "1.0 , 2.0 , 3.0 , 1 , 5.0";
	static String data2 = "2.0 , 3.0 , 4.0 , 2 , 6.0";
	static String data3 = "3.0 , 4.0 , 5.0 , 3 , 7.0";
	static String data4 = "4.0 , 5.0 , 6.0 , 4 , 8.0";
	
//	static String data1 = "1.0 , 2.0 , 3.0 , 5.0 , 6.0";
//	static String data2 = "2.0 , 3.0 , 4.0 , 6.0 , 7.0";
//	static String data3 = "3.0 , 4.0 , 5.0 , 7.0 , 8.0";
//	static String data4 = "4.0 , 5.0 , 6.0 , 8.0 , 9.0";
	
	public static void main(String[] args) {
		List<String> rawDataList = new ArrayList<>();
		rawDataList.add(data1);
		rawDataList.add(data2);
		rawDataList.add(data3);
		rawDataList.add(data4);
		
		for(MLDataPair checkDataPair : SourceDataParser.dataAnalyze(rawDataList , title.split(",") , 1 , 0.9 , -0.9).values().iterator().next()) {
			BasicMLData input = new BasicMLData(checkDataPair.getInput());
			BasicMLData output = new BasicMLData(checkDataPair.getIdeal());
			System.out.println("输入：" + input.toString() + ";" + "输出：" + output.toString());
		}
		
		
//		NormalizedField fuelStats = new NormalizedField(NormalizationAction.Normalize , "test", 1.001 , 0.999 , -0.9 , 0.9) ;
//		System.out.println(fuelStats.normalize(1));
	}
}
