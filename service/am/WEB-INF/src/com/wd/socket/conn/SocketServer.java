package com.wd.socket.conn;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import com.wd.comp.Constant;
import com.wd.socket.SocketUtil;

public class SocketServer implements Runnable {
	private static boolean status = true;
	private static ServerSocket server = null;
	private final int PORT=10088;
	@Override
	public void run() {
		// 创建Socket服务器
		server = SocketUtil.getServerSocket(PORT);
		while (status) {
			// 获取客户端socket实例
			Socket socket = SocketUtil.getSocket(server);
			new Thread(new SocketThread(socket,
					Constant.MESSAGE_ENCODING)).start();
		}
	}

	/**
	 * 启动服务器
	 */
	public void startSocket() {
		new Thread(this).start();
	}

	/**
	 * 关闭服务器
	 */
	public void stopSocket() {
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
