package com.hanslv.test.machine.learning.encog.test;

import java.util.List;

import com.hanslv.test.machine.learning.encog.util.EquilateralClassUtil;

public class TestEquilateralClassUtil {
	public static void main(String[] args) {
		List<Double[]> resultList = EquilateralClassUtil.getEquilateralIdealOutput(3 , -1 , 1);
		for(Double[] resultArray : resultList) {
			for(Double result : resultArray) System.out.print(result + ",");
			System.out.println();
		}
	}
}
