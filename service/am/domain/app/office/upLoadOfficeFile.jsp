<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="java.io.*,java.sql.*,oracle.jdbc.*,java.util.*,com.fastunit.Path" %>
<%@ page language="java" import="org.apache.commons.fileupload.*,org.apache.commons.fileupload.disk.*,org.apache.commons.fileupload.servlet.*,java.net.URLDecoder"%>
<%!

public FileItem officeFileItem =null ;
public FileItem attachFileItem =null ;
public String officeFilePath ="";
public String attachFilePath ="" ;
public String officefileNameDisk = "";
public String attachFileNameDisk ="";

public String tempFileDir = "D:\\tempFile\\" ;            //临时文件目录
	public String absoluteOfficeFileDir = "D:\\work\\wd_ylsh_new\\fastunit\\files\\";   //office文档保存绝对路径
	public String relativeOfficeFileUrl = "files/" ;  //office文档相对目录

public boolean saveFileToDisk()
{
	File officeFileUpload = null;
	File attachFileUpload = null;
	String officeFileUploadPath ="";
	String attachFileUploadPath ="";
	boolean result=true ;
	try
	{
		if(!"".equalsIgnoreCase(officefileNameDisk)&&officeFileItem!=null)
		{
			officeFileUpload =  new File(officefileNameDisk);
			officeFileItem.write(officeFileUpload);
		}
	}
	catch(FileNotFoundException e){}
	catch(Exception e)
	{
		System.out.println("error saveFileToDisk:"+e.getMessage());
		e.printStackTrace();
		result=false;
	}
	return result;	
}

public void outString(String msg)
{
	//System.out.println(msg);
}
%>
<%	
	outString("aaaaaaaaa"+Path.getRootPath());
	String path=request.getParameter("path");
	path = java.net.URLDecoder.decode(path,"utf-8");
	outString("49行 "+path);
	long fileSize = 0;
	String fileName = "" ;
	String otherData ="";
	String fileType ="";
	String result = "";
	boolean isNewRecode = true ;
	officeFileItem =null ;
	DiskFileItemFactory factory = new DiskFileItemFactory();
	outString("86行");
	// 设置最多只允许在内存中存储的数据,单位:字节
	factory.setSizeThreshold(4096);
	// 设置一旦文件大小超过setSizeThreshold()的值时数据存放在硬盘的目录
	outString("90行");
	tempFileDir=Path.getRootPath()+"\\tempFile";
	factory.setRepository(new File(tempFileDir));
	ServletFileUpload upload = new ServletFileUpload(factory);
	outString("93行");
	//设置允许用户上传文件大小,单位:字节
	upload.setSizeMax(1024*1024*40);
	List fileItems = null;
	outString("97行");
	try{
		fileItems=upload.parseRequest(request);
		outString("100行");
	}
	catch(FileUploadException e)
	{
		outString("104行");
		out.println("上传文件超出最大值!");
		out.println(e.getMessage());
		e.printStackTrace();
		return;
	}
	Iterator iter = fileItems.iterator();
	attachFileItem=null;
	outString("112行");
	while (iter.hasNext()) 
	{
		FileItem item = (FileItem) iter.next();
		//打印提交的文本域和文件域名称
		if(item.isFormField())
		{
			if(item.getFieldName().equalsIgnoreCase("fileName"))
			{
			fileName = item.getName().substring(
			     item.getName().lastIndexOf("\\") + 1);

				//fileName=item.getString("utf-8").trim();
				outString("9800行:"+fileName);
			}
			if(item.getFieldName().equalsIgnoreCase("otherData"))
			{
				otherData=item.getString("utf-8").trim();
				otherData=otherData.equalsIgnoreCase("")?"请输入附加数据":otherData;
			}
					
			if(item.getFieldName().equalsIgnoreCase("fileType"))
			{
				fileType=item.getString("utf-8").trim();
			}	
		}else
		{
			if(item.getFieldName().equalsIgnoreCase("EDITFILE"))
			{
			officeFileItem=item;
			fileName = item.getName().substring(
					 item.getName().lastIndexOf("\\") + 1);
			}
		}
	}
	outString("159行");	
	fileName=URLDecoder.decode(fileName,"utf-8");
	outString("162行 fileName="+fileName);
	if(!"".equalsIgnoreCase(fileName)&&officeFileItem!=null)
	{
		fileSize = officeFileItem.getSize();
		//保存到磁盘中的文档
		officefileNameDisk = fileName;
		if(saveFileToDisk())
		{
			outString("183行--保存");
		}
		else
		{
			outString("187行");
			result="save file failed,please check upload file size,the max size is 4M";
		}
	}
	else{result="wrong information";}
	out.println(result);
%>