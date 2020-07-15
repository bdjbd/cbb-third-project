package com.ambdp.member.action;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年5月5日
 * @version 
 * 说明:<br />
 * 添加会员到知道的标签
 */
public class AddMemberToLabelAction extends DefaultAction {

		@Override
		public void doAction(DB db, ActionContext ac) throws Exception {
			//标签id
			String labelId=ac.getRequestParameter("labelId");
			System.err.println("获得标签id："+labelId);
			//需要添加会员的id
			String addIds=ac.getRequestParameter("addIds");
			System.err.println("获得会员id："+addIds);
			//先删除，再添加
			String delSQL="DELETE FROM mall_member_label_relation WHERE  member_id=? AND label_id=?";
			
			String insertSQL="INSERT INTO mall_member_label_relation"
					+ " (id, member_id, label_id, create_time)VALUES (?, ?, ?, now()) ";
			
			//会员id数字
			String[] addIdsArry={};
			
			//删除参数
			List<String[]> delBatchSQLParams=new ArrayList<String[]>();
			//新增参数
			List<String[]> insertBatchSQLParams=new ArrayList<String[]>();
			
			if(!Checker.isEmpty(addIds)){
				addIdsArry=addIds.split(",");
			}
			
			for(int i=0;i<addIdsArry.length;i++){
				delBatchSQLParams.add(new String[]{addIdsArry[i],labelId});
				insertBatchSQLParams.add(new String[]{UUID.randomUUID().toString(),
						addIdsArry[i],
						labelId});
			}
			
			//批量删除
			db.executeBatch(delSQL, delBatchSQLParams,new int[]{Type.VARCHAR,Type.VARCHAR});
			
			//批量新增
			db.executeBatch(insertSQL, insertBatchSQLParams,new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
			
			Ajax ajax=new Ajax(ac);
			ajax.addScript("window.opener.location.reload();window.close()");
			ajax.send();
		}
	
}
