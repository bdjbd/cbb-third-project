package com.cdms.guiji;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.cdms.guiji.bean.CarLocation;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class GuiJiDaoChu implements IWebApiService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		String cph=request.getParameter("cph");
		String startime= request.getParameter("startime");
		String endtime=request.getParameter("endtime");
		String sql2="select device_sn_number from cdms_vehiclebasicinformation where license_plate_number='"+cph+"'";
		DBManager db=new DBManager();
		String sn=db.query(sql2).getRow(0).get("device_sn_number");
		System.out.println(sn);
		String sql1="select ROW_NUMBER () OVER (ORDER BY cl.positioning_time ASC) AS id,cl.driving_behavior_status,cl.alarm_status,cl.state_of_vehicle,cl.lng,cl.lat,cvb.license_plate_number,cl.location,cl.positioning_time,cl.speed,cl.current_mileage"
						+" from  cdms_vehiclebasicinformation cvb,cdms_vcd_"+sn+" cl "
                        + "where cvb.id=cl.car_id "
                        + " and cvb.license_plate_number='"+cph+"' "
                        + " and cl.positioning_time>='"+startime+"' "
                        + " and cl.positioning_time<'"+endtime+"' and isloc='1' order by cl.positioning_time ASC";

		MapList maplist=db.query(sql1);
	      HSSFWorkbook wb = new HSSFWorkbook();
	      HSSFSheet sheet = wb.createSheet("轨迹数据");
	      HSSFRow row = sheet.createRow((int) 0);
	      HSSFCellStyle style = wb.createCellStyle();
	      style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	      HSSFCell cell = row.createCell(0);
	      cell.setCellValue("车牌号");
	      cell.setCellStyle(style);
	 
	      cell = row.createCell(1);
	      cell.setCellValue("时间");
	      cell.setCellStyle(style);
	 
	      cell = row.createCell(2);
	      cell.setCellValue("速度 (km/h)");
	      cell.setCellStyle(style);
	 
	      cell = row.createCell(3);
	      cell.setCellValue("里程(km)");
	      cell.setCellStyle(style);
	 
	      cell = row.createCell(4);
	      cell.setCellValue("定位");
	      cell.setCellStyle(style);
	      
	      cell = row.createCell(5);
	      cell.setCellValue("状态");
	      cell.setCellStyle(style);
	      List<CarLocation> cls =new ArrayList<CarLocation>();
	      GuiJiFindLocaltionList gList = new GuiJiFindLocaltionList();
	      for (int i=0; i<maplist.size();i++) {
	    	  CarLocation cl= new CarLocation();
			Row r=maplist.getRow(i);
			
			String acc = r.get("state_of_vehicle");
			if(!Checker.isEmpty(acc)){
				acc = acc.replaceAll(",", "','");
				acc = "'"+acc+"'";
				acc = gList.getACCState(db, acc);
			}else{
				acc = "";
			}
			
			
			String alarm = r.get("alarm_status");
			if(!Checker.isEmpty(alarm)){
				alarm = alarm.replaceAll(",", "','");
				alarm = "'"+alarm+"'";
				alarm = gList.getAlarmName(db, alarm);
			}else {
				alarm = "";
			}

			
			cl.setLicense_plate_number(r.get("license_plate_number"));
			cl.setPositioning_time(r.get("positioning_time"));
			cl.setSpeed(r.get("speed"));
			cl.setCurrent_mileage(r.get("current_mileage"));
			cl.setLocation(r.get("location"));
			cl.setCar_state(acc+" "+alarm);
			cls.add(cl);
		}
	      for (int i = 0; i < cls.size(); i++) {
	          row = sheet.createRow((int) i + 1);
	          CarLocation cl= cls.get(i);
	          row.createCell(0).setCellValue(cl.getLicense_plate_number());
	          row.createCell(1).setCellValue(cl.getPositioning_time());
	          row.createCell(2).setCellValue(cl.getSpeed());
	          row.createCell(3).setCellValue(cl.getCurrent_mileage());
	          row.createCell(4).setCellValue(cl.getLocation());
	          row.createCell(5).setCellValue(cl.getCar_state());
	        }
	      Random r=new Random();
//	      String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+r.nextInt(100);
	      String fileName = cph+"轨迹数据";

	      ByteArrayOutputStream os = new ByteArrayOutputStream();
	      InputStream is;
	      BufferedInputStream bis = null;
	      BufferedOutputStream bos = null;
	      try {
	      wb.write(os);
	      byte[] content = os.toByteArray();
	      is = new ByteArrayInputStream(content);
	      response.reset();
	      response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setHeader("Content-Disposition", "attachment;filename="
			      + new String((fileName + ".xls").getBytes(), "iso-8859-1"));
		
	      ServletOutputStream out = response.getOutputStream();
	 
	      
	        bis = new BufferedInputStream(is);
	        bos = new BufferedOutputStream(out);
	        byte[] buff = new byte[2048];
	        int bytesRead;
	        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
	          bos.write(buff, 0, bytesRead);
	        }
	      } catch (Exception e1) {
				e1.printStackTrace();
			}finally {
	        if (bis != null)
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        if (bos != null)
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	      }
		return null;
	}

}
