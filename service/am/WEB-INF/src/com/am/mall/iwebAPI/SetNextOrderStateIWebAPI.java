package com.am.mall.iwebAPI;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年11月18日
 * @version 
 * 说明:<br />
 * 订单状态修改WEBAPI<br />
 * ORDERID 订单ID
 * STATEVALUE 需要执行Action的状态值：
 * ACTONCODE  动作编号
 * <ul>
 *<li> 购物车结算  | 1 |1 | 动作编号=101;              </li>
 *<li> 直接下单购买|   |1 |                            </li>
 *<li> 订单确认    |   |  |                            </li>
 *<li> 订单支付    |   |  |                            </li>
 *<li> 支付完成    | 1 |3 | 动作编号=102;              </li>
 *<li> 派单（自动）| 3 |30|                            </li>
 *<li> 派单（自动）| 32|30|                            </li>
 *<li> 手工派单    | 31|30|                            </li>
 *<li> 接单-接受   | 30|4 | 动作编号=301;              </li>
 *<li> 接单-拒绝   | 30|31| 动作编号=302;              </li>
 *<li> 接单-超时   | 30|32| 动作编号=303;              </li>
 *<li> 订单配送    | 4 |5 | 出库作业=true;动作编号=401;</li>
 *<li> 订单收货    | 5 |6 | 动作编号=501;              </li>
 *<li> 订单服务完成| 6 |7 |                            </li>
 *<li> 确认交易完成| 7 |8 |                            </li>
 *<li> 订单评价    | 8 |81|                            </li>
 *<li> 订单追评    | 81|82|                            </li>
 *<li> 订单完成    | 82|  |                            </li>
 *<li> 取消订单    | 3 |9 |                            </li>
 *<li> 订单退货申请| 4 |91| 动作编号=402;              </li>
 *<li> 订单退货申请| 5 |91| 动作编号=502;              </li>
 *<li> 订单退货成功| 91|9 |                            </li>
 *<li> 订单删除    | 82|10|                            </li>
 *<li> 订单删除    | 9 |10|                            </li>
 * </ul>
 * 
 * URL:http://127.0.0.1/p2p/com.am.mall.iwebAPI.SetNextOrderStateIWebAPI.do?ORDERID=订单ID&STATEVALUE=流程状态值
 */
public class SetNextOrderStateIWebAPI implements IWebApiService {

	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String result="";
		DB db=null;
		try{
			//获取订单信息
			String orderId=request.getParameter("ORDERID");
			//目标状态值
			String stateValue=request.getParameter("STATEVALUE");
			//动作编号
			String stateActonCode=request.getParameter("ACTONCODE");
			
			
			db=DBFactory.newDB();
			
			/**一，获取当前流程状态**/
			//订单信息
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			
			//订单对应的商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
			
			Map<String,String> otherParam=new HashMap<String,String>();
			
			if(!Checker.isEmpty(stateActonCode)){
				otherParam.put(OrderFlowParam.STATE_ACTON_CODE, stateActonCode);
			}
			
			/**2,执行当前流程状态配置动作，跳转流程**/
			result=StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),stateValue,order.getId(),otherParam);
			
		}catch(Exception e){
			e.printStackTrace();
			
			try{
				JSONObject msg=new JSONObject();
				
				//构造返回值
				msg.put("CODE", 0);
				msg.put("ERRCODE",1);
				msg.put("SUCCESS",false);
				msg.put("MSG",e.getMessage());
				msg.put("STATE","");
				msg.put("ORDERID","");
				
				result=msg.toString();
				
			}catch(Exception je){
				je.printStackTrace();
			}
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}

}
