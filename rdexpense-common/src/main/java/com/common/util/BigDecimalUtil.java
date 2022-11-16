package com.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimal计算方法的工具类
 * @author rdexpense
 */
public class BigDecimalUtil {

	private static int defaultScale = 12;//默认精度

	/**
	 * 提供精确加法计算的add方法
	 * @param value1            被加数
	 * @param value2            加数
	 * @return 两个参数的和
	 * @throws Exception
	 */
	public static double add(Object value1, Object value2) throws Exception {
		boolean result = validateParam(value1, value2);
		if(!result){
			throw new Exception("参数验证未通过,存在不能参与计算的参数！");
		}
		BigDecimal b1 = new BigDecimal(String.valueOf(value1));
		BigDecimal b2 = new BigDecimal(String.valueOf(value2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确减法运算的sub方法
	 * @param value1            被减数
	 * @param value2            减数
	 * @return 两个参数的差
	 * @throws Exception
	 */
	public static double sub(Object value1, Object value2) throws Exception {
		boolean result = validateParam(value1, value2);
		if(!result){
			throw new Exception("参数验证未通过,存在不能参与计算的参数！");
		}
		BigDecimal b1 = new BigDecimal(String.valueOf(value1));
		BigDecimal b2 = new BigDecimal(String.valueOf(value2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确乘法运算的mul方法
	 * @param value1            被乘数
	 * @param value2            乘数
	 * @return 两个参数的积
	 * @throws Exception
	 */
	public static double mul(Object value1, Object value2) throws Exception {
		boolean result = validateParam(value1, value2);
		if(!result){
			throw new Exception("参数验证未通过,存在不能参与计算的参数！");
		}
		BigDecimal b1 = new BigDecimal(String.valueOf(value1));
		BigDecimal b2 = new BigDecimal(String.valueOf(value2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供精确的除法运算方法div
	 * @param value1    被除数
	 * @param value2    除数
	 * @param scale    精度
	 * @param roundingMode    默认四舍五入模式
	 * @return 两个参数的商
	 * @throws Exception
	 */
	public static double div(Object value1, Object value2, int scale,RoundingMode roundingMode)
			throws Exception {
		// 如果精确范围小于0，抛出异常信息
		if (scale < 0) {
			throw new IllegalAccessException("精确度不能小于0");
		}
		if(roundingMode==null){
			roundingMode = RoundingMode.HALF_UP;
		}

		boolean result = validateParam(value1, value2);
		if(!result){
			throw new Exception("参数验证未通过,存在不能参与计算的参数！");
		}

		BigDecimal b1 = new BigDecimal(String.valueOf(value1));
		BigDecimal b2 = new BigDecimal(String.valueOf(value2));
		return b1.divide(b2, scale,roundingMode).doubleValue();
	}

	/**
	 * 提供精确的除法运算方法div
	 * @param value1            被除数
	 * @param value2            除数
	 * @param scale            精确范围
	 * @return 两个参数的商
	 * @throws Exception
	 */
	public static double div(Object value1, Object value2, int scale) throws Exception{
		return div(value1, value2, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 提供精确的除法运算方法div 使用默认12位精度，默认四舍五入模式
	 * @param value1            被除数
	 * @param value2            除数
	 * @return 两个参数的商
	 * @throws Exception
	 */
	public static double div(Object value1, Object value2) throws Exception{
		return div(value1, value2, defaultScale, RoundingMode.HALF_UP);
	}

	/**
	 * 验证参数
	 * @param value1
	 * @param value2
	 * @return
	 */
	private static boolean validateParam(Object value1, Object value2){
		System.out.println("参与计算参数：value1="+value1+",value2="+value2);
		boolean validateResult = false;
		if(value1==null||value2==null){
			System.out.println("参与计算参数有空值!");
			return false;
		}

		if((value1 instanceof Integer||value1 instanceof Double
				||value1 instanceof Float||value1 instanceof Long)&&
				(value2 instanceof Integer||value2 instanceof Double
						||value2 instanceof Float||value2 instanceof Long)){
			validateResult = true;
		}
		return validateResult;
	}

	/**
	 * 调整值精度
	 * @param value 值
	 * @param scale 精度
	 * @param roundingMode 无设置默认四舍五入类型
	 * @return
	 */
	public static double format(double value, int scale,RoundingMode roundingMode){
		if(roundingMode==null){
			roundingMode = RoundingMode.HALF_UP;
		}
		BigDecimal b = new BigDecimal(String.valueOf(value));
		return b.setScale(scale, roundingMode).doubleValue();
	}

	/**
	 * 调整值精度 默认四舍五入
	 * @param value 值
	 * @param scale 精度
	 * @return
	 */
	public static double format(double value, int scale){
		return format(value, scale, null);
	}



	public static void main(String[] args) throws Exception{
		System.out.println(0.06+0.01);
		System.out.println(1.0-0.42);
		System.out.println(4.015*100);
		System.out.println(10/3);

		System.out.println(add(-1, 1000));
		System.out.println(sub(1.0, 0.42));
		System.out.println(mul(4.015, 100));
		System.out.println(div(10, 3));

		System.out.println(format(4.0146, 3));


	}
}
