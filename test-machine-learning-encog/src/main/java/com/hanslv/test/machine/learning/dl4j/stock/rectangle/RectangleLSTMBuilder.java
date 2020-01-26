package com.hanslv.test.machine.learning.dl4j.stock.rectangle;

import java.util.Map;

import org.deeplearning4j.nn.conf.layers.DenseLayer;
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
 * 矩形预测LSTM网络构建
 * @author hanslv
 *
 */
public class RectangleLSTMBuilder {
	public static final String INPUT_SIZE = "inputSize";
	public static final String IDEALOUTPUT_SIZE = "idealOutputSize";
	public static final String HIDELAYERA_RIGHT = "hidelayerARight";
	public static final String HIDELAYERB_RIGHT = "hidelayerBRight";
	public static final String HIDELAYERC_RIGHT = "hidelayerCRight";
	
	/**
	 * 构建神经网络
	 * @param hideLayerParams 需要包含上方参数
	 * @param activation
	 * @param dropOut
	 * @return
	 */
	public static MultiLayerNetwork build(Map<String , Integer> hideLayerParams , Activation activation , double dropOut) {
		FeedForwardLayer hideLayerA = new GravesLSTM.Builder()
				.nIn(hideLayerParams.get(INPUT_SIZE))
				.nOut(hideLayerParams.get(HIDELAYERA_RIGHT))
//				.activation(activation)
//				.dropOut(dropOut)
				.build();
//		FeedForwardLayer hideLayerB = new GravesLSTM.Builder()
//				.nOut(hideLayerParams.get(HIDELAYERB_RIGHT))
//				.activation(activation)
//				.build();
//		FeedForwardLayer hideLayerC = new DenseLayer.Builder()
//				.nOut(hideLayerParams.get(HIDELAYERC_RIGHT))
//				.activation(activation)
//				.build();
		
		/*
		 * 输出层
		 */
		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
				.nOut(hideLayerParams.get(IDEALOUTPUT_SIZE))
				.activation(Activation.IDENTITY)
				.build();
//		return NNFactory.buildRNN(1234567 , WeightInit.XAVIER , new Adam(0.01 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , hideLayerC , outputLayer);
//		return NNFactory.buildRNN(1234567 , WeightInit.XAVIER , new Adam(0.01) , hideLayerA , hideLayerB , hideLayerC , outputLayer);
//		return NNFactory.buildRNN(1234567 , WeightInit.XAVIER , new Adam(0.01) , hideLayerA , hideLayerB , outputLayer);
		return NNFactory.buildRNN(1234567 , WeightInit.XAVIER , new Adam(0.01) , hideLayerA , outputLayer);
//		return NNFactory.buildRNN(1234 , WeightInit.XAVIER , new Adam(0.01) , hideLayerA , hideLayerC , outputLayer);
	}
}
