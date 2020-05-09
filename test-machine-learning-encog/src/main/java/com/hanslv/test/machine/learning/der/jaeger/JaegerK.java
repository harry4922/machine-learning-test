package com.hanslv.test.machine.learning.der.jaeger;

import java.math.BigDecimal;
import java.util.List;

public class JaegerK {
	/**
	 * rsv = (n日收盘价-n日最低价)/(n日最高价-n日最低价) * 100
	 * @param priceInfoList n日区间的股票信息，包含每日收盘价、最低价、最高价
	 * @return
	 */
	private BigDecimal rsv(List<String> priceInfoList) {
		return BigDecimal.ZERO;
	}
	
	/**
	 * k = 2/3 * 前一日K值 + 1/3 * 当前RSV （第一天记为50）
	 * @return
	 */
	private BigDecimal k() {
		return BigDecimal.ZERO;
	}
	
	/**
	 * d = 2/3 * 前一日d值 + 1/3 * 当前K值（第一天记为50）
	 * @return
	 */
	private BigDecimal d() {
		return BigDecimal.ZERO;
	}
	
	/**
	 * 3 * 当前K值 - 2 * 当前D值（第一天记为50）
	 * @return
	 */
	private BigDecimal j() {
		return BigDecimal.ZERO;
	}
	
	/**
	 * 计算一只股票的KDJ
	 * @param stockId
	 */
	void countKDJ(String stockId) {
		
	}
}
