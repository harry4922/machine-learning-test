package com.hanslv.test.machine.learning.encog.test;

import java.util.ArrayList;
import java.util.List;

public class TestSubList {
	public static void main(String[] args) {
		List<Integer> testList = new ArrayList<>();
		for(int i = 1 ; i <= 100 ; i ++) testList.add(i);
		List<Integer> testSubListA = testList.subList(0 , 50);
		List<Integer> testSubListB = testList.subList(50 , 100);
		
		for(Integer test : testSubListA) System.out.println(test);
		System.out.println("-----------------------------------------------------");
		for(Integer test : testSubListB) System.out.println(test);
	}
}
