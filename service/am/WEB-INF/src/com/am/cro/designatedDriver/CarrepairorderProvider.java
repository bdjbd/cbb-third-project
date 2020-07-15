package com.am.cro.designatedDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/**
 * 代驾费用管理模糊查询
 * @author 王成阳
 * 2017-9-7
 */
public class CarrepairorderProvider implements SqlProvider{
	private static final long serialVersionUID = 1L;
	//打印日志
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public String getSql(ActionContext ac) {
		logger.debug("-----------------------------------进入代驾费用管理模糊查询方法---------------------------------------");
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","cro_carrepairorders.query");
		StringBuilder qsb = new StringBuilder();
		//只显示需要接送车,审核状态为待审核、已审核的信息。并按照创建时间倒序排列
		qsb.append("select cr.* from cro_CarRepairOrder cr , AM_MEMBER member , AORG aorg where cr.is_shuttle_service = '1' and cr.driving_settlement_audit_state in( '1','2' ) and cr.memberid = member.id and cr.orgcode = aorg.orgid");
		//如果查询单元不为空
		if (queryRow != null) {
			//如果获取的会员id不为空则拼条件
			if (!Checker.isEmpty(queryRow.get("memberid1"))) {
				//根据memberid1查询
				qsb.append(" and cr.memberid in (select id from am_member where membername like '%"+queryRow.get("memberid1")+"%')");
				logger.debug("------------------------------------会员模糊查询--------------------------------------------");
			}
			//如果获取的修理厂id不为空则拼条件
			if (!Checker.isEmpty(queryRow.get("orgcode1"))) {
				//根据orgcode1模糊查询
				qsb.append(" and cr.orgcode in (select orgid from aorg where orgname like '%"+queryRow.get("orgcode1")+"%')");
				logger.debug("-----------------------------------汽修厂模糊查询------------------------------------------");
			}
		}
		qsb.append(" order by createdate desc"); 
		
		return qsb.toString();
	}
}
