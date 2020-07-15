function setRow(obj){
	var row = $(obj).getParent('tr');
	var g = row.getElement('select[name=c.g]').value;
	row.getElement('select[name=c.gr]').setStyle('display',(g && g!='')?'block':'none');
	//var dt = row.getElement('input[name=c.dt]').value;
	//row.getElement('input[name=c.l]').setStyle('display',(dt && dt!='1')?'block':'none');
	//row.getElement('input[name=c.s]').setStyle('display',(dt && dt!='1')?'block':'none');
}
function setRows(){
	var gs = $$('select[name=c.g]');
	if(gs && gs.length){
		for(var i=0;i<gs.length;i++){
			setRow(gs[i]);
		}
	}
}
setRows();

function loadInsertSql(mode){
	var tableName=$fnv('table.t.k');
	doAjax('/dev/tablescript/load.do?tablename='+tableName+'&mode='+mode);
}