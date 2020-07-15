package com.am.app_plugins.rural_market.my_booth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.common.util.FileUtils;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016.06.12
 *@version
 *说明：修改大市场WebApi
 */
public class UpdateMarketIWebApi implements IWebApiService{

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
		//分类
		String classId = request.getParameter("class_id");
		//内容
		String content = request.getParameter("content");
		//社员id
		String memberid = request.getParameter("memberid");
		//金额
		String price = request.getParameter("price");
		//省
		String recvProvince = request.getParameter("recvProvince");
		//市
		String recvCity = request.getParameter("recvCity");
		//区
		String recvArea = request.getParameter("recvArea");
		//图片
		String imagepaths = request.getParameter("imagepaths");
		resultJson = new JSONObject();
		//出账类型
		//String outAccountCode  = SystemAccountClass.CASH_ACCOUNT;
		//入账类型
		//String inAccountCode  = SystemAccountClass.ANTI_RISK_SELF_SAVING_ACCOUNT;
		//手续费
		String free = Var.get("marketplace_release_free");
		String iremakers ="发布农村大市场手续费";
		String selfree = "";
		if(!Checker.isEmpty(imagepaths)){
			imagepaths=imagepaths.replaceAll("\\\\", "/");
		}
		//根据订单id查询交易是否成功未处理
		String selSql="SELECT * FROM mall_marketplace_entity WHERE id='"+uuid+"' ";
		
		MapList selMap = db.query(selSql);
		if(!Checker.isEmpty(selMap)){
			selfree = selMap.getRow(0).get("free");
		}
		if("0".equals(selfree)){
			//保存大市场
			SaveMarket(uuid,title,classId,content,imagepaths,price,recvProvince,recvCity,recvArea,free,db);
			//VirementManager server = new VirementManager();
			//扣除手续费（将现金账户钱转到自救金账户）
			//resultJson = server.execute(db,memberid,memberid,outAccountCode,inAccountCode,free,iremakers,iremakers,"",false);
		}else{
			SaveMarket(uuid,title,classId,content,imagepaths,price,recvProvince,recvCity,recvArea,free,db);
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

	private void SaveMarket(String uuid, String title, String classId,
			String content, String imagepaths, String price,
			String recvProvince, String recvCity, String recvArea, String free, DB db) throws JDBCException {
		String saveSQL=" UPDATE mall_marketplace_entity SET "
				+ " title='"+title+"', "
				+ " class_id='"+classId+"', "
				+ " me_content='"+content+"', "
				+ " list_images ='"+imagepaths+"', "
				+ " price="+price+"*100,"
				+ " province='"+recvProvince+"', "
				+ " city ='"+recvCity+"', "
				+ " zone='"+recvArea+"', "
				//+ " free="+free+"*100, "
				+ " status='1' WHERE id='"+uuid+"'";
		
		db.execute(saveSQL);
		//同步文件
		new FileUtils().syncFilesByFilePath("MALL_MARKETPLACE_ENTITY", "bdp_list_images", uuid, imagepaths);
	}

}
