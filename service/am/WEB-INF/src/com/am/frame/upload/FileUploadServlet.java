package com.am.frame.upload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.upload.fileProcess.FileProcessManager;
import com.am.frame.upload.fileProcess.UploadFileProcess;

/**
 *
 * @author 白斌
 *
 * @date 2014-10-18
 *
 */
public class FileUploadServlet extends HttpServlet 
{
	final Logger logger = LoggerFactory.getLogger(FileUploadServlet.class);

	private static final long serialVersionUID = -7744625344830285257L;
	private ServletContext sc;
	private String savePath;
	
	//文件前，后置处理器
	private UploadFileProcess beforeProcess,afterProcess;

	@Override
	public void init(ServletConfig config) 
	{
		// 在web.xml中设置的一个初始化参数
		savePath = config.getInitParameter("savePath");
		sc = config.getServletContext();
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException
	{
		
		request.setCharacterEncoding("UTF-8");
		//response.setContentType("text/html;charset=UTF-8");
		//response.setHeader("Cache-Control", "no-cache");
		//response.setContentLength(1000);
		response.setHeader("Access-Control-Allow-Origin", "*");
		String result=null;
		
		//参数对象集合
		Map<String,String> params=new HashMap<String, String>();
		
		Map<String,UploadDataItem> uploadData=new HashMap<String, UploadDataItem>();
		
		//构建输出对象
		PrintWriter out = new PrintWriter(response.getOutputStream());

		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		
		Enumeration ee=request.getHeaders("Content-Type");
		String fromData=null;
		
		while(ee.hasMoreElements()){
			
			fromData=ee.nextElement().toString();
			logger.info("Content-Type:"+fromData);
			break;
		}
		
		if(fromData==null||!fromData.contains("form-data")){
			logger.info("不是有效的form-data数据");
			return ;
		}
		
		try 
		{
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> itr = items.iterator();

			// 获得文件保存路径
			String tFilePath = getFolder(itr, sc, savePath);
			
			//迭代所有数据,保存集合
			itr = items.iterator();
			while (itr.hasNext()){
				FileItem item = itr.next();
				UploadDataItem dataItem=new UploadDataItem(item);
				
				if(UploadDataItem.STRING.equals(dataItem.getType())){
					uploadData.put(item.getFieldName(),dataItem);
					//logger.info("String:" +item.getFieldName()+"\n"+dataItem);
				}
				if(UploadDataItem.FILE.equals(dataItem.getType())){
					uploadData.put(dataItem.getFileName(),dataItem);
					//logger.info("FILE:" +item.getFieldName()+"\n"+dataItem);
				}
			}
			
			//处理文件集合
			//文件前置处理器，处理文件
			FileProcessManager fpm=new FileProcessManager();
			
			//执行前置处理器
			fpm.runBeforFileProcess(sc,uploadData,request,response);
			
			//保存文件
			JSONObject saveFileResults=fpm.saveFile(sc,savePath,uploadData,request,response);
			
			//执行后置处理器
			fpm.runAfterFileProcess(sc, uploadData, request, response);
			
			
//			while (itr.hasNext()) 
//			{
//				FileItem item = (FileItem) itr.next();
//				
//				if (item.isFormField()){
//					//非文件参数的处理
//					String pName=item.getFieldName();
//					String pValue=item.getString("UTF-8");
//					logger.info(" | " +pName+ " = "+pValue+ " | ");
//					params.put(pName, pValue);
//					if("savePath".equals(pName)){
//						//自定义保存路径
//						savePath=pValue;
//						tFilePath=sc.getRealPath(File.separator)+savePath;
//						logger.info("修改默认上传地址为："+tFilePath);
//						newFolder(tFilePath);
//					}
//					if("afterProcess".equals(pName)){
//						//文件前置处理器
//						afterProcess=(UploadFileProcess) Class.forName(pValue).newInstance();
//					}
//					if("beforeProcess".equals(pName)){
//						//文件后置处理器
//						beforeProcess=(UploadFileProcess) Class.forName(pValue).newInstance();
//					}
//				}else{
//					//文件处理
//					if (item.getName() != null && !item.getName().equals("")) 
//					{
//						logger.info("上传文件的大小:" + item.getSize());
//						logger.info("上传文件的类型:" + item.getContentType());
//
//						// item.getName()返回上传文件在客户端的完整路径名称
//						logger.info("上传文件的名称:" + item.getName());
//						
//						//上传的临时文件
//						String tempFileName=item.getName();
//						if(params.containsKey("fileName")){
//							tempFileName=params.get("fileName");
//						}
//						File tempFile = new File(tempFileName);
//						//前置文件处理器，处理临时文件
//						if(beforeProcess!=null){
//							result=beforeProcess.process(tempFile,sc,params,response,savePath);
//						}
//
//						// 上传文件的保存路径
//						File file = new File(tFilePath, tempFileName);
//						item.write(file);
//						
//						
//						//后置文件处理器，处理已经处理后的文件
//						if(afterProcess!=null){
//							result=afterProcess.process(file,sc,params,response,savePath);
//						}
//						
//						//如果结果为空，则执行默认的后置处理器
//						if(Checker.isEmpty(result)){
//							afterProcess=(UploadFileProcess) Class.forName("com.am.frame.upload.impl.DefaultAfterFileProcess").newInstance();
//							result=afterProcess.process(file,sc,params,response,savePath);
//						}
//						
//						
//						logger.info("上传文件成功！返回值:"+result);
//						
//						out.print(result);
//					} 
//					else 
//					{
//						logger.info("没有选择上传文件！");
//						
//						out.print(URLEncoder.encode(getJsonString("1","Not Select File Upload","",""),"UTF-8"));
//					}
//				}
//			}
			
			out.flush();
			out.close();
			
		} 
		catch (FileUploadException e) 
		{
			e.printStackTrace();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			logger.info("上传文件失败！");
		}
	}
	
	private String getJsonString(String code,String msg,String serverPath,String filename)
	{
		String rValue="{";
		
		rValue+=getJsonParam("CODE",code) + ",";
		rValue+=getJsonParam("MSG",msg) + ",";
		rValue+=getJsonParam("SERVERPATH",serverPath) + ",";
		rValue+=getJsonParam("FILENAME",filename);
		rValue+="}";
		
		logger.debug(" getJsonString() rValue=" + rValue);
		
		System.out.println(" getJsonString() rValue=" + rValue);
		
		return rValue;
	}
	
	private String getJsonParam(String name,String value)
	{
		return "\"" + name + "\":\"" + value + "\"";
	}

	
	private String getFolder(Iterator<FileItem> itr,ServletContext sc,String root) throws UnsupportedEncodingException
	{
		String rValue=sc.getRealPath("/");
		String tYear="";
		String tMonth="";
		String tDay="";
		String tTagName="";
		
		while (itr.hasNext()) 
		{
			FileItem item = itr.next();
			
			if (item.isFormField()) 
			{
				if(item.getFieldName().equals("year"))
					tYear=item.getString("UTF-8") + "";
				else if (item.getFieldName().equals("month"))
					tMonth=item.getString("UTF-8") + "";
				else if (item.getFieldName().equals("day"))
					tDay=item.getString("UTF-8") + "";
				else if (item.getFieldName().equals("tag_name"))
					tTagName=item.getString("UTF-8") + "";
			} 
		}
		
		//按照目录层级构建文件夹
		rValue += "" + root;
		newFolder(rValue);
		
		rValue += File.separatorChar + tYear;
		newFolder(rValue);
		rValue += File.separatorChar + tMonth;
		newFolder(rValue);
		rValue += File.separatorChar + tDay;
		newFolder(rValue);
		rValue += File.separatorChar + tTagName;
		newFolder(rValue);
		
		logger.debug(" | getFolder() return rValue= " + rValue + " | ");
		
		return rValue;
	}
	
	 /**
     * 新建目录
     *
     * @param folderPath
     *            String 如 c:/fqf
     * @return boolean
     */
    private void newFolder(String folderPath) 
    {
        try
        {
        	String tFolderPath=folderPath;
        	
        	logger.debug("文件路径[folderPath] = " + tFolderPath);
            
            java.io.File myFilePath = new java.io.File(tFolderPath);
            
            if (!myFilePath.exists()) 
            {
            	boolean isOK=myFilePath.mkdir();
            }
        } 
        catch (Exception e) 
        {
        	logger.debug("新建目录(" + folderPath + ")操作出错");
            e.printStackTrace();
        }
    }
}
