
window.onload=removechilds;
    
	function removechilds(){    
		var msg_type = document.getElementsByName("ws_fsendmess.form_scan.msg_type")[0].value;
        var vTable = document.getElementById("u_g_ws_fsendmess.form_scan");//取得table
	    var vTd = vTable.childNodes[4];
		if(msg_type==1){

		vTd.childNodes[3].style.display=""
		vTd.childNodes[4].style.display="none";
		vTd.childNodes[5].style.display="none";
		vTd.childNodes[6].style.display="none";
		vTd.childNodes[7].style.display="none";
		vTd.childNodes[8].style.display="none";
		
	}else if(msg_type==2){

		vTd.childNodes[3].style.display="none"
        vTd.childNodes[4].style.display="";
		vTd.childNodes[5].style.display="none";
		vTd.childNodes[6].style.display="none";
		vTd.childNodes[7].style.display="none";
		vTd.childNodes[8].style.display="none";

	}else if(msg_type==6){

		vTd.childNodes[3].style.display="none"
        vTd.childNodes[4].style.display="none";
		vTd.childNodes[5].style.display="";
		vTd.childNodes[6].style.display="";
		vTd.childNodes[7].style.display="";
		vTd.childNodes[8].style.display="";
	}
}
