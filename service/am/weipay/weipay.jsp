<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>微信支付</title>
<script src="js/jquery.min.js"></script>
<script src="js/jquery.qrcode.min.js"></script>
<style>
html{
	height:100%;
	width:100%;
	padding:0px;
	margin:0px;
}

body{
	height:100%;
	width:100%;
	padding:0px;
	margin:0px;
}
.container{
	height:100%;
	width:100%;
	padding:0px;
	margin:0px;
	text-align:center;
	margin-left:auto;
	margin-right:auto;
}
.body_content{
	width:900px;
	height:430px;
	margin-top:50px;
	border: 8px solid #b1b1b1;
	margin-right: auto;
    margin-left: auto;
}
.title{
	height: 20px;
    padding: 15px 20px;
    background: #4b5b78;
}
.title_p{
	line-height:1px;
    background: #4b5b78;
	color:white;
	vertical-align: middle;
	font-weight: 700;
}
.tips{
  font-size:22px;
  font:22px/150% Arial,Verdana,"\5b8b\4f53";
  color: #666;
  white-space: nowrap;
}
.money{
 color:red;
}

.pay_phone_img{
	width:260px;
	height:auto;
}
.confirm_btn{
    background-color: #1a9c1a;
    border: none;
    height: 30px;
    padding: 12px 26px;
    font-size: 16px;
    color: white;
    border-radius: 5px;
    margin: 8px;
    vertical-align: middle;
    line-height: 10px;
}

.cancel_btn{
	background-color: #ff7573;
    border: none;
    height: 30px;
    padding: 12px 26px;
    font-size: 16px;
    color: white;
    border-radius: 5px;
    margin: 8px;
    vertical-align: middle;
    line-height: 10px;
}

</style>
</head>
<body>

<div class="container" >
	
	<div class="body_content">
		<div class="title">
			<p class="title_p">微信支付</p>
		</div>
		<div>
			<table style="width:100%;padding:10px;">
				<colgroup width="40%"></colgroup>
				<colgroup width="30%"></colgroup>
				<colgroup width="30%"></colgroup>
				<tr>
					<td style="padding-right:10px;">
					
						<p><span class="tips">订单号：</span><%=request.getParameter("payId")%></p>
						<p><span class="tips">支付金额：</span><span class="money"><%=request.getParameter("payMoney")%>元</span></p>
						<p><%=request.getParameter("commodityName")%></p>
					</td>
					<td id="code"></td>
					<td>
						<img class="pay_phone_img" src="images/direct-weixin-phone.png" />
					</td>
				</tr>
				<tr>
					<td colspan=3 style="text-align: center;">
						<button onclick="confirmPay()" class="confirm_btn">支付完成</button>
						<button onclick="cancelPay()" class="cancel_btn">取消支付</button>
					</td>
				</tr>
			</table>
		</div>

	</div>
	
</div>




<script>
$("#code").qrcode({ 
    render: "table", //table方式 
    width:250, //宽度 
    height:250, //高度 
    text:'<%=request.getParameter("weiPayQrcode")%>' //任意内容 
});

function confirmPay(){
	window.close();
}

function cancelPay(){
	history.back(3)
}

</script>

</body>
</html>