package com.hanslv.test.machine.learning.lstm.test;

/**
 * 预测结果
 * @author hanslv
 *
 */
public class Result {
	private String stockId;//股票ID
	private String date;//执行预测的日期
	private double f1;
	private double predictedMax;//预测的Max
	private double predictedMin;//预测Min
	private double realMax;//真实Max
	private double realMin;//真实Min
	
	@Override
	public String toString() {
		return "predictedMax = " + predictedMax + "，predictedMin = " + predictedMin + "，realMax = " + realMax + "，realMin = " + realMin + "，f1：" + f1;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getPredictedMax() {
		return predictedMax;
	}

	public void setPredictedMax(double predictedMax) {
		this.predictedMax = predictedMax;
	}

	public double getPredictedMin() {
		return predictedMin;
	}

	public void setPredictedMin(double predictedMin) {
		this.predictedMin = predictedMin;
	}

	public double getRealMax() {
		return realMax;
	}

	public void setRealMax(double realMax) {
		this.realMax = realMax;
	}

	public double getRealMin() {
		return realMin;
	}

	public void setRealMin(double realMin) {
		this.realMin = realMin;
	}

	public double getF1() {
		return f1;
	}

	public void setF1(double f1) {
		this.f1 = f1;
	}
}
