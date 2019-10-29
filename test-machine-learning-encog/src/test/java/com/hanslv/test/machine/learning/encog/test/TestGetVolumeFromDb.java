package com.hanslv.test.machine.learning.encog.test;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

public class TestGetVolumeFromDb {
	public static void main(String[] args) {
		for(String data : DbUtil.getDataAndVolumeMap("1" , "2019-07-26" , 25)) {
			System.out.println(data);
		}
	}
}
