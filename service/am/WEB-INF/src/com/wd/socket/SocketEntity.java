package com.wd.socket;

/***********************************************************************
 * Module:  SocketEntity.java
 * Author:  丁照祥
 * Purpose: Defines the Class SocketEntity
 ***********************************************************************/

import java.net.Socket;

/**
 * Socket实体类 包含客户端的Socket实例和客户端登陆账号
 */
public class SocketEntity {
	/**
	 * Socket实例
	 */
	private Socket socket;
	/**
	 * 当前登录用户账号
	 **/
	private String userId = null;

	/**
	 * 构造器，创建SocketEntity实例对象
	 * 
	 * @param socket
	 *            Socket客户端实例
	 */
	public SocketEntity(Socket socket) {
		this.socket = socket;
	}

	/**
	 * 构造器，创建SocketEntity实例对象
	 * 
	 * @param socket
	 *            Socket客户端实例
	 * @param userid
	 *            当前登录人的账号
	 */
	public SocketEntity(Socket socket, String userid) {
		this.socket = socket;
		this.userId = userid;
	}

	/**
	 * 获取Socket实例
	 */
	public Socket getSocket() {
		return this.socket;
	}

	/**
	 * 获取当前登录用户账号
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * 设置Socket实例
	 * 
	 * @param socket
	 *            Socket实例
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	/**
	 * 设置当前登录用户id
	 * 
	 * @param userId
	 *            当前登录用户id
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
}