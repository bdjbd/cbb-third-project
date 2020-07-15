package com.am.organization.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;
/**
 * 后台组织机构申请加入form表单UI
 *
 */
public class OrgainzationFormUI implements UnitInterceptor{

	private static final long serialVersionUID = 1L;
	
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData=unit.getData();
		
		if(!Checker.isEmpty(unitData)){
			//状态
			//10=待审核  =>审核拒绝，审核通过
			//11=审核拒绝=> 
			//12=通过审核 =>强制踢出
			//20=退出待审核=>
			//21=退出审核拒绝=>
			//22=退出审核通过=>
			//30=强制踢出=>
			String status=unitData.getRow(0).get("status");
			switch (status){
				case "10":
					unit.getElement("save").setShowMode(ElementShowMode.REMOVE);//保存
					unit.getElement("exit_description").setName("拒绝原因");
//					unit.getElement("backward_to_exit").setShowMode(ElementShowMode.REMOVE);//backward_to_exit 退出申请审核拒绝
//					unit.getElement("forward_to_exit").setShowMode(ElementShowMode.REMOVE);//forward_to_exit 退出申请审核通过
					unit.getElement("remove").setShowMode(ElementShowMode.REMOVE);//delete 强制踢出
//					ac.getActionResult().setUrl("/am_bdp/lxny_organizational_relationshi.do?m=s");
				 break;
				case "11":
					unit.getElement("save").setShowMode(ElementShowMode.REMOVE);//保存
					unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);//审核拒绝
//					unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);//审核拒绝
					unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);//forward 审核通过
//					unit.getElement("backward_to_exit").setShowMode(ElementShowMode.REMOVE);//backward_to_exit 退出申请审核拒绝
//					unit.getElement("forward_to_exit").setShowMode(ElementShowMode.REMOVE);//forward_to_exit 退出申请审核通过
					unit.getElement("remove").setShowMode(ElementShowMode.REMOVE);//delete 强制踢出
					 break;
				case "12":
					unit.getElement("save").setShowMode(ElementShowMode.REMOVE);//保存
					unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);//审核拒绝
					unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);//forward 审核通过
//			        unit.getElement("backward_to_exit").setShowMode(ElementShowMode.REMOVE);//backward_to_exit 退出申请审核拒绝
//					unit.getElement("forward_to_exit").setShowMode(ElementShowMode.READONLY);//forward_to_exit 退出申请审核通过
					unit.getElement("add_description").setShowMode(ElementShowMode.READONLY);
					unit.getElement("joining_mechanism_id").setShowMode(ElementShowMode.READONLY);
					unit.getElement("status").setShowMode(ElementShowMode.READONLY);
					break;
				case "20":
					//unit.getElement("exit").setShowMode(ElementShowMode.READONLY);
					unit.getElement("save").setShowMode(ElementShowMode.REMOVE);//保存
//					unit.getElement("backward_to_exit").setShowMode(ElementShowMode.REMOVE);//backward_to_exit 退出申请审核拒绝
//					unit.getElement("forward_to_exit").setShowMode(ElementShowMode.REMOVE);//forward_to_exit 退出申请审核通过
					unit.getElement("remove").setShowMode(ElementShowMode.REMOVE);//delete 强制踢出
					 break;
				case "21":
//					unit.getElement("save").setShowMode(ElementShowMode.REMOVE);//保存
//					unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);//审核拒绝
//					unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);//审核拒绝
//					unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);//forward 审核通过
//					unit.getElement("backward_to_exit").setShowMode(ElementShowMode.REMOVE);//backward_to_exit 退出申请审核拒绝
//					unit.getElement("forward_to_exit").setShowMode(ElementShowMode.REMOVE);//forward_to_exit 退出申请审核通过
//					unit.getElement("delete").setShowMode(ElementShowMode.REMOVE);//delete 强制踢出
					 break;
			    case "22":
//					unit.getElement("save").setShowMode(ElementShowMode.REMOVE);//保存
//					unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);//审核拒绝
//					unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);//审核拒绝
//					unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);//forward 审核通过
//					unit.getElement("backward_to_exit").setShowMode(ElementShowMode.REMOVE);//backward_to_exit 退出申请审核拒绝
//					unit.getElement("forward_to_exit").setShowMode(ElementShowMode.REMOVE);//forward_to_exit 退出申请审核通过
//					unit.getElement("delete").setShowMode(ElementShowMode.REMOVE);//delete 强制踢出
      				 break;
				case "30":
					unit.getElement("save").setShowMode(ElementShowMode.REMOVE);//保存
					unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);//审核拒绝
					unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);//审核拒绝
					unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);//forward 审核通过
//					unit.getElement("backward_to_exit").setShowMode(ElementShowMode.REMOVE);//backward_to_exit 退出申请审核拒绝
//					unit.getElement("forward_to_exit").setShowMode(ElementShowMode.REMOVE);//forward_to_exit 退出申请审核通过
					unit.getElement("remove").setShowMode(ElementShowMode.REMOVE);//delete 强制踢出
//					ac.getActionResult().setUrl("/am_bdp/lxny_organizational_relationshi.do?m=s");
					 break;
					 
			}
		}
//		else{
//			//没有数据，则为新增模式，新增时，还有保存按钮和返回按钮
//
//			unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);//forward 审核通过
//			unit.getElement("backward_to_exit").setShowMode(ElementShowMode.REMOVE);//backward_to_exit 退出申请审核拒绝
//			unit.getElement("forward_to_exit").setShowMode(ElementShowMode.REMOVE);//forward_to_exit 退出申请审核通过
//			unit.getElement("delete").setShowMode(ElementShowMode.REMOVE);//delete 强制踢出
//		}
		
		
		
		return unit.write(ac);
	}
	

	}
