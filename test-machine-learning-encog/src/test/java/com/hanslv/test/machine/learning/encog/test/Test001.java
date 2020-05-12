package com.hanslv.test.machine.learning.encog.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test001 {
	static final String BASE_PATH = "E:\\其他\\白一帆\\2020-05-12\\baseData.txt";
	static final String IN_PATH = "E:\\其他\\白一帆\\2020-05-12\\inData.txt";
	
	
	public static void main(String[] args) {
		Map<String , String> baseInfoMap = new HashMap<>();
		Map<String , String> inInfoMap = new HashMap<>();
		
		try(FileInputStream inputStream = new FileInputStream(BASE_PATH);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream , "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){
			String buffer = null;
			while((buffer = bufferedReader.readLine()) != null) {
				String[] infoArray = buffer.split(",");
				if(infoArray.length < 1) {
					System.err.println(buffer);
					continue;
				}
				String name = infoArray[0];
				baseInfoMap.put(name , "base");
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
		
		try(FileInputStream inputStream = new FileInputStream(IN_PATH);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream , "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){
			String buffer = null;
			while((buffer = bufferedReader.readLine()) != null) {
				String[] infoArray = buffer.split(",");
				
				if(infoArray.length > 6) {
					String name = infoArray[2];
					String auth = infoArray[3];
					String code = infoArray[5];
					String cost = infoArray[6];
					inInfoMap.put(name , name + "," + auth + "," +  code + "," + cost + ",," + "上架");
				}else if(infoArray.length > 5) {
					String name = infoArray[2];
					String auth = infoArray[3];
					String code = infoArray[5];
					inInfoMap.put(name , name + "," + auth + "," + code + "," + ",," + "上架");
				}else if(infoArray.length < 3) continue;
				else {
					String name = infoArray[2];
					inInfoMap.put(name , name + ",," + "," + ",," + "上架");
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		for(Entry<String , String> entry : inInfoMap.entrySet()) {
			String key = entry.getKey();
			if(baseInfoMap.get(key) == null) {
		        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		        Matcher m = p.matcher(key);
		        if (m.find()) {
		        	System.out.println(entry.getValue());
		        }
			}
		}
		
		
//		for(Entry<String , String> entry : baseInfoMap.entrySet()) System.out.println(entry.getKey() + "," + entry.getValue());
//		for(Entry<String , String> entry : inInfoMap.entrySet()) System.out.println(entry.getKey() + "," + entry.getValue());
	}
}
