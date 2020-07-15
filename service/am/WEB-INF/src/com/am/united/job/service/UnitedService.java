package com.am.united.job.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author 	QinTao
 * @create 2016年11月30
 * @version 
 * 说明:<br />
 * 联合会会费操作
 */

public class UnitedService {
	
	private DB db=null;
	private	Date date=null;
	private	String year =null;
	private String create_time=null;
	// 总农技协
	private double country_ATAF_free_ratio=0;
	// 省农技协
	private double province_ATAF_free_ratio =0;
	// 市农技协
	private double city_ATAF_free_ratio =0;
	// 区县农技协
	private double zone_ATAF_free_ratio =0;
	
	 //消费者需交会费
	private double free_x=0;
	 // 生产者
	private double free_s=0;
	 // 单位
	private double free_d=0;
	 // 农场
	private double free_nc=0;
	 // 涉农企业
	private double free_sn=0;
	 // 合作社
	private double free_hz=0;
	 // 联合社 40
	private double free_lh=0;
	 //会费金额
	private double free=0;
	//总联合会分配比例
	private double free_a=0;
	//省联合会分配比例
	private double free_p=0;
	//市联合会分配比例
	private double free_c=0;
	//区县联合会分配比例
	private double free_z=0;
	
	public void calculateUnitedPressFree() {
		
	
		try {
			//初始化属性
			db=DBFactory.getDB();
			date=new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
			SimpleDateFormat dateFormats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//当前年份
			year = dateFormat.format(date);
			//当前时间
			create_time = dateFormats.format(date);
			// 总农技协
			 country_ATAF_free_ratio = Var.getDouble("country_ATAF_free_ratio", 0);
			// 省农技协
			 province_ATAF_free_ratio = Var.getDouble("province_ATAF_free_ratio", 0);
			// 市农技协
			 city_ATAF_free_ratio = Var.getDouble("city_ATAF_free_ratio", 0);
			// 区县农技协
			 zone_ATAF_free_ratio = Var.getDouble("zone_ATAF_free_ratio", 0);
			// 消费者
			free_x = Var.getDouble("consumer_free", 0) * 100;
			// 生产者
			free_s = Var.getDouble("farmer_united_free", 0) * 100;
			// 单位
			free_d = Var.getDouble("company_free", 0) * 100;
			// 农场
			free_nc= Var.getDouble("farm_united_free", 0) * 100;
			// 涉农企业
			free_sn = Var.getDouble("agricultural_org_free", 0) * 100;
			// 合作社
			free_hz = Var.getDouble("cooperative_united_free", 0) * 100;
			// 联合社 40
			free_lh = Var.getDouble("zone_united_press_free", 0) * 100;
				
			//调用查询统计表是否有数据
			queryUnitedFree(db);
			
			
			
			
			//调用查询所有人和机构缴费记录
			//个人、机构
			PayRecord(db,"am_member");
			//联合社
			PayRecord(db,"UNITED_PRESS");
			//合作社
			PayRecord(db,"MALL_COOPERATIVE");
			//涉农企业
			PayRecord(db,"MALL_AGRICULTURAL_ORG");
			//农场
			PayRecord(db,"HOME_FARM");
			
			//调用扣款方法
			queryUnitedStatus(db);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// 统计表中查询联合会是否有本年的数据
		private void queryUnitedFree(DB db) throws Exception {

			// 查询所有的联合会
			String queryUNITEDSQL = "SELECT * FROM aorg where orgtype in ('13','23','33','43')";
			
			MapList map = db.query(queryUNITEDSQL);
			if (!Checker.isEmpty(map)) {
				for (int i = 0; i < map.size(); i++) {
					String orgid = map.getRow(i).get("orgid");
					// 查询统计表中是否有记录
					String queryRECORDSQL = "SELECT * FROM lxny_union_membership_dues where f_id='" + orgid + "'";
					MapList maps = db.query(queryRECORDSQL);
					// 无记录则增加
					if (!Checker.isEmpty(maps)) {
					}else{
						Table table = new Table("am_bdp", "lxny_union_membership_dues");
						TableRow insertTr = table.addInsertRow();
						// 联合会id
						insertTr.setValue("f_id", orgid);
						// 年份
						insertTr.setValue("free_date",year);
						// 应缴总额
						insertTr.setValue("meet_total", 0);
						// 实缴总额
						insertTr.setValue("received_total", 0);
						// 应缴费数量
						insertTr.setValue("meet_number", 0);
						// 实缴费数量
						insertTr.setValue("received_number", 0);
						db.save(table);
					}

				}
			}
			
		}
	
	
		//查询需要扣费的人/机构
		private void queryUnitedStatus(DB db) throws Exception {
			String queryPAYSTATUSSQL="SELECT * FROM mall_united_press_free WHERE status='02'";
			
			MapList map_pay = db.query(queryPAYSTATUSSQL);
			for (int i = 0; i < map_pay.size(); i++) {
				String id=map_pay.getRow(i).get("id");
				String f_id=map_pay.getRow(i).get("f_id");
				int free=map_pay.getRow(i).getInt("free", 0);
				String united_press_free_id=map_pay.getRow(i).get("united_press_free_id");
				String payment_org_type=map_pay.getRow(i).get("payment_org_type");
				
					//调用支付方法，返回0则成功，1失败
					int cg=Pay(db, united_press_free_id, f_id, payment_org_type,free/100);
					//判断扣费是否成功，成功则条用更新数据方法
					if (cg == 0) {
					updateRecord(db, id);
					updateUnitedFree(db, 0,free, 0, 1, f_id);
				}
			}
		}
		// 更新统计表数据
		private void updateUnitedFree(DB db, int meet_total, int received_total, int meet_number, int received_number,
				String f_id) throws Exception {
			String updateUNITEDSQL =  "update lxny_union_membership_dues " 
										+ "set meet_total=meet_total+" + meet_total+ " , " 
										+ "received_total=received_total+" + received_total + ", " 
										+ "meet_number=meet_number+"+ meet_number + ","
										+ "received_number=received_number+" + received_number + " "
										+ "where f_id='" + f_id +"'";
			db.execute(updateUNITEDSQL);

		}
		
		// 更新缴费表数据
		private void updateRecord(DB db,String uid) throws Exception {

			String updateUNITEDSQL = "update mall_united_press_free " 
									+" set payment_time='"+create_time+"', " 
									+" status='01' where id='"+uid+"'";
			db.execute(updateUNITEDSQL);
		}
		
		
		// 扣费
		private int Pay(DB db, String united_press_free_id, String f_id, String payment_org_type, double free)
				throws Exception {

			String freeCash = (Var.getDouble("GROUP_CASH_ACCOUNT", 0) * free) + "";
			// 机构抗风险自救金账户
			String freeAnit = (Var.getDouble("GROUP_ANTI_RISK_SELF_SAVING_ACCOUNT", 0) * free) + "";
			// 机构信用保证金账户
			String freeCredit = (Var.getDouble("GROUP_CREDIT_MARGIN_ACCOUNT", 0) * free) + "";
			VirementManager vir = new VirementManager();
			if ("1".equals(payment_org_type) || "2".equals(payment_org_type) || "3".equals(payment_org_type)) {
				// 扣费 社员现金账户
				JSONObject jso = vir.execute(db, united_press_free_id, "", SystemAccountClass.CASH_ACCOUNT, "", free + "", "", "支付联合会会费", "",false);
				if("0".equals(jso.get("code"))){
				// 机构现金账户
				vir.execute(db, "", f_id, "", SystemAccountClass.GROUP_CASH_ACCOUNT, freeCash, "联合会会费", "", "", false);
				// 机构抗风险自救金账户
				vir.execute(db, "", f_id, "", SystemAccountClass.GROUP_ANTI_RISK_SELF_SAVING_ACCOUNT, freeAnit, "联合会会费", "",
						"", false);
				// 机构信用保证金账户
				vir.execute(db, "", f_id, "", SystemAccountClass.GROUP_CREDIT_MARGIN_ACCOUNT, freeCredit, "联合会会费", "", "",
						false);
					
					return 0;
				}
				return 1;
			} else {
				// 扣费 机构现金账户
				JSONObject jso = vir.execute(db, united_press_free_id, "", SystemAccountClass.GROUP_CASH_ACCOUNT, "", free + "", "",
									"支付联合会会费", "", false);
				
				if("0".equals(jso.get("code"))){
					// 机构现金账户
					jso = vir.execute(db, "", f_id, "", SystemAccountClass.GROUP_CASH_ACCOUNT, freeCash, "联合会会费", "", "", false);
					// 机构抗风险自救金账户
					vir.execute(db, "", f_id, "", SystemAccountClass.GROUP_ANTI_RISK_SELF_SAVING_ACCOUNT, freeAnit, "联合会会费", "",
							"", false);
					// 机构信用保证金账户
					vir.execute(db, "", f_id, "", SystemAccountClass.GROUP_CREDIT_MARGIN_ACCOUNT, freeCredit, "联合会会费", "", "",
							false);
					return 0;
				}
				return 1;
			}
		}

		/**
		 * 创建缴费记录
		 * @param db  DB
		 * @param f_id 目标联合会id
		 * @param free 会费金额
		 * @param payment_time  缴纳会费时间
		 * @param status 会费缴纳状态 01：已缴纳 02：未缴纳;
		 * @param united_press_free_id 缴纳会费对象id
		 * @param payment_org_type 缴纳会费机构类型 50：农厂 60：涉农企业 40：合作社41：区县联合社 01：消费者 02：农户 1：消费者社员 1：单位 3：生产者社员;
		 */
		// 创建缴费记录
		private void addPayRecord(DB db, String f_id, double free_s, String payment_time, String status,
				String united_press_free_id, String payment_org_type) throws Exception {
			Table table = new Table("am_bdp", "mall_united_press_free");
			TableRow insertTr = table.addInsertRow();
			// 目标联合会id
			insertTr.setValue("f_id", f_id);
			// 缴纳会费金额
			insertTr.setValue("free", (int)free_s);
			// 缴纳会费时间
			insertTr.setValue("payment_time","");
			// 会费年份
			insertTr.setValue("free_date", year);
			// 会费状态
			insertTr.setValue("status", status);
			// 缴纳会费对象id
			insertTr.setValue("united_press_free_id", united_press_free_id);
			// 缴纳会费机构类型
			insertTr.setValue("payment_org_type",payment_org_type);
			db.save(table);
			int frees = (int) free_s;
			// 更新统计表
			updateUnitedFree(db, frees, 0, 1, 0, f_id);
		}


		// 查询所有个人/机构数据
		private void PayRecord(DB db,String table) throws Exception {
			int x = 0;
			String queryMEMBERSQL = "";
					//判断查询哪张表
					if("am_member".equals(table)){
					// 个人
					queryMEMBERSQL = "select id as s_id,* from am_member";
					}else{
					//机构
					queryMEMBERSQL = "select up.province_id as province,  "
							+ "up.city_id as city, up.zone_id as zzone, "
							+ "up.orgid as s_id, aorg.orgtype as member_type	"
							+ " from "+table+" as up "
							+ "LEFT JOIN aorg as aorg on aorg.orgid=up.orgid ";
					}
					String id = null;
					String province = null;
					String city = null;
					String zone = null;
					String membertype = null;
					
				MapList map = db.query(queryMEMBERSQL);
				for (int i = 0; i < map.size(); i++) {
					
					if (x == 0) {
						 id = map.getRow(i).get("s_id");
						 province = map.getRow(i).get("province");
						 city = map.getRow(i).get("city");
						 zone = map.getRow(i).get("zzone");
						 membertype = map.getRow(i).get("member_type");
						queryPayRecord(db, id, year, membertype, province, city, zone);
					}

				}
			}
		

		/**
		 * 查询缴费记录是否存在
		 * 
		 * @param db
		 *            DB
		 * @param id
		 *            个人id/机构id
		 * @param free_date
		 *            会费年份
		 * @param payment_org_type
		 *            缴费机构类型
		 * @param province
		 *            所属省
		 * @param city
		 *            所属市
		 * @param zone
		 *            所属地区
		 */
		private void queryPayRecord(DB db, String id, String free_date, String payment_org_type, String province,
				String city, String zone) throws Exception {
				
			
			//判断扣费对象类型，并且把元转分
			if ("1".equals(payment_org_type)) {
				// 消费者
				free = free_x;
			} else if ("3".equals(payment_org_type)) {
				// 生产者
				free = free_s;
			} else if ("2".equals(payment_org_type)) {
				// 单位
				free = free_d;
			} else if ("50".equals(payment_org_type)) {
				// 农场
				free= free_nc;
			} else if ("60".equals(payment_org_type)) {
				// 涉农企业
				free =free_sn;
			} else if ("40".equals(payment_org_type)) {
				// 合作社
				free = free_hz;
			} else if ("41".equals(payment_org_type)) {
				// 联合社 40
				free = free_lh;
			}
			
			//计算不同层级会费分配金额比例
			//总联合会分配比例
			free_a=free*country_ATAF_free_ratio;
			//省联合会分配比例
			free_p=free*province_ATAF_free_ratio;
			//市联合会分配比例
			free_c=free*city_ATAF_free_ratio;
			//区县联合会分配比例
			free_z=free*zone_ATAF_free_ratio;
			
			try {
				// 查询是总联合会缴费记录
				String querySUNITEDSQL = "SELECT * FROM mall_united_press_free where united_press_free_id=? and free_date=? and f_id='country_ATAF'";
				MapList map = db.query(querySUNITEDSQL, new String[] { id, free_date},new int[] { Type.VARCHAR, Type.VARCHAR });
				
				if (Checker.isEmpty(map)) {
					addPayRecord(db, "country_ATAF",(int)free_a, "0", "02", id,payment_org_type);
				}
				
				//查询该省、市、区各级别联合会是否存在，存在则条用查询缴费记录方法，不存在则直接跳过
				for (int k = 0; k <3; k++) {
					if(k==0){
					String querySSQL = "SELECT * FROM aorg where orgtype='23' and province=?";
					MapList map_pr = db.query(querySSQL, province, Type.VARCHAR);
					if (!Checker.isEmpty(map_pr)) {
						String f_id = map_pr.getRow(0).get("orgid");
						queryPCZUnited(db, "23", "province", province, free_date, id, payment_org_type,free_p,f_id);
					}
					}else if(k==1){
						String querySSQL = "SELECT * FROM aorg where orgtype='33' and city=?";
						MapList map_ct = db.query(querySSQL, city, Type.VARCHAR);
						if (!Checker.isEmpty(map_ct)) {
							String f_id = map_ct.getRow(0).get("orgid");
							queryPCZUnited(db, "33", "city", city, free_date, id, payment_org_type,free_c,f_id);
						}
					}else if(k==2){
						String querySSQL = "SELECT * FROM aorg where orgtype='43' and zone=?";
						MapList map_zone = db.query(querySSQL, zone, Type.VARCHAR);
						if (!Checker.isEmpty(map_zone)) {
							String f_id = map_zone.getRow(0).get("orgid");
							queryPCZUnited(db, "43", "zone", zone, free_date, id, payment_org_type,free_z,f_id);
						}
					}
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		//查询省市区联合会缴费记录是否存在
		private void queryPCZUnited(DB db,String orgtype,String pcz,String pczvalue,String free_date,String id,String payment_org_type,double free,String f_id) throws Exception {
			
			String queryPCZUNITEDSQL = " SELECT * FROM mall_united_press_free as mup "
					+ " LEFT JOIN aorg as aorg on mup.f_id=aorg.orgid " + " where aorg.orgtype=? and aorg."+pcz+"=? "
					+ " and mup.free_date=? and united_press_free_id=?";
			MapList map_p = db.query(queryPCZUNITEDSQL, new String[] {orgtype,pczvalue,free_date ,id},
					new int[] {Type.VARCHAR ,Type.VARCHAR ,Type.VARCHAR ,Type.VARCHAR});
			if (Checker.isEmpty(map_p)) {
					addPayRecord(db, f_id,free, "", "02", id, payment_org_type);
				}
			
		
		}

}