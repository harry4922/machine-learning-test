package com.hanslv.test.machine.learning.dl4j.stock;

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
 * 股票LSTMBuilder
 * @author hanslv
 *
 */
public class TestStockLSTMBuilder {
	/**
	 * 构建神经网络
	 * @return
	 */
	public static MultiLayerNetwork build(int inputSize , int idealOutputSize) {
		/*
		 * 隐藏层
		 */
		
		/*
		 * 
		 * 	9.55,9.68,			9.65,9.45,			9.79,9.82,		9.73,9.80,			10.04,9.81
			10.68,11.57,		10.41,10.66,		10.48,10.44,	10.57,10.48,		10.50,10.45
			11.63,11.57,		11.61,11.62,		11.62,11.56,	11.72,11.61,		11.86,11.71
		 * 
		 * [[   10.0160,   10.0381,    9.8189,    9.9130,    9.6859,    9.8501,    9.6711,    9.6439,    9.7358,    9.7204], 
 			[   10.6002,   10.7106,   10.5451,   10.5820,   10.3949,   10.5133,   10.3559,   10.4083,   10.3398,   10.4385], 
 			[   11.3232,   11.4771,   11.3385,   11.3772,   11.2501,   11.2968,   11.2289,   11.2946,   11.1951,   11.2632]]
 			
 			 # of classes:    10
			 Accuracy:        0.3333
			 Precision:       0.2500	(8 classes excluded from average)
			 Recall:          0.5000	(8 classes excluded from average)
			 F1 Score:        0.6667	(9 classes excluded from average)
			 Precision, recall & F1: macro-averaged (equally weighted avg. of 10 classes)
		 */
//		FeedForwardLayer hideLayerA = new LSTM.Builder()
//				.nIn(inputSize)
//				.nOut(idealOutputSize)
//				.dropOut(0.2)
//				.activation(Activation.TANH).build();
//		FeedForwardLayer hideLayerB = new LSTM.Builder()
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH).build();
//		FeedForwardLayer hideLayerC = new LSTM.Builder()
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH).build();
//		/*
//		 * 输出层
//		 */
//		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		//new Nesterovs(9, 0.9)	new Adam(0.1 , 0.9 , 0.9999 , 1e-10)
////		return NNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.005) , hideLayerA , hideLayerB , hideLayerC , outputLayer);
//		return NNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , hideLayerC , outputLayer);
////		return NNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.01 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , hideLayerC , outputLayer);
		
		
		
//		FeedForwardLayer hideLayerA = new LSTM.Builder()
//				.nIn(inputSize)
//				.nOut(idealOutputSize)
//				.dropOut(0.2)
//				.activation(Activation.TANH)
//				.build();
//		FeedForwardLayer hideLayerB = new DenseLayer.Builder()
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		FeedForwardLayer hideLayerC = new LSTM.Builder()
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		FeedForwardLayer hideLayerD = new LSTM.Builder()
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		/*
//		 * 输出层
//		 */
//		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(new ActivationTanH()).build();
//		//new Nesterovs(9, 0.9)	new Adam(0.1 , 0.9 , 0.9999 , 1e-10)
//		return NNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , hideLayerC , hideLayerD , outputLayer);
		
		
		
		
		
		/*
		 * 只预测收盘价
		 * [[   10.0597,    9.8851,    9.8424,    9.6715,    9.6931], 
 			[   11.7321,   11.6285,   11.6083,   11.6166,   11.4861], 
 			[   11.6599,   11.5418,   11.5235,   11.5269,   11.3525]]

			Predictions labeled as 0 classified by model as 0: 1 times
			Predictions labeled as 3 classified by model as 3: 1 times
			Predictions labeled as 4 classified by model as 0: 1 times
			Warning: 3 classes were never predicted by the model and were excluded from average precision
			Classes excluded from average precision: [1, 2, 4]
			Warning: 2 classes were never predicted by the model and were excluded from average recall
			Classes excluded from average recall: [1, 2]
			
			==========================Scores========================================
			 # of classes:    5
			 Accuracy:        0.6667
			 Precision:       0.7500	(3 classes excluded from average)
			 Recall:          0.6667	(2 classes excluded from average)
			 F1 Score:        0.8333	(3 classes excluded from average)
			Precision, recall & F1: macro-averaged (equally weighted avg. of 5 classes)
			========================================================================
		 */
