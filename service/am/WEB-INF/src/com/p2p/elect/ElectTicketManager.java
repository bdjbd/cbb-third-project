package com.p2p.elect;

import java.util.HashMap;
import java.util.Map;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
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
			try{
				String sql="SELECT * FROM p2p_OrgElectTicker  WHERE expired>now() AND member_code="+memberCode;
				
				DB db=DBFactory.getDB();
				MapList map=db.query(sql);
				
				if(!Checker.isEmpty(map)){
					for(int i=0;i<map.size();i++){
						MemberElectTicket et=new MemberElectTicket(map.getRow(i));
						etMap.put(et.getId(), et);
					}
				}
				
			}catch(JDBCException e){
				e.printStackTrace();
			}
		}
	}
	
	/***
	 * 电子券ID获取用户的电子券 需要先初始化用户电子券
	 * @param id
	 * @return
	 */
	public MemberElectTicket getMemmberElectById(String id){
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
					"SELECT effectivedate FROM p2p_EterpElectTicket           "+
					" WHERE id='"+etId+"' ";
			MapList map=db.query(sql);
			
			if(!Checker.isEmpty(map)){
				String effectveDate=map.getRow(0).get("effectivedate");
				
				String addSQL=
						" INSERT INTO p2p_orgelectticker(id, member_code,           "+
						" usestatus, getdatetime, expired,EterpElectTicketID) VALUES (                 "+
						" uuid_generate_v4(),"+memberCode+" ,1, now(), now()+interval '"+effectveDate+" day'"+
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
			
			String sql="SELECT * FROM p2p_eterpelectticket  WHERE id='"+id+"' ";
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
				String updateSQL="UPDATE ws_member SET cash=COALESCE(cash,0)+"+eet.getCash()+
						" WHERE member_code="+memEt.getMemberCode();
				db.execute(updateSQL);
				
				//更新电子券状态
				updateSQL="UPDATE p2p_orgelectticker SET usestatus=2,"
						+ "usedatetime=now() WHERE id='"+memEt.getId()+"' ";
				if(db.execute(updateSQL)>0)result=true;
			}
			
			if("2".equals(eet.getElecttickettype())){//抵现券
				
				//更新电子券状态
				String updateSQL="UPDATE p2p_orgelectticker SET usestatus=2,"
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
		String sql="SELECT oet.id,oet.member_code,oet.expired,eet.cash,eet.sname,eet.iconpath "
				+ " FROM p2p_orgelectticker AS oet "
				+ " LEFT JOIN p2p_eterpelectticket AS eet "
				+ " ON oet.eterpelectticketid=eet.id "
				+ " WHERE oet.member_code="+memberCode
				+ " AND oet.usestatus!=2  AND oet.expired >now() "
				+ " AND eet.electtickettype='"+electTickType+"'";
					
		result=DataService.execute(sql);
		return result;
	}
}
