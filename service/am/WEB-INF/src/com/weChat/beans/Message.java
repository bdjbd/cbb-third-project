package com.weChat.beans;

import java.util.Formatter;

import com.fastunit.MapList;

/**
 *   说明:
 *   消息类
 *   @creator	岳斌
 *   @create 	Nov 21, 2013 
 *   @version	$Id
 */
public class Message {
	/**事件消息**/
	public static final String MSG_TYPE_EVENT="event";
	/**文本消息**/
	public static final String MSG_TYPE_TEXT="text";
	/**图片消息**/
	public static final String MSG_TYPE_IMAGE="image";
	/**语音消息**/
	public static final String MSG_TYPE_VOICE="voice";
	/**视频消息**/
	public static final String MSG_TYPE_VIDEO="video";
	/**位置消息**/
	public static final String MSG_TYPE_LOCATION="location";
	/**链接消息**/
	public static final String MSG_TYPE_LINK="link";
	protected String msgId;
	protected String fromUserName;
	protected String toUserName;
	protected String msgType;
	protected String createTime;
	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "{'MSGID':'"+this.msgId+"','FROMUSERNAME':'"+this.fromUserName+"','TOUSERNAME':'"+
				this.toUserName+"','MSGTYPE':'"+this.msgType+"','CREATETIME':'"+this.createTime+"'}";
	}
	
	@Override
	public int hashCode() {
		int temp=17;
		return temp*(msgId.equals(null)?0:msgId.hashCode());
	}
	@Override
	public boolean equals(Object obj) {
		if(obj==null){
			return false;
		}
		if(obj==this){
			return true;
		}
		if(obj instanceof Message){
			Message msg=(Message)obj;
			return msg.getMsgId().equals(msgId);
		}
		return false;
	}
	
	public final class  MessageTemplet{
		/**
		 * 文本消息格式模板
		 * 格式：
		 * <code>
		 * <xml></code>
		 * 	<ToUserName><![CDATA[toUser]]></ToUserName>
		 * <FromUserName><![CDATA[fromUser]]></FromUserName>
		 * <CreateTime>12345678</CreateTime>
		 * <MsgType><![CDATA[text]]></MsgType>
		 * <Content><![CDATA[你好]]></Content>
		 * </xml>
		 * </code>
		 * 参数	是否必须	描述</br>
		 *	ToUserName	 是	 接收方帐号（收到的OpenID)</br>
		 *	FromUserName	 是	开发者微信号</br>
		 *	CreateTime	 是	 消息创建时间 （整型）</br>
		 *	MsgType	 是	 text</br>
		 *	Content	 是	 回复的消息内容（换行：在content中能够换行，微信客户端就支持换行显示）</br>
		 */
		public static final String TEXT_MSG="<xml>" +
				"<ToUserName><![CDATA[%s]]></ToUserName>" +
				"<FromUserName><![CDATA[%s]]></FromUserName>" +
				"<CreateTime>%s</CreateTime>" +
				"<MsgType><![CDATA[%s]]></MsgType>" +
				"<Content><![CDATA[%s]]></Content>" +
				"<FuncFlag>0</FuncFlag>" +
				"</xml>";
		
		/**
		 * 图片消息</br>
		 * ToUserName	 是	 接收方帐号（收到的OpenID）</br>
		 * FromUserName	 是	开发者微信号</br>
		 * CreateTime	 是	 消息创建时间 （整型）</br>
		 * MsgType	 是	 image</br>
		 * MediaId	 是	 通过上传多媒体文件，得到的id。</br>
		 */
		public static final String IMAGE_MSG="<xml>"+
					"<ToUserName><![CDATA[%s]]></ToUserName>"+
					"<FromUserName><![CDATA[%s]]></FromUserName>"+
					"<CreateTime>%s</CreateTime>"+
					"<MsgType><![CDATA[%s]]></MsgType>"+
					"<Image>"+
					"<MediaId><![CDATA[%s]]></MediaId>"+
					"</Image>"+
					"</xml>";
		/**
		 * 语音消息</br>
		 * ToUserName	 是	 接收方帐号（收到的OpenID）</br>
		 * FromUserName	 是	开发者微信号</br>
		 * CreateTime	 是	 消息创建时间戳 （整型）</br>
		 * MsgType	 是	 语音，voice</br>
		 * MediaId	 是	 通过上传多媒体文件，得到的id</br>
		 */
		public static final String VOICE_MSG="<xml>"+
				"<ToUserName><![CDATA[%s]]></ToUserName>"+
				"<FromUserName><![CDATA[%s]]></FromUserName>"+
				"<CreateTime>%s</CreateTime>"+
				"<MsgType><![CDATA[voice]]></MsgType>"+
				"<Voice>"+
				"<MediaId><![CDATA[%s]]></MediaId>"+
				"</Voice>"+
				"</xml>";
		/**
		 * 视频消息</br>
		 * ToUserName	 是	 接收方帐号（收到的OpenID）</br>
		 * FromUserName	 是	开发者微信号</br>
		 * CreateTime	 是	 消息创建时间 （整型）</br>
		 * MsgType	 是	 video</br>
		 * MediaId	 是	 通过上传多媒体文件，得到的id</br>
		 * ThumbMediaId	 是	 缩略图的媒体id，通过上传多媒体文件，得到的id</br>
		 */
		public static final String VIDEO_MSG="<xml>"+
				"<ToUserName><![CDATA[%s]]></ToUserName>"+
				"<FromUserName><![CDATA[%s]]></FromUserName>"+
				"<CreateTime>%s</CreateTime>"+
				"<MsgType><![CDATA[video]]></MsgType>"+
				"<Video>"+
				"<MediaId><![CDATA[%s]]></MediaId>"+
				"<ThumbMediaId><![CDATA[%s]]></ThumbMediaId>"+
				"</Video> "+
				"</xml>";
		/**
		 * 音乐消息</br>
		 * ToUserName	 是	 接收方帐号（收到的OpenID）</br>
		 * FromUserName	 是	开发者微信号</br>
		 * CreateTime	 是	 消息创建时间 （整型）</br>
		 * MsgType	 是	 music</br>
		 * Title	 否	 音乐标题</br>
		 * Description	 否	 音乐描述</br>
		 * MusicURL	 否	 音乐链接</br>
		 * HQMusicUrl	 否	 高质量音乐链接，WIFI环境优先使用该链接播放音乐</br>
		 * ThumbMediaId	 是	 缩略图的媒体id，通过上传多媒体文件，得到的id</br>
		 */
		public static final String MUSIC_MSG="<xml>"+
				"<ToUserName><![CDATA[%s]]></ToUserName>"+
				"<FromUserName><![CDATA[%s]]></FromUserName>"+
				"<CreateTime>%s</CreateTime>"+
				"<MsgType><![CDATA[music]]></MsgType>"+
				"<Music>"+
				"<Title><![CDATA[%s]]></Title>"+
				"<Description><![CDATA[%s]]></Description>"+
				"<MusicUrl><![CDATA[%s]]></MusicUrl>"+
				"<HQMusicUrl><![CDATA[%s]]></HQMusicUrl>"+
				"<ThumbMediaId><![CDATA[%s]]></ThumbMediaId>"+
				"</Music>"+
				"</xml>";
		/**
		 * 图文消息</br>
		 * ToUserName	 是	 接收方帐号（收到的OpenID）</br>
		 * FromUserName	 是	开发者微信号</br>
		 * CreateTime	 是	 消息创建时间 （整型）</br>
		 * MsgType	 是	 news</br>
		 * ArticleCount	 是	 图文消息个数，限制为10条以内</br>
		 * Articles	 是	 多条图文消息信息，默认第一个item为大图,注意，如果图文数超过10，则将会无响应</br>
		 * Title	 否	 图文消息标题</br>
		 * Description	 否	 图文消息描述</br>
		 * PicUrl	 否	 图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200</br>
		 * Url	 否	 点击图文消息跳转链接</br>
		 */
		public static final String NEWS_MSG="<xml>"+
				"<ToUserName><![CDATA[%s]]></ToUserName>"+
				"<FromUserName><![CDATA[%s]]></FromUserName>"+
				"<CreateTime>%s</CreateTime>"+
				"<MsgType><![CDATA[%s]]></MsgType>"+
				"<ArticleCount>%s</ArticleCount>"+
				"<Articles>"+
				"<item>"+
				"<Title><![CDATA[%s]]></Title>"+
				"<Description><![CDATA[%s]]></Description>"+
				"<PicUrl><![CDATA[%s]]></PicUrl>"+
				"<Url><![CDATA[%s]]></Url>"+
				"</item>"+
				"</Articles>"+
				"</xml> ";
		
	}
	
	/**
	 *文本消息 
	 * @author 岳斌
	 */
	public class TextMessage{
		public TextMessage(MapList msgMap){
			Formatter fmt=new Formatter();
			fmt.format(MessageTemplet.TEXT_MSG, "");
		}
	}
}
