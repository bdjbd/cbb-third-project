package com.wisdeem.wwd.WeChat.validator;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.support.Action;
import com.wisdeem.wwd.WeChat.Utils;
import com.wisdeem.wwd.WeChat.exception.WeChatInfaceException;

/**
 *   说明:
 * 		公众帐号数据验证
 *   @creator	岳斌
 *   @create 	Nov 25, 2013 
 *   @version	$Id
 */
public class PublicAccountVaildator  implements Action {
	
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		Ajax ajax=new Ajax(ac);
		String token=ac.getRequestParameter("token");
		String appId=ac.getRequestParameter("app_id");
		String appSecrect=ac.getRequestParameter("app_secret");
		String isVaild=ac.getRequestParameter("is_valid");
		
		if(appId==null||appSecrect==null||appId.isEmpty()||appSecrect.isEmpty()){
			ajax.addScript("alert('请输入数据')");
			ajax.send();
			return ac;
		}else{
			try {
				String result=Utils.getAccessToken(token, appId, appSecrect);
				boolean vaild=Utils.checkVaildate(result);
				if(result!=null&&vaild){
					ajax.addScript("setIsVaild(1);enableSaveBtn();");
					ajax.send();
					return ac;
				}else{
					ajax.addScript("setIsVaild(3);enableSaveBtn();");
					ajax.send();
					return ac;
				}
				
			} catch (WeChatInfaceException e) {
				e.printStackTrace();
				ajax.addScript("alert('输入有误，请检查')");
				ajax.send();
				return ac;
			}finally{
				ajax.send();
				return ac;
			}
		}
	}
}
