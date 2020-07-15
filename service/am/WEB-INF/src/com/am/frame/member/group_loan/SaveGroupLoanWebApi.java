package com.am.frame.member.group_loan;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月30日
 *@version
 *说明：联保贷款发起邀请
 */
public class SaveGroupLoanWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
			//申请银行
			String applyBank = request.getParameter("applyBank");
			//申请人
			String appyName = request.getParameter("appyName");
			//申请人id
			String memberid = request.getParameter("memberid");
			//申请金额
			Double money = Double.parseDouble(request.getParameter("money"));
			//身份证号
			String code = request.getParameter("code");
			//证件有效期
			String expiresDate = request.getParameter("expiresDate");
			//邮编
			String zipCode = request.getParameter("zipCode");
			//居住地
			String address = request.getParameter("address");
			//固定电话
			String telephone = request.getParameter("telephone");
			//手机号
			String phone = request.getParameter("phone");
			//联保人员ID--数组
			String loanMemberid = request.getParameter("loanMemberid");
			//贷款申请保存数据
			resultJson=insertLoanApply(db,applyBank,appyName,memberid,money,code,expiresDate,zipCode,address,telephone,phone);
			String applyId = (String) resultJson.get("id");
			if(!Checker.isEmpty(applyId)){
				//增加担保人
				insertApplyGuarantor(db,applyId,loanMemberid);
			}
			resultJson.put("code", "0");
			resultJson.put("msg", "邀请成功");
		} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(db!=null){
					try {
						db.close();
					} catch (JDBCException e) {
						e.printStackTrace();
					}
				}
			}
			
		
			
			return resultJson.toString();
		}
	/**
	 * 增加担保人
	 * @param db  
	 * @param applyId 借款信息Id 
	 * @param loanMemberid  担保人数组
	 * @throws JDBCException 
	 */
	private void insertApplyGuarantor(DB db, String applyId, String loanMemberid) throws JDBCException {
		
		String loanMember = loanMemberid.replace("[", "");
		loanMember = loanMember.replace("]", "");
		loanMember = loanMember.replace("\"", "'");
		String[] ary = loanMember.split(",");
		for (int i = 0; i < ary.length; i++) {
            String uuid = UUID.randomUUID().toString();
            String inserSQL=" INSERT INTO mall_loan_appy_guarantor (id,lp_id,member_id,lpg_status) "
    				+ " VALUES('" +uuid + "','" +applyId + "',"+ary[i]+",'1' ) ";
    		
    		db.execute(inserSQL);
        }
		
		
	}
	/**
	 * 贷款申请保存数据
	 * @param db
	 * @param applyBank  申请银行
	 * @param appyName 申请人
	 * @param memberid 申请人ID
	 * @param money 申请金额
	 * @param code 身份证号
	 * @param expiresDate 证件有效期
	 * @param zipCode 邮编 
	 * @param address 居住地
	 * @param telephone 固定电话
	 * @param phone 手机号
	 * @return 
	 * @throws JDBCException 
	 */
	private JSONObject insertLoanApply(DB db, String applyBank, String appyName,
			String memberid, Double money, String code, String expiresDate,
			String zipCode, String address, String telephone, String phone) throws Exception {
		
		JSONObject resultJson = new JSONObject();
		
		Double moneys = money*100;
		int intMoney = (new   Double(moneys)).intValue(); 
		
		Table table=new Table("am_bdp","mall_loan_appy");
		TableRow insertTr=table.addInsertRow();
		insertTr.setValue("target_org_id", applyBank);
		insertTr.setValue("appy_member_id", memberid);
		insertTr.setValue("appy_amount", intMoney);
		insertTr.setValue("lp_stauts", 2);
		insertTr.setValue("appy_name", appyName);
		insertTr.setValue("id_code", code);
		insertTr.setValue("expires_date", expiresDate);
		insertTr.setValue("zip_code", zipCode);
		insertTr.setValue("address", address);
		insertTr.setValue("telephone", telephone);
		insertTr.setValue("phone", phone);
		db.save(table);
		String id=table.getRows().get(0).getValue("id");
		if(!Checker.isEmpty(id)){
			resultJson.put("id", id);
		}else{
			resultJson.put("code", "999");
			resultJson.put("msg", "邀请失败");
		}
		return resultJson;
	}

		
		

}