package com.cdms;
/**
 *保险表删除，更新车辆基础信息表中的相应保险到期时间字段
 * 
 * 
 */
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.executable.Select;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class deleteTerm_Insurance extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String[] List=ac.getRequestParameters("_s_cdms_insurance.list");
		String[] Ids=ac.getRequestParameters("cdms_insurance.list.id.k");
		if(!Checker.isEmpty(Ids)){
			int i=0;
			for (String id : Ids) {
				if("1".equals(List[i])){
					String sql="delete from cdms_insurance where id='"+id+"'";
					db.execute(sql);
				}
				i++;
			}

		}
		String car_id=ac.getRequestParameter("cdms_insurance.list.car_id");
		UpdateTerm_Insurance uti=new UpdateTerm_Insurance();
		uti.upCar(db,car_id);
	}

}
