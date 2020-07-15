package cn.hylexus.jt808.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.socket.SocketChannel;

/**
 * @author 王成阳
 * 2018-3-1
 */
public class ChannelsMap 
{ 
	private static Map<String, SocketChannel> map = new ConcurrentHashMap<>();
	
    public static void addGatewayChannel(String id, SocketChannel gateway_channel){
        map.put(id, gateway_channel);
    }
    
    public static Map<String, SocketChannel> getChannels(){
        return map;
    }

    public static SocketChannel getGatewayChannel(String id){
        return map.get(id);
    } 
    
    public static void removeGatewayChannel(String id){
        map.remove(id);
    }
}
