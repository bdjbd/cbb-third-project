package com.am.mall.commodity;

import com.fastunit.Element;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author mike
 * @create 2014年12月3日
 * @version 
 * 说明:<br />
 * 商品管理列表UI
 * 
 * MallCommoditySpecificationsList
 */
public class MallCommodityListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList data=unit.getData();
		
		if(!Checker.isEmpty(data)){

		Element editEle=unit.getElement("edit");
		Element morSetEle=unit.getElement("more_set");
		Element specLinkEle=unit.getElement("specifications_link");
			
			for(int i=0;i<data.size();i++){
				if("1".equals(data.getRow(i).get("commoditystate"))){
					//1,上架
//					editEle.setShowMode(i,ElementShowMode.CONTROL_DISABLED);
					String url="/am_bdp/mall_commodityspecifications.do?"
							+ "m=s&mall_commodity.form.id="+data.getRow(i).get("id")+"&autoback=/am_bdp/mall_commodity.do?m=s&oper=manager";
					
					editEle.setLink(i, url);
					
					//more_set  更多设置
					url="/am_bdp/mall_commodity_more_setting.do?m=s"
							+ "&mall_commodity.form.id="+data.getRow(i).get("id")
							+ "&commodity_feature.mall_class="+data.getRow(i).get("mall_class")
							+ "&commoditystate="+data.getRow(i).get("commoditystate")
							+"&autoback=/am_bdp/mall_commodity.do?m=s";
					
					morSetEle.setLink(i, url);
					
					//specifications_link 规格及价格
					url="/am_bdp/mall_commodityspecifications_fs.do?"
							+ "m=s&mall_commodity.form.id="+data.getRow(i).get("id")
							+ "&mall_class="+data.getRow(i).get("mall_class")
							+ "&commoditystate="+data.getRow(i).get("commoditystate")
							+ "&autoback=/am_bdp/mall_commodity.do?m=s";
					
					specLinkEle.setLink(i, url);
				};
			}
		}
		
		return unit.write(ac);
	}

}
