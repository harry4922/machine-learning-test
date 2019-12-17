package com.hanslv.test.machine.learning.dl4j.stock.rectangle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import com.hanslv.test.machine.learning.dl4j.stock.rectangle.RectangleLSTMTrainer.ForecastResult;

public class RectangleLSTMTrainerTest {
	static LocalDate endDate = LocalDate.parse("2019-11-22");
	static int trueCount;
	static int mainCount;
	
	public static void main(String[] args) {
		for(int i = 1 ; i < 3550 ; i++) {
			ForecastResult result = RectangleLSTMTrainer.train(i + "", endDate.toString());
			if(result == ForecastResult.TRUE) {
				mainCount++;
				trueCount++;
			}else if(result == ForecastResult.FALSE)mainCount++;
			
			if(result != ForecastResult.EXCLUDE)
				System.out.println(new BigDecimal(trueCount).divide(new BigDecimal(mainCount) , 2 , BigDecimal.ROUND_HALF_UP));
			try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
}
