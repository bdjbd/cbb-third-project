package com.am.cro.orderallot;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.member.util.DataToImgUI;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * 订单分配页面json转图片ui  
 * 用户从APP端上传的小视频、图片，在数据库中是以JSON格式保存的，需要从附件字段取出这些内容，在后台管理端显示出来（需要重新拼接页面格式）。
 * 若用户未上传附件，则不显示。
 * 
 * @author guorenjie
 *
 */
public class OrderAllotUI implements UnitInterceptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		// 获得单元数据的附件
		String attachment = unit.getData().getRow(0).get("attachment");
		String ticket_photo_path = unit.getData().getRow(0)
				.get("ticket_photo_path");
		
		// 判断附件如果不为空，则按类型显示，如果为空，则不显示，
		if (!Checker.isEmpty(attachment)) {
			if ("".equals(attachment) || "[]".equals(attachment)
					|| "null".equals(attachment)) {
				unit.removeElement("attachment");
			} else {
				// 根据类型格式化数据（img/video）
				JSONArray jsonArray = new JSONArray(attachment);
				String dataFormat = "";
				JSONObject jsonObject = null;
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObject = jsonArray.getJSONObject(i);
					if (jsonObject.getString("type").equals("video")) {
						dataFormat += "<video style='width:50%;height:auto;margin-left: 12px;margin-bottom: 10px;' controls='controls' preload='auto' src='"
								+ jsonObject.getString("source")
								+ "'"
								+ ">您的浏览器不支持播放此视频</video><br/>";
					} else {
						dataFormat += "<img style='width:50%;height:auto;margin-left: 12px;margin-bottom: 10px;' src='"
								+ jsonObject.getString("path")
								+ "'"
								+ "/><br/>";
					}

				}
				logger.info("附件数据经过处理===" + dataFormat);
				unit.getElement("attachment").setHtml(dataFormat);
			}
		}
		if(!unit.getTitle().equals("结算内容")){
			// 判断票据如果不为空，则按类型显示，如果为空，则不显示，
			if (!Checker.isEmpty(ticket_photo_path)) {
				if ("".equals(ticket_photo_path) || "[]".equals(ticket_photo_path)
						|| "null".equals(ticket_photo_path)) {
					unit.removeElement("ticket_photo_path");
				} else {
					DataToImgUI.getIstance().intercept(ac, unit, "ticket_photo_path", "50%", "auto");
				}

			}
		}
		
		return unit.write(ac);

	}

}
