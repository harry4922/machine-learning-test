package com.hanslv.test.machine.learning.encog.perceptron;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * 感知机模型
 * @author hanslv
 *
 */
public class PerceptronModel {
	
	/**
	 * 根据样本数据对感知机模型进行训练
	 * 
	 * 在使用后最好调用Encog.getInstance().shutdown();关闭资源
	 * 
	 * 感知机构造：
	 * 输入层：null（不包含激活函数） , true（包含偏置神经元） , 2（包含两个输入神经元）
	 * 隐藏层一：new ActivationReLU()（线性整流函数，输出0-x范围） , true（包含偏置神经元） , 5（包含五个神经元）
	 * （发现用ActivationReLU可能会出现无法收敛的问题，可以使用ActivationSigmoid替代ActivationReLU，但会加大计算量）
	 * 输出层：new ActivationSigmoid()（S形函数，常用于隐藏层的激活函数，计算量比较大并且容易出现梯度消失，输出0-1范围） , false , 1
	 * 
	 * 训练算法：
	 * 弹性反向传播算法（ResilientPropagation RPROP）
	 * 当连续误差梯度符号不变时采用加速策略，当连续误差梯度符号改变时采用减速策略
	 * 要求设定权重变化的上限和下线
	 * 适用于full-batch learning，不适用于mini-batch learning
	 * 该算法的改良版为RMSPROP算法
	 * 为了得到所需的全局优化算法。两种流行的全局优化算法是粒子群优化算法(PSO)和遗传算法(GA)，经过测试LevenbergMarquardtTraining算法为最优解
	 * 
	 * 
	 * 
	 * ----------------------------------------------------------------------------------------
	 * 测试结果：
	 * 
	 * 当前纪元：0误差为：0.2499400282988336
	 * 当前纪元：1误差为：0.22045906478109778
	 * 当前纪元：2误差为：0.1935126035183763
	 * 当前纪元：3误差为：0.17491747638696675
	 * 当前纪元：4误差为：0.1508268726227244
	 * 当前纪元：5误差为：0.11699378448033448
	 * 当前纪元：6误差为：0.09175597478339867
	 * 当前纪元：7误差为：0.06755911404268299
	 * 当前纪元：8误差为：0.056817070868992084
	 * 当前纪元：9误差为：0.04282201785516896
	 * 当前纪元：10误差为：0.04016397880042545
	 * 当前纪元：11误差为：0.03298274912768636
	 * 当前纪元：12误差为：0.029136596548557728
	 * 当前纪元：13误差为：0.02621924450516846
	 * 当前纪元：14误差为：0.02235391817714674
	 * 当前纪元：15误差为：0.01885385963344335
	 * 当前纪元：16误差为：0.015115440517743271
	 * 当前纪元：17误差为：0.011426277439446771
	 * 当前纪元：18误差为：0.008102368203978295
	 * --------------------------------------------
	 * 运算结果：
	 * 测试数据1：0.0,0.0，期望输出值：0.0，实际输出值：0.14072241659370918
	 * 测试数据2：1.0,0.0，期望输出值：1.0，实际输出值：0.9783886171772584
	 * ----------------------------------------------------------------------------------------
	 * 使用ActivationSigmoid替代ActivationReLU后：
	 * 
	 * 当前纪元：0误差为：0.2683468681647823
	 * 当前纪元：1误差为：0.2512822000532781
	 * 当前纪元：2误差为：0.25533459845096473
	 * 当前纪元：3误差为：0.25109630826492557
	 * 当前纪元：4误差为：0.24990902020125355
	 * 当前纪元：5误差为：0.2509810094535645
	 * 当前纪元：6误差为：0.24950843943910697
	 * 当前纪元：7误差为：0.24941781048561895
	 * 当前纪元：8误差为：0.24903860938115663
	 * 当前纪元：9误差为：0.24844617094376686
	 * 当前纪元：10误差为：0.24789242612643675
	 * 当前纪元：11误差为：0.24727502731213402
	 * 当前纪元：12误差为：0.2462624094590749
	 * 当前纪元：13误差为：0.24500266803464632
	 * 当前纪元：14误差为：0.2435310839375521
	 * 当前纪元：15误差为：0.2413441022807173
	 * 当前纪元：16误差为：0.2384940580633765
	 * 当前纪元：17误差为：0.23479350552564934
	 * 当前纪元：18误差为：0.23006885451106823
	 * 当前纪元：19误差为：0.22416011343711006
	 * 当前纪元：20误差为：0.21777394638749004
	 * 当前纪元：21误差为：0.20948089429917466
	 * 当前纪元：22误差为：0.20158525614858705
	 * 当前纪元：23误差为：0.1969412842200703
	 * 当前纪元：24误差为：0.1879912013794048
	 * 当前纪元：25误差为：0.17867149691529946
	 * 当前纪元：26误差为：0.16877951478707376
	 * 当前纪元：27误差为：0.16354768046980983
	 * 当前纪元：28误差为：0.1566947683419647
	 * 当前纪元：29误差为：0.1508015149587729
	 * 当前纪元：30误差为：0.14103906267300997
	 * 当前纪元：31误差为：0.13128962776699293
	 * 当前纪元：32误差为：0.121041308133364
	 * 当前纪元：33误差为：0.10235217477751449
	 * 当前纪元：34误差为：0.08724866626192891
	 * 当前纪元：35误差为：0.0713279613365223
	 * 当前纪元：36误差为：0.05767172113847062
	 * 当前纪元：37误差为：0.04367018926425528
	 * 当前纪元：38误差为：0.032352180562632546
	 * 当前纪元：39误差为：0.02443640704524744
	 * 当前纪元：40误差为：0.017113765481509974
	 * 当前纪元：41误差为：0.011446440547787828
	 * 当前纪元：42误差为：0.007037176788735508
	 * --------------------------------------------
	 * 运算结果：
	 * 测试数据1：0.0,0.0，期望输出值：0.0，实际输出值：0.11052962642878959
	 * 测试数据2：1.0,0.0，期望输出值：1.0，实际输出值：0.9635324422638548
	 * ----------------------------------------------------------------------------------------
	 * 
	 * 
	 * @param input 输入神经元样本数据（二维double数组）
	 * @param idealOutput 理想输出样本数据（二维double数组）
	 * @param errorLimit 误差限制
	 * @return 训练后的感知机模型
	 */
	public static BasicNetwork train(double[][] input , double[][] idealOutput , double errorLimit) {
		/*
		 * 构建感知机前馈神经网络模型
		 */
		BasicNetwork perceptronNetwork = new BasicNetwork();
		perceptronNetwork.addLayer(new BasicLayer(null , true , 2));
//		perceptronNetwork.addLayer(new BasicLayer(new ActivationReLU() , true , 5));
		perceptronNetwork.addLayer(new BasicLayer(new ActivationSigmoid() , true , 5));
		perceptronNetwork.addLayer(new BasicLayer(new ActivationSigmoid() , false , 1));
		/*
		 * 表示层次构建完毕
		 */
		perceptronNetwork.getStructure().finalizeStructure();
		
		/*
		 * 重置偏置神经元、神经元连接权限值，使用Nguyen-Widrow为其设置随机值
		 */
		perceptronNetwork.reset();
		
		
		/*
		 * 初始化训练数据
		 */
		MLDataSet trainSet = new BasicMLDataSet(input , idealOutput);
		
		
		/*
		 * 通过RPROP算法对当前模型进行训练
		 */
		final LevenbergMarquardtTraining trianAlgorithm = new LevenbergMarquardtTraining(perceptronNetwork , trainSet);
		
		/*
		 * 记录迭代纪元
		 */
		int epoch = 0;
		
		/*
		 * 根据初始化样本迭代训练当前模型
		 * 当误差大于误差限制时执行while
		 */
		do {
			trianAlgorithm.iteration();
			System.out.println("当前纪元：" + epoch + "误差为：" + trianAlgorithm.getError());
			epoch++;
		}while(trianAlgorithm.getError() > errorLimit);
		
		/*
		 * 结束训练
		 */
		trianAlgorithm.finishTraining();
		
		
		/*
		 * 返回训练后的模型
		 */
		return perceptronNetwork;
	}
	
	
	/**
	 * 传入MLDataSet
	 * @param trainData
	 * @param errorLimit
	 * @return
	 */
	public static BasicNetwork train(MLDataSet trainData , double errorLimit) {
		/*
		 * 构建感知机前馈神经网络模型
		 */
		BasicNetwork perceptronNetwork = new BasicNetwork();
		perceptronNetwork.addLayer(new BasicLayer(null , true , 2));
//		perceptronNetwork.addLayer(new BasicLayer(new ActivationReLU() , true , 5));
		perceptronNetwork.addLayer(new BasicLayer(new ActivationSigmoid() , true , 5));
		perceptronNetwork.addLayer(new BasicLayer(new ActivationSigmoid() , false , 1));
		/*
		 * 表示层次构建完毕
		 */
		perceptronNetwork.getStructure().finalizeStructure();
		
		/*
		 * 重置偏置神经元、神经元连接权限值，使用Nguyen-Widrow为其设置随机值
		 */
		perceptronNetwork.reset();
		
		/*
		 * 通过RPROP算法对当前模型进行训练
		 */
		final ResilientPropagation trianAlgorithm = new ResilientPropagation(perceptronNetwork , trainData);
		
		/*
		 * 记录迭代纪元
		 */
		int epoch = 0;
		
		/*
		 * 根据初始化样本迭代训练当前模型
		 * 当误差大于误差限制时执行while
		 */
		do {
			trianAlgorithm.iteration();
			System.out.println("当前纪元：" + epoch + "误差为：" + trianAlgorithm.getError());
			epoch++;
		}while(trianAlgorithm.getError() > errorLimit);
		
		/*
		 * 结束训练
		 */
		trianAlgorithm.finishTraining();
		
		
		/*
		 * 返回训练后的模型
		 */
		return perceptronNetwork;
	}
}
