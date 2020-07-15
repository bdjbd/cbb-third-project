package cn.hylexus.jt808.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hylexus.jt808.service.codec.AttachedDecoder;
import cn.hylexus.jt808.util.BitOperator;
import cn.hylexus.jt808.vo.req.LocationInfoUploadMsg;
/**
 * @author 王成阳
 * 2018-3-5
 */
public class LocationInfoAttached {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 截取附加协议
	 * @param locationInfoUploadData	位置信息汇报消息
	 * @return AttachedAnalysis 		附加协议列表
	 */
	public List<LocationInfoUploadMsg> getLocationInfoAttachedMsg(byte[] locationInfoUploadData , LocationInfoUploadMsg locationInfoUploadMsg) {
		
		AttachedDecoder attachedDecoder = new AttachedDecoder();
		BitOperator bitOperator = new BitOperator();
		
		logger.debug("总数组 [" + locationInfoUploadData.length + "]= " + Arrays.toString(locationInfoUploadData));
		
		boolean flag = true;
		List<LocationInfoUploadMsg> locationInfoUploadMsgs = new ArrayList<LocationInfoUploadMsg>();
		
		//1、附加信息列表 = 位置信息汇报 - 位置基本信息
		int locationInfoAttachedLength = locationInfoUploadData.length - 28;
		byte[] locationInfoAttached = new byte[locationInfoAttachedLength];
		
		//byte数组截取(src：byte源数组,srcPos：截取源byte数组起始位置（0位置有效）,dest,：byte目的数组（截取后存放的数组）,destPos：截取后存放的数组起始位置（0位置有效）,length：截取的数据长度)
		System.arraycopy(locationInfoUploadData, 28, locationInfoAttached, 0, locationInfoAttachedLength);
		
		logger.debug("目标数组 ["+ locationInfoAttached.length +"] = " + Arrays.toString(locationInfoAttached));
		
		//位置信息汇报数组截取后剩余长度大于0,则继续截取
		while(flag){
			
			if (locationInfoAttached.length > 2) {
			
			//2、附加信息项
			byte[] attached = new byte[bitOperator.oneByteToInteger(locationInfoAttached[1])];
			
			//截取完到第几项
			int j = 1;

			//3、获得附加信息内容
			//i=2为附加信息内容开始位置;locationInfoAttached[1] + 2表示附加信息项的长度
			for (int i = 2; i < bitOperator.oneByteToInteger(locationInfoAttached[1])  + 2; i++) {
				attached[i-2] = locationInfoAttached[i];
				j = i;
			}
			
			//附加信息内容
			LocationInfoAttachedAnalysis locationInfoAttachedAnalysis = new LocationInfoAttachedAnalysis(locationInfoAttached[0],locationInfoAttached[1],attached);
//			System.err.println("输出id=" + locationInfoAttachedAnalysis.getId());
//			System.err.println("输出length=" + locationInfoAttachedAnalysis.getLength());
//			System.err.println("输出content=" + Arrays.toString(locationInfoAttachedAnalysis.getContent()));
			
			//附加协议解析
			locationInfoUploadMsg = attachedDecoder.toDecoder(locationInfoAttachedAnalysis,locationInfoUploadMsg);
			locationInfoUploadMsgs.add(locationInfoUploadMsg);
			
			//5、判断截取后剩余长度,如果大于0则继续截取;反之退出while循环
			
				//创建新数组承接截取后的长度
				byte [] newByte = new byte[locationInfoAttached.length-j-1];
				//byte数组截取(src：byte源数组,srcPos：截取源byte数组起始位置（0位置有效）,dest,：byte目的数组（截取后存放的数组）,destPos：截取后存放的数组起始位置（0位置有效）,length：截取的数据长度)
				System.arraycopy(locationInfoAttached, j+1, newByte, 0, locationInfoAttached.length-j-1);
				locationInfoAttached = newByte;
				System.err.println("数组 ["+locationInfoAttached.length+"]= " + Arrays.toString(locationInfoAttached));
			}else {
				flag = false;
			}
		}
		return locationInfoUploadMsgs;
	}
}
