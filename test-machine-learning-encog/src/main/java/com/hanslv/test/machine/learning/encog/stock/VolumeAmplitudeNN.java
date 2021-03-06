package com.hanslv.test.machine.learning.encog.stock;

import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.pso.NeuralPSO;

/**
 * 成交量-振幅训练模型
 * 
 * 1、输入层设计
 * 输入样本集仅包括股票成交量一个因子因此输入层采用单神经元结构
 * 设置偏置单元使决策线可从原点偏移
 * 2、隐藏层设计
 * 由于输入层神经元数量为1，根据设计合理性推断隐藏层各层神经元数量为2n+1=3，因此每层设置3个神经元
 * 根据测试逐渐调整隐藏层层数得出计算时间和计算精度的最佳组合，结果为2
 * 设置偏置单元使决策线可从原点偏移
 * 考虑到输出为(-1,1)区间，因此采用常用的tanh函数为激活函数
 * 3、输出层设计
 * 输出仅需要当前股票的振幅，因此输出层采取单神经元结构
 * 考虑到输出为(-1,1)区间，因此采用常用的tanh函数为激活函数
 * 4、为了加快迭代速度，采取粒子群算法
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
public class VolumeAmplitudeNN {
	/**
	 * 执行训练
	 * @param input 样本数据（输入）
	 * @param idealOutput 样本数据（预测输出）
	 * @param errorLimit 误差上限
	 * @return
	 */
	public static BasicNetwork train(double[][] input , double[][] idealOutput , double errorLimit) {
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
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationTANH() , true , 3));
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationTANH() , true , 3));
		
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
		 * 样本数据对象
		 */
		MLDataSet trainData = new BasicMLDataSet(input , idealOutput);
		
		/*
		 * 粒子群算法
		 */
		final NeuralPSO trainAlgorithm = new NeuralPSO(dataAndVolumeMode , trainData);
		
		/*
		 * 迭代纪元
		 */
		int epoch = 0;
		
		/*
		 * 执行迭代训练
		 */
		do {
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
