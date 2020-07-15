package com.wd.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Path;
import com.fastunit.Row;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.DateUtil;
import com.lowagie.text.pdf.codec.Base64;
import com.wd.database.DataBaseFactory;

public class CommonUtil {

	/**
	 * 将file转换成字节流
	 * */
	public static byte[] getBytesFromFile(File file) throws IOException {
		byte[] bytes=new byte[]{};
		if(file.exists()){
		InputStream is = new FileInputStream(file);
		bytes = new byte[(int) file.length()];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}
		is.close();
		}
		return bytes;
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			} else {
				file.delete();
			}
		} else {
			System.out.println("所删除的文件不存在！" + '\n');
		}
	}

	/**
	 * 获得文件夹下所有文件(返回ArrayList<byte[]>)
	 * 
	 * @param file
	 * @return ArrayList<byte[]>
	 */
	public static ArrayList<byte[]> findFolderFile(File file) {
		ArrayList<byte[]> returnList = new ArrayList<byte[]>();
		try {
			// 声明目录下所有的文件 files[]
			File files[] = file.listFiles();
			// 遍历目录下所有的文件
			for (int i = 0; i < files.length; i++) {
				returnList.add(getBytesFromFile(files[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnList;
	}

	/**
	 * 获得文件夹下所有文件(返回JSONArray)
	 * 
	 * @param file
	 * @return JSONArray
	 */
	public static JSONArray findFolderFileReturnJsonArray(File file) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			// 声明目录下所有的文件 files[];
			File files[] = file.listFiles();
			if(files!=null && files.length>0)
			{
				// 遍历目录下所有的文件
				for (int i = 0; i < files.length; i++) {
					JSONObject jo = new JSONObject();
					jo.put("fileName", files[i].getName());
					String fileByteStr = Base64
							.encodeBytes(getBytesFromFile(files[i]));
					jo.put("fileByte", fileByteStr);
					jo.put("state", "upload");
					returnJsonArray.put(jo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}

	/**
	 * 记录业务操作日志
	 * @param type 业务类型：1接口调入、2接口调出、3新增、4修改、5删除
	 * @param modulename 模块名称
	 * @param dataid 业务数据id
	 * @param message 日志内容
	 * @param flag 是否成功
	 * @param result 执行结果
	 * @param userID 用户ID
	 * */
	public static synchronized void addBusinesslog(int type,String modulename,String dataid,String message,String flag,String result,String userID) {
		message = message.replaceAll("'", "\"");
		result = result.replace("'", "\"");
		String logSql = "insert into aOperationLog (id,Type,Modulename,dataid,Message,Createtime,Flag,Result,Operationuser) " +
				"select case when max(id) is null then 0 else max(id)+1 end,'"+type+"','"+modulename+"','"+dataid+"','"+message+"'," +
				DataBaseFactory.getDataBase().getSysdateStr()+",'"+flag+"','"+result+"','"+userID+"'" +
				" from aOperationLog";
		DatabaseAccess.execute(logSql);
	}
	
	/**
	 * 添加业务操作日志
	 * 
	 * @param message
	 *            日志
	 * @param userID
	 *            用户ID
	 * */
	public static void addBusinesslog(String message, String userID) {
		message = message.replaceAll("'", "\"");
		String logSql = "INSERT INTO abdp_businesslog(id, message, reguserid, regdate) VALUES ('"
				+ UUID.randomUUID().toString()
				+ "', '"
				+ message
				+ "', '"
				+ userID
				+ "', "
				+ DataBaseFactory.getDataBase().getSysdateStr() + "   )";
		DatabaseAccess.execute(logSql);
	}

	/**
	 * 删除指定时间之前的文件
	 * 
	 * @param file
	 */
	public static void deleteFileByDate(File file, Date date) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			} else {
				// 当前时间
				Date d = new Date(file.lastModified());
				if (d.before(date)) {
					file.delete();
				}
			}
		} else {
			System.out.println("所删除的文件不存在！" + '\n');
		}
	}
	/**
	 * 根据关键字在指定文件夹搜索指定后缀名的文件
	 * @param keyName 关键字，多个关键字之间用空格分隔
	 * @param extension 后缀名，多个后缀名之间用逗号分隔
	 * */
	public static JSONArray GlobalSearchFilesInfo(String path,String keyName,String extension)
	{
		JSONArray js=new JSONArray();
		 File root = new File(path);
	        File[] filesOrDirs = root.listFiles();
	        if(filesOrDirs!=null && filesOrDirs.length>0)
	        {
	        	 for (int i = 0; i < filesOrDirs.length; i++) 
	 	        {
	 	            if (filesOrDirs[i].isDirectory())
	 	            {
	 	            	GlobalSearchFilesInfo(js,filesOrDirs[i].getAbsolutePath(),keyName,extension);
	 	            } 
	 	            else 
	 	            {
	 	            	if(Filter(filesOrDirs[i].getName(),keyName,extension))
	 	            	{
	 	            		int eindex = filesOrDirs[i].getName().lastIndexOf('.');
	 	            		JSONObject jo=new JSONObject();
	 	            		try {
	 	            			jo.put("filename",filesOrDirs[i].getName());//文件名
	 	            			jo.put("newfilename",filesOrDirs[i].getName());//文件名
	 							jo.put("path",filesOrDirs[i].getAbsolutePath());//路径
	 		            		jo.put("filetype",filesOrDirs[i].getName().substring(eindex + 1));//后缀名
	 		            		js.put(jo);
	 						} catch (JSONException e) {
	 							e.printStackTrace();
	 						}
	 	            		
	 	            	}
	 	            }
	 	        }
	        }
	       
		return js;
	}
	
	/**
	 * 根据关键字在指定文件夹搜索指定后缀名的文件
	 * @param keyName 关键字，多个关键字之间用空格分隔
	 * @param extension 后缀名，多个后缀名之间用逗号分隔
	 * */
	private static void GlobalSearchFilesInfo(JSONArray js,String path,String keyName,String extension)
	{
		File root = new File(path);
        File[] filesOrDirs = root.listFiles();
        if(filesOrDirs!=null && filesOrDirs.length>0)
        {
        	 for (int i = 0; i < filesOrDirs.length; i++) 
 	        {
 	            if (filesOrDirs[i].isDirectory())
 	            {
 	            	GlobalSearchFilesInfo(js,filesOrDirs[i].getAbsolutePath(),keyName,extension);
 	            } 
 	            else 
 	            {
 	            	if(Filter(filesOrDirs[i].getName(),keyName,extension))
 	            	{
 	            		int eindex = filesOrDirs[i].getName().lastIndexOf('.');
 	            		JSONObject jo=new JSONObject();
 	            		try {
 	            			jo.put("filename",filesOrDirs[i].getName());//文件名
 	            			jo.put("newfilename",filesOrDirs[i].getName());//文件名
 							jo.put("path",filesOrDirs[i].getAbsolutePath());//路径
 		            		jo.put("filetype",filesOrDirs[i].getName().substring(eindex + 1));//后缀名
 		            		js.put(jo);
 						} catch (JSONException e) {
 							e.printStackTrace();
 						}
 	            		
 	            	}
 	            }
 	        }
        }
	}
	
	/**
	 * 过滤文件
	 * @param file 文件名
	 * @param keyName 关键字，多个关键字之间用空格分隔
	 * @param extension 后缀名，多个后缀名之间用逗号分隔
	 * @return true表示当前文件是要搜索的文件，反之则false
	 * */
	private static boolean Filter(String file,String keyName,String extension) {
		String[] exs = extension.split(",");
		String[]kns=keyName.split(" ");
		if (exs != null && exs.length > 0 && kns!=null && kns.length>0) {
			for (int i = 0; i < exs.length; i++) {
				if (file.toLowerCase().endsWith(exs[i].toLowerCase())) {
					for(int j=0;j<kns.length;j++)
					{
						if(kns[j]!=null && !kns[j].trim().equalsIgnoreCase("") && file.indexOf(kns[j])>=0)
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 获得指定的文件(返回JSONArray)
	 * @param file
	 * @return JSONArray
	 */
	public static JSONArray findFile(File file) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			if(file!=null && file.isFile())
			{
				JSONObject jo = new JSONObject();
				jo.put("fileName", file.getName());
				String fileByteStr = Base64
						.encodeBytes(getBytesFromFile(file));
				jo.put("fileByte", fileByteStr);
				returnJsonArray.put(jo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}
	
	/**
	 * 根据关键字从数据库中搜索相关文档信息
	 * @param keyName 关键字，多个关键字之间用空格分隔
	 * @param extension 后缀名，多个后缀名之间用逗号分隔,形如：'doc','xls','ppt'
	 * */
	public static JSONArray GlobalSearchFilesInfo(String keyName,String extension)
	{
		if(keyName==null || keyName.equalsIgnoreCase(""))
		{
			return null;
		}
		String kns=keyName.trim().toLowerCase().replace(" ", "|");
		JSONArray js=new JSONArray();
		String qry="select caseid,filetype,filename,case when fldvaluename is null then '其他' else fldvaluename end modulename" +
				" from bzgl_casemanage a left join sdatadictionarymx b on fldvaluecode=a.businesstable and dictionaryid='wd_modulecontrast'" +
				" where filetype in ("+extension+") and FILENAME ~* '(" + kns + ")'";
		MapList list=null;
		try {
			list = DBFactory.getDB().query(qry);
		} catch (JDBCException e1) {
			e1.printStackTrace();
		}
		if(list!=null && list.size()>0)
		{
			for(int i = 0; i < list.size(); i++)
			{
				Row row = list.getRow(i);
				String caseid=row.get("caseid");
				String fileName=row.get("filename");
				String fileType=row.get("filetype");
				String path=Path.getRootPath()+"\\files\\BZGL_CASEMANAGE\\"+caseid+"\\oldfilename\\"+caseid+"."+fileType;
				String url = "/files/BZGL_CASEMANAGE/"+caseid+"/oldfilename/"+caseid+"."+fileType;
				String moduleName=row.get("modulename");

				File f = new File(path);
				if(f.exists())
				{
					JSONObject jo=new JSONObject();
	        		try {
	        			jo.put("filename",fileName);//原始文件名，用于显示
	        			jo.put("newfilename",caseid+"."+fileType);//新文件名，用于下载
						jo.put("path",path);//路径
	            		jo.put("filetype",fileType);//后缀名
	            		jo.put("url",url);//文件相对路径
	            		jo.put("modulename",moduleName);
	            		js.put(jo);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return js;
	}
	
	/**
	 * 计算当前时间与系统时间的差值，不同的时间段，显示不同的格式
	 * currentTime 要转换的时间，格式：yyyy-MM-dd HH:mm:ss
	 * */
	public static String getDiffTimeToFormat(String currentTime)
	{
		long diff=DateUtil.getSeconds(currentTime,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		if(currentTime==null || currentTime.trim().equalsIgnoreCase(""))
		{
			return "";
		}
		else if((diff/60)<60)
		{
			long d=diff/60;
			if(d==0)
			{
				d=1;
			}
			return d+"分钟前";
		}
		else if((diff/60)>=60 && (diff/60/60)<24)
		{
			long d=diff/60/60;
			if(d==0)
			{
				d=1;
			}
			return d+"小时前";
		}
		else if((diff/60/60)>=24 && (diff/60/60)<48)
		{
			return "昨天";
		}
		else if((diff/60/60)>=48 && (diff/60/60/24)<365)
		{
			long d=diff/60/60/24;
			if(d==0)
			{
				d=1;
			}
			return d+"天前";
		}
		else if((diff/60/60/24)>=365)
		{
			long d=diff/60/60/24/365;
			if(d==0)
			{
				d=1;
			}
			return d+"年前";
		}
		else 
		{
			return "";
		}
	}
}
