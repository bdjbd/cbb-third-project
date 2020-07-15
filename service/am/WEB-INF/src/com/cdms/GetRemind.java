package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 获取提醒数据
* @author guorenjie  
* @date 2018年5月2日
 */
public class GetRemind implements IWebApiService {
	Logger logger =  LoggerFactory.getLogger(getClass());
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String orgid=request.getParameter("orgid");//当前登录人所属机构
		String remindtype=request.getParameter("remindtype");//提醒类型
		DBManager db=new DBManager();
		CheckAlarmAndRemind cRemind = new CheckAlarmAndRemind();//获取提醒的maplist
	    JSONArray rValue = new JSONArray();
	    //没有选择类型时显示全部
	    if(Checker.isEmpty(remindtype)){
	    	MapList by = cRemind.getBY(db, orgid);
	    	by.add(cRemind.getNJ(db, orgid));
	    	by.add(cRemind.getBX(db, orgid));
	    	rValue = db.mapListToJSon(by);
	    }else{
		    //保养提醒
		    if("1".equals(remindtype)){
		    	rValue = db.mapListToJSon(cRemind.getBY(db, orgid));
		    }
		    //年检提醒
		    if("2".equals(remindtype)){
		    	rValue = db.mapListToJSon(cRemind.getNJ(db, orgid));
		    }
		    //保险提醒
		    if("3".equals(remindtype)){
		    	rValue = db.mapListToJSon(cRemind.getBX(db, orgid));
		    }
	    }
		return rValue.toString();
	}

}
