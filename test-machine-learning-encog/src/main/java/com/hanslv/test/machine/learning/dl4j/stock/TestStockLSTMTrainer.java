package com.hanslv.test.machine.learning.dl4j.stock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.joda.time.LocalDate;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;

import com.hanslv.test.machine.learning.encog.util.DbUtil;
import com.hanslv.test.machine.learning.encog.util.SourceDataParser;

/**
 * 训练
 * @author hanslv
 *
 */
public class TestStockLSTMTrainer {
	/**
	 * 训练LSTM股票模型
	 * @param stockId 股票ID
	 * @param trainDataEndDate 训练开始时间
	 * @param trainDataSize 训练数据长度
	 * @param testDataSize 测试数据长度
	 * @param idealOutputSize 输出神经元数量
	 * @param epoch 训练纪元
	 * @return
	 */
	public static Evaluation train(String stockId , String trainDataEndDate , int trainDataSize , int testDataSize , int inputSize , int idealOutputSize , int epoch) {
		/*
		 * 实例化图形界面
		 */
//		UIServer uiServer = UIServer.getInstance();
//		StatsStorage statsStorage = new InMemoryStatsStorage();
//		uiServer.attach(statsStorage);
		
		/*
		 * 获取训练数据和测试数据
		 */
		List<String> mainDataList = DbUtil.deeplearning4jData(stockId , trainDataEndDate , trainDataSize + testDataSize);
		
		/*
		 * 判断数据量是否符合标准
		 */
		if(mainDataList == null) {
			System.err.println("数据集小于预期");
			return null;
		}
		
		List<String> trainDataList = mainDataList.subList(0 , trainDataSize * 5);
		List<String> testDataList = mainDataList.subList(trainDataSize * 5 , mainDataList.size());
		
//		List<String> testDataList = mainDataList.subList(0 , testDataSize);
//		List<String> trainDataList = mainDataList.subList(testDataSize , mainDataList.size());
		
		/*
		 * Test训练数据集合
		 */
//		System.out.println(trainDataList.size());
//		System.out.println("---------------------------------------------");
//		System.out.println(testDataList.size());
//		for(String trainData : trainDataList) System.out.println(trainData);
//		System.out.println("----------------------------------------");
//		for(String testData : testDataList) System.out.println(testData);
//		for(String trainData : trainDataList) System.out.println(trainData.split(",").length);
//		System.out.println("----------------------------------------");
//		for(String testData : testDataList) System.out.println(testData.split(",").length);
//		return null;
		
		
		
		
		System.out.println("实际值：");
		for(String testData : testDataList) {
			String[] datas = testData.split(",");
//			for(int i = inputSize * 2 ; i < datas.length ; i++) {
			for(int i = inputSize ; i < datas.length ; i++) {
				System.out.print("    " + datas[i] + ",");
			}
			System.out.println();
		}
////		return null;
		
		/*
		 * 标准化训练数据集、测试数据集
		 */
//		List<DataSetIterator> iteratorList = SourceDataParser.dl4jDataNormalizer(trainDataList , testDataList, idealOutputSize * 2);
		List<DataSetIterator> iteratorList = SourceDataParser.dl4jDataNormalizer(trainDataList , testDataList , idealOutputSize);
		
		/*
		 * Test标准化后的数据集合
		 */
//		while(iteratorList.get(0).hasNext()) System.out.println(iteratorList.get(0).next());
//		System.out.println("-----------------------------------------");
//		while(iteratorList.get(1).hasNext()) System.out.println(iteratorList.get(1).next());
//		return null;
		
		/*
		 * 获取神经网络模型
		 */
//		MultiLayerNetwork lstmNetwork = TestStockLSTMBuilder.build(inputSize * 2 , idealOutputSize * 2);
		MultiLayerNetwork lstmNetwork = TestStockLSTMBuilder.build(inputSize , idealOutputSize);
		
		/*
		 * 给当前网络配置UI界面
		 */
//		lstmNetwork.setListeners(new StatsListener(statsStorage));
		
		/*
		 * 训练模型epoch次
		 */
		for(int i = 0 ; i < epoch ; i++) {
			lstmNetwork.fit(iteratorList.get(0));
			lstmNetwork.rnnClearPreviousState();
		}
		
		/*
		 * Test预测
		 */
		System.out.println("预测值：");
//		NormalizerStandardize normalizerStandardize = (NormalizerStandardize) iteratorList.get(1).getPreProcessor();
		NormalizerMinMaxScaler normalizerStandardize = (NormalizerMinMaxScaler) iteratorList.get(1).getPreProcessor();
		
		/*
		 * 测试数值标准化
		 */
//		DataSet testData = iteratorList.get(1).next();
//		normalizerStandardize.revert(testData);
//		System.out.println(testData);
//		System.out.println("-------------------------------------");
		
		Evaluation result = lstmNetwork.evaluate(iteratorList.get(1));
		iteratorList.get(1).reset();
		
		/*
		 * 批量预测
		 */
		DataSet input = iteratorList.get(1).next();
		INDArray output = lstmNetwork.rnnTimeStep(input.getFeatures());
		DataSet resultDataSet = new DataSet(input.getFeatures() , output);
		normalizerStandardize.revert(resultDataSet);
		System.out.println(resultDataSet.getLabels());
		
		/*
		 * 按前一测试值预测
		 */
////		List<DataSet> testDataSet = iteratorList.get(1).next().batchBy(1);
////		for(DataSet testData : testDataSet) {
////			INDArray input = testData.getFeatures();
////			INDArray output = lstmNetwork.rnnTimeStep(input);
////			DataSet resultDataSet = new DataSet(input , output);
////			normalizerStandardize.revert(resultDataSet);
////			System.out.println(resultDataSet.getLabels());
//////			System.out.println(resultDataSet);
////		}
//		
//		
//		/*
//		 * 按前一天预测的值预测
//		 */
////		INDArray input = iteratorList.get(1).next().batchBy(1).get(0).getFeatures();
////		for(int i = 0 ; i < testDataSize ; i++) {
////			INDArray output = lstmNetwork.rnnTimeStep(input);
////			DataSet result = new DataSet(input , output);
////			normalizerStandardize.revert(result);
////			System.out.println(result.getLabels());
////			input = output;
////		}
//		
//		/*
//		 * 返回评估结果
//		 */
////		lstmNetwork.setListeners(new EvaluativeListener(iteratorList.get(1) , 1));
		return result;
	}
	
	
	public static void main(String[] args) {
		LocalDate currentDate = LocalDate.now();
		int stockIdMax = 3555;
		
		/*
		 * 写入文件
		 */
		String filePath = "D:\\data\\mine\\test-data\\2019-11-29\\stockNN.txt";
		File file = new File(filePath);
		if(file.exists()) file.delete();
		try {
			file.createNewFile();
		} catch (IOException e1) {e1.printStackTrace();}
		
		
		try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
			System.setOut(new PrintStream(fileOutputStream , true));
			
			for(int i = 1 ; i <= stockIdMax ; i ++) {
				String stockId = i + "";//股票ID
//				String stockId = "1";//股票ID
				int trainDataSize = 10;//训练数据大小
				int testDataSize = 1;//测试数据大小
				int idealOutputSize = 2;//输出源大小
				int inputSize = 3;//输入源大小
				int epochSize = 20;//单次训练纪元
				int trainTimes = 100;//实验次数
				for(int j = 0 ; j < trainTimes ; j++) {
					String trainDate = currentDate.minusDays(j * trainDataSize * idealOutputSize).toString();//训练日期
					Evaluation result = train(
							stockId , 
							trainDate ,
							trainDataSize , 
							testDataSize , 
							inputSize , 
							idealOutputSize , 
							epochSize);
					if(result == null) continue;
					if(
							result.accuracy() == 1 
							&& result.precision() == 1 
							&& result.recall() == 1 
							&& result.f1() == 1 
//							&& result.averageF1NumClassesExcluded() == 0 
//							&& result.averageFBetaNumClassesExcluded() == 0 
//							&& result.averagePrecisionNumClassesExcluded() == 0 
//							&& result.averageRecallNumClassesExcluded() == 0
							) {
						System.out.println("stockId=" + stockId + "," + "trainDate=" + trainDate);
//						System.out.println(result.stats());
					}
					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
//		train(
//		stockId , 
//		"2019-06-01" ,
//		trainDataSize , 
//		testDataSize , 
//		inputSize , 
//		idealOutputSize , 
//		epochSize);
	}
}
