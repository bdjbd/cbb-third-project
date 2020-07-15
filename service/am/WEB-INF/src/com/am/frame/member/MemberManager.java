package com.am.frame.member;

import java.util.Arrays;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.task.task.TaskEngine;
import com.am.frame.util.ShareCodeUtil;
import com.am.frame.webapi.db.DBManager;
import com.am.frame.webapi.member.service.SystemAccountServer;
import com.am.mall.beans.member.MemberAddres;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月20日
 * @version 说明:<br />
 *          会员管理类
 * 
 */
public class MemberManager {

	/**
	 * 社员类型：<br>
	 * 10：普通消费者<br>
	 * 20：社区义工<br>
	 * 30：家庭农厂帮扶社员<br>
	 * 40：家庭农厂互帮互助组组长<br>
	 * 50：家庭农厂会计<br>
	 * 60：家庭农厂出纳<br>
	 * 70：家庭农厂副厂长<br>
	 * 80：家庭农厂厂长<br>
	 * 90：联合社创办人<br>
	 * 100：配送中心创办人<br>
	 * 110：合作社创办人<br>
	 * 其中10，20为消费者社员；其它为生产者社员。当值为10，20时，机构ID为空。
	 **/
	public enum MemberRole {
		/** 普通消费者 **/
		ROLE_10(10),
		/** 社区义工 **/
		ROLE_20(20),
		/** 家庭农厂帮扶社员 **/
		ROLE_30(30),
		/** 家庭农厂互帮互助组组长 **/
		ROLE_40(40),
		/** 家庭农厂会计 **/
		ROLE_50(50),
		/** 家庭农厂出纳 **/
		ROLE_60(60),
		/** 家庭农厂副厂长 **/
		ROLE_70(70),
		/** 家庭农厂厂长 **/
		ROLE_80(80),
		/** 联合社创办人 **/
		ROLE_90(90),
		/** 配送中心创办人 **/
		ROLE_100(100),
		/** 合作社创办人 **/
		ROLE_110(110);

		private int value;

		private MemberRole(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	};

	public static void main(String[] args) {
		System.out.println(MemberRole.ROLE_90.value);
		System.out.println(Arrays.toString(MemberRole.values()));
	}

	public void updateMemberData(String fileName[], String values[], int types[], DB db) {
	}

