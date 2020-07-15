function setNextUsers(){
	var users='';
	var msg='';
	var select = $$('input[name=_s_flow.nextusers.list]');
	var id = $$('input[name=flow.nextusers.list.userid]');
	var name = $$('input[name=flow.nextusers.list.username]');
	for(var i=0;i<select.length;i++){
		if('1'==select[i].value){
			users+=id[i].value+',';
			msg+=name[i].value+'\n';
		}
	}
	if(users){
		if(confirm(($fn('flow.nextusers.query.orgid')?Lang.FlowChoiceUsers:'按以下顺序会签，你确认吗？')+'\n'+msg)){
			var parent = window.parent;
			parent.document.getElement('input[name=_flow_next_users]').value=users.substring(0,users.length-1);
			parent.document.forms[0].submit();
		}
	}else{
		alert(Lang.FlowNoChoiceUsers);
	}
}
function up(src){
  var obj=$(src).getParent('tr');
  var p=obj.getPrevious();
  if(p){
    obj.inject(p,'before');
  }
}
