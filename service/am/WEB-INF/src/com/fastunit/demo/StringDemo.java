package com.fastunit.demo;
/** * @author  作者：yangdong
 * @date 创建时间：2016年3月25日 下午5:01:11
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class StringDemo {

	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		
		//SELECT AET.ID,'http://192.168.1.108:8080'||AET.ICONPATH AS ICONPATH, 
		//AET.SNAME,AOE.USESTATUS,AET.CASH,  
		//TO_CHAR(AOE.USEDATETIME,'YYYY-MM-DD HH:MI:SS') AS USEDATETIME,
		//AET.SCOREVALUE,  TO_CHAR(AOE.EXPIRED,'YYYY-MM-DD HH:MI:SS') AS EXPIRED,  
		//TO_CHAR(AOE.GETDATETIME,'YYYY-MM-DD HH:MI:SS') AS GETDATETIME  
		//FROM AM_ORGELECTTICKER AOE  INNER JOIN AM_ETERPELECTTICKET AET 
		//ON AET.ID=AOE.ETERPELECTTICKETID  
		//WHERE AOE.AM_MEMBERID='dabfa38f-0bcf-4f8c-b75d-231afbaf5d93'  
		//AND AOE.USESTATUS='2'  AND AOE.EXPIRED + '10DAY' &GT;= NOW() ORDER BY AOE.GETDATETIME DESC 

	}

}
