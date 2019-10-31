package com.hanslv.test.machine.learning.encog.stock;

import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;

/**
 * 股票日期-成交量训练模型
 * 
 * 最大学习纪元为1000 * 3
 * 
 * 1、输入层设计
 * 输入样本集包括股票日期1个神经元
 * 设置偏置单元使决策线可从原点偏移
 * 2、隐藏层设计
 * 经过迭代实验，采用3隐藏层，每层10个神经元的学习效率最高
 * 设置偏置单元使决策线可从原点偏移
 * 考虑到输出为(-1,1)区间，因此采用常用的tanh函数为激活函数
 * 3、输出层设计
 * 输出仅需要当前股票的成交量，因此输出层采取单神经元结构
 * 考虑到输出为(-1,1)区间，因此采用常用的tanh函数为激活函数
 * 4、采用Levenberg Marquardt算法
 * ----------------------------
 * 输入层：
 * 神经元数量：1
 * 是否包含偏置单元：true
 * ----------------------------
 * 隐藏层：
 * 层数：2
 * 每层神经元数量：3
 * 是否包含偏置单元：true
 * 激活函数：tanh
 * ----------------------------
 * 输出层：
 * 神经元数量：1
 * 激活函数：tanh
 * ----------------------------
 * trainAlgorithm：粒子群算法（PSO）
 * ----------------------------
 * 
 * @author hanslv
 *
 */
public class DateVolumeNN {
	private static final int MAX_EPOCH = 1000 * 3;
	
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
		dataAndVolumeMode.addLayer(new BasicLayer(null , true , 1));
		
		/*
		 * 隐藏层结构
		 */
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationTANH() , true , 10));
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationTANH() , true , 10));
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationTANH() , true , 10));
		
		/*
		 * 输出层结构
		 */
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationTANH() , true , 1));
		
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
			if(epoch >= MAX_EPOCH) {
				trainAlgorithm.finishTraining();
				return null;
			}
			trainAlgorithm.iteration();
//			System.out.println("当前纪元：" + epoch + "误差为：" + trainAlgorithm.getError());
			epoch++;
		}while(trainAlgorithm.getError() > errorLimit);
		
		/*
		 * 训练结束
		 */
		trainAlgorithm.finishTraining();
		
		
		return dataAndVolumeMode;
	}
}
