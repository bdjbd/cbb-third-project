package com.wd.upload;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.Path;
import com.lowagie.text.pdf.codec.Base64;
import com.wd.ICuai;
import com.wd.tools.CommonUtil;

/**
 *   说明:
 * 			离线文件附件下载类
 *   @creator	岳斌
 *   @create 	2013-2-28 
 *   @revision	$Id
 */
public class OfflieFileDownloadCuai implements ICuai {
	/**读取文件大小**/
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJA=null;
		try {
			JSONObject infoJo=jsonArray.getJSONObject(0);
			if(infoJo==null||infoJo.length()==0){
				return new JSONArray().put(new JSONObject().put("RESULT", "FALSE"));
			}
			//获取表名
			String tableName=infoJo.getString("TABLENAME");
			//获取主键值
			String primaryKeyValue=infoJo.getString("PRIMARYKEYVALUE");
			//获取元素编号
			String elemNo=infoJo.getString("ELEMENTNO");
			//获取元素类型
			String elemType=infoJo.getString("ELEMENTTYPE").equals("1")?"ENCLOSURE":"SIGN";
			//手持端主键ID
			String localTablePK=infoJo.getString("LOCALTABLEPK");
			//获取文件目录
			String filePath=Path.getHomePath()+File.separator+"files"+
					File.separator+tableName+File.separator+primaryKeyValue+File.separator+elemNo;
			//根据文件目录和元素类型将附件转行成指定格式的JSONArray对象.
			returnJA=fileToJSONArray(filePath,elemType,localTablePK,tableName+File.separator+primaryKeyValue+File.separator+elemNo);
			if(returnJA==null||returnJA.length()==0){
				return new JSONArray().put(new JSONObject().put("RESULT", "FALSE"));
			}
			//设置文件操作结果
			returnJA.put(new JSONObject().put("RESULT", "TRUE"));
		} catch (JSONException e) {
			e.printStackTrace();
		}catch(Exception e1){
			e1.printStackTrace();
		}
		return returnJA;
	}
	
	/***
	 * 将filePaht下面的文件转行成JSONArray格式
	 * @param filePath  目录名称
	 * @param elemType  元素类型：1，enclosure 签名 。2, sign附件。
	 * @param dbSaveFilePaht 附件保存字段，tableName+primaryKey+ElementNo
	 * @return  [....,{"FILENAME":"FILE_NAME.TXT|JPG|MP3","ELEMTYPE":"SING|ENCLOSURE","FILEDATA":"文件BASE64编码","FILELENGTH":"10000"}]
	 * @throws  IOException
	 */
	private JSONArray fileToJSONArray(String filePath, String elemType,String localTablePK,String dbSaveFilePaht)throws Exception {
		JSONArray fileJSA=new JSONArray();
		File file=new File(filePath);
		if(!file.exists()){
			//文件不存在，返回null
			return null;
		}
		
		//获取目录下的所有文件
		File[] files=file.listFiles();
		for(File f:files){
			byte[] fileBuffer=CommonUtil.getBytesFromFile(f);
			JSONObject jo=new JSONObject();
			jo.put("FILENAME",f.getName());
			jo.put("ELEMTYPE", elemType);
			jo.put("FILEDATA",Base64.encodeBytes(fileBuffer));
			jo.put("FILELENGTH",f.length());
			jo.put("LOCALTABLEPK",localTablePK);
			jo.put("SERVERFILEPATH", dbSaveFilePaht);
			fileJSA.put(jo);
		}
		return fileJSA;
	}
}
