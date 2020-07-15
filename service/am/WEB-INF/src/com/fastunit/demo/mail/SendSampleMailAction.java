package com.fastunit.demo.mail;

import org.apache.commons.mail.SimpleEmail;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Action;

public class SendSampleMailAction implements Action {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String host = ac.getRequestParameter("samplemail.host");
		String charset = ac.getRequestParameter("samplemail.charset");
		String username = ac.getRequestParameter("samplemail.username");
		String password = ac.getRequestParameter("samplemail.password");
		String to = ac.getRequestParameter("samplemail.to");
		String from = ac.getRequestParameter("samplemail.from");
		String fromName = ac.getRequestParameter("samplemail.fromname");
		String subject = ac.getRequestParameter("samplemail.subject");
		String msg = ac.getRequestParameter("samplemail.msg");

		SimpleEmail email = new SimpleEmail();
		email.setHostName(host);
		email.setAuthentication(username, password);
		// 必须放在setMsg()前面，否则乱码
		email.setCharset(charset);
		email.addTo(to);
		email.setFrom(from, fromName);
		email.setSubject(subject);
		email.setMsg(msg);
		email.send();

		return ac;
	}

}
