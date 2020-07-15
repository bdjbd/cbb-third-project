/**
 * 资源分类点击JavaScript文件
 */

function setRole(){
	var id=th.ae.s.id;
	window.parent.document.getElementById('mjyc_resourcemanagement_frame.e2').src
		='/am_bdp/mjyc_resourcemanagement.do?m=s&resourceclassid='+id+'&clear=am_bdp.mjyc_resourcemanagement.query';
}