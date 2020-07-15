function setRow(obj){
	var row = $(obj).getParent('tr');
	var s = row.getElement('select[name=splits.d]').value;
	row.getElement('select[name=splits.c]').setStyle('display',(s && s!='')?'block':'none');
	row.getElement('input[name=splits.e]').setStyle('display',(s && s!='')?'block':'none');
	var d = row.getElement('select[name=splits.i5]').value;
	row.getElement('select[name=splits.to_tid]').setStyle('display',(d=='1' || d=='3')?'block':'none');
	row.getElement('select[name=splits.i4]').setStyle('display',(d=='3')?'block':'none');
}