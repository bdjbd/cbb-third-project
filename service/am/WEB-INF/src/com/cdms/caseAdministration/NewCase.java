package com.cdms.caseAdministration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.utils.GetAddressByLatLng;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class NewCase extends DefaultAction {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception 
	{
		// 发送请求，取得高德返回的json字符串
		String url="http://restapi.amap.com/v3/geocode/geo?key=a5bf76c78efebd788d3c59731012c1d5&address=陕西省西安市雁塔区神舟四路航创广场";
		String sLatLngJson = com.am.utils.HttpRequest.sendGet(url, "");
		logger.debug("DefaultAction.doAction().sLatLngJson=" + sLatLngJson);

		// 获取表格
		Table table = ac.getTable("cdms_case");
		db.save(table);

		TableRow tr = table.getRows().get(0);

		// 获得案件ID
		String id = tr.getValue("id");

		String sql = "select (select proname from province where a.proid=proid) as proname,"
				+ "(select cityname from city where a.cityid=cityid) as cityname,"
				+ "(select zonename from zone where a.zoneid=zoneid) as zonename," + "a.address "
				+ "from cdms_case a where a.id='" + id + "'";
		MapList mapList = db.query(sql);

		String proname = "";
		String cityname = "";
		String zonename = "";
		String address = "";
		if (!Checker.isEmpty(mapList)) {
			proname = mapList.getRow(0).get("proname");
			cityname = mapList.getRow(0).get("cityname");
			zonename = mapList.getRow(0).get("zonename");
			address = mapList.getRow(0).get("address");
		}

		GetAddressByLatLng ga = new GetAddressByLatLng();
		String myAddress = proname + cityname + zonename + address;
		logger.debug(myAddress);

		String lnglat = ga.getLngLat(myAddress);
		logger.debug(lnglat);

		if (!"解析失败".equals(lnglat)) {

			String[] mylnglat = lnglat.split(",");
			logger.debug(mylnglat.toString());
			String lng = mylnglat[0];
			String lat = mylnglat[1];

			System.out.println("=================================");

			System.out.println(lng);
			System.out.println(lat);

			String sqla = "update cdms_case set lng='" + lng + "',lat='" + lat + "' where id='" + id + "'";
			db.execute(sqla);
		}

	}
}
