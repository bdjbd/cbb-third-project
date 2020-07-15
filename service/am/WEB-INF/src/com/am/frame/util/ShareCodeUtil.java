package com.am.frame.util;

import java.util.Random;

/**
 * @author YueBin
 * @create 2016年3月16日
 * @version 
 * 说明:<br />
 * 邀请码生成器
 */
public class ShareCodeUtil {

	 /** 自定义进制 */
	private static final char[] r=new char[]{
		'3','B','a','R','X','b','M','c','4','e','f','h','g','i','k','L','m','9','n','q','t',
		'u','v','w','z','1','5','I','6','Z','V','F','8','A','D','N','Y','p','s','r','#','-',
		'y','2','x','E','H','d','T','Q','7','G','J','C','W','U','0','P','K','l','O','j'};
 
    /** (不能与自定义进制有重复) */
    private static final char b='o';
 
    /** 进制长度 */
    private static final int binLen=r.length;
 
    /** 序列最小长度 */
    private static final int s=6;
 
    /**
     * 根据ID生成六位随机码
     * @param id ID
     * @return 随机码
     */
    public static synchronized String toSerialCode(long id) {
        char[] buf=new char[32];
        int charPos=32;
        
        while((id / binLen) > 0) {
            int ind=(int)(id % binLen);
            // System.out.println(num + "-->" + ind);
            buf[--charPos]=r[ind];
            id /= binLen;
        }
        buf[--charPos]=r[(int)(id % binLen)];
        // System.out.println(num + "-->" + num % binLen);
        String str=new String(buf, charPos, (32 - charPos));
        // 不够长度的自动随机补全
        if(str.length() < s) {
            StringBuilder sb=new StringBuilder();
            sb.append(b);
            Random rnd=new Random();
            for(int i=1; i < s - str.length(); i++) {
            sb.append(r[rnd.nextInt(binLen)]);
            }
            str+=sb.toString();
        }
        return str;
    }
 
    public static long codeToId(String code) {
        char chs[]=code.toCharArray();
        long res=0L;
        for(int i=0; i < chs.length; i++) {
            int ind=0;
            for(int j=0; j < binLen; j++) {
                if(chs[i] == r[j]) {
                    ind=j;
                    break;
                }
            }
            if(chs[i] == b) {
                break;
            }
            if(i > 0) {
                res=res * binLen + ind;
            } else {
                res=ind;
            }
        }
        return res;
    }
    
    
    public static void main(String[] args) {
    	long time=System.currentTimeMillis();
    	System.out.println(time);
    	String invCode=toSerialCode(time);
    	System.out.println(invCode);
    	
    	long invCodeValue=codeToId(invCode);
    	System.out.println(invCodeValue);
	}
    
   
	
}
