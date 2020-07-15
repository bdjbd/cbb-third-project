package com.am.mall.Activities;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月7日
 * @version 
 * 说明:<br />
 * 活动启用，停用Activity
 */
public class StartOrStopActivities extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) {
		// 获得选择列: _s_单元编号
		String[] select = ac.getRequestParameters("_s_mall_activities.list");
		// 获得主键: 单元编号.元素编号.k（设置了映射表时会自动用隐藏域记录主键值）
		String[] bookId = ac.getRequestParameters("mall_activities.list.id.k");
		List<String> selectedBookId = new ArrayList<String>();
		
		if (!Checker.isEmpty(select)) {
			for (int i = 0; i < select.length; i++) {
				if ("1".equals(select[i])) {// 1为选中
					selectedBookId.add(bookId[i]);
				}
			}
			
		}

		// 设置提示消息
		String msg ="";
		if (Checker.isEmpty(selectedBookId)) {
			msg="没有选择数据！";
		} else {
			//0=停用.1=启用
			boolean result=true;
			for(int i=0;i<selectedBookId.size();i++){
				result=result&&StartOrStopActivity(db,selectedBookId.get(i));
			}
			
			if(result)
				msg="活动设置成功！";
			else
				msg="活动设置出错！请检查选择的数据！";
		}
		ac.getActionResult().addSuccessMessage(msg.toString());
	}
	
	
	private boolean StartOrStopActivity(DB db,String id){
		
		boolean result=false;
		
		try{
			//1,查询是否状态
			String querySQL="SELECT id,activitiesstate FROM mall_activities WHERE id='"+id+"' ";
			MapList queryMap=db.query(querySQL);
			
			if(!Checker.isEmpty(queryMap)){
				//2,如果是启用，则停用，如果是停用，则启用
				String state=queryMap.getRow(0).get("activitiesstate");
				
				String updateState="";
				if("0".equals(state)){
					updateState="1";
				}else{
					updateState="0";
				}
				
				String updateSQL="UPDATE mall_Activities SET activitiesstate='"+updateState+"' WHERE id='"+id+"' ";;
				db.execute(updateSQL);
				result=true;
			}
		}catch(JDBCException e){
			e.printStackTrace();
			
		}
		return result;
	}
}
