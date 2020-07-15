package cn.hylexus.jt808.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 终端位置上报协议参数处理
 * @author 王成阳
 * 2018-3-12
 */
public class LocationInfoUploadUtils {

	/**
	 * 根据标志位设置对应的标志状态为1
	 * @param  num  标志位
	 * @param  type 类型(0为报警标志,1为状态)	 
	 * @return alarmFlag{alarmFlag_0:0,alarmFlag_1:0,......alarmFlag_31:0} 	报警标志{报警标志位_0:0,报警标志位_1:0,......alarmFlag_31:0}(共32个标志位，其中1=有报警，0=无报警；)
       }
	 */
	public String setFlagStatus(int num , int type)
	{
		//标志位数组
		int[] flag = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
		
		JSONObject jsonObject = new JSONObject();
		
		//循环数组,根据传来的报警标志更改对应的标志位为1
		try {
			
			//报警标志
			if (type == 0) {
				
				for (int i = 0; i < flag.length; i++) {
					
					if (flag[i] == num) {
						
						jsonObject.put("alarmFlag_"+i+"",1);
						
					}else{
						
						jsonObject.put("alarmFlag_"+i+"",0);
					}
				}
				
			//状态
			}else{
				
				for (int i = 0; i < flag.length; i++) {
					
					if (flag[i] == num) {
						
						jsonObject.put("status_"+i+"",1);
						
					}else{
						
						jsonObject.put("status_"+i+"",0);
					}
				}
			}
			
			return jsonObject.toString();
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * 构建API参数
	 * @param terminalId			终端Id
	 * @param alarmFlagJson			报警标志
	 * @param statusJson			状态
	 * @param location				实时位置
	 * @param appendInfo			附加实时信息
	 * @param ObdRealTimeDataRpt	OBD实时数据
	 * @param ObdOverDataRpt		OBD本次行驶结束数据
	 * @param TerminalVehicleRpt	本次行驶结束数据
	 * @return 
	 */
	public String getApiParam(String terminalId,JSONObject alarmFlagJson,JSONObject statusJson,JSONObject location,JSONObject appendInfo,JSONObject ObdRealTimeDataRpt,JSONObject ObdOverDataRpt,JSONObject TerminalVehicleRpt) {
		
		JSONObject apiParamJson = new JSONObject();
		
		try {
			
			apiParamJson.put("terminalId", terminalId);
			apiParamJson.put("alarmFlag", alarmFlagJson);
			apiParamJson.put("status", statusJson);
			apiParamJson.put("location", location);
			apiParamJson.put("appendInfo", appendInfo);
			apiParamJson.put("ObdRealTimeDataRpt", ObdRealTimeDataRpt);
			apiParamJson.put("ObdOverDataRpt", ObdOverDataRpt);
			apiParamJson.put("TerminalVehicleRpt", TerminalVehicleRpt);
			return apiParamJson.toString();
		
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据报警状态数组获得报警标志位
	 * @param warningFlagField	报警状态数组
	 * @return 报警标志位
	 * "alarmFlag": {"alarmFlag_30": 0,"alarmFlag_29": 0,"alarmFlag_28": 0,"alarmFlag_27": 0,"alarmFlag_26": 0,"alarmFlag_25": 0}
	 */
	public JSONObject getWarningFlag(byte[] warningFlagField) {
		
		JSONObject warningFlag = new JSONObject();
		
	  	int x=31;
	  	//循环标志位数组
	  	for(int i=0;i<warningFlagField.length;i++){
	  		//循环每一项byte
	  		//getBooleanArray把byte转8位bit
	  		for(int j=0;j<getBooleanArray(warningFlagField[i]).length;j++,x--){
	  			try {
					warningFlag.put("alarmFlag_"+x, getBooleanArray(warningFlagField[i])[j]);
				} catch (JSONException e) {
					e.printStackTrace();
				}
	  		}
	  	}
	  	System.out.println(warningFlag);
	  	
	    
		return warningFlag;
	}
	
	/**
	 * 根据状态数组获得标志位
	 * @param warningFlag	报警状态数组
	 * @return 状态标志位
	 * "status": {"status_17": 0,"status_16": 0,"status_15": 0,"status_14": 0,"status_19": 0,"status_18": 0}
	 */
	public JSONObject getStatus(byte[] statusField) {
		
		JSONObject status = new JSONObject();
		
	  	int x=31;
	  	for(int i=0;i<statusField.length;i++){
	  		for(int j=0;j<getBooleanArray(statusField[i]).length;j++,x--){
	  			try {
					status.put("status_"+x, getBooleanArray(statusField[i])[j]);
				} catch (JSONException e) {
					e.printStackTrace();
				}
	  		}
	  	}
		
		return status;
	}
	
	/**
	 * byte转byte
	 * @param b
	 * @return
	 */
    public byte[] getBooleanArray(byte b) {  
        byte[] array = new byte[8];  
        for (int i = 7; i >= 0; i--) {  
            array[i] = (byte)(b & 1);  
            b = (byte) (b >> 1);  
        }  
        return array;  
    } 
	
}
