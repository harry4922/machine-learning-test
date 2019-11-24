package com.hanslv.test.machine.learning.dl4j.rnn;

import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.impl.ActivationIdentity;
import org.nd4j.linalg.activations.impl.ActivationTanH;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * 构建RNNTest神经网络
 * @author hanslv
 *
 */
public class RNNTestBuilder {
	/**
	 * 构建RNNTest网络
	 */
	public static MultiLayerNetwork build() {
		/*
		 * 隐藏层
		 */
		FeedForwardLayer hiddenLayerA = new LSTM.Builder()
				.nIn(2)
				.nOut(2)
				.activation(new ActivationTanH())
				.build();
		
//		FeedForwardLayer hiddenLayerB = new LSTM.Builder()
//				.nIn(1)
//				.nOut(1)
//				.activation(new ActivationTanH())
//				.build();
		
		/*
		 * 输出层
		 */
		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
				.nIn(2)
				.nOut(1)
				.activation(new ActivationIdentity())
				.build();
		
		return NNFactory.buildRNN(1234 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 1e-10) , hiddenLayerA , outputLayer);
	}
}
