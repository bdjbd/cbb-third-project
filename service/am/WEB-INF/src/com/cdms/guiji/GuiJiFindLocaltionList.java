package com.cdms.guiji;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class GuiJiFindLocaltionList implements IWebApiService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String rValue = "";
		String cph=request.getParameter("cph");
		String startime= request.getParameter("startime");
		String endtime=request.getParameter("endtime");
		String sql2="select device_sn_number from cdms_vehiclebasicinformation where license_plate_number='"+cph+"'";
		DBManager db=new DBManager();
		String sn=db.query(sql2).getRow(0).get("device_sn_number");
		System.out.println(sn);
		String sql1="select ROW_NUMBER () OVER (ORDER BY cl.positioning_time ASC) AS id,cl.driving_behavior_status,cl.alarm_status,cl.state_of_vehicle,cl.lng,cl.lat,cvb.license_plate_number,cl.location,cl.positioning_time,cl.speed,cl.current_mileage"
						+" from  cdms_vehiclebasicinformation cvb,cdms_vcd_"+sn+" cl "
                        + "where cvb.id=cl.car_id "
                        + " and cvb.license_plate_number='"+cph+"' "
                        + " and cl.positioning_time>='"+startime+"' "
                        + " and cl.positioning_time<'"+endtime+"' and isloc='1' order by cl.positioning_time ASC";
		JSONArray jsonArray = db.queryToJSON(sql1);
		//ҳ����ʾ��״̬=acc״̬+14�ֱ�����Ϣ
		if(jsonArray.length()>0){
			String acc = "";
			String alarm = "";
			for (int i = 0; i < jsonArray.length(); i++) {
				if(jsonArray.getJSONObject(i).has("STATE_OF_VEHICLE")){
					String status = jsonArray.getJSONObject(i).getString("STATE_OF_VEHICLE");
					if(!Checker.isEmpty(status)){
						status = status.replaceAll(",", "','");
						status = "'"+status+"'";
						acc = getACCState(db,status);
					}
					
				}
				if(jsonArray.getJSONObject(i).has("ALARM_STATUS")){
					String alarm1 = jsonArray.getJSONObject(i).getString("ALARM_STATUS");
					if(!Checker.isEmpty(alarm1)){
						alarm1 = alarm1.replaceAll(",", "','");
						alarm1 = "'"+alarm1+"'";
						alarm = getAlarmName(db, alarm1);
					}
					
				}
				jsonArray.getJSONObject(i).put("CAR_STATE", acc+" "+alarm);
			}
		}
		rValue = jsonArray.toString();
		
		return rValue;
	}
	/**
	 * ��ѯ�ֵ���еı�������
	 * @param db
	 * @param code	����ƴ�ӵĶ��������
	 * @return
	 */
	public String getAlarmName(DBManager db,String code){
	String rValue = "";
	String sql = "select fatname from cdms_faultalarmtype where alarm_code in ("+code+")";
	MapList mapList = db.query(sql);
	if(!Checker.isEmpty(mapList)){
		for (int i = 0; i < mapList.size(); i++) {
			rValue += mapList.getRow(i).get(0)+" ";
		}		
	}
	return rValue;
}

	/**
	 * ��ѯ״̬�ֵ���acc״̬����
	 * @param db
	 * @param code	����ƴ�ӵĶ��״̬��
	 * @return
	 */
	public String getACCState(DBManager db,String code){
		String rValue = "";
		String sql = "select statusname from cdms_statusdicr where id in ("+code+")";
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			//״̬��λ��ACC״̬
				rValue = mapList.getRow(0).get(0);
		}
		return rValue;
	}

}
