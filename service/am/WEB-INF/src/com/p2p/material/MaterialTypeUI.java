package com.p2p.material;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * 物资分类UI
 * @author Administrator
 *
 */
public class MaterialTypeUI implements UnitInterceptor{

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList map=unit.getData();
		
		if(!Checker.isEmpty(map)){//查询，修改
			
			String typeCode=map.getRow(0).get("typecode");
			if(!Checker.isEmpty(typeCode)&&typeCode.contains("_")){
				String pre=typeCode.substring(0, typeCode.lastIndexOf("_"));
				String suff=typeCode.substring(typeCode.lastIndexOf("_")+1, typeCode.length());
				unit.getElement("pre_typecode").setDefaultValue(pre);
				unit.getElement("typecode_suff").setDefaultValue(suff);
			}else{
				unit.getElement("typecode_suff").setDefaultValue(typeCode);
			}
//			
		}else{//新增
			String id=ac.getRequestParameter("parentid");
			String sql="SELECT typecode FROM p2p_materialsType WHERE id='"+id+"'";
			MapList pMap=DBFactory.getDB().query(sql);
			
			if(!Checker.isEmpty(pMap)){
				unit.getElement("pre_typecode").setDefaultValue(pMap.getRow(0).get("typecode"));
			}
		}
		
		return unit.write(ac);
	}
	
}
