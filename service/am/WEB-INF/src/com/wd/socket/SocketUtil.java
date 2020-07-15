package com.wd.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketUtil {

	/**
	 * 创建ServerSocket
	 * 
	 * @param port
	 * @return
	 */
	@SuppressWarnings("unused")
	public static ServerSocket getServerSocket(int port) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			
		} catch (IOException e) {
			if (server != null && !server.isClosed()) {
				try {
					server.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return server;
	}

	/**
	 * 获取Socket
	 * 
	 * @param server
	 * @return
	 */
	@SuppressWarnings("unused")
	public static Socket getSocket(ServerSocket server) {
		Socket socket = null;
		try {
			socket = server.accept();
			return socket;
		} catch (IOException e) {
			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			throw new RuntimeException("创建Socket时发送异常", e);
		}
	}

}