//		FeedForwardLayer hideLayerA = new LSTM.Builder()
//				.nIn(inputSize)
//				.nOut(idealOutputSize)
//				.dropOut(0.2)
//				.activation(Activation.TANH).build();
//		FeedForwardLayer hideLayerB = new LSTM.Builder()
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH).build();
////		FeedForwardLayer hideLayerC = new LSTM.Builder()
////				.nIn(idealOutputSize)
////				.nOut(idealOutputSize)
////				.activation(Activation.TANH).build();
//		/*
//		 * 输出层
//		 */
//		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		//new Nesterovs(9, 0.9)	new Adam(0.1 , 0.9 , 0.9999 , 1e-10)
//		return NNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , outputLayer);
		
		
		/*
		 * 	[[   10.0634,    9.9229,    9.8661,    9.7035,    9.8747], 
			 [   11.3485,   11.3156,   11.3831,   11.0588,   11.0496], 
			 [   11.8737,   11.8283,   11.8301,   11.8339,   11.7594]]
			
			Predictions labeled as 0 classified by model as 2: 1 times
			Predictions labeled as 3 classified by model as 4: 1 times
			Predictions labeled as 4 classified by model as 4: 1 times
			
			Warning: 3 classes were never predicted by the model and were excluded from average precision
			Classes excluded from average precision: [0, 1, 3]
			Warning: 2 classes were never predicted by the model and were excluded from average recall
			Classes excluded from average recall: [1, 2]
			
			==========================Scores========================================
			 # of classes:    5
			 Accuracy:        0.3333
			 Precision:       0.2500	(3 classes excluded from average)
			 Recall:          0.3333	(2 classes excluded from average)
			 F1 Score:        0.6667	(4 classes excluded from average)
			Precision, recall & F1: macro-averaged (equally weighted avg. of 5 classes)
			========================================================================
		 */
//		FeedForwardLayer hideLayerA = new LSTM.Builder()
//				.nIn(inputSize)
//				.nOut(idealOutputSize)
//				.dropOut(0.2)
//				.activation(Activation.TANH).build();
////		FeedForwardLayer hideLayerB = new LSTM.Builder()
////				.nIn(idealOutputSize)
////				.nOut(idealOutputSize)
////				.activation(Activation.TANH).build();
////		FeedForwardLayer hideLayerC = new LSTM.Builder()
////				.nIn(idealOutputSize)
////				.nOut(idealOutputSize)
////				.activation(Activation.TANH).build();
//		/*
//		 * 输出层
//		 */
//		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		//new Nesterovs(9, 0.9)	new Adam(0.1 , 0.9 , 0.9999 , 1e-10)
//		return NNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , outputLayer);
		
		
		/*
		 * 中位数
		 */
//		FeedForwardLayer hideLayerA = new LSTM.Builder()
//				.nIn(inputSize)
//				.nOut(idealOutputSize)
//				.dropOut(0.2)
//				.activation(Activation.TANH).build();
////		FeedForwardLayer hideLayerB = new LSTM.Builder()
////				.nIn(idealOutputSize)
////				.nOut(idealOutputSize)
////				.activation(Activation.TANH).build();
////		FeedForwardLayer hideLayerC = new LSTM.Builder()
////				.nIn(idealOutputSize)
////				.nOut(idealOutputSize)
////				.activation(Activation.TANH).build();
//		/*
//		 * 输出层
//		 */
//		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		//new Nesterovs(9, 0.9)	new Adam(0.1 , 0.9 , 0.9999 , 1e-10)
//		return NNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , outputLayer);
		
		
		
		/*
		 * 最高价、最低价
		 */
//		FeedForwardLayer hideLayerA = new LSTM.Builder()
//				.nIn(inputSize)
//				.nOut(idealOutputSize)
////				.dropOut(0.2)
//				.activation(Activation.TANH).build();
//		FeedForwardLayer hideLayerB = new LSTM.Builder()
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH).build();
//		FeedForwardLayer hideLayerC = new LSTM.Builder()
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH).build();
//		/*
//		 * 输出层
//		 */
//		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		//new Nesterovs(9, 0.9)	new Adam(0.1 , 0.9 , 0.9999 , 1e-10)
//		return NNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , hideLayerC , outputLayer);
		
