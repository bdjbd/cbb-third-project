package com.am.instore.action;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.instance.JudgeBonusRewardTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.virement.VirementManager;
import com.am.instore.server.GroupBonusService;
import com.am.instore.server.UpdateMaterialsCodeServer;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.FlowEndAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月28日
 *@version
 *说明：入库流程结束Action
 */
public class InstoreFlowEndAction implements FlowEndAction{

	private Logger logger=org.slf4j.LoggerFactory.getLogger(getClass());
	
	@Override
	public void cancel(DB arg0, ActionContext arg1, String arg2, boolean arg3)
			throws Exception {
		
	}

	/**
	 * 流程通过
	 */
	@Override
	public void pass(DB db, ActionContext ac, String flow_id) throws Exception {
		
		// 入库时组织机构ID，供应商，入库总金额，物资编码，货位ID，重量，单价，仓库名称，商品类型，商品名称
				String querSql = " SELECT store.orgid,store.support_account,store.total_amount/100 AS total_amount,list.materialscode,list.storeallocid,list.counts,list.inprice,ps.sname,pmc.name,pmt.tname  "
						+ "	FROM p2p_InStore AS store "
						+ " LEFT JOIN mall_InStore_list AS list ON store.id=list.in_store_id "
						+ " LEFT JOIN p2p_Store AS ps ON list.store_id= ps.id "
						+ " LEFT JOIN p2p_MaterialsCode AS pmc ON list.materialscode=pmc.code "
						+ " LEFT JOIN p2p_materialstype AS pmt ON list.materials_id=pmt.id "
						+ " WHERE store.flow_id='"+flow_id+"' ";
				
				MapList list = db.query(querSql);
				
				String snams="";
				String names="";
				
				if (!Checker.isEmpty(list)) {
					for (int i = 0; i < list.size(); i++) {
						//物资编码
						String materialsCode = list.getRow(i).get("materialscode");
						//货位ID
						String storeAllocId = list.getRow(i).get("storeallocid");
						//数量
						String counts = list.getRow(i).get("counts");
						//单价(分)
						String inPrice = list.getRow(i).get("inprice");
						//仓库名称
						String sname = list.getRow(i).get("sname");
						snams+=","+sname;
						//商品类型
						String tname = list.getRow(i).get("tname");
						//商品名称
						String name = list.getRow(i).get("name");
						names+=","+name;
						
						UpdateMaterialsCodeServer service=new UpdateMaterialsCodeServer();
						//更新物资编码表的数量和单价
						service.updateMaterialsCode(materialsCode,counts,inPrice,db);
						//更新货位库存信息表的物资编码和数量
						service.updateStoreAllocInfo(materialsCode,counts,storeAllocId,db);
					}
					//组织机构ID
					String orgId = list.getRow(0).get("orgid");
					//供应商ID
					String supportAccount = list.getRow(0).get("support_account");
					//转账金额（元）
					String totalAmount = list.getRow(0).get("total_amount");
					//帐号
					String iremakers = "供应商账号为："+supportAccount+"的"+snams+"进入"+names+"的"+"入库收入";
					String oremakers = "供应商账号为："+supportAccount+"的"+snams+"进入"+names+"的"+"入库支出";
					
					//查询供应商是机构还是个人
					String querySQLType="SELECT * FROM mall_account WHERE loginaccount=? ";
					MapList map=db.query(querySQLType,supportAccount, Type.VARCHAR);
					
					String table="";
					String supportAccountCode=SystemAccountClass.CASH_ACCOUNT;
					if(!Checker.isEmpty(map)){
						table=map.getRow(0).get("tablename");
					}
					
					if("aorg".equals(table)){
						supportAccountCode=SystemAccountClass.GROUP_CASH_ACCOUNT;
					}

					String sql="SELECT id FROM am_member WHERE loginaccount=?  ";
					
					MapList mmMap=db.query(sql,supportAccount,Type.VARCHAR);
					
					VirementManager memager = new VirementManager();
					
					if(!Checker.isEmpty(mmMap)){
						//会员转账
						String memberId=mmMap.getRow(0).get("id");
						
						//入库帐号转账给供应商帐号
						JSONObject jso=memager.execute(db, orgId, memberId, SystemAccountClass.GROUP_CASH_ACCOUNT, supportAccountCode, totalAmount, iremakers, oremakers,"", false);
						logger.info("入库转账，转账结果："+jso);
						
						//  totalAmount   入库金额
						// supportAccount  供应商id
						
						//如果是前台用户，调用用的分红任务，
						//如果是机构，不触发任务，调用服务类
							TaskEngine taskEngine=TaskEngine.getInstance();
							RunTaskParams params=new RunTaskParams();
							params.pushParam(JudgeBonusRewardTask.RECHARGEMONEY,VirementManager.changeY2F(totalAmount));
							params.setTaskCode(JudgeBonusRewardTask.TASK_CODE); //
							params.setMemberId(memberId);
							taskEngine.executTask(params);
						
					}else{
						sql="SELECT * FROM aorg WHERE orgid=? ";
						mmMap=db.query(sql,supportAccount,Type.VARCHAR);
						
						if(!Checker.isEmpty(mmMap)){
							
							String memberId=mmMap.getRow(0).get("orgid");
							
							//入库帐号转账给供应商帐号
							JSONObject jso=memager.execute(db, orgId, memberId, SystemAccountClass.GROUP_CASH_ACCOUNT, supportAccountCode, totalAmount, iremakers, oremakers,"", false);
							logger.info("入库转账，转账结果："+jso);
							
							//记录分红权
							GroupBonusService bgs=new GroupBonusService();
							long menoy=VirementManager.changeY2F(totalAmount);
							bgs.calculationDividendRightBuyInMeony(db, menoy, memberId);
							
							
						}else{
							logger.info("入账账号不合法，账号："+supportAccount);
						}
						
					}
				}
		
	}
	
	@Override
	public void unpass(DB arg0, ActionContext arg1, String arg2)
			throws Exception {
		
	}

}
