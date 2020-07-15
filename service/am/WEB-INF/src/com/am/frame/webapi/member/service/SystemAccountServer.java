package com.am.frame.webapi.member.service;

import java.text.DecimalFormat;
import java.util.UUID;

import org.json.JSONObject;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.systemAccount.groupAccount.GroupInitAccountAction;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月11日
 * @version 
 * 说明:<br />
 */
public class SystemAccountServer {
	
	/**
	 * 初始化用户帐号
	 * @param db  DB
	 * @param memberId 会员ID
	 * @param org_code  机构编号
	 * @throws JDBCException 
	 */
	public void initUserSystemAccount(DB db, String memberId, String orgCode,String memberType) 
			throws JDBCException {
		
		//1,查询系统已经启用的帐号
		//如果memberType==3 生产者社员，则启用信誉卡帐号，否则不启用信誉卡帐号
		String queryAccountSQL="SELECT id,sa_code,sa_name "
				+ " FROM mall_system_account_class WHERE status_valid=1 AND account_type=1";
		
//      日期 2016年11月26日，初始化所有账户
//		if(!"3".equals(memberType)){
//			queryAccountSQL+=" AND sa_code NOT IN ('CREDIT_CARD_ACCOUNT') ";
//		}
		
		//2，给用户初始化帐号
		MapList map=db.query(queryAccountSQL);
		
		if(!Checker.isEmpty(map)){
			for(int i=0;i<map.size();i++){
				//系统帐号类型id
				String saClassId=map.getRow(i).get("id");
				initUserSystemAccountByClassId(db,memberId,orgCode,saClassId);
			}
		}
	}
	
	/**
	 * 初始化用户帐号,
	 * 	查询系统已经启用，但是当前社员还没有的账号
	 * @param db  DB
	 * @param memberId 会员ID
	 * @param org_code  机构编号
	 * @throws JDBCException 
	 */
	public void initUserSystemAccount(DB db, String memberId, String orgCode) throws JDBCException {
		
		//1,查询系统已经启用，但是当前社员还没有的账号
		String queryAccountSQL="SELECT id,sa_code,sa_name "
				+ " FROM mall_system_account_class "
				+ " WHERE status_valid=1 AND account_type=1 "
				+ "  AND id NOT IN ( "
				+ "   select a_class_id from mall_account_info where member_orgid_id='"+memberId+"' "
				+ "  )";
		
		//2，给用户初始化帐号
		MapList map=db.query(queryAccountSQL);
		
		if(!Checker.isEmpty(map)){
			for(int i=0;i<map.size();i++){
				//系统帐号类型id
				String saClassId=map.getRow(i).get("id");
				initUserSystemAccountByClassId(db,memberId,orgCode,saClassId);
			}
		}
	}
	
	
	/***
	 * 根据系统帐号id，启用帐号
	 * @param db
	 * @param memberId
	 * @param orgCode
	 * @param saClassId
	 * @throws JDBCException 
	 */
	public void initUserSystemAccountByClassId(DB db, String memberId,
			String orgCode, String saClassId) throws JDBCException{
		
		//1,查询当前用户是否有账户，如果有，则不更新
		String querySQL="SELECT * FROM mall_account_info WHERE member_orgid_id=? AND a_class_id=? ";
		
		MapList checkMap=db.query(querySQL,new String[]{
				memberId,saClassId
		},new int[]{
				Type.VARCHAR,Type.VARCHAR
		});
		
		if(Checker.isEmpty(checkMap)){
			//如果没有账号，则新增
			StringBuilder  insertSQL=new StringBuilder();
			
			insertSQL.append("INSERT INTO mall_account_info(   "); 
			insertSQL.append("        id, member_orgid_id, a_class_id, total_amount, balance ");
			insertSQL.append("       , create_time, last_modify_time)  ");
			insertSQL.append(" VALUES (?, ?, ?, ?, ");
			insertSQL.append("       ?, now(), now() ) ");
			
			db.execute(insertSQL.toString(), 
					new String[]{UUID.randomUUID().toString(),memberId,saClassId,"0","0"}, 
					new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.INTEGER,Type.INTEGER});
		}
		
		
	}

	/**
	 * 启用系统账号
	 * @param db  DB
	 * @param asClassId 系统账号ID
	 * @param orgCode  机构编号 过滤查询人 如果为null，表示全部启用
	 * @throws JDBCException 
	 */
	public void startSystemAccount(DB db,String asClassId,String orgCode) throws JDBCException{
		//查询帐号系统帐号类型是系统帐号还是社员帐号
		String querySQL="SELECT * FROM mall_system_account_class WHERE id='"+asClassId+"' ";
		MapList map=db.query(querySQL);
		
		if(!Checker.isEmpty(map)){
			Row row=map.getRow(0);
			String accountType=row.get("account_type");
			//帐号类型  1：社员帐号 2：机构帐号
			if("1".equals(accountType)){
				//如果是社员帐号类型，则给社员初始化
				initMemberAccount(db,asClassId,orgCode);
			}else if("2".equals(accountType)){
				//如果是系统帐号类型，这给系统帐号初始化
				initOrgAccount(db,asClassId,orgCode);
			}
		}
	}
	
	/**
	 * 给社机构始化
	 * @param db DB
	 * @param asClassId  classId
	 * @param orgCode 机构id
	 * @throws JDBCException
	 */
	public void initOrgAccount(DB db, String asClassId, String orgCode)throws JDBCException{
		//1,查询需要初始化的机构
		String querySysteAccoutnSQL="SELECT orgid AS id,orgtype FROM aorg "+
				" WHERE orgid NOT IN ( "+
				" SELECT member_orgid_id FROM mall_account_info AS aci "+
				" LEFT JOIN mall_system_account_class AS sac "+
				" ON aci.a_class_id=sac.id " +
				" WHERE sac.id='"+asClassId+"' "+
				" AND sac.sa_code<>'"+SystemAccountClass.GROUP_POVERTY_ACCOUNT+"' "+
				" ) ";
		
		if(!Checker.isEmpty(orgCode)){
			querySysteAccoutnSQL+=" AND orgid='"+orgCode+"' ";
		}
		
		//2,启用账号
		MapList map=db.query(querySysteAccoutnSQL);
		if(!Checker.isEmpty(map)){
			for(int i=0;i<map.size();i++){
				Row row=map.getRow(i);
				String orgType=row.get("orgtype");
				//如果机构类型为省,市，区，则不给初始化系统帐号
				if(SystemAccountClass.ORG_TYPE_PROVINCE.equals(orgType)
					||SystemAccountClass.ORG_TYPE_CITY.equals(orgType)
					||SystemAccountClass.ORG_TYPE_ZONE.equals(orgType)){
					
				}else{
					//如果不是省，市，区，则给初始化帐号
					String memberId=row.get("id");
					initUserSystemAccountByClassId(db, memberId, orgCode, asClassId);
				}
				
			}
		}
	}

	/**
	 * 给社员初始化
	 * @param db DB
	 * @param asClassId  classId
	 * @param orgCode 机构id
	 * @throws JDBCException
	 */
	public void initMemberAccount(DB db, String asClassId, String orgCode)throws JDBCException{
		//1,查询需要初始化的人
		String querySysteAccoutnSQL="SELECT id FROM am_member "+
				" WHERE id NOT IN ( "+
				" SELECT member_orgid_id FROM mall_account_info AS aci "+
				" LEFT JOIN mall_system_account_class AS sac "+
				" ON aci.a_class_id=sac.id WHERE sac.id='"+asClassId+"'  )";
		
		if(!Checker.isEmpty(orgCode)){
			querySysteAccoutnSQL+=" AND orgcode='"+orgCode+"' ";
		}
		
		//2,启用账号
		MapList map=db.query(querySysteAccoutnSQL);
		if(!Checker.isEmpty(map)){
			for(int i=0;i<map.size();i++){
				String memberId=map.getRow(i).get("id");
				initUserSystemAccountByClassId(db, memberId, orgCode, asClassId);
			}
		}
	}
	
	
	/**
	 * 为机构初始化所有已启用的帐号
	 * 次功能不启用扶贫账号
	 * @param db
	 * @param orgCode
	 * @throws JDBCException
	 */
	public void startSystemAllAccount(DB db,String orgCode) throws JDBCException{
		//1,查询所有的所有额机构帐号
		//2,迭代初始化
		String  querySQL="SELECT * FROM mall_system_account_class "
				+ "  WHERE status_valid=1  "
				+ "  AND account_type=2  "
				+ "  AND sa_code<>'"+SystemAccountClass.GROUP_POVERTY_ACCOUNT+"'  ";
		
		MapList map=db.query(querySQL);
		
		if(!Checker.isEmpty(map)){
			for (int i = 0; i < map.size(); i++) {
				startSystemAccount(db, map.getRow(i).get("id"), orgCode);
			}
		}
	}
	
	
	/**
	 * 涉农企业，合作社，配送中心，自然村农厂，联合社审核通过后初始化
	 * @param db DB
	 * @param orderId  对应主表的id
	 * @param tableName 涉农企业，合作社，配送中心，自然村农厂，联合社表名
	 * @return  机构ID
	 * @throws Exception
	 */
	public JSONObject  initSystemServiceCommodity(DB db,String orderId,String tableName) 
			throws Exception{
		JSONObject fResult=new JSONObject();
		
		String querySQL="SELECT t.area_type,sctype.* FROM  mall_service_comd_type AS sctype  "+
				" LEFT JOIN mall_service_commodity AS sc  ON sc.sc_type=sctype.id "+
				" LEFT JOIN aorgtype AS t ON t.orgtype=sctype.org_type  "+
				" WHERE sctype.t_table_name=? AND t.area_type=? ";
		
		//查询区域类型
		String queyAreaSQL="SELECT * FROM "+tableName+" WHERE id=? ";
		
		MapList map=db.query(queyAreaSQL,orderId, Type.VARCHAR);
		
		String areaType="";
		
		if(!Checker.isEmpty(map)){
			areaType=map.getRow(0).get("area_type");//01 国家，02；省，03 市； 04 区县
		}
		
		try {
			//服务类商品类型设置信息
			MapList scMap=db.query(querySQL,new String[]{
					tableName,areaType
			}, new int[]{
					Type.VARCHAR,Type.VARCHAR
			});
			
			if(!Checker.isEmpty(scMap)){
				//编码
				String tCode=scMap.getRow(0).get("t_code");
				//机构类型
				String orgType=scMap.getRow(0).get("org_type");
				//创建机构
				String orgCode=createOrg(db, tableName, orderId, tCode, orgType);
				
				//初始化帐号系统
				String clasPath=scMap.getRow(0).get("init_class_path");
				
				if(!Checker.isEmpty(clasPath)){
					try {
						GroupInitAccountAction initAction=(GroupInitAccountAction) 
								Class.forName(clasPath).newInstance();
						fResult=initAction.initOrg(db, orgCode);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//创建管理员帐号
				if(fResult!=null&&"0".equalsIgnoreCase(fResult.getString("code"))){
					String queryAdminRoleSQL="SELECT sctype.* FROM  mall_service_comd_type AS sctype  "+
							" LEFT JOIN mall_service_commodity AS sc  ON sc.sc_type=sctype.id "+
							" WHERE sctype.t_table_name=? AND ";
					
					//管理员角色编号
					String adminRoleCode=scMap.getRow(0).get("admin_role_code");
					//管理员帐号名称
					querySQL="SELECT * FROM "+tableName+" WHERE id=?";
					MapList queryMap=db.query(querySQL,orderId,Type.VARCHAR);
					String userId=queryMap.getRow(0).get("admin_account");
					
					//创建机构管理员
					createOrgAdmin(db,orgCode,userId,adminRoleCode);
					fResult.put("msg", orgCode);
				}else{
					//管理员角色编号
					String adminRoleCode=scMap.getRow(0).get("admin_role_code");
					//管理员帐号名称
					querySQL="SELECT * FROM "+tableName+" WHERE id=?";
					MapList queryMap=db.query(querySQL,orderId,Type.VARCHAR);
					String userId=queryMap.getRow(0).get("admin_account");
					
					//配送中心特殊处理
					if("mall_distribution_center".equalsIgnoreCase(tableName)){
						//查询店铺ID
						//查询机构对应的店铺ID
						String querySQLStroe="SELECT * FROM MALL_STORE WHERE orgcode=? ";
						MapList stroeMap=db.query(querySQLStroe,orgCode,Type.VARCHAR);
						
						if(!Checker.isEmpty(stroeMap)){
							String id=stroeMap.getRow(0).get("id");
							fResult.put("store_id", id);
						}
					}
					
					//创建机构管理员
					createOrgAdmin(db,orgCode,userId,adminRoleCode);
					fResult.put("msg", orgCode);
					fResult.put("code","0");
				}
			}
		} catch (JDBCException e) {
			e.printStackTrace();
			fResult.put("code",9991);
		}
		return fResult;
	}

	/**
	 * 创建用户并给用户更新角色和机构
	 * @param db
	 * @param orgId
	 * @param userId
	 * @param adminRoleCode
	 * @throws JDBCException 
	 */
	private String createOrgAdmin(DB db,String orgId ,String userId, String adminRoleCode)
			throws JDBCException{
		
		String  result=null;
		//A,检查角色用户是否存在
		String checkSQL="SELECT * FROM auser WHERE userid=? ";
		MapList map=db.query(checkSQL, userId,Type.VARCHAR);
		if(!Checker.isEmpty(map)){
			//如果用户存在，这直接退出；
			result="管理员帐号名称“"+userId+"”已存在！";
			return result;
		}
		//B,如果不存在，创建用户信息    MD5：c4ca4238a0b923820dcc509a6f75849b
		String defaultPassword=Var.get("default_password");
		
		String insertSQL="INSERT INTO auser (userid,username,orgid,password,expired,userlevel,isleader) "
				+ " VALUES ('"+userId+"','"+userId+"','"+orgId+"',md5('"+defaultPassword+"'),0,1,0)";
		
		db.execute(insertSQL);
		
		//1,检查用户是否已经拥有此角色
		checkSQL="SELECT * FROM auserrole WHERE userid='"+userId+"' AND roleid='"+adminRoleCode+"' ";
		
		map=db.query(checkSQL);
		
		if(Checker.isEmpty(map)){
			//2，如果没有，则增加角色
			String inserSQL="INSERT INTO auserrole( "+
					" userid, roleid, \"hold\")"+
						" VALUES (?, ?, ? )";
			
			db.execute(inserSQL,
					new String[]{userId,adminRoleCode,"1"},
					new int[]{Type.VARCHAR,Type.VARCHAR,Type.SMALLINT});
		}
		return result;
	}


	/**
	 * 创建机构
	 * @param db  DB
	 * @param tableName  农厂，合作社，联合社，配送中心表名
	 * @param orderId 对应表的id
	 * @param tCode  农厂，合作社，联合社，配送中心，类型对应的编码
	 * @param orgType 机构i类型
	 * @return String 机构id
	 * @throws JDBCException 
	 */
	private String createOrg(DB db, String tableName, String orderId, String tCode,String orgType) throws JDBCException {
		//1,查询对应机构的省市区
		String querySQL="SELECT * FROM "+tableName+" WHERE id=? ";
		MapList orgMapList=db.query(querySQL, orderId, Type.VARCHAR);
		//省 70
		String provinceId="";
		
		//市 80 
		String cityId="";
		
		//区/县 90
		String zoneId="";
		
		//序号
		long seq=0;
		//GAP_name
		String gapName="";
		
		if(!Checker.isEmpty(orgMapList)){
			 provinceId=orgMapList.getRow(0).get("province_id");
			 cityId=orgMapList.getRow(0).get("city_id");
			 zoneId=orgMapList.getRow(0).get("zone_id");
			 seq=orgMapList.getRow(0).getLong("seq",0)+1;
			 gapName=orgMapList.getRow(0).get("gap_name");
		}
		//2,根据规则，创建对应的省市区及机构，并返回机构id
			//a,查收是否有对应的省，如果没有，创建
		String pOrgid="org_P"+provinceId;
		String orgName=getProvinceName(db,provinceId);
		if(!checkOrgExist(db,pOrgid)){//省
			addOrg(db, pOrgid, "org", orgName,"70",1+"", Integer.parseInt(provinceId)+"");
		}
		
		//b,查询是否有对应的市，如果没有，创建
		String cOrgid="";
		if(!Checker.isEmpty(cityId)){
			cOrgid=pOrgid+"_C"+cityId;
			orgName=getCityName(db,cityId);
			if(!checkOrgExist(db,cOrgid)){//市
				addOrg(db, cOrgid,pOrgid, orgName,"80",1+"", cityId);
			}
		}else{
			cOrgid=pOrgid;
		}
		
		//c,查询是否有对应的区县，如果没有，创建
		String zOrgid="";
		if(!Checker.isEmpty(zoneId)){
			zOrgid=cOrgid+"_Z"+zoneId;
			orgName=getZoneName(db,zoneId);
			if(!checkOrgExist(db,zOrgid)){//区县
				addOrg(db, zOrgid, cOrgid, orgName,"90",1+"", zoneId);
			}
		}else{
			zOrgid=cOrgid;
		}
		
		//拼接联合社，农村，配送中机构id和名称
		String newOrgId="";
		if(!Checker.isEmpty(tCode)){
			newOrgId=zOrgid+"_"+tCode;
			if("60".equals(orgType)||"50".equals(orgType)||"40".equals(orgType)){
				//涉农企业,农厂，配送中心
				newOrgId=newOrgId+"_"+getSeq(seq);
				addOrg(db, newOrgId, zOrgid, gapName+"",orgType,1+"",(Integer.parseInt(zoneId)+2)+"" );
			}else{
				String tempZoneId="1";
				
				if(!Checker.isEmpty(zoneId)){
					tempZoneId=(Integer.parseInt(zoneId)+1)+"";
				}
				
				addOrg(db, newOrgId, zOrgid, gapName+"",orgType,1+"",tempZoneId );
			}
		}
		
		return newOrgId;
	}



	private void addOrg(DB db, String orgid,String parentid, String orgName,String orgtype,
			String orglevel,String o) throws JDBCException {
		
		String checkSQL="SELECT * FROM aorg WHERE orgid=? ";
		MapList map=db.query(checkSQL, orgid,Type.VARCHAR);
		
		if(Checker.isEmpty(map)){
			String inserSQL="INSERT INTO aorg (orgid,parentid,orgname,orgtype,orglevel,o,f_status) "
					+ " VALUES(?,?,?,?,?,?,1)";
			
			db.execute(inserSQL,new String[]{orgid,parentid,orgName,orgtype,orglevel,o},
					new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.INTEGER,Type.INTEGER});
		}
	}


	private String getProvinceName(DB db, String provinceId) throws JDBCException {
		String provinceName="";
		String sql="SELECT * FROM province WHERE proid=? ";
		MapList map=db.query(sql,provinceId,Type.VARCHAR);
		if(!Checker.isEmpty(map)){
			provinceName=map.getRow(0).get("proname");
		}
		return provinceName;
	}
	
	/**
	 * 检查机构是否存在，如果存在，返回true，不存在，返回false
	 * @param db  DB
	 * @param orgid  机构ID
	 * @return  true，存在；false，不存在；
	 * @throws JDBCException
	 */
	public boolean checkOrgExist(DB db,String orgid) throws JDBCException{
		boolean result=false;
		String query="SELECT * FROM aorg WHERE orgid=? ";
		MapList checkMap=db.query(query,orgid,Type.VARCHAR);
		if(!Checker.isEmpty(checkMap)){
			result=true;
		}
		return result;
	}
	
	
	/**
	 * 获取市的名称
	 * @param db
	 * @param cityId  市ID
	 * @return
	 * @throws JDBCException 
	 */
	private String getCityName(DB db, String cityId) throws JDBCException {
		String cityName="";
		String sql="SELECT * FROM city WHERE cityid=? ";
		MapList map=db.query(sql,cityId,Type.VARCHAR);
		if(!Checker.isEmpty(map)){
			cityName=map.getRow(0).get("cityname");
		}
		return cityName;
	}
	
	
	/**
	 * 获取区县的名称
	 * @param db
	 * @param cityId  市ID
	 * @return
	 * @throws JDBCException 
	 */
	private String getZoneName(DB db, String cityId) throws JDBCException {
		String zoneName="";
		String sql="SELECT * FROM zone WHERE zoneid=? ";
		MapList map=db.query(sql,cityId,Type.VARCHAR);
		if(!Checker.isEmpty(map)){
			zoneName=map.getRow(0).get("zonename");
		}
		return zoneName;
	}
	
	public String getSeq(long seq){
		String  result="";
		DecimalFormat df = new DecimalFormat("00000");
		result= df.format(seq); //次id即为四位不重复的流水号
		return result;
	}
	
	/**
	 * 检查用户是否已经存在
	 * @param db
	 * @param userId
	 * @return  true 表示用户已存在
	 */
	public boolean checkUserExist(DB db,String userId){
		boolean result=false;
		String checkSQL="SELECT * FROM auser WHERE userid=? ";
		try {
			MapList map = db.query(checkSQL,userId, Type.VARCHAR);
			if(!Checker.isEmpty(map)){
				result=true;
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	/**
	 * 更新管理员帐号到对应的表中(组织机构表，如农厂，合作社，联合社，配送中心等 )
	 * @param db
	 * @param userId
	 * @param id
	 * @param tableName
	 * @throws JDBCException
	 */
	public void updateUserId(DB db, String userId, String id,String tableName) throws JDBCException {
		String updateSQL="UPDATE "+tableName+" SET admin_account=? WHERE id=?";
		db.execute(updateSQL,new String[]{
				userId,id
		},new int[]{
				Type.VARCHAR,Type.VARCHAR
		});
	}
	
	/**
	 * 根据账号编号初始化系统账号
	 * @param db  DB
	 * @param orgId  机构ID
	 * @param initAccountCodes 系统账号编号集合：<br>
	 *   <p>如：GRUP_DISTRIBUTION_ACCOUNT,GROUP_POPULAR_SCHOOL_FUNDS_ACCOUNT,GROUP_LOAN_ACCOUNT,
	 *   	GROUP_IDENTITY_STOCK_ACCOUNT,GROUP_FOOD_SAFETY_TRACING_ACCOUNT,GROUP_CREDIT_MARGIN_ACCOUNT,
	 *   	GROUP_CASH_ACCOUNT,GROUP_BONUS_ACCOUNT,GROUP_BANK_ACCOUNT,GROUP_ANTI_RISK_SELF_SAVING_ACCOUNT,
	 *   	GROUP_POVERTY_ACCOUNT	 
	 * @throws Exception 
	 **/
	public void startSystemAllAccount(DB db, String orgId, String initAccountCodes) throws Exception {
		if(!Checker.isEmpty(initAccountCodes)){
			String[] accountCodes=initAccountCodes.split(",");
			
			for(String accountCode:accountCodes){
				startSystemAccount(db, getAccountClassId(db,accountCode), orgId);
			}
		}
	}
	
	
	/**
	 * 获取系统账号ID
	 * @param db
	 * @param orgOrMemberId
	 * @param accountCode
	 * @return
	 * @throws JDBCException 
	 */
	public String getAccountClassId(DB db,String accountCode) throws Exception{
		String result="";
		String sql="SELECT * FROM mall_system_account_class WHERE sa_code=? ";
		MapList map=db.query(sql, accountCode,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			result=map.getRow(0).get("id");
		}
		return result;
	}
	
}
