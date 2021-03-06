package com.hanslv.test.machine.learning.lstm.test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
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
	static final String START_DATE = "2020-02-10";//开始时间2020-01-03
	static final int TEST_COUNT = 1;//训练次数
	static final int STOCK_ID_COUNT = 3550;//参与测试的股票
	static final int STOCK_ID_START = 1;//起始股票ID
	static final int AVERAGE_TYPE = 89;//均线类型
	static final int SLEEP_SECONDS = 2;//休眠时间
	
	static final int INPUT_SIZE = 5;//输入神经元数量
	static final int IDEAL_OUTPUT_SIZE = 2;//输出神经元数量
	
	static final String TRAIN_DATA_LABEL_FILE_PREFIX = "E:\\Java\\eclipse\\machine-learning-test\\dataFiles\\trainData-label";//训练数据标题文件前缀
	static final String TRAIN_DATA_FEATURES_FILE_PREFIX = "E:\\Java\\eclipse\\machine-learning-test\\dataFiles\\trainData-features";//训练数据输入文件前缀
	static final String TEST_DATA_LABEL_FILE_PREFIX = "E:\\Java\\eclipse\\machine-learning-test\\dataFiles\\testData-label";//测试数据标题文件前缀
	static final String TEST_DATA_FEATURES_FILE_PREFIX = "E:\\Java\\eclipse\\machine-learning-test\\dataFiles\\testData-features";//测试数据输入文件前缀
	static final String DATA_FILE_PATH_SUFFIX = ".csv";//数据文件存储地址后缀
	
	static final int EPOCH = 20;//训练纪元10000
	static final int TRAIN_DATA_SIZE = 100;//训练数据批次
	static final int TEST_DATA_SIZE = 101;//测试数据批次
	static final int BATCH_SIZE = 1;//单步长中包含的数据量
	static final int SIGLE_TIME_LENGTH = 5;//单个数据的时间跨度，包含几天成交信息的汇总
	static final String SORT_ID = "2258";//分类ID
	
	public static void main(String[] args) throws InterruptedException {
		/*
		 * 改为获取波段分类的股票
		 */
		String[] stockIdArray = DbUtil.getStockIdBySortId(SORT_ID).split(",");
		try {
			for(String stockId : stockIdArray) {
				BigDecimal averageScope = new BigDecimal(DbUtil.getAverage(stockId , START_DATE , AVERAGE_TYPE)[1]);
				if(averageScope.compareTo(BigDecimal.ZERO) <= 0) continue;
				doTrain(stockId , START_DATE);
				TimeUnit.SECONDS.sleep(SLEEP_SECONDS);
			}
		}finally {
			NNFactory.stopUI();
		}
		
		
//		try {
//			for(int i = 0 ; i < TEST_COUNT ; i++) {
//				for(int j = STOCK_ID_START ; j <= STOCK_ID_COUNT ; j++) {
//					String stockId = j + "";
//					String currentStartDate = DbUtil.changeDate(stockId , START_DATE , i * SIGLE_TIME_LENGTH , true);
//					
//					/*
//					 * 判断当前均线是否上涨
//					 */
//					BigDecimal averageScope = new BigDecimal(DbUtil.getAverage(stockId , currentStartDate , AVERAGE_TYPE)[1]);
//					if(averageScope.compareTo(BigDecimal.ZERO) <= 0) continue;
//					doTrain(stockId , currentStartDate);
//					TimeUnit.SECONDS.sleep(SLEEP_SECONDS);
//				}
//			}
//		}finally {
//			NNFactory.stopUI();
//		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 执行训练
	 * @return
	 */
	private static void doTrain(String stockId , String date) {
		String[] stockInfoArray = DbUtil.findStockInfo(stockId).split(",");
		String stockCode = stockInfoArray[1];
//		System.out.println("-------------------------------------正在计算：" + stockCode + "-------------------------------------");
		try {
			Result result = train(getTrainData(stockId , date));
			if(result != null) {
				double predictedMax = result.getPredictedMax();
				double predictedMin = result.getPredictedMin();
				double realMax = result.getRealMax();
				double realMin = result.getRealMin();
				System.err.println("stockCode = " + stockCode + "，date = " + date + "，maxDiff = " + (predictedMax - realMax) + "，minDiff = " + (predictedMin - realMin) + "，result = " + result);
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
		
		DataSetIterator trainDataSetIterator = dataSetIterators[0];
		DataSetIterator testDataSetIterator = dataSetIterators[1];
		
		/*
		 * 执行预测
		 */
		Evaluation eval = null;
		Map<Double , String> scoreMap = new HashMap<>();
		for(int i = 0 ; i < EPOCH ; i++) {
			while(trainDataSetIterator.hasNext()) {
				DataSet trainDataSet = trainDataSetIterator.next();
				lstmNetwork.fit(trainDataSet);
			}
			
			/*
			 * 测试
			 */
			eval = new Evaluation(IDEAL_OUTPUT_SIZE);
			int testCounter = 0;
			while(testDataSetIterator.hasNext()) {
				testCounter++;
	            DataSet testData = testDataSetIterator.next();
	            INDArray features = testData.getFeatures();
	            INDArray labels = testData.getLabels();
	            INDArray predicted = lstmNetwork.output(features, true);
	
	            eval.evalTimeSeries(labels , predicted);
	            if(testCounter == TEST_DATA_SIZE - 1) break;
			}
			
			trainDataSetIterator.reset();
			testDataSetIterator.reset();
			
			double currentF1 = eval.f1();
			if(currentF1 >= 0.5) {
				List<double[]> resultList = doPredicted(trainDataSetIterator , testDataSetIterator , lstmNetwork , normalizer);
				double[] realResult = resultList.get(0);
				double realMax = realResult[0];
				double realMin = realResult[1];
				double[] predictedResult = resultList.get(1);
				double predictedMax = predictedResult[0];
				double predictedMin = predictedResult[1];
				scoreMap.put(currentF1 , realMax + "," + realMin + "," + predictedMax + "," + predictedMin);
				
				System.err.println("currentF1 = " + currentF1 + "，maxDiff = " + (realMax - predictedMax) + "，minDiff = " + (realMin - predictedMin));
			}
		}
		
//		Result result = null;
//		double currentF1 = eval.f1();
//		if(currentF1 >= 0.5) {
//			List<double[]> resultList = doPredicted(trainDataSetIterator , testDataSetIterator , lstmNetwork , normalizer);
//			double[] realResult = resultList.get(0);
//			double realMax = realResult[0];
//			double realMin = realResult[1];
//			double[] predictedResult = resultList.get(1);
//			double predictedMax = predictedResult[0];
//			double predictedMin = predictedResult[1];
//			System.err.println("currentF1 = " + currentF1 + "，maxDiff = " + (realMax - predictedMax) + "，minDiff = " + (realMin - predictedMin));
//		}

		
		
		
		/*
		 * 按照F1分值对数据进行排序
		 */
		Set<Double> keySet = scoreMap.keySet();
		Object[] keyArray = keySet.toArray();
		Arrays.sort(keyArray);
		
//		for(Object key : keyArray) {
//			Double keyDouble = Double.parseDouble(key.toString());
//			System.err.println("key = " + keyDouble + "，value = " + scoreMap.get(key));
//		}
		
		Result result = new Result();
		if(keyArray.length != 0) {
			Double f1 = Double.parseDouble(keyArray[keyArray.length - 1].toString());
			String[] resultArray = scoreMap.get(f1).split(",");
			result.setRealMax(Double.parseDouble(resultArray[0]));
			result.setRealMin(Double.parseDouble(resultArray[1]));
			result.setPredictedMax(Double.parseDouble(resultArray[2]));
			result.setPredictedMin(Double.parseDouble(resultArray[3]));
			result.setF1(f1);
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
		lstmNetwork.rnnClearPreviousState();
		return resultList;
    }
	
	
	/**
	 * 获取训练源数据
	 * @return
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private static DataSetIterator[] getTrainData(String stockId , String date) throws IOException, InterruptedException{
		boolean createFileResult = createDatas(stockId , date);
		/*
		 * 判空
		 */
		if(!createFileResult) return null;
		
		/*
		 * 从CSV文件读取数据
		 */
		SequenceRecordReader trainDataLabelReader = new CSVSequenceRecordReader(0 , ",");//指定跳过的行数和分隔符
		trainDataLabelReader.initialize(new NumberedFileInputSplit(TRAIN_DATA_LABEL_FILE_PREFIX + "%d" + DATA_FILE_PATH_SUFFIX , 0 , TRAIN_DATA_SIZE - 1));
		SequenceRecordReader trainDataFeaturesReader = new CSVSequenceRecordReader(0 , ",");//指定跳过的行数和分隔符
		trainDataFeaturesReader.initialize(new NumberedFileInputSplit(TRAIN_DATA_FEATURES_FILE_PREFIX + "%d" + DATA_FILE_PATH_SUFFIX , 0 , TRAIN_DATA_SIZE - 1));
		
		SequenceRecordReader testDataLabelReader = new CSVSequenceRecordReader(0 , ",");//指定跳过的行数和分隔符
		testDataLabelReader.initialize(new NumberedFileInputSplit(TEST_DATA_LABEL_FILE_PREFIX + "%d" + DATA_FILE_PATH_SUFFIX , 0 , TEST_DATA_SIZE - 2));
		SequenceRecordReader testDataFeaturesReader = new CSVSequenceRecordReader(0 , ",");//指定跳过的行数和分隔符
		testDataFeaturesReader.initialize(new NumberedFileInputSplit(TEST_DATA_FEATURES_FILE_PREFIX + "%d" + DATA_FILE_PATH_SUFFIX , 0 , TEST_DATA_SIZE - 2));
		//定义单时间步长数据量、是否为回归模型
		DataSetIterator trainDataIterator = new SequenceRecordReaderDataSetIterator(trainDataFeaturesReader , trainDataLabelReader , BATCH_SIZE , -1 , true , SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_START);
		DataSetIterator testDataIterator = new SequenceRecordReaderDataSetIterator(testDataFeaturesReader , testDataLabelReader , BATCH_SIZE , -1 , true , SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_START);
		
		return new DataSetIterator[] {trainDataIterator , testDataIterator};
	}
	
	
	/**
	 * 获取训练数据并将问价写入指定文件
	 * @param stockId
	 * @param date
	 * @return
	 */
	private static boolean createDatas(String stockId , String date) {
		/*
		 * 获取全部数据
		 * 注：不包括date当周数据
		 */
		List<String> sourceDataList = getSourceData(stockId , date , TRAIN_DATA_SIZE + TEST_DATA_SIZE , SIGLE_TIME_LENGTH);
		if(sourceDataList.size() != TRAIN_DATA_SIZE + TEST_DATA_SIZE - 1) return false;
		
		/*
		 * 拆分为训练数据、测试数据
		 */
		List<String> trainDataList = new ArrayList<>();
		for(int i = 0 ; i < TRAIN_DATA_SIZE ; i++) trainDataList.add(sourceDataList.get(i));
		List<String> testDataList = new ArrayList<>();
		for(int i = TRAIN_DATA_SIZE ; i < sourceDataList.size() ; i++) testDataList.add(sourceDataList.get(i));
		
		try {
			writeToFile(TRAIN_DATA_LABEL_FILE_PREFIX , TRAIN_DATA_FEATURES_FILE_PREFIX , trainDataList);
			writeToFile(TEST_DATA_LABEL_FILE_PREFIX , TEST_DATA_FEATURES_FILE_PREFIX , testDataList);
		}catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 将数据写入对应文件
	 * @param labelFilePathPrefix
	 * @param featuresFilePathPrefix
	 * @param dataList
	 * @throws IOException 
	 */
	private static void writeToFile(String labelFilePathPrefix , String featuresFilePathPrefix , List<String> dataList) throws IOException {
		int stepLong = dataList.size();//当前步长
		/*
		 * 将数据写入到文件中
		 */
		for(int i = 0 ; i < stepLong ; i++) {
			/*
			 * 创建新文件
			 */
			File labelDataFile = new File(labelFilePathPrefix + i + DATA_FILE_PATH_SUFFIX);
			if(labelDataFile.exists()) labelDataFile.delete();
			labelDataFile.createNewFile();
			File featuresDataFile = new File(featuresFilePathPrefix + i + DATA_FILE_PATH_SUFFIX);
			if(featuresDataFile.exists()) featuresDataFile.delete();
			featuresDataFile.createNewFile();
			
			String[] currentData = dataList.get(i).split(",");//当前条数据
			StringBuffer labelData = new StringBuffer();//输出数据
			StringBuffer featuresData = new StringBuffer();//输入数据
			for(int j = 0 ; j < currentData.length ; j++) {
				/*
				 * 输入数据
				 */
				if(j < INPUT_SIZE) {
					if(j == INPUT_SIZE - 1) featuresData.append(currentData[j]);
					else featuresData.append(currentData[j]).append(",");
				}
				/*
				 * 实际输出数据
				 */
				else {
					if(j == currentData.length - 1) labelData.append(currentData[j]);
					else labelData.append(currentData[j]).append(",");
				}
			}
			
			/*
			 * 将数据分别写入Labels和Features
			 */
			try(RandomAccessFile labelRandomAccessFile = new RandomAccessFile(labelDataFile , "rw");
				FileChannel labelDataFileChannel = labelRandomAccessFile.getChannel();
				RandomAccessFile featuresRandomAccessFile = new RandomAccessFile(featuresDataFile , "rw");
				FileChannel featuresDataFileChannel = featuresRandomAccessFile.getChannel();){
				ByteBuffer labelDataBuffer = ByteBuffer.wrap(labelData.toString().getBytes());
				labelDataFileChannel.write(labelDataBuffer);
				
				ByteBuffer FeaturesDataBuffer = ByteBuffer.wrap(featuresData.toString().getBytes());
				featuresDataFileChannel.write(FeaturesDataBuffer);
			}
		}
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
					String sourceData = 
							max + "," +
							min + "," + 
							startPrice + "," + 
							endPrice + "," + 
							turnoverRate;
					bufferList.add(sourceData);
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
