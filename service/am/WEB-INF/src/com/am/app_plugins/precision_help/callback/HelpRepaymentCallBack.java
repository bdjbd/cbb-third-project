package com.am.app_plugins.precision_help.callback;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;


/**
 * 农产品还款业务处理类
 * @author yuebin
 * 
 * 农产品还款后后，修改状态为已还款
 *
 */
public class HelpRepaymentCallBack extends AbstraceBusinessCallBack{
	
	private Logger logger=LoggerFactory.getLogger(getClass());

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		
		JSONObject result=new JSONObject();
		
		//1,检查交易是否成功
		if(checkTradeState(id, business, db, type)&&!checkProcessBuissnes(id, db)){
			
			JSONObject businessJS=new JSONObject(business);
			
			//更新还款日期和还款状态
			if(businessJS!=null&&businessJS.has("type")){
				String ptype=businessJS.getString("type");
				
				if(ptype!=null&&"redPackage".equals(ptype)){
					//果蔬红包感谢达恩
					logger.info("果蔬红包感恩答谢:"+businessJS.toString());
					
				}else if(ptype!=null&&"farmProduceRepayment".equals(ptype)){
					logger.info("农产品还款:"+businessJS.toString());
					//农产品还款
					String updateSQL="UPDATE mall_help_info SET repayment_status=3,repayment_time=now() WHERE id=? AND help_type=2 ";
					db.execute(updateSQL,new String[]{
							businessJS.getString("helpId")
					},new int[]{
							Type.VARCHAR
					});
				}
			}
			
			updateProcessBuissnes(id, db, "1");
		}
		
		return result.toString();
	}


}
