package com.am.frame.elect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.Utils;
import com.p2p.service.DataService;


/***
 * 电子券管理类
 * @author Administrator
 *
 */
public class ElectTicketManager {
	
	private static ElectTicketManager etm;
	
	private Map<String,MemberElectTicket> etMap=new HashMap<String,MemberElectTicket>();
	
	private ElectTicketManager(){}
	
	public static ElectTicketManager getInstance(){
		if(etm==null){
			etm=new ElectTicketManager();
		}
		return etm;
	}
	
	/**
	 * 初始化用户电子券
	 * @param memberCode
	 */
	public void initMemberElectTicket(String memberCode){
		if(!Checker.isEmpty(memberCode)){
			DB db=null;
			try{
				String sql="SELECT * FROM am_OrgElectTicker  WHERE expired>now() AND am_MemberId='"+memberCode+"'";
				
				db=DBFactory.getDB();
				MapList map=db.query(sql);
				
				if(!Checker.isEmpty(map)){
					for(int i=0;i<map.size();i++){
						MemberElectTicket et=new MemberElectTicket(map.getRow(i));
						etMap.put(et.getId(), et);
					}
				}
				
			}catch(JDBCException e){
				e.printStackTrace();
			}finally{
				if(db!=null){
					try {
						db.close();
					} catch (JDBCException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/***
	 * 电子券ID获取用户的电子券 需要先初始化用户电子券
	 * @param id
	 * @return
	 */
	public MemberElectTicket getMemmberElectById(String id){
		MemberElectTicket met=etMap.get(id);
		if(met==null){
			DB db=null;
			try{
				String sql="SELECT * FROM am_OrgElectTicker  WHERE expired>now() AND id='"+id+"'";
				db=DBFactory.newDB();
				
				MapList map=db.query(sql);
				if(!Checker.isEmpty(map)){
					met=new MemberElectTicket(map.getRow(0));
				}
			}catch(Exception e){
				e.printStackTrace();
				if(db!=null){
					try {
						db.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			
		}
		
		return etMap.get(id);
	}
	
	
	/**
	 * 为会员添加电子券
	 * @param memberCode  会员编号
	 * @param etId  电子券ID
	 * @return 成功返回ture
	 */
	public boolean addMemberElectTicket(String memberCode,String etId){
		boolean result=false;
		
		try{
			DB db=DBFactory.getDB();
			
			String sql=
					"SELECT effectivedate FROM am_EterpElectTicket           "+
					" WHERE id='"+etId+"' ";
			MapList map=db.query(sql);
			
			if(!Checker.isEmpty(map)){
				String effectveDate=map.getRow(0).get("effectivedate");
				
				String addSQL=
						" INSERT INTO am_orgelectticker(id, am_memberid,           "+
						" usestatus, getdatetime, expired,EterpElectTicketID) VALUES (                 "+
						" uuid_generate_v4(),'"+memberCode+"' ,1, now(), now()+interval '"+effectveDate+" day'"+
						" ,'"+etId+"') ";
				
				int res=db.execute(addSQL);
				
				if(res>0){
					result=true;
				}
			}
		}catch(JDBCException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 根据ID获取企业电子券
	 * @param id
	 * @return
	 */
	public EterpElectTicket getEterElectTicketById(String id){
		EterpElectTicket result=null;
		try{
			
			String sql="SELECT * FROM am_eterpelectticket  WHERE id='"+id+"' ";
			DB db=DBFactory.getDB();
			MapList map=db.query(sql);
			
			if(!Checker.isEmpty(map)){
				Row row=map.getRow(0);
				result=new EterpElectTicket(row);
			}
			
		}catch(JDBCException e){
			e.printStackTrace();
		}
		
		
		return result;
	}

	/**
	 * 使用电子券
	 * @param memEt 会员电子券MemberElectTicket
	 * @param eet 企业电子券EterpElectTicket
	 */
	public boolean userElectTicke(MemberElectTicket memEt,EterpElectTicket eet) throws JDBCException{
		boolean result=false;
		
		if("1".equals(memEt.getUsestatus())){//未使用
			DB db =DBFactory.getDB();
			
			if("3".equals(eet.getElecttickettype())){//兑换现金劵
				
				//更新用户金额
				String updateSQL="UPDATE am_member SET cash=COALESCE(cash,0)+"+eet.getCash()+
						" WHERE id='"+memEt.getMemberCode()+"'";
				db.execute(updateSQL);
				
				//更新电子券状态
				updateSQL="UPDATE am_orgelectticker SET usestatus=2,"
						+ "usedatetime=now() WHERE id='"+memEt.getId()+"' ";
				if(db.execute(updateSQL)>0)result=true;
			}
			
			if("2".equals(eet.getElecttickettype())){//抵现券
				
				//更新电子券状态
				String updateSQL="UPDATE am_orgelectticker SET usestatus=2,"
						+ "usedatetime=now() WHERE id='"+memEt.getId()+"' ";
				if(db.execute(updateSQL)>0)result=true;
			}
		}
		
		return result;
	}

	
	/**
	 * 消费电子券
	 * @param memEt  会员电子券
	 * @return true，完成消费，false，无法消费，
	 */
	public boolean userElectTicke(MemberElectTicket memEt){
		boolean result=false;
		try{
			//更具电子券类型获取企业电子券
			EterpElectTicket eet=getEterElectTicketById(memEt.getEterpElectTicketId());
			
			result=userElectTicke(memEt, eet);
		}catch(JDBCException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * 获取会员指定类型的电子卷JSON字符串
	 * @param memberCode
	 * @param electTickType
	 * @return
	 */
	public String getMemberElectTicketToJson(String memberCode ,String electTickType){
		String result="{\"DATA\":[]}";
		//获取会员抵现券
		String sql="SELECT oet.id,oet.am_memberid,oet.expired,eet.cash,eet.sname,eet.iconpath "
				+ " FROM am_orgelectticker AS oet "
				+ " LEFT JOIN am_eterpelectticket AS eet "
				+ " ON oet.eterpelectticketid=eet.id "
				+ " WHERE oet.am_memberid='"+memberCode+"' "
				+ " AND oet.usestatus!=2  AND oet.expired >now() "
				+ " AND eet.electtickettype='"+electTickType+"'";
					
		result=DataService.execute(sql);
		
		return result;
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
		 * 判断用户积分是否足够
		 * @param memberCode 会员ID
		 * @param electTicketId 电子券ID
		 * @return 够返回true否则返回false
		 */
		private boolean checkScore(String memberCode, String electTicketId) {
			boolean result=false;
			try{
				String checkSQL="SELECT (SELECT ("
						+ "SELECT scorevalue FROM am_eterpelectticket  "
						+ " WHERE id='"+electTicketId+"')<=("
						+ " SELECT score FROM am_member WHERE id='"+memberCode+"' "
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
				String sql="SELECT EffectiveDate||' day' AS EffectiveDate FROM am_eterpelectticket "
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
				sql="INSERT INTO am_orgelectticker("
						+ " id, eterpelectticketid,am_MemberId, usestatus, getdatetime, expired) VALUES ("
						+ "'"+id+"','"+eet.getId()+"','"+memberCode+"',1, now(),"
						+ "to_date('"+expired+"','yyyy-mm-dd hh24:mi:ss') );";
				
				db.execute(sql);
				
				//同时扣除用户相应积分
				sql="UPDATE am_member SET  score=score-("
						+ " SELECT scorevalue FROM am_eterpelectticket  "
						+ " WHERE id='"+eet.getId()+"'  ) "
						+ " WHERE  id='"+memberCode+"'";


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
}
