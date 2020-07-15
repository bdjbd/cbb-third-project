package com.cdms.org;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.adm.org.SaveOrgAction;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月19日
 * @version 说明:<br />
 *          扩展机构保存类，如果是新增机构，需要对机构进行初始化操作，增加机构系统帐号
 */
public class OrgSaveActon extends SaveOrgAction {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = ac.getTable("AORG");
		// id
		String id = table.getRows().get(0).getValue("id");
		// orgid
		String orgcode = table.getRows().get(0).getValue("orgid");
		// 父机构
		String parentid = table.getRows().get(0).getValue("parentid");
		// 得到类别
		String orgtype = table.getRows().get(0).getValue("orgtype");
		// 得到省级id
		String province = table.getRows().get(0).getValue("province");
		// 得到市级id
		String city = table.getRows().get(0).getValue("city");
		// 得到区级id
		String zone = table.getRows().get(0).getValue("zone");
		
		if(!Checker.isEmpty(orgcode)){
			//修改时不修改orgid
			table.getRows().get(0).setValue("orgid", orgcode);
			db.save(table);
			ac.getActionResult().setSuccessful(true);
			ac.getActionResult().setUrl(
					"/am_bdp/org.form.do?m=e&org.form.orgid=" + orgcode);
		}else{
			//新增时生成的orgid
			String orgid = checkRepetition(db,id, parentid);
			logger.info("生成的机构id="+orgid);
			table.getRows().get(0).setValue("orgid", orgid);
			db.save(table);
			ac.getActionResult().setSuccessful(true);
			ac.getActionResult().setUrl(
					"/am_bdp/org.form.do?m=e&org.form.orgid=" + orgid);
		}

	}
	/**
	 * @throws JDBCException 
	 * 生成不重复的机构id
	* @param     参数  
	* @return void    返回类型  
	* @throws
	 */
	public String checkRepetition(DB db,String id,String parentid) throws JDBCException{
		OrgStr oStr = new OrgStr();
		// 新增时生成的orgid
		String orgid = parentid+"_"+oStr.getOrgStr();	
		String querySql = "select count(*) from aorg where orgid='" + orgid+ "'";
		int count = 0;
		MapList mapList = db.query(querySql);
		if(!Checker.isEmpty(mapList)){
			count = mapList.getRow(0).getInt(0, 0);
			logger.info("生成的机构id="+orgid+",检查重复="+querySql+",count="+count);
			if(count>0){
				//orgid重复，重新生成
				orgid = checkRepetition(db, orgid, parentid);
			}
		}
		return orgid;
	}
}
/**
 * 机构ID后拼接的字符串
 * @author guorenjie
 *
 */
class OrgStr {  
    private static int totalCount = 0;  
    private int id;  
    public OrgStr(){  
        ++totalCount;  
        id = totalCount; 
    }  
    public String getOrgStr() {  
    DecimalFormat decimalFormat = new DecimalFormat("00000");  
    return decimalFormat.format(id);  
    }  
}