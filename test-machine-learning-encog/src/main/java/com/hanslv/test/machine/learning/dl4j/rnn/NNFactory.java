package com.hanslv.test.machine.learning.dl4j.rnn;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.learning.config.IUpdater;

/**
 * 尝试搭建RNN模型
 * @author hanslv
 *
 */
public class NNFactory {
	private static UIServer uiServer;
	
	/**
	 * 搭建神经网络
	 * @param weightSeed 随机权重种子
	 * @param weightInit 初始化方式
	 * @param updater 梯度更新方式
	 * @param layers 各层神经网络，需要按顺序
	 * @return
	 */
	public static MultiLayerNetwork buildRNN(long weightSeed , WeightInit weightInit , IUpdater updater , FeedForwardLayer ... layers) {
		/*
		 * 初始化神经网络配置
		 */
        ListBuilder configBuilder = new NeuralNetConfiguration.Builder()
        		/*
        		 * 随机权重种子，保证每次运算的结果相同
        		 */
                .seed(weightSeed)
                /*
                 * 设置梯度下降方式
                 */
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                /*
                 * 偏置向量初始化
                 */
                .biasInit(0)
                /*
                 * 神经网络初始化方式(当前选择XAVIER：使每层输出的方差尽量相等)
                 * XAVIER能够很好的适用于TANH激活函数，但是当使用ReLU时建议将初始化方式替换为WeightInit.RELU
                 */
//                .weightInit(WeightInit.XAVIER)
                .weightInit(weightInit)
                /*
                 * 梯度更新方式，将会影响神经网络中各个层，是每层神经网络梯度更新的默认方式
                 * Nesterov：根据动量理论进行优化，防止梯度大幅震荡，避免错过最小值，通过参数未来的大致方向对参数进行预测
                 * Adagrad：适合稀疏数据，根据参数对稀疏数据进行大步更新，对频繁参数进行小幅更新，缺点是学习速率总是在衰减
                 * AdaDelta：解决Adagrad学习速率衰减的问题
                 * Adam：学习速率为自适应的，自使用时刻估计法，收敛效果较好，如果希望加快收敛速度或网络为复杂解构则使用该方法|β1设为0.9，β2设为0.9999，ϵ设为10-8
                 * 如果输入数据集比较稀疏，SGD、Nesterov和动量项等方法可能效果不好
                 */
//                .updater(new Nesterovs(9, 0.9))
                .updater(updater)
                /*
                 * 设置梯度规范化器，防止梯度消失
                 */
//                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                /*
                 * 设置梯队规范化器阈值
                 */
//                .gradientNormalizationThreshold(0.5)
                /*
                 * 根据上方的配置构建ListBuilder
                 */
                .list();
        /*
         * 添加各层
         */
        
        for(FeedForwardLayer layer : layers) configBuilder.layer(layer);
        
        /*
         * 初始化神经网络结构        
         */
        MultiLayerConfiguration layerConfig = configBuilder
        		/*
        		 * 是否预训练
        		 */
        		.pretrain(false)
        		/*
        		 * 是否反向传播
        		 */
        		.backprop(true)
        		/*
        		 * 设置反向传播类型
        		 */
//        		.backpropType(BackpropType.TruncatedBPTT)
//        		.tBPTTForwardLength(50).tBPTTBackwardLength(50)
        		/*
        		 * 执行构建
        		 */
        		.build();
        
        /*
         * 实例化神经网络
         */
        MultiLayerNetwork network = new MultiLayerNetwork(layerConfig);
//        network.setListeners(new ScoreIterationListener(1));
        
        /*
         * 初始化神经网络
         */
        network.init();
        
        return network;
	}
	
	
	/**
	 * 初始化图形界面
	 * @param net
	 * @return
	 */
	public static void initUI(MultiLayerNetwork net) {
		if(uiServer == null) uiServer = UIServer.getInstance();
		
        //设置网络信息（随时间变化的梯度、分值等）的存储位置。这里将其存储于内存。
        StatsStorage statsStorage = new InMemoryStatsStorage();         //或者： new FileStatsStorage(File)，用于后续的保存和载入
        
        //将StatsStorage实例连接至用户界面，让StatsStorage的内容能够被可视化
        uiServer.attach(statsStorage);

        //然后添加StatsListener来在网络定型时收集这些信息
        net.setListeners(new StatsListener(statsStorage));
	}
	
	
	/**
	 * 停止UI界面
	 */
	public static void stopUI() {
		if(uiServer != null) uiServer.stop();
	}
	
}
