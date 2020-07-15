package com.am.frame.member.util;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.LoadMenus;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.util.Checker;

/**
 * 后台显示前台的图集公共方法
 * @author xianlin
 *
 */

public class DataToImgUI {
	
	final Logger logger = LoggerFactory.getLogger(LoadMenus.class);
	
	// 定义一个私有构造方法
    private DataToImgUI() { 
     
    }   
    //定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
    private static volatile DataToImgUI instance;  

    //定义一个共有的静态方法，返回该类型实例
    public static DataToImgUI getIstance() { 
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (instance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (DataToImgUI.class) {
                //未初始化，则初始instance变量
                if (instance == null) {
                    instance = new DataToImgUI();   
                }   
            }   
        }   
        return instance;   
    }
	
    /**
     * 
     * @param ac
     * @param unit
     * @param table_name
     * @param width
     * @param height
     * @return
     * @throws Exception
     */
    //table_img是图片字段名
	public String intercept(ActionContext ac, Unit unit,String table_img,String width,String height) throws Exception {
		
		MapList maplist = unit.getData();
		//取出图片字段的值
		String photo = maplist.getRow(0).get(table_img);
		
		logger.debug("DataToImgUI.intercept() photo=" + photo + ";");
		
		if(!Checker.isEmpty(photo))
		{
			JSONArray jsonarray = new JSONArray(photo);
			
			String photo_img = "";
			
			for(int i=0;i<jsonarray.length();i++)
			{
				photo_img += "<img style='width:"+width+";height:"+height+";margin-left: 12px;margin-bottom: 10px;' src='"+jsonarray.getJSONObject(i).get("path").toString()+"'/>";
			}
			logger.debug("处理过的 photo_img=" + photo_img + ";");
			unit.getElement(table_img).setHtml(photo_img);
		}
		return unit.write(ac);
	}

}
