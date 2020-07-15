package com.wd.init.mainActivity;

import org.json.JSONArray;
import com.fastunit.User;
import com.fastunit.user.UserFactory;
import com.wd.ICuai;
import com.wd.database.DataBaseFactory;
import com.wd.tools.DatabaseAccess;

/**
 * 获取对应角色主活动
 * */
public class FindLoginUserMainActivitySetCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJSonArray = new JSONArray();
		try {
			// 获得登录人id
			String pUserID = jsonArray.getString(0).toString();
			// 从UserFactory中得到用户信息
			User user = UserFactory.getUser(pUserID);
			// 获得登录人的所有角色
			if(user!=null)
			{
				String[] roleIDs = user.getAllRoles();
				String rolesStr = "";
				// 将角色id用","拼接
				for (String roleID : roleIDs) {
					rolesStr += roleID + ",";
				}
				rolesStr = rolesStr.substring(0, rolesStr.length() - 1);
				String sql = "select m.activityClassName from abdp_RoleClientMain r left join abdp_MainActivitySet m on  m.id = r.mainactivitysetid where "
						+ DataBaseFactory.getDataBase().getInstrStr(
								"'" + rolesStr + "'", "roleid")
						+ " >0 and isDefaultActivity ='0' "
						+ DataBaseFactory.getDataBase().getTop1Str();
				returnJSonArray = DatabaseAccess.query(sql);
				// 如果角色么有对应主活动，则取默认活动
				if (returnJSonArray.length() == 0 || returnJSonArray == null) {
					String defaultMainActivitySql = "select activityClassName from abdp_MainActivitySet where isDefaultActivity ='1'";
					returnJSonArray = DatabaseAccess.query(defaultMainActivitySql);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJSonArray;
	}
}
