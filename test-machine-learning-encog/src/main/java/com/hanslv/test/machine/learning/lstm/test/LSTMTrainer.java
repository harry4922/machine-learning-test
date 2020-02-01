package com.hanslv.test.machine.learning.lstm.test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;

import com.hanslv.test.machine.learning.dl4j.rnn.NNFactory;
import com.hanslv.test.machine.learning.encog.util.DbUtil;

/**
 * http://localhost:9000/train/overview
 * @author hanslv
 *
 */
public class LSTMTrainer {
	static final String START_DATE = "2020-01-03";//开始时间
	static final int TEST_COUNT = 1;//训练次数
	static final int STOCK_ID_COUNT = 1;//参与测试的股票
	static final int AVERAGE_TYPE = 89;//均线类型
	
	static final int INPUT_SIZE = 5;//输入神经元数量
	static final int IDEAL_OUTPUT_SIZE = 2;//输出神经元数量
	
	static final String DATA_FILE_PATH_PREFIX = "E:\\Java\\eclipse\\machine-learning-test\\dataFiles\\trainData";//数据文件存储地址前缀
	static final String TEST_DATA_FILE_PATH_PREFIX = "E:\\Java\\eclipse\\machine-learning-test\\dataFiles\\testData";//训练数据文件存储地址前缀
	static final String DATA_FILE_PATH_SUFFIX = ".csv";//数据文件存储地址后缀
	
	static final int EPOCH = 100;//训练纪元
	static final int TRAIN_STEP_LONG = 300;//训练数据批次
	static final int TEST_STEP_LONG = 6;//测试数据批次
	static final int BATCH_SIZE = 1;//单步长中包含的数据量
	static final int SIGLE_TIME_LENGTH = 5;//单个数据的时间跨度，包含几天成交信息的汇总
	
