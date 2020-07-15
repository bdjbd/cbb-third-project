package com.am.content;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月15日 下午4:25:16
 * @version 发布内容
 */
public class ContentrListRelease extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//获取选择列
		String[] SelectList = ac.getRequestParameters("_s_am_content_list");
		
		//获取列表中的id
		String[] SelectId = ac.getRequestParameters("am_content_list.id.k");
		
		//是否是列表
		if(!Checker.isEmpty(SelectList)){
			
			for(int i = 0 ; i < SelectList.length; i ++){
				//是否选中,1表示选中
				if(SelectList[i].equals("1")){
					String UpdateDataStateSQL = " UPDATE am_content SET datastate = '2' WHERE id = '"+SelectId[i]+"' ";
					db.execute(UpdateDataStateSQL);
				}
			}
			
			
		}
	}
}
