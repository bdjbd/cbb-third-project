package com.wd.tools.fileTransimssion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.Path;
import com.lowagie.text.pdf.codec.Base64;

public class FileTransmission implements Runnable {

	/** SOCKET 请求 **/
	public static final String REQUEST = "REQUEST";
	/** SOCKET 请求 下载文件 **/
	public static final String DOWNLOAD_FILE = "DOWNLOAD_FILE";
	/**上传类型 **/
	public static final String UPLOAD_FILE_TYPE = "UPLOAD_FILE_TYPE";
	
	/** SOCKET 应答 **/
	public static final String RESPONSE = "RESPONSE";
	/** 源文件路径 **/
	public static final String SERVER_PATH = "SERVER_PATH";
	/** 目标文件路径 **/
	public static final String LOCATION_PATH = "LOCATION_PATH";
	/** 下载类型 **/
	public static final String DOWNLOAD_TYPE = "DOWNLOAD_TYPE";
	/**HOME目录**/
	private static final String HOME="${home}";
	/**ROOT目录**/
	private static final String ROOT="${root}";
	
	/**每次读取文件的大小 1024*500  **/
	public static final int BUFFER_SIZE=1024*500;
	private Socket socket;

	public FileTransmission(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			JSONArray params = new JSONArray(br.readLine());
			System.out.println("request:"+params);
			JSONObject headJob = params.getJSONObject(0);
			String reques = headJob.getString(REQUEST);
			
			if (DOWNLOAD_FILE.equals(reques)) {// 下载文件
				downloadFileProcess(headJob);
			}else if(UPLOAD_FILE_TYPE.equals(reques)){//上传文件
				uploadFileProcess(params,socket);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void uploadFileProcess(JSONArray sctData,Socket sct) {
		/*
		 * [{"REQUEST":"UPLOAD_FILE_TYPE","SAVEPATH":"savepath"},
		 * {"FILENAME":"当前文件名","FILESIZE":"当前文件大小"},
		 * {"START":"数据开始位置","END":"数据结束位置",
		 * "LENGTH":"本次读取长度","FILEDATA":"Base64.encode UTF-8 data" } ]
		 */
		try {			
			String savePath=getSavePaht(sctData.getJSONObject(0).getString(SERVER_PATH));
//			new String(Base64.decode(sctData.getJSONObject(0).getString(SERVER_PATH),Base64.DECODE),"UTF-8");
			File file=new File(savePath);
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			String dataStr="";
			BufferedReader br = new BufferedReader(new InputStreamReader(sct.getInputStream()));
			while ((dataStr = br.readLine()) != null) {
				JSONArray fileData = new JSONArray(dataStr);
				//此次数据开始读取位置
				int start=Integer.parseInt(fileData.getJSONObject(2).getString("START"));
				RandomAccessFile raf = new RandomAccessFile(savePath, "rw");
				// 将文件制定跳到指定的位置
				raf.seek(start);
				raf.write(Base64.decode(fileData.getJSONObject(2).getString("FILEDATA"),Base64.DECODE));
				raf.close();
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		
	}

	/**
	 * 下载文件处理
	 * @param headJob   
	 * @throws Exception
	 */
	public void downloadFileProcess(JSONObject headJob) throws Exception {
		String serverPath =getSavePaht(headJob.getString(SERVER_PATH)); 
//				new String(Base64.decode(headJob.getString(SERVER_PATH), Base64.DECODE), "UTF-8");// 文件路径
		File file = new File(serverPath);
		if (file.isDirectory()) {// 下载目录先所有文件
			downloadDirectory(file);
		} else if (file.isFile()) {// 下载单个文件
			downloadFile(file,"FILE");
		} else {
			sendErroeInfo(socket);
			return;
		}
	}

	public void downloadFile(File file,String type) throws Exception {
		if (!file.exists()) {
			return;
		}
		PrintWriter out=new PrintWriter(socket.getOutputStream());
		sendFileData(file,out,type);
	}

	public void downloadDirectory(File file) throws Exception {
		if(!file.exists()){
			return;
		}
		for(File f:file.listFiles()){
			downloadFile(f,"DIRECTORY");
		}
	}

	public void sendFileData(File file, PrintWriter out,String downloadType)
			throws Exception {
		/*
		 * [ {"DOWNLOAD_TYPE":"FILE||DIRECTORY(文件或目录)" },
		 * {"FILENAME":"当前文件名","FILESIZE":"当前文件大小"}, 
		 * {"START":"数据开始位置","END":"数据结束位置", "LENGTH":"本次读取长度","FILEDATA":"Base64.encode UTF-8 data" }
		 * ]
		 */
		
		JSONObject downTypeJobt=new JSONObject();
		downTypeJobt.put(DOWNLOAD_TYPE,downloadType);
		
		JSONObject fileInfoJobt=new JSONObject();
		fileInfoJobt.put("FILENAME",file.getName());
		fileInfoJobt.put("FILESIZE", file.length());
		
		long start=0;
		long end=0;
		long length=0;
		byte[] readBuff=new byte[BUFFER_SIZE];
		
		InputStream in=new FileInputStream(file);
		while((length=in.read(readBuff))>0){
			JSONArray sendData=new JSONArray();
			
			start=end;
			end=start+length;
			JSONObject fileData=new JSONObject();
			fileData.put("START", start);
			fileData.put("END", end);
			fileData.put("LENGTH","LENGTH");
			fileData.put("FILEDATA",Base64.encodeBytes(readBuff));
			
			sendData.put(downTypeJobt);
			sendData.put(fileInfoJobt);
			sendData.put(fileData);
			
			out.println(sendData);
		}
		out.flush();
		if(!socket.isClosed()){
			socket.close();
		}
	}
	
	private static  String getSavePaht(String baseEncodeStr){
		String result="";
		try {
			result=new String(Base64.decode(baseEncodeStr, Base64.DECODE), "UTF-8");
			if(result!=null&&result.contains(HOME)){//home目录
				String home=Path.getHomePath();
				result=result.replace(HOME, home);
				return result;
			}else if(result!=null&&result.contains(ROOT)){
				String root=Path.getRootPath();
				result=result.replace(ROOT, root);
				return result;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public  void sendErroeInfo(Socket ss)throws Exception{
		JSONArray errorInfo=new JSONArray();
		JSONObject job=new JSONObject();
		job.put(DOWNLOAD_TYPE,"ERROR");
		errorInfo.put(job);
		PrintWriter out=new PrintWriter(socket.getOutputStream());
		out.println(errorInfo);
	}
}
