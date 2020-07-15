function showMonthlyFee(){
  var quantity = document.getElementsByName("ws_alipay_order.form.quantity")[0].value="";
  var total_fee = document.getElementsByName("ws_alipay_order.form.total_fee")[0].value="";
  var monthly_fee_id = document.getElementsByName("ws_alipay_order.form.monthly_fee_id")[0].value;
  if(monthly_fee_id==0){
     var price = document.getElementsByName("ws_alipay_order.form.price")[0].value="";
  }
  doAjaxSubmit('/wwd/ws_alipay_order.form/monthly_fee_id.do')
}


function ShowTotalFee(){
	var quantity = document.getElementsByName("ws_alipay_order.form.quantity")[0].value;

	var price = document.getElementsByName("ws_alipay_order.form.price")[0].value;
	
    var total=quantity*price;
	if(new Number(total)>new Number(999999.99)){
		document.getElementsByName("ws_alipay_order.form.total_fee")[0].value="";
		alert("总金额超出限制");
		return;
	}else{
		document.getElementsByName("ws_alipay_order.form.total_fee")[0].value=total;
	}
    
}
