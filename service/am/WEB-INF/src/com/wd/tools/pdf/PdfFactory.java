package com.wd.tools.pdf;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 *   说明:
 * 		PDF文件工厂类
 *   @creator	岳斌
 *   @create 	2012-11-22 
 *   @revision	$Id
 */
public class PdfFactory {
	/**
	 *  根据数据源在指定的目录下创建PDF文件
	 * @param sql   SQL查询语句
	 * @param filePath  文档输入路径，绝对路径
	 * @param title 	PDF文件中表格标题
	 * @param pafTableHead  PDF文件中表格表头
	 * @param pdfOrientation 纸张方向 默认纵向
	 * @return   PDF文件查是否生成 
	 */
	public static boolean createPDFByDataSource(
			String sql ,String filePath,String title,String pafTableHead,String pdfOrientation){
		//查询数据结构集合
		MapList dataList=null;
		try {
			//访问数据库并查询
			DB db=DBFactory.getDB();
			dataList=db.query(sql);
			//访问完后关闭数据库连接
			db.close();
		} catch (JDBCException e) {
			e.printStackTrace();
			return false;
		}
		//
		if(dataList==null||dataList.size()<1){
			return false;
		}
		//在PDF文件中创建表格的列数
		int column=0;
		FileOutputStream fos=null;
		Document document=null;
		BaseFont bfChinese=null;
		//表体字体
		Font tableBodyFont=null;
		//表头字体
		Font tableHeadFont=null;
		//表标题字体
		Font tableTitleFont=null;
		//表头
		JSONArray tableHead=null;
		try {
		//判断表头是否正确
		if(pafTableHead==null||pafTableHead.trim().equals("")){
			return false;
		}	
		//初始化表头数组
		tableHead=new JSONArray(pafTableHead);
		//表列数
		column=tableHead.length();
		//判断SQL与表头字符是否一一对应
		if(column!=dataList.getRow(0).size()){
			return false;
		}
		//
		float[] columns=new float[column];
		for(int i=0;i<column;i++){
			columns[i]=Float.parseFloat(tableHead.getJSONObject(i).getString("WIDTH"));
		}
		File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
		// 如果文件夹不存在则创建
		if (!file.exists()) {
			file.mkdirs();
		}
		fos = new FileOutputStream(filePath);
		// 创建文档，大小为A4，纵向，如果要使用横向，使用这个方法new Document(PageSize.A4.rotate(),50,50,50,50);
		//后面数字表示页边距，次序为左右上下 为float类型
		if("horizontal".equals(pdfOrientation)){
			document=new Document(PageSize.A4.rotate(), 15,  15, 30,  15);
		}else{
			document = new Document(PageSize.A4, 15, 15, 30, 15);
		}
		//获取PdfWriter的实例，但基本用不到此实例
//		PdfWriter pefWriter=PdfWriter.getInstance(document,fos);
		PdfWriter.getInstance(document,fos);
		/**汉字处理
		*   使用此方法产生一个Font是为了解决汉字无法现在问题，其中/SIMYOU.TTF表示要使用的字体，
		*	在此方法中需要将SIMYOU.TTF拷贝到项目src目录下,也可以从系统中直接读取（win7）如：c:\\Window\\Fonts\\SIMYOU.TTF
		*/
//		BaseFont baseFont = BaseFont.createFont("/SIMYOU.TTF",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);    
//		Font font = new Font(baseFont);
		//使用此方法创建font是为了解决汉字无法显示问题，使用此方和还需要itext-asian.jar包的支持
		bfChinese= BaseFont.createFont( "STSongStd-Light" ,  "UniGB-UCS2-H" ,  false );
		//此font无法显示中文 Font.NORMAL
		//表体字体
		tableBodyFont=new Font(bfChinese,10,Font.COURIER,new Color(60,60,60));
		//设置表头字体
		tableHeadFont=new Font(bfChinese,12,Font.BOLD,new Color(0,0,0));
		//表标题字体
		tableTitleFont=new Font(bfChinese,16,Font.BOLD,new Color(0,0,0));
		document.open();
		//创建一个Paragraph,用来填充标题
        Paragraph tableTitle=new Paragraph(title,tableTitleFont);
        //设置报表标题居中显示
        tableTitle.setAlignment(Element.ALIGN_CENTER);
        //设置段前间距
//        tableTitle.setSpacingBefore(10);
        //设置段后间距
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);
        //创建表格,columns为一个float数组，数组大小为表格列数，单个数组大小表示宽度，按总和的百分比分配
        PdfPTable table=new PdfPTable(columns);
        //创建一个单元格用来填充表格
//        PdfPCell headCell=new PdfPCell(tableHead);
        // 设置单元格跨行
//        headCell.setColspan(column);
        //设置单元格内内容水平居中  
//        headCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        //设置表头单元格背景色
//        headCell.setBackgroundColor(new Color(200,200,200));
        //将表头添加到table中
//        table.addCell(headCell);
        //创建表头
        for(int j=0;j<dataList.getRow(0).size();j++){
            PdfPCell head=new PdfPCell(new Paragraph(
            			//使用FontFactory.getFont()得到的字体无法显示中文
//            			dataList.getRow(0).getKey(j),FontFactory.getFont(FontFactory.COURIER_BOLD,15,new Color(200,200,0))));
            	tableHead.getJSONObject(j).getString("NAME"),tableHeadFont));
            	//设置此单元水平居中
            	head.setHorizontalAlignment(Element.ALIGN_CENTER);
            	//设置此单元垂直居中
            	head.setVerticalAlignment(Element.ALIGN_CENTER);
            	//将此单元格添加到表中
            	table.addCell(head);
            }
        
        for(int i=0;i<dataList.size();i++){
        	Row row=dataList.getRow(i);
        	//添加表体
        	for(int j=0;j<row.size();j++){
        		PdfPCell bodyCell=new PdfPCell(new Paragraph(row.get(j),tableBodyFont));
            	table.addCell(bodyCell);
        	}
        }
        //将Table添加到document中
        document.add(table);
        //关闭文档
        document.close();
        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}catch (DocumentException e) {
			e.printStackTrace();
			return false;
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch(JSONException e){
			e.printStackTrace();
			return false;
		} 
		return true;
	}
}
