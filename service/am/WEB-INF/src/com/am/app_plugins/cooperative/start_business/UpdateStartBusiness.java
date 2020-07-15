package com.am.app_plugins.cooperative.start_business;


import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.instance.GetVolunteerAccountWithQualificationItask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.util.Checker;


/**
 * 大众创业支付回调
 * @author wz
 *
 */
public class UpdateStartBusiness extends AbstraceBusinessCallBack{

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	JSONObject result = new JSONObject();
	
	@Override
	public String callBackExec(String id,String business, DB db,String type) throws Exception{
		logger.info("大众创业支付回调"+business);
		
		if(!checkProcessBuissnes(id, db)&&checkTradeState(id, business, db, type)){
			
			JSONObject businessJso = new JSONObject(business);
			
			//区域类型全国/省/市/区县；01：全国；02：省；03：市；04：区县
			String areaType="04";
			
			String province ="";
			if(businessJso.has("province")){
				province =businessJso.getString("province");
				areaType="02";
			}
			
			String city ="";
			if(businessJso.has("city")){
				city =businessJso.getString("city");
				areaType="03";
			}
			
			String zone ="";
			if(businessJso.has("zone")){
				zone =businessJso.getString("zone");
				areaType="04";
			}
			
			String businessid = businessJso.getString("businessid");
			String memberid = businessJso.getString("memberid");
			String pay_money = businessJso.getString("pay_money");
			String files  = businessJso.getString("files");
			String orgid =  businessJso.getString("orgid");
			String uuid = businessJso.getString("uuid");
			String DataName = businessJso.getString("DataName");
			
			
			//状态  1为支付创建  2为借款创业
			String business_type = businessJso.getString("business_type");
			
			
			if("2".equals(type)){
				String  sql = "SELECT msc.*,msct.t_name,msct.t_code,msct.t_table_name,msct.create_time,msct.admin_role_code,msct.org_type FROM mall_service_commodity as msc "
			    		+ " LEFT JOIN mall_service_comd_type as msct"
			    		+ " ON msc.sc_type = msct.id"
			    		+ " WHERE status = '1' and msc.id = '"+businessid+"'" ;
				
				MapList list = db.query(sql);
				if(!Checker.isEmpty(list)){
					String status ="";
					
					if("1".equals(business_type)){
						status = "0";
						
						String selesql = "select msac.sa_code from mall_trade_detail as mtd "
								+ "left join mall_account_info  as mai on mai.id = mtd.account_id "
								+ "left join mall_system_account_class as msac on msac.id = mai.a_class_id where mtd.id= '"+id+"'";
						MapList lists = db.query(selesql);
						//查询判断是否是志愿者账户支付，如果是志愿者账户支付则更新志愿者服务账户提现额度
//						if("VOLUNTEER_ACCOUNT".equals(lists.getRow(0).get("sa_code")))
//						{
							TaskEngine taskEngine = TaskEngine.getInstance();
							RunTaskParams params = new RunTaskParams();
							// 获取志愿者账号提现资格任务 START
							params = new RunTaskParams();
							params.setTaskCode(GetVolunteerAccountWithQualificationItask.TASK_ECODE);
							params.pushParam(GetVolunteerAccountWithQualificationItask.START_BUSINESS,pay_money);
							params.setMemberId(memberid);
							taskEngine.executTask(params);
//						}
						
					}else {
						status = "4";
					}
					//插入联合社等数据
//					String isql = "INSERT INTO "+list.getRow(0).get("t_table_name")+" "
//							+ "(id,province_id,city_id,zone_id,purchaser,buy_price,buy_time,f_status,enclosures,orgid) "
//							+ "VALUES('"+ids+"','"+province+"','"+city+"','"+zone+"','"+memberid+"','"+pay_money+"','now()','"+status+"','"+files+"','"+orgid+"')";
					
					Table table = new Table("am_bdp",list.getRow(0).get("t_table_name"));
					TableRow  tableRow= table.addInsertRow();
					tableRow.setValue("id", uuid);
					tableRow.setValue("province_id", province);
					tableRow.setValue("city_id", city);
					tableRow.setValue("zone_id", zone);
					tableRow.setValue("purchaser", memberid);
					tableRow.setValue("buy_price", pay_money);
					tableRow.setValue("f_status", status);
					tableRow.setValue("enclosures", files);
					tableRow.setValue("orgid", orgid);
					tableRow.setValue("gap_name", DataName);
					tableRow.setValue("area_type", areaType);
					
					db.save(table);
//					db.execute(isql);
					
					
					uuid=tableRow.getValue("id");
					
					if("2".equals(business_type)){
						/**
						 * 借款申请
						 */
						//身份证
						String id_card_number = businessJso.getString("idnumber");
						//年龄
						String age = businessJso.getString("age");
						//还款约定
						String promise = businessJso.getString("loan_business_term");
						//文化程度
						String cultures_level = businessJso.getString("degree_of_education");
						//授信保证金额
						String credit_margin = businessJso.getString("credit_money");
						//地址
						String address = businessJso.getString("address");
						
						//插入借款记录表
//						String iisql = "INSERT INTO mall_borrowing_records "
//								+ "(id,member_id,sc_id,orgid,credit_margin"
//								+ ",cultures_level,address,id_card_number"
//								+ ",age,promise,status,create_time) "
//								+ "VALUES('"+UUID.randomUUID()+"'"
//										+ ",'"+memberid+"'"
//										+ ",'"+businessid+"'"
//										+ ",'"+ids+"'"
//										+ ",'"+credit_margin+"'"
//										+ ",'"+cultures_level+"'"
//										+ ",'"+address+"'"
//										+ ",'"+id_card_number+"'"
//										+ ",'"+age+"','"+promise+"','1','now()')";
						

						Table lodTable = new Table("am_bdp","mall_borrowing_records");
						TableRow  lodTableRow= lodTable.addInsertRow();
						lodTableRow.setValue("member_id", memberid);
						lodTableRow.setValue("sc_id", businessid);
						lodTableRow.setValue("orgid", uuid);
						lodTableRow.setValue("credit_margin", Long.parseLong(credit_margin));
						lodTableRow.setValue("cultures_level", cultures_level);
						lodTableRow.setValue("address", address);
						lodTableRow.setValue("id_card_number", id_card_number);
						lodTableRow.setValue("age", age);
						lodTableRow.setValue("promise", promise);
						lodTableRow.setValue("status", "1");
						db.save(lodTable);
//						db.execute(iisql);
					}
					
					result.put("code", "0");
					result.put("msg", "执行插入成功");
					
				}
				
			}
		}
		
		
		return result.toString();
	}

}
