package com.am.frame.upload;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author YueBin
 * @create 2016年2月26日
 * @version 
 * 说明:<br />
 * 上传数据对象
 */
public class UploadDataItem{
	
	/**上传的值为文件**/
	public final static String FILE="UPLOAD_DATA_ITEM_FILE";
	/**上传的值为字符串**/
	public final static String STRING="UPLOAD_DATA_ITEM_STRING";
	
	/**文件保存路径**/
	public final static String SAVE_FILE_PATH_ROOT="savePath";
	/**文件保存路径规则**/
	public final static String FILE_PATH_RULE="FILE_PATH_RULE";
	/**文件保存文件的文件名**/
	public final static String FILE_NAME="fileName";
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	private String type;//String File
	private String key;
	private String value;
	private FileItem file;
	private String contentType;
	
	public UploadDataItem(){}
	
	
	public UploadDataItem(FileItem fItem){
		
		if(fItem==null)return ;
		
		contentType=fItem.getContentType();
		
		if (fItem.isFormField()){
			//非文件参数的处理
			String pName=fItem.getFieldName();
			String pValue="";
			try {
				pValue = fItem.getString("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			logger.info("--------------"+pName+"="+pValue);
			this.key=pName;
			this.value=pValue;
			this.type=STRING;
			
		}else{
			//文件处理
			if (fItem.getName() != null && !"".equals(fItem.getName())){
				logger.info("上传文件的大小:" + fItem.getSize()+"\t上传文件的名称:" + fItem.getName());
				// item.getName()返回上传文件在客户端的完整路径名称
				//上传的临时文件
				this.file=fItem;
				this.key=fItem.getName();
				this.type=FILE;
			}
		}
	
	}
	
	/**
	 * 将上传的文件写入到指定的path路径下，文件名为fileName
	 * @param path 文件路径
	 * @param fileName 文件名
	 * @return 文件的全路径名称
	 */
	public String writeFile(String path,String fileName){
		
		File savePath=new File(path);
		
		if(!savePath.exists()){
			savePath.mkdirs();
		}
		
		File svaeFile=new File(path+File.separator+fileName);
		
		try {
			file.write(svaeFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String filePath=savePath.getAbsoluteFile().getAbsolutePath();
		
		return filePath+File.separator+fileName;
	}
	
	/**
	 * 返回文件的大小，单位byte
	 * @return 文件大小
	 */
	public long getFileSize(){
		return file.getSize();
	}
	
	
	/**
	 * 获取保存前的文件名称
	 * @return 文件名
	 */
	public String getFileName(){
		return file.getName();
	}
	
	/**
	 * 将上传的文件保存到path路径下，文件名为上传的文件名。
	 * @param path 保存的文件路径
	 * @return  文件的全路径名称
	 */
	public String writeFile(String path){
		String fileName=file.getName();
		String filePath=writeFile(path,fileName);
		return filePath;
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public FileItem getFile() {
		return file;
	}

	public String getContentType() {
		return contentType;
	}
	
	
	@Override
	public String toString() {
		String outStr="TYPE:"+type+" value:"+value+" FILENAME:";
		return outStr;
	}
	
	
	
	
}
