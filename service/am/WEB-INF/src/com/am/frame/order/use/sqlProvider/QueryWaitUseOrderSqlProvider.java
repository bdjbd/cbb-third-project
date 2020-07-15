package com.am.frame.order.use.sqlProvider;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月26日
 * @version 
 * 说明:<br />
 * 订单待使用sqlprovidr ，如果查询条件为空，修改查询sql
 * 
 */
public class QueryWaitUseOrderSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		String sql=
				"SELECT  m.membername,st.orgcode,od.* ,trim(to_char(od.saleprice ,'999999990D99')) AS saleprices, "+
				" trim(to_char(od.totalprice  ,'999999990D99')) AS totalprices "+
				" FROM  mall_MemberOrder AS od  "+
				" LEFT JOIN mall_Commodity AS cmd ON cmd.id=od.commodityid "+
				" LEFT JOIN mall_store AS st ON st.id=cmd.store  "+
				" LEFT JOIN am_member AS m ON od.memberid=m.id  "+
				" WHERE st.orgcode  LIKE '$O{orgid}%'  ";
		
		Row queryRow=FastUnit.getQueryRow(ac, "am_bdp", "mall_memberorder_use_cusmore.query");
		
		if(queryRow!=null&&Checker.isEmpty(queryRow.get("custom_code"))){
			//消费吗
			//如果消费码为空，则出现不到数据
			sql+=" AND 1=2 ";
		}else if(queryRow==null){
			sql+="  AND 1=2 AND od.orderstate IN ('320')  $Q{MALL_MEMBERORDER,od}";
		}else{
			sql+=" AND od.orderstate IN ('320')  $Q{MALL_MEMBERORDER,od}";
		}
		
		return sql;
	}

}
