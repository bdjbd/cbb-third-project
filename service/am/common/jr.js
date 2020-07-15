function browserPrint(){
	showBar('none');
	window.print();
	window.setTimeout("showBar('block');",1);
}
function showBar(show){
	document.getElementById('jrbar').style.display=show;
}
function appletPrint(url){
	document.write('<applet code="JRPrinterApplet.class" codebase="/common/applets" archive="jasperreports-3.7.6-applet.jar,commons-logging-1.1.1.jar,commons-collections-3.2.1.jar" width="300" height="40">');   
	document.write('<param name="scriptable" value="false">');   
	document.write('<param name="REPORT_URL" value="'+url+'">');   
	document.write('</applet>');
}
