package com.hanslv.test.machine.learning.encog.test;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

public class TestGetMacdInfo {
	public static void main(String[] args) {
		for(String macdInfo : DbUtil.getStockMacdInfoByLimitAndBeforeDate("1" , "2019-11-10" , 100)) System.out.println(macdInfo);
	}
}
