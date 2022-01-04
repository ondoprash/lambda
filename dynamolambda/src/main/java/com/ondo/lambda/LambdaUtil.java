package com.ondo.lambda;

import java.text.DecimalFormat;

public class LambdaUtil {

	static float compTemp = 0;
	static final float lowerThreshold = (float) 30.0;
	static final float middleThreshold = (float) 34.5;
	static final float higherThreshold = (float) 36.5;

	static final float minimumCompensation = (float) 1.5;
	static final float middleCompensation = (float) 2.5;
	static final float maximumCompensation = (float) 6.5;
	
	//Any change here must be reflected in OndoServiceUtil
	static final double indeterminateCondition = 0.9f;

	 

	/*
	 * public static float compensateTemperature(float currentTemp) {
	 * 
	 * if (currentTemp == lowerThreshold) { compTemp = maximumCompensation; } else
	 * if (currentTemp > lowerThreshold && currentTemp < higherThreshold) { compTemp
	 * = (float) (maximumCompensation - ((maximumCompensation - minimumCompensation)
	 * / (higherThreshold - lowerThreshold)) (currentTemp - lowerThreshold)); } else
	 * if (currentTemp >= higherThreshold) { compTemp = minimumCompensation; }
	 * return compTemp <= 0 ? 0 : compTemp;
	 * 
	 * }
	 */

	public static float compensateTemperatureV2(float currentTemp) {

		if (currentTemp == lowerThreshold) {
			compTemp = maximumCompensation;
		} else if (currentTemp > lowerThreshold && currentTemp <= middleThreshold) {
			compTemp = (float) (maximumCompensation
					- ((maximumCompensation - middleCompensation) / (middleThreshold - lowerThreshold))
							* (currentTemp - lowerThreshold));
		} else if (currentTemp > middleThreshold && currentTemp <= higherThreshold) {
			compTemp = (float) (middleCompensation
					- ((middleCompensation - minimumCompensation) / (higherThreshold - middleThreshold))
							* (currentTemp - middleThreshold));
		} else if (currentTemp > higherThreshold) {
			compTemp = minimumCompensation;
		}
		return compTemp <= 0 ? 0 : compTemp;

	}

	public static float celsiusToFahrenheit(float celsiusTemp) {

		return (celsiusTemp * 9 / 5) + 32;

	}

	public static void main(String[] args) {

		/*
		 * curTempD 31.7 curTempFt 4.9888883 curTempFh 40.98
		 */
		/*
		 * double curTempD = 31.7d;
		 * 
		 * System.out.println("curTempD " + curTempD); // Compensate Temperature in
		 * float float curTempFt = (float) curTempD +
		 * OndoServiceUtil.compensateTemperatureV2((float) curTempD);
		 * 
		 * System.out.println("curTempFt " + curTempFt); // Convert float temperature in
		 * Fahrenheit float curTempFh = OndoServiceUtil.celsiusToFahrenheit(curTempFt);
		 * 
		 * System.out.println(curTempFh);
		 * 
		 * System.out.println(twodigit.format(curTempFh));
		 * 
		 * System.out.println("curTempFh " + curTempFh);
		 */
		Float curTemp=37.0f;
		Float ambTemp=37.7f;
	//	Float alertThreshold=36.5f;
		
		//boolean variable=isDeterminateTemperature(curTemp, ambTemp, alertThreshold);
		
		boolean variable=isDeterminateTemperatureFloat(curTemp, ambTemp);
		
		System.out.println(variable);
		
		
	}
	
	/*
	 * Notable state �indeterminate� takes place when
	 * 
	 * Wrist temperature meets the alert criteria (by threshold or baseline
	 * deviation) &&
	 * 
	 * WAD <= 0.7 �C
	 */
	//Below logic is USED BY API , any logic change here must also be done at OndoServiceUtil 
	public static boolean isDeterminateTemperature(Double curTemp, Double ambTemp, Double alertThreshold) {

		DecimalFormat numberFormat = new DecimalFormat("#.#");
		
		System.out.println("cur Temp " + curTemp);
		System.out.println("amb Temp " + ambTemp);

		if (curTemp != null && ambTemp != null && alertThreshold != null) {

			if (curTemp < alertThreshold) {
				return true; // determinate
			} else {
				// double diff = Math.abs(curTemp - ambTemp);

				if (Double.valueOf(numberFormat.format(Math.abs(curTemp - ambTemp))) <= indeterminateCondition) {
					return false; // Not determinate
				} else {
					return true; // determinate
				}
			}
		} else
			return false;
	}
	
	
	
	//User by Lambda for Warning Temperature

	public static boolean isDeterminateTemperatureFloat(Float curTemp, Float ambTemp) {

		DecimalFormat numberFormat = new DecimalFormat("#.#");
		
		System.out.println("cur Temp " + curTemp);
		System.out.println("amb Temp " + ambTemp);

		if (curTemp != null && ambTemp != null ) {

				if (Double.valueOf(numberFormat.format(Math.abs(curTemp - ambTemp))) <= indeterminateCondition) {
					return false; // Not determinate
				} else {
					return true; // determinate
				}
			
		} else
			return false;
	}
	

}
