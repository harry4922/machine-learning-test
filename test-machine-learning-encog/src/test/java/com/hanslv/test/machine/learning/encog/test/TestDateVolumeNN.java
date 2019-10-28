package com.hanslv.test.machine.learning.encog.test;

import java.time.LocalDate;
import java.util.List;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import com.hanslv.test.machine.learning.encog.stock.DateVolumeNN;
import com.hanslv.test.machine.learning.encog.util.DbUtil;
import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

public class TestDateVolumeNN {
	public static void main(String[] args) {
		String startDate = "2019-07-26";
		String stockId = "1";
		/*
		 * 获取20天数据
		 */
		List<List<String>> daysData20 = DbUtil.getDataAndVolumeMap(stockId, startDate, 20);
		
		/*
		 * 算法训练数据
		 */
		double[][] inputData = SourceDataParser.dataAnalyze("input,1,2019-07-26(test)", daysData20.get(0));
		double[][] idealOutputData = SourceDataParser.dataAnalyze("idealOutput,1,2019-07-26(test)", daysData20.get(1));
		
		
		/*
		 * 5天验证数据
		 */
		String dateStr = daysData20.get(0).get(daysData20.get(0).size() - 1);
		Integer year = Integer.parseInt(dateStr.substring(0 , 4));
		Integer month = Integer.parseInt(dateStr.substring(4 , 6));
		Integer day = Integer.parseInt(dateStr.substring(6 , 8));
		String nextDate = LocalDate.of(year , month , day).plusDays(1).toString();
		List<List<String>> daysData5 = DbUtil.getDataAndVolumeMap(stockId , nextDate , 5);
		double[][] checkInputData = SourceDataParser.dataAnalyze("inpuy,1," + nextDate + "(test)" , daysData5.get(0));
		double[][] checkIdealOutput = SourceDataParser.dataAnalyze("idealOutput,1," + nextDate + "(test)" , daysData5.get(1));
		
		/*
		 * 训练算法
		 */
		BasicNetwork algorithmModel = DateVolumeNN.train(inputData , idealOutputData , 0.001);
		
		
		/*
		 * 输出结果
		 */
		for(int i = 0 ; i < checkInputData.length ; i++) {
			BasicMLData checkInput = new BasicMLData(checkInputData[i]);
			System.out.println("--------------------------------");
			for(int j = 0 ; j < checkInputData[i].length ; j++) System.out.print(checkInputData[i][j]);
			System.out.println("预测输出：");
			for(int j = 0 ; j < checkIdealOutput[i].length ; j++) System.out.print(checkIdealOutput[i][j]);
			
			/*
			 * 执行算法
			 */
			MLData output = algorithmModel.compute(checkInput);
			double[] outputArray = output.getData();
			System.out.println("实际输出：");
			for(int j = 0 ; j < outputArray.length ; j++) System.out.print(outputArray[j]);
		}
		
	}
}
