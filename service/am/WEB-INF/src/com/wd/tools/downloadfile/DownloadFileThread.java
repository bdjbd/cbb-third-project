package com.wd.tools.downloadfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.Path;
import com.lowagie.text.pdf.codec.Base64;
import com.wd.tools.DatabaseAccess;
import com.wd.tools.fileTransimssion.FileTransmission;

/**
 *   说明:
 * 			使用Socket下载文件时，客户端Socket文件下载处理线程类</br>
 * 服务器向客户端发送消息格式的固定格式
 * 消息格式：<h2>[</br>
 * 				{"DIRECTORYS":"目录中文件数量",</br>
 * 				 "FILES":"目录文件名，用逗号分开",</br>
 * 			     "FILENAME":"当前文件名",</br>
 * 				 "FILESIZE":"当前文件大小",</br>
 * 			     "START":"数据开始位置",</br>
 * 			     "END":"数据结束位置",</br>
 * 				 "LENGTH":"本次读取长度",</br>
 * 				 "ELEMENTNO":"元素编号",</br>
 * 				 "LOCALTABLEPK":"手持端数据下载表主键",</br>
 * 				 "SERVERSAVEPAHT":"文件服务器端保存路径"</br>
 * 				},{</br>
 * 				  "FILEDATA":"本次读取的数据，Base64编码"</br>
 * 				}]</br></h2>
 *	<h2>客户端请求服务消息头格式：</br>		
 * 		[ {</br>
 * 			 	"TABLENAME":"表名", </br>
 * 				"PRIMARYKEYVALUE":"主键值",</br>
 * 				"ELEMENTNO":"元素编号",</br>
 * 				"ELEMENTTYPE":"元素类型1附件，2签名",</br>
 * 				"LOCALTABLEPK":"本地数据下载表主键" },</br>
 * 		  {</br>
 *		 		"DOWNLOADFILETYPE":"下载文件类型，1，下载全部目录先文件，2，下载指定文件，3,断点续传",</br>
 * 				"FILENAMES":"文件类型，当下载类型为目录全部文件是为NULL，当下载指定文件时，多个文件使用逗号隔开，</br>
 * 					断点续传时为断点续传文件名"</br>
 * 		  }, </br>
 * 		  { </br>
 * 				"START":"断点续传开始位置",</br>
 * 				"END":"断点续传结束位置，当为NULL表示从开始位置到文件结束"</br>
 * 		 } ]</h2>
 * 
 * 追加了上传班组圈子图片的功能
 *   @creator	岳斌
 *   @create 	2013-3-6 
 *   @revision	$Id
 */
public class DownloadFileThread implements Runnable {
	/**客户端Socket**/
	private Socket clientSocket;
	/**客户端Socket输入流**/
	private InputStream in;
	/**客户端Socket输出流**/
	private OutputStream out;
	/**下载文件目录**/
	private String path="";
	/**文件下载类型**/
	private String downfielType="";
	/**客户端消息头**/
	private JSONObject clientHeadJo;
	/**当前目录中文件数量**/
	private int fileCount;
	/** SOCKET 请求 **/
	public static final String REQUEST = "REQUEST";
	/** SOCKET 请求 下载文件 **/
	public static final String DOWNLOAD_FILE = "DOWNLOAD_FILE";
	/**上传类型 **/
	public static final String UPLOAD_FILE_TYPE = "UPLOAD_FILE_TYPE";
	private FileTransmission ftr;
	
