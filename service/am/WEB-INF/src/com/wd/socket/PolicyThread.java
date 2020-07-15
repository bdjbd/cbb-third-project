package com.wd.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PolicyThread implements Runnable {
	private final String policy_xml = "<policy-file-request/>";
	private final String cross_xml = "<?xml version=\"1.0\"?><cross-domain-policy><site-control permitted-cross-domain-policies=\"all\"/><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>\0";
	private Socket socket;

	public PolicyThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			// 接收并发送Flex安全验证请求
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter pw = new PrintWriter(socket.getOutputStream());
			char[] by = new char[22];
			br.read(by, 0, 22);
			String s = new String(by);
			if (s.equals(policy_xml)) {
				//System.out.println("接收policy-file-request认证");
				pw.print(cross_xml);
				pw.flush();
				br.close();
				pw.close();
				socket.close();
				//System.out.println("完成policy-file-request认证");
			}
		} catch (IOException e) {
			if (!socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			throw new RuntimeException("执行policy认证时发生异常", e);
		}
	}

}
