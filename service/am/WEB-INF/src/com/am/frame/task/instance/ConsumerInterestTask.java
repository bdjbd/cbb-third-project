package com.am.frame.task.instance;

import java.util.ArrayList;
import java.util.List;

import com.am.frame.task.instance.entity.InstanceMember;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.AbstractTask;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年3月16日
 * @version 
 * 说明:<br />
 * 消费分利任务
 * RunTaskParams 中的memberID为消费会员的ID，
 * 需要根据消费会员的ID，计算出给谁分利，分多少，然后进行分利
 */
public class ConsumerInterestTask extends AbstractTask {
	
	//ConsumerInterestTask
	/**企业任务编码**/
	public static final String TASK_ECODE="ConsumerInterestTask";
	/**消费分利，需要分红的上级会员分利信息**/
	public static final String CONSUMER_INSTEREST_MEMBERSET="ConsumerInterestTask.CONSUMER_INSTEREST_MEMBERSET";
	
	@Override
	public boolean updateUserTaskData(RunTaskParams runTaskParams,DB db)throws Exception{
		//根据当前会员id，和订单ID，以及系统设置，计算出奖励的会员的会员ID
		//查询分利会员信息
		List<InstanceMember> result=buildInstanceMember(db, runTaskParams);
		//将分利会员信息保存在运行参数中，由分利奖励进行奖励操作
		runTaskParams.pushParam(CONSUMER_INSTEREST_MEMBERSET, result);
		return true;
	}

	/**
	 * 构建分利帐号信息
	 * @param db
	 * @param runTaskParams
	 * @return
	 */
	public List<InstanceMember> buildInstanceMember(DB db,RunTaskParams runTaskParams)
	throws JDBCException{
		
		List<InstanceMember> reuslt=new ArrayList<InstanceMember>();
		
		//1,查询分利层级，比例
		//2,根据分利层级，查询分利会员
		//订单信息SELECT id,TotalPrice FROM mall_MemberOrder WHERE  id=?
		
//		String querySQL="SELECT * FROM consumer_dividend WHERE status=1 ORDER BY level ASC  ";
//		
//		MapList map=db.query(querySQL);
//		if(!Checker.isEmpty(map)){
//			for(int i=0;i<map.size();i++){
//				//层级
//				String level=map.getRow(i).get("level");
//				//消费分利比例
//				String consume_ratio=map.getRow(i).get("consume_ratio");
//				//套餐充值分利比例
//				String package_ratio=map.getRow(i).get("package_ratio");
//				
//				StringBuilder queryLevelSQL=new StringBuilder();
//				
//				//1,获取当前会员的upid，即，上级的id集合，格式为'id1','id2','id3'
//				String memberIds=getUpidSet(db,memberId);
//				
//				if(Checker.isEmpty(memberIds)){
//					//如果无上级，直接返回
//					return reuslt;
//				}
//				
//				//消费分利任务，查询当前会员的上级会员，并给会员执行配置的奖励
//				queryLevelSQL.append(" SELECT * FROM ( ");
//				queryLevelSQL.append("SELECT row_number() over() AS mlevel, * FROM ( ");
//				queryLevelSQL.append("	SELECT upid,array_length(regexp_split_to_array(upid,','),1) AS upidlength,id ");
//				queryLevelSQL.append("   FROM am_member WHERE id IN(");
//				queryLevelSQL.append( memberIds);
//				queryLevelSQL.append(" ) ORDER BY upidlength DESC");
//				queryLevelSQL.append(" ) ds1 ");
//				queryLevelSQL.append(" ) ds2 WHERE mlevel=? ");
//				
//				MapList memberMap=db.query(queryLevelSQL.toString(),level,Type.INTEGER);
//				
//				if(!Checker.isEmpty(memberMap)){
//					for(int j=0;j<memberMap.size();j++){
//						
//						InstanceMember item=new InstanceMember();
//						item.instRatiol=consume_ratio;
//						item.packageRatiol=package_ratio;
//						item.memberId=memberMap.getRow(j).get("id");
//						
//						reuslt.add(item);
//					}
//				}
//			}
//		}
		//所有参与提出比例会员
		//1,获取订单金额
		String orderId=runTaskParams.getMemberOrderId();
		String queyrOrder="SELECT totalprice*100 AS totalprice FROM mall_memberorder WHERE id=?  ";
		
		MapList orderInfo=db.query(queyrOrder,orderId,Type.VARCHAR);
		
		if(!Checker.isEmpty(orderInfo)){
			
			String totalprice=orderInfo.getRow(0).get("totalprice");
			
			//2,根系社员得消费金额
			String updateSQL="UPDATE am_member_distribution_map "
					+ " SET monetary=COALESCE(monetary,0)+? WHERE sub_member_id=? ";
			
			db.execute(updateSQL,new String[]{
					totalprice,runTaskParams.getMemberId()
			},new int[]{Type.DECIMAL,Type.VARCHAR});
			
			//3,查询任务触发人的所有上级
			String querySQL="SELECT * "+
					" FROM am_member_distribution_map AS amdp "+
					" LEFT JOIN consumer_dividend AS cd ON amdp.level=cd.level "+
					" WHERE amdp.sub_member_id='"+runTaskParams.getMemberId()+"' "+
					" ORDER BY amdp.level ASC ";
			
//			AND amdp.invitation_status='1'/
			MapList map=db.query(querySQL);
			
			if(!Checker.isEmpty(map)){
				for(int i=0;i<map.size();i++){
					Row row=map.getRow(i);
					
					InstanceMember item=new InstanceMember();
					item.instRatiol=row.get("consume_ratio");
					item.packageRatiol=row.get("package_ratio");
					item.memberId=row.get("member_id");
					
					reuslt.add(item);	
				}
			}
		}
		
		
		return reuslt;
	}
	
	
	/**
	 * 获取当前会员的upid，即，上级的id集合，格式为'id1','id2','id3'
	 * @param db
	 * @param memberId
	 * @return
	 * @throws JDBCException 
	 */
	private String getUpidSet(DB db, String memberId) throws JDBCException {
		String reuslt="";
		
		String querySQL="SELECT upid FROM am_member WHERE id=? ";
		
		MapList map=db.query(querySQL,memberId,Type.VARCHAR);
		if(!Checker.isEmpty(map)){
			String upid=map.getRow(0).get("upid");
			if(upid!=null){
				String[] upids=upid.split(",");
				for(int i=0;i<upids.length;i++){
					reuslt+="'"+upids[i]+"',";
				}	
			}
			
		}
		if(reuslt.length()>1){
			reuslt=reuslt.substring(0, reuslt.length()-1);
		}
		
		return reuslt;
	}
	
	
}
