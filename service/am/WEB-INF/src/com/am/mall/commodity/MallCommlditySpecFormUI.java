package com.am.mall.commodity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月20日
 * @version 
 * 说明:<br />
 * 商品规格Form拦截器
 */
public class MallCommlditySpecFormUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;
	

	//界面UI控制配置
	private static final String PAGE_UI_CONFIG=
			"{\"1\":{\"NAME\":\"景区\",\"SHOW_ELE\":[\"dates\",\"price\"]}, "+
			"\"2\":{\"NAME\":\"酒店\",\"SHOW_ELE\":  [\"dates\",\"price\",\"stock\"]}, "+
			"\"3\":{\"NAME\":\"餐饮\",\"SHOW_ELE\":[\"dates\",\"price\"]}, "+
			"\"4\":{\"NAME\":\"演艺\",\"SHOW_ELE\":[\"dates\",\"price\"]}, "+
			"\"5\":{\"NAME\":\"特产\",\"SHOW_ELE\": [\"name\",\"price\"]}, "+
			"\"6\":{\"NAME\":\"租车\",\"SHOW_ELE\":[\"dates\",\"stock\",\"price\"]}, "+
			"\"7\":{\"NAME\":\"优惠组合\",\"SHOW_ELE\": [\"dates\",\"price\"]}, "+
			"\"8\":{\"NAME\":\"跟团游\",\"SHOW_ELE\": [\"dates\",\"children_price\",\"price\"]}}";
	
	//界面所有序号控制的界面元素名称
	private static final String PAGE_UI_ALL_CONFIG="price,children_price,stock,dates,name";
	
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		//获取商品所属分类
		String mallClassId=ac.getRequestParameter("mall_class");
		
		if(Checker.isEmpty(mallClassId)){
			mallClassId=(String)ac.getSessionAttribute("mall_commodity_space.mall_class");
		}else{
			ac.setSessionAttribute("mall_commodity_space.mall_class",mallClassId);
		}
		
		processUnitEle(mallClassId, ac, unit);
		
		return unit.write(ac);
	}

	
	/**
	 * 根据商城分类ID处理特色设置界面元素
	 * @param mallClass
	 */
	private void processUnitEle(String mallClass,ActionContext ac, Unit unit) {
		try {
			
			String model=ac.getRequestParameter("m");
			
			if(model==null||"".equals(model)){
				model=(String)ac.getSessionAttribute("m");
			}else{
				ac.setAttribute("m", model);
			}
			
			JSONObject pageConfig=new JSONObject(PAGE_UI_CONFIG);
			//1,先全部移除元素
			for(String eleItem:PAGE_UI_ALL_CONFIG.split(",")){
				unit.getElement(eleItem).setShowMode(ElementShowMode.REMOVE);
			}
			//2,设置需要显示的元素
			JSONObject showObj=pageConfig.getJSONObject(mallClass);
			JSONArray showEle=showObj.getJSONArray("SHOW_ELE");
			
			for(int i=0;i<showEle.length();i++){
				String eleItem=showEle.getString(i);
				
				if("s".equalsIgnoreCase(model)){
					unit.getElement(eleItem).setShowMode(ElementShowMode.TEXT_VALUE);
				}else{
					unit.getElement(eleItem).setShowMode(ElementShowMode.CONTROL);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