	private String rootpath="";
	public DownloadFileThread(Socket socket){
		this.clientSocket=socket;
		rootpath=Path.getRootPath();
	}
	@Override
	public void run() {
		try{
			in=clientSocket.getInputStream();
			out=clientSocket.getOutputStream();
			String data="";
			BufferedReader reader=new BufferedReader(new InputStreamReader(in));
			while(!clientSocket.isInputShutdown()){
				data=reader.readLine();
				JSONArray head=null;
				try{
					head=new JSONArray(data);					
				}catch (Exception e) {
					continue;
				}
					 
					
				//下载文件基本信息
				clientHeadJo=head.getJSONObject(0);
				if(clientHeadJo.has("SOCKET_FLAG"))
				{
					//上传
					String socket_flag=clientHeadJo.getString("SOCKET_FLAG");
					if(socket_flag.equalsIgnoreCase("1"))
					{
						//内容图片上传
						String id=clientHeadJo.getString("ID");//内容id
						String fileName=new String(Base64.decode(clientHeadJo.getString("FILENAME"),Base64.DECODE),"UTF-8");//文件名称
						int fileSize=Integer.parseInt(clientHeadJo.getString("FILESIZE"));//文件大小
						int start=Integer.parseInt(clientHeadJo.getString("START"));//此次数据开始读取位置
						int end=Integer.parseInt(clientHeadJo.getString("END"));//此次数据结束位置
						
						createFile("tc_contentpicture",id,fileName,start,clientHeadJo.getString("FILEDATA"));
						if(end==fileSize)
						{
							//一个文件下载完成,将信息记录到数据库。
							String cid=UUID.randomUUID().toString();
							String path="files/tc_contentpicture/"+id+"/"+fileName;
							String insertSql="INSERT INTO tc_contentpicture(id, cid, path) VALUES ('"+id+"','"+cid+"','"+path+"');";
							DatabaseAccess.execute(insertSql);
						}
					}
					else if(socket_flag.equalsIgnoreCase("2"))
					{
						//评论图片上传
						String id=clientHeadJo.getString("ID");//评论id
						String fileName=new String(Base64.decode(clientHeadJo.getString("FILENAME"),Base64.DECODE),"UTF-8");//文件名称
						int fileSize=Integer.parseInt(clientHeadJo.getString("FILESIZE"));//文件大小
						int start=Integer.parseInt(clientHeadJo.getString("START"));//此次数据开始读取位置
						int end=Integer.parseInt(clientHeadJo.getString("END"));//此次数据结束位置
						
						createFile("tc_teamcomment",id,fileName,start,clientHeadJo.getString("FILEDATA"));
						if(end==fileSize)
						{
							//一个文件下载完成,将信息记录到数据库。
							String sql="select paths from tc_TeamComment where pid='"+id+"'";
							JSONArray js=DatabaseAccess.query(sql);
							if(js!=null && js.length()>0)
							{
								String paths=js.getJSONObject(0).getString("PATHS");
								String p="files/tc_teamcomment/"+id+"/"+fileName;
								if(paths!=null && !paths.trim().equalsIgnoreCase(""))
								{
									paths+=","+p;
								}
								else
								{
									paths=p;
								}
								String update="update tc_teamcomment set paths='"+paths+"' where pid='"+id+"'";
								DatabaseAccess.execute(update);
							}
						}
					}
					else if(socket_flag.equalsIgnoreCase("3"))
					{
						//个人封面
						String userid=clientHeadJo.getString("USERID");//userid
						String fileName=new String(Base64.decode(clientHeadJo.getString("FILENAME"),Base64.DECODE),"UTF-8");//文件名称
						int fileSize=Integer.parseInt(clientHeadJo.getString("FILESIZE"));//文件大小
						int start=Integer.parseInt(clientHeadJo.getString("START"));//此次数据开始读取位置
						int end=Integer.parseInt(clientHeadJo.getString("END"));//此次数据结束位置
						
						createFile("tc_circlemembers",userid,fileName,start,clientHeadJo.getString("FILEDATA"));
						if(end==fileSize)
						{
							//一个文件下载完成,将信息记录到数据库。
							String path="files/tc_circlemembers/"+userid+"/"+fileName;
							String update="UPDATE tc_circlemembers SET bgimage='"+path+"' WHERE userid='"+userid+"';";
							DatabaseAccess.execute(update);
						}
					}
				}else if(clientHeadJo.has(REQUEST)){
					String reques = clientHeadJo.getString(REQUEST);
					if(ftr==null){
						ftr=new FileTransmission(clientSocket);
					}
					if (DOWNLOAD_FILE.equals(reques)) {// 下载文件
						ftr.downloadFileProcess(clientHeadJo);
					}else if(UPLOAD_FILE_TYPE.equals(reques)){//上传文件
						ftr.uploadFileProcess(head,clientSocket);
					}
				}
				else
				{
					//下载文件类型，文件
					JSONObject downfileInfo=head.getJSONObject(1);
					downfielType=downfileInfo.getString("DOWNLOADFILETYPE");
					if(downfielType==null||"1".equals(downfielType.trim())){
						//下载目录下全部文件
						downloadAllFiles(out,clientHeadJo);
					}else if(downfielType!=null||"2".equals(downfielType.trim())){
						//下载部分文件
						downloadAssignationFiles(out,clientHeadJo,downfileInfo.getString("FILENAMES"));
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/***
	 * 下载指定的文件。
	 * @param out2  clientSocket输出流
	 * @param clientHeadJo2  客户端下载信息请求头，JOSNObject
	 * @param fileNames  需要下载的文件名，多个文件使用都和分隔
	 * @exception JSONException
	 */
	private void downloadAssignationFiles(OutputStream out2,
			JSONObject clientHeadJo2, String fileNames)throws JSONException {
		path=builderPath(clientHeadJo);
		/**离线文件根目录**/
		String root=Path.getHomePath()+File.separator+"files";
		File file=new File(root+File.separator+path);
		/**客户端信息输出流**/
		PrintWriter clientSocketWriter=new PrintWriter(out2);
		//判断目录,及目录下是否有文件存在
		fileNames=checkFilesExists(file,fileNames);
		if(!file.exists()||file.list()==null||file.list().length==0||
				fileNames==null){
			//如果不存在。
			JSONArray fileDataJa=builderNoFileHead(clientHeadJo2);
			clientSocketWriter.print(fileDataJa.toString());
			clientSocketWriter.flush();
			clientSocketWriter.print("\n");
			return;
		}
		File[] files=file.listFiles();
		for(File f:files){
			if(fileNames.contains(f.getName())){//判断需要下载的文件进行下载
				//将需要下载的文件写入到输出流中
				downLoadFile(f,clientSocketWriter,clientHeadJo,fileNames);
			}
		}
	}
	
	/**
	 * 检查file目录下对应的fileNames文件是否存在，如果存在，返回true，否则返回
	 * 存在的文件名，但全无是返回null
	 * @param file 目录
	 * @param fileNames 文件名，多个文件使用逗号分隔
	 * @return  存在时返回存在的文件名，如果不存在，返回null
	 */
	private String checkFilesExists(File file,String fileNames) {
		String exitsFils="";
		String[] files=file.list();
		for(String fileName:files){
			if(fileNames.contains(fileName)){
				exitsFils+=fileName+",";
			}
		}
		return exitsFils.length()>0?exitsFils.substring(0, exitsFils.length()-1):null;
	}
	/**
	 * 下载目录下知道的全部文件
	 * @param out2  clientSocket输出流
	 * @param clientHeadJo2   客户端下载信息请求头，JOSNObject
	 * @throws JSONException   
	 */
	private void downloadAllFiles(OutputStream out2,JSONObject clientHeadJo2)throws JSONException{
		path=builderPath(clientHeadJo);
		/**离线文件根目录**/
		String root=Path.getHomePath()+File.separator+"files";
		File file=new File(root+File.separator+path);
		//目录下的文档名称，多个文档使用逗号分开
		String fileNames="";
		/**客户端信息输出流**/
		PrintWriter clientSocketWriter=new PrintWriter(out2);
		//判断目录,及目录下是否有文件存在
		if(!file.exists()||file.list()==null||file.list().length==0){
			//如果不存在。
			JSONArray fileDataJa=builderNoFileHead(clientHeadJo2);
			clientSocketWriter.print(fileDataJa.toString());
			clientSocketWriter.flush();
			clientSocketWriter.print("\n");
			return;
		}
		//获取目下所有的文档，多个文档之间使用逗号分隔。
		fileNames=Arrays.toString(file.list()).substring(
				Arrays.toString(file.list()).indexOf("[")+1,Arrays.toString(
						file.list()).lastIndexOf("]"));
		fileCount=file.list().length;
		File[] files=file.listFiles();
		for(File f:files){
			//将需要下载的文件写入到输出流中
			downLoadFile(f,clientSocketWriter,clientHeadJo,fileNames);
		}
		
	}
	
	/**
	 * 
	 * @param f  需要下载的文件
	 * @param out2   客户端socket输出流
	 * @param clientHeadJo2  消息头
	 * @param fileNames 目录下对应的文件，多个文件使用逗号分隔
	 */
	private void downLoadFile(File f, PrintWriter out2,
			JSONObject clientHeadJo2,String fileNames) throws JSONException{
		try{
			/**服务器端文件读取过滤器**/
			InputStream in=new FileInputStream(f);
			byte[] buff=new byte[DownloadFileSocketServer.BUFFER_SIZE];
			int start=0;
			int end=0;
			int length=0;
			long fileSize=f.length();
			String fileName=f.getName();
			while((length=in.read(buff))>0){
				start=end;
				end=start+length;
				/**构造发送客户端的消息头**/
				JSONObject headJo=new JSONObject();
				headJo.put("DIRECTORYS", ""+fileCount);
				headJo.put("FILES", Base64.encodeBytes(fileNames.getBytes(Charset.forName("UTF-8"))));
				headJo.put("FILENAME", Base64.encodeBytes(fileName.getBytes(Charset.forName("UTF-8"))));
				headJo.put("FILESIZE", fileSize);
				headJo.put("START", start);
				headJo.put("END", end);
				headJo.put("LENGTH", length);
				headJo.put("ELEMENTNO", clientHeadJo2.getString("ELEMENTNO"));
				headJo.put("LOCALTABLEPK", clientHeadJo2.getString("LOCALTABLEPK"));
				headJo.put("SERVERSAVEPATH", path);
				JSONObject fileData=new JSONObject();
				fileData.put("FILEDATA",Base64.encodeBytes(buff));
				JSONArray fileDataJa=new JSONArray();
				fileDataJa.put(headJo);
				fileDataJa.put(fileData);
				
				out2.print(fileDataJa.toString());
				//行缓冲
				out2.print("\n");
				out2.flush();
			}
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**获取下载目录路径
	 * @param clientHeadJo2 接收到客户端的消息头
	 * @return tableName+priamrkeyVale+elementno组成的路径
	 **/
	private String builderPath(JSONObject clientHeadJo2) throws JSONException{
		String path=clientHeadJo2.getString("TABLENAME")+
				File.separator+clientHeadJo2.getString("PRIMARYKEYVALUE")+
				File.separator+clientHeadJo2.getString("ELEMENTNO");
		return path;
	}
	
	/***
	 * 无对应的目录时，构造返回客户端的信息
	 * @param requestJO  客户端请求信息头
	 * @return
	 */
	private JSONArray builderNoFileHead(JSONObject requestJO)throws JSONException{
		/**构造发送客户端的消息头**/
		JSONObject headJo=new JSONObject();
		//如果目录不存在，此项为：NOTDIRECTORYS001
		headJo.put("DIRECTORYS", "NOTDIRECTORYS001");
		headJo.put("FILES", "");
		headJo.put("FILENAME","");
		headJo.put("FILESIZE","0");
		headJo.put("START","");
		headJo.put("END", "0");
		headJo.put("LENGTH","0");
		headJo.put("ELEMENTNO", requestJO.getString("ELEMENTNO"));
		headJo.put("LOCALTABLEPK", requestJO.getString("LOCALTABLEPK"));
		headJo.put("SERVERSAVEPATH","");
		JSONObject fileData=new JSONObject();
		fileData.put("FILEDATA","");
		JSONArray fileDataJa=new JSONArray();
		fileDataJa.put(headJo);
		fileDataJa.put(fileData);
		return fileDataJa;
	}
	
	/**
	 * 利用分段文件流创建文件
	 * @param tableName 表名
	 * @param id 内容id
	 * @param fileName 文件名
	 * @param start 本段文件流的开始位置
	 * @param fileData 本段文件流
	 * */
	private void createFile(String tableName,String id,String fileName,int start,String fileData)
	{
		try {
			/**保存文件下载文件数据**/
			String filePath=rootpath+File.separator+"files"+File.separator+tableName+File.separator+id;
			File f=new File(filePath);
			if(!f.exists()){
				f.mkdirs();
			}
			File file=new File(filePath+File.separator+fileName);
			RandomAccessFile raf=new RandomAccessFile(file, "rw");
			//将文件制定跳到指定的位置
			raf.seek(start);
			raf.write(
					Base64.decode(fileData,Base64.DECODE));
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
