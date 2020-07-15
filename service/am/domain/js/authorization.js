function  authorizationCheck(obj,temp1,temp2,temp3){
     
	  var obj1 = obj.parentNode.parentNode.id;

	  var v1 = document.getElementsByName(temp1)[obj1].value;
	  var v2 = document.getElementsByName(temp2)[obj1].value;
	  var v3 = document.getElementsByName(temp3)[obj1].value;	
	  if(v3 ==undefined || v3==""){
			doLink(' /app/authorization.form.do?m=e&abdp_terminalmanage.list.tid='+v1+'&imei='+v2);
	  } else {
		   alert("该设备已授权不可再次授权！");
	  }
}