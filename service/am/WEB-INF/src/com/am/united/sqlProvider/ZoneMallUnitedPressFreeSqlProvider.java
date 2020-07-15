package com.am.united.sqlProvider;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月29日
 * @version 
 * 说明:<br />
 * 联合会会费管理
 */
public class ZoneMallUnitedPressFreeSqlProvider implements SqlProvider {


	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		String querSQL="SELECT d1.gap_name,frecord.*  "+
					" ,to_char(frecord.free_date,'yyyy-MM') AS ffree_date"+
					"  FROM  mall_united_press_free AS frecord "+
					"  LEFT JOIN ("+
					"  SELECT id,orgid,gap_name FROM mall_cooperative UNION "+
					"  SELECT id,orgid,gap_name  FROM home_farm "+
					"  ) d1 ON d1.id=frecord.united_press_free_id WHERE 1=1  ";
		
		//收费机构为当前登录人所在机构
		querSQL+=" AND frecord.f_id LIKE '"+ac.getVisitor().getUser().getOrgId()+"%' ";
		
		Row queryRow=FastUnit.getQueryRow(ac,"am_bdp", "zone_mall_united_press_free.query");
		
		
		if(queryRow!=null){
			String fName=queryRow.get("f_nameq");
			String freeDateStart=queryRow.get("free_dateq");
			String freeDateEnd=queryRow.get("free_dateq.t");
			String status=queryRow.get("statusq");
			
			if(!Checker.isEmpty(fName)){
				querSQL+=" AND d1.gap_name LIKE '%"+fName+"%' ";
			}
			if(!Checker.isEmpty(freeDateStart)&&!Checker.isEmpty(freeDateEnd)){
				querSQL+=" AND frecord.free_date<to_date('"+freeDateEnd+"','yyyy-MM') AND frecord.free_date>= to_date('"+freeDateStart+"','yyyy-MM')";
			}
			if(!Checker.isEmpty(status)){
				querSQL+=" AND frecord.status ='"+status+"' ";
			}
		}
		
		querSQL+=" ORDER BY d1.gap_name,frecord.free_date DESC ";
		
		
		return querSQL;
	}

}
