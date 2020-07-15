package com.am.frame.webapi;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年10月8日
 * @version 
 * 说明:<br />
 * 理性农业 后台登陆注册或是设备信息接口
 */
public class TerminalInitWebApi implements IWebApiService {

	private Logger logger=Logger.getLogger(getClass());
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject result=new JSONObject();
		
		String miei=request.getParameter("MIEI");//			params.put("MIEI", app.getMIEI());
		String lineNumber=request.getParameter("LINENUMBER");//		params.put("LINENUMBER",app.getLineNumber());
		String deviceModel=request.getParameter("DEVICEMODEL");//		params.put("DEVICEMODEL",app.getDeviceModel());
		String versionSDK=request.getParameter("VERSIONSDK");//		params.put("VERSIONSDK",app.getVersionSdk());
		String versionRelease=request.getParameter("VERSIONRELEASE");//		params.put("VERSIONRELEASE",app.getVersionRelease());
		
		DB db=null;
		
		logger.info("miei:"+miei);
		
		try{
			db=DBFactory.newDB();
			
			//1，根据MIEI 查询设备是否已经存在，如果存在，则更新最后登录时间，如果不存在。
			Table table=new Table("am_bdp","AM_REGISTER_TERMINAL");
			TableRow tr=null;
			
			String querySQL="SELECT * FROM am_register_terminal WHERE miei=? ";
			MapList mieiMap=db.query(querySQL, miei,Type.VARCHAR);
			
			if(!Checker.isEmpty(mieiMap)){
				//2，如果存在，更新最后登录时间 last_update_time，并查询设备授权机构返回。
				tr=table.addUpdateRow();
				tr.setOldValue("id",mieiMap.getRow(0).get("id"));
				tr.setOldValue("miei",miei);
				tr.setValue("last_update_time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//last_update_time
				db.save(table);
				
				result.put("ORGID",mieiMap.getRow(0).get("orgid"));
				result.put("IS_AUTHOR",mieiMap.getRow(0).get("is_author"));
				
				
			}else{
				//3，如果不存在，则新增记录数据,同事查询订单，修改订单状态为已收货
				tr=table.addInsertRow();
				tr.setValue("miei",miei);//miei
				tr.setValue("linenumber", lineNumber);//LINENUMBER
				tr.setValue("devicemodel",deviceModel);//DEVICEMODEL
				tr.setValue("versionsdk", versionSDK);//VERSIONSDK
				tr.setValue("versionrelease",versionRelease);//VERSIONRELEASE
				
				tr.setValue("last_update_time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//last_update_time
				db.save(table);
				
			}
			
		}catch(Exception e){
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
		
		return result.toString();
	}

}
