package com.hanslv.test.machine.learning.encog.test;

import java.util.List;

import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;

import com.hanslv.test.machine.learning.encog.stock.DateVolumeNN;
import com.hanslv.test.machine.learning.encog.util.DbUtil;
import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

public class TestDateVolumeNN {
	public static void main(String[] args) {
//		loop1:for(int j = 0 ; j <= 3555 ; j++) {
//		loop1:for(int j = 1 ; j < 2 ; j++) {
		for(int j = 1 ; j < 2 ; j++) {
			String startDate = "1999-11-10";
			String stockId = j + "";
	//		String titles = "year,month,day,stockPriceVolume";
			String titles = "year,month,day,stockPriceVolume";
			int trainDataSize = 365;
			int checkDataSize = 5;
			
			/*
			 * 获取股票数据
			 */
			List<String> mainDataList = DbUtil.getDataAndVolumeMap(stockId, startDate, trainDataSize + checkDataSize);
			
			if(mainDataList.size() < trainDataSize + checkDataSize) {
				System.err.println("数据样本集小于预期");
				continue;
			}
	
			/*
			 * 算法训练数据
			 */
			MLDataSet mainData = SourceDataParser.dataAnalyze(mainDataList , titles.split(",") , 1 , 1 , 0);
			MLDataSet trainData = new BasicMLDataSet();
			MLDataSet checkData = new BasicMLDataSet();
			
			for(int i = 0 ; i < mainData.size() ; i++) {
				if(i < trainDataSize) trainData.add(mainData.get(i));
				else checkData.add(mainData.get(i));
			}
			
			/*
			 * 5天验证数据
			 */
	//		String dateStr = daysData20.get(daysData20.size() - 1);
	//		Integer year = Integer.parseInt(dateStr.substring(0 , 4));
	//		Integer month = Integer.parseInt(dateStr.substring(4 , 6));
	//		Integer day = Integer.parseInt(dateStr.substring(6 , 8));
	//		String nextDate = LocalDate.of(year , month , day).plusDays(1).toString();
	//		List<String> daysData5 = DbUtil.getDataAndVolumeMap(stockId , nextDate , 5);
	//		MLDataSet checkData = SourceDataParser.dataAnalyze(daysData5 , titles.split(",") , 1 , 0.9 , -0.9);
			
			/*
			 * 训练算法
			 */
			BasicNetwork algorithmModel = DateVolumeNN.train(trainData , 0.0005);
			
			/*
			 * 收敛失败
			 */
			if(algorithmModel == null) {
				System.err.println("---收敛失败");
				Encog.getInstance().shutdown();
				continue;
			}
//			else System.out.println("收敛成功！" + stockId);
			
			
			/*
			 * 输出结果
			 */
			for(MLDataPair checkDataPair : checkData) {
				BasicMLData checkInput = new BasicMLData(checkDataPair.getInput());
				System.out.println("--------------------------------");
//				System.out.println("输入：");
//				System.out.println(checkInput.toString());
				System.out.println("预测输出：");
				
				BasicMLData checkOutput = new BasicMLData(checkDataPair.getIdeal());
				
				System.out.println(checkOutput.toString());
				
				/*
				 * 执行算法
				 */
				BasicMLData output = new BasicMLData(algorithmModel.compute(checkInput));
				
				System.out.println("实际输出：");
				System.out.println(output.toString());
				
//				if(SourceDataParser.check(checkOutput , output , 0.1)) {
//					Encog.getInstance().shutdown();
//					System.err.println("-----预测失败");
//					continue loop1;
//				}
			}
//			System.out.println("预测成功！" + stockId);
			Encog.getInstance().shutdown();
		}
	}
}
