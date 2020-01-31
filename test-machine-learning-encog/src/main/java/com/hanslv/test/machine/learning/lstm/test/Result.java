package com.hanslv.test.machine.learning.lstm.test;

import java.util.Arrays;

/**
 * 预测结果
 * @author hanslv
 *
 */
public class Result {
	private String stockId;//股票ID
	private String date;//执行预测的日期
	private double mse;//当前均方误差值
	private double[] resultDataArray;//结果数组
	private double[] realResultDataArray;
	
	@Override
	public String toString() {
		return "forcastResult：" + Arrays.toString(resultDataArray) + "，realResult" + Arrays.toString(realResultDataArray) + ",MSE：" + mse;
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

	public double getMse() {
		return mse;
	}

	public void setMse(double mse) {
		this.mse = mse;
	}

	public double[] getResultDataArray() {
		return resultDataArray;
	}

	public void setResultDataArray(double[] resultDataArray) {
		this.resultDataArray = resultDataArray;
	}

	public double[] getRealResultDataArray() {
		return realResultDataArray;
	}

	public void setRealResultDataArray(double[] realResultDataArray) {
		this.realResultDataArray = realResultDataArray;
	}
}
