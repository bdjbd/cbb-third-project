package com.baidu.yun.push;

import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;

public class TsetPush {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MT mt=new MT();
		try {
			mt.push("我推送不出去，我人品有问题吗！？ ");
		} catch (ChannelClientException e) {
			e.printStackTrace();
		} catch (ChannelServerException e) {
			e.printStackTrace();
		}
	}

}
