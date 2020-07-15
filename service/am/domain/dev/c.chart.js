//[root]
function addChart(){
	window.parent.main.location="/dev/chart.do?m=a";
}
function addChartGroup(){
	window.parent.main.location="/dev/group.do?m=a&grouptype=5";
}
var mr5 = new Tree('mr5','mr5');
mr5.add(new Tree('mr51','统计图',addChart,'/domain/dev/t/addchart.png'));
mr5.add(new Tree('mr52','组',addChartGroup,'/domain/dev/t/addgroup.png'));
function wmr5(){
	mr5.writeMenu();
}
//[group]
function addChart2Group(){
	window.parent.main.location="/dev/chart.do?groupid="+getSelectedId()+"&m=a";
}
function deleteChartGroup(){
	deleteGroup(5);
}
var mg5 = new Tree('mg5','mg5');
mg5.add(new Tree('mg51','统计图',addChart2Group,'/domain/dev/t/addchart.png'));
var mg59 = mg5.add(new Tree('mg59','删除',deleteChartGroup,'/common/images/tool/delete.png'));
function wmg5(){
	var c = mg5.writeMenu();
	setGroupDeleteStatus(c,mg59,deleteChartGroup);
}
//[chart]
function copyChart(){
	window.parent.main.location="/dev/chart.do?src="+getSelectedId()+"&m=a";
}
function deleteChart(){
	var chartId = getSelectedId();
	if (confirm("删除统计图'"+chartId+"'，此操作不可恢复，你确认吗？")){
		window.parent.main.location="/dev/welcome.do";
		doSubmit("/dev/chart/delete.do?chartid="+chartId);
	}
}
function lookChartReference(){
	window.parent.main.location="/dev/unit.relation.do?c=87&id="+getSelectedId();
}
var m5 = new Tree('m5','m5');
m5.add(new Tree('m51','关联',lookChartReference,'/domain/dev/t/link.png'));
m5.add(new MenuSeparator());
m5.add(new Tree('m52','复制',copyChart,'/common/images/tool/copy.png'));
m5.add(new Tree('m59','删除',deleteChart,'/common/images/tool/delete.png'));
function wm5(){
	m5.writeMenu();
}
