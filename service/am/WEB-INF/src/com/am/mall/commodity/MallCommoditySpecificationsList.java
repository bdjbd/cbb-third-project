package com.am.mall.commodity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.Element;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2014年12月3日
 * @version 
 * 说明:<br />
 * 商品维护，规格UI
 * &commoditystate=$D{commoditystate}
 * 
一、特色设置
1、即不同商城分类中商品主表的特色字段
商品表：当前库存数量（用于列表展示）、所属商城分类
订单表：支付时间限制、物流单号、物流公司

2、景区
商品表添加字段：扫码进、支持退、有效期区间
订单表添加字段：购买数量
规格：日期 ["datas"]
3、酒店
商品表：含早餐、不可取消、入住保障
订单表：入住时间、离店时间、房间数、预计到店时间、联系人、联系电话、酒店预订方式（在线预订/在线支付）
规格表：库存数量、日期 ["stock","datas"]
4、餐饮
商品表：可叠加、有效期区间
订单表：有效期
规格：日期  ["datas"]
5、演艺
商品表：场次、席位
订单表：演出日期、场次、席位
规格表：日期  ["datas"]
6、租车
商品表：所属车型分类、 租车类型（包车/租车）、车型标签、座位数标签、价格标签
订单表：用车数量、联系人、联系电话
规格表：库存数量 ["stock"]
7、跟团游
商品表：我要玩标签、天数标签、铁定成团、提前几天预订、呼旅点评、每周日(文本框)、启差说明(文本框)
订单表：出发日期、旅客信息（子表：姓名、身份证号、联系电话）、联系人、联系电话、备注
规格表：儿童价格、日期  ["children_price","datas"] 

8、优惠组合
商品表：主题标签、组合标签、选择商品（使用配套推荐实现）
规格表：日期 ["datas"]
9、特产
标准商城，现有字段即可[]

二、介绍设置
直接使用店铺的单元

三、价格设置
1、价格设置即原规格设置
2、增加批量添加数据的功能
2.1、选择日期区间，选择需要添加的规则，整周或周几（可多选），设定价格
2.2、点击添加后，删除所选择的已有记录，在插入即可
2.2、按照商城分类控制界面，跟团游时多一个儿童价格
3、增加价格日期区间搜索、名称搜索功能 
4、按照商城分类控制界面
4.1、场次、席位，用产品实现即可

四、套餐设置
迁移现有的

五、商品推荐
迁移现有的

六、邮费设置
1、迁移现有的
2、快递名称修改为快递公司，并且是下拉框

七、周边推荐
使用店铺的单元

八、图集设置
使用店铺的单元
 */
public class MallCommoditySpecificationsList implements UnitInterceptor {

	private static final long serialVersionUID = 1L;
	
	//界面UI控制配置
	private static final String PAGE_UI_CONFIG=
			"{\"1\":{\"NAME\":\"景区\",\"SHOW_ELE\":[ \"dates_week\"]}, "+
			"\"2\":{\"NAME\":\"酒店\",\"SHOW_ELE\":  [ \"dates_week\"]}, "+
			"\"3\":{\"NAME\":\"餐饮\",\"SHOW_ELE\":[ \"dates_week\"]}, "+
			"\"4\":{\"NAME\":\"演艺\",\"SHOW_ELE\":[ \"dates_week\"]}, "+
			"\"5\":{\"NAME\":\"特产\",\"SHOW_ELE\": [\"name\"]}, "+
			"\"6\":{\"NAME\":\"租车\",\"SHOW_ELE\":[\"dates_week\"]}, "+
			"\"7\":{\"NAME\":\"优惠组合\",\"SHOW_ELE\": [ \"dates_week\"]}, "+
			"\"8\":{\"NAME\":\"跟团游\",\"SHOW_ELE\": [ \"dates_week\"]}}";
	
	//界面所有序号控制的界面元素名称
	private static final String PAGE_UI_ALL_CONFIG="dates_week,name";
	

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList map=unit.getData();
		
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
		
		//删除
		Element deleBtn=unit.getElement("delete");
		//新增
		Element newBtn=unit.getElement("new");
		
		
		if("1".equals(state)){
			if(deleBtn!=null){
				deleBtn.setShowMode(ElementShowMode.REMOVE);
			}
			if(newBtn!=null){
				newBtn.setShowMode(ElementShowMode.REMOVE);
			}
			
		}
		
		processUnitEle(mallClassId, ac, unit);
		
		
		
		if(!Checker.isEmpty(map)){
			
			Element editBtn=unit.getElement("edit");
			if("1".equals(state)){
				//如果上架，编辑菜单不可使用
				editBtn.setShowMode(ElementShowMode.CONTROL_DISABLED);
			}
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
					unit.getElement(eleItem).setShowMode(ElementShowMode.TEXT_VALUE);
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
