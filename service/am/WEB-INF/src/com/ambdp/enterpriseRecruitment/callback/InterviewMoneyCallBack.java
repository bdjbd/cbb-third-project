package com.ambdp.enterpriseRecruitment.callback;

import org.json.JSONObject;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;

/**
 * @author 作者：qintao
 * @date 创建时间：2016年10月25日 下午3:11:40
 * @explain 说明 : 应聘手续费支付回调
 */

public class InterviewMoneyCallBack extends AbstraceBusinessCallBack {

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		
		JSONObject reuslt=new JSONObject();
		
		if(checkTradeState(id, business, db, type)&&!checkProcessBuissnes(id, db)){
			//检查业务是否处理，如果没有处理，处理业务
			JSONObject businessJS=new JSONObject(business);
			Table table=new Table("am_bdp", "LXNY_MYAPPLICATION");
			TableRow insertTr=table.addInsertRow();
			
			insertTr.setValue("enterprise_recruitment_id", businessJS.getString("id"));
			insertTr.setValue("status","0");//应聘状态  0=待处理，1=通过，2=驳回;
			insertTr.setValue("member_id",businessJS.getString("memberid"));
			insertTr.setValue("content",businessJS.getString("content"));
			insertTr.setValue("applicant_phone",businessJS.getString("applicant_phone"));
			
			db.save(table);
			
			updateProcessBuissnes(id, db, "1");
		}
		return reuslt.toString();
	}

}
