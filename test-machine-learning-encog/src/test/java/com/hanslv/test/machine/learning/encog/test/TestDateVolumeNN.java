package com.hanslv.test.machine.learning.encog.test;

import java.time.LocalDate;
import java.util.List;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import com.hanslv.test.machine.learning.encog.stock.DateVolumeNN;
import com.hanslv.test.machine.learning.encog.util.DbUtil;
import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

public class TestDateVolumeNN {
	public static void main(String[] args) {
		String startDate = "2019-07-26";
		String stockId = "1";
		String titles = "year,month,day,stockPriceVolume";
		
		/*
		 * 获取20天数据
		 */
		List<String> daysData20 = DbUtil.getDataAndVolumeMap(stockId, startDate, 20);
		
		/*
		 * 算法训练数据
		 */
		MLDataSet trainData = SourceDataParser.dataAnalyze(daysData20 , titles.split(",") , 1 , 0.9 , -0.9);
		
		
		/*
		 * 5天验证数据
		 */
		String dateStr[] = daysData20.get(daysData20.size() - 1).split(",");
		Integer year = Integer.parseInt(dateStr[0]);
		Integer month = Integer.parseInt(dateStr[1]);
		Integer day = Integer.parseInt(dateStr[2]);
		String nextDate = LocalDate.of(year , month , day).plusDays(1).toString();
		List<String> daysData5 = DbUtil.getDataAndVolumeMap(stockId , nextDate , 5);
		MLDataSet checkData = SourceDataParser.dataAnalyze(daysData5 , titles.split(",") , 1 , 0.9 , -0.9);
		
		/*
		 * 训练算法
		 */
		BasicNetwork algorithmModel = DateVolumeNN.train(trainData , 0.001);
		
		/*
		 * 输出结果
		 */
		for(MLDataPair checkDataPair : checkData) {
			BasicMLData checkInput = new BasicMLData(checkDataPair.getInput());
			System.out.println("--------------------------------");
			System.out.print(checkInput.toString());
			System.out.println("预测输出：");
			BasicMLData checkOutput = new BasicMLData(checkDataPair.getIdeal());
			System.out.print(checkOutput.toString());
			
			/*
			 * 执行算法
			 */
			BasicMLData output = new BasicMLData(algorithmModel.compute(checkInput));
			System.out.println("实际输出：");
			System.out.print(output.toString());
		}
	}
}
