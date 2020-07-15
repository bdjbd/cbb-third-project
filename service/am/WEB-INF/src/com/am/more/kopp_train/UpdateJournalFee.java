package com.am.more.kopp_train;

import java.util.UUID;
import org.json.JSONObject;
 




import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.instance.GetVolunteerAccountWithQualificationItask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.callback.IBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 购买文章增加期刊费用 支付成功回调
 * 
 * @author xiechao
 */
public class UpdateJournalFee implements IBusinessCallBack {

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {

		JSONObject jobBus = new JSONObject(business);
		try {

			// 增加的金额
			String addfee = jobBus.getString("addfee");
			// 会员id
			String memberid = jobBus.getString("memberid");
			// 期刊id
			String journal_id = jobBus.getString("id");
			// 阅读次数
			String count = jobBus.getString("count");
			// 学校的id
			String kopp_train_id = null;
			// 会员名称
			String membername = null;
			// 书籍名称
			String bookname = null;
			
			
			//查找会员名称
			String m_nSQL = "select * from am_member where id = '" + memberid+ "'";
			MapList m_nsql = db.query(m_nSQL);
			if(m_nsql.size()>0){
				membername = m_nsql.getRow(0).get("membername");			
			}
			
			
			//查找书籍名称
			String b_nSQL = "select pc_title from mall_publishing_content where id = '" + journal_id+ "'";
			MapList b_nsql = db.query(b_nSQL);
			if(b_nsql.size()>0){
				bookname = b_nsql.getRow(0).get("pc_title");			
			}
			
			

			// 增加期刊费用
			String updateSQL = "UPDATE am_member SET journal_fee=COALESCE(journal_fee,0)+" 
					+ addfee + "*100 WHERE id='" + memberid+ "' ";
			db.execute(updateSQL);
			
			String checkUserSQL = "SELECT * FROM mall_purchase_records WHERE member_id = '"+memberid+"' and pc_id = '"+journal_id+"'";
			
			MapList maplist = db.query(checkUserSQL);
		
			String UpdateMemberReaderSQL ="";
			
			if(maplist.size()>0){
				// 更新阅读次数
				UpdateMemberReaderSQL = "UPDATE mall_purchase_records "
							+ " SET access = '"+count+"' "
							+ " WHERE member_id = '"+memberid+"' and pc_id = '"+journal_id+"'";
				db.execute(UpdateMemberReaderSQL);
			}else
			{
				// 添加购买记录
				String Id=UUID.randomUUID().toString();
				
				UpdateMemberReaderSQL= "INSERT INTO mall_purchase_records "
						+ "(id, member_id, pc_id, access,create_time"
						+ "  )"
						+ "  VALUES ('"+Id+"', '"+memberid+"', '"+journal_id+"', '"+count+"','now()'"
						+ " )";
				db.execute(UpdateMemberReaderSQL);
			}
			
			//获取学校的id
			String sql = "SELECT * FROM avar WHERE vid='kopp_train_id'";		
			MapList list = db.query(sql);
					
			if(list.size()>0){
				kopp_train_id = list.getRow(0).get("vvalue");			
			}
			
			//向学校现金账户转账
			VirementManager vir = new VirementManager();
			String iremakers = membername+"在APP端购买" + bookname + "期刊的费用";

			if(kopp_train_id != null){
				
				vir.execute(db, "", kopp_train_id, "", SystemAccountClass.GROUP_POPULAR_SCHOOL_FUNDS_ACCOUNT, addfee, iremakers, "", "", false);
				
			}
			
			
			//触发会员提现资格任务
			// 获取志愿者账号提现资格任务 START
			TaskEngine taskEngine = TaskEngine.getInstance();
			RunTaskParams params = new RunTaskParams();
			//提现资格任务
			params.setTaskCode(GetVolunteerAccountWithQualificationItask.TASK_ECODE);
			params.pushParam(GetVolunteerAccountWithQualificationItask.PURCHASE_ARTICLE_AMOUNT,
					addfee);

			params.setMemberId(memberid);
			taskEngine.executTask(params);
			

		} catch (JDBCException e1) {
			e1.printStackTrace();
		} finally {

		}
		return null;
	}

}