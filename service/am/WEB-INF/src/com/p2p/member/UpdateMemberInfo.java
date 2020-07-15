package com.p2p.member;

import com.fastunit.jdbc.DBFactory;

/**
 * 更新用户信息
 * @author Administrator
 *
 */
public class UpdateMemberInfo {

	/**
	 * 根据电话号码更新用户密码
	 * @param phone 电话号码
	 * @param nativePassword 原始密码 未加密密码
	 * @return boolean， 返回true表示成功，返回false表示更新失败
	 */
	public boolean updatePassword(String phone,String nativePassword){
		boolean result=false;
		try{
			String desigStr=com.p2p.Utils.getMD5Str(nativePassword);
			String updateSQL="UPDATE ws_member  SET wshaop_password='"+desigStr
					+"' WHERE phone='"+phone+"'";
			if(DBFactory.getDB().execute(updateSQL)>=0){
				result=true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return result;
		}
	}
}
