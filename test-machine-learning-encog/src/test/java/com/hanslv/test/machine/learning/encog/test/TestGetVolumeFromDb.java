package com.hanslv.test.machine.learning.encog.test;

import java.util.List;

import com.hanslv.test.machine.learning.encog.util.DbUtil;

public class TestGetVolumeFromDb {
	public static void main(String[] args) {
		for(List<String> list : DbUtil.getDataAndVolumeMap("1" , "" , "25")) {
			System.out.println("----------------------------------------------");
			for(String data : list) System.out.println(data);
		}
	}
}
