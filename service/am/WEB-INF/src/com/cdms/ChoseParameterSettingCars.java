package com.cdms;
import org.apache.log4j.Logger;
import com.fastunit.context.ActionContext;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 * 
 * 
 * @author Administrator
 *参数设置   得到复选框车辆  进行传参
 *刘扬
 */
public class ChoseParameterSettingCars extends DefaultAction {

	Logger log = Logger.getLogger(GetSomeCarId.class);

	public ActionContext execute(ActionContext ac) throws Exception {
		//变量ids
		String ids="";
		
		
		String msg="无效的操作!请选择车辆进行参数设置!";
		// 获取列表选择列
		String[] List = ac.getRequestParameters("_s_cdms_vehiclebasicinformationpar.list");
		
		// 获取主键
		String[] id = ac.getRequestParameters("cdms_vehiclebasicinformationpar.list.id.k");	
		
		
		if (!Checker.isEmpty(id)) {
			for (int i = 0; i < id.length; i++) {
				if ("1".equals(List[i])) {
					ids+=id[i]+",";	
				}
			}
		}
		//截取字符串
		String ids1 = "";	
		if(ids != ""){
		ids1 = ids.substring(0, ids.length() - 1);
			ac.getActionResult().setUrl("/cdms/cdms_vehiclebasicparametersettings.do?m=s&car_id="+ids1);
		}else{
//			jump="/cdms/cdms_vehiclebasicinformationpar.do?m=s";
			ac.getActionResult().setSuccessful(true);
			ac.getActionResult().setUrl("/cdms/cdms_vehiclebasicinformationpar.do?m=s");
			ac.getActionResult().addErrorMessage(msg);
		}
		
		System.err.println(ids1);
		System.err.println("成功传参!!!!");
		return ac;
	}
}
