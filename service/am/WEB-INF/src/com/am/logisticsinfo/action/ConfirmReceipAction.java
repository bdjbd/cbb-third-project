package com.am.logisticsinfo.action;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.support.Action;
import com.fastunit.util.Checker;

/**
 * @author 作者：yangdong
 * @date 创建时间：2016年5月4日 下午4:04:20
 * @version 确认收货action
 */
public class ConfirmReceipAction implements Action {

	/** 机构社员现金账户 **/
	public static final String GROUP_CASH_ACCOUNT = SystemAccountClass.GROUP_CASH_ACCOUNT;
	/** 社员现金账户 **/
	public static final String CASH_ACCOUNT = SystemAccountClass.CASH_ACCOUNT;

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		DB db = DBFactory.newDB();
		
		// 获取id
		String id = ac.getRequestParameter("mall_logistics_info.form.id");
		String materialName = ac.getRequestParameter("mall_logistics_info.form.material_name");


		// 查询物流车辆信息
		String queryLogisticsVehiclesSQL = " SELECT m.id,m.membername " + "   FROM mall_logistics_info AS mli "
				+ "   LEFT JOIN mall_recv_logistics_info AS mrli ON mli.id=mrli.order_id AND mrli.r_type=2 "
				+ "   LEFT JOIN am_member AS m ON mrli.member_id=m.id  " + "   WHERE mli.id = '" + id + "' ";
		MapList logisticsVehiclesList = db.query(queryLogisticsVehiclesSQL);
		String inMemberId = "";
		if (!Checker.isEmpty(logisticsVehiclesList)) {
			inMemberId = logisticsVehiclesList.getRow(0).get("purchaser");
		};

		String outMemberId = ac.getRequestParameter("mall_logistics_info.form.orgid");
		String virementNumber = Double
				.toString(Double.parseDouble(ac.getRequestParameter("mall_logistics_info.form.freight"))
						);
		//+ Double.parseDouble(ac.getRequestParameter("mall_logistics_info.form.deposit")

		String inreamarks = "配送物资" + materialName + "，客户确认收货，客户支付运费。";
		String onreamarks= "确认收货支付运费，物资名称：" + materialName + "。";

		// 转账
		VirementManager virementManager = new VirementManager();
		JSONObject resultJson = virementManager.execute(
				db, outMemberId, "", GROUP_CASH_ACCOUNT,"",
				virementNumber,"", onreamarks, "", false);

		if (resultJson != null && "0".equals(resultJson.get("code"))) {
			
			
			//更新接单时间
			String updateLogisticsInfoSQL = " UPDATE mall_logistics_info SET status = 4,recv_time=now() WHERE id = '" + id
					+ "' ";
			db.execute(updateLogisticsInfoSQL);
			
			updateLogisticsInfoSQL="SELECT * FROM mall_recv_logistics_info WHERE order_id=?  ORDER BY create_time DESC";
			
			MapList map=db.query(updateLogisticsInfoSQL,new String[]{
					id
			},new int[]{
					Type.VARCHAR
			});
			
			if(!Checker.isEmpty(map)){
				String deposit=map.getRow(0).get("deposit");//获取押金
				
				inMemberId=map.getRow(0).get("member_id");
				
				//转入运费
				resultJson = virementManager.execute(db, "", inMemberId, "",SystemAccountClass.CASH_ACCOUNT, virementNumber, inreamarks, "", "", false);
				
				
				inreamarks = "配送物资" + materialName + "，客户确认收货。退货押金。";
				// 转账，退货押金
				resultJson = virementManager.execute(db, "", inMemberId, "",SystemAccountClass.CASH_ACCOUNT, deposit, inreamarks, "", "", false);
				
				logger.info(inreamarks+"  resultJson:"+resultJson);
			}
			
			
			
			ac.getActionResult().addSuccessMessage("已成功收货！");
		} else {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage(resultJson.getString("msg"));
		}
		
		if(db!=null){
			db.close();
		}
		
		return ac;
	}

}
