/*在iframe所在的页面内引用*/
function setIFrameHeight(){
	var iframes = $$('iframe');
	var height = $(document).getSize().y-$(document).getElement('div.CG').getPosition().y-10;
	for(var i=0;i<iframes.length;i++){
		iframes[i].setStyle('height',height);
	}
}
setIFrameHeight();
window.addEvent('resize', setIFrameHeight);

