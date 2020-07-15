package com.am.frame.editflowstatus;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 *@author 作者：yangdong
 *@create 时间：2016年7月25日 上午11:25:55
 *@version 说明：修改流程状态公共service
 */
public class EditFlowStatus {
	/**
	 * 修改流程状态
	 * @param tableName 表名
	 * @param id 数据id
	 * @param auditPerson 审核人
	 * @param auditOpinion 审核意见
	 * @param auditResult 审核结果
	 */
	public int editFlowStatus(String tableName,String id,String auditPerson,String auditOpinion,String mainAuditOption,String auditResult){
		
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE "+tableName+" SET  ");
		
		//副主任审核通过
		if("1".equals(auditResult)){
			
			sql.append(" audit_state =  2, ");
			sql.append(" audit_result = "+auditResult+", ");
			sql.append(" audit_opinion = '"+auditOpinion+"', ");
		}else if("2".equals(auditResult)){//主任审核通过
			
			sql.append(" audit_state =  3, ");
			sql.append(" settlement_state =  1,");
			sql.append(" audit_result = "+auditResult+", ");
			sql.append(" main_audit_opinion = '"+mainAuditOption+"', ");
		}else if("3".equals(auditResult)){//副主任审核驳回
			
			sql.append(" audit_state = 4, ");
			sql.append(" audit_result = "+auditResult+" ,");
			sql.append(" audit_opinion = '"+auditOpinion+"', ");
		}else if("4".equals(auditResult)){//主任审核驳回
			
			sql.append(" audit_state = 4, ");
			sql.append(" audit_result = "+auditResult+" ,");
			sql.append(" main_audit_opinion = '"+mainAuditOption+"', ");
		}
		
		
		sql.append(" audit_person = '"+auditPerson+"' ");
		sql.append(" WHERE id = '"+id+"' ");
		
		DB db = null;
		int res = 0;
		try{
			db = DBFactory.newDB();
			res = db.execute(sql.toString());
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		return res;
	}
}