	/**
	 * 更新用户open_id
	 * 
	 * @param score
	 * @param memberId
	 */
	public void updateMemberOpenId(String openId, String memberId) {

		try {

			DBManager db = new DBManager();

			String updateSQL = "UPDATE am_Member SET openid=? WHERE Id=?";

			db.execute(updateSQL, new String[] { openId, memberId }, new int[] { Type.VARCHAR, Type.VARCHAR });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新用户积分
	 * 
	 * @param score
	 * @param memberId
	 */
	public void updateMemberScore(int score, String memberId) {

		try {

			DB db = DBFactory.getDB();

			String updateSQL = "UPDATE am_Member SET score=COALESCE(score,0)+? WHERE Id=?";

			db.execute(updateSQL, new String[] { score + "", memberId }, new int[] { Type.INTEGER, Type.VARCHAR });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据地址id，获取会员地址信息
	 * 
	 * @param addresID
	 * @return
	 */
	public MemberAddres getMemberAddressById(String addresID) {

		MemberAddres result = null;

		try {
			DB db = DBFactory.getDB();

			result = getMemberAddressById(addresID, db);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 根据地址id，获取会员地址信息
	 * 
	 * @param addresID
	 * @param db
	 * @return
	 * @throws JDBCException
	 */
	public MemberAddres getMemberAddressById(String addresID, DB db) throws JDBCException {

		String findAddrSQL = "SELECT * FROM  am_ADDRES WHERE id=?";

		MapList map = db.query(findAddrSQL, addresID, Type.VARCHAR);

		MemberAddres addr = new MemberAddres(map);

		return addr;
	}

	/**
	 * 修改社员类型
	 * 
	 * @param db
	 *            DB
	 * @param memberId
	 *            社员ID
	 * @param membeType
	 *            社员类型：1=消费者社员;2=单位消费者社员;3=生产者社员
	 * @throws JDBCException
	 */
	public void changeMemberType(DB db, String memberId, String membeType) throws JDBCException {
		String updateSQL = "UPDATE am_member SET member_type=? WHERE id=?  ";
		db.execute(updateSQL, new String[] { membeType, memberId }, new int[] { Type.VARCHAR, Type.VARCHAR });

		// 如果社员类型为3，则需要启用信誉卡帐号
		SystemAccountServer saServer = new SystemAccountServer();
		try {
			saServer.initUserSystemAccount(db, memberId, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 重置社员类型
	 * @param db
	 * @param memberId
	 * @param membeType
	 * @throws Exception 
	 */
	public void resetMemberType(DB db, String memberId, String membeType) throws Exception{
		changeMemberType(db, memberId, membeType);
		
		//初始化社员的任务
		TaskEngine taskEngine=TaskEngine.getInstance();
		taskEngine.addUserAllTask(memberId,"", db);
	}

	/**
	 * 修改社员角色
	 * 
	 * @param db
	 *            DB
	 * @param memberId
	 *            社员ID
	 * @param memberRole
	 *            社员角色：10：普通消费者;20：社区义工
	 *            30：家庭农厂帮扶社员;40：家庭农厂互帮互助组组长;50：家庭农厂会计;60：家庭农厂出纳
	 *            70：家庭农厂副厂长;80：家庭农厂厂长;90：联合社创办人;100：配送中心创办人;110：合作社创办人
	 * @throws JDBCException
	 */
	public void changeMemberRole(DB db, String memberId, String memberRole) throws JDBCException {
		String updateSQL = "UPDATE am_member SET mrole=? WHERE id=?  ";
		db.execute(updateSQL, new String[] { memberRole, memberId }, new int[] { Type.INTEGER, Type.VARCHAR });
	}

	
	/**
	 * 为社员生成邀请码 消费者，单位，普通生产者的邀请码都为手机号，厂长的邀请码为手机号+role
	 * 
	 * @param db
	 * @param memeberId
	 *            社员ID
	 * @param memberRole
	 *            社员角色：10：普通消费者;20：社区义工
	 *            30：家庭农厂帮扶社员;40：家庭农厂互帮互助组组长;50：家庭农厂会计;60：家庭农厂出纳
	 *            70：家庭农厂副厂长;80：家庭农厂厂长;90：联合社创办人;100：配送中心创办人;110：合作社创办人
	 * @throws Exception
	 */
	public void createInvitationCode(DB db, String memberId, String memberRole) throws Exception {
		StringBuilder inserSQL = new StringBuilder();

		// 邀请码
		String invCode = "";

		String id = UUID.randomUUID().toString();

		// 其他普通社员
		// 邀请码为社员登录帐号，登录帐号在理性农业中是和电话号码一致，注册时。
		MapList map = db.query("SELECT loginaccount FROM am_member WHERE id='" + memberId + "' ");
		if (!Checker.isEmpty(map)) {
			invCode = map.getRow(0).get("loginaccount");
		} else {
			invCode = ShareCodeUtil.toSerialCode(System.currentTimeMillis());
		}

		// 检查社员是否已经有邀请码，如果有，则不生成
		String checkSQL = "SELECT * FROM am_memberinvitationcode "
				+ " WHERE am_memberid=? AND invitationcode=? AND ic_role=? ";

		MapList checkMap = db.query(checkSQL, new String[] { memberId, invCode, "10"// 10
																					// 普通消费者
		}, new int[] { Type.VARCHAR, Type.VARCHAR, Type.INTEGER });

		// 如果用户没有邀请码，则生成邀请码
		if (Checker.isEmpty(checkMap)) {
			inserSQL.append("INSERT INTO am_memberinvitationcode(                 ");
			inserSQL.append("            id, am_memberid,registrationdate,");
			inserSQL.append("            invitationcode, ic_role,                 ");
			inserSQL.append("              state)                      ");
			inserSQL.append("    VALUES (?, ?,now(),                          ");
			inserSQL.append("            ?, ?,                                   ");
			inserSQL.append("             '0')                                   ");

			db.execute(inserSQL.toString(), new String[] { id, memberId, invCode, "10" },
					new int[] { Type.VARCHAR, Type.VARCHAR, Type.VARCHAR, Type.INTEGER });
		}

		// 理性农业业务 检查是否为厂长，如果是，生成相应的邀请码
		createProducerInvCode(db, memberId, memberRole);
	}

	/**
	 * 厂长生存邀请码
	 * 
	 * @param db
	 * @param memberId
	 *            社员ID
	 * @param memberRole
	 *            社员角色：10：普通消费者;20：社区义工
	 *            30：家庭农厂帮扶社员;40：家庭农厂互帮互助组组长;50：家庭农厂会计;60：家庭农厂出纳
	 *            70：家庭农厂副厂长;80：家庭农厂厂长;90：联合社创办人;100：配送中心创办人;110：合作社创办人
	 * @throws JDBCException
	 */
	public void createProducerInvCode(DB db, String memberId, String memberRole) throws JDBCException {
		if ((MemberRole.ROLE_80.value + "").equals(memberRole)) {
			// 如果为农厂厂长，则需要生成农厂职位相关邀请码
			/**
			 * 社员类型： 10：普通消费者 20：社区义工 30：家庭农厂帮扶社员 40：家庭农厂互帮互助组组长 50：家庭农厂会计
			 * 60：家庭农厂出纳 70：家庭农厂副厂长 80：家庭农厂厂长 90：联合社创办人 100：配送中心创办人 110：合作社创办人
			 * 其中10，20为消费者社员；其它为生产者社员。当值为10，20时，机构ID为空。
			 **/

			// 查询购买者购买的机构的机构ID
			String querySQL = "SELECT id,loginaccount,orgid FROM account_info WHERE purchaser=? ";
			MapList meberInfo = db.query(querySQL, memberId, Type.VARCHAR);

			// 获取会员登录帐号
			String memberLoginaccount = "";
			querySQL = "SELECT id,loginaccount,orgid FROM account_info WHERE id=? ";
			MapList map = db.query(querySQL, memberId, Type.VARCHAR);
			if (!Checker.isEmpty(map)) {
				memberLoginaccount = map.getRow(0).get("loginaccount");
			}

			if (!Checker.isEmpty(meberInfo)) {

				Table invTable = new Table("am_bdp", "AM_MEMBERINVITATIONCODE");
				MemberRole[] memberRoles = MemberRole.values();

				for (MemberRole role : memberRoles) {
					TableRow insertTr = invTable.addInsertRow();

					insertTr.setValue("am_memberid", memberId);
					insertTr.setValue("orgid", meberInfo.getRow(0).get("orgid"));
					insertTr.setValue("invitationcode", memberLoginaccount + "_" + role.value);
					insertTr.setValue("ic_role", role.getValue());
					insertTr.setValue("explain", "");
					insertTr.setValue("state", "0");
				}
				db.save(invTable);
			}
		}
	}

	/**
	 * 删除邀请码对应的邀请码社员使用ID和邀请码使用状态
	 * 
	 * @param db
	 * @param invId
	 * @throws JDBCException
	 */
	public void delInverMember(DB db, String invId) throws JDBCException {
		String udpateSQL = "UPDATE am_MemberInvitationCode SET user_member_id='',State=0 WHERE id=? ";
		db.execute(udpateSQL, invId, Type.VARCHAR);
	}

	/**
	 * 修改社员机构ID
	 * 
	 * @param db
	 * @param orgId
	 *            社员机构ID
	 * @param member
	 *            社员id
	 * @throws JDBCException
	 */
	public void changeMemberOrg(DB db, String orgId, String memberId) throws JDBCException {
		String updateSQL = "UPDATE am_Member SET OrgCode=? WHERE id=? ";
		db.execute(updateSQL, new String[] { orgId, memberId }, new int[] { Type.VARCHAR, Type.VARCHAR });
	}

	/**
	 * 厂长生存邀请码
	 * 
	 * @param db
	 * @param memberId
	 *            社员ID
	 * @param memberRole
	 *            社员角色：10：普通消费者;20：社区义工
	 *            30：家庭农厂帮扶社员;40：家庭农厂互帮互助组组长;50：家庭农厂会计;60：家庭农厂出纳
	 *            70：家庭农厂副厂长;80：家庭农厂厂长;90：联合社创办人;100：配送中心创办人;110：合作社创办人
	 * @param orgId,
	 *            邀请码机构ID
	 * @throws JDBCException
	 */
	public void createProducerInvCode(DB db, String memberId, String memberRole, String orgId) throws JDBCException {
		if ((MemberRole.ROLE_80.value + "").equals(memberRole)) {
			// 如果为农厂厂长，则需要生成农厂职位相关邀请码
			/**
			 * 社员类型： 10：普通消费者 20：社区义工 30：家庭农厂帮扶社员 40：家庭农厂互帮互助组组长 50：家庭农厂会计
			 * 60：家庭农厂出纳 70：家庭农厂副厂长 80：家庭农厂厂长 90：联合社创办人 100：配送中心创办人 110：合作社创办人
			 * 其中10，20为消费者社员；其它为生产者社员。当值为10，20时，机构ID为空。
			 **/

			// 查询购买者购买的机构的机构ID
			String querySQL = "SELECT id,loginaccount,orgid FROM account_info WHERE purchaser=? ";
			MapList meberInfo = db.query(querySQL, memberId, Type.VARCHAR);

			// 获取会员登录帐号
			String memberLoginaccount = "";
			querySQL = "SELECT id,loginaccount,orgid FROM account_info WHERE id=? ";
			MapList map = db.query(querySQL, memberId, Type.VARCHAR);
			if (!Checker.isEmpty(map)) {
				memberLoginaccount = map.getRow(0).get("loginaccount");
			}

			if (!Checker.isEmpty(meberInfo)) {

				Table invTable = new Table("am_bdp", "AM_MEMBERINVITATIONCODE");
				// MemberRole[] memberRoles=MemberRole.values();

				// for(MemberRole role:memberRoles){
				// TableRow insertTr=invTable.addInsertRow();
				//
				// insertTr.setValue("am_memberid",memberId);
				// insertTr.setValue("orgid",orgId);
				// insertTr.setValue("invitationcode",
				// memberLoginaccount+"_"+role.value);
				// insertTr.setValue("ic_role",role.getValue());
				// insertTr.setValue("explain","");
				// insertTr.setValue("state","0");
				// }
				// 查询农村机构配置 格式为
				/**
				 * {"code":"home_farm", "groupConfig":[
				 * {"title":"副厂长","number":"2","ic_role":""},
				 * {"title":"会计","number":"1","ic_role":""},
				 * {"title":"出纳","number":"1","ic_role":""},
				 * {"title":"组长","number":"5","ic_role":""}] }
				 */
				String queryGroupCofnigSQL = "SELECT * FROM am_group_config WHERE code='home_farm' ";
				MapList groupMap = db.query(queryGroupCofnigSQL);
				if (!Checker.isEmpty(groupMap)) {
					try {
						JSONObject groupConfig = new JSONObject(groupMap.getRow(0).get("property_value"));
						JSONArray configArry = groupConfig.getJSONArray("groupConfig");
						for (int i = 0; i < configArry.length(); i++) {
							JSONObject item = configArry.getJSONObject(i);
							int number = item.getInt("number");
							for (int j = 0; j < number; j++) {
								String icRole = item.getString("ic_role");
								String title = item.getString("title");

								TableRow insertTr = invTable.addInsertRow();
								insertTr.setValue("am_memberid", memberId);
								insertTr.setValue("orgid", orgId);
								insertTr.setValue("invitationcode",
										memberLoginaccount + "_" + Integer.toHexString(i) + Integer.toHexString(j));
								insertTr.setValue("ic_role", icRole);
								insertTr.setValue("explain", title);
								insertTr.setValue("state", "0");
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				db.save(invTable);
			}
		}
	}

	/**
	 * 用户使用邀请码 理性农业业务处理
	 * 
	 * @param db
	 * @param invCode
	 *            邀请码
	 * @param userInvCodeMemberId
	 *            邀请码使用者ID
	 * @throws JDBCException
	 */
	public JSONObject userInvCode(DB db, String invCode, String userInvCodeMemberId) throws Exception {
		JSONObject result = new JSONObject();
		// 1,查询社员类型，如果邀请码角色不是普通用户和消费用户，这邀请码需要修改
		// 邀请码状态和邀请码使用人
		String querySQL = "SELECT m.orgcode,m.member_type,inv.* " + " FROM am_memberinvitationcode AS inv "
				+ " LEFT JOIN am_member AS m ON m.id=inv.am_memberid "
				+ "   WHERE  invitationcode=?  AND member_type is not null AND member_type <>'' ";//AND ic_role NOT IN (0,10) 
		MapList map = db.query(querySQL, new String[] { invCode }, new int[] { Type.VARCHAR });

		if (!Checker.isEmpty(map)) {
			Row row = map.getRow(0);
			String id = row.get("id");
			String ic_role = row.get("ic_role");
			String mType = row.get("member_type");
			String orgcode = row.get("orgcode");

			// 更新邀请码使用状态SQL
			String updateSQL = " UPDATE am_memberinvitationcode SET State='1',user_member_id=? " + " WHERE id=? ";
			db.execute(updateSQL, new String[] { userInvCodeMemberId, id }, new int[] { Type.VARCHAR, Type.VARCHAR });

			
			// 2,更新邀请码使用者的角色 社员类型
//			updateSQL = "UPDATE am_member SET mrole=?,member_type=?,orgcode=? WHERE id=? ";

//			db.execute(updateSQL, new String[] { ic_role, mType, orgcode, userInvCodeMemberId },
//					new int[] { Type.INTEGER, Type.VARCHAR, Type.VARCHAR, Type.VARCHAR });
			
			updateSQL = "UPDATE am_member SET mrole=?,orgcode=? WHERE id=? ";
			db.execute(updateSQL, new String[] { ic_role, orgcode, userInvCodeMemberId },
					new int[] { Type.INTEGER, Type.VARCHAR, Type.VARCHAR });

			// 邀请码ID
			String invId = row.get("id");
			// 更新社员邀请码使用者 ,如果角色为0或者10，则不更新
			if(!"0".equals(ic_role)&&!"10".equals(ic_role)){
				updateSQL = "UPDATE am_member SET register_inv_code_id=? WHERE id=? ";
				db.execute(updateSQL, new String[] { invId, userInvCodeMemberId },
						new int[] { Type.VARCHAR, Type.VARCHAR });
			}

			result.put("CODE", "0");
			result.put("MSG", "社员角色转换成功，请重新登录帐号生效！");

		} else {
			result.put("CODE", "8332");
			result.put("MSG", "无效的邀请码！");
		}
		return result;

	}

	/**
	 * 理性农业邀请码验证
	 * 
	 * 如果是注册类型为生产者,邀请码的拥有者的社员类型必须是生产者, 如果是消费者或者单位,如果有邀请码,邀请码的拥有者不做验证.
	 * 
	 * @param member_type
	 *            1=消费者社员,2=单位消费者社员,3=生产者社员
	 * @param invitation_code
	 * @return
	 */
	public String validateRegisterCode(String member_type, String invitation_code) throws Exception{

		String result = null;
		JSONObject resoultObj =new JSONObject();
		if (!Checker.isEmpty(invitation_code)) {

			String loginAccount = "";
			if (invitation_code.indexOf("_") > 0) {
				loginAccount = invitation_code.substring(0, invitation_code.indexOf("_"));
			} else {
				loginAccount = invitation_code;
			}

			String querySQL = " SELECT id,am_memberid,orgid,invitationcode,ic_role " + " FROM am_MemberInvitationCode  "
					+ " WHERE invitationcode='" + invitation_code + "' "
					+ " AND am_memberid IN (select id FROM am_member WHERE loginaccount='" + loginAccount + "' )";

			DBManager dbManager = new DBManager();
			MapList map = dbManager.query(querySQL);
			
			
			if (!Checker.isEmpty(map)) {
				String am_memberid = map.getRow(0).get("am_memberid");

				querySQL = "SELECT member_type FROM am_member WHERE id=? ";

				MapList mMap = dbManager.query(querySQL, am_memberid, Type.VARCHAR);

				if (!Checker.isEmpty(mMap)) {
					// 邀请码拥有者的社员类型
					String amMemberType = mMap.getRow(0).get("member_type");

					// 如果注册类型是生产者，需要卡邀请码拥有者的内容必须类型相同。
					if ("3".equals(member_type)) {
						if (member_type != null && member_type.equals(amMemberType)) {
							
						} else {
							result = "请输入正确生产者的邀请码!";
							resoultObj.put("CODE", "09999");
							resoultObj.put("MSG", result);
							return resoultObj.toString();
						}
					}
					
					result = vilerDataVi(am_memberid,dbManager);
					
					return result;
				} else {
					result = "请输入正确的邀请码!";
					resoultObj.put("CODE", "09999");
					resoultObj.put("MSG", result);
					return resoultObj.toString();
				}

				// String role=map.getRow(0).get("ic_role");
				// //1，如果是消费者，2，单位消费者社员；只能使用消费者邀请码 //10：普通消费者;
				// // 1=消费者社员,2=单位消费者社员,3=生产者社员
				//
				// if(("2".equals(member_type)||"1".equals(member_type))
				// &&("10".equals(role)||"0".equals(role))){
				// //检查是否为合格的消费者邀请码
				// result=null;
				// return result;
				// }else
				// if("3".equals(member_type)&&(!"10".equals(role)&&!"0".equals(role))){
				// //检查是否为合格的消费者邀请码
				// result=null;
				// return result;
				// }else{
				// result="请输入正确的邀请码!";
				// return result;
				// }
				// 检查邀请码拥有者的类型与注册者类型是否相同

			} else {
				result = "此邀请码不存在，请输入正确的邀请码!";
				resoultObj.put("code", "09999");
				resoultObj.put("msg", result);
				return resoultObj.toString();
			}
		}else
		{
			result = "此邀请码不存在，请输入正确的邀请码!";
			resoultObj.put("code", "0");
			resoultObj.put("msg", result);
		}
		
		return resoultObj.toString();
	}

	/**
	 * 获取社员的上级社员信息
	 * 
	 * @param memberId
	 */
	public void getUpMember(String memberId) {
	}

	public MapList getMemberById(String memberId, DB db) throws JDBCException {
		MapList map = null;

		String querySQL = "SELECT * FROM am_member WHERE id=? ";

		map = db.query(querySQL, memberId, Type.VARCHAR);

		return map;
	}

	
	/**
	 * 判断志愿者邀请人数
	 * am_memberid 所填的邀请码用户的用户id
	 * 
	 */
	public String vilerDataVi(String am_memberid,DBManager dbManager) throws Exception
	{
		String result = "";
		JSONObject resoultObj = new JSONObject();
		//增加一条件  判断社员的志愿者等级 人数 volunteers_manager volunteers_record
		String vsql = "select * from volunteers_record where member_id = '"+am_memberid+"'";
		
		MapList vmlist = dbManager.query(vsql);
		
		if(Checker.isEmpty(vmlist))
		{
			result = "该社员的邀请码，可邀请人数为0，无法进行邀请操作!";
			resoultObj.put("CODE", "1");
			resoultObj.put("MSG", result);
			return resoultObj.toString();
		}else
		{
			//判断可邀请人数people_num 和已邀请人数people_num  如果可邀请人数大于已邀请人数则更新已邀请人数 +1
			if(vmlist.getRow(0).getInt("people_num", 0)>=vmlist.getRow(0).getInt("surplus_num", 0))
			{
				resoultObj.put("CODE", "0");
				resoultObj.put("volunteers_id", vmlist.getRow(0).get("id"));
				return resoultObj.toString();
				
			}else
			{
				result = "该社员的邀请码，可邀请人数已满，无法进行邀请操作!";
				resoultObj.put("CODE", "09999");
				resoultObj.put("MSG", result);
				return resoultObj.toString();
			}
			
		}
	}
	
	/**
	 * 更新会员的开放平台的openid，与微信公众账号的opendid不同
	 * 
	 * @param openId
	 *            微信开放平台的OPENID
	 * @param memberId
	 *            会员ID
	 */
	public void updateMemberAPPOpenId(String appOpenId, String memberId) {
		try {

			DBManager db = new DBManager();

			String updateSQL = "UPDATE am_Member SET app_openid=? WHERE Id=?";

			db.execute(updateSQL, new String[] { appOpenId, memberId }, new int[] { Type.VARCHAR, Type.VARCHAR });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 申请加入农场
	 * 
	 * @param db
	 * @param invCode
	 *            邀请码，必须为农场对应的组织机构编码
	 * @param userInvCodeMemberId
	 *            使用邀请码社员id
	 * @param membrType
	 *            社员类型
	 * @return
	 * @throws Exception
	 */
	public JSONObject userInvCode(DB db, String invCode, String userInvCodeMemberId, String membrType)
			throws Exception {
		JSONObject result = new JSONObject();
		
		// 1,查询邀请码是否为机构编号
		String querySQL = "SELECT orgid,orgname FROM aorg WHERE orgtype='50' AND orgid=? ";
		
		MapList map = db.query(querySQL,invCode,Type.VARCHAR);

		if (!Checker.isEmpty(map)) {
			Row row = map.getRow(0);
			
			String orgcode = row.get("orgid");

			// 更新社员邀请码使用者
			String updateSQL = "UPDATE am_member SET orgcode=? WHERE id=? ";
			db.execute(updateSQL, new String[] { orgcode, userInvCodeMemberId },
					new int[] { Type.VARCHAR, Type.VARCHAR });

			result.put("CODE", "0");
			result.put("MSG", "社员角色转换成功，请重新登录帐号生效！");

		} else {
			result.put("CODE", "8332");
			result.put("MSG", "无效的邀请码！");
		}
		return result;
	}

	
	/**
	 * 根据登录账号检查用户是否已经存在
	 * @param db
	 * @param loginAccount
	 * @return  true 表示用户已经存在；false 表示用户不存在
	 * @throws JDBCException 
	 */
	public MapList getMemberByLoginAccount(DB db, String loginAccount) throws JDBCException {
		MapList reuslt=null;
		
		String checkSQL="SELECT * FROM am_member WHERE loginaccount=? ";
		
		reuslt=db.query(checkSQL, loginAccount, Type.VARCHAR);
		
		
		return reuslt;
	}

}
