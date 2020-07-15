package com.am.frame.farmbarcode.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月20日 下午4:35:58
 * @version 保存农厂条形码信息
 */
public class SaveFarmBarCode extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//获取生产者账号
		String loginaccount = ac.getRequestParameter("mall_bar_code.form.member_phone");
		//获取页面状态
		String m=ac.getRequestParameter("mall_bar_code.form.mm");
		
		
		//如果是新增状态，执行保存
		if(m.equals("a")){
			MapList maplist = montageBarCode(loginaccount);
			//条形码字符串
			String barcode = null;
			if(!Checker.isEmpty(maplist)){
				
				Table table=ac.getTable("MALL_BAR_CODE");
				db.save(table);
				
				String barCodeID = table.getRows().get(0).getValue("id");
				barcode = table.getRows().get(0).getValue("bar_code");
				//打印份数
				double print_number = Double.parseDouble(table.getRows().get(0).getValue("print_number"));
				
				//生成条形码图片，并返回保存路径
				CreateBarCode createBarCode = new  CreateBarCode();
				String barCodePath = createBarCode.createBarCode(barcode);
				
				StringBuilder updateBarCodeSql =  new StringBuilder();
				updateBarCodeSql.append(" UPDATE mall_bar_code SET bar_code_path = '"+barCodePath+"' WHERE id = '"+barCodeID+"' ");
				db.execute(updateBarCodeSql.toString());
				
				ac.setSessionAttribute("am_bdp.mall_bar_code.form.id", barCodeID);
				
				//获取后台自定义参数
				String state = ac.getActionParameter();
				
				//点击确定打印，则跳入打印页面
				if(state.equals("print")){
					ac.getActionResult().setScript("window.open('/barcode/bar_code.html?"
							+ "imgsPath="+barCodePath+"&printNumber="+print_number+"',"
							+ "'','height=650,width=1000,top=70,left=100,resizable=no,toolbar=no,"
							+ "menubar=yes,scrollbars=yes,location=no,status=no')");
				}
			}else{
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("请输入正确的生产者账号");
			}
		}else{
			String barCodePath = ac.getRequestParameter("mall_bar_code.form.bar_code_path");
			String print_number = ac.getRequestParameter("mall_bar_code.form.print_number");
			ac.getActionResult().setScript("window.open('/barcode/bar_code.html?"
					+ "imgsPath="+barCodePath+"&printNumber="+print_number+"',"
					+ "'','height=650,width=1000,top=70,left=100,resizable=no,toolbar=no,"
					+ "menubar=yes,scrollbars=yes,location=no,status=no')");
		}
		
		
		
	}
	
	//查询生产者账户是否存在
	public MapList montageBarCode(String loginaccount){
		//UPPER(SUBSTR(id,32,37) || to_char(now(),'YYYYMMddHHmmSS')) as sdes
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * FROM am_member WHERE loginaccount = '"+loginaccount+"'  ");
		MapList maplist = null;
		DB db = null;
		try{
			db = DBFactory.newDB();
			maplist = db.query(sql.toString());
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		return maplist;
	}
	
	
}
