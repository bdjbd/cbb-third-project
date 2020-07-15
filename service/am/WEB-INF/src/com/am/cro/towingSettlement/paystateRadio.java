package com.am.cro.towingSettlement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 * jingtao 详情页结算
 * @param towing_settlement_state：拖车费结算状态
 * @param towing_settlement_audit_state：拖车费结算审核状态
 * @param paystate：预约支付状态
 * @version 拖车费结算状态 0=未结算|1=已结算，默认为0； 拖车费结算审核状态=2时，才可设置该字段=1
 *          拖车费结算审核状态0=草稿|1=待审核|2=通过审核，默认为0； 预约单设置支付状态=已支付时设置该字段=1； 预约支付状态
 *          '0'=未支付 ''1''=已支付'
 * */
public class paystateRadio extends DefaultAction {
	private  Logger logger= LoggerFactory.getLogger(getClass());
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		logger.info("+++++++++++++请+++进+++入++++++");
		// 获得主键
		String id = ac.getRequestParameter("cro_carrepairorder.form_towing.id");
		String checkSQL = "SELECT * FROM CRO_CARREPAIRORDER  WHERE id='" + id + "'";
		MapList map = db.query(checkSQL);
		logger.info("id+++++++++++++请+++进+++入++++++"+id);
		logger.info("id+++++++++++++请+++进+++入++++++"+!Checker.isEmpty(map));
		if(!Checker.isEmpty(map)) {
			String towing_settlement_audit_state = map.getRow(0).get("towing_settlement_audit_state");
			logger.info("会员审核状态为:2=" + towing_settlement_audit_state + "时:" + ac.getVisitor().getUser().getId()
					+ "修改结算：0 改为1 ");
			if (towing_settlement_audit_state.equals("2")) {
				String SQL = " UPDATE CRO_CARREPAIRORDER set towing_settlement_state = '1'  where  id=? ";
				db.execute(SQL, id, Type.VARCHAR);
			}
		}
		
	}
}
