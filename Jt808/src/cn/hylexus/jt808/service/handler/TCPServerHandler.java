package cn.hylexus.jt808.service.handler;

import cn.hylexus.jt808.vo.req.LocationInfoUploadMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import cn.hylexus.jt808.common.TPMSConsts;
import cn.hylexus.jt808.server.SessionManager;
import cn.hylexus.jt808.service.ChannelsMap;
import cn.hylexus.jt808.service.TerminalMsgProcessService;
import cn.hylexus.jt808.service.codec.MsgDecoder;
import cn.hylexus.jt808.util.HexStringUtils;
import cn.hylexus.jt808.util.HttpAsyncMap;
import cn.hylexus.jt808.util.JT808ProtocolUtils;
import cn.hylexus.jt808.vo.PackageData;
import cn.hylexus.jt808.vo.PackageData.MsgHeader;
import cn.hylexus.jt808.vo.Session;
import cn.hylexus.jt808.vo.req.TerminalAuthenticationMsg;
import cn.hylexus.jt808.vo.req.TerminalCommonMsg;
import cn.hylexus.jt808.vo.req.TerminalRegisterMsg;
import cn.hylexus.jt808.vo.req.TerminalUpgradeResultMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class TCPServerHandler extends ChannelInboundHandlerAdapter { // (1)

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final SessionManager sessionManager;
	private final MsgDecoder decoder;
	private TerminalMsgProcessService msgProcessService;
	private JT808ProtocolUtils jT808ProtocolUtils ;
	private HexStringUtils hexStringUtils;

	public TCPServerHandler() {
		this.sessionManager = SessionManager.getInstance();
		//解码类
		this.decoder = new MsgDecoder(); 
		//消息数据处理
		this.msgProcessService = new TerminalMsgProcessService();
		this.jT808ProtocolUtils = new JT808ProtocolUtils();
		this.hexStringUtils = new HexStringUtils();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception { // (2)
		try {
			ByteBuf buf = (ByteBuf) msg;
			if (buf.readableBytes() <= 0) {
				// ReferenceCountUtil.safeRelease(msg);
				return;
			}

			byte[] bs = new byte[buf.readableBytes()];
			buf.readBytes(bs);
			
			byte[] data = this.jT808ProtocolUtils.doEscape4Receive(bs, 0, bs.length-1);
			String a = hexStringUtils.toHexString(data);
			
			logger.debug(">>>>>>>>>>>>>>>终端上报十六进制协议<<<<<<<<<<<<<<<" + a);
			
			// 字节数据转换为针对于808消息结构的实体类
			PackageData pkg = this.decoder.bytes2PackageData(data);
			
			// 引用channel,以便回送数据给硬件
			pkg.setChannel(ctx.channel());
			//System.err.println("输出管道 = " + ctx.channel());
			
			//处理业务逻辑
			this.processPackageData(pkg);
			
		} finally {
			release(msg);
		}
	}
 
	/**
	 * 处理业务逻辑
	 * @param packageData
	 */
	private void processPackageData(PackageData packageData) {
		final MsgHeader header = packageData.getMsgHeader();

		// 1. 终端心跳-消息体为空 ==> 平台通用应答
		if (TPMSConsts.msg_id_terminal_heart_beat == header.getMsgId()) {
			logger.info(">>>>>[终端心跳],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			try {
				this.msgProcessService.processTerminalHeartBeatMsg(packageData);
				logger.info("<<<<<[终端心跳],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[终端心跳]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
						e.getMessage());
				e.printStackTrace();
			}
		}

		// 5. 终端鉴权 ==> 平台通用应答
		else if (TPMSConsts.msg_id_terminal_authentication == header.getMsgId()) {
			logger.info(">>>>>[终端鉴权],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			try {
				TerminalAuthenticationMsg authenticationMsg = new TerminalAuthenticationMsg(packageData);
				this.msgProcessService.processAuthMsg(authenticationMsg);
				logger.info("<<<<<[终端鉴权],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[终端鉴权]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
						e.getMessage());
				e.printStackTrace();
			}
		}
		// 6. 终端注册 ==> 终端注册应答
		else if (TPMSConsts.msg_id_terminal_register == header.getMsgId()) {
			logger.info(">>>>>[终端注册],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			try {
				//终端注册请求消息体设置
				TerminalRegisterMsg msg = this.decoder.toTerminalRegisterMsg(packageData);
				//消息数据处理
				this.msgProcessService.processRegisterMsg(msg);
				logger.info("<<<<<[终端注册],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[终端注册]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),	e.getMessage());
				e.printStackTrace();
			}
		}
		// 7. 终端注销(终端注销数据消息体为空) ==> 平台通用应答
		else if (TPMSConsts.msg_id_terminal_log_out == header.getMsgId()) {
			logger.info(">>>>>[终端注销],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			try {
				this.msgProcessService.processTerminalLogoutMsg(packageData);
				logger.info("<<<<<[终端注销],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[终端注销]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
						e.getMessage());
				e.printStackTrace();
			}
		}
		// 8. 位置信息汇报 ==> 平台通用应答
		else if (TPMSConsts.msg_id_terminal_location_info_upload == header.getMsgId()) {
			logger.info(">>>>>[位置信息],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			try {
				LocationInfoUploadMsg locationInfoUploadMsg = this.decoder.toLocationInfoUploadMsg(packageData);
				this.msgProcessService.processLocationInfoUploadMsg(locationInfoUploadMsg);
				logger.info("<<<<<[位置信息],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[位置信息]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
						e.getMessage());
				e.printStackTrace();
			}
		}
		
		// 9. 终端升级结果通知 0x0108
		else if (TPMSConsts.cmd_terminal_upgrade_result == header.getMsgId()) {
			logger.info(">>>>>[终端升级结果通知],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			try {
				//终端升级结果通知
				TerminalUpgradeResultMsg terminalUpgradeResultMsg = this.decoder.toTerminalUpgradeResultMsg(packageData);
				
				this.msgProcessService.processTerminalUpgradeResultMsg(terminalUpgradeResultMsg);
				logger.info("<<<<<[终端升级结果通知],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			} catch (Exception e) {
				logger.error("<<<<<[终端升级结果通知]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
						e.getMessage());
				e.printStackTrace();
			}
		}
		
		// 10、终端通用应答 0x0001
		else if (TPMSConsts.msg_id_terminal_common_resp == header.getMsgId()) {
			logger.info(">>>>>[终端通用应答],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
			try {
				//终端通用应答
				TerminalCommonMsg terminalCommonMsg = this.decoder.toTerminalCommandMsg(packageData);
				
				//远程升级终端通用应答0x8108
				if (terminalCommonMsg.getReplyId() == 0x8108) {
					logger.info("<<<<<[终端通用升级回复],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId(),terminalCommonMsg.getReplyId());
					this.msgProcessService.processUpgradeTerminalCommandtMsg(terminalCommonMsg);
				}
				//参数设置终端通用应答0x8103
				else if (terminalCommonMsg.getReplyId() == 0x8103) {
					logger.info("<<<<<[终端通用参数设置回复],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId(),terminalCommonMsg.getReplyId());
					this.msgProcessService.processParamSettingTerminalCommandtMsg(terminalCommonMsg);
				}else {
					logger.debug("<<<<<[终端通用应答错误],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId(),terminalCommonMsg.getReplyId());
				}
				
			} catch (Exception e) {
				logger.error("<<<<<[终端通用应答]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),e.getMessage());
				e.printStackTrace();
			}
		}
		
		// 其他情况
		else {
			logger.error(">>>>>>[未知消息类型],phone={},msgId={},package={}", header.getTerminalPhone(), header.getMsgId(),packageData);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		logger.error("发生异常:{}", cause.getMessage());
		cause.printStackTrace();
		ctx.close();
//		HttpAsyncMap.removeGatewayChannel(Session.buildId(ctx.channel()));	
		
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//构建session对象
		Session session = Session.buildSession(ctx.channel());
		//存进sessionManager
		sessionManager.put(session.getId(), session);
		
		//把连接存进ChannelsMap里(仅为主动发起协议)
		ChannelsMap.addGatewayChannel(session.getId(), (SocketChannel)ctx.channel());
		
		//把连接存进异步线程池
		HttpAsyncMap.addGatewayChannel(session.getId());
		
		logger.debug("终端连接:{}", session);
		System.out.println("新终端连接:" + session);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//获得连接id
		final String sessionId = ctx.channel().id().asLongText();
		//通过连接id获得session对象
		Session session = sessionManager.findBySessionId(sessionId);
		logger.debug("终端断开连接:{}", session);
		System.out.println("终端断开连接:" + session);
		
		//移除该连接
		this.sessionManager.removeBySessionId(sessionId);
		//移除连接(仅为主动发起协议)
		ChannelsMap.removeGatewayChannel(sessionId);
		//把连接从异步线程池移除
		HttpAsyncMap.removeGatewayChannel(sessionId);
		ctx.channel().close();
		// ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				Session session = this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
				logger.error("服务器主动断开连接:{}", session);
				System.out.println("服务器主动断开连接:" + session);
				
				//移除连接(仅为主动发起协议)
				ChannelsMap.removeGatewayChannel(Session.buildId(ctx.channel()));
				//把连接从异步线程池移除
				HttpAsyncMap.removeGatewayChannel(Session.buildId(ctx.channel()));
				ctx.close();
			}
		}
	}

	private void release(Object msg) {
		try {
			ReferenceCountUtil.release(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}