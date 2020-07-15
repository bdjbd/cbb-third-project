package com.cdms;

import org.apache.log4j.Logger;
import com.fastunit.context.ActionContext;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 
 * 
 * @author Administrator 远程升级 得到复选框车辆 进行传参 刘扬
 */
public class GetSomeCarId extends DefaultAction {

	Logger log = Logger.getLogger(GetSomeCarId.class);

	public ActionContext execute(ActionContext ac) throws Exception {
		// 变量ids
		String ids = "";
		
		
		String msg="无效的操作!请选择车辆进行远程升级!";
		
		// 获取列表选择列
		String[] List = ac.getRequestParameters("_s_cdms_upgrademanagement111.list");

		// 获取主键
		String[] id = ac.getRequestParameters("cdms_upgrademanagement111.list.id.k");
		if (!Checker.isEmpty(id)) {
			for (int i = 0; i < id.length; i++) {
				if ("1".equals(List[i])) {
					ids += id[i] + ",";
				}
			}
		// 截取字符串
		String ids1 = "";	
		if(ids != ""){
			ids1 = ids.substring(0, ids.length() - 1);
			ac.getActionResult().setUrl("/cdms/cdms_remoteupgrade.do?m=s&bdp_car_id=" + ids1);
		}else{
			ac.getActionResult().setUrl("/cdms/cdms_remoteupgrademanagement111.do?m=s");
			ac.getActionResult().setSuccessful(true);
			ac.getActionResult().addErrorMessage(msg);
		}
		System.err.println(ids1);
		
		System.err.println("成功传参!!!!");
		}
		return ac;
	}
}
