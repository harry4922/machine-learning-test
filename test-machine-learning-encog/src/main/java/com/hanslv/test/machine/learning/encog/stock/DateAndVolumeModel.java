package com.hanslv.test.machine.learning.encog.stock;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.pso.NeuralPSO;

/**
 * 股票日期-成交量训练模型
 * @author hanslv
 *
 */
public class DateAndVolumeModel {
	public static BasicNetwork getDataAndVolumeMode(double[][] input , double[][] idealOutput , double errorLimit) {
		BasicNetwork dataAndVolumeMode = new BasicNetwork();
		dataAndVolumeMode.addLayer(new BasicLayer(null , true , 3));
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 3));
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 3));
		dataAndVolumeMode.addLayer(new BasicLayer(new ActivationSigmoid() , true , 1));
		dataAndVolumeMode.getStructure().finalizeStructure();
		dataAndVolumeMode.reset();
		
		MLDataSet trainData = new BasicMLDataSet(input , idealOutput);
		
		final NeuralPSO trainAlgorithm = new NeuralPSO(dataAndVolumeMode , trainData);
		
		int epoch = 0;
		
		do {
			trainAlgorithm.iteration();
			System.out.println("当前纪元：" + epoch + "误差为：" + trainAlgorithm.getError());
			epoch++;
		}while(trainAlgorithm.getError() > errorLimit);
		
		/*
		 * 结束训练
		 */
		trainAlgorithm.finishTraining();
		
		return dataAndVolumeMode;
	}
}
