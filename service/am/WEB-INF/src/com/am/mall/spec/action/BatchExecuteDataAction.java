package com.am.mall.spec.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jgroups.util.UUID;

import com.am.common.util.DateUtils;
import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.AjaxAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月20日
 * @version 
 * 说明:<br />
 * 
 */
public class BatchExecuteDataAction extends AjaxAction {
	
	private String[] weeks=new String[]{"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
	
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		Ajax ajax=new Ajax(ac);
		
		//商品ID
		String comdId=(String)ac.getSessionAttribute("am_bdp.mall_commodity.form.id");
		
		//开始时间
		String inputStartDate=ac.getRequestParameter("inputStartDate");
		//结束时间
		String inputEndDate=ac.getRequestParameter("inputEndDate");
		
		//儿童价格
		String children_price=ac.getRequestParameter("childPrice");
		
		//生成类型  1：整周；2：选择星体
		String intpuDataeTypee=ac.getRequestParameter("intpuDataeType");
		//价格
		String pirce=ac.getRequestParameter("pirce");
		//库存
		String store=ac.getRequestParameter("store");
		//选择的周 
		String weeks=ac.getRequestParameter("weeks");
		
		DateUtils dateUtils=new DateUtils();
		
		if("".equals(inputStartDate)||"".equals(inputEndDate) ){
			ajax.addScript("alert('请设置日期 !')");
			ajax.send();
			return super.execute(ac);
		}
		
		List<Date> days=dateUtils.getDiffDate(inputStartDate, inputEndDate);
		
		//获取商品所属分类
		String mallClassId=ac.getRequestParameter("mall_class");
		
		if(Checker.isEmpty(mallClassId)){
			mallClassId=(String)ac.getSessionAttribute("mall_commodity_space.mall_class");
		}else{
			ac.setSessionAttribute("mall_commodity_space.mall_class",mallClassId);
		}
		
		
		//如果为酒店或者租车 库存为必填
		if("2".equals(mallClassId)||"6".equals(mallClassId)){
			
			if(!Checker.isDecimal(store)){
				ajax.addScript("alert('请设置正确的库存 !')");
				ajax.send();
				return super.execute(ac);
			}
			
			long stores=Long.parseLong(store);
			if(stores<0){
				ajax.addScript("alert('请设置正确的库存 !')");
				ajax.send();
				return super.execute(ac);
			}
		}else{
			store="9999999";
		}
		
		
		if(!Checker.isEmpty(pirce)&&Double.parseDouble(pirce)<0.01){
			ajax.addScript("alert('请设置合适的价格 !')");
			ajax.send();
			return super.execute(ac);
		}
		
		
		if(!Checker.isEmpty(children_price)&&Double.parseDouble(children_price)<0){
			ajax.addScript("alert('请设置合适的儿童价格!')");
			ajax.send();
			return super.execute(ac);
		}
		
		
		//批量生成日期
//		List<String[]> batchDates=new ArrayList<String[]>();
		if(Checker.isEmpty(pirce)){
			ajax.addScript("alert('请填写价格!')");
			ajax.send();
			return super.execute(ac);
		}
		
		
		
		
		if(days==null){
			ajax.addScript("alert('开始日期和结束日期设置不正确!')");
			ajax.send();
			return  super.execute(ac);
		}else{
			DB db=DBFactory.newDB();
			
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
			for(int i=0;i<days.size();i++){
				Date date=days.get(i);
				Calendar calendar = Calendar.getInstance();      
				 calendar.setTime(date);      
				 int w = calendar.get(Calendar.DAY_OF_WEEK);  
				
				if("2".equals(intpuDataeTypee)){
					//选择星期
					     
					 System.out.println(w); 
					 if(weeks.indexOf(w+"")>-1){
//						 batchDates.add(new String[]{
//								 UUID.randomUUID().toString(),comdId,pirce,
//								 df.format(date),store,children_price
//								 });
						 insertOrUpdateData(db,comdId,pirce,
								 df.format(date),store,children_price,calendar);
					 }
				}else{
//					 batchDates.add(new String[]{
//							 UUID.randomUUID().toString(),comdId,pirce,
//							 df.format(date),store,children_price
//							 });
					 insertOrUpdateData(db,comdId,pirce,
							 df.format(date),store,children_price,calendar);
				}
			}
			
			
			
			db.close();
			
			ajax.addScript("location.reload();");
			ajax.send();
			
			
			return  super.execute(ac);
		}
		
		
	}

	
	/**
	 * 更新数据，如果没有数据，则新增，如果有，则值更新值不为空的字段
	 * @param db
	 * @param comdId
	 * @param pirce
	 * @param format
	 * @param store
	 * @param children_price
	 * @throws JDBCException 
	 */
	private void insertOrUpdateData(DB db, String comdId, String pirce,
			String formatDate, String store, String children_price,Calendar calendar) throws JDBCException{
		//1,如果日期不为空，则更加商品ID和日期查询是否有这条数据，如果有，则更新，如果没有，则新增
		String checkSQL="SELECT id,commodityid,name,dates FROM mall_CommoditySpecifications "
				+ " WHERE dates='"+formatDate+"' AND commodityid='"+comdId+"' ";
		
		String week=weeks[calendar.get(Calendar.DAY_OF_WEEK)-1];
		
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			//数据已经存在
			String id=map.getRow(0).get("id");
			
			Table table=new Table("am_bdp","mall_commodityspecifications");
			
			TableRow updateRow=table.addUpdateRow();
			
			if(!Checker.isEmpty(pirce)){
				updateRow.setValue("price", pirce);
			}
			if(!Checker.isEmpty(store)){
				updateRow.setValue("stock", store);
			}
			if(!Checker.isEmpty(children_price)){
				updateRow.setValue("children_price", children_price);
			}
			if(!Checker.isEmpty(week)){
				updateRow.setValue("week", week);
			}
			
			updateRow.setOldValue("id", id);
			db.save(table);
			
		}else{
			//数据不存在
			//批量插入SQL
			String insreSQL="INSERT INTO mall_commodityspecifications( "+
	           " id, commodityid,price,dates, stock, children_price,week) "+
	           " VALUES (?, ?, ?, ?, ?, ?,?)";
			
			db.execute(insreSQL,new String[]{
					UUID.randomUUID().toString(),comdId,pirce,formatDate,store,children_price,week
			},new int[]{
					Type.VARCHAR,Type.VARCHAR,Type.DECIMAL,Type.DATE,Type.INTEGER,Type.DECIMAL,Type.VARCHAR
			});
		}
		
	}
	
	
	
}
