package com.am.frame.weichart.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @author YueBin
 * @create 2016年3月20日
 * @version 
 * 说明:<br />
 * 常用工具类
 */
public class Utils {
	
	public static String getRandomStr(int length){
		String str = "0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,i,j,k,l,m,"
				+ " n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,"
				+ "U,V,W,X,Y,Z";
		
        String str2[] = str.split(",");//将字符串以,分割  
        
        Random rand = new Random();//创建Random类的对象rand
        int index = 0;  
        String randStr = "";//创建内容为空字符串对象randStr  
        for (int i=0; i<length; ++i)  
        {  
            index = rand.nextInt(str2.length-1);//在0到str2.length-1生成一个伪随机数赋值给index  
            randStr += str2[index];//将对应索引的数组与randStr的变量值相连接  
        }
		return randStr;
	}
	
	public static String SHA1(String decript) {
		return DigestUtils.sha1Hex(decript);
	}
	
	  public static Map<String, String> sign(String jsapi_ticket, String url) {
	        Map<String, String> ret = new HashMap<String, String>();
	        String nonce_str = create_nonce_str();
	        String timestamp = createTimestamp();
	        String string1;
	        String signature = "";

	        //注意这里参数名必须全部小写，且必须有序
	        string1 = "jsapi_ticket=" + jsapi_ticket +
	                  "&noncestr=" + nonce_str +
	                  "&timestamp=" + timestamp +
	                  "&url=" + url;
	        System.out.println(string1);

	        try
	        {
	            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	            crypt.reset();
	            crypt.update(string1.getBytes("UTF-8"));
	            signature = byteToHex(crypt.digest());
	        }
	        catch (NoSuchAlgorithmException e)
	        {
	            e.printStackTrace();
	        }
	        catch (UnsupportedEncodingException e)
	        {
	            e.printStackTrace();
	        }

	        ret.put("url", url);
	        ret.put("jsapi_ticket", jsapi_ticket);
	        ret.put("nonceStr", nonce_str);
	        ret.put("timestamp", timestamp);
	        ret.put("signature", signature);

	        return ret;
	    }

	    private static String byteToHex(final byte[] hash) {
	        Formatter formatter = new Formatter();
	        for (byte b : hash)
	        {
	            formatter.format("%02x", b);
	        }
	        String result = formatter.toString();
	        formatter.close();
	        return result;
	    }

	    private static String create_nonce_str() {
	        return UUID.randomUUID().toString();
	    }

	    public static String createTimestamp() {
	        return Long.toString(System.currentTimeMillis() / 1000);
	    }
	    /** 
	     * xml to map xml <node><key label="key1">value1</key><key 
	     * label="key2">value2</key>......</node> 
	     *  
	     * @param xml 
	     * @return 
	     */
	    public static Map xmltoMap(String xml) {
	    	 Map map = new HashMap();  
	        try {  
	            Document document = DocumentHelper.parseText(xml);  
	            Element nodeElement = document.getRootElement();  
	            List node = nodeElement.elements();  
	            for (Iterator it = node.iterator(); it.hasNext();) {  
	                Element elm = (Element) it.next();  
	                map.put(elm.attributeValue("label"), elm.getText());  
	                elm = null;  
	            }  
	            node = null;  
	            nodeElement = null;  
	            document = null;  
	            
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return map;  
	    }  
	    
	    /** 
	     * map to xml xml <node><key label="key1">value1</key><key 
	     * label="key2">value2</key>......</node> 
	     *  
	     * @param map 
	     * @return 
	     */  
	    public static String maptoXml(Map map) {  
	        Document document = DocumentHelper.createDocument();  
	        Element nodeElement = document.addElement("node");  
	        for (Object obj : map.keySet()) {  
	            Element keyElement = nodeElement.addElement("key");  
	            keyElement.addAttribute("label", String.valueOf(obj));  
	            keyElement.setText(String.valueOf(map.get(obj)));  
	        }  
	        return doc2String(document);  
	    }
	    
	    /** 
	     *  
	     * @param document 
	     * @return 
	     */  
	    public static String doc2String(Document document) {  
	        String s = "";  
	        try {  
	            // 使用输出流来进行转化  
	            ByteArrayOutputStream out = new ByteArrayOutputStream();  
	            // 使用UTF-8编码  
	            OutputFormat format = new OutputFormat("   ", true, "UTF-8");  
	            XMLWriter writer = new XMLWriter(out, format);  
	            writer.write(document);  
	            s = out.toString("UTF-8");  
	        } catch (Exception ex) {  
	            ex.printStackTrace();  
	        }  
	        return s;  
	    }  
}
