package com.wd.init.permission;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.User;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.user.UserFactory;
import com.wd.ICuai;
import com.wd.database.DataBaseFactory;
import com.wd.tools.DatabaseAccess;

public class FindPermissionByUserID implements ICuai {
	@Override
	/**
	 * 通过登录人ID得到登录人权限集合
	 * */
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJsonArray = new JSONArray();
		try {
				// 获得登录人id
				String pUserID = jsonArray.getString(0).toString();
				// 从UserFactory中得到用户信息
				User user = UserFactory.getUser(pUserID);
				if(user!=null)
				{
					// 获得登录人的所有角色
					String[] roleIDs = user.getAllRoles();
					String rolesStr = "";
					// 将角色id用","拼接
					for (String roleID : roleIDs) {
						rolesStr += roleID + ",";
					}
					rolesStr = rolesStr.substring(0, rolesStr.length() - 1);
					// 顶级节点id为"1"
					returnJsonArray = findTerminalMenuByFatherMenuID("1", rolesStr,
							false);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}

	/**
	 * 通过父菜单节点ID和角色ID得到拥有权限下级菜单集合
	 * 
	 * @param java
	 *            .lang.String 终端菜单ID
	 * @param java
	 *            .lang.String 角色id拼接（角色1ID,角色2）
	 * @return ArrayList<HashMap<String, Object>> 菜单集合,包括菜单属性和菜单参数
	 * */
	private JSONArray findTerminalMenuByFatherMenuID(String pFatherMenuID,
			String pRolesStr, Boolean isEndNodes) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			String sql = "select t.id,t.fathermenuid,t.menucode,t.menuname,t.path,t.icon,t.remark,t.sortNo,max(r.permission)as permission  from abdp_RoleTerminalMenu r "
					+ " left join  abdp_TerminalMenu t on r.menuid = t.id"
					+ " where "
					+ DataBaseFactory.getDataBase().getInstrStr(
							"'" + pRolesStr + "'", "roleid")
					+ ">0  and t.fathermenuid='"
					+ pFatherMenuID
					+ "'"
					+ " and r.permission != 0 group by t.id,t.fathermenuid,t.menucode,t.menuname,t.path,t.icon,t.remark,t.sortno order by t.sortNo";
			MapList list = DBFactory.getDB().query(sql);
			int row_count=list.size();
			for(int i=0;i<row_count;i++) 
			{
				Row row=list.getRow(i);
				int column_count=row.size();
				JSONObject jo = new JSONObject();
				// 添加菜单属性
				for (int j =0;j <column_count;j++) {
					String currentValue=row.get(j);
					if(currentValue==null || currentValue.trim().equalsIgnoreCase(""))
					{
						jo.put("","");
					}
					else
					{
						jo.put(row.getKey(j).toUpperCase(),currentValue);
					}
				}
				String menuID =row.get("id");
				// 如果不是末级节点，则添加子节点
				if (isEndNodes == false) {
					// 暂时只支持3级
					JSONArray childMenuList = this
							.findTerminalMenuByFatherMenuID(menuID, pRolesStr,
									true);
					jo.put("childMenuList", childMenuList);

					// 如果是末级节点，则添加菜单参数
				} else {
					// 添加菜单参数集合
					JSONArray paramList = this
							.findTerminalMenuParamByMenuID(menuID);
					jo.put("paramList", paramList);
				}
				returnJsonArray.put(jo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}

	/**
	 * 通过终端菜单ID的到终端菜单参数集合
	 * 
	 * @param java
	 *            .lang.String 终端菜单ID
	 * @return ArrayList<HashMap<String, String>> 菜单参数集合
	 * */
	private JSONArray findTerminalMenuParamByMenuID(String pMenuID) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			String sql = "select p.menuparamid,p.menuid,p.paramkey,p.paramvalue from TerminalMenuParam p where p.menuid='"
					+ pMenuID + "'";
			returnJsonArray = DatabaseAccess.query(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}
}
