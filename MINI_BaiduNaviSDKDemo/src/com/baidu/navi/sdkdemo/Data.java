package com.baidu.navi.sdkdemo;

public class Data{
	private static String naviInfo;//导航文字信息提取
	private static String from_note;//起始地址
	private static String to_note;//终点地址
	private static double d1;//起始点纬度
	private static double d2;//起始点经度
	private static double c1;//终点纬度
	private static double c2;//终点纬度
	public static String getNaviInfo() {
		return naviInfo;
	}

	public static void setNaviInfo(String naviInfo) {
		Data.naviInfo += naviInfo;
	}
	public static void cleanAll()
	{
		Data.naviInfo = "";
	}

	public static String getFrom_note() {
		return from_note;
	}

	public static void setFrom_note(String from_note) {
		Data.from_note = from_note;
	}

	public static String getTo_note() {
		return to_note;
	}

	public static void setTo_note(String to_note) {
		Data.to_note = to_note;
	}

	public static double getD1() {
		return d1;
	}

	public static void setD1(double d1) {
		Data.d1 = d1;
	}

	public static double getD2() {
		return d2;
	}

	public static void setD2(double d2) {
		Data.d2 = d2;
	}

	public static double getC1() {
		return c1;
	}

	public static void setC1(double c1) {
		Data.c1 = c1;
	}

	public static double getC2() {
		return c2;
	}

	public static void setC2(double c2) {
		Data.c2 = c2;
	}
	
	
	

	
}
