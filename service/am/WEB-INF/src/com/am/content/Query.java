package com.am.content;
/** * @author  作者：yangdong
 * @date 创建时间：2016年4月13日 下午6:25:26
 * @version 
 * @parameter  
 * @since  
 * @return
 */

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionContextHelper;
import com.fastunit.framework.config.AppConfig;
import com.fastunit.framework.session.FastUnitSession;
import com.fastunit.support.Action;
import com.fastunit.view.unit.UnitFactory;
import com.fastunit.view.unit.component.QueryUnit;
import com.fastunit.view.unit.util.UnitUtil;

import java.util.List;

public class Query implements Action
{
	  @Override
	public ActionContext execute(ActionContext ac)
	    throws Exception
	  {
	    if (AppConfig.isResetPageNumber()) {
	      String domain = ActionContextHelper.getActionDomain(ac);
	      String unitId = ActionContextHelper.getActionUnitId(ac);
	      Unit unit = UnitFactory.getUnitWithoutClone(domain, unitId);
	      if (unit instanceof QueryUnit) {
	        FastUnitSession session = ActionContextHelper.getSession(ac);

	        List listUnits = ((QueryUnit)unit).getListUnits();
	        for (int i = 0; i < listUnits.size(); ++i) {
	          session.getUnitSettings(UnitUtil.getKey(domain, (String)listUnits.get(i)))
	            .clear();
	        }
	      }
	    }
	    String menCode = (String) ac.getSessionAttribute("menucodes");
	    System.out.println(menCode);
	    
	    ac.getActionResult().setUrl("/am_bdp/am_content_assembly.do?m=s&menucode="+menCode);
	    
//	    ac.setSessionAttribute("menucode",menuCode);
	    
//	    ac.getActionResult().setScript();
	    
	    return ac;
	  }
}
