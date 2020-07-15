package com.am.cro.OrderSettlement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.common.util.FileUtils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author 张少飞
 * @create 2017/7/14
 * @version 说明：汽车公社订单报价保存Action  更新订单总报价，只报价不修改订单状态
 */
public class OrderSettlementFormSaveAction extends DefaultAction {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// //车辆维修预约表 先保存表单信息
		Table table = ac.getTable("cro_CarRepairOrder");
		db.save(table);
		TableRow tr = table.getRows().get(0);
		// 主键ID
		String id = tr.getValue("id");

		// 票据虚拟字段 在线多文件 url
		String bdp_ticket_photo_path = new FileUtils().getFastUnitFilePathJSON(
				"CRO_CARREPAIRORDER", "bdp_ticket_photo_path", id);
		logger.info("票据========" + bdp_ticket_photo_path);
		// 材料费 转成数字型 用于计算订单总额
		String MaterialMoney = ac
				.getRequestParameter("cro_carrepairorder2.form.materialmoney");
		// 人工费
		String ArtificialMoney = ac
				.getRequestParameter("cro_carrepairorder2.form.artificialmoney");
		//若汽修厂管理员未输入材料费、人工费，则保存失败，进行提示
		if (Checker.isEmpty(MaterialMoney) || Checker.isEmpty(ArtificialMoney)) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请输入人工费、材料费，如果没有，则输入0");
			ac.getActionResult().setUrl(
					"/am_bdp/cro_carrepairorder2.form.do?m=e");
		//若汽修厂管理员进行正常报价，输入了材料费、人工费，则计算订单报价总金额，并保存到订单表
		} else {
			Double MaterialMoneys = Double.parseDouble(MaterialMoney);

			Double ArtificialMoneys = Double.parseDouble(ArtificialMoney);

			// 应付金额 = 材料费 + 人工费
			Double TotalMoneys = MaterialMoneys + ArtificialMoneys;
			// 订单折扣
			double orderdiscount = Double.parseDouble(tr
					.getValue("orderdiscount"));
			// 实付金额
			double amountpaid = 0;
			// 如果折扣为0,实付金额=应付金额;如果折扣不为0，实付金额=应付金额*折扣
			if (orderdiscount == 0) {
				amountpaid = TotalMoneys;
			} else {
			//若折扣率不为0，则实付金额 = 材料费+人工费*折扣率          注意这里人工费有折扣率，材料费没有折扣率
				amountpaid = MaterialMoneys + ArtificialMoneys * orderdiscount;
			}
			// 修改订单报价总金额、实际应付金额（含折扣）、票据照片真实字段url
			String updateSql1 = " update cro_CarRepairOrder set TotalMoney = "
					+ TotalMoneys + ",amountpaid = " + amountpaid
					+ ",ticket_photo_path='" + bdp_ticket_photo_path
					+ "' where id = '" + id + "' ";
			db.execute(updateSql1);
		}

	}

}
