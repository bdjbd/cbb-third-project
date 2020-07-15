package com.fastunit.app.office;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import com.fastunit.Element;
import com.fastunit.Path;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.context.LocalContext;
import com.fastunit.support.UnitInterceptor;

public class EditOfficeUI implements UnitInterceptor {
	private static final long serialVersionUID = 9085861458836577536L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		// TODO Auto-generated method stub
		HttpServletRequest request=LocalContext.getLocalContext().getHttpServletRequest();
		int port=request.getServerPort();
		String ip=InetAddress.getLocalHost().getHostAddress();
		Element serveraddres= unit.getElement("serveraddres");
		serveraddres.setDefaultValue(ip+":"+port);
		
		Element savePath=unit.getElement("savapath");
		savePath.setDefaultValue(Path.getRootPath());
		return unit.write(ac);
	}
}
