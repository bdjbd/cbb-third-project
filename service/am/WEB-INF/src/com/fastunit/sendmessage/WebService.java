package com.fastunit.sendmessage; 
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import com.fastunit.Var;


public class WebService {
	private static final String NAMESPACE = "http://ws.apache.org/axis2";
	private static String url="";
	private static int accessFlag = 0;
	
	/**
	 * 访问后台Webservice返回SoapObject
	 * @param serviceName webservice名称
	 * @param METHOD_NAME 方法名
	 * @param ArrValues 参数集合
	 * @return SoapObject
	 */
	public static SoapObject ConnectWebService(String methodName,String[] ArrValues)
	{
		accessFlag = 0;
		//url = "http://192.168.1.30:8080/FormService/services/MPadDataService";//固定测试地址 
		url="http://gocom.ylshenhua.com:9901/GoComWebService/services/GoComWebService?wsdl";
		url=Var.get("goComWebServiceUrl",url);//从变量中获取
		
		
		System.out.println("url:"+url);
		SoapObject so=null; 
		try {
			SoapObject rpc = new SoapObject(NAMESPACE,methodName);
			if(ArrValues!=null)
			{
				for(int i=0;i<ArrValues.length;i++)
				{
					rpc.addProperty("can"+i,ArrValues[i]);
				}
			}
			//定义发送数据的信封的封装格式
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut=rpc;
			envelope.dotNet = false;
			//发出请求 
			AndroidHttpTransport ht = new AndroidHttpTransport(url);
			ht.call("", envelope);	
			so= (SoapObject) envelope.bodyIn;
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			accessFlag = 1;
		}
		return so;
	}
	public static int returnFlag()
	{
		return accessFlag;
	}
}