package com.hanslv.test.machine.learning.lstm.test;

import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.learning.config.Adam;

import com.hanslv.test.machine.learning.dl4j.rnn.NNFactory;

/**
 * 构建LSTM神经网络
 * @author hanslv
 *
 */
public class LSTMBuilder {
	
	/**
	 * 构建神经网络
	 * @param inputSize
	 * @param idealOutputSize
	 * @return
	 */
	public static MultiLayerNetwork build(int inputSize , int idealOutputSize) {
		FeedForwardLayer hideLayerA = new DenseLayer.Builder()
				.nIn(inputSize)
				.nOut(idealOutputSize * 2)
				.activation(Activation.SIGMOID)
				.build();
		FeedForwardLayer hideLayerB = new DenseLayer.Builder()
				.nIn(idealOutputSize * 2)
				.nOut(idealOutputSize * 2)
				.activation(Activation.SIGMOID)
				.build();
		
		FeedForwardLayer outputLayer = new OutputLayer.Builder()
				.nIn(idealOutputSize * 2)
				.nOut(idealOutputSize)
				.activation(Activation.SIGMOID)
				.build();
		
		return NNFactory.buildRNN(1000000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , outputLayer);
//		return NNFactory.buildRNN(1000000 , WeightInit.XAVIER , new AdaGrad() , hideLayerA , outputLayer);
	}
}
