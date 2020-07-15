package com.am.cro.designatedDriver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;


/**
 * 列表代驾费结算
 * @author 王成阳
 * 2017-9-7
 */
public class Statements extends DefaultAction{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public void doAction(DB db , ActionContext ac){
		logger.debug("-----------------------------------输出代驾费结算方法-----------------------------------");
		//获得所有列:_s_单元编号
		String[] select = ac.getRequestParameters("_s_cro_carrepairorders.list");
		 //获得所有列主键:单元编号.元素编号.k
		String[] ids = ac.getRequestParameters("cro_carrepairorders.list.id.k");
		 //定义列表selectId
	     List<String> selectId = new ArrayList<String>();
	     //获得所有选择单元
	     //判断选择单元不为空，把选择的所有单元循环出来，依次 添加到selectId列表里
	     if(!Checker.isEmpty(select)){
	    	 for (int i = 0; i < select.length; i++) {
	             //1为选中
	             if ("1".equals(select[i])) {
	            	 selectId.add(ids[i]);
	             }
	         }
     	}
	     //设置提示消息
	     String msg = "";
	     //如果单元列表为空提示没有选择数据
	     if (Checker.isEmpty(selectId)) {
	        msg = "没有选择数据";
	     }else {
	        //定义一个boolean类型的result
	        boolean result = true ;
	        //循环所有选中的单元
	        for (int i = 0; i < selectId.size(); i++) {
	          //获得修改结果并执行修改方法
	          result = result&&Update(db, selectId.get(i),ac);
	        }
	        if (result) {
//	          msg = "设置成功!";
	        }else {
	          msg = "设置出错!情检查选择的数据！";
	        }
	     }
	         ac.getActionResult().addSuccessMessage(msg.toString());
	}


  /*
   * 结算方法
   */
  public boolean Update(DB db,String id,ActionContext ac){
	  logger.debug("-----------------------------------进入结算状态修改方法-----------------------------------");
     //定义一个result,用来判断商品状态修改情况。默认为false也就是未修改
     boolean result = false ;
     try {
       //通过id查询单个商品信息
       String querySql = "select * from CRO_CARREPAIRORDER where id = '"+id+"'";
       MapList queryMap = db.query(querySql);
       //如果查询结果不为空
       if(!Checker.isEmpty(queryMap)){
          //获得代驾费结算审核状态
          String driving_settlement_audit_state = queryMap.addRow(0).get("driving_settlement_audit_state");
          logger.debug("输出代驾费结算审核状态" + driving_settlement_audit_state);
          //获得用户id
          String memberid = queryMap.addRow(0).get("memberid");
          logger.debug("输出用户id" + memberid);
          //获得代驾费用金额
          String driving_expenses = queryMap.addRow(0).get("driving_expenses");
          logger.debug("输出代驾费用金额" + driving_expenses);
          //获得结算操作人
          String driving_settlement_man = ac.getVisitor().getUser().getName();
          logger.debug("输出结算操作人" + driving_settlement_man);
          //获得结算时间
          Date date = new Date();
  		  DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		  String driving_settlement_time = format.format(date);
  		  logger.debug("输出结算时间" + driving_settlement_time);
          //调用转账方法
          VirementManager vm = new VirementManager();
          /*
           * @param db
           * @param "" 转出用户/机构id
           * @param memberid 转入用户id
           * @param "" 转出账号类型 常量 sa_code  常量 见{@link com.am.frame.systemAccount.SystemAccountClass} 
           * @param "CASH_ACCOUNT" 转入账号类型   常量 sa_code 常量 见{@link com.am.frame.systemAccount.SystemAccountClass} 
           * @param driving_expenses 转账金额 元
           * @param "" 转入描述
           * @param "" 转出描述
           * @param ""  业务参数
           * @param flags  true 是 false 否 是否收取手续费
           */
	      	JSONObject jso = vm.execute(db,"",memberid, "", "CASH_ACCOUNT", driving_expenses,"","","",false);
	      	//如果转账失败则提示
	      	if("1000".equals(jso.get("code"))){
	    		ac.getActionResult().setSuccessful(false);
//	    		ac.getActionResult().addErrorMessage("转账失败");
	    		logger.debug("转账失败！");
	    	}else{
	    		//转账成功则将结算状态改为1(已结算)
	  	        //只有当审核状态为已审核，才可以进行结算状态更改	
	            if ("2".equals(driving_settlement_audit_state)){
	            	//结算状态driving_settlement_state	结算人:driving_settlement_man	结算时间:driving_settlement_time
	   	    	  	String updateSql = "update CRO_CARREPAIRORDER set driving_settlement_state = '1' , driving_settlement_man = '"+driving_settlement_man+"' , driving_settlement_time = '"+driving_settlement_time+"' where id = '"+id+"'";
	   	    	  	logger.debug("-------------------------------输出修改结算状态SQL---------------------------------" + updateSql);
	   	    	  	db.execute(updateSql);
	   	           }
	    		ac.getActionResult().setSuccessful(true);
//	    		ac.getActionResult().addSuccessMessage("转账成功");
	    		logger.debug("转账成功!");
	    	}
          //修改成功把修改状态该为true(已修改)
          result = true ;
       }
     } catch (Exception e) {
     }    //返回修改后的结果
          return result;
  }
}
