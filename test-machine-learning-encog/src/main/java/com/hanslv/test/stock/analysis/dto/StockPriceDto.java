package com.hanslv.test.stock.analysis.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 股票价格DTO
 * @author hanslv
 *
 */
@Data
public class StockPriceDto {
	@ApiModelProperty(value="股票ID")
	private String stockId;
	@ApiModelProperty(value="股票名称")
	private String stockName;
	@ApiModelProperty(value="股票代码")
	private String stockCode;
	@ApiModelProperty(value="当前日期")
	private String currentDate;
	@ApiModelProperty(value="开盘价")
	private BigDecimal startPrice;
	@ApiModelProperty(value="收盘价")
	private BigDecimal endPrice;
	@ApiModelProperty(value="最低价")
	private BigDecimal lowestPrice;
	@ApiModelProperty(value="最高价")
	private BigDecimal highestPrice;
	@ApiModelProperty(value="昨日收盘价")
	private BigDecimal lastDayEndPrice;
	@ApiModelProperty(value="涨跌")
	private String riseRate;
}
