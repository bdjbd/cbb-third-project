package com.am.cro.towingSettlement;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author 荆涛  列表页 多选结算
 * @create 2017/9/7 
 * @param towing_settlement_state：拖车费结算状态
 * @param towing_settlement_audit_state：拖车费结算审核状态
 * @param paystate：预约支付状态
 * @version 拖车费结算状态 0=未结算|1=已结算，默认为0； 拖车费结算审核状态=2时，才可设置该字段=1
 *          拖车费结算审核状态0=草稿|1=待审核|2=通过审核，默认为0； 预约单设置支付状态=已支付时设置该字段=1； 预约支付状态
 *          '0'=未支付 ''1''=已支付'
 */
public class towingSettlement extends DefaultAction {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		/**拖车费结算操作人*/
		String towing_settlement_man=ac.getVisitor().getUser().getName();
		logger.info("获取拖车费结算操作人towing_settlement_man的" + towing_settlement_man);
		/**拖车费结算时间*/
		Date date = new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String towing_settlement_time = format.format(date);
		logger.info("获取拖车费结算操作人towing_settlement_man的" + towing_settlement_time);
		// 获得选择列: _s_单元编号
		String[] select = ac.getRequestParameters("_s_cro_carrepairorder.list_towing");
		// 获得主键: 单元编号.元素编号.k（设置了映射表时会自动用隐藏域记录主键值）
		String[] id = ac.getRequestParameters("cro_carrepairorder.list_towing.id.k");
		if (!Checker.isEmpty(select)) {
			for (int i = 0; i < select.length; i++) {
				if ("1".equals(select[i])) {// 1为选中
					// 需结算的的会员ID
					String ids = id[i];
					String checkSQL="SELECT * FROM CRO_CARREPAIRORDER  WHERE id='"+ids+"'";
					MapList map = db.query(checkSQL);
					String towing_settlement_audit_state = map.getRow(0).get("towing_settlement_audit_state");
					logger.info("获取当前勾选的" + ids);
					logger.info("会员审核状态为:2=" + towing_settlement_audit_state + "时:" + ac.getVisitor().getUser().getId()
							+ "修改结算：0 改为1 ");
					if (towing_settlement_audit_state.equals("2")) {
						String SQL = " UPDATE CRO_CARREPAIRORDER set towing_settlement_state = '1',towing_settlement_man='"+towing_settlement_man+"',towing_settlement_time='"+towing_settlement_time+"'  where  id=? ";
						db.execute(SQL, ids, Type.VARCHAR);
					}
				}
			}
		}
	}
}
