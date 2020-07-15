package com.am.frame.servicecommodity.action;


import com.am.frame.transactions.virement.VirementManager;
import com.am.tools.Utils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  wz  
 * @descriptions  后台向自己转账action 
 * @date 创建时间：2016年4月21日 下午3:34:43 
 * @version 1.0   
 */
public class MallServiceCommoditySaveAction extends DefaultAction{


		@Override
		public void doAction(DB db, ActionContext ac) throws Exception {
			
			//服务类商品价格
			String price=ac.getRequestParameter("mall_service_commodity.form.price");
			
			//运营终端售价(元)
			String tearminSealPricef=ac.getRequestParameter("mall_service_commodity.form.tearmin_seal_pricef");
			
			//空间年使用费单价(元)
			String spaceEveyearUseFreef=ac.getRequestParameter("mall_service_commodity.form.space_eveyear_use_freef");
			
			if(price==null&&"".equals(price)){
				price="0";
			}
			
			if(Checker.isEmpty(tearminSealPricef)){
				tearminSealPricef="0";
			}
			
			if(Checker.isEmpty(spaceEveyearUseFreef)){
				spaceEveyearUseFreef="0";
			}
			
			//运营终端售价(分)
			Long tearminSealPriceL=VirementManager.changeY2F(tearminSealPricef);
			
			//空间年使用费单价(分)
			Long spaceEveyearUseFreeL=VirementManager.changeY2F(spaceEveyearUseFreef);
			
			
			Long money =VirementManager.changeY2F(price);// Long.parseLong(((Double.parseDouble(price)*100)+"").substring(0,((Double.parseDouble(price)*100)+"").indexOf(".") ));
			
			Table table = ac.getTable("mall_service_commodity");
			
			table.getRows().get(0).setValue("price", money);
			table.getRows().get(0).setValue("tearmin_seal_price", tearminSealPriceL);
			table.getRows().get(0).setValue("space_eveyear_use_free", spaceEveyearUseFreeL);
			
			db.save(table);
			
			String id=table.getRows().get(0).getValue("id");
			String updateSql ="";
			
			String fileName = Utils.getFastUnitFilePath("mall_service_commodity","bdp_list_img", id);
			if (fileName != null && fileName.length() > 1) {
				fileName = fileName.substring(0, fileName.length() - 1);
				updateSql = "UPDATE mall_service_commodity  SET list_img ='" + fileName
						+ "'  WHERE id='" + id + "' ";
				
				db.execute(updateSql);
			}
			
			ac.setSessionAttribute("am_bdp.mall_service_commodity.form.id", id);
		}

}
