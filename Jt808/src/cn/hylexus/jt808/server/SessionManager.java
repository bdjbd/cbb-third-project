package cn.hylexus.jt808.server;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import cn.hylexus.jt808.vo.Session;

public class SessionManager { 
	
	public static String ServerID="";
	
	public static String SerialNumber = "";
	
	public static CloseableHttpAsyncClient httpclient = null;
	
	private static volatile SessionManager instance = null;
	// netty生成的sessionID和Session的对应关系
	private Map<String, Session> sessionIdMap;
	// 终端手机号和netty生成的sessionID的对应关系
	private Map<String, String> phoneMap;

	public static SessionManager getInstance() {
		if (instance == null) {
			synchronized (SessionManager.class) {
				if (instance == null) {
					instance = new SessionManager();
				}
			}
		}
		return instance;
	}

	public SessionManager() {
		this.sessionIdMap = new ConcurrentHashMap<>();
		this.phoneMap = new ConcurrentHashMap<>();
	}

	public boolean containsKey(String sessionId) {
		return sessionIdMap.containsKey(sessionId);
	}

	public boolean containsSession(Session session) {
		return sessionIdMap.containsValue(session);
	}

	public Session findBySessionId(String id) {
		return sessionIdMap.get(id);
	}

	public Session findByTerminalPhone(String phone) {
		String sessionId = this.phoneMap.get(phone);
		if (sessionId == null)
			return null;
		return this.findBySessionId(sessionId);
	}
	
	//获得所有管道
	public Map<String, Session> getChannels(){
		return sessionIdMap;
	}
	
	/**
	 * 终端连接
	 */
	public synchronized Session put(String key, Session value) {
		//终端id不为空
		if (value.getTerminalPhone() != null && !"".equals(value.getTerminalPhone().trim())) {
			//终端id作为key，连接id作为value存进phoneMap
			this.phoneMap.put(value.getTerminalPhone(), value.getId());
		}
		//终端为空则把连接id作为key，session作为value存进sessionIdMap
		return sessionIdMap.put(key, value);
	}
	
	/**
	 * 断开连接
	 */
	public synchronized Session removeBySessionId(String sessionId) {
		if (sessionId == null)
			return null;
		Session session = sessionIdMap.remove(sessionId);
		if (session == null)
			return null;
		if (session.getTerminalPhone() != null)
			this.phoneMap.remove(session.getTerminalPhone());
		return session;
	}

	
	
	// public synchronized void remove(String sessionId) {
	// if (sessionId == null)
	// return;
	// Session session = sessionIdMap.remove(sessionId);
	// if (session == null)
	// return;
	// if (session.getTerminalPhone() != null)
	// this.phoneMap.remove(session.getTerminalPhone());
	// try {
	// if (session.getChannel() != null) {
	// if (session.getChannel().isActive() || session.getChannel().isOpen()) {
	// session.getChannel().close();
	// }
	// session = null;
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public Set<String> keySet() {
		return sessionIdMap.keySet();
	}

	public void forEach(BiConsumer<? super String, ? super Session> action) {
		sessionIdMap.forEach(action);
	}

	public Set<Entry<String, Session>> entrySet() {
		return sessionIdMap.entrySet();
	}

	public List<Session> toList() {
		return this.sessionIdMap.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
	}

}