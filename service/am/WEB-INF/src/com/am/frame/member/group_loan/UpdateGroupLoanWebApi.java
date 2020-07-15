package com.am.frame.member.group_loan;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
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
 *说明：修改联保贷款发起邀请
 */
public class UpdateGroupLoanWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		DB db=null;
		JSONObject resultJson = new JSONObject();
		try {
			 db = DBFactory.newDB();
			 //申请id
			String id = request.getParameter("id");
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
			//删除联保人员ID--数组
			String deletemember = request.getParameter("deletemember");
			//修改贷款申请保存数据
			updateLoanApply(db,id,applyBank,appyName,memberid,money,code,expiresDate,zipCode,address,telephone,phone);
			//增加担保人
			insertApplyGuarantor(db,id,loanMemberid);
			//删除担保人
			daleteApplyGuarantor(db,deletemember);
			
			
			resultJson.put("code", "0");
			resultJson.put("msg", "邀请成功");
		} catch (Exception e) {
				e.printStackTrace();
				try {
					resultJson.put("code", "999");
					resultJson.put("msg", "更新失败!");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
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
	 * 删除担保人
	 * @param db
	 * @param deletemember 删除担保人id
	 * @throws JDBCException 
	 */
	private void daleteApplyGuarantor(DB db, String deletemember) throws JDBCException {

		String member = deletemember.replace("[", "");
		member = member.replace("]", "");
		member = member.replace("\"", "'");
		String[] ary = member.split(",");
		if(ary.length>0){
			for (int i = 0; i < ary.length; i++) {
	            if(!Checker.isEmpty(ary[i])){
	            String deleteSQL=" DELETE FROM mall_loan_appy_guarantor WHERE id="+ary[i]+" ";
	    		
	    		db.execute(deleteSQL);
	            }
			}
		}
		
}
	/**
	 * 修改贷款申请保存数据
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
	private void updateLoanApply(DB db, String id, String applyBank, String appyName,
			String memberid, Double money, String code, String expiresDate,
			String zipCode, String address, String telephone, String phone) throws JDBCException {
		
		Double moneys = money*100;
		int intMoney = (new   Double(moneys)).intValue();
		
		Table table=new Table("am_bdp","mall_loan_appy");
		TableRow updateTr=table.addUpdateRow();
		updateTr.setOldValue("id", id);
		updateTr.setValue("target_org_id", applyBank);
		updateTr.setValue("appy_member_id", memberid);
		updateTr.setValue("appy_amount", intMoney);
		updateTr.setValue("lp_stauts", 2);
		updateTr.setValue("appy_name", appyName);
		updateTr.setValue("id_code", code);
		updateTr.setValue("expires_date", expiresDate);
		updateTr.setValue("zip_code", zipCode);
		updateTr.setValue("address", address);
		updateTr.setValue("telephone", telephone);
		updateTr.setValue("phone", phone);
		
		db.save(table);
		
	}
	/**
	 * 增加担保人
	 * @param db  
	 * @param applyId 借款信息Id 
	 * @param loanMemberid  担保人数组
	 * @throws JDBCException 
	 */
	private void insertApplyGuarantor(DB db, String id, String loanMemberid) throws JDBCException {
		
		String loanMember = loanMemberid.replace("[", "");
		loanMember = loanMember.replace("]", "");
		loanMember = loanMember.replace("\"", "'");
		String[] ary = loanMember.split(",");
		if(ary.length>0){
			for (int i = 0; i < ary.length; i++) {
	            if(!Checker.isEmpty(ary[i])){
	            	String uuid = UUID.randomUUID().toString();
		            String inserSQL=" INSERT INTO mall_loan_appy_guarantor (id,lp_id,member_id,lpg_status) "
		    				+ " VALUES('" +uuid + "','" +id + "',"+ary[i]+",'1' ) ";
		    		
		    		db.execute(inserSQL);
	            }
	        }
		}
	}
		

}