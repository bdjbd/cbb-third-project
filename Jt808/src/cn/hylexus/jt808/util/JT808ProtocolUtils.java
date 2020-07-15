package cn.hylexus.jt808.util;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JT808协议转义工具类
 * 
 * <pre>
 * 0x7d01 <====> 0x7d
 * 0x7d02 <====> 0x7e
 * </pre>
 * 
 * @author hylexus
 *
 */
public class JT808ProtocolUtils {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private BitOperator bitOperator;
	private BCD8421Operater bcd8421Operater;

	public JT808ProtocolUtils() {
		this.bitOperator = new BitOperator();
		this.bcd8421Operater = new BCD8421Operater();
	}

	/**
	 * 接收消息时转义<br>
	 * 
	 * <pre>
	 * 0x7d01 <====> 0x7d
	 * 0x7d02 <====> 0x7e
	 * </pre>
	 * 
	 * @param bs
	 *            要转义的字节数组
	 * @param start
	 *            起始索引
	 * @param end
	 *            结束索引
	 * @return 转义后的字节数组
	 * @throws Exception
	 */
	public byte[] doEscape4Receive(byte[] bs, int start, int end) throws Exception {
		if (start < 0 || end > bs.length)
			throw new ArrayIndexOutOfBoundsException("doEscape4Receive error : index out of bounds(start=" + start
					+ ",end=" + end + ",bytes length=" + bs.length + ")");
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			for (int i = 0; i < start; i++) {
				baos.write(bs[i]);
			}
			for (int i = start; i < end - 1; i++) {
				if (bs[i] == 0x7d && bs[i + 1] == 0x01) {
					baos.write(0x7d);
					i++;
				} else if (bs[i] == 0x7d && bs[i + 1] == 0x02) {
					baos.write(0x7e);
					i++;
				} else {
					baos.write(bs[i]);
				}
			}
			for (int i = end - 1; i < bs.length; i++) {
				baos.write(bs[i]);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if (baos != null) {
				baos.close();
				baos = null;
			}
		}
	}

	/**
	 * 
	 * 发送消息时转义<br>
	 * 
	 * <pre>
	 *  0x7e <====> 0x7d02
	 * </pre>
	 * 
	 * @param bs
	 *            要转义的字节数组
	 * @param start
	 *            起始索引
	 * @param end
	 *            结束索引
	 * @return 转义后的字节数组
	 * @throws Exception
	 */
	public byte[] doEscape4Send(byte[] bs, int start, int end) throws Exception {
		log.debug(">>>>>>>>>>[转译]");
		if (start < 0 || end > bs.length)
			throw new ArrayIndexOutOfBoundsException("doEscape4Send error : index out of bounds(start=" + start
					+ ",end=" + end + ",bytes length=" + bs.length + ")");
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			for (int i = 0; i < start; i++) {
				baos.write(bs[i]);
			}
			for (int i = start; i < end; i++) {
				if (bs[i] == 0x7e) {
					log.debug("[0x7e转译]");
					baos.write(0x7d);
					baos.write(0x02);
				}else if (bs[i] == 0x7d) {
					log.debug("[0x7d转译]");
					baos.write(0x7d);
					baos.write(0x01);
				} 
				else {
					baos.write(bs[i]);
				}
			}
			for (int i = end; i < bs.length; i++) {
				baos.write(bs[i]);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if (baos != null) {
				baos.close();
				baos = null;
			}
		}
	}
	
	/**
	 * 消息体属性
	 * @param msgLen			消息体长度
	 * @param enctyptionType	加密类型
	 * @param isSubPackage		是否分包
	 * @param reversed_14_15	保留位	
	 * @return
	 */
	public int generateMsgBodyProps(int msgLen, int enctyptionType, boolean isSubPackage, int reversed_14_15) {
		
		// [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
		// [10-12] 0001,1100,0000,0000(1C00)(加密类型)
		// [ 13 ] 0010,0000,0000,0000(2000)(是否有子包)
		// [14-15] 1100,0000,0000,0000(C000)(保留位)
		
		//单包消息体长度不超过1024个字节
		if (msgLen >= 1024)
			log.warn("The max value of msgLen is 1023, but {} .", msgLen);
		//是否分包,true为1，false为0
		int subPkg = isSubPackage ? 1 : 0;
		
		//字节位数计算
		int ret = (msgLen & 0x3FF) | ((enctyptionType << 10) & 0x1C00) | ((subPkg << 13) & 0x2000)| ((reversed_14_15 << 14) & 0xC000);
		
//		消息体长度msglen = 6
//		加密类型enctyptionType = 0
//		是否分包subPkg = 0
//		保留位reversed_14_15 = 0
//		ret =6 
		
		return ret & 0xffff;
	}
	
	/**
	 * 消息头(无消息包封装项)
	 * @param phone				终端手机号
	 * @param msgType			消息ID
	 * @param body
	 * @param msgBodyProps		消息体属性
	 * @param flowId			消息流水号			
	 * @return
	 * @throws Exception
	 */
	public byte[] generateMsgHeader(String phone, int msgType, byte[] body, int msgBodyProps, int flowId)throws Exception {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			// 1. 消息ID word(16)		2byte
			baos.write(bitOperator.integerTo2Bytes(msgType));
			// 2. 消息体属性 word(16)		2byte
			baos.write(bitOperator.integerTo2Bytes(msgBodyProps));
			// 3. 终端手机号 bcd[6]		6byte
			baos.write(bcd8421Operater.string2Bcd(phone));
			// 4. 消息流水号 word(16),按发送顺序从 0 开始循环累加	2byte
			baos.write(bitOperator.integerTo2Bytes(flowId));
			// 5.消息包封装项 此处不予考虑
			return baos.toByteArray();
		} finally {
			if (baos != null) {
				baos.close();
			}
		}
	}
	
	
	/**
	 * 消息头(有消息包封装项)
	 * @param phone				终端手机号
	 * @param msgType			消息ID
	 * @param body
	 * @param msgBodyProps		消息体属性
	 * @param flowId			消息流水号
	 * @param totalPackages		总包数
	 * @param currentPackage	当前包号		
	 * @return
	 * @throws Exception
	 */
	public byte[] generateMsgHeader(String phone, int msgType, byte[] body, int msgBodyProps, int flowId , int totalPackages , int currentPackage)throws Exception {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			// 1. 消息ID word(16)		2byte
			baos.write(bitOperator.integerTo2Bytes(msgType));
			// 2. 消息体属性 word(16)		2byte
			baos.write(bitOperator.integerTo2Bytes(msgBodyProps));
			// 3. 终端手机号 bcd[6]		6byte
			baos.write(bcd8421Operater.string2Bcd(phone));
			// 4. 消息流水号 word(16),按发送顺序从 0 开始循环累加	2byte
			baos.write(bitOperator.integerTo2Bytes(flowId));
			// 5.消息包封装项
			baos.write(bitOperator.integerTo2Bytes(totalPackages));
			baos.write(bitOperator.integerTo2Bytes(currentPackage));
			return baos.toByteArray();
		} finally {
			if (baos != null) {
				baos.close();
			}
		}
	}
	
	
	
	
}
