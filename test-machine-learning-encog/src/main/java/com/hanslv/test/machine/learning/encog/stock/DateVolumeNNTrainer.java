package com.hanslv.test.machine.learning.encog.stock;

import java.util.List;

import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;

import com.hanslv.test.machine.learning.encog.util.DbUtil;
import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

/**
 * 训练股票日期-成交量模型
 * 
 * @author hanslv
 *
 */
public class DateVolumeNNTrainer {
	/**
	 * 训练模型
	 * 
	 * @param stockId 股票ID
	 * @param startDate 起始时间
	 * @param checkDataSize 校验数据大小
	 * @param limit 精度
	 * @return
	 */
	public static boolean trainNN(String stockId , String startDate , int checkDataSize , double limit) {
		String titles = "date,stockPriceVolume";
		String algorithmFileSuffix = "date_volume.eg";
		String basePath = "test/";
		String algorithmFilePath = basePath + stockId + "_" + algorithmFileSuffix;
		
		/*
		 * 初始训练时间为200天，每次迭代增加50天直到当前ID的全部数据用完或找出最佳模型
		 */
		loop1:for(int trainDataSize = 200 ; trainDataSize < Integer.MAX_VALUE ; trainDataSize = trainDataSize + 50) {
			
			
			/*
			 * 获取股票数据
			 */
			List<String> mainDataList = DbUtil.getDataAndVolumeMap(stockId, startDate, trainDataSize + checkDataSize);
			
			/*
			 * 数据已经全部用完，没有找到合适模型
			 */
			if(mainDataList.size() < trainDataSize + checkDataSize) {
				System.err.println("数据样本集小于预期");
				return false;
			}
	
			/*
			 * 算法训练数据
			 */
			MLDataSet mainData = SourceDataParser.dataAnalyze(mainDataList , titles.split(",") , 1 , 0.9 , -0.9);
			MLDataSet trainData = new BasicMLDataSet();
			MLDataSet checkData = new BasicMLDataSet();
			
			/*
			 * 将样本拆分为训练数据和对比数据
			 */
			for(int i = 0 ; i < mainData.size() ; i++) {
				if(i < trainDataSize) trainData.add(mainData.get(i));
				else checkData.add(mainData.get(i));
			}
			
			/*
			 * 训练算法
			 */
			BasicNetwork algorithmModel = DateVolumeNN.train(trainData , limit);
			
			/*
			 * 收敛失败
			 */
			if(algorithmModel == null) {
				System.err.println("---收敛失败");
				Encog.getInstance().shutdown();
				continue;
			}
			
			/*
			 * 判断结果是否符合预期
			 */
			for(MLDataPair checkDataPair : checkData) {
				BasicMLData checkInput = new BasicMLData(checkDataPair.getInput());
				BasicMLData checkOutput = new BasicMLData(checkDataPair.getIdeal());
				
				/*
				 * 执行算法
				 */
				BasicMLData output = new BasicMLData(algorithmModel.compute(checkInput));
				
				/*
				 * 预测失败，增加天数后重新计算
				 */
				if(SourceDataParser.check(checkOutput , output , 0.1)) {
					Encog.getInstance().shutdown();
					System.err.println("-----预测失败");
					continue loop1;
				}
			}
			
			/*
			 * 预测成功保存算法到文件
			 */
			SourceDataParser.saveAlgorithm(algorithmFilePath , algorithmModel);
			System.out.println("预测成功！" + stockId);
			Encog.getInstance().shutdown();
			return true;
		}
		return false;
	}
}
