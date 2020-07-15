package com.wd.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PolicyServer implements Runnable {

	private final int policy_port = 843;
	private boolean status = true;

	private ServerSocket server = null;

	@Override
	public void run() {
		// 创建安全验证服务器
		server = SocketUtil.getServerSocket(policy_port);

		while (status) {
			Socket socket = SocketUtil.getSocket(server);
			new Thread(new PolicyThread(socket)).start();
		}
	}

	/**
	 * 启动服务器
	 */
	public void startPolicy() {
		new Thread(this).start();
	}

	/**
	 * 关闭服务器
	 */
	public void stopPolicy() {
		status = false;
		if (server != null && !server.isClosed()) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
