package com.am.frame.order.use.ui;

import com.fastunit.Element;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月26日
 * @version 
 * 说明:<br />
 */
public class OrderUseFormUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		String use=ac.getRequestParameter("use");
		
		//订单状态
		String orderState=ac.getRequestParameter("orderstate");
		
		//按钮控制
		if("true".equals(use)){
			if("320".equals(orderState)){
				
			}else{
				unit.getElement("save").setShowMode(ElementShowMode.REMOVE);
			}
		}else{
			unit.getElement("save").setShowMode(ElementShowMode.REMOVE);
		}
		
		//界面元素控制
		MapList mapData=unit.getData();
		
		Element checkInTimeEle=unit.getElement("check_in_time");
		Element leaveTimeEle=unit.getElement("leave_time");
		
		//预计到店时间
		Element expectArrivalTime=unit.getElement("expect_arrival_time");
		//酒店预订方式
		Element reserveType=unit.getElement("reserve_type");
		
		if(!Checker.isEmpty(mapData)){
			String mallClass=mapData.getRow(0).get("mall_class");
			//1,酒店需要显示，入住时间 ，离店时间，
			//2，车行需要显示用车开始时间，交车结束时间
			if("2".equals(mallClass)){
				//酒店
				checkInTimeEle.setName("入住时间");
				leaveTimeEle.setName("离店时间");
			}
			if("6".equals(mallClass)){
				//车行
				checkInTimeEle.setName("开始用车时间");
				leaveTimeEle.setName("结束用车时间");
				
				expectArrivalTime.setShowMode(ElementShowMode.REMOVE);
				reserveType.setShowMode(ElementShowMode.REMOVE);
			}
			
			
			
		}
		
		
		
		
		
		
		
		return unit.write(ac);
	}

}
