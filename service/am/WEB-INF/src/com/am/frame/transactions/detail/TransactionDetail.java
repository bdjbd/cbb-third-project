package com.am.frame.transactions.detail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.util.Checker;

/** 
 * @author  wz  
 * @descriptions 交易明细操作
 * @date 创建时间：2016年4月14日 上午11:45:16 
 * @version 1.0   
 */
public class TransactionDetail {
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	/**
	 * 支出插入交易明细操作
	 * @param db
	 * @param afterDetailBean
	 * @return
	 */
	public AfterDetailBean afterActions(DB db,AfterDetailBean afterDetailBean) {
		
		
		Table tranTable = afterDetailBean.getTranTable();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD HH:SS:mm");
		
		String date = dateFormat.format(new Date());
		
		try {
			
			afterDetailBean.setTrade_type("1");
			
			if(Checker.isEmpty(afterDetailBean.getRmarks())){
				afterDetailBean.setRmarks("支出");
			}
			
			if(Checker.isEmpty(afterDetailBean.getCreate_time())){
				afterDetailBean.setCreate_time(date);
			}
			
			if(Checker.isEmpty(afterDetailBean.getTrade_time())){
				afterDetailBean.setTrade_time(date);
			}
			
			
			logger.info("支出操作,");
			
			db.save(tranTable);
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		
		}
		
		return afterDetailBean;
	}
	
	/**
	 * 更新交易记录方法
	 */
	public AfterDetailBean upDateActions(DB db,AfterDetailBean afterDetailBean,String id) throws Exception{
		
		Table tranTable=afterDetailBean.getTranTable();
		
		if(!Checker.isEmpty(id)){
			
			TableRow row = afterDetailBean.getTableRow();
			
			row.setOldValue("id", id);
			
			if(!Checker.isEmpty(afterDetailBean.getIs_process_buissnes())){
				row.setValue("is_process_buissnes", afterDetailBean.getIs_process_buissnes());
			}
			if(!Checker.isEmpty(afterDetailBean.getAccount_id())){
				row.setValue("account_id", afterDetailBean.getAccount_id());
							
			}
			if(!Checker.isEmpty(afterDetailBean.getBusiness_id())){
				row.setValue("business_id", afterDetailBean.getBusiness_id());
				
			}
			if(!Checker.isEmpty(afterDetailBean.getBusiness_json())){
				row.setValue("business_json", afterDetailBean.getBusiness_json());
				
			}
			if(!Checker.isEmpty(afterDetailBean.getMember_id())){
				row.setValue("member_id", afterDetailBean.getMember_id());
				
			}
			if(!Checker.isEmpty(afterDetailBean.getRmarks())){
				row.setValue("rmarks", afterDetailBean.getRmarks());
				
			}
			if(!Checker.isEmpty(afterDetailBean.getSa_class_id())){
				row.setValue("sa_class_id", afterDetailBean.getSa_class_id());
				
			}
			if(!Checker.isEmpty(afterDetailBean.getTrade_state())){
				row.setValue("trade_state", afterDetailBean.getTrade_state());
				
			}
			
//			if(!Checker.isEmpty(afterDetailBean.getTrade_time().toString())){
//				row.setValue("trade_time", afterDetailBean.getTrade_time());
//				
//			}
			if(!Checker.isEmpty(afterDetailBean.getCounter_fee()+"")){
				row.setValue("counter_fee", afterDetailBean.getCounter_fee());
				
			}
			if(!Checker.isEmpty(afterDetailBean.getTrade_total_money()+"")){
				row.setValue("trade_total_money", afterDetailBean.getTrade_total_money());
				
			}
			if(!Checker.isEmpty(afterDetailBean.getTrade_type())){
				row.setValue("trade_type", afterDetailBean.getTrade_type());
				
			}
			if(!Checker.isEmpty(afterDetailBean.getPerson_remaker())){
				row.setValue("person_remaker", afterDetailBean.getTrade_type());
			}
			
			db.save(tranTable);
			
		}
		
		return afterDetailBean;
		
	}
	
	
	
	
	/**
	 * 支出批量插入交易记录
	 * @param db
	 * @param detailBeans
	 * @return
	 */
	public List<AfterDetailBean> afterActions(DB db,List<AfterDetailBean> detailBeans) {
		
		List<AfterDetailBean> resultDetailBeans = new ArrayList<AfterDetailBean>();
		for(int i=0;i<detailBeans.size();i++){   
			  
			AfterDetailBean afterDetailBean = detailBeans.get(i);
			resultDetailBeans.add(afterActions(db,afterDetailBean));
		  
		}   
		
		return resultDetailBeans;
	}
	
	/**
	 * 收入插入交易明细操作
	 * @param db
	 * @param afterDetailBean
	 * @return
	 */
	public AfterDetailBean earningActions(DB db,AfterDetailBean afterDetailBean) {
		
		Table tranTable = afterDetailBean.getTranTable();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD HH:SS:mm");
		
		String date = dateFormat.format(new Date());
		
		try {
			
			afterDetailBean.setTrade_type("2");
			
			if(Checker.isEmpty(afterDetailBean.getRmarks())){
				afterDetailBean.setRmarks("收入");
			}
			
			if(Checker.isEmpty(afterDetailBean.getCreate_time())){
				afterDetailBean.setCreate_time(date);
			}
			
			if(Checker.isEmpty(afterDetailBean.getTrade_time())){
				afterDetailBean.setTrade_time(date);
			}
			
			db.save(tranTable);
			
		
		} catch (Exception e) {
			
			e.printStackTrace();
		
		}
		
		return afterDetailBean;
	}
	
	/**
	 * 收入批量插入交易记录
	 * @param db
	 * @param detailBeans
	 * @return
	 */
	public List<AfterDetailBean> earningActions(DB db,List<AfterDetailBean> detailBeans) {
		
		List<AfterDetailBean> resultDetailBeans = new ArrayList<AfterDetailBean>();
		
		for(int i=0;i<detailBeans.size();i++){   
			  
			AfterDetailBean afterDetailBean = detailBeans.get(i);
			resultDetailBeans.add(afterActions(db,afterDetailBean));
		  
		}   
		
		return resultDetailBeans;
	}
	

}
