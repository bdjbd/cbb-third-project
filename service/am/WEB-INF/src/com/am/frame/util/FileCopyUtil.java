package com.am.frame.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * @author YueBin
 * @create 2016年7月25日
 * @version 
 * 说明:<br />
 */
public class FileCopyUtil {
	
	/**
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 * @throws Exception
	 */
	public long forJava(File sourceFile,File targetFile) throws Exception{
		  long time=new Date().getTime();
		  int length=2097152;
		  FileInputStream in=new FileInputStream(sourceFile);
		  FileOutputStream out=new FileOutputStream(targetFile);
		  byte[] buffer=new byte[length];
		  while(true){
		   int ins=in.read(buffer);
		   if(ins==-1){
		    in.close();
		    out.flush();
		    out.close();
		    return new Date().getTime()-time;
		   }else
		    out.write(buffer,0,ins);
		  }
		 }
	
	
}
