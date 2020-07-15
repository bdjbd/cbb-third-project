package com.am.cro.towingSettlement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 审核
 * 
 * @author 荆涛
 * @create2017/9/7 @param towing_settlement_state：拖车费结算状态
 * @param towing_settlement_audit_state：拖车费结算审核状态
 * @param towing_settlement_audit_state：拖车费结算审核状态
 * @version 拖车费结算状态 0=未结算|1=已结算，默认为0； 拖车费结算审核状态=2时，才可设置该字段=1
 *          拖车费结算审核状态0=草稿|1=待审核|2=通过审核，默认为0； 预约单设置支付状态=已支付时设置该字段=1； 预约支付状态
 *          '0'=未支付 ''1''=已支付'
 */
public class paystate extends DefaultAction {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		/**拖车费审核操作人*/
		String towing_settlement_audit_man=ac.getVisitor().getUser().getName();
		/**拖车费结算审核时间*/
		Date date = new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String towing_settlement_audit_time = format.format(date);
		// 获取拖车结算主键
		String id = ac.getRequestParameter("id");
		String checkSQL = "SELECT * FROM CRO_CARREPAIRORDER  WHERE id='" + id + "'";
		MapList map = db.query(checkSQL);
		if (!Checker.isEmpty(map)) {
			String towing_settlement_audit_state = map.getRow(0).get("towing_settlement_audit_state");
			if (towing_settlement_audit_state.equals("1")) {
				String Sql = " UPDATE CRO_CARREPAIRORDER set towing_settlement_audit_state='2',towing_settlement_audit_man='"+towing_settlement_audit_man+"',towing_settlement_audit_time='"+towing_settlement_audit_time+"' where towing_settlement_audit_state = '"
						+ towing_settlement_audit_state + "' and id='" + id + "' ";
				
				
				db.execute(Sql);
			}
		}
	}
}
