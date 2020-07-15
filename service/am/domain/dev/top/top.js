function changeView(url){
	//如果定制单色导航，设置选中项的颜色
	var dev = document.getElementById("bar.dev");
	if(dev){
		dev.value="/dev/welcome.do";
	}
	window.parent.app.location=url;
}
function changeDevView(url){
	window.parent.app.location=url;
}
