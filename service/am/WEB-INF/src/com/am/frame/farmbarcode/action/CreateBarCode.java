package com.am.frame.farmbarcode.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code39Encoder;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.WideRatioCodedPainter;
import org.jbarcode.util.ImageUtil;

import com.fastunit.Path;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月20日 下午2:28:41
 * @version 生成条码
 */
public class CreateBarCode {
	public String createBarCode(String barcode){
		String imgPath = "";
		try  
		{  
			JBarcode localJBarcode = new JBarcode(Code39Encoder.getInstance(), WideRatioCodedPainter.getInstance(), BaseLineTextPainter.getInstance());  
//			localJBarcode.setBarHeight(25);//生成的条形码的高度
			localJBarcode.setCheckDigit(false);//是否生成校验码
			localJBarcode.setShowCheckDigit(false);  //是否显示校验码
			//生成条形码图片
			BufferedImage localBufferedImage = localJBarcode.createBarcode(barcode);  
			//当前服务器地址
			imgPath = "/barcodes/"+"/"+barcode+"/"+barcode+".png";
			File file=new File(Path.getRootPath()+"/barcodes/"+"/"+barcode);    
			if(!file.exists())    
			{    
			    file.mkdirs();
			}  
			  try  
			  {  
				FileOutputStream localFileOutputStream = new FileOutputStream(Path.getRootPath()+imgPath);
				//生成png图片
				ImageUtil.encodeAndWrite(localBufferedImage,"png", localFileOutputStream, 96, 96);
				localFileOutputStream.close();  
			  }  
		    catch (Exception localException)  
		    {  
		      localException.printStackTrace();  
		    }  
		  
		}  
		catch (Exception localException)  
		{  
		  localException.printStackTrace();  
		} 
		return imgPath;
	}
}
