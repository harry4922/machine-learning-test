package com.hanslv.test.machine.learning.encog.util;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.Equilateral;
import org.encog.util.Format;
/**
 * 等边类Util
 * 
 * 用于对非数值类型的属性数值化，并扩大分类之间差异，降低出现误判的几率，返回预期值
 * 需要至少存在三个或三个以上类别时使用
 * 
 * 例如：
 * 类别1		期望输出值：1,-1,-1
 * 类别2		期望输出值：-1,1,-1
 * 类别3		期望输出值：-1,-1,1
 * 类别间差异最小值为：1/3
 * 
 * 优化后：
 * 类别1		期望输出值：0.8660,0.5000
 * 类别2		期望输出值：-0.8660,0.5000
 * 类别3		期望输出值：0.0000,-1.0000
 * 类别间差异最小值为：1/2
 * 
 * @author hanslv
 *
 */
public class EquilateralClassUtil {
	/**
	 * 根据类别数量、期望输出值范围获取每个类别对应的等边期望输出值Double[]
	 * @param classCount
	 * @param outputRangeStart
	 * @param outputRangeEnd
	 * @return
	 */
	public static List<Double[]> getEquilateralIdealOutput(int classCount , int outputRangeStart , int outputRangeEnd) {
		List<Double[]> equilateralIdealOutputList = new ArrayList<>();
		/*
		 * 根据给定的类别数量、期望输出值范围获取等边类
		 */
		Equilateral equilateralFactory = new Equilateral (classCount , outputRangeStart , outputRangeEnd) ;
		
		/*
		 * 获取每个类别对应的期望输出值
		 */
		for(int i = 0 ; i < classCount ; i++) {
			/*
			 * 获取当前类别对应的期望输出值数组
			 */
			double[] encodedEquilateral = equilateralFactory.encode(i);
			Double[] encodedEquilateralDouble = new Double[encodedEquilateral.length];
			for(int j = 0 ; j < encodedEquilateral.length ; j++) encodedEquilateralDouble[j] = new Double(Format.formatDouble(encodedEquilateral[j] ,4));
			equilateralIdealOutputList.add(encodedEquilateralDouble);
		}
		return equilateralIdealOutputList;
	}
}
