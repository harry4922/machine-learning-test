package com.hanslv.test.machine.learning.dl4j.stock.rectangle;

import java.util.Map;

import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
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
		FeedForwardLayer hideLayerA = new LSTM.Builder()
				.nIn(hideLayerParams.get(INPUT_SIZE))
				.nOut(hideLayerParams.get(IDEALOUTPUT_SIZE) * hideLayerParams.get(HIDELAYERA_RIGHT))
				.activation(activation)
				.build();
		FeedForwardLayer hideLayerB = new LSTM.Builder()
				.nIn(hideLayerParams.get(IDEALOUTPUT_SIZE) * hideLayerParams.get(HIDELAYERA_RIGHT))
				.nOut(hideLayerParams.get(IDEALOUTPUT_SIZE) * hideLayerParams.get(HIDELAYERB_RIGHT))
				.activation(activation)
				.build();
		FeedForwardLayer hideLayerC = new DenseLayer.Builder()
				.nIn(hideLayerParams.get(IDEALOUTPUT_SIZE) * hideLayerParams.get(HIDELAYERB_RIGHT))
				.nOut(hideLayerParams.get(IDEALOUTPUT_SIZE) * hideLayerParams.get(HIDELAYERC_RIGHT))
				.activation(activation)
				.build();
		/*
		 * 输出层
		 */
		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
				.nIn(hideLayerParams.get(IDEALOUTPUT_SIZE) * hideLayerParams.get(HIDELAYERC_RIGHT))
				.nOut(hideLayerParams.get(IDEALOUTPUT_SIZE))
				.activation(activation)
				.dropOut(dropOut)
				.build();
		return NNFactory.buildRNN(1000000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , hideLayerC , outputLayer);
	}
}
