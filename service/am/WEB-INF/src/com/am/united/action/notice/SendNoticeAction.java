package com.am.united.action.notice;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年5月17日
 * @version 
 * 说明:<br />
 * 通知发送Action
 */
public class SendNoticeAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		super.doAction(db, ac);
		
		//通知目标 1：直接上级;2：直接下级;3：所有下级
		String targetId=ac.getRequestParameter("mall_federation_notice.form.target_id");
		//通知id
		String noticeId=ac.getRequestParameter("mall_federation_notice.form.id");
		
		String updateSQL="UPDATE mall_federation_notice SET status=1 WHERE id=? ";
		db.execute(updateSQL,noticeId,Type.VARCHAR);
		
		
		createTargetOrgId(db,ac,targetId,noticeId);
	}

	/**
	 * 
	 * @param db
	 * @param targetId1：直接上级;2：直接下级;3：所有下级
	 * @param noticeId 通知id
	 * @throws JDBCException 
	 */
	private void createTargetOrgId(DB db,ActionContext ac, String targetId,String noticeId) throws JDBCException {
		
		
		//当前机构
		String currentOrg=ac.getVisitor().getUser().getOrgId();
		
		//机构类型
		//1,总农技协联合会；2，省；3，市；4，区县；
		String orgLevel="";
		String querSQL="SELECT * FROM aorg WHERE orgid=? ";
		MapList orgMap=db.query(querSQL, currentOrg, Type.VARCHAR);
		
		if(!Checker.isEmpty(orgMap)){
			Row orgRow=orgMap.getRow(0);
			orgLevel=orgRow.get("orglevel");
			
		}
		
		if("1".equals(targetId)&&!"country_ATAF".equals(currentOrg)){
			//直接上级  总农技协联合会无上级
//			"country_ATAF"
//			"org_P23_ATAF"
//			"org_P23_C297_ATAF"
//			"org_P23_C297_Z2324_ATAF"
			String[] orgPosition=currentOrg.split("_");
			
			String targetOrg="";
			if("4".equals(orgLevel)){
				//区县
				targetOrg=orgPosition[0]+"_"+orgPosition[1]+"_"+orgPosition[2]+"_ATAF";
			}
			if("3".equals(orgLevel)){
				//市
				targetOrg=orgPosition[0]+"_"+orgPosition[1]+"_ATAF";
			}
			if("2".equals(orgLevel)){
				//省
				targetOrg="country_ATAF";
			}
			
			addTargetNotice(db, noticeId, targetOrg);
			
			
		}
		if("2".equals(targetId)){
			//直接下级
			//如果是总农技协联合会，则为全部省
			if("1".equals(orgLevel)){
				querSQL="SELECT orglevel,* FROM aorg WHERE orgid LIKE 'org_P%ATAF' AND orglevel=2 ";
			}else{
				String[] orgPosition=currentOrg.split("_ATAF");
				querSQL="SELECT orglevel,* FROM aorg WHERE orgid LIKE '"+orgPosition[0]+
						"%ATAF' AND orglevel=("+orgLevel+"+1)";
			}
			
			MapList noticeOrgMap=db.query(querSQL);
			if(!Checker.isEmpty(noticeOrgMap)){
				Table noticeTargetTable=new Table("am_bdp", "mall_federation_notice_scaner");
				for(int i=0;i<noticeOrgMap.size();i++){
					addTargetNotice(db, noticeId, noticeOrgMap.getRow(i).get("orgid"));
				}
				db.save(noticeTargetTable);
			}
		}
		if("3".equals(targetId)){
			//所有下级
			if("1".equals(orgLevel)){
				querSQL="SELECT orglevel,* FROM aorg WHERE orgid LIKE 'org_P%ATAF' ";
			}else{
				String[] orgPosition=currentOrg.split("_ATAF");
				String qorg=orgPosition[0]+"%_ATAF";
				querSQL="SELECT orglevel,* FROM aorg WHERE orgid LIKE '"+qorg+"' AND orgid <>'"+currentOrg+"' ";
			}
			
			MapList noticeOrgMap=db.query(querSQL);
			if(!Checker.isEmpty(noticeOrgMap)){
				Table noticeTargetTable=new Table("am_bdp", "mall_federation_notice_scaner");
				for(int i=0;i<noticeOrgMap.size();i++){
					addTargetNotice(db, noticeId, noticeOrgMap.getRow(i).get("orgid"));
				}
				db.save(noticeTargetTable);
			}
		}
	}
	
	/**
	 * 添加接受通知的目标单位
	 * @param db
	 * @param noticeId 通知id
	 * @param targetId 接受目标ID
	 * @throws JDBCException 
	 */
	public void addTargetNotice(DB db,String noticeId,String targetOrgId) throws JDBCException{
		Table noticeTargetTable=new Table("am_bdp", "mall_federation_notice_scaner");
		TableRow tr=noticeTargetTable.addInsertRow();
		tr.setValue("fnt_id", noticeId);//通知id
		tr.setValue("orgid",targetOrgId );
		
		db.save(noticeTargetTable);
	}
	
	
}
