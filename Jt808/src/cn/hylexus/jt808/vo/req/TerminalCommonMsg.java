package cn.hylexus.jt808.vo.req;

import cn.hylexus.jt808.vo.PackageData;

/**
 * 终端通用应答
 */
public class TerminalCommonMsg extends PackageData{

	// byte[0-1] 应答流水号 对应的终端消息的流水号
	private int replyFlowId;
	// byte[2-3] 应答ID 对应的终端消息的ID
	private int replyId;
	/**
	 * 0：成功∕确认<br>
	 * 1：失败<br>
	 * 2：消息有误<br>
	 * 3：不支持<br>
	 */
	private byte replyCode;

	public TerminalCommonMsg() {
	}

	public TerminalCommonMsg(PackageData packageData) {
		this();
		this.channel = packageData.getChannel();
		this.checkSum = packageData.getCheckSum();
		this.msgBodyBytes = packageData.getMsgBodyBytes();
		this.msgHeader = packageData.getMsgHeader();
	}

	public int getReplyFlowId() {
		return replyFlowId;
	}

	public void setReplyFlowId(int replyFlowId) {
		this.replyFlowId = replyFlowId;
	}

	public int getReplyId() {
		return replyId;
	}

	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}

	public byte getReplyCode() {
		return replyCode;
	}

	public void setReplyCode(Byte replyCode) {
		this.replyCode = replyCode;
	}

	@Override
	public String toString() {
		return "TerminalCommonMsg [replyFlowId=" + replyFlowId + ", replyId=" + replyId + ", replyCode=" + replyCode
				+ "]";
	}
}
