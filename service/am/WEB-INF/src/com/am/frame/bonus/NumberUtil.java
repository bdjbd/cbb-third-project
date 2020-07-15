package com.am.frame.bonus;
/**
 * <pre>
 * String,double,long之间的转换工具类
 * 
 * @comment:
 * 1,String -->double-->long 
 * 2,long -->double-->long或double-->String
 * 
 *  
 */
public class NumberUtil {
	
	public static double _d_10000 = 10000d;
	public static double _d_100 = 100d;

	/**
	 * <pre>
	 * String(30.58)转换成double(3058),
	 * 乘以10000(305800)
	 * 再转换成long(305800)
	 * @param _str
	 * @return
	 * </pre>
	 */
	public static long setDoubleToLong(String _str) {
		String str=NumberUtil.trim(_str);
		long _long = 0;
		if ("".equals(str) || "0".equals(str) || "0.0".equals(str) || "0.00".equals(str)) {
			_long = 0;
		} else {
			double _d = Double.parseDouble(str);
			_long = (long) BigDecimalUtil.mul(_d, _d_10000);
		}
		//System.out.println(_long);
		return _long;
	}

	/**
	 * <pre>
	 * long (305800,30000) 转换double
	 * 除以10000(30.58,3),如果没有小数点,再转换成long
	 * 最后再转换成String
	 * 
	 * @param _long
	 * @return
	 * </pre>
	 */
	public static String getLongToDouble(long _long) {
		String _str = "0";
		if (_long == 0) {
			_str = "0";
		} else {
			double _double = _long;
			_double = BigDecimalUtil.div(_double, _d_10000);
			
			long _long_ = _long;
			_long_ = _long_ / 100l;
			_long_ = _long_ / 100l;

			if (_double == _long_) {
				_str = String.valueOf(_long_);
			} else {
				_str = String.valueOf(_double);
			}
		}
		//System.out.println(_str);
		return _str;
	}
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		NumberUtil.getLongToDouble(3000800);
		NumberUtil.setDoubleToLong("300.88");
	}
	public static String trim(String str) {
		return ((str == null) ? "" : str.trim());
	}

}
