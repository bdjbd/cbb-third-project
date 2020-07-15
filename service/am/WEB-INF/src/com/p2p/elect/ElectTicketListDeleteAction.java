package com.p2p.elect;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class ElectTicketListDeleteAction extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		// 获得选择列: _s_单元编号
		String[] electTickets = ac.getRequestParameters("_s_p2p_eterpelectticket.list");
		// 获得主键: 单元编号.元素编号.k（设置了映射表时会自动用隐藏域记录主键值）
		String[] ids = ac.getRequestParameters("p2p_eterpelectticket.list.id.k");
		
		//获取选择行
		String eleId="";
		if(!Checker.isEmpty(electTickets)){
			for(int i=0;i<electTickets.length;i++){
				if("1".equals(electTickets[i])){
					eleId=ids[i];
				}
			}
		}
		
		Ajax ajax=new Ajax(ac);
		
		//检查是否可以删除
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM p2p_eterpelectticket  WHERE id='"+eleId+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String datastatus=map.getRow(0).get("datastatus");
			if(!"1".equals(datastatus)){
				//非起草状态的无法删除
				ajax.addScript("alert('数字在使用中，不可以删除!');");
				ac.getActionResult().setSuccessful(false);
				ajax.send();
			}else{
				//删除
				String deleteSQL="DELETE FROM   p2p_eterpelectticket WHERE id='"+eleId+"' ";
				db.execute(deleteSQL);
				ac.getActionResult().setSuccessful(true);
			}
		}
		
		//设置action执行后返回地址
		ac.getActionResult().setSuccessful(true);
		ac.getActionResult().setUrl("/wwd/p2p_eterpelectticket.do?m=s");
		
		return ac;
	}
}
