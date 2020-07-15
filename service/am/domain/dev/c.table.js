
//[root]
function addTable(){
	window.parent.main.location="/dev/table.do?m=a";
}
function addTableFromDatabase(){
	window.parent.main.location="/dev/addtable.database.do";
}
function addTableGroup(){
	addGroup(1);
}
function addTableFormXml(){
	window.parent.main.location="/dev/addtable.xml.do";
}
function outputTables(){
	window.parent.main.location="/dev/table/output.do";
}
var mr1 = new Tree('mr1','mr1');
mr1.add(new Tree('mr11','表',addTable,'/domain/dev/t/addtable.png'));
mr1.add(new Tree('mr12','组',addTableGroup,'/domain/dev/t/addgroup.png'));
mr1.add(new MenuSeparator());
mr1.add(new Tree('mr13','导入',addTableFromDatabase,'/domain/dev/t/fromdb.png'));
function wmr1(){
	mr1.writeMenu();
}
//[group]
function addTable2Group(){
	window.parent.main.location="/dev/table.do?groupid="+getSelectedId()+"&m=a";
}
function deleteTableGroup(){
	deleteGroup(1);
}
var mg1 = new Tree('mg1','mg1');
mg1.add(new Tree('mg11','表',addTable2Group,'/domain/dev/t/addtable.png'));
var mg19 = mg1.add(new Tree('mg19','删除',deleteTableGroup,'/common/images/tool/delete.png'));
function wmg1(){
	var c = mg1.writeMenu();
	setGroupDeleteStatus(c,mg19,deleteTableGroup);
}
//[table]
function lookTableReference(){
	window.parent.main.location="/dev/unit.relation.do?c=101&id="+getSelectedId();
}
function copyTable(){
	window.parent.main.location="/dev/table.do?src="+getSelectedId()+"&m=a";
}
function deleteTable(){
	var tableName = getSelectedId();
	if (confirm("删除表'"+tableName+"'，此操作不可恢复，你确认吗？")){
		window.parent.main.location="/dev/welcome.do";
		doSubmit("/dev/table/delete.do?tablename="+tableName);
	}
}
function lockTable(){
}
var m1 = new Tree('m1','m1');
m1.add(new Tree('m11','关联',lookTableReference,'/domain/dev/t/link.png'));
m1.add(new MenuSeparator());
m1.add(new Tree('m12','复制',copyTable,'/common/images/tool/copy.png'));
m1.add(new Tree('m19','删除',deleteTable,'/common/images/tool/delete.png'));
function wm1(){
	m1.writeMenu();
}
