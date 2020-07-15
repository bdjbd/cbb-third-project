function setRow(obj){
	var row = $(obj).getParent('tr');
	var d = row.getElement('select[name=actions.i5]').value;
	row.getElement('select[name=actions.to_tid]').setStyle('display',(d=='1' || d=='3')?'block':'none');
	row.getElement('select[name=actions.skip_to_tid]').setStyle('display',(d=='3')?'block':'none');
}

function setNoticeType(type){
  if(type!='0'){
    $('wait').setStyle('visibility','visible');
  }else{
    $('wait').setStyle('visibility','hidden');
    $fn('manualtask.v4').value='';
  }
  if(type=='2'){
    $('loop').setStyle('visibility','visible');
  }else{
    $('loop').setStyle('visibility','hidden');
    $fn('manualtask.v5').value='';
  }
}
setNoticeType($fnv('manualtask.v3'));