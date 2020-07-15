package com.am.cro.member.SqlProvider;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author 张少飞
 * @create 2017/9/6
 * @version 
 * 说明:会员标签管理 添加会员  只显示当前机构及下属机构的会员列表
 * 会员查询
 */
public class CroMembersListSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		Row queryRow=FastUnit.getQueryRow(ac,"am_bdp","am_member_label.query");
		//当前登录机构
		String orgcode = ac.getVisitor().getUser().getOrgId();
		
		StringBuilder querySQL=new StringBuilder();
		
		querySQL.append(" select distinct m.id as id,m.phone,m.membername,m.memberbirthday,m.membersex,m.identitycardnumber,m.registrationdate "
				+ ",c.carframenumber "  //当同一个会员有多个车架号时，就会出现会员姓名重复的数据，所以车架号不宜列出来
				+ "");
		querySQL.append(" from  am_member as m, cro_CarManager as c ");
		querySQL.append(" where m.id = c.memberid and m.orgcode like '"+orgcode+"%' ");
		//查询条件
		if(queryRow!=null){
			//注册手机号码
			String phone = queryRow.get("phone");
			if(!Checker.isEmpty(phone)){
				querySQL.append(" and m.phone like '%"+phone+"%' ");
			}
			//会员姓名
			String membername = queryRow.get("membername");
			if(!Checker.isEmpty(membername)){
				querySQL.append(" and m.membername like '%"+membername+"%' ");
			}
			//会员性别
			String membersex = queryRow.get("membersex");
			if(!Checker.isEmpty(membersex)){
				querySQL.append(" and m.membersex = '"+membersex+"' ");
			}
			//身份证号
			String identitycardnumber = queryRow.get("identitycardnumber");
			if(!Checker.isEmpty(identitycardnumber)){
				querySQL.append(" and m.identitycardnumber like '%"+identitycardnumber+"%' ");
			}
			//车架号
			String carframenumber = queryRow.get("carframenumber");
			if(!Checker.isEmpty(carframenumber)){
				querySQL.append(" and c.carframenumber like '%"+carframenumber+"%' ");
			}
			
						
		}
		
		querySQL.append(" ORDER BY m.registrationdate desc ");
		return querySQL.toString();
	}

}
