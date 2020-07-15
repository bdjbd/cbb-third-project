package com.wd.database;

/**
 * 数据库函数接口，支持数据库必须重写此接口
 * 
 * @author zhouxn
 * */
public interface IDataBase {
	/**
	 * 返回instr函数字符串
	 * 
	 * @param substr
	 *            被截取字符串
	 * @param str
	 *            截取字符串
	 * @param String
	 *            拼接截取字符串
	 * */
	String getInstrStr(String substr, String str);

	/**
	 * 返回Date函数字符串
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param formatStr
	 *            格式化字符串
	 * @param String
	 *            拼接to_date后的字符串
	 * */
	String getTo_DateStr(String dateStr, String formatStr);

	/**
	 * 获取数据库当前时间字符串
	 * 
	 * @return String 当前时间函数字符串
	 * */
	String getSysdateStr();

	/**
	 * 获取第一条字符串
	 * 
	 * @return String 取得第一条数据的函数字符串
	 * */
	String getTop1Str();

	/**
	 * 获取to_number字符串
	 * 
	 * @param str
	 *            数字字符串
	 * @param formatStr
	 *            格式化字符串
	 * @return String 拼接数字格式化函数字符串
	 * */
	String getTo_NumberStr(String str, String formatStr);

	/**
	 * 获取计算与当前时间差字符串
	 * 
	 * @param str
	 *            时间字符串
	 * @return String 返回计算与当前时间差字符串
	 * */
	String getDateTimeDifferenceStr(String str);
}
