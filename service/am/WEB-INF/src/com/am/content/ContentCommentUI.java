package com.am.content;
/** * @author  作者：yangdong
 * @date 创建时间：2016年4月15日 下午3:59:48
 * @version 是否显示评论UI
 */
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class ContentCommentUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		DB db=DBFactory.getDB();
		
		String menuid=ac.getRequestParameter("menucode");
		String m=ac.getRequestParameter("m");
		
		if(Checker.isEmpty(menuid)){
			menuid=(String)ac.getSessionAttribute("am_content_list.menucode");
		}
		
		
		//查看模式下查询是否显示评论
		String showCommentSQL = "SELECT show_comment FROM contentmenu WHERE key='"+menuid+"'";
		MapList showCommentList = db.query(showCommentSQL);

		if(!Checker.isEmpty(showCommentList) && !Checker.isEmpty(m)){
			//查看模式下查询是否显示评论,0=不显示，1=显示
			if(m.equals("s") && showCommentList.getRow(0).get(0).equals("0")){
				unit.getElement("comment_list").setShowMode(ElementShowMode.REMOVE);
			}
		}
		
		return unit.write(ac);
	}

}

