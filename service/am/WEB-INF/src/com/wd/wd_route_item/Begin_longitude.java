package com.wd.wd_route_item;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;
/***
 * 路段管理模块校验经度,经度格式为两位整数,六到十位小数
 * @author 霍凯丽
 *
 */
public class Begin_longitude implements Validator
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex)
	{
		// TODO Auto-generated method stub
		String reg = "\\d{3}\\.{1}\\d{6,10}";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(value);
		if (!matcher.matches() && !value.isEmpty())
		{
			return "无效的经度，经度格式为三位整数,六到十位小数";
		}
		return null;
	}
	@Override
	public String validate(ActionContext arg0, String arg1, String arg2,
			String arg3)
	{
		// TODO Auto-generated method stub
		return null;
	}


}

