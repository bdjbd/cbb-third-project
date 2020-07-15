package com.wd;

import org.json.JSONArray;

/**
 * <p> Title: ICuai </p>
 * <p> Description:调用WebService统一接口  </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 维德科技[www.wisdeem.cn] </p>
 * 
 * @author $Author: zhouxn $
 * @version $Revision: 1.1 $
 */
public interface ICuai {
	
  	/** 
	  * 实现类WebService接口方法
	  * @return JSONArray jsonArray json类型参数
	  * @param JSONArray jsonArray json类型参数
	  */
	JSONArray doAction(JSONArray jsonArray);
}
