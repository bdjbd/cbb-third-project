package com.am.cro.OrderSettlement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.common.util.FileUtils;
import com.am.frame.pay.PayManager;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author 张少飞
 * @create 2017/7/14
 * @version 说明：汽车公社订单结算Action，若选择线下支付，则进行转账操作
 * 上传票据图片（在线多文件）   从虚拟字段得到上传路径，存入真实字段
 */
public class OrderSettlementFormAction extends DefaultAction {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// //车辆维修预约表 先保存表单信息
		Table table = ac.getTable("cro_CarRepairOrder");
		db.save(table);
		TableRow tr = table.getRows().get(0);
		// 主键ID
		String id = tr.getValue("id");
		String memberId = tr.getValue("memberid");

		// 票据 得到虚拟字段url
		String bdp_ticket_photo_path = new FileUtils().getFastUnitFilePathJSON(
				"CRO_CARREPAIRORDER", "bdp_ticket_photo_path", id);
		logger.info("票据========" + bdp_ticket_photo_path);

		// 订单的支付方式
		String PayMode = ac
				.getRequestParameter("cro_carrepairorder2.form.paymode");
		// 检查 是否已选择支付方式
		if (Checker.isEmpty(PayMode) || "".equals(PayMode)) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请选择支付方式！");
			ac.getActionResult().setUrl(
					"/am_bdp/cro_carrepairorder2.form.do?m=e");
			return;
		}
		// 材料费 转成数字型 用于计算订单总额
		String MaterialMoney = ac
				.getRequestParameter("cro_carrepairorder2.form.materialmoney");
		// 人工费
		String ArtificialMoney = ac
				.getRequestParameter("cro_carrepairorder2.form.artificialmoney");
		//若汽修厂管理员未输入材料费、人工费，则不保存，进行提示
		if (Checker.isEmpty(MaterialMoney) || Checker.isEmpty(ArtificialMoney)) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请输入人工费、材料费，如果没有，则输入0");
			ac.getActionResult().setUrl(
					"/am_bdp/cro_carrepairorder2.form.do?m=e");

		} else {
		//若汽修厂管理员进行正常报价，输入了材料费、人工费，则计算订单报价总金额，并保存到订单表
			Double MaterialMoneys = Double.parseDouble(MaterialMoney);

			Double ArtificialMoneys = Double.parseDouble(ArtificialMoney);

			// 应付金额 = 材料费 + 人工费
			Double TotalMoneys = MaterialMoneys + ArtificialMoneys;
			// 订单折扣
			double orderdiscount = Double.parseDouble(tr
					.getValue("orderdiscount"));
			// 实付金额
			double amountpaid = 0;
			// 如果折扣为0,实付金额=应付金额，否则实付金额=应付金额*折扣  注意这里人工费有折扣率，材料费没有折扣率
			if (orderdiscount == 0) {
				amountpaid = TotalMoneys;
			} else {
				amountpaid = MaterialMoneys + ArtificialMoneys * orderdiscount;
			}

			String PayState = ""; // 支付状态
			// 如果选择线上支付PayMode='1'，则支付状态仍为未支付PayState='0'，不进行转账操作
			if ("1".equals(PayMode)) {
				PayState = "0";
			} else if ("2".equals(PayMode)) {
			//如果选择线下支付PayMode='2'则将支付状态修改为已支付PayState='1'，进行转账操作
				PayState = "1";
				//支付管理类
				PayManager payManager = new PayManager();
				//更新会员积分(积分为整数)
				payManager.updateMemberScore(memberId,
						new Double(amountpaid).intValue(), db);
				//在会员积分记录表中，新增一条积分记录，注意新增积分为整数
				payManager.addMemberScore(memberId,
						new Double(amountpaid).intValue(), "", db);
				//修改会员等级（先查找当前机构的下一等级，若积分不够，则不升级；若已是最高会员，则也不升级）
				payManager.updateMemberLevel(memberId, db);
			}
			// 修改订单报价总金额、实际应付金额（含折扣）、票据照片真实字段url，注意此处修改了支付状态，并未修改订单状态
			String updateSql1 = " update cro_CarRepairOrder set TotalMoney = "
					+ TotalMoneys
					+ ",amountpaid="
					+ amountpaid
					+ ",PayState = '"
					+ PayState
					+ "',towing_settlement_audit_state = '1',driving_settlement_audit_state='1',ticket_photo_path='"
					+ bdp_ticket_photo_path + "' where id = '" + id + "' ";
			db.execute(updateSql1);
		}

	}

}
