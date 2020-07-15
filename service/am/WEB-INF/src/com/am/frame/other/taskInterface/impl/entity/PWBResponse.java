package com.am.frame.other.taskInterface.impl.entity;

import java.util.List;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 
 <!-- 返回数据 如果返回不成功如何处理？增加计划任务，重复执行，直至成功为止  -->
<?xml version="1.0" encoding="UTF-8"?>
<PWBResponse>
	<transactionName>SEND_CODE_RES</transactionName>
	<code>0</code>
	<description>成功</description>
	<orderResponse>
		<order>
			<certificateNo>330182198804273139</certificateNo>
			<linkName>庄工</linkName>
			<linkMobile>13625814109</linkMobile>
			<orderCode>201308120000045137</orderCode><智游宝订单号>
			<orderPrice>1</orderPrice>
			<payMethod>vm</payMethod>支付方式
			<assistCheckNo>00055359</assistCheckNo>辅助码
			<src>interface</src>
			<ticketOrders>
				<ticketOrder>
					<orderCode>t20141204002226</orderCode><第三方订单号>
					<credentials>              实名制的必填，非实名制可以不填
						<credential>
							<name>帅哥</name> （真实姓名）
							<id>330182198804273139</id> (实名制商品需要传多个身份证,数量跟门票数量一致）
						</credential>
					</credentials>
					<totalPrice>1</totalPrice>
					<price>1</price>
					<quantity>2</quantity>
					<occDate>2014-12-09 00:00:00</occDate>
					<goodsCode>20140331011721</goodsCode>
					<goodsName>商品名称</goodsName>
					<remark>helloworld</remark>
				</ticketOrder>
			</ticketOrders>
		</order>
	</orderResponse>
</PWBResponse>
 
 
 
 ***/
public class PWBResponse {

	private String transactionName;//>SEND_CODE_RES</transactionName>
	private String code;//>0</code>
	private String description;//>成功</description>
	private List<Order> orderResponse;//>
	
}
