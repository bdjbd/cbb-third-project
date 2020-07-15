package com.am.mall.action;

import java.io.File;

import com.am.frame.util.FileCopyUtil;
import com.fastunit.MapList;
import com.fastunit.Path;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年7月6日
 * @version 
 * 说明:<br />
 * 商品特色设置保存类
 */
public class CommodotiyFeatureSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		super.doAction(db, ac);
		//代金券
		String vouchersFaceValue=ac.getRequestParameter("mall_commodity_feature.form.vouchers_face_value");
		//商品ID
		String comId=ac.getRequestParameter("mall_commodity_feature.form.id");
		
		if(!Checker.isEmpty(vouchersFaceValue)){
			//在资源里面查询资源，并保持到商品对应的列表图中
			String querySQL=" SELECT serverroute FROM mjyc_resourcemanagement WHERE code='djq_"+vouchersFaceValue+"' ";
			
			MapList map=db.query(querySQL);
			if(!Checker.isEmpty(map)){
				Row row=map.getRow(0);
				String serverroute=row.get("serverroute");
				
				//将文件移动到数据对于的目录下
				//更新文件
				
				String sourceFilePath=Path.getRootPath()+serverroute;
				File sourceFile=new File(sourceFilePath);
				
				String saveTargetFile="/files/MALL_COMMODITY/"+comId+"/bdp_listimage";
				String targetFilePaht=Path.getRootPath()+saveTargetFile;
				//+"/"+sourceFile.getName();
				File targetFile=new File(targetFilePaht);
				if(!targetFile.exists()){
					targetFile.mkdirs();
				}
				
				targetFilePaht+="/"+sourceFile.getName();
				saveTargetFile+="/"+sourceFile.getName();
				targetFile=new File(targetFilePaht);
				
				new FileCopyUtil().forJava(sourceFile, targetFile);
			
				
				db.execute("UPDATE MALL_COMMODITY SET listimage =? WHERE id=? ",
						new String[]{
						saveTargetFile,comId
				}, 
						new int[]{
						Type.VARCHAR,Type.VARCHAR
				});
				
				
			}
		
		}
		
	}
	
}
