package com.wisdeem.wwd.publicAccount;

import com.fastunit.MapList;
import com.fastunit.Path;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;
import com.wisdeem.wwd.WeChat.exception.WeChatInfaceException;

public class PublicAccountSaveAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String orgid= ac.getVisitor().getUser().getOrgId(); 
		Table table = ac.getTable("WS_PUBLIC_ACCOUNTS");
		Table ctable = new Table("wwd","WS_ACTDETAL");

	    String pid = (ac.getRequestParameter("ws_public_accounts.form.wchat_account")).trim();
		//私有公众帐号的密码
		String password = ac.getRequestParameter("ws_public_accounts.form.password");
		String account_type = ac.getRequestParameter("ws_public_accounts.form.account_type");
		String is_valid = ac.getRequestParameter("ws_public_accounts.form.is_valid");
		String app_id = ac.getRequestParameter("ws_public_accounts.form.app_id");
		String app_secret = ac.getRequestParameter("ws_public_accounts.form.app_secret");
		String token = ac.getRequestParameter("ws_public_accounts.form.token");
		String welcomeword = ac.getRequestParameter("ws_public_accounts.form.welcomeword");
		String explain = ac.getRequestParameter("ws_public_accounts.form.explain");
		
		String regex = "^[0-9]*$";
		if((pid.trim()).matches(regex)){
			//为数字时，先判断是手写的还是选择的
			String sql = "select * from WS_OPERATORS_ACCOUNT where public_id='"+pid+"'";
			MapList list = db.query(sql);
			String wchat_account = "";
			String pwd="";
			String account_belong = "";
			int sceneId =0;
			if(!Checker.isEmpty(list)){
				//证明是为选择的公有公众帐号 ACCOUNT_BELONG=1
				wchat_account = list.getRow(0).get("wchat_account");
				pwd = list.getRow(0).get("password");//公有公众帐号的密码
				account_belong = "1";
				String Querysql = "select max(scene_id) as scene_id from ws_actdetal";
				MapList str = db.query(Querysql);
				String scene_id = str.getRow(0).get("scene_id");
				if(scene_id==""||scene_id==null){
					sceneId  = 1;
				}else{
					sceneId = Integer.parseInt(scene_id)+1;
				}
			}else{
				//证明是私有公众帐号 ACCOUNT_BELONG=0
				wchat_account = (ac.getRequestParameter("ws_public_accounts.form.wchat_account")).trim();
				pwd = ac.getRequestParameter("ws_public_accounts.form.password");
				account_belong = "0";
				sceneId=0;
			}
				TableRow tableRow = table.getRows().get(0);
				tableRow.setValue("orgid",orgid);
				tableRow.setValue("wchat_account",wchat_account);
				tableRow.setValue("password",pwd);
				tableRow.setValue("account_type",account_type);
				if("是".equalsIgnoreCase(is_valid)){
					tableRow.setValue("is_valid",1);
				}else{
					tableRow.setValue("is_valid",3);
				}
				tableRow.setValue("app_id",app_id);
				tableRow.setValue("app_secret",app_secret);
				tableRow.setValue("token",token);
				tableRow.setValue("welcomeword",welcomeword);
				tableRow.setValue("explain", explain);
				tableRow.setValue("account_belong", account_belong);
				db.save(table);
				String publicId = tableRow.getValue("public_id");
				TableRow cupdataTr = ctable.addInsertRow();
				cupdataTr.setValue("public_id", publicId);
				cupdataTr.setValue("orgid", orgid);
				cupdataTr.setValue("scene_id", String.valueOf(sceneId));

				try {
//					if(sceneId!=0){
						String accessToken=Utils.getAccessToken(token,app_id,app_secret);
						String path ="/files/qrcode/limt/"+publicId;
						String fileName=orgid+sceneId+".jpg";
						Utils.getLimitQrcode(accessToken, sceneId, Path.getRootPath()+path, fileName);	
						String qrcode_url=path+"/"+fileName;
						cupdataTr.setValue("qrcode_url", qrcode_url);
						cupdataTr.setValue("scene_type", 1);
						cupdataTr.setValue("data_status", 1);
						db.save(ctable);
//					}
				} catch (WeChatInfaceException e) {
					e.printStackTrace();
				}
				ac.getActionResult().addSuccessMessage("保存成功");
		}else{
            //不为数字时，即为手动写入的时。
			String wchat_account = (ac.getRequestParameter("ws_public_accounts.form.wchat_account")).trim();
			TableRow tableRow = table.getRows().get(0);
			tableRow.setValue("orgid",orgid);
			tableRow.setValue("wchat_account",wchat_account);
			tableRow.setValue("password",password);
			tableRow.setValue("account_type",account_type);
			if("是".equalsIgnoreCase(is_valid)){
				tableRow.setValue("is_valid",1);
			}else{
				tableRow.setValue("is_valid",3);
			}
			tableRow.setValue("app_id",app_id);
			tableRow.setValue("app_secret",app_secret);
			tableRow.setValue("token",token);
			tableRow.setValue("welcomeword",welcomeword);
			tableRow.setValue("explain", explain);
			tableRow.setValue("account_belong", 0);
			db.save(table);
			ac.getActionResult().addSuccessMessage("保存成功");
		}
	}
}
