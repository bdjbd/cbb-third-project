package com.ambdp.menu.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Action;
import com.fastunit.util.Checker;
import com.wd.comp.Constant;
/**
 * 功能：导入会员Excel文件到数据库
 */
public class ImportMemberAction implements Action {

	private static final int BUFFER_SIZE = 1024;
	private final Logger logger = Logger.getLogger(ImportMemberAction.class);
	
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		if(ac.getFile("am_member_list.importfile") == null) return ac;
		File file = ac.getFile("am_member_list.importfile");
		String fileName = ac.getRequestParameter("am_member_list.importfilefilename");
		String filePath = Constant.EXCEL_TEMP_FILEPATH + fileName;
		
		File dst = new File(filePath);
		InputStream in = new BufferedInputStream(new FileInputStream(file),	BUFFER_SIZE);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(dst),BUFFER_SIZE); 
		byte[] buffer = new byte[BUFFER_SIZE];
		int byteCount = in.read(buffer);
		while (byteCount > 0) {
			out.write(buffer,0,byteCount);
			byteCount = in.read(buffer);
		}
		in.close();
		out.close();
		
		importMember(filePath);
		
		return ac;
	}
	
	private void importMember(String filePath)
	{
        InputStream is;
        HSSFWorkbook hssfWorkbook;
        HSSFSheet sheet;
        HSSFRow row;
        int rowNum;
        StringBuffer importSql;
		try {
			
			is = new FileInputStream(filePath);
			hssfWorkbook = new HSSFWorkbook(is);
			sheet = hssfWorkbook.getSheetAt(0);
			rowNum = sheet.getLastRowNum();
			String phone;
			String password;
			String membername;
			String memberbirthday;
			String membersex;
			String email;
			String registrationdate;
			String identitycardnumber;
			String memberaddress;
			String orgcode;
			password = "888888";
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			registrationdate = sf.format(new java.util.Date());
			
			for (int i = 1; i <= rowNum; i++)
			{
				importSql = new StringBuffer("INSERT INTO am_member(id, loginaccount, loginpassword, phone, membername, memberbirthday, membersex, email, registrationdate, identitycardnumber, memberaddress, orgcode) values ( ");
				row = sheet.getRow(i); 
				orgcode = getStringCellValue(row.getCell(0));
				membername = getStringCellValue(row.getCell(1));
				phone = getStringCellValue(row.getCell(2));
				memberbirthday = getStringCellValue(row.getCell(3));
				membersex = getStringCellValue(row.getCell(4));
				email = getStringCellValue(row.getCell(5));
				identitycardnumber = getStringCellValue(row.getCell(6));
				memberaddress = getStringCellValue(row.getCell(7));
				
				orgcode = getOrgCode(orgcode);
				
				importSql.append("'"+UUID.randomUUID().toString()+"'");
				importSql.append(",'"+phone+"'");
				importSql.append(",'"+com.p2p.Utils.getMD5Str(password)+"'");
				importSql.append(",'"+phone+"'");
				importSql.append(",'"+membername+"'");
				importSql.append(",'"+memberbirthday+"'");
				importSql.append(",'"+membersex+"'");
				importSql.append(",'"+email+"'");
				importSql.append(",'"+registrationdate+"'");
				importSql.append(",'"+identitycardnumber+"'");
				importSql.append(",'"+memberaddress+"'");
				importSql.append(",'"+orgcode+"')");
				
				logger.debug(importSql.toString());
				try {
					DBFactory.getDB().execute(importSql.toString());
				} catch (JDBCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	
	 private String getStringCellValue(HSSFCell cell) {      
         String strCell = "";
         
         if (cell == null) {      
             return "";      
         }
         
         switch (cell.getCellType()) 
         {      
	         case Cell.CELL_TYPE_STRING:
	             strCell = cell.getStringCellValue();      
	             break;      
	         case Cell.CELL_TYPE_NUMERIC:
	        	 DecimalFormat df = new DecimalFormat("0");  
	        	 strCell = df.format(cell.getNumericCellValue()); 
	        	 break;      
	         case Cell.CELL_TYPE_BOOLEAN:      
	             strCell = String.valueOf(cell.getBooleanCellValue());      
	             break;      
	         case Cell.CELL_TYPE_BLANK:
	             strCell = "''";      
	             break;     
	         default:      
	             strCell = "''";      
	             break;      
         }
         if (strCell.equals("''") || strCell == null) {      
             return "";      
         }
         
         return strCell;      
     }
	 
	 private String getOrgCode(String orgcode)
	 {
		 String orgid = "";
		 String orgSql = "select orgid from aorg where orgname = '"+orgcode+"'";
		 MapList map = new MapList();
		 try {
			 map = DBFactory.getDB().query(orgSql);
		 } catch (JDBCException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }

		 if (!Checker.isEmpty(map))
		 {
			 orgid = map.getRow(0).get("orgid");
		 }
		 return orgid;
	 }

}
