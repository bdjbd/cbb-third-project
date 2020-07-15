package com.am.cro.car;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author 张少飞
 *@create 2017/9/6
 *@version
 *说明：判断用户输入的车架号，在当前汽修厂名下是否已存在，避免重复
 */
public class checkCarFrameNumberIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
	
		//当前汽修厂的机构id  
		String orgcode = request.getParameter("orgcode");
		//会员输入的车架号
		String carframenumber = request.getParameter("carframenumber");
		
		String checkSql="select CarFrameNumber from cro_CarManager where OrgCode = '"+orgcode+"' and CarFrameNumber = '"+carframenumber+"' ";
		DB db=null;
		//默认该车架号已存在,提示信息为“no”
		String message = "no"; 
		try {
			db = DBFactory.newDB();
			MapList list = db.query(checkSql);
			//若当前修车厂名下，不存在该车架号，则返回正常提示；
			if (Checker.isEmpty(list)) {
				message = "ok";
			}
		} catch (JDBCException e) {
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
		return message;
	}

}
