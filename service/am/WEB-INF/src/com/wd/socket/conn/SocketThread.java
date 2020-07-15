package com.wd.socket.conn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wd.ICuai;

public class SocketThread implements Runnable {
	private Socket socket;
	private String encoding;
	private BufferedReader br;

	/**
	 * 构造器，获取当前socket客户端发送的消息
	 * */
	public SocketThread(Socket socket, String encoding) {
		// System.out.println("收到客户端发来的新消息了！！！！！");
		this.socket = socket;
		this.encoding = encoding;
		try {
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), encoding));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 与客户端交互代码
	 */
	@Override
	public void run() {
		// 处理从socket客户端直接发送的请求
		String content = null;
		try {
			if ((content = br.readLine()) != null) {
				// System.out.println("从socket客户端直接发送的请求:"+content);
				sendMessage(content);
			}
		} catch (IOException e) {
			System.out.println("读取数据异常了...");
			e.printStackTrace();
		}
	}

	private void sendMessage(String content) {
		if (content == null || content.equalsIgnoreCase("")) {
			return;
		}
		String params[] = content.split("`");
		JSONArray rs = doAction(params[0], params[1]);
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), encoding));
			if (params.length == 2)
			{
				// 普通数据
				bw.append(rs.toString() + ",\r\n");
				bw.flush();
			} 
			else if (params.length == 3)
			{
				//同步离线数据，数据量有可能会很大，所以要分批传输
				int len = rs.length();
				for (int i = 0; i < len; i++) 
				{
					try 
					{
						if(params[2].equalsIgnoreCase("createsql"))
						{
							//建表语句
							JSONArray js=rs.getJSONArray(i);
							bw.append(js.toString() + ",\r\n");
							bw.flush();
						}
						else if(params[2].equalsIgnoreCase("offlinedata"))
						{
							//数据
							JSONObject jo=rs.getJSONObject(i);
							bw.append(jo.toString() + ",\r\n");
							bw.flush();
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			//bw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过类名，参数调用Action方法
	 * 
	 * @return JSONArray jsonArray json类型参数
	 * @param String
	 *            classPath 全路径类名
	 * @param JSONArray
	 *            jsonArray json类型参数
	 */
	public JSONArray doAction(String classPath, String jsonStr) {
		JSONArray returnStr = null;
		if ((classPath != null) && (!classPath.trim().equalsIgnoreCase(""))) {
			try {
				@SuppressWarnings("rawtypes")
				// 创建反射类
				Class c = Class.forName(classPath);
				// 反射创建对象
				ICuai object = (ICuai) c.newInstance();
				JSONArray paramJsonArray = null;
				// 判断是否有参数
				if (jsonStr != null && !jsonStr.equals("null")
						&& jsonStr.length() != 0) {
					paramJsonArray = new JSONArray(jsonStr);
				}
				returnStr = object.doAction(paramJsonArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return returnStr;
	}
}
