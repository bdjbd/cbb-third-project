package com.wd.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.wd.comp.Constant;

public class SocketThread implements Runnable {
	private SocketEntity socket;
	private String encoding;
	private BufferedReader br;
	private String receives = null;

	/**
	 * 构造器，获取当前socket客户端发送的消息
	 * */
	public SocketThread(SocketEntity socket, String encoding) {
		// System.out.println("收到客户端发来的新消息了！！！！！");
		this.socket = socket;
		this.encoding = encoding;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getSocket()
					.getInputStream(), encoding));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构造器，有新消息需要推送时，用该构造器实例化该对象。
	 * */
	public SocketThread(String receives, String encoding) {
		this.receives = receives;
		this.encoding = encoding;
	}

	/**
	 * 与客户端交互代码
	 */
	@Override
	public void run() {
		// 如果接收者不为空，说明是由消息操作类直接通知过来的。直接根据接收者推送。
		if (this.receives != null && !this.receives.equalsIgnoreCase("")) {
			// System.out.println("消息操作类直接通知过来的:"+this.receives);
			sendMessage(this.receives);
		} else {
			// 处理从socket客户端直接发送的请求
			String content = null;
			try {
				while ((content = br.readLine()) != null) {
					// System.out.println("从socket客户端直接发送的请求:"+content);
					sendMessage(content);
				}
			} catch (IOException e) {
				System.out.println("读取数据异常了...");
				e.printStackTrace();
			}
		}
	}

	private void sendMessage(String content) {
		if (content == null || content.equalsIgnoreCase("")) {
			return;
		}
		// 如果是以@开头的内容，表示本次内容是客户端的用户信息
		else if (content.startsWith(Constant.MESSAGE_USERID_MARK)) {
			content = content.substring(1, content.length());
			this.socket.setUserId(content);
		}
		// 如果是以#开头的内容，表示本次内容是新消息的接收者，多个接收者之间用英文逗号隔开
		else if (content.startsWith(Constant.MESSAGE_RECEIVER_MARK)) {
			// 去掉#
			content = content.substring(1, content.length());
			// 给所有在线的socket实例，推送消息
			for (SocketEntity s : SocketServer.socket_list) {
				// 如果该客户端已经断开，从socket列表里移除该实例。
				if (!s.getSocket().isConnected() || s.getSocket().isClosed()) {
					SocketServer.socket_list.remove(s);
					continue;
				}

				BufferedWriter bw;
				try {
					bw = new BufferedWriter(new OutputStreamWriter(s
							.getSocket().getOutputStream(), encoding));
					bw.append(content + ",\r\n");
					bw.flush();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
