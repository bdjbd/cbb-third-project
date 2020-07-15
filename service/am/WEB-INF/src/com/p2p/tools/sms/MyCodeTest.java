package com.p2p.tools.sms;

import org.junit.Test;

public class MyCodeTest {
	
	@Test
	public void test1(){
		SmsResultManager4ZK sms = new SmsResultManager4ZK();
		sms.init("<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
					+ "<returnsms> "
					+ "	<returnstatus>Success</returnstatus> "
					+ "	<message>ok</message>"
					+ "	<remainpoint>54543</remainpoint> "
					+ "	<taskID>31357441</taskID> "
					+ "	<successCounts>1</successCounts>"
					+ "</returnsms>");
		System.out.println(sms.getValue("taskID"));
	}

}
