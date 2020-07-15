package com.am.withdrawals.notifyWebAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.transactions.withdraw.WithdrawService;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年9月11日
 * @version 说明:<br />
 *          支付宝批量付款回调接口
 */
public class AlipayNotifyWebApi implements IWebApiService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		String result=null;
		
		try {
			ServletOutputStream out = response.getOutputStream();

			// 获取支付宝POST过来反馈信息
			Map<String, String> params = new HashMap<String, String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter
					.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				// valueStr = new String(valueStr.getBytes("ISO-8859-1"),
				// "gbk");
				params.put(name, valueStr);
			}

			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			// 批量付款数据中转账成功的详细信息
			String success_details =request.getParameter("success_details");
			if(success_details!=null){
				success_details = new String(request.getParameter("success_details").getBytes("ISO-8859-1"), "UTF-8");
				logger.info("批量付款数据中转账成功的详细信息:"+success_details);
			}
			
			String fail_details=request.getParameter("fail_details");
			if(fail_details!=null){
				// 批量付款数据中转账失败的详细信息
				fail_details = new String(request.getParameter("fail_details").getBytes("ISO-8859-1"), "UTF-8");
				logger.info("批量付款数据中转账失败的详细信息:"+fail_details);
			}
			
			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
			
			//提现业务处理
			WithdrawService ws=new WithdrawService();
			
			//此方法会判断业务的正确性和请求是否合法
			result=ws.processAlipayNotify(params,success_details,fail_details);
			

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

}
