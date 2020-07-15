package com.am.organization.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;
/**
 * 10 待审核  查看无审核通过，审核拒绝，退出申请审核拒绝，退出申请审核通过，没有退出说明
 * 11 审核拒绝
 * 12 审核通过  查看无审核通过，审核拒绝，退出申请审核拒绝，退出申请审核通过，没有退出说明，
 *    但有强制踢出按钮并且不显示申请说明，显示退出说明
 * 30 强制踢出，写明退出原因
 * 	
 *
 */
public class OrgainizationApproveUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception
	{
		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			for(int i=0;i<unitData.size();i++){
				String dataStatus=unitData.getRow(i).get("status");
//				10=待审核
//				11=审核拒绝
//				12=通过审核
//				
//				20=退出待审核
//				21=退出审核拒绝
//				22=退出审核通过
				switch (dataStatus) {
					case "10":
						
						unit.getElement("backward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("delete").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("exit_description").setShowMode(i,ElementShowMode.HIDDEN);
						
						break;
					case "11":
						
						unit.getElement("backward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("backward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("delete").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("exit_description").setShowMode(i,ElementShowMode.HIDDEN);
										
						break;
					case "12":
						
						unit.getElement("backward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("backward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("exit_description").setShowMode(i,ElementShowMode.HIDDEN);
						
						break;
					case "20":
						
						unit.getElement("backward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("add_description").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("delete").setShowMode(i,ElementShowMode.HIDDEN);
						
						break;
					case "21":
						unit.getElement("backward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("backward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("delete").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						break;
					case "22":
						unit.getElement("backward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("backward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("delete").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						break;
						
					case "30":
						unit.getElement("backward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("backward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("delete").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						unit.getElement("forward_to_exit").setShowMode(i,ElementShowMode.HIDDEN);
						break;

					default:
						break;
					}
				
		
			}
			
		}
		
		return unit.write(ac);
	        
   }
 }

