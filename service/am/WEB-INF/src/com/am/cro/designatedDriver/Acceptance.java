package com.am.cro.designatedDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.member.util.DataToImgUI;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * 代驾费用管理:详情票据图片显示
 * @author 王成阳
 * 2017-9-11
 */
public class Acceptance implements UnitInterceptor {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		logger.debug("进入详情票据图片显示");
		//获得单元数据
		String ticket_photo_path = unit.getData().getRow(0).get("ticket_photo_path");
		logger.debug("输出票据路径");
		String attachment = unit.getData().getRow(0).get("attachment");
		logger.debug("输出附件路径");
		//如果票据不为空则显示票据图片
		if (!Checker.isEmpty(ticket_photo_path)) {
			if ("".equals(ticket_photo_path)||"[]".equals(ticket_photo_path)||"null".equals(ticket_photo_path)){
				unit.removeElement("ticket_photo_path");
			} else {
				DataToImgUI.getIstance().intercept(ac, unit, "ticket_photo_path","14%", "auto");
			}
		//否则则显示无票据	
		}else{
			attachment = "无票据";
		}
		//如果附件不为空则显示附件图片
		if (!Checker.isEmpty(attachment)) {
			if ("".equals(attachment)||"[]".equals(attachment)||"null".equals(attachment)){
				unit.removeElement("attachment");
			} else {
				DataToImgUI.getIstance().intercept(ac, unit, "attachment","14%", "auto");
			}
		//否则显示无附件	
		}else{
			attachment = "无附件";
		}
		return unit.write(ac);
	}
}
