	public static void main(String[] args) {
		try {
			for(int i = 0 ; i < TEST_COUNT ; i++) {
				for(int j = 1 ; j <= STOCK_ID_COUNT ; j++) {
					String stockId = j + "";
					String currentStartDate = DbUtil.changeDate(stockId , START_DATE , i * SIGLE_TIME_LENGTH , true);
					
					/*
					 * 判断当前均线是否上涨
					 */
					BigDecimal averageScope = new BigDecimal(DbUtil.getAverage(stockId , currentStartDate , AVERAGE_TYPE)[1]);
					if(averageScope.compareTo(BigDecimal.ZERO) <= 0) continue;
					doTrain(stockId , currentStartDate);
				}
			}
		}finally {
			NNFactory.stopUI();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 执行训练
	 * @return
	 */
	private static void doTrain(String stockId , String date) {
		String[] stockInfoArray = DbUtil.findStockInfo(stockId).split(",");
		String stockCode = stockInfoArray[1];
		try {
			Result result = train(getTrainData(stockId , date));
			if(result != null) {
				double forcastMax = result.getResultDataArray()[0];
				double forcastMin = result.getResultDataArray()[1];
				double realMax = result.getRealResultDataArray()[0];
				double realMin = result.getRealResultDataArray()[1];
				
				System.err.println("stockCode = " + stockCode + "，date = " + date + "，maxDiff = " + (forcastMax - realMax) + "，minDiff = " + (forcastMin - realMin) + "，result = " + result);
				
//				if(forcastMax * (1 - DIFF_LIMIT) <= realMax && forcastMin * (1 + DIFF_LIMIT) >= realMin)
//					System.out.println("stockCode：" + stockCode + "，date：" + date + "，Result：" + result);
//				else
//					System.err.println("stockCode：" + stockCode + "，date：" + date + "，Result：" + result);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * 训练LSTM
	 * @param trainDataList 训练数据集合
	 * @param testDataList 测试数据集合
	 * @return
	 * @throws IOException 
	 */
	private static Result train(DataSetIterator[] dataSetIterators) throws IOException {
		if(dataSetIterators == null) return null;
		
		/*
		 * 数据归一化处理
		 */
		DataNormalization normalizer = normalize(dataSetIterators);
		
		/*
		 * 获取LSTM神经网络
		 */
		MultiLayerNetwork lstmNetwork = LSTMBuilder.build(INPUT_SIZE , IDEAL_OUTPUT_SIZE);
		System.out.println(lstmNetwork.summary());
		
		DataSetIterator trainDataSetIterator = dataSetIterators[0];
		DataSetIterator testDataSetIterator = dataSetIterators[1];
		
		/*
		 * 执行预测
		 */
		Result result = new Result();
		for(int i = 0 ; i < EPOCH ; i++) {
			while(trainDataSetIterator.hasNext()) {
				DataSet trainDataSet = trainDataSetIterator.next();
				lstmNetwork.fit(trainDataSet);
			}
//			lstmNetwork.fit(trainDataSetIterator);
			trainDataSetIterator.reset();
			
			/*
			 * 测试
			 */
			RegressionEvaluation eval = new RegressionEvaluation(IDEAL_OUTPUT_SIZE);
			Evaluation eval2 = new Evaluation(IDEAL_OUTPUT_SIZE);
			while(testDataSetIterator.hasNext()) {
	            DataSet testData = testDataSetIterator.next();
	            INDArray features = testData.getFeatures();
	            INDArray lables = testData.getLabels();
	            INDArray predicted = lstmNetwork.output(features, true);
	
	            eval.evalTimeSeries(lables, predicted);
	            eval2.evalTimeSeries(lables , predicted);
			}
			testDataSetIterator.reset();
            
            List<double[]> resultList = doPredicted(trainDataSetIterator, testDataSetIterator, lstmNetwork, normalizer);
            
            double currentMse = eval.averageMeanSquaredError();
            boolean mseCheckB = currentMse < 1;
            double currentAccuracy = eval2.accuracy();
            double lastAccuracy = result.getAccuracy();
            boolean accuracyCheck = currentAccuracy != 0 && currentAccuracy > lastAccuracy;
            double currentPrecision = eval2.precision();
            double lastPrecision = result.getPrecision();
            boolean precisionCheck = currentAccuracy != 0 && currentPrecision > lastPrecision;
            double currentRecall = eval2.recall();
            double lastRecall = result.getRecall();
            boolean recallCheck = currentRecall != 0 && currentRecall > lastRecall;
            double currentF1 = eval2.f1();
            double lastF1 = result.getF1();
            boolean f1Check = currentF1 != 0 && currentF1 > lastF1;
            
//            double lastMse = result.getMse();
//            boolean mseCheck = currentMse < lastMse || lastMse == 0;
//            
//			double forcastMax = resultList.get(1)[0];
//			double forcastMin = resultList.get(1)[1];
//			double realMax = resultList.get(0)[0];
//			double realMin = resultList.get(0)[1];
//			System.err.println("maxDiff = " + (forcastMax - realMax) + "，minDiff = " + (forcastMin - realMin) + "，MSE = " + currentMse);
//			System.out.println(eval.stats());
//			System.out.println(eval2.stats());
            
//            if(mseCheck) {
//            	result.setMse(currentMse);
//            	result.setRealResultDataArray(resultList.get(0));
//            	result.setResultDataArray(resultList.get(1));
//            }
			
			if(mseCheckB && accuracyCheck && precisionCheck && recallCheck && f1Check) {
				result.setAccuracy(currentAccuracy);
				result.setPrecision(currentPrecision);
				result.setRecall(currentRecall);
				result.setF1(currentF1);
				result.setRealResultDataArray(resultList.get(0));
				result.setResultDataArray(resultList.get(1));
			}
			
		}
		return result;
	}
	
	
	/**
	 * 执行预测并获取预测结果
	 * @param trainDataSetIterator
	 * @param testDataSetIterator
	 * @param lstmNetwork
	 * @param normalizer
	 * @return
	 */
    private static List<double[]> doPredicted(DataSetIterator trainDataSetIterator , DataSetIterator testDataSetIterator , MultiLayerNetwork lstmNetwork , DataNormalization normalizer){
    	List<double[]> resultList = new ArrayList<>();
		while(trainDataSetIterator.hasNext()) {
			DataSet trainData = trainDataSetIterator.next();
			lstmNetwork.rnnTimeStep(trainData.getFeatures());
		}
		INDArray predictedResult = null;
		DataSet testData = null;
		while(testDataSetIterator.hasNext()) {
			testData = testDataSetIterator.next();
			predictedResult = lstmNetwork.rnnTimeStep(testData.getFeatures());
		}
		normalizer.revert(testData);
		normalizer.revertLabels(predictedResult);
		double[] realResultDouble = testData.getLabels().data().asDouble();
		double[] predictedResultDouble = predictedResult.data().asDouble();
		resultList.add(realResultDouble);
		resultList.add(predictedResultDouble);
		trainDataSetIterator.reset();
		testDataSetIterator.reset();
		lstmNetwork.clear();
		return resultList;
    }
	
	
	/**
	 * 获取训练源数据
	 * @return
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private static DataSetIterator[] getTrainData(String stockId , String date) throws IOException, InterruptedException{
		String trainDataEndDate = DbUtil.changeDate(stockId , date , TEST_STEP_LONG * SIGLE_TIME_LENGTH , true);//去除测试数据日期
		boolean resultA = createDatas(stockId , trainDataEndDate , TRAIN_STEP_LONG , DATA_FILE_PATH_PREFIX);//创建训练数据集文件
		boolean resultB = createDatas(stockId , date , TEST_STEP_LONG , TEST_DATA_FILE_PATH_PREFIX);//创建测试数据集文件
		
		if(!resultA || !resultB) return null;
		
		/*
		 * 从CSV文件读取数据
		 */
		SequenceRecordReader trainDataReader = new CSVSequenceRecordReader(0 , ",");//指定跳过的行数和分隔符
		trainDataReader.initialize(new NumberedFileInputSplit(DATA_FILE_PATH_PREFIX + "%d" + DATA_FILE_PATH_SUFFIX , 0, TRAIN_STEP_LONG - 2));
		SequenceRecordReader testDataReader = new CSVSequenceRecordReader(0 , ",");//指定跳过的行数和分隔符
		testDataReader.initialize(new NumberedFileInputSplit(TEST_DATA_FILE_PATH_PREFIX + "%d" + DATA_FILE_PATH_SUFFIX , 0, TEST_STEP_LONG - 2));
		//定义单时间步长数据量、是否为回归模型
		DataSetIterator trainDataIterator = new SequenceRecordReaderDataSetIterator(trainDataReader , BATCH_SIZE , 2 , INPUT_SIZE , true);
		DataSetIterator testDataIterator = new SequenceRecordReaderDataSetIterator(testDataReader , BATCH_SIZE , 2 , INPUT_SIZE , true);
		
		return new DataSetIterator[] {trainDataIterator , testDataIterator};
	}
	
	
	/**
	 * 生成数据
	 * @param inputStart
	 * @param stepLong
	 * @param filePrefix
	 * @param random
	 * @throws IOException
	 */
	private static boolean createDatas(String stockId , String date , int stepLong , String filePrefix) throws IOException {
		/*
		 * 将数据集合写入到文件
		 */
		List<String> sourceDataList = getSourceData(stockId , date , stepLong , SIGLE_TIME_LENGTH);
		if(sourceDataList.size() != stepLong - 1) return false;
		for(int i = 0 ; i < stepLong - 1 ; i++) {
			/*
			 * 创建当前时间步长文件
			 */
			File dataFile = new File(filePrefix + i + DATA_FILE_PATH_SUFFIX);
			if(dataFile.exists()) dataFile.delete();
			dataFile.createNewFile();
			
			/*
			 * 将指定数量的数据写入到单个文件
			 */
			try(RandomAccessFile randomAccessFile = new RandomAccessFile(dataFile , "rw");
					FileChannel dataFileChannel = randomAccessFile.getChannel()){
				for(int j = 0 ; j < BATCH_SIZE ; j++) {
					String data = sourceDataList.get(i * BATCH_SIZE + j) + System.lineSeparator();
					ByteBuffer dataBuffer = ByteBuffer.wrap(data.getBytes());
					dataFileChannel.write(dataBuffer);
				}
			}catch(IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	
	
	/**
	 * 获取训练数据List
	 * @param stockId
	 * @param date
	 * @return
	 */
	private static List<String> getSourceData(String stockId , String date , int stepLong , int singleTimeLength){
		/*
		 * 获取标准数据
		 */
		List<String> rawDataList = DbUtil.getPriceInfo(stockId , date , stepLong * singleTimeLength);
		BigDecimal max = null;//最大值
		BigDecimal min = null;//最小值
		BigDecimal startPrice = null;//开盘价
		BigDecimal endPrice = null;//收盘价
//		BigDecimal lastEndPrice = null;//上一时间批次收盘价
		BigDecimal turnoverRate = BigDecimal.ZERO;//换手率
		int counterA = 0;
		List<String> bufferList = new ArrayList<>();
		for(String rawData : rawDataList) {
			String[] rawDataArray = rawData.split(",");
			BigDecimal currentMax = new BigDecimal(rawDataArray[2]);//最大值
			BigDecimal currentMin = new BigDecimal(rawDataArray[3]);//最小值
			BigDecimal currentStartPrice = new BigDecimal(rawDataArray[4]);//开盘价
			BigDecimal currentEndPrice = new BigDecimal(rawDataArray[5]);//收盘价
			BigDecimal currentTurnoverRate = new BigDecimal(rawDataArray[6]);//换手率
			counterA++;
			
			if(max == null || max.compareTo(currentMax) < 0) max = currentMax;
			if(min == null || min.compareTo(currentMin) > 0) min = currentMin;
			if(counterA == 1) startPrice = currentStartPrice;
			if(counterA == singleTimeLength) endPrice = currentEndPrice;
			turnoverRate = turnoverRate.add(currentTurnoverRate);
			if(counterA == singleTimeLength) {
//				if(lastEndPrice == null) lastEndPrice = endPrice;
//				else {
					String sourceData = 
//							max.subtract(lastEndPrice).divide(lastEndPrice , 4 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)) + "," + 
//							lastEndPrice.subtract(min).divide(lastEndPrice , 4 , BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)) + "," + 
							max + "," +
							min + "," + 
							startPrice + "," + 
							endPrice + "," + 
							turnoverRate;
					bufferList.add(sourceData);
//				}
				max = null;
				min = null;
				startPrice = null;
				endPrice = null;
				turnoverRate = BigDecimal.ZERO;
				counterA = 0;
			}
		}
		
		/*
		 * 拼接实际结果
		 */
		List<String> sourceDataList = new ArrayList<>();
		for(int i = 0 ; i < bufferList.size() ; i++) {
			if(i + 1 < bufferList.size()) {
				String[] nextValArray = bufferList.get(i + 1).split(",");
				String nextMax = nextValArray[0];
				String nextMin = nextValArray[1];
				String sourceData = bufferList.get(i) + "," + nextMax + "," + nextMin;
				sourceDataList.add(sourceData);
			}
		}
		return sourceDataList;
	}
	
	
	
	/**
	 * 数据标准化
	 * @param trainDataSet
	 * @return
	 */
	private static DataNormalization normalize(DataSetIterator dataSetIterators[]) {
		DataNormalization normalizer = new NormalizerMinMaxScaler(-1 , 1);
		normalizer.fitLabel(true);
		normalizer.fit(dataSetIterators[0]);
		normalizer.fit(dataSetIterators[1]);
		dataSetIterators[0].setPreProcessor(normalizer);
		dataSetIterators[1].setPreProcessor(normalizer);
		return normalizer;
	}
}
