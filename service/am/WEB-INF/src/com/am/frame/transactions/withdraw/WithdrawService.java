package com.am.frame.transactions.withdraw;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.am.frame.weichart.beans.EntPaymentInfo;
import com.am.frame.weichart.util.WeiChartAPIUtils;
import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年9月8日
 * @version 说明:<br />
 *          提现支付管理类
 * 
 */
public class WithdrawService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 提现
	 * 
	 * @param type
	 *            提现账号类型：'1' THEN '银行卡' WHEN '2' THEN '支付宝' WHEN '3' THEN '微信'
	 * @param widIds
	 *            提现id集合
	 * @param db
	 *            DB
	 * @throws JDBCException
	 */
	public void executeWithdar(String type, List<String> widIds, DB db,
			String ip,ActionContext ac) throws Exception {

		if ("1".equals(type)) {
			// '银行卡'
		}
		if ("2".equals(type)) {
			// 支付宝
			String widsStr="'',";
			for(int i=0;i<widIds.size();i++){
				widsStr+="'"+widIds.get(i)+"',";
			}
			widsStr+="''";
			
			
			String url="/alipay/batch_trans/alipayapi.jsp?widsStr="+widsStr+"";
			
			logger.info("windoopen 参数url:"+url);
			
			Ajax ajax=new Ajax(ac);
			ajax.addScript("window.open(\""+url+"\");location.href=\"/am_bdp/withdrawals.do?m=s\";");
			ajax.send();
			
		}
		if ("3".equals(type)) {

			// 微信
			logger.info("使用微信给社员付款");
			weiChartPaymentWithdarw(db, widIds, ip);
		}

	}

	/**
	 * 微信付款到微信个人账号
	 * 
	 * @param db
	 * @param widIds
	 * @throws JDBCException
	 */
	private String weiChartPaymentWithdarw(DB db, List<String> widIds, String ip)
			throws JDBCException {

		String result = null;

		// 1，获取支付金额，openid，收款人证实姓名
		// 2，提现手续费已经扣除掉了
		String queyrSQL = "SELECT wd.id AS wid,wd.cash_withdrawal,wd.member_id"
				+ "  ,wd.remarks,m.membername,m.openid,m.app_openid"
				+ "  FROM withdrawals AS wd "
				+ "  LEFT JOIN am_member AS m ON wd.member_id=m.id  "
				+ "  WHERE wd.id=? ";

		for (int i = 0; i < widIds.size(); i++) {
			String wid = widIds.get(i);
			// 查询提现信息
			MapList map = db.query(queyrSQL, wid, Type.VARCHAR);

			if (!Checker.isEmpty(map)) {

				Row wInfo = map.getRow(0);

				String cashWithdrawals = wInfo.get("cash_withdrawal");
				String memberId = wInfo.get("member_id");
				// 验证提现数字是否正确
				result = weiChartPayValidate(db, cashWithdrawals, memberId);
				logger.info("微信提现验证结果："+result);
				// 验证未通过
				if (result != null) {
					return result;
				}

				String uuid = UUID.randomUUID().toString();
				String noceStr = uuid.replaceAll("-", "").toUpperCase();

				String sign = "";

				WeiChartAPIUtils utils = new WeiChartAPIUtils();

				EntPaymentInfo entPaymentInfo = new EntPaymentInfo();

				// 这个与微信开放平台对应 的AppId相同
				entPaymentInfo.setMch_appid(Var.get("wepay_mobile_appid"));
				// 这个与微信开放平台的mchid相同
				entPaymentInfo.setMchid(Var.get("wepay_mobile_partner"));
				// 随机数，不可超过32位
				entPaymentInfo.setNonce_str(noceStr);
				// 商户订单号 提现ID 商户订单号为提现表主键
				entPaymentInfo.setPartner_trade_no(wInfo.get("wid"));
				// 提现OPENID wInfo.get("openid") openid微信公众账号对应的opendid；
				// app_openid微信开放平台对于的openid
				entPaymentInfo.setOpenid(wInfo.get("app_openid"));

				entPaymentInfo.setCheck_name("OPTION_CHECK");

				entPaymentInfo.setRe_user_name(wInfo.get("membername"));
				// 支付金额 单位分
				entPaymentInfo.setAmount(cashWithdrawals);

				entPaymentInfo.setDesc(wInfo.get("remarks"));
				entPaymentInfo.setSpbill_create_ip(ip);

				// API KEY
				entPaymentInfo.setKey(Var.get("wepay_mobile_key"));

				// 获取签名后的xml信息
				String queryStr = utils.createEntPaymentInfo(entPaymentInfo);

				logger.info("微信提现参数："+queryStr);

				// 企业付款业务接口地址
				String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
				String questReslt = null;
				try {
					questReslt = doWeiPay(url, queryStr,
							Var.get("wepay_mobile_partner"));
					logger.info("支付返回结果：" + questReslt);

					Map<String, String> xmlMap = WeiChartAPIUtils.xmlStrToMap(questReslt);

					String return_code = xmlMap.get("return_code");

					// 转账成功
					if ("SUCCESS".equals(return_code)) {
						logger.info("微信支付方，支付成功！：" + return_code);
						// 获取 商户订单号 提现ID 商户订单号为提现表主键
						String partnerTradeNo = xmlMap.get("partner_trade_no");
						// 微信订单号
						String paymentNo = xmlMap.get("payment_no");
						// 更新交易状态为成功
						updateWithdrawSuccess(db, partnerTradeNo, paymentNo);
					}

					// 转账失败
					if ("FAIL".equals(return_code)) {
						logger.info("微信支付方，支付失败！：" + return_code);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}else{
				logger.info("没有对应的提现信息！wid：" + wid);
			}// end if
		}

		return result;
	}

	/**
	 * 付款成功，更新提现信息
	 * 
	 * @param db
	 * @param partnerTradeNo
	 *            提现ID
	 * @param paymentNo
	 *            微信支付号
	 * @throws JDBCException
	 */
	private void updateWithdrawSuccess(DB db, String partnerTradeNo,
			String paymentNo) throws JDBCException {
		String updateSQL = "UPDATE withdrawals SET payment_time=now(),settlement_state=4,batch_number=? WHERE id=? ";

		db.execute(updateSQL, new String[] { paymentNo, partnerTradeNo },
				new int[] { Type.VARCHAR, Type.VARCHAR });
	}
	
	

	/**
	 * 支付宝付款到个人支付宝账号
	 * 
	 * @param db
	 * @param widIds
	 *            提现ID
	 */
	public String alipayPaymentWithdarw(DB db,String widsStr)throws Exception {
		
		String result="";
		
		long count=1;
		
		//查询批次号，
		String querySQL="SELECT count(*) AS count FROM withdrawals ";
		
		MapList map=db.query(querySQL);
		if(!Checker.isEmpty(map)){
			count=map.getRow(0).getLong("count",1);
		}
		
		// 付款笔数   必填，即参数detail_data的值中，“|”字符出现的数量加1，最大支持1000笔
		//（即“|”字符出现的数量999个）
		String batch_num ="1";
		
		
		// 付款总金额    必填，即参数detail_data的值中所有金额的总和 付款文件中的总金额。
		//格式：10.01，精确到分
		String batch_fee="0.00";
		//查询提现总金额
		querySQL="SELECT count(*) AS count, "
//				+ " trim(to_char(sum(cash_withdrawal)/100.0,'999999999999D99')) AS sum "
				+ " SUM(cash_withdrawal)*0.01 AS sum"
				+ " FROM  withdrawals WHERE id IN ("+widsStr+")";
		
		map=db.query(querySQL);
		if(!Checker.isEmpty(map)){
			batch_fee=map.getRow(0).get("sum");
			batch_num=map.getRow(0).get("count");
		}
		
		// 服务器异步通知页面路径 // 需http://格式的完整路径，不允许加?id=123这类自定义参数
		String notify_url =Var.get("alipay_batch_trans_notify_url");

		// 付款账号 // 必填
		String email = Var.get("alipay_batch_trans_alipay_email");
		

		// 付款账户名// 必填，个人支付宝账号是真实姓名公司支付宝账号是公司名称
		String account_name = Var.get("alipay_batch_trans_account_name");
		
		
		// 付款当天日期 // 必填，格式：年[4位]月[2位]日[2位]，如：20100801
		SimpleDateFormat sdfs=new SimpleDateFormat("yyyyMMdd");
		String pay_date =sdfs.format(new Date());
		

		// 批次号
		//new String(request.getParameter("WIDbatch_no").getBytes("ISO-8859-1"), "UTF-8");
		// 必填，格式：当天日期[8位]+序列号[3至16位]，如：201008010000001
//		DecimalFormat   df   =   new   DecimalFormat("0000000000000000");
		String batch_no =pay_date+System.currentTimeMillis();
		
		// 付款详细数据  // 必填，
		//格式：流水号1^收款方帐号1^真实姓名^付款金额1^备注说明1|
		//流水号2^收款方帐号2^真实姓名^付款金额2^备注说明2....
		String detail_data="";
		querySQL="SELECT mb.bank_holder,mb.bank_code, "+
				" ws.id||'^'||mb.bank_code||'^'||mb.bank_holder||'^'||"+
				" ws.cash_withdrawal *0.01||'^'||ws.remarks AS detail_data_item "+
				" ,ws.* "+
				" FROM withdrawals AS ws "+
				" LEFT JOIN mall_member_bank AS mb ON ws.in_account_id=mb.id "+
				" WHERE ws.id IN ("+widsStr+")";
		
		map=db.query(querySQL);
		
		if(!Checker.isEmpty(map)){
			for(int i=0;i<map.size();i++){
				Row row=map.getRow(i);
				detail_data+=row.get("detail_data_item")+"|";
			}
		}
		
		if(!Checker.isEmpty(detail_data)&&(detail_data.lastIndexOf("|")==detail_data.length()-1)){
			detail_data=detail_data.substring(0, detail_data.length()-1);
		}
		
		logger.info("请求参数 detail_data：" + detail_data);
		
		
		//更新批次好
//		batch_no
		String updateSQL="UPDATE withdrawals SET batch_number=? WHERE id IN ("+widsStr+") ";
		db.execute(updateSQL,new String[]{
				batch_no
		},new int[]{
				Type.VARCHAR
		});
		
		// ////////////////////////////////////////////////////////////////////////////////
		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "batch_trans_notify");
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("email", email);
		sParaTemp.put("account_name", account_name);
		sParaTemp.put("pay_date", pay_date);
		sParaTemp.put("batch_no", batch_no);
		sParaTemp.put("batch_fee", batch_fee);
		sParaTemp.put("batch_num", batch_num);
		sParaTemp.put("detail_data", detail_data);

		// 建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");

		logger.info("请求参数：" + sHtmlText);
		
		result=sHtmlText;
		return result;
	}

	private String doWeiPay(String url, String data, String certKey)
			throws Exception {
		
		logger.info("开始调用微信提现接口 ,data："+data);
		
		String jsonStr = null;
		/**
		 * 注意PKCS12证书 是从微信商户平台-》账户设置-》 API安全 中下载的
		 */
		String apiClientCertPath = Var
				.get("OPENPlatformWeiChartApiClientCertPath");
		
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		
		FileInputStream instream = new FileInputStream(new File(
				apiClientCertPath));// P12文件目录
		try {
			keyStore.load(instream, certKey.toCharArray());// 这里写密码..默认是你的MCHID
		} finally {
			instream.close();
		}

		SSLContext sslcontext = SSLContexts.custom()
				.loadKeyMaterial(keyStore, certKey.toCharArray())// 这里也是写密码的
				.build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf).build();
		try {
			HttpPost httpost = new HttpPost(url); // 设置响应头信息
			httpost.addHeader("Connection", "keep-alive");
			httpost.addHeader("Accept", "*/*");
			httpost.addHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httpost.addHeader("Host", "api.mch.weixin.qq.com");
			httpost.addHeader("X-Requested-With", "XMLHttpRequest");
			httpost.addHeader("Cache-Control", "max-age=0");
			httpost.addHeader("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
			httpost.setEntity(new StringEntity(data, "UTF-8"));
			CloseableHttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();

			jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			logger.info("微信付款返回结果字符串 jsonStr："+jsonStr);

			EntityUtils.consume(entity);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.close();
		}
		return jsonStr;
	}

	/**
	 * 微信支付验证
	 * 
	 * @param db
	 * @param cashWithdrawals
	 * @param ac
	 * @return ◆ 给同一个实名用户付款，单笔单日限额2W/2W ◆ 给同一个非实名用户付款，单笔单日限额2000/2000 ◆
	 *         一个商户同一日付款总额限额100W ◆ 单笔最小金额默认为1元 ◆
	 *         每个用户每天最多可付款10次，可以在商户平台--API安全进行设置 ◆ 给同一个用户付款时间间隔不得低于15秒
	 * @throws JDBCException
	 */

	public String weiChartPayValidate(DB db, String cashWithdrawals,
			String memberId) throws JDBCException {
		String result = null;

		// 1,验证账户同一日付款金额
		// 已付款金额+需要付款的金额的钱数应该小时100w
//		String checkSQL = "SELECT count(cash_withdrawal+"
//				+ cashWithdrawals
//				+ ")-100000000 AS result "
//				+ " FROM withdrawals WHERE settlement_state=4 AND to_char(payment_time,'yyyy-MM-dd')=to_char(now(),'yyyy-MM-dd')";
		String checkSQL="SELECT count(ws.cash_withdrawal)-100000000 AS result  "+
					" FROM withdrawals AS ws "+
					" LEFT JOIN mall_member_bank  AS cd ON ws.in_account_id=cd.id "+
					" WHERE ws.settlement_state=4  "+
					" AND cd.account_type='3' "+
					" AND to_char(ws.payment_time,'yyyy-MM-dd')=to_char(now(),'yyyy-MM-dd')";
		
		
		MapList map = db.query(checkSQL);
		if (!Checker.isEmpty(map) && map.getRow(0).getInt("result", 0)> 0) {
			result = "微信今日付款金额已经超过100W";
			return result;
		}

		// 2,验证单笔交易金额值
		if (2000*100 < Double.parseDouble(cashWithdrawals)) {
			result = "微信申请金额不能超过2000元";
			return result;
		}
		// 验证最小交易金额
		if (100 > Double.parseDouble(cashWithdrawals)) {
			result = "微信最小金额为1元";
			return result;
		}

		checkSQL = "SELECT count(*)  AS times "
				+ " FROM withdrawals "
				+ " WHERE settlement_state=4 "
				+ " AND to_char(payment_time,'yyyy-MM-dd')=to_char(now(),'yyyy-MM-dd')"
				+ " AND member_id='" + memberId + "' ";

		map = db.query(checkSQL);
		if (!Checker.isEmpty(map) && map.getRow(0).getInt("times", 0) >= 10) {
			result = "微信每个用户每天最多可付款10次";
			return result;
		}

		return result;
	}

	
	/**
	 * 此方法会判断业务的正确性和请求是否合法,并完成对于业务的处理
	 * @param params  通知接口请求参数集合
	 * @param success_details  接口调用成功参数
	 * @param fail_details   接口调用失败请求参数
	 * @return  处理返回值：success，表示成功；fail，表示失败，此值为需要返回给接口调用方阿里，
	 * 必须返回这两个值中的一个.
	 */
	public String processAlipayNotify(Map<String, String> params,
			String success_details, String fail_details) {
		
		String result=null;
		
		DB db=null;
		
		try{
			db=DBFactory.newDB();
		
			if (AlipayNotify.verify(params)) {// 验证成功
				// ////////////////////////////////////////////////////////////////////////////////////////
				//回调接口返回参数后，更新提现业务状态数据
				//批量付款数据中转账成功的详细信息:
				// 判断是否在商户网站中已经做过了这次通知返回的处理
				// 如果没有做过处理，那么执行商户的业务程序
				// 如果有做过处理，那么不执行商户的业务程序
				result="success";
				logger.info("付款成功:"+success_details);
				
				//7cc925ee-e6dd-4ce2-a438-a7ace9af2080^yuebin616@126.com^??^1.00^S^^201609130119338519^20160913154810|
				//支付成功，处理业务数据位置
				
				updateStatae(db, result,success_details);
				
			} else {// 验证失败
				result="fail";
				logger.info("付款失败:"+fail_details);
				updateStatae(db, result,success_details);
			}
		
		}catch(Exception e){
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
		
		return result;
	}
	
	
	/**
	 * 
	 * @param db
	 * @param state success:表示成功；fail:表示失败
	 * @param details 详情
	 * @throws JDBCException
	 */
	private void updateStatae(DB db,String state,String details) throws JDBCException{
		
		String[] detailItems=details.split("\\|");//对批量支付的详情进行分割，获取每个详情
		String updateSQL="";
		
		if("success".equals(state)){
			updateSQL="UPDATE withdrawals SET settlement_state=4,notify_details=? WHERE id=? ";
		}
		if("fail".equals(state)){
			updateSQL="UPDATE withdrawals SET notify_details=? WHERE settlement_state<>4 AND id=? ";
		}
		
		//DB批处理
		List<String[]> updateParams=new ArrayList<String[]>();
		for(int i=0;i<detailItems.length;i++){
			//批量支付详情条目 :流水号^收款方账号^收款账号姓名^付款金额^失败标识(F)^失败原因^支付宝内部流水号^完成
			String item=detailItems[i];
			//切割获取流水号。注：流水号即我们系统中的提现ID;
			String wid=detailItems[i].split("\\^")[0];
			updateParams.add(new String[]{item,wid});
		}
		
		//批量插入数据
		db.executeBatch(updateSQL, updateParams,
				new int[]{Type.VARCHAR,Type.VARCHAR});
	}

}
