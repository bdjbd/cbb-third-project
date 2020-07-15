package com.wisdeem.wwd.WeChat;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

import com.wisdeem.wwd.WeChat.beans.AccessToken;

/**
 *   说明:
 * 	 accesstoken 缓存
 *   @creator	岳斌
 *   @create 	Dec 10, 2013 
 *   @version	$Id
 */
class AccessTokenCache{
	private static AccessTokenCache cache;
	private Hashtable<String,AccessTokenRef> accessTokenRefs;
	private ReferenceQueue<AccessToken> q;
	/**
	 * 获取AccessTokenCache实例
	 * @return AccessTokenCache
	 */
	public static AccessTokenCache getInstAccessTokenCache(){
		if(cache!=null){
			return cache;
		}
		return new AccessTokenCache();
	}
	
	/**
	 * 构造器
	 */
	private AccessTokenCache(){
		accessTokenRefs=new Hashtable<String, AccessTokenRef>();
		q=new ReferenceQueue<AccessToken>();
	}
	
	/**
	 * 添加一个accesstoken到accesstoke缓冲区
	 * @param acToken accesstoek的Beans对象
	 */
	public void cacheAccessToken(AccessToken acToken){
		cleanCache();
		AccessTokenRef ref=new AccessTokenRef(acToken, q);
		accessTokenRefs.put(acToken.getToken(),ref);
	}
	
	/**
	 * 获取accessstoken字符串对象根据token
	 * @param token token
	 * @return accesstoken
	 */
	public AccessToken getAccessToken(String token){
		AccessToken acToken=null;
		if(accessTokenRefs.containsKey(token)){
			AccessTokenRef ref=accessTokenRefs.get(token);
			acToken=ref.get();
		}
		return acToken;
	} 
	
	/**
	 * 清除无效的token
	 */
	private void cleanCache(){
		AccessTokenRef ref=null;
		while((ref=(AccessTokenRef)q.poll())!=null){
			accessTokenRefs.remove(ref.get().getToken());
		}
	}
	
	/**
	 * 清除缓冲区
	 */
	public void clearCache(){
		cleanCache();
		accessTokenRefs.clear();
		System.gc();
		System.runFinalization();
	}
	/**
	 * 移除token对应accesstoken
	 * @param token
	 */
	public void removeTonken(String token){
		accessTokenRefs.remove(token);
	}
}
/**
 * AccessToken的软引用对象
 * @author 岳斌
 * @date 2013-12-10
 */
class AccessTokenRef extends SoftReference<AccessToken>{
	private String key="";
	public AccessTokenRef(AccessToken acToken,
			ReferenceQueue<? super AccessToken> q) {
		super(acToken, q);
		key=acToken.getToken();
	}
	public String getKey(){
		return key;
	}
}
