package com.am.mall.commodity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.Element;
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
 * 规格批量生成数据单元拦截器
 */
public class BatchExecuteDataOperationUnitUI implements UnitInterceptor {

private static final long serialVersionUID = 1L;
	
	//界面UI控制配置
	private static final String PAGE_UI_CONFIG=
			"{\"1\":{\"NAME\":\"景区\",\"SHOW_ELE\":[\"execute_date\",\"intput_start_date\",\"intput_end_date\",\"intput_data_type\",\"intput_price\"]}, "+
			"\"2\":{\"NAME\":\"酒店\",\"SHOW_ELE\":  [\"execute_date\", \"intput_store\",\"intput_start_date\",\"intput_end_date\",\"intput_data_type\",\"intput_price\"]}, "+
			"\"3\":{\"NAME\":\"餐饮\",\"SHOW_ELE\":[\"execute_date\", \"intput_start_date\",\"intput_end_date\",\"intput_data_type\",\"intput_price\"]}, "+
			"\"4\":{\"NAME\":\"演艺\",\"SHOW_ELE\":[\"execute_date\", \"intput_start_date\",\"intput_end_date\",\"intput_data_type\",\"intput_price\"]}, "+
			"\"5\":{\"NAME\":\"特产\",\"SHOW_ELE\": []}, "+
			"\"6\":{\"NAME\":\"租车\",\"SHOW_ELE\":[\"execute_date\",\"intput_store\", \"intput_start_date\",\"intput_end_date\",\"intput_data_type\",\"intput_price\"]}, "+
			"\"7\":{\"NAME\":\"优惠组合\",\"SHOW_ELE\": [\"execute_date\", \"intput_start_date\",\"intput_end_date\",\"intput_data_type\",\"intput_price\"]}, "+
			"\"8\":{\"NAME\":\"跟团游\",\"SHOW_ELE\": [\"execute_date\", \"input_children_price\",\"intput_start_date\",\"intput_end_date\",\"intput_data_type\",\"intput_price\"]}}";
	
	//界面所有序号控制的界面元素名称
	private static final String PAGE_UI_ALL_CONFIG="intput_start_date,intput_end_date,"
			+ "intput_price,execute_date,input_children_price,intput_data_type,intput_store";
	

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		//商品上架状态
		String state=ac.getRequestParameter("commoditystate");
		
		if(Checker.isEmpty(state))
		{
			state  = (String)ac.getSessionAttribute("commoditystate");
		}else
		{
			ac.setSessionAttribute("commoditystate",state);
		}
		
		//获取商品所属分类
		String mallClassId=ac.getRequestParameter("mall_class");
		
		if(Checker.isEmpty(mallClassId)){
			mallClassId=(String)ac.getSessionAttribute("mall_commodity_space.mall_class");
		}else{
			ac.setSessionAttribute("mall_commodity_space.mall_class",mallClassId);
		}
		//开始日期
		Element intputStartDateEle=unit.getElement("intput_start_date");
		//结束日期
		Element intputEndDateEle=unit.getElement("intput_end_date");
		//类型
		Element intputDataTypeEle=unit.getElement("intput_data_type");
		//价格
		Element intputPriceEle=unit.getElement("intput_price");
		//库存数量
		Element intputStoreEle=unit.getElement("intput_store");
		//儿童价
		Element inputChildrenPriceEle=unit.getElement("input_children_price");
		
		//星期
		Element weeksEle=unit.getElement("weeks");
		//生成数据
		Element executeDateBtn=unit.getElement("execute_date");
		
		
		if("1".equals(state)){
			if(intputStartDateEle!=null){
				intputStartDateEle.setShowMode(ElementShowMode.REMOVE);
			}
			if(intputEndDateEle!=null){
				intputEndDateEle.setShowMode(ElementShowMode.REMOVE);
			}
			if(intputDataTypeEle!=null){
				intputDataTypeEle.setShowMode(ElementShowMode.REMOVE);
			}
			if(intputPriceEle!=null){
				intputPriceEle.setShowMode(ElementShowMode.REMOVE);
			}
			if(intputStoreEle!=null){
				intputStoreEle.setShowMode(ElementShowMode.REMOVE);
			}
			if(weeksEle!=null){
				weeksEle.setShowMode(ElementShowMode.REMOVE);
			}
			if(executeDateBtn!=null){
				executeDateBtn.setShowMode(ElementShowMode.REMOVE);
			}
			if(inputChildrenPriceEle!=null){
				inputChildrenPriceEle.setShowMode(ElementShowMode.REMOVE);
			}
			
		}else{
			processUnitEle(mallClassId, ac, unit);
		}
		
		
		
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
				if(unit.getElement(eleItem)!=null){
					unit.getElement(eleItem).setShowMode(ElementShowMode.REMOVE);
				}
			}
			//2,设置需要显示的元素
			JSONObject showObj=pageConfig.getJSONObject(mallClass);
			JSONArray showEle=showObj.getJSONArray("SHOW_ELE");
			
			for(int i=0;i<showEle.length();i++){
				String eleItem=showEle.getString(i);
				if(unit.getElement(eleItem)!=null){
					unit.getElement(eleItem).setShowMode(ElementShowMode.CONTROL);
					if(eleItem.equals("dates_week")){
						unit.getElement(eleItem).setShowMode(ElementShowMode.TEXT);
					}
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
