
window.onload=removechilds;
    
	function removechilds(){    
		var msg_type = document.getElementsByName("ws_auto_replay_rule.form_scan.msg_type")[0].value;
        var vTable = document.getElementById("u_g_ws_auto_replay_rule.form_scan");//取得table
	    var vTd = vTable.childNodes[4];
		if(msg_type==1){
		   //显示行对象
		   vTd.childNodes[4].style.display="";
		   //隐藏行对象
		   vTd.childNodes[5].style.display="none";
		   vTd.childNodes[6].style.display="none";
		   vTd.childNodes[7].style.display="none";
		   vTd.childNodes[8].style.display="none";
		   vTd.childNodes[9].style.display="none";
		}else if(msg_type==2){
           vTd.childNodes[5].style.display="";
           //隐藏行对象
           vTd.childNodes[4].style.display="none";
		   vTd.childNodes[6].style.display="none";
		   vTd.childNodes[7].style.display="none";
		   vTd.childNodes[8].style.display="none";
		   vTd.childNodes[9].style.display="none";

	    }else if(msg_type==6){
		   //alert(msg_type);
           vTd.childNodes[6].style.display="";
		   vTd.childNodes[7].style.display="";
		   vTd.childNodes[8].style.display="";
		   vTd.childNodes[9].style.display="";
           //隐藏行对象
           vTd.childNodes[4].style.display="none";
		   vTd.childNodes[5].style.display="none";
	   }
		
	}
