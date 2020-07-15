package com.am.frame.payment.impl.weipay;

import org.json.JSONObject;

import com.am.frame.transactions.callback.BusinessCallBack;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 微信支付回调接口
 * @author mac
 *
 */
public class WepayNotifyResponse 
{
	public void notiyResp(JSONObject respParams,String paytype)
	{
		DB db = null;
		if(respParams!=null)
		{
			if(respParams.has("out_trade_no"))
			{
				try 
				{
					db = DBFactory.newDB();
					//执行支付完成后操作，业务操作
					BusinessCallBack businessCallBack = new BusinessCallBack();
					
					businessCallBack.callBack(respParams.getString("out_trade_no"), db,"1");
					
				} catch (Exception e) 
				{
					e.printStackTrace();
				}finally
				{
					if(db!=null)
					{
						try 
						{
							db.close();
						} catch (JDBCException e) {
							e.printStackTrace();
						}
					}
				}
			
			}
			
		}
		
	}
}