var myDialog;

function closeDialog(){
	var parent = window.parent;
	parent.myDialog.close();
}


/**�����б�����**/
function addListData(){
	var defaultValue=document.getElementsByName("lxny_anti_poverty_funds_record.form.anti_amounts")[0].value;
	doListAdd('lxny_anti_pfunds_record_dateils.list')
	//doListAdd('lxny_anti_pfunds_record_dateils.list');
	var length=$$("input[name='lxny_anti_pfunds_record_dateils.list.member_loginaccount']").length;
	
	if(length>0){
		$$("input[name='lxny_anti_pfunds_record_dateils.list.anti_amounts']")[length-1].value=defaultValue;
	}
}


/**�б�ѡ���û�,����ѡ��**/
function selectListUser(s,valueEle){   
	//var bookid = $$('input[name=showmodaldialog.bookid]')[0].value;
	//s ��ǰ�����name��name ��Ԫid backname ����Ԫ��id
	var parentid = s.get('name'); 
	var type=s.tagName;
	var index=0;
	if($$(type+'[name='+s.get('name')+']').length>1){
		index=s.parentNode.parentNode.id
	};

	//window.open(,
	//	'',
	//	'height=650,width=1110,top=15,left=20,scrollbars =yes,toolbar=no,menubar=no'); 
	
	var url='/am_bdp/select_am_member.do?parentid='+parentid+'&index='+index+"&valueEle="+valueEle
		+'&clear=am_bdp.select_am_member.query&m=s';
	myDialog = new MooDialog.Iframe(url,{
		title: '',
		'class': 'MooDialog myDialog'
	});

}


function selectUser(){
	//��ǰ������listҳ����������
	var selectArray=$$('input[name=_s_select_am_member.list]');
	//��ǰ������listҳ���������ݵ�id
	var idArray=$$('input[name=select_am_member.list.id]');
	var loginAccountArray=$$('input[name=select_am_member.list.loginaccount]');
	//��ȡ�������еĴ���session�е�ֵ
	var parentid=$$('input[name=select_am_member.list.parentid]')[0].value;
	var index=$$('input[name=select_am_member.list.index]')[0].value;
	var eleVale=$$('input[name=select_am_member.list.valueele]')[0].value;
	//��ȡ������ʾ����
	var nameArray=$$('input[name=select_am_member.list]');
	var valueSelects=parentid.split(".");
	var valueParentEle=valueSelects[0]+"."+valueSelects[1]+"."+eleVale;

	var finished = false;
	var parent = window.parent;

	for(var i=0;i<selectArray.length;i++){
		if(selectArray[i].value=='1'){
			parent.document.getElementsByName(parentid)[index].value=loginAccountArray[i].value;
			parent.document.getElementsByName(valueParentEle)[index].value=idArray[i].value;
			
			finished = true;
			try{
				parent.document.getElementsByName(parentid)[index].fireEvent("onchange"); 
				parent.document.getElementsByName(parentid)[index].onchange(); 
			}catch(e){}
			break;
		}
	}
	if(finished){
		closeDialog();
	}
}
