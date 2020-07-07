package com.hanslv.test.stock.analysis.question.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 股票价格信息实体
 * @author hanslv
 *
 */
@Data
public class StockPriceInfoDto {
	private String stockId;
	private String stockCode;
	private String stockName;
	private BigDecimal startPrice;
	private BigDecimal endPrice;
	private BigDecimal lowestPrice;
}
