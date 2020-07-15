package com.p2p.elect;

import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.util.Checker;
import com.p2p.Utils;


/**
 * 积分商场接口
 * @author Administrator
 *
 */
public class ElecticketServer {

	private static ElecticketServer eleServer;
	
	private ElecticketServer(){}
	
	
	public static ElecticketServer getInstance(){
		if(eleServer==null){
			eleServer=new ElecticketServer();
		}
		return eleServer;
	}
	
	
	
	//积分兑换
	//接口功能：
		//1、判断用户积分是否足够，不够则返回错误提示信息
		//2、积分够，则向用户电子券表插入该电子券数据，计算到期时间，同时扣除用户相应积分
		//3、返回值：{ErrCode : 0,ErrMsg:""}

	public String exchangeTicket(String memberCode,String electTicketId){
		String result="";
		//Utils.executeResult(4004, "用户已存在");
		//1、判断用户积分是否足够，不够则返回错误提示信息
		//2、积分够，则向用户电子券表插入该电子券数据，计算到期时间，同时扣除用户相应积分
		//3、返回值：{ErrCode : 0,ErrMsg:""}
		ElectTicketManager etManager=ElectTicketManager.getInstance();
		EterpElectTicket eet=etManager.getEterElectTicketById(electTicketId);
		
		if(checkScore(memberCode,electTicketId)){
			//积分够，向用户电子券表插入该电子券数据，
			//计算到期时间，
			//扣除用户相应积分
			//如果是兑换现金劵，还需将兑换现金劵直接兑换成现金
			
			result=processEleTicket(memberCode,eet);
			
		}else{
			result=Utils.executeResult(41001, "您的积分不够，无法兑换！");
		}
		
		return result;
	}


	/**
	 * 为用户添加徽章，并扣除消费积分，如果是兑换现金劵，怎还需要消费。
	 * @param memberCode 会员编号
	 * @param electTicketId 企业电子券ID
	 * @param usestatus  使用状态  1，有效；2，已经使用。
	 */
	private String processEleTicket(String memberCode, EterpElectTicket eet) {
		String result="";
		try{
			//有效日期
			String effectiveDate="0 day";
			String expired="";
			
			DB db=DBFactory.getDB();
			
			//计算到期时间，
			String sql="SELECT EffectiveDate||' day' AS EffectiveDate FROM p2p_eterpelectticket "
					+ " WHERE id='"+eet.getId()+"'";


			MapList map=db.query(sql);
			if(!Checker.isEmpty(map)){
				effectiveDate=map.getRow(0).get("effectivedate");
			}
			
			//计算过期日期
			sql="SELECT now()+ interval '"+effectiveDate+"' AS expired";
			map=db.query(sql);
			expired=map.getRow(0).get("expired");
			
			
			//则向用户电子券表插入该电子券数据
			String id=UUID.randomUUID().toString();
			sql="INSERT INTO p2p_orgelectticker("
					+ " id, eterpelectticketid,member_code, usestatus, getdatetime, expired) VALUES ("
					+ "'"+id+"','"+eet.getId()+"',"+memberCode+",1, now(),"
					+ "to_date('"+expired+"','yyyy-mm-dd hh24:mi:ss') );";
			
			db.execute(sql);
			
			//同时扣除用户相应积分
			sql="UPDATE ws_member SET  integration=integration-("
					+ " SELECT scorevalue FROM p2p_eterpelectticket  "
					+ " WHERE id='"+eet.getId()+"'  ) "
					+ " WHERE  member_code="+memberCode;


			db.execute(sql);
			
			result=Utils.executeResult(0, "兑换成功");
			
			
			//检查是否为兑换现金劵，如果时，则消费此电子券
			if("3".equals(eet.getElecttickettype())){
				
				//获取电子券管理实例
				ElectTicketManager etManager=ElectTicketManager.getInstance();
				//初始化会员电子券
				etManager.initMemberElectTicket(memberCode);
				
				MemberElectTicket memEt=etManager.getMemmberElectById(id);
				
				if(etManager.userElectTicke(memEt, eet)){
					result=Utils.executeResult(0, "兑换现金劵成功");
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			result=Utils.executeResult(41002, "积分兑换出错，请稍后再试");
		}
		return result;
	}


	/**
	 * 判断用户积分是否足够
	 * @param memberCode 会员ID
	 * @param electTicketId 电子券ID
	 * @return 够返回true否则返回false
	 */
	private boolean checkScore(String memberCode, String electTicketId) {
		boolean result=false;
		try{
			String checkSQL="SELECT (SELECT ("
					+ "SELECT scorevalue FROM p2p_eterpelectticket  "
					+ " WHERE id='"+electTicketId+"')<=("
					+ " SELECT INTEGRATION FROM ws_member WHERE member_code="+memberCode
					+ ") AS isenough)::varchar AS isenough ";
			
			DB db=DBFactory.getDB();
			MapList map=db.query(checkSQL);

			if(!Checker.isEmpty(map)){
				if("true".equalsIgnoreCase(map.getRow(0).get("isenough"))){
					result=true;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	
}
