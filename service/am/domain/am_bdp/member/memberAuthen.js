var gEntBadge;
//选择企业徽章
function selectEntBadge(e){
	gEntBadge=e;
	doAjax('/am_bdp/mall_userbadge.list/add.do?entbadgeid='+e.value);
}
//设置徽章参数
function setBadgeParams(params){
	gEntBadge.parentNode.nextSibling.firstChild.value=params;
}