function notice(url){
  if(!$fnv('notice_hand.select')){
    alert('请选择通知方式');
    return;
  }
  var selected = $$('input[name=_s_notice_hand.list][value=1]');
  if(selected.length<1){
    alert('请选择接收人');
    return;
	}
  doConfirm(url,'发送通知，你确认吗？');
}

function close(){
  alert('发送成功');
  window.parent.taskInstDialog.close();
}