//		FeedForwardLayer hideLayerA = new LSTM.Builder()
//				.nIn(inputSize)
//				.nOut(idealOutputSize)
//				.dropOut(0.2)
//				.activation(Activation.TANH)
//				.build();
//		FeedForwardLayer hideLayerB = new LSTM.Builder()
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		/*
//		 * 输出层
//		 */
//		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
//				.nIn(idealOutputSize)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		//new Nesterovs(9, 0.9)	new Adam(0.1 , 0.9 , 0.9999 , 1e-10)
//		return NNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , outputLayer);
		
		
		
		
		
		/**
		 * ！网络基本可用
		 * ------------------------------------------------
		 * 通过前5日成交量、开盘价、收盘价预测后5日最高价、最低价
		 * 用后5日的最高价、最低价作为前5日每天数据的idealOutput
		 * 
		 * 序列化器：NormalizerStandardize
		 * 
		 * 参数：
		 * int trainDataSize = 10;//训练数据大小
		 * int testDataSize = 1;//测试数据大小
		 * int idealOutputSize = 2;//输出源大小
		 * int inputSize = 3;//输入源大小
		 * String stockId = "1";//测试股票ID
		 * int epochSize = 20;//单次训练纪元
		 * 
		 * 理想数据指标：
		 * result.accuracy() == 1 && result.precision() == 1 && result.recall() == 1 && result.f1() == 1
		 * 输出值中最高价或最低价偏离区间较近的较为可信，可取最低价中的最高值附近作为买入值、最高价的最低值附近作为卖出值
		 * 应注意当当前价格高于预测最高价时，则考虑放弃，避免股价呈下降趋势
		 * ------------------------------------------------
		 */
		FeedForwardLayer hideLayerA = new LSTM.Builder()
				.nIn(inputSize)
				.nOut(idealOutputSize * 100)
				.activation(Activation.TANH).build();
		FeedForwardLayer hideLayerB = new LSTM.Builder()
				.nIn(idealOutputSize * 100)
				.nOut(idealOutputSize * 200)
				.dropOut(0.2)
				.activation(Activation.TANH).build();
		/*
		 * 输出层
		 */
		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
				.nIn(idealOutputSize * 200)
				.nOut(idealOutputSize)
				.activation(Activation.TANH)
				.build();
		return NNFactory.buildRNN(1000000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , outputLayer);
		
		
		
		
		
		
		
		
		
		/**
		 * ！网络基本可用
		 * ------------------------------------------------
		 * 通过前5日成交量、开盘价、收盘价预测后5日最高价、最低价
		 * 用后5日的最高价、最低价作为前5日每天数据的idealOutput
		 * 
		 * 序列化器：NormalizerMinMaxScaler
		 * 
		 * 参数：
		 * int trainDataSize = 10;//训练数据大小
		 * int testDataSize = 1;//测试数据大小
		 * int idealOutputSize = 2;//输出源大小
		 * int inputSize = 3;//输入源大小
		 * String stockId = "1";//测试股票ID
		 * int epochSize = 20;//单次训练纪元
		 * 
		 * 理想数据指标：
		 * result.accuracy() == 1 && result.precision() == 1 && result.recall() == 1 && result.f1() == 1
		 * 输出值中最高价或最低价偏离区间较近的较为可信，可取最低价中的最高值附近作为买入值、最高价的最低值附近作为卖出值
		 * 应注意当当前价格高于预测最高价时，则考虑放弃，避免股价呈下降趋势
		 * ------------------------------------------------
		 */
//		FeedForwardLayer hideLayerA = new LSTM.Builder()
//				.nIn(inputSize)
//				.nOut(idealOutputSize * 150)
//				.activation(Activation.TANH).build();
//		FeedForwardLayer hideLayerB = new LSTM.Builder()
//				.nIn(idealOutputSize * 150)
//				.nOut(idealOutputSize * 250)
//				.dropOut(0.2)
//				.activation(Activation.TANH).build();
//		/*
//		 * 输出层
//		 */
//		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
//				.nIn(idealOutputSize * 250)
//				.nOut(idealOutputSize)
//				.activation(Activation.TANH)
//				.build();
//		return NNFactory.buildRNN(1000000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , outputLayer);
	}
}
