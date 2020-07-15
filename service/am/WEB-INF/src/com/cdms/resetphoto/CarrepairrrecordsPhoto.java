package com.cdms.resetphoto;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.member.util.DataToImgUI;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class CarrepairrrecordsPhoto implements UnitInterceptor {

	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		System.err.println(unit.getData());
		System.err.println(unit.getTitle());
		// 获得附件
		if (unit.getData() != null) {
			String bdp_repair_picture = unit.getData().getRow(0).get("bdp_repair_picture");
			log.info(bdp_repair_picture);
			String car_repair_picture = unit.getData().getRow(0).get("car_repair_picture");
			log.info(car_repair_picture);
			// 判断票据如果不为空，则按类型显示，如果为空，则不显示，
			if (!Checker.isEmpty(car_repair_picture)) {
				if ("".equals(car_repair_picture) || "[]".equals(car_repair_picture)
						|| "null".equals(car_repair_picture)) {
					unit.removeElement("car_repair_picture");
				} else {
					DataToImgUI.getIstance().intercept(ac, unit, "car_repair_picture", "50%", "auto");
				}

			}

		}
		return unit.write(ac);
	}

}
