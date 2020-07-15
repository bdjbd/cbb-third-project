package com.am.frame.transactions.virement;




import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.transactions.callback.BusinessCallBack;
import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;


/** 
 * @author  wz  
 * @descriptions  转账操作 
 * @date 创建时间：2016年4月7日 下午4:41:51 
 * @version 1.0   
 */
public class VirementManager {
	
	/**日志记录**/
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	/**
	 * 【方法一】
	 * 扣款方法
	 * @param db
	 * @param outMemberId  出账用户/机构id
	 * @param outAccountCode 出账账户code 常量 见{@link com.am.frame.systemAccount.SystemAccountClass} 
	 * @param virementNumber 交易金额 单位元
	 * @param oremakers  出账描述 即交易记录描述
	 * @param out_pay_id 出账订单号 ，如果无，可以为空
	 * @param business 业务参数，可以为空
	 * @return  code : 0成功   其他值失败   msg ：信息   out_pay_id:出账id in_pay_id :入账id
	 */
	 public JSONObject execute(DB db,String outMemberId,String outAccountCode,String virementNumber,String oremakers,String out_pay_id,String business){
		 
		 	JSONObject resultJson = null;
		 	String outId = out_pay_id;
		 	if(Checker.isEmpty(out_pay_id)){
		 		
		 		outId = UUID.randomUUID().toString();
		 	}
			
			
			TransactionDetail transactionDetail=new TransactionDetail();
	    	
	    	AfterDetailBean outDetailBean = new AfterDetailBean();
		 
		 try {
			 
			//先执行插入交易记录表
		    	
	    	if(!Checker.isEmpty(outMemberId)){
	    		outDetailBean.setTableRow(outDetailBean.getTranTable().addInsertRow());
	    		outDetailBean.setId(outId);
	    		//1 成功；2 失败或未执行
	    		outDetailBean.setTrade_state("2");
	    		if(!Checker.isEmpty(business))
	    		{
	    			JSONObject jsoObj = new JSONObject(business);
	    			//判断用户是否填写了操作原因，如果有则插入交易记录表中
	    			if(jsoObj.has("person_remaker"))
	    			{
	    				outDetailBean.setPerson_remaker(jsoObj.getString("person_remaker"));
	    			}
	    		}
	    		transactionDetail.afterActions(db, outDetailBean);
			}
			 
			MapList oList = getAccountInfo(db,outAccountCode,outMemberId);
			//检查用户是否已开通现金账户，若已开通，则从该现金账户进行扣款
			if(!Checker.isEmpty(oList)){
				//执行扣款操作 【执行方法二】
				resultJson = executeRun(db,outMemberId,oList,null,virementNumber,"",oremakers,outId,"",business,false);
				
			}else{
				resultJson = new JSONObject();
				try {
					resultJson.put("code", "999");
					resultJson.put("msg", "用户账户不存在");
					resultJson.put("out_pay_id",outId);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			resultJson = new JSONObject();
			try {
				resultJson.put("code", "999");
				resultJson.put("msg", "操作失败");
				resultJson.put("out_pay_id",outId);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		 return resultJson;
	 }
	
	/**
	 * 转账调用方法
	 * @param db
	 * @param outMemberId 转出用户/机构id
	 * @param inMemberId 转入用户/机构id   可以为空字符串
	 * @param outAccountCode 转出账号类型 常量 sa_code  常量 见{@link com.am.frame.systemAccount.SystemAccountClass} 
	 * @param inAccountCode 转入账号类型 常量 sa_code 常量 见{@link com.am.frame.systemAccount.SystemAccountClass} 
	 * @param virementNumber 转账金额 元
	 * @param iremakers 转入描述
	 * @param oremakers 转出描述
	 * @param business  业务参数
	 * @param flags  true 是 false 否 是否收取手续费
	 * @return code : 0成功 其他值失败   msg ：信息   out_pay_id:出账id in_pay_id :入账id
	 */
	public JSONObject execute(DB db,String outMemberId,String inMemberId,String outAccountCode
			,String inAccountCode,String virementNumber,String iremakers,String oremakers,String business,boolean flags){
		
		JSONObject resultJson = null;
		
		if(outMemberId.equals(inMemberId) && outAccountCode.equals(inAccountCode))
		{
			resultJson = new JSONObject();
			
			try {
				resultJson.put("code", "999");
				resultJson.put("msg", "不能给自己相同账户转账！");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			resultJson.put("out_pay_id",outId);
//			resultJson.put("in_pay_id",inId);	
			
			return resultJson;
		}
		
		String inId = UUID.randomUUID().toString();
		
		String outId = UUID.randomUUID().toString();
		
		TransactionDetail transactionDetail=new TransactionDetail();
    	
    	AfterDetailBean outDetailBean = new AfterDetailBean();
    	
    	//入账交易记录
    	AfterDetailBean inDetailBean = new AfterDetailBean();
    	
    	try {
    			//先执行插入交易记录表  出账交易记录插入
				if(!Checker.isEmpty(outMemberId)|| !Checker.isEmpty(outAccountCode)){
		    		
		    		outDetailBean.setTableRow(outDetailBean.getTranTable().addInsertRow());
		    		outDetailBean.setId(outId);
		    		//1 成功；2 失败或未执行
		    		outDetailBean.setTrade_state("2");
		    		//判断业务参数是否存在
		    		if(!Checker.isEmpty(business))
		    		{
		    			JSONObject jsoObj = new JSONObject(business);
		    			//判断是否有用户操作原因，如果有则插入交易记录表中
		    			if(jsoObj.has("person_remaker"))
		    			{
		    				outDetailBean.setPerson_remaker(jsoObj.getString("person_remaker"));
		    			}
		    		}
		    		transactionDetail.afterActions(db, outDetailBean);
				}
				
				MapList oList = getAccountInfo(db,outAccountCode,outMemberId); 
				MapList iList = null;
				
				//入账交易记录插入操作 判断入账用户id是否存在 如果存在则只需下方操作 有可能执行插入操作
				if(!Checker.isEmpty(inMemberId)){
					
					iList = getAccountInfo(db,inAccountCode,inMemberId);
					inDetailBean.setTableRow(inDetailBean.getTranTable().addInsertRow());
					inDetailBean.setId(inId);
					//1 成功；2 失败或未执行
					inDetailBean.setTrade_state("2");
					if(!Checker.isEmpty(business))
		    		{
		    			JSONObject jsoObj = new JSONObject(business);
		    			//判断是否有用户操作原因，如果有则插入交易记录表中
		    			if(jsoObj.has("person_remaker"))
		    			{
		    				outDetailBean.setPerson_remaker(jsoObj.getString("person_remaker"));
		    			}
		    		}
					transactionDetail.earningActions(db, inDetailBean);
				
				}else{
					
					//入账交易记录插入操作
					if(!Checker.isEmpty(inAccountCode)){
						
						iList = getAccountInfo(db,inAccountCode,outMemberId);
						
				    	inDetailBean.setTableRow(inDetailBean.getTranTable().addInsertRow());
						inDetailBean.setId(inId);
						//1 成功；2 失败或未执行
						inDetailBean.setTrade_state("2");
						if(!Checker.isEmpty(business))
			    		{
			    			JSONObject jsoObj = new JSONObject(business);
			    			//判断是否有用户操作原因，如果有则插入交易记录表中
			    			if(jsoObj.has("person_remaker"))
			    			{
			    				outDetailBean.setPerson_remaker(jsoObj.getString("person_remaker"));
			    			}
			    		}
						transactionDetail.earningActions(db, inDetailBean);
					}
					
				}
				
				
				if(Checker.isEmpty(oList) && Checker.isEmpty(iList)){
					
					resultJson = new JSONObject();
					resultJson.put("code", "999");
					resultJson.put("msg", "用户账户异常");
					resultJson.put("out_pay_id",outId);
					resultJson.put("in_pay_id",inId);	
					
				}else{
					
					//执行后续转账操作
					resultJson = executeRun(db,outMemberId,oList,iList,virementNumber,iremakers
							,oremakers,outId,inId,business,flags);
				}
			
		} catch (Exception e) {
			
			resultJson = new JSONObject();
			try {
				resultJson.put("code", "999");
				resultJson.put("msg", "操作失败");
				resultJson.put("out_pay_id",outId);
				resultJson.put("in_pay_id",inId);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return resultJson;
	}
	
	/**
	 * 【方法二】
	 * 转账操作调用方法
	 * @param outMemberId 用户/机构id
	 * @param outAccountId 出账id
	 * @param inAccountId 入账id
	 * @param virementNumber 转账金额
	 * @param free_type 是否收取手续费 true false
	 * @return JSONObject code:1000 转账失败，code:0转账成功 ;msg 操作结果信息  out_pay_id:出账id in_pay_id :入账id
	 */
	public JSONObject executeRun(DB db ,String outMemberId,MapList outList,MapList inList,String virementNumber,String iremakers,String oremakers,String out_pay_id,String in_pay_id,String business,boolean free_type){
		
		
//		Transaction  tx=null;
		
		JSONObject jsonObj=null;
		try{
			
//			if(db.getConnection().getAutoCommit()){
//				logger.debug("set auto commit");
//				db.getConnection().setAutoCommit(false);
//			}
			//开始事务
//			tx=db.beginTransaction();
			
			//执行转账操作 判断是否为组织机构或他人转账    【执行方法三】
			jsonObj = executeVirement(outList,inList,virementNumber,db,iremakers,oremakers,out_pay_id,in_pay_id,business,free_type);
			
			//业务处理
			String code=jsonObj.getString("code");
			processBusiness(db, out_pay_id, in_pay_id, code);
			
		}catch(Exception e){
			e.printStackTrace();
		
				
				try {
					jsonObj = new JSONObject();
					jsonObj.put("code", "1000");
					jsonObj.put("msg", "系统异常，转账失败");
					jsonObj.put("out_pay_id",out_pay_id);
					jsonObj.put("in_pay_id",in_pay_id);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
			
		}
		
		return jsonObj;
	}
	
	
	/**
	 * 支付处理结束后，处理业务回调 
	 * @param db  DB
	 * @param out_pay_id  出帐ID
	 * @param in_pay_id  出帐支付ID
	 * @param jsonObj
	 * @throws JSONException
	 * @throws Exception
	 */
	public void processBusiness(DB db, String out_pay_id, String in_pay_id,
			String code) throws JSONException, Exception {
		
		if("0".equals(code)){
			//执行支付完成后操作，业务操作
			BusinessCallBack businessCallBack = new BusinessCallBack();
			businessCallBack.callBack(in_pay_id, db,"1");
			businessCallBack.callBack(out_pay_id, db,"2");
		}
	}
	
	
	/**
	 * 【方法三】
	 * 执行转账操作
	 * @param outAccountList 转出账户信息
	 * @param inAccountList 转入账户信息
	 * @param virementNumber 转账金额 单位，元
	 * @param virtementType 转账类型
	 * @param iremakers 入账说明
	 * @param oremakers 出账说明
	 * @param out_pay_id  出账账单id  只为交易记录中用
	 * @param in_pay_id  入账账单id   只为交易记录中用
	 * @param business 业务字段
	 * @return free_type
	 */
	private JSONObject executeVirement (MapList outAccountList,
			
			MapList inAccountList, String virementNumber,DB db,String iremakers,String oremakers,String out_pay_id,String in_pay_id,String business,boolean free_type) throws Exception{
			
			JSONObject jsobj  = new JSONObject();
			
			//系统转账信息
			String sysReamarks="";
			
			//判断系统用户账户是否存在
			if(!Checker.isEmpty(outAccountList)){
				//会员财务帐号ID
		    	String accountId=outAccountList.getRow(0).get("id");
		    	sysReamarks+=getAccountName(db,accountId)+"出帐"+virementNumber+"元.";
			}
			if(!Checker.isEmpty(inAccountList)){
				String accountId=inAccountList.getRow(0).get("id");
				sysReamarks+=getAccountName(db,accountId)+"入账"+virementNumber+"元(含手续费)";
			}
			
			iremakers+=sysReamarks;
			oremakers+=sysReamarks;
			
			
			boolean flag = true;
			
			/**
			 * 最大交易额
			 */
			if(!Checker.isEmpty(outAccountList)){
				if(changeY2F(virementNumber)>(outAccountList.getRow(0).getLong("max_gmv",0))){
					
					flag = false;
					jsobj.put("code", "404");
					jsobj.put("msg", "交易金额超出账户最大交易金额限制");
					jsobj.put("out_pay_id",out_pay_id);
					jsobj.put("in_pay_id",in_pay_id);
					
					logger.info("交易金额超出账户最大交易金额限制,out_pay_id:"+out_pay_id+" ,in_pay_id:"+in_pay_id);
				
				}
			}
			
			/**
			 * 最小交易额
			 */
			if(!Checker.isEmpty(outAccountList)){
				
				//检查账号最小交易金额限制
				if(changeY2F(virementNumber)<(outAccountList.getRow(0).getLong("min_gmv",0))){
					
					flag = false;
					jsobj.put("code", "405");
					jsobj.put("msg", "交易金额超出账户最小交易金额限制");
					jsobj.put("out_pay_id",out_pay_id);
					jsobj.put("in_pay_id",in_pay_id);
				
					logger.info("交易金额超出账户最小交易金额限制,out_pay_id:"+out_pay_id+" ,in_pay_id:"+in_pay_id);
				}
			}
			
			Long virement_money  =0L;
			/**
			 * 判断交易额度
			 */
			if(flag){
				if(!Checker.isEmpty(outAccountList)){
					/**
					 * 判断转账金额是否大于转出账户的余额    判断余额是否足够支付
					 */
					if(changeY2F(virementNumber)<=outAccountList.getRow(0).getLong("balance", 0)){
						
						
						if(free_type){
							//判断后台账户配置是否收手续费
							if(!Checker.isEmpty(outAccountList.getRow(0).get("transfer_fee_ratio"))){
								//计算手续费
								virement_money  =(long)Math.floor(changeY2F(virementNumber)*(new Float(outAccountList.getRow(0).get("transfer_fee_ratio"))));
							}
							
						}
						/**1 更新转出账户信息*/  
						if(!Checker.isEmpty(outAccountList)){
							//【执行方法四】
							updateAccount(outAccountList,db,changeY2F(virementNumber),"out",virement_money,oremakers,out_pay_id,business,changeY2F(virementNumber));
						}
						
						/** 2 更新转入账户余额*/
						if(!Checker.isEmpty(inAccountList)){
							////【执行方法四】
							updateAccount(inAccountList,db,changeY2F(virementNumber)-virement_money,"in",virement_money,iremakers,in_pay_id,business,changeY2F(virementNumber));
						}

						jsobj.put("code", "0");
						jsobj.put("msg", "支付成功");
						jsobj.put("out_pay_id", out_pay_id);
						jsobj.put("in_pay_id",in_pay_id);
					}else{
						
						
						jsobj.put("code", "403");
						jsobj.put("msg", "转账操作失败，用户余额不足！");
						jsobj.put("out_pay_id", out_pay_id);
						jsobj.put("in_pay_id",in_pay_id);
					
					}
				}else{
					/**1 更新转出账户信息*/
					if(!Checker.isEmpty(outAccountList)){
						updateAccount(outAccountList,db,changeY2F(virementNumber),"out",virement_money,oremakers,out_pay_id,business,changeY2F(virementNumber));
					}
					
					/** 2 更新转入账户余额*/
					if(!Checker.isEmpty(inAccountList)){
						updateAccount(inAccountList,db,changeY2F(virementNumber)-virement_money,"in",virement_money,iremakers,in_pay_id,business,changeY2F(virementNumber));
					}
					
					
					jsobj.put("code", "0");
					jsobj.put("msg", "支付成功");
					jsobj.put("out_pay_id", out_pay_id);
					jsobj.put("in_pay_id",in_pay_id);
					jsobj.put("金额",virementNumber);
				}
				
			}
			
	
		return jsobj;
	}

	
	
	
	/**
	 * 获取用户账户信息
	 * @param db
	 * @param accountId
	 * @return
	 */
	public  MapList getAccountInfo(DB db,String outAccountCode,String outMemberId){
		
		MapList list = null;
		
		String sql = "";	
		//后台
			sql = "select mai.*"
					+ " ,myac.max_gmv"
					+ " ,myac.min_gmv"
					+ " ,myac.sa_code"
					+ " ,myac.id as class_id"
					+ " ,myac.transfer_fee_ratio"
					+ " FROM mall_account_info as mai"
					+ " left join mall_system_account_class as myac on "
					+ " myac.id=mai.a_class_id "
					+ " WHERE mai.member_orgid_id ='"+outMemberId+"'"
					+ " and myac.status_valid='1' and myac.sa_code = '"+outAccountCode+"'";	
		try {
			list = db.query(sql); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
    /**
     * 【方法四】
     * 更新用户转出、转入账户
     * @param list 转入转出账户信息
     * @param type 转账类型 in:转入 out:转出
     * @param virementNumber 转账金额  减去手续费的金额
     * @param type  out:出账，
     * @param virement_money  手续费
     * @param remakers 备注
     * @param pay_id 支付ID
     * @param business 业务参数
     * @param userSubmitMeony 用户提交交易金额 这个是含手续费的
     */
    private void updateAccount(MapList list,DB db,Long virementNumber,
    		String type,Long virement_money,String remakers,
    		String pay_id,String business,Long userSubmitMeony){
    	
    	/**
		 * 更新转出账户余额
		 * 转入账户余额*转账手续费-需转账金额
		 */
    	long money = 0;
    	
    	TransactionDetail transactionDetail=new TransactionDetail();
    	
    	AfterDetailBean afterDetailBean = new AfterDetailBean();
    	
    	/**转账操作sql*/
    	String sql = "";
    	
    	//转账机构或者会员id
    	String memberId=list.getRow(0).get("member_orgid_id");
    	//会员财务帐号ID
    	String accountId=list.getRow(0).get("id");
    	//财务帐号类型
    	String saClassId=list.getRow(0).get("class_id");
    	
    	if("out".equals(type)){
    		//出账转出，  新余额 = 当前余额 - 出账金额
    		money = (list.getRow(0).getLong("balance",0)-virementNumber);
    		
			//更新前台表
    		//更新出账账户余额信息
			sql = "update mall_account_info "
					+ " set balance='"+ money +"' where id ='"+list.getRow(0).get("id")+"'";
			
//			logger.info("out 操作金额:"+virementNumber+",操作字段:balance,操作数据主键:"+list.getRow(0).get("id"));
			try {
				logger.info(getAccountName(db,accountId)+"出帐"+virementNumber+"分，余额为："+money);
			} catch (JDBCException e1) {
				e1.printStackTrace();
			}
			
			
			afterDetailBean.setTableRow(afterDetailBean.getTranTable().addUpdateRow());
			afterDetailBean.setMember_id(memberId);
    		afterDetailBean.setAccount_id(accountId);
    		afterDetailBean.setBusiness_id(saClassId);
    		afterDetailBean.setSa_class_id(saClassId);
    		afterDetailBean.setCounter_fee(virement_money);
    		afterDetailBean.setTrade_total_money(virementNumber);
    		afterDetailBean.setRmarks(remakers);
    		afterDetailBean.setUserSubmitTrade_money(userSubmitMeony);
    		
    		//1 成功；2 失败或未执行
    		afterDetailBean.setTrade_state("1");
    		if(!Checker.isEmpty(business)){
    			afterDetailBean.setBusiness_json(business);
    		}
    		/**
	    	 * 执行账户余额更新操作
	    	 */
	    	try {
	    		transactionDetail.upDateActions(db, afterDetailBean,pay_id);
	    		db.execute(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	
    	}else{
    		
    		/**
        	 * 手续费
        	 */
    		//入账账户，       新余额 = 交易金额 + 当前余额
    		money = virementNumber+(list.getRow(0).getLong("balance",0));
    		//更新入账账户余额信息
	    		sql = "update mall_account_info "
	    				+ " set balance='"+money+"',total_amount ='"+(Long.parseLong(list.getRow(0).get("total_amount"))+virementNumber)+"'  where id ='"+list.getRow(0).get("id")+"'";
	    		
	    		try {
					logger.info(getAccountName(db,accountId)+"入帐"+virementNumber+"分，余额为："+money);
				} catch (JDBCException e1) {
					e1.printStackTrace();
				}
	    		
	    		
	    		afterDetailBean.setTableRow(afterDetailBean.getTranTable().addUpdateRow());
	    		afterDetailBean.setMember_id(memberId);
	    		afterDetailBean.setAccount_id(accountId);
	    		afterDetailBean.setBusiness_id(saClassId);
	    		afterDetailBean.setSa_class_id(saClassId);
	    		afterDetailBean.setCounter_fee(virement_money);
	    		afterDetailBean.setTrade_total_money(virementNumber);
	    		afterDetailBean.setRmarks(remakers);
	    		afterDetailBean.setUserSubmitTrade_money(userSubmitMeony);
	    		
	    		//1 成功 2 失败
	    		afterDetailBean.setTrade_state("1");
	    		if(!Checker.isEmpty(business)){
	    			afterDetailBean.setBusiness_json(business);
	    		}
	    		/**
		    	 * 执行账户余额更新操作
		    	 */
		    	try {
		    		transactionDetail.upDateActions(db, afterDetailBean,pay_id);
				
		    		db.execute(sql);
				} catch (Exception e) {
					e.printStackTrace();
					
				}
    		
    	}
    	  	
    }
    /**   
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额  
     *   
     * @param amount  
     * @return  
     */    
    public static int changeY2FInt(String amount){
    	if(amount==null){
    		return 0;
    	}
        String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额    
        int index = currency.indexOf(".");    
        int length = currency.length();    
        int amLong = 0;    
        if(index == -1){    
            amLong = Integer.valueOf(currency+"00");    
        }else if(length - index >= 3){    
            amLong = Integer.valueOf((currency.substring(0, index+3)).replace(".", ""));    
        }else if(length - index == 2){    
            amLong = Integer.valueOf((currency.substring(0, index+2)).replace(".", "")+0);    
        }else{    
            amLong = Integer.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");    
        }    
        return amLong;    
    }
    /**   
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额  
     *   
     * @param amount  
     * @return  
     */    
    public static Long changeY2F(String amount){
    	if(Checker.isEmpty(amount)){
    		return 0l;
    	}
        String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额    
        int index = currency.indexOf(".");    
        int length = currency.length();    
        Long amLong = 0l;    
        if(index == -1){    
            amLong = Long.valueOf(currency+"00");    
        }else if(length - index >= 3){    
            amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));    
        }else if(length - index == 2){    
            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);    
        }else{    
            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");    
        }    
        return amLong;    
    }
    
    /**   
     * 将元为单位的转换为分 替换小数点
     *   
     * @param amount  
     * @return  
     */  
    public static Long changeY2F(double amount){
    	return changeY2F(amount+"");
    }
    
    
    /**   
     * 将分为单位的转换为元  替换小数点，支持以逗号区分的金额  ，保留2位小数。
     *   
     * @param amount  
     * @return  
     */    
    public static Double changeF2Y(long mAmount){    
        double reulst=0.0;
    	
        if(!Checker.isEmpty(mAmount+"")){
        	String amount=mAmount+"";
        	String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额    
        	reulst=Long.valueOf(currency)/100;
        }
        
        return reulst;    
    }
    
    
    /**   
     * 将分为单位的转换为元  替换小数点，支持以逗号区分的金额  ，保留2位小数。
     *   
     * @param amount  
     * @return  
     */    
    public static Double changeF2Y(String mAmount){    
        double reulst=0.0;
    	
        if(!Checker.isEmpty(mAmount+"")){
        	
        	if(mAmount.indexOf(".")>0){
        		mAmount=mAmount.substring(0,mAmount.indexOf("."));
        	}
        	
        	long lAmont=Long.parseLong(mAmount);
        	
        	String amount=lAmont+"";
        	String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额    
        	reulst=Long.valueOf(currency)/100;
        }
        
        return reulst;    
    }
    

    /**
     * 检查帐号余额是否充足
     * @param buyerAccount   会员/机构ID
     * @param totalAmount    购买金额
     * @param accountCode    帐号类型
     * @param db
     * @throws JDBCException 
     */

    public JSONObject balance(String buyerAccount, String totalAmount,
			String accountCode, DB db) throws JDBCException {
    	Long balance = 0L;
    	Long total = Long.parseLong(totalAmount);
    	//查询机构帐号余额
		String accountInfoSql = "SELECT balance FROM  mall_account_info "
				+ " WHERE member_orgid_id='"+buyerAccount+"'  AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+accountCode+"')";
		MapList accountInfoList = db.query(accountInfoSql);
		if (!Checker.isEmpty(accountInfoList)) {
			//帐号余额
			balance= Long.parseLong(accountInfoList.getRow(0).get("balance"));
		}
		JSONObject resultJson  = new JSONObject();
		try {
			if(total>balance){
					resultJson.put("code", "503");
					resultJson.put("msg", "余额不足！");
			}else{
					resultJson.put("code", "0");
					resultJson.put("msg", "余额充足！");
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return resultJson;
		
	}

    /**
     * 获取账号名称
     * @param db DB
     * @param memberId 会员或者机构id
     * @param accountClassId  会员财务帐号ID
     * @return
     * @throws JDBCException 
     */
    public String getAccountName(DB db,String accountId) throws JDBCException{
    	String result=null;
    	
    	String sql="SELECT ma.loginaccount,sac.sa_code,sac.sa_name "+
					"  FROM mall_system_account_class AS sac "+
					"  LEFT JOIN mall_account_info AS acinfo ON sac.id=acinfo.a_class_id "+
					"  LEFT JOIN mall_account as ma on ma.id = acinfo.member_orgid_id "+
					"  WHERE acinfo.id=? ";
    	
    	MapList map=db.query(sql, accountId,Type.VARCHAR);
    	if(!Checker.isEmpty(map)){
    
    		result= map.getRow(0).get("loginaccount")+map.getRow(0).get("sa_name");
    	}
    	return result;
    }
    
    
    /**
     * 强制将double类型装好成long类型
     * @param number
     * @return
     */
    public static long double2Long(double number){
    	long result=0;
    	
    	String fNumber=number+"";
    	
    	if(fNumber.contains(".")){
    		fNumber=fNumber.substring(0,fNumber.indexOf("."));
    	}
    	result=Long.valueOf(fNumber);
    	
    	return result;
    }
	/**
	 * 转账调用方法
	 * @param db
	 * @param outMemberId 转出用户/机构id
	 * @param inMemberId 转入用户/机构id   可以为空字符串
	 * @param outAccountCode 转出账号类型 常量 sa_code  常量 见{@link com.am.frame.systemAccount.SystemAccountClass} 
	 * @param inAccountCode 转入账号类型 常量 sa_code 常量 见{@link com.am.frame.systemAccount.SystemAccountClass} 
	 * @param virementNumber 转账金额 元
	 * @param iremakers 转入描述
	 * @param oremakers 转出描述
	 * @param business  业务参数
	 * @param flags  true 是 false 否 是否收取手续费
	 * @return code : 0成功 其他值失败   msg ：信息   out_pay_id:出账id in_pay_id :入账id
	 */
	public JSONObject executeJin(DB db,String outMemberId,String inMemberId,String outAccountCode
			,String inAccountCode,String virementNumber,String iremakers,String oremakers,String business,boolean flags){
		
		JSONObject resultJson = null;
		
//		if(outMemberId.equals(inMemberId) && outAccountCode.equals(inAccountCode))
//		{
//			resultJson = new JSONObject();
//			
//			try {
//				resultJson.put("code", "999");
//				resultJson.put("msg", "不能给自己相同账户转账！");
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
////			resultJson.put("out_pay_id",outId);
////			resultJson.put("in_pay_id",inId);	
//			
//			return resultJson;
//		}
		
		String inId = UUID.randomUUID().toString();
		
		String outId = UUID.randomUUID().toString();
		
		TransactionDetail transactionDetail=new TransactionDetail();
    	
    	AfterDetailBean outDetailBean = new AfterDetailBean();
    	
    	//入账交易记录
    	AfterDetailBean inDetailBean = new AfterDetailBean();
    	
    	try {
    			//先执行插入交易记录表  出账交易记录插入
				if(!Checker.isEmpty(outMemberId)|| !Checker.isEmpty(outAccountCode)){
		    		
		    		outDetailBean.setTableRow(outDetailBean.getTranTable().addInsertRow());
		    		outDetailBean.setId(outId);
		    		//1 成功；2 失败或未执行
		    		outDetailBean.setTrade_state("2");
		    		//判断业务参数是否存在
		    		if(!Checker.isEmpty(business))
		    		{
		    			JSONObject jsoObj = new JSONObject(business);
		    			//判断是否有用户操作原因，如果有则插入交易记录表中
		    			if(jsoObj.has("person_remaker"))
		    			{
		    				outDetailBean.setPerson_remaker(jsoObj.getString("person_remaker"));
		    			}
		    		}
		    		transactionDetail.afterActions(db, outDetailBean);
				}
				
				MapList oList = getAccountInfo(db,outAccountCode,outMemberId); 
				MapList iList = null;
				
				//入账交易记录插入操作 判断入账用户id是否存在 如果存在则只需下方操作 有可能执行插入操作
				if(!Checker.isEmpty(inMemberId)){
					
					iList = getAccountInfo(db,inAccountCode,inMemberId);
					inDetailBean.setTableRow(inDetailBean.getTranTable().addInsertRow());
					inDetailBean.setId(inId);
					//1 成功；2 失败或未执行
					inDetailBean.setTrade_state("2");
					if(!Checker.isEmpty(business))
		    		{
		    			JSONObject jsoObj = new JSONObject(business);
		    			//判断是否有用户操作原因，如果有则插入交易记录表中
		    			if(jsoObj.has("person_remaker"))
		    			{
		    				outDetailBean.setPerson_remaker(jsoObj.getString("person_remaker"));
		    			}
		    		}
					transactionDetail.earningActions(db, inDetailBean);
				
				}else{
					
					//入账交易记录插入操作
					if(!Checker.isEmpty(inAccountCode)){
						
						iList = getAccountInfo(db,inAccountCode,outMemberId);
						
				    	inDetailBean.setTableRow(inDetailBean.getTranTable().addInsertRow());
						inDetailBean.setId(inId);
						//1 成功；2 失败或未执行
						inDetailBean.setTrade_state("2");
						if(!Checker.isEmpty(business))
			    		{
			    			JSONObject jsoObj = new JSONObject(business);
			    			//判断是否有用户操作原因，如果有则插入交易记录表中
			    			if(jsoObj.has("person_remaker"))
			    			{
			    				outDetailBean.setPerson_remaker(jsoObj.getString("person_remaker"));
			    			}
			    		}
						transactionDetail.earningActions(db, inDetailBean);
					}
					
				}
				
				
//				if(Checker.isEmpty(oList) && Checker.isEmpty(iList)){
//					
//					resultJson = new JSONObject();
//					resultJson.put("code", "999");
//					resultJson.put("msg", "用户账户异常");
//					resultJson.put("out_pay_id",outId);
//					resultJson.put("in_pay_id",inId);	
//					
//				}else{
					
					//执行后续转账操作
					resultJson = executeRun(db,outMemberId,oList,iList,virementNumber,iremakers
							,oremakers,outId,inId,business,flags);
//				}
			
		} catch (Exception e) {
			
			resultJson = new JSONObject();
			try {
				resultJson.put("code", "999");
				resultJson.put("msg", "操作失败");
				resultJson.put("out_pay_id",outId);
				resultJson.put("in_pay_id",inId);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return resultJson;
	}
  
}
