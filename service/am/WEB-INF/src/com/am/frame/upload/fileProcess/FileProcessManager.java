package com.am.frame.upload.fileProcess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.upload.UploadDataItem;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年2月26日
 * @version 
 * 说明:<br />
 * 文件处理器
 * 负责对上传文件配置的文件出路其的处理
 */
public class FileProcessManager {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	/**前置处理器，key**/
	public final static String BEFORE_PROCESS="beforeProcess";
	
	/**后置处理器，key**/
	public final static String AFTER_PROCESS="afterProcess";
	/**写入文件后的新增**/
	public final static String WRITE_FILE_INFO="WRITE_FILE_INFO";
	
	private Map<String,UploadDataItem> mUploadDatas;
	
	private Map<String,String> results=new HashMap<String, String>();
	
	/**
	 * 运行前置处理器
	 * @param scontext ServletContext
	 * @param uploadDatas Map<String,UploadDataItem>
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return
	 */
	public String runBeforFileProcess(ServletContext scontext,Map<String,UploadDataItem> uploadDatas,
			HttpServletRequest request, HttpServletResponse response){
		String result=runProcess( scontext,uploadDatas, request, response,BEFORE_PROCESS);
		return result;
	}
	
	/**
	 * 运行后置处理器
	 * @param scontext ServletContext
	 * @param uploadDatas Map<String,UploadDataItem> 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return
	 */
	public String runAfterFileProcess(ServletContext scontext,Map<String,UploadDataItem> uploadDatas,
			HttpServletRequest request, HttpServletResponse response){
		
		runProcess(scontext,uploadDatas, request, response,AFTER_PROCESS);
		
		return null;
		
	}
	
	
	private String runProcess(ServletContext scontext,Map<String,UploadDataItem> uploadDatas,
			HttpServletRequest request, HttpServletResponse response,String proceeKey){
		
		if(uploadDatas==null){logger.info("上传数据集合为空！");return "";}
		mUploadDatas=uploadDatas;
		
		List<UploadFileProcess>  fontProcess=getProcess(proceeKey);
		
		for(UploadFileProcess process:fontProcess){
			String procesResult=process.process(scontext, uploadDatas, request, response,this);
			
			if(!Checker.isEmpty(procesResult)){
				this.results.put(process.getClass().getName(), procesResult);
			}
		}
		
		return null;
	}
	

	/**
	 * 获取处理器
	 * @param processKey 获取处理器关键字
	 * @return 处理器列表
	 */
	public List<UploadFileProcess> getProcess(String processKey){
		
		List<UploadFileProcess> result=new ArrayList<UploadFileProcess>();
		
		if(mUploadDatas.containsKey(processKey)){
			//前置处理器字符串
			String process=mUploadDatas.get(processKey).getValue();
			
			if(process!=null){
				//查看是否包号字符“[”，如果包号则为数组，如果不包含，则为单个处理器。
				if(process.indexOf("[")>-1){
					try {
						JSONArray fontList=new JSONArray(process);
						
						for(int i=0;i<fontList.length();i++){
							String className=fontList.get(i).toString();
							if(className!=null&&!"".equals(className)){
								UploadFileProcess fileProcess = (UploadFileProcess)
										Class.forName(className).newInstance();
								result.add(fileProcess);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					try {
						if(process!=null&&!"".equals(process)){
							UploadFileProcess fileProcess = (UploadFileProcess)
									Class.forName(process).newInstance();
							result.add(fileProcess);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return result;
	}

	
	/***
	 * 保存文件
	 * @param sc ServletContext
	 * @param savePath 默认保存路径
	 * @param uploadData 上传数据集合
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 */
	public JSONObject saveFile(ServletContext sc, String savePath,
			Map<String, UploadDataItem> uploadData, HttpServletRequest request,
			HttpServletResponse response) {
		
		
		JSONObject result=new JSONObject();
		
		//文件保存跟路径
		String fileSavePath=savePath;
		
		if(uploadData.containsKey(UploadDataItem.SAVE_FILE_PATH_ROOT)){
			fileSavePath=uploadData.get(UploadDataItem.SAVE_FILE_PATH_ROOT).getValue();
		}
		
		//路径保存规则
//		String filePahtClass="com.am.frame.upload.fileProcess.impl.DefaultFilePathRule";
		String filePahtClass="com.am.frame.upload.fileProcess.impl.FSDefaultFileSavaPathRule";
		
		if(uploadData.containsKey(UploadDataItem.FILE_PATH_RULE)){
			filePahtClass=uploadData.get(UploadDataItem.FILE_PATH_RULE).getValue();
		}
		FilePathRule filePahtRule=null;
		try {
			filePahtRule=(FilePathRule)Class.forName(filePahtClass).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String rValue=sc.getRealPath("/");
		
		//保存文件的路径
		fileSavePath=rValue+fileSavePath+filePahtRule.getFilePath();
		
		for(String dataKey:uploadData.keySet()){
			UploadDataItem item=uploadData.get(dataKey);
			if(UploadDataItem.FILE.equals(item.getType())){
				//查询是否有保存地址，如果没有保存地址，则使用系统的地址
				String fileName=null;
				String realFileSavePath="";
				if(uploadData.containsKey(UploadDataItem.FILE_NAME)){
					fileName=uploadData.get(UploadDataItem.FILE_NAME).getValue();
					//参数有文件名，按照文件名保存，不适合多个文件上传
					realFileSavePath=item.writeFile(fileSavePath, fileName);
				}else{
					realFileSavePath=item.writeFile(fileSavePath);
				}
				
				//文件相对路径
				String relativeFilePath=realFileSavePath.replace(sc.getRealPath(File.separator),"");
				
				try {
					result.put("CODE",0);
					result.put("MSG","File Upload Sucessful");
					result.put("SERVERPATH",savePath);
					result.put("FILENAME",item.getFileName());
					result.put("FILESIZE",item.getFileSize());
					result.put("NET_FILE_PATH",relativeFilePath);
					
					if(uploadData.containsKey("mediaType")){
						result.put("MEDIATYPE",uploadData.get("mediaType"));
					}
					if(uploadData.containsKey("type")){
						result.put("TYPE",uploadData.get("type").getValue());
					}
					
					//修改为支持中文字符串
					response.getOutputStream().write(result.toString().getBytes("UTF-8"));//  .print(result.toString());
					this.results.put(WRITE_FILE_INFO, result.toString());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		logger.info(result.toString());
		
		return result;
	}

	public Map<String, String> getResults() {
		return results;
	}
	
	
}
