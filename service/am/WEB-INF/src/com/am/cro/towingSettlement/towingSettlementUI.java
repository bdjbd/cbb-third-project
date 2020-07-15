package com.am.cro.towingSettlement;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
/**
 * @author 荆涛
 * @pamar 车结算状态：towing_settlement_state;   
 *  	      拖车审核状态：towing_settlement_audit_state;
 * @see 控制列表按钮在不同审核或者结算状态下是否可点击。
 * 	            结算状态：已结算--1，未结算--0；
 * 	   	审核状态：已审核--2，未审核--1； 
 * */

public class towingSettlementUI implements UnitInterceptor {
	//打印日志
		private Logger logger = LoggerFactory.getLogger(getClass());
		private static final long serialVersionUID = 1L;
		@Override
		public String intercept(ActionContext ac, Unit unit) throws Exception {
			//获得页面数据
			MapList unitData = unit.getData();
			//循环页面数据
			for (int i = 0; i < unitData.size(); i++) {
				//获得拖车结算状态
				String towing_settlement_state = unitData.getRow(i).get("towing_settlement_state");
				//获得拖车审核状态
				String towing_settlement_audit_state = unitData.getRow(i).get("towing_settlement_audit_state");
                //当拖车结算状态为0(未结算)并且审核状态为1(待审核)不能点结算按钮
				if ("0".equals(towing_settlement_state)&&"1".equals(towing_settlement_audit_state)) {
					unit.setListSelectAttribute(i, "disabled");
				}
				//当拖车结算状态为1(已结算)不能点结算按钮
				if ("1".equals(towing_settlement_state)) {
					unit.setListSelectAttribute(i, "disabled");
				}
				//当审核状态为2(已审核)不能点审核按钮
				if ("2".equals(towing_settlement_audit_state)) {
					unit.getElement("audit_state").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
					logger.debug("-----------------------------------审核按钮已失效----------------------------------------------------");
				}
			}
			return unit.write(ac);
			}
		}
