package com.hanslv.test.stock.analysis.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 按照月份统计股票涨跌
 * @author hanslv
 *
 */
@Data
public class StockRiseAndFallCountMonthDto {
	@ApiModelProperty(value="年份")
	private String year;
	@ApiModelProperty(value="月份")
	private String month;
	@ApiModelProperty(value="上涨数量")
	private Integer riseCount;
	@ApiModelProperty(value="下跌数量")
	private Integer fallCount;
}
