function au(p){window.parent.main.location="/dev/unit.do?m=a"+p}
function af(p){window.parent.main.location="/dev/frameset.do?m=a"+p}
function ar(p){window.parent.main.location="/dev/jr.do?m=a"+p}
//[root]
function au1(){au('&c=1')}
function au2(){au('&c=2')}
function au3(){au('&c=3')}
function au4(){af('')}
function au7(){ar('')}
function addSuite(){window.parent.main.location="/dev/wizard.do?groupid=";}
function addUnitGroup(){addGroup(2);}
function findUnit(){window.parent.main.location="/dev/findunit.do";}
function findElement(){window.parent.main.location="/dev/findelement.do";}
function batchUnit(){window.parent.main.location="/dev/unit.batch.do?m=e";}
var mr2 = new Tree('mr2','mr2');
mr2.add(new Tree('mr21','表单',au1,'/domain/dev/t/addu1.png'));
mr2.add(new Tree('mr22','列表',au2,'/domain/dev/t/addu2.png'));
mr2.add(new Tree('mr23','查询',au3,'/domain/dev/t/addu3.png'));
mr2.add(new Tree('mr24','框架',au4,'/domain/dev/t/addu4.png'));
mr2.add(new Tree('mr25','报表',au7,'/domain/dev/t/addu7.png'));
mr2.add(new MenuSeparator());
mr2.add(new Tree('mr26','组',addUnitGroup,'/domain/dev/t/addgroup.png'));
mr2.add(new Tree('mr27','向导',addSuite,'/domain/dev/t/wizard.png'));
mr2.add(new MenuSeparator());
mr2.add(new Tree('mr28','单元',findUnit,'/common/images/tool/find.png'));
mr2.add(new Tree('mr29','元素',findElement,'/common/images/tool/find.png'));
mr2.add(new MenuSeparator());
mr2.add(new Tree('mr2a','批量',batchUnit,'/domain/dev/t/batch.png'));
//mr2.add(new Tree('mr20','统计','','/domain/dev/c/chart.png'));
function wmr2(){
	mr2.writeMenu();	
}
//[group]
function au1g(){au('&c=1&groupid='+getSelectedId())}
function au2g(){au('&c=2&groupid='+getSelectedId())}
function au3g(){au('&c=3&groupid='+getSelectedId())}
function au4g(){af('&groupid='+getSelectedId())}
function au7g(){ar('&groupid='+getSelectedId())}
function addSuite2Group(){window.parent.main.location="/dev/wizard.do?groupid="+getSelectedId();}
function deleteUnitGroup(){deleteGroup(2);}
var mg2 = new Tree('mg2','mg2');
mg2.add(new Tree('mg21','表单',au1g,'/domain/dev/t/addu1.png'));
mg2.add(new Tree('mg22','列表',au2g,'/domain/dev/t/addu2.png'));
mg2.add(new Tree('mg23','查询',au3g,'/domain/dev/t/addu3.png'));
mg2.add(new Tree('mg24','框架',au4g,'/domain/dev/t/addu4.png'));
mg2.add(new Tree('mg25','报表',au7g,'/domain/dev/t/addu7.png'));
mg2.add(new MenuSeparator());
mg2.add(new Tree('mg26','向导',addSuite2Group,'/domain/dev/t/wizard.png'));
mg2.add(new MenuSeparator());
var mg29 = mg2.add(new Tree('mg29','删除',deleteUnitGroup,'/common/images/tool/delete.png'));
function wmg2(){
	var c = mg2.writeMenu();
	setGroupDeleteStatus(c,mg29,deleteUnitGroup);
}
//[unit]
function lookUnitReference(){window.parent.main.location="/dev/unit.relation.do?c=80&id="+getSelectedId();}
function au1u(){au('&c=1&parentid='+getSelectedId())}
function au2u(){au('&c=2&parentid='+getSelectedId())}
function au3u(){au('&c=3&parentid='+getSelectedId())}
function copyUnit(){au('&src='+getSelectedId())}
function batchElement(){window.parent.main.location="/dev/element.batch.do?m=e&uid="+getSelectedId();}
function deleteUnit(){
	var unitId = getSelectedId();
	if (confirm("删除单元'"+unitId+"'，此操作不可恢复，你确认吗？")){
		window.parent.main.location="/dev/welcome.do";
		doSubmit("/dev/unit/delete.do?uid="+unitId);
	}
}
var m2 = new Tree('m2','m2');
m2.add(new Tree('m21','关联',lookUnitReference,'/domain/dev/t/link.png'));
m2.add(new MenuSeparator());
m2.add(new Tree('m27','复制',copyUnit,'/common/images/tool/copy.png'));
m2.add(new Tree('m29','删除',deleteUnit,'/common/images/tool/delete.png'));
m2.add(new MenuSeparator());
m2.add(new Tree('m2a','批量',batchElement,'/domain/dev/t/batch.png'));
function wm2(){
	m2.writeMenu();
}
//frameset
function copyU4(){af('&src='+getSelectedId())}
var mfs = new Tree('mfs','mfs');
mfs.add(new Tree('mfs1','关联',lookUnitReference,'/domain/dev/t/link.png'));
mfs.add(new MenuSeparator());
mfs.add(new Tree('mfs2','复制',copyU4,'/common/images/tool/copy.png'));
mfs.add(new Tree('mfs9','删除',deleteUnit,'/common/images/tool/delete.png'));
function wmfs(){
	mfs.writeMenu();
}
//jr
function copyU7(){ar('&src='+getSelectedId())}
var mjr = new Tree('mjr','mjr');
mjr.add(new Tree('mjr1','关联',lookUnitReference,'/domain/dev/t/link.png'));
mjr.add(new MenuSeparator());
mjr.add(new Tree('mjr2','复制',copyU7,'/common/images/tool/copy.png'));
mjr.add(new Tree('mjr9','删除',deleteUnit,'/common/images/tool/delete.png'));
function wmjr(){
	mjr.writeMenu();
}
