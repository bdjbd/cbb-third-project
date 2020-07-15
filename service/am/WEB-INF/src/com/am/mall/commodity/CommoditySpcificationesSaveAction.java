package com.am.mall.commodity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * @author YueBin
 * @create 2014年11月11日
 * @version 
 * 说明:<br />
 * 商品规格保存，需要计算出最小的价格，然后保存在商品主表的price字段中。
 * 
 */
public class CommoditySpcificationesSaveAction extends DefaultAction {

	private String[] weeks=new String[]{"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//呼旅，如果是日期类型的需要检查日期的唯一性
		//获取商品所属分类
		String mallClassId=ac.getRequestParameter("mall_class");
		
		if(Checker.isEmpty(mallClassId)){
			mallClassId=(String)ac.getSessionAttribute("mall_commodity_space.mall_class");
		}
		
		//商品ID
		String commodityid=ac.getRequestParameter("mall_commodityspecifications.form_fs.commodityid");
		//日期
		String dates=ac.getRequestParameter("mall_commodityspecifications.form_fs.dates");
		//主键
		String id=ac.getRequestParameter("mall_commodityspecifications.form_fs.id.k");
		
		//租车5和特产6与日期无关
		if("5".equals(mallClassId)||"6".equals(mallClassId)){
			
		}else{
			//检查日期的唯一性
			String checkSQL="SELECT * FROM mall_CommoditySpecifications "
					+ " WHERE dates='"+dates+"' AND  commodityid='"+commodityid+"' ";
			
			if(!Checker.isEmpty(id)){
				checkSQL+=" AND id <>'"+id+"' ";
			}
			
			MapList map=db.query(checkSQL);
			
			if(!Checker.isEmpty(map)){
				//数据已经存在，不能新增了
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("日期"+dates+"的数据已存在。");
				return;
			}
		}
		
		
		//商品规格表
		Table specificationsTab=ac.getTable("mall_commodityspecifications");
		//商品详情表
		Table materTab=ac.getTable("p2p_commoditydetail");
		
		//保存主表上个规格数据
		db.save(specificationsTab);
		
		//获取规格ID
		TableRow tr=specificationsTab.getRows().get(0);
		id=tr.getValue("id");
		String date=tr.getValue("dates");
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		
		if(!Checker.isEmpty(date)){
			Date data=sdf.parse(date);
			
			Calendar calendar = Calendar.getInstance();      
			calendar.setTime(data);  
			
			String week=weeks[calendar.get(Calendar.DAY_OF_WEEK)-1];
			
			String updateSQL="UPDATE mall_commodityspecifications SET week=? WHERE id=?";
			db.execute(updateSQL,new String[]{
					week,id
			},new int[]{
					Type.VARCHAR,Type.VARCHAR
			});
		}
		
		
		//如果商城分类是酒店和租车，需要处理库存
		if("2".equals(mallClassId)||"6".equals(mallClassId)){
			
		}else{
			long stock=999999999;
			
			String updaeSQL="UPDATE mall_commodityspecifications SET stock="+stock+" WHERE id=? ";
			db.execute(updaeSQL,id, Type.VARCHAR);
		}
		
		
		//设置子表商品详情规格ID
		if(!Checker.isEmpty(materTab)){
			for(int i=0;i<materTab.getRows().size();i++){
				materTab.getRows().get(i).setValue("comdityformatid",id);
			}
		}
		
		//保存商品详情信息
		db.save(materTab);
		
		//计算规格价格中的最小值  am_bdp.mall_commodity.form.id
		String comdityId=(String)ac.getSessionAttribute("am_bdp.mall_commodity.form.id");
		
		//保存id到session中
		ac.setSessionAttribute("am_bdp.mall_commodityspecifications.form_fs.id", id);
		
		//2016-07-11 修改需求 不需要更新最小值到商品价格中.
//		updateMinUpdateToCommodity(db,comdityId);  
		
		//保存图片bdp_specimages  specimages  MALL_COMMODITYSPECIFICATIONS
		String mainimages=Utils.getFastUnitFilePath("MALL_COMMODITYSPECIFICATIONS", "bdp_specimages", id);
				
		if(!Checker.isEmpty(mainimages)){
			mainimages=mainimages.substring(0, mainimages.length()-1);
			String updateSql="UPDATE MALL_COMMODITYSPECIFICATIONS  SET specimages='"
					+mainimages+"'  WHERE id='"+id+"' ";
			
			db.execute(updateSql);
		}
		
	}

	
	/**
	 * 更新商品基本信息中的价格为最小值.
	 * @param db
	 * @param comdityId
	 * @throws JDBCException 
	 */
	private void updateMinUpdateToCommodity(DB db, String comdityId) throws JDBCException {
		String updatePriceSQL=
				"UPDATE mall_commodity SET price=( "+
				"	SELECT min(price) FROM mall_CommoditySpecifications "+
				"	WHERE commodityid='"+comdityId+"') "+
				"	WHERE id='"+comdityId+"' ";
		
		db.execute(updatePriceSQL);
	}
	
}
