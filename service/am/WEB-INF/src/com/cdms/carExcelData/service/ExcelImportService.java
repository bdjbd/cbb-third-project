package com.cdms.carExcelData.service;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdms.carExcelData.action.DefaultImportDataAction;
import com.fastunit.context.ActionContext;

/**
 * excel数据导入接口
 * @author guorenjie
 *
 */
public interface ExcelImportService
{
	//Logger log = LoggerFactory.getLogger(ExcelImportService.class);
	/**
	 * 根据类名反射类  调用对应的Action
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public Class<?> newTclass(String className) throws Exception; 
    /**
     * 保存数据到DB
     * @param ac
     * @param file
     * @param className
     * @param import_year
     * @param import_rule
     * @return
     */
	public ActionContext saveDB(ActionContext ac,File file,String className,String import_year,String import_rule,String menuname);     
          
    

}
