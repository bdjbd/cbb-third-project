function changeFlow(){
	if(window.parent.task){
		window.parent.task.location="/adm/welcome.do";
	}
	doSubmit('/adm/tasktree.do');
}
function changeItems(obj){
	$f52(obj.parentNode);
	doSubmit('/adm/tasktree.do');
}

//flowMenu
function addManualTask(){
	window.parent.task.location="/adm/manualtask.do?m=a";
}
function addSplitTask(){
	window.parent.task.location="/adm/splittask.do?m=a";
}
var flowMenu = new Tree('fm','fm');
flowMenu.add(new Tree('fm2','人工任务',addManualTask,'/domain/adm/flow/d/task/add2.png'));
flowMenu.add(new Tree('fm3','分支任务',addSplitTask,'/domain/adm/flow/d/task/add3.png'));
function showFlowMenu(){
	flowMenu.writeMenu();
}
//taskMenu
function getSelectedId(){
	var id = th.ae.s.id;
	return id.substring(1,id.length);
}
function deleteTask(){
	var taskId = getSelectedId();
	if (confirm("删除任务'"+taskId+"'，此操作不可恢复，你确认吗？")){
		window.parent.task.location="/adm/welcome.do";
		doSubmit("/adm/tasktree/delete.do?tid="+taskId);
	}
}
var taskMenu = new Tree('tm','fm');
taskMenu.add(new Tree('tm1','删除',deleteTask,'/common/images/tool/delete.png'));
function showTaskMenu(){
	taskMenu.writeMenu();
}
