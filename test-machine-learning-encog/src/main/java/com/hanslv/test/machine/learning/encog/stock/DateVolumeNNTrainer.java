package com.hanslv.test.machine.learning.encog.stock;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.arrayutil.NormalizedField;

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
	public static void trainNN(String stockId , String startDate , int checkDataSize , double limit , double checkLimit) {
		String titles = "month,day,stockPricestart,stockPriceend";
		int trainDataSize = 31;
		/*
		 * 获取股票数据
		 */
		List<String> mainDataList = DbUtil.getDataAndVolumeMap(stockId, startDate, trainDataSize + checkDataSize);
		
		/*
		 * 数据已经全部用完，没有找到合适模型
		 */
		if(mainDataList.size() < trainDataSize + checkDataSize) {
			System.err.println("数据样本集小于预期");
			return;
		}
	
		/*
		 * 算法训练数据
		 */
		Map<Map<String , NormalizedField> , MLDataSet> analyzedResult = SourceDataParser.dataAnalyze(mainDataList , titles.split(",") , 2 , 1 , 0);
		Entry<Map<String , NormalizedField> , MLDataSet> analyzedResultEntry = analyzedResult.entrySet().iterator().next();
		Map<String , NormalizedField> deNormalizedMap = analyzedResultEntry.getKey();
		MLDataSet mainData = analyzedResultEntry.getValue();
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
		 * 判断结果是否符合预期
		 */
		for(MLDataPair checkDataPair : checkData) {
			BasicMLData checkInput = new BasicMLData(checkDataPair.getInput());
			BasicMLData checkOutput = new BasicMLData(checkDataPair.getIdeal());
			
			/*
			 * 执行算法
			 */
			BasicMLData output = new BasicMLData(algorithmModel.compute(checkInput));
			
			NormalizedField endPriceNormalizer = deNormalizedMap.get("stockPriceend");
			System.out.println("--------------------------");
			System.out.println(checkOutput.getData(1) + "实际：" + endPriceNormalizer.deNormalize(checkOutput.getData(1)));
			System.out.println(output.getData(1) + "预测：" + endPriceNormalizer.deNormalize(output.getData(1)));
//			System.out.println(output.getData(1));
			System.out.println("++++++++++++++++++++++++++++");
			System.out.println(checkOutput.getData(0) + "实际：" + endPriceNormalizer.deNormalize(checkOutput.getData(0)));
			System.out.println(output.getData(0) + "预测：" + endPriceNormalizer.deNormalize(output.getData(0)));
			
		}		
	}
}
