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
	static final int TEST_COUNT = 10;//训练次数
	static final int STOCK_ID_COUNT = 3550;//参与测试的股票
	static final int AVERAGE_TYPE = 89;//均线类型
	
	static final int INPUT_SIZE = 5;//输入神经元数量
	static final int IDEAL_OUTPUT_SIZE = 2;//输出神经元数量
	
	static final String DATA_FILE_PATH_PREFIX = "E:\\Java\\eclipse\\machine-learning-test\\dataFiles\\trainData";//数据文件存储地址前缀
	static final String TEST_DATA_FILE_PATH_PREFIX = "E:\\Java\\eclipse\\machine-learning-test\\dataFiles\\testData";//训练数据文件存储地址前缀
	static final String DATA_FILE_PATH_SUFFIX = ".csv";//数据文件存储地址后缀
	
	static final int EPOCH = 100;//训练纪元
	static final int TRAIN_STEP_LONG = 251;//训练数据批次
	static final int TEST_STEP_LONG = 6;//测试数据批次
	static final int BATCH_SIZE = 1;//单步长中包含的数据量
	static final int SIGLE_TIME_LENGTH = 5;//单个数据的时间跨度，包含几天成交信息的汇总
	static final double DIFF_LIMIT = 0.01;//误差允许范围
	
//	static Map<String , List<Result>> resultMap = new HashMap<>();//结果集
	
	public static void main(String[] args) {
		try {
			for(int i = 0 ; i < TEST_COUNT ; i++) {
				for(int j = 1 ; j < STOCK_ID_COUNT ; j++) {
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
	private static Result doTrain(String stockId , String date) {
		/*
		 * 获取训练数据
		 */
		try {
			Result testResult =train(getTrainData(stockId , date));
			
			if(testResult != null) {
				String[] stockInfoArray = DbUtil.findStockInfo(stockId).split(",");
				String stockCode = stockInfoArray[1];
				double forcastMax = testResult.getResultDataArray()[0];
				double forcastMin = testResult.getResultDataArray()[1];
				double realMax = testResult.getRealResultDataArray()[0];
				double realMin = testResult.getRealResultDataArray()[1];
				
				if(forcastMax * (1 - DIFF_LIMIT) <= realMax && forcastMin * (1 + DIFF_LIMIT) >= realMin)
					System.out.println("stockCode：" + stockCode + "，date：" + date + "，Result：" + testResult);
				else
					System.err.println("stockCode：" + stockCode + "，date：" + date + "，Result：" + testResult);
			}
			return train(getTrainData(stockId , date));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}		
		
	}
	
	
	/**
	 * 训练LSTM
	 * @param trainDataList 训练数据集合
	 * @param testDataList 测试数据集合
	 * @return
	 */
	private static Result train(DataSetIterator[] dataSetIterators) {
		if(dataSetIterators == null) return null;
		
		/*
		 * 数据归一化处理
		 */
		DataNormalization normalize = normalize(dataSetIterators);
		
		/*
		 * 获取LSTM神经网络
		 */
		MultiLayerNetwork lstmNetwork = LSTMBuilder.build(INPUT_SIZE , IDEAL_OUTPUT_SIZE);
//		NNFactory.initUI(lstmNetwork);
//		System.out.println(lstmNetwork.summary());
		
		DataSetIterator trainDataSetIterator = dataSetIterators[0];
		DataSetIterator testDataSetIterator = dataSetIterators[1];
		
		/*
		 * 执行预测
		 */
		Evaluation eval = new Evaluation(IDEAL_OUTPUT_SIZE);
		Result result = new Result();
		for(int i = 0 ; i < EPOCH ; i++) {
			while(trainDataSetIterator.hasNext()) {
				DataSet trainDataSet = trainDataSetIterator.next();
				lstmNetwork.fit(trainDataSet);
			}
			
			/*
			 * 测试
			 */
			double[] realResult = null;
			double[] forcastResult = null;
			while(testDataSetIterator.hasNext()) {
				DataSet testDataSet = testDataSetIterator.next();
				INDArray testInput = testDataSet.getFeatures();
				INDArray testOutput = testDataSet.getLabels();
				INDArray checkOutput = lstmNetwork.output(testInput , false);
				DataSet checkDataSet = new DataSet(testInput , checkOutput);
				eval.evalTimeSeries(testOutput , checkOutput);
				
				
				normalize.revert(testDataSet);
				normalize.revert(checkDataSet);
				INDArray revertedRealResult = testDataSet.getLabels();
				INDArray revertedForcastResult = checkDataSet.getLabels();
				double[] realResultArray = revertedRealResult.data().asDouble();
				double[] forcastResultArray = revertedForcastResult.data().asDouble();
				realResult = realResultArray;
				forcastResult = forcastResultArray;
//				System.out.println("预测：max：" + forcastResultArray[0] + ",min：" + forcastResultArray[1]);
//				System.out.println("实际：max：" + realResultArray[0] + ",min：" + realResultArray[1]);
//				System.out.println("----------------------------------------------------------");
			}
//			System.out.println(eval.stats());
			trainDataSetIterator.reset();
			testDataSetIterator.reset();
			double currentAccuracy = result.getAccuracy();
			double accuracy = eval.accuracy();
			boolean accuracyCheck = accuracy >= currentAccuracy;
			
			double currentPrecision = result.getPrecision();
			double precision = eval.precision();
			boolean precisionCheck = precision >= currentPrecision;
			
			double currentRecall = result.getRecall();
			double recall = eval.recall();
			boolean recallCheck = recall >= currentRecall;
			
			double currentF1 = result.getF1();
			double f1 = eval.f1();
			boolean f1Check = f1 >= currentF1;
			
			if(f1Check && recallCheck && precisionCheck && accuracyCheck) {
				result.setAccuracy(accuracy);
				result.setPrecision(precision);
				result.setRecall(recall);
				result.setF1(f1);
				result.setRealResultDataArray(realResult);
				result.setResultDataArray(forcastResult);
				
				currentAccuracy = accuracy;
				currentPrecision = precision;
				currentRecall = recall;
				currentF1 = f1;
			}
		}
		return result;
	}
	
	
	
	/**
	 * 获取训练源数据
	 * @return
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private static DataSetIterator[] getTrainData(String stockId , String date) throws IOException, InterruptedException{
		boolean resultA = createDatas(stockId , date , TRAIN_STEP_LONG , DATA_FILE_PATH_PREFIX);//创建训练数据集文件
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
