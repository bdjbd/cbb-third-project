package com.am.more.expert_compound;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.common.util.FileUtils;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月19日
 *@version
 *说明：修改保存/发布文章WebApi
 */
public class UpdateArtclesIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
		
		//标题
		String title = request.getParameter("title");
		//技术栏目
		String payType = request.getParameter("pay_type");
		//内容id
		String id = request.getParameter("id");
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
			SaveArtclse(title,payType,id,content,status,imagepaths,db);
		}else{
			//保存发布文章
			SavePublishArtclse(title,payType,id,content,status,imagepaths,db);
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
	private void SavePublishArtclse(String title, String payType,
			String id, String content, String status, String imagepaths, DB db) throws JDBCException {
		
		String inserPublishSQL=" UPDATE mall_cope_article SET "
				+ " category_id='"+payType+"', "
				+ " c_title='"+title+"', "
				+ " c_content='"+content+"', "
				+ " status='2',"
				+ " c_enclosure='"+imagepaths+"' WHERE id='"+id+"'";
		
		db.execute(inserPublishSQL);
		//同步文件
		new FileUtils().syncFilesByFilePath("MALL_COPE_ARTICLE", "bdp_c_enclosure", id, imagepaths);
		
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
	private void SaveArtclse(String title, String payType, String id,
			String content, String status, String imagepaths, DB db) throws JDBCException {
		String inserSQL=" UPDATE mall_cope_article SET "
				+ " category_id='"+payType+"', "
				+ " c_title='"+title+"', "
				+ " c_content='"+content+"', "
				+ " status='1', "
				+ " c_enclosure='"+imagepaths+"' WHERE id='"+id+"'";
		
		db.execute(inserSQL);
		//同步文件
		new FileUtils().syncFilesByFilePath("MALL_COPE_ARTICLE", "bdp_c_enclosure", id, imagepaths);
	}

}
