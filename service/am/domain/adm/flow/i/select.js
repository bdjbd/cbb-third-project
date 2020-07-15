function selectFlow(){
  if(!$fnv('flow_select.flow')){
    alert('请选择流程');
    return;
  }
  var name = $fn('flow_select.flow').getSelected().get('text');
  if(confirm('选择"'+name+'"进入下一步，你确认吗？')){
    doSubmit($fnv('flow_select.url'));
  }  
}