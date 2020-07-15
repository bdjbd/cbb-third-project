package com.am.borrowing.subsidize;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
/**
 * 资助大学生回调类
 *
 */
public class SubsidizeStudentCallback extends AbstraceBusinessCallBack{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		
		//MALL_COLLEGE_STUDENT_AID  
		JSONObject result=new JSONObject();
		
		if(checkTradeState(id, business, db, type)&&!checkProcessBuissnes(id, db)){
			
			JSONObject businessJS=new JSONObject(business);
			
			String upateSQL="UPDATE mall_college_student_aid SET status=? WHERE id=? ";
			
			db.execute(upateSQL,new String[]{
					businessJS.getString("start"),businessJS.getString("uuid")
			},new int[]{Type.INTEGER,Type.VARCHAR});
			
		}
		
		return result.toString();
	}

}
