package com.am.app_plugins.precision_help;

import org.json.JSONObject;

import com.am.app_plugins.precision_help.server.PrecisionHelpServer;
import com.am.frame.member.MemberManager;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;


/**
 * 消费者帮扶农民投资村头冷库业务回调
 * @author yuebin
 * 
 * 1，给农民投资农民对应的村头冷库
 *
 */
public class ConsumerHelpInvestrAgricultureProjectsBusinessCall extends AbstraceBusinessCallBack {

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		
		if(!"1".equals(type))
		{
			updateProcessBuissnes(id, db, "1");
			return null;
		}
		
		if(checkTradeState(id, business, db, type)&&!checkProcessBuissnes(id, db)){
			
			//业务参数
			JSONObject businessJs=new JSONObject(business);
//			business:{
//                'stock_price':$scope.pageData.stock_price,//投资项目单固金额
//			      'stock_number':1,//帮扶股数，默认为1股
//                'help_member_id':$scope.pageData.helpMemberId, //  被帮扶者ID
//				  'roleid':roleid,// 帮扶着类型
//                'pay_id':uuid,
//                'memberid':$scope.pageData.memberid,//帮扶者id
//                "orders":uuid,
//                "paymoney":$scope.pageData.stock_price, //帮扶金额
//                "success_call_back":'com.am.app_plugins.precision_help.ConsumerHelpInvestrAgricultureProjectsBusinessCall' 
//            }
			
			//被帮扶者ID
			String helpMemberId="";
			//帮扶股数，默认为1股
			String stockNumber="";
			//投资项目单位金额  单位元
			String stockPrice="";
			//帮扶者ID
			String memberId="";
			
			JSONObject customerParamsObj = new JSONObject();
			
			if(!businessJs.has("customer_params"))
			{
				helpMemberId = businessJs.getString("help_member_id");
				stockNumber=businessJs.getString("stock_number");
				stockPrice=businessJs.getString("stock_price");
				memberId=businessJs.getString("memberid");
			}else
			{
				customerParamsObj = businessJs.getJSONObject("customer_params");
				helpMemberId = customerParamsObj.getString("help_member_id");
				stockNumber=customerParamsObj.getString("stock_number");
				stockPrice=customerParamsObj.getString("stock_price");
				memberId=customerParamsObj.getString("memberid");
			}
			
//			MemberManager mm=new MemberManager();
//			String memberName=mm.getMemberById(memberId, db).getRow(0).get("membername");
			
			//获取帮扶者信息  
			String memberName = "";
			MemberManager mm=new MemberManager();
			MapList memberMap=mm.getMemberById(memberId, db);
			//判断帮扶者是否为组织机构 2016年12月22日添加
			if(!Checker.isEmpty(memberMap)){
				memberName = memberMap.getRow(0).get("membername");
			}else{
				String nameSQL = "select u_name from service_mall_info where orgid = '"+ memberId+ "'";
				MapList namesql = db.query(nameSQL);
				if(namesql.size()>0){
					memberName = namesql.getRow(0).get("u_name");			
				}
			}
			
			PrecisionHelpServer helpServer = new PrecisionHelpServer();
			helpServer.farmProjectInvest(db, helpMemberId, memberName, Long.parseLong(stockNumber), Double.parseDouble(stockPrice));
			
			helpServer.updateHelpInfo(db, helpMemberId,memberId,
						VirementManager.changeY2FInt((Double.parseDouble(stockPrice)*Long.parseLong(stockNumber))+""));
			
			//更新处理业务
			updateProcessBuissnes(id, db, "1");
		}
		
		return null;
	}

}
