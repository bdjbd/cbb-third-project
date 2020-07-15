package cn.hylexus.jt808.vo;

import java.util.Arrays;

import cn.hylexus.jt808.util.BitOperator;

/**
 * 附加协议解析类
 * @author 王成阳
 * 2018-3-5
 */
public class LocationInfoAttachedAnalysis {
	
	 BitOperator bitOperator = new BitOperator();
	
	//附加信息ID
	private String id ;
	//附加信息长度
	private int length;
	//附加信息
	private byte[] content;
	
	public LocationInfoAttachedAnalysis(){
		
	}
	
	public LocationInfoAttachedAnalysis(byte id , byte length , byte[] content){
		this.id = bitOperator.bytesToHexString(id);
		this.length = bitOperator.oneByteToInteger (length);
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "LocationInfoAttachedAnalysis [bitOperator=" + bitOperator + ", id=" + id + ", length=" + length
				+ ", content=" + Arrays.toString(content) + "]";
	}
	
	
}
