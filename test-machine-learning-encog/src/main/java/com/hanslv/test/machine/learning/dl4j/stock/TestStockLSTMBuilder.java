package com.hanslv.test.machine.learning.dl4j.stock;

import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.impl.ActivationTanH;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.hanslv.test.machine.learning.dl4j.rnn.NNFactory;

/**
 * 股票LSTMBuilder
 * @author hanslv
 *
 */
public class TestStockLSTMBuilder {
	/**
	 * 构建神经网络
	 * @return
	 */
	public static MultiLayerNetwork build() {
		/*
		 * 隐藏层
		 */
		FeedForwardLayer hideLayerA = new LSTM.Builder()
				.nIn(2)
				.nOut(4)
				.activation(new ActivationTanH()).build();
		FeedForwardLayer hideLayerB = new LSTM.Builder()
				.nIn(4)
				.nOut(4)
				.activation(new ActivationTanH()).build();
		
		/*
		 * 输出层
		 */
		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
				.nIn(4)
				.nOut(2)
				.activation(new ActivationTanH()).build();
		//new Nesterovs(9, 0.9)	new Adam(0.1 , 0.9 , 0.9999 , 1e-10)
		return NNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , outputLayer);
	}
}
