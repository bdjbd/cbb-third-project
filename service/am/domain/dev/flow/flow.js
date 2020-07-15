var flowVarDevObj;
function onDevClick(obj){
	flowVarDevObj = obj;
	setTimeout(setRow, 10);
}
function setRow(){
	var row = $(flowVarDevObj).getParent('tr');
	var checked = row.getElement('input[name=fv.d]').value=='1';
	row.getElement('select').setStyle('display',checked?'none':'block');
}
function setRows(){
	var ds = $$('input[name=fv.d]');
	if(ds && ds.length){
		for(var i=0;i<ds.length;i++){
			flowVarDevObj = ds[i];
			setRow();
		}
	}
}
setRows();