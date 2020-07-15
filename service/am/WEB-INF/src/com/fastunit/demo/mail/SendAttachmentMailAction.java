package com.fastunit.demo.mail;

import javax.mail.internet.MimeUtility;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Action;

public class SendAttachmentMailAction implements Action {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String host = ac.getRequestParameter("attachmentmail.host");
		String charset = ac.getRequestParameter("attachmentmail.charset");
		String username = ac.getRequestParameter("attachmentmail.username");
		String password = ac.getRequestParameter("attachmentmail.password");
		String to = ac.getRequestParameter("attachmentmail.to");
		String from = ac.getRequestParameter("attachmentmail.from");
		String fromName = ac.getRequestParameter("attachmentmail.fromname");
		String subject = ac.getRequestParameter("attachmentmail.subject");
		String msg = ac.getRequestParameter("attachmentmail.msg");

		HtmlEmail email = new HtmlEmail();
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

	private void attach(MultiPartEmail email, String path, String name)
			throws Exception {
		EmailAttachment attachment = new EmailAttachment();
		attachment.setPath(path); 
		attachment.setDisposition(EmailAttachment.ATTACHMENT);
		// 设置附件名称 防止乱码
		attachment.setName(MimeUtility.encodeText(name));
		// email.attach(null, "pom.xml", "pom");
		// email.attach(attachment);
	}
}
