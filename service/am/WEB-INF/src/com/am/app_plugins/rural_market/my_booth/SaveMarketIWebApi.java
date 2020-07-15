package com.am.app_plugins.rural_market.my_booth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.app_plugins.rural_market.server.RuralMarketServer;
import com.am.frame.systemAccount.SystemAccountClass;
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
 *说明：新增保存/发布大市场WebApi
 */
public class SaveMarketIWebApi implements IWebApiService{

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
		//会员id
		String memberid = request.getParameter("memberid");
		//内容
		String content = request.getParameter("content");
		//金额
		String price = request.getParameter("price");
		//状态
		String status = request.getParameter("status");
		//省
		String recvProvince = request.getParameter("recvProvince");
		//市
		String recvCity = request.getParameter("recvCity");
		//区
		String recvArea = request.getParameter("recvArea");
		//保存还是保存发布 1：保存  2：保存发布
		String start = request.getParameter("start");
		//图片
		String imagepaths = request.getParameter("imagepaths");
		resultJson = new JSONObject();
		//出账类型
		String outAccountCode  = SystemAccountClass.CASH_ACCOUNT;
		//入账类型
		String inAccountCode  = SystemAccountClass.ANTI_RISK_SELF_SAVING_ACCOUNT;
		//手续费
		String free = Var.get("marketplace_release_free");
		String iremakers ="发布农村大市场手续费";
		
		//数据状态 0=新增数据，1=修改数据
		String data_status = request.getParameter("data_status");
		
		if(!Checker.isEmpty(imagepaths)){
			imagepaths=imagepaths.replaceAll("\\\\", "/");
		}
			//判断是保存还是保存发布
//			if("1".equals(start)){
				//保存大市场
		RuralMarketServer server = new RuralMarketServer();
		if("0".equals(data_status))
		{
			server.SaveMarket(uuid,title,classId,memberid,content,status,imagepaths,price,recvProvince,recvCity,recvArea,db);
		}else
		{
			server.UpdateMarket(uuid,title,classId,memberid,content,status,imagepaths,price,recvProvince,recvCity,recvArea,db);
		}
		

				resultJson.put("code","0");
				resultJson.put("start",start);
				
				//			}else{
//				//保存发布大市场
//				SavePublishMarket(uuid,title,classId,memberid,content,status,imagepaths,price,recvProvince,recvCity,recvArea,free,db);
//				
//				VirementManager server = new VirementManager();
//				//扣除手续费（将现金账户钱转到自救金账户）
//				resultJson = server.execute(db,memberid,memberid,outAccountCode,inAccountCode,free,iremakers,iremakers,"",false);
//			}
		} catch (Exception e) {
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
	
}
