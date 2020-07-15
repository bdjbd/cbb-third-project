package com.fastunit.app;

import javax.servlet.http.HttpServletRequest;

import com.fastunit.Element;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.context.LocalContext;
import com.fastunit.support.UnitInterceptor;

/**
 * 标题单元UI，获取服务器端ip和端口
 */
public class TitleUI implements UnitInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		// TODO Auto-generated method stub
		HttpServletRequest reauest=LocalContext.getLocalContext().getHttpServletRequest();
		String serverIp=""+reauest.getServerName();
		String serverPort=""+reauest.getServerPort();
		 Element txt_ip=unit.getElement("ipaddres");
		txt_ip.setDefaultValue(serverIp);
		
		Element txt_port=unit.getElement("serverport");
		txt_port.setDefaultValue(serverPort);
		
		return unit.write(ac);
	}

}
