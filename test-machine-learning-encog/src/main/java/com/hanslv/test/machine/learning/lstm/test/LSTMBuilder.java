package com.hanslv.test.machine.learning.lstm.test;

import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.hanslv.test.machine.learning.dl4j.rnn.NNFactory;

/**
 * 构建LSTM神经网络
 * @author hanslv
 *
 */
public class LSTMBuilder {
	public static MultiLayerNetwork build(int inputSize , int idealOutputSize) {
		FeedForwardLayer hideLayerA = new GravesLSTM.Builder()
				.nIn(inputSize)
				.nOut(150)
				.activation(Activation.SOFTSIGN)
				.build();
		FeedForwardLayer hideLayerB = new GravesLSTM.Builder()
				.nOut(150)
				.activation(Activation.SOFTSIGN)
				.build();
		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
				.nOut(idealOutputSize)
				.activation(Activation.IDENTITY)
				.build();
//		return NNFactory.buildRNN(12345 , WeightInit.XAVIER , new RmsProp(0.01) , hideLayerA , hideLayerB , outputLayer);
		return NNFactory.buildRNN(12345 , WeightInit.XAVIER , new Adam(0.01) , hideLayerA , hideLayerB , outputLayer);
//		return NNFactory.buildRNN(12345 , WeightInit.XAVIER , new RmsProp(0.01) , hideLayerA , hideLayerB , hideLayerC , outputLayer);
	}
}
