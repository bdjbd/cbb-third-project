package com.am.more.expert_compound;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月19日
 *@version
 *说明：新增保存/发布文章WebApi
 */
public class SaveArtclesIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
		
		//uuid
		String uuid = request.getParameter("uuid");
		//标题
		String title = request.getParameter("title");
		//技术栏目
		String payType = request.getParameter("pay_type");
		//会员id
		String memberid = request.getParameter("memberid");
		//内容
		String content = request.getParameter("content");
		//状态
		String status = request.getParameter("status");
		//保存还是保存发布 1：保存  2：保存发布
		String start = request.getParameter("start");
		//图片
		String imagepaths = request.getParameter("imagepaths");
		resultJson = new JSONObject();
		
		if(!Checker.isEmpty(imagepaths)){
			imagepaths=imagepaths.replaceAll("\\\\", "/");
		}
			//判断是保存还是保存发布
			if("1".equals(start)){
				//保存文章
				SaveArtclse(uuid,title,payType,memberid,content,status,imagepaths,db);
			}else{
				//保存发布文章
				SavePublishArtclse(uuid,title,payType,memberid,content,status,imagepaths,db);
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
		
		
		return resultJson.toString();
	}
	/**
	 * 保存并发布文章
	 * @param title  标题
	 * @param payType  技术栏目
	 * @param memberid  发布人
	 * @param content  内容
	 * @param status  状态
	 * @param imagepaths 
	 * @param db
	 * @throws JDBCException 
	 */
	private void SavePublishArtclse(String uuid,String title, String payType,
			String memberid, String content, String status, String imagepaths, DB db) throws JDBCException {
		String inserPublishSQL=" INSERT INTO mall_cope_article (id,category_id,c_title,c_content,status,member_id,create_time,c_enclosure) "
				+ " VALUES('" +uuid + "','" +payType + "','"+title+"','"+content+"','2', '"+memberid+"','now()','"+imagepaths+"' ) ";
		
		db.execute(inserPublishSQL);
	}
	/**
	 * 保存文章
	 * @param title  标题
	 * @param payType  技术栏目
	 * @param memberid  发布人
	 * @param content  内容
	 * @param status  状态
	 * @param imagepaths 
	 * @param db 
	 * @throws JDBCException 
	 */
	private void SaveArtclse(String uuid,String title, String payType, String memberid,
			String content, String status, String imagepaths, DB db) throws JDBCException {
		String inserSQL=" INSERT INTO mall_cope_article (id,category_id,c_title,c_content,status,member_id,create_time,c_enclosure) "
				+ " VALUES('" +uuid + "','" +payType + "','"+title+"','"+content+"','1', '"+memberid+"','now()','"+imagepaths+"' ) ";
		
		db.execute(inserSQL);
	}

}
