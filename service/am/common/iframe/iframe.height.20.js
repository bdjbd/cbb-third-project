/*在iframe所在的页面内引用*/
function setIFrameHeight(){
	var iframes = $$('iframe');
	var windowHeight = $(document).getSize().y;
	for(var i=0;i<iframes.length;i++){
		iframes[i].setStyle('height',windowHeight-iframes[i].getPosition().y-20);
	}
}
setIFrameHeight();
window.addEvent('resize', setIFrameHeight);

