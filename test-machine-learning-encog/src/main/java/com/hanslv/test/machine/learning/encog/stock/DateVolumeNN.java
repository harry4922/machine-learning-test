package com.hanslv.test.machine.learning.encog.stock;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;

/**
 * 股票日期-成交量训练模型
 * @author hanslv
 *
 */
public class DateVolumeNN {
//	private static final int MAX_EPOCH = 10000 * 20;
	
	/**
	 * 执行训练
	 * @param trainData 样本数据
	 * @param errorLimit 误差上限
	 * @return
	 */
	public static BasicNetwork train(MLDataSet trainData , double errorLimit) {
		/*
		 * 实例化神经网络对象
		 */
		BasicNetwork dataAndVolumeMode = new BasicNetwork();
		
		/*
		 * 输入层结构
		 */
		dataAndVolumeMode.addLayer(new BasicLayer(null , true , 2));
		
		/*
		 * 隐藏层结构
		 */
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 7));
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 7));
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 7));
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 7));
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 7));
//		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 7));
//		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 10));
//		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 10));
//		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 10));
//		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 10));
//		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 10));
//		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 10));
		
		/*
		 * 输出层结构
		 */
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 2));
		
		/*
		 * 结束神经网络构建
		 */
		dataAndVolumeMode.getStructure().finalizeStructure();
		
		/*
		 * 重置连接权重、偏置单元
		 */
		dataAndVolumeMode.reset();
		
		/*
		 * Levenberg Marquardt算法
		 */
		final LevenbergMarquardtTraining trainAlgorithm = new LevenbergMarquardtTraining(dataAndVolumeMode , trainData);
		
		/*
		 * 迭代纪元
		 */
		int epoch = 0;
		
		/*
		 * 执行迭代训练
		 */
		do {
//			if(epoch >= MAX_EPOCH) {
//				trainAlgorithm.finishTraining();
//				return null;
//			}
			trainAlgorithm.iteration();
			System.out.println("当前纪元：" + epoch + "误差为：" + trainAlgorithm.getError());
			epoch++;
		}while(trainAlgorithm.getError() > errorLimit);
		
		/*
		 * 训练结束
		 */
		trainAlgorithm.finishTraining();
		
		
		return dataAndVolumeMode;
	}
}
