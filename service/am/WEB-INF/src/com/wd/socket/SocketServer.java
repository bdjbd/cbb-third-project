package com.wd.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.wd.comp.Constant;

public class SocketServer implements Runnable {
	private boolean status = true;
	private static ServerSocket server = null;
	// socket实体类集合，保存所有建立连接的客户端socket实例
	public static List<SocketEntity> socket_list = new ArrayList<SocketEntity>();

	@Override
	public void run() {
		// 创建Socket服务器
		server = SocketUtil.getServerSocket(Constant.MESSAGE_PORT);
		while (status) {
			// 获取客户端socket实例
			Socket socket = SocketUtil.getSocket(server);
			SocketEntity currentSocketEntity = new SocketEntity(socket);
			socket_list.add(currentSocketEntity);
			new Thread(new SocketThread(currentSocketEntity,
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

	/**
	 * 通知推送
	 * 
	 * @param receives接收者
	 * */
	public void NotifyPush(String receives) {
		// 给接收者添加前缀标识
		receives = Constant.MESSAGE_RECEIVER_MARK + receives;
		new Thread(new SocketThread(receives, Constant.MESSAGE_ENCODING))
				.start();
	}
}
