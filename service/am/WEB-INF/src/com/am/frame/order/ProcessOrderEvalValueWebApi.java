package com.am.frame.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.mall.order.OrderManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月24日
 * @version 
 * 说明:<br />

1、商品表增加平均评星数、总评论数，字段名同店铺表
商品表、店铺表、用户表增加：晒图数、差评数、好评数
晒图数，只要有图就+1
差评数，只要1星就+1
好评数，只要4星及以上就+1

评论表增加字段，晒图（有=1，无=0）

2、评论时向商品表及店铺表写值：
商品表
平均评星数=((平均评星数*总评论数)+当前评星数)/(总评论数+1)
总评论数++;

店铺表
平均评星数=((平均评星数*总评论数)+当前评星数)/(总评论数+1)
总评论数++;

 * 
 * 
 */
public class ProcessOrderEvalValueWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
//		evalId:evalId,
//        memberId:memberId
		//评论ID
		String evalId=request.getParameter("evalId");
		//会员ID
		String memberId=request.getParameter("memberId");
		
		OrderManager orderManger=new OrderManager();
		
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			
			orderManger.processEval(db,evalId,memberId);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		
		
		return null;
	}

}
