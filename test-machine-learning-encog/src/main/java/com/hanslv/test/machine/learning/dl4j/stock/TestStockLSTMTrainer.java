package com.hanslv.test.machine.learning.dl4j.stock;

import java.util.List;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;

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
	public static Evaluation train(String stockId , String trainDataEndDate , int trainDataSize , int testDataSize , int idealOutputSize , int epoch) {
		/*
		 * 获取训练数据和测试数据
		 */
		List<String> mainDataList = DbUtil.deeplearning4jData(stockId, trainDataEndDate , trainDataSize + testDataSize + 1);
		
		/*
		 * 判断数据量是否符合标准
		 */
		if(mainDataList.size() < (trainDataSize + testDataSize)) {
			System.err.println("数据集小于预期");
			return null;
		}
		
		List<String> trainDataList = mainDataList.subList(0 , trainDataSize);
		List<String> testDataList = mainDataList.subList(trainDataSize , mainDataList.size());
		
		/*
		 * Test训练数据集合
		 */
//		for(String trainData : trainDataList) System.out.println(trainData);
//		System.out.println("----------------------------------------");
//		for(String testData : testDataList) System.out.println(testData);
//		return null;
		
		/*
		 * 标准化训练数据集、测试数据集
		 */
		List<DataSetIterator> iteratorList = SourceDataParser.dl4jDataNormalizer(trainDataList , testDataList, idealOutputSize);
		
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
		MultiLayerNetwork lstmNetwork = TestStockLSTMBuilder.build();
		
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
		NormalizerStandardize normalizerStandardize = (NormalizerStandardize) iteratorList.get(1).getPreProcessor();
		while(iteratorList.get(1).hasNext()) {
			INDArray input = iteratorList.get(1).next().getFeatures();
			INDArray output = lstmNetwork.rnnTimeStep(input);
			DataSet resultDataSet = new DataSet(input , output);
			normalizerStandardize.revert(resultDataSet);
			System.out.println(resultDataSet.getLabels());
			
//			DataSet testData = iteratorList.get(1).next();
//			normalizerStandardize.revert(testData);
//			System.out.println(testData);
			System.out.println("-------------------------------------");
		}
		iteratorList.get(1).reset();
		
		/*
		 * 返回评估结果
		 */
		return lstmNetwork.evaluate(iteratorList.get(1));
	}
	
	
	public static void main(String[] args) {
		/*
		 * 计算50个纪元
		 */
		Evaluation result = train("980" , "2019-11-24" , 89 , 5 , 2 , 100);
		if(result != null) {
			System.out.println("accuracy：" + result.accuracy());
			System.out.println(result.f1());
			System.out.println(result.stats());
		}
	}
}
