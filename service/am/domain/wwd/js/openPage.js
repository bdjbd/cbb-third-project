function openPage(){
	window.open ('/openPage.html','newwindow','height=900,width=1020,top=10,left=300,toolbar=no,menubar=no,scrollbars=yes,resizable=yes,location=no,status=no'); 

}

function setPwd(){
   var account_belong = document.getElementsByName("ws_public_accounts.form_scan.account_belong")[0].value;
   if(account_belong==1){
      document.getElementsByName("ws_public_accounts.form_scan.password")[0].value="******";
   }
  
}