function setWrapperAuto(){
	var windowWidth = $(document).getSize().x;
	var wrappers = $$('div.WRAPPER_AUTO');
	for(var i=0;i<wrappers.length;i++){
		var wrapper = wrappers[i];
		wrapper.setStyle('width',windowWidth-14);
    var grid = wrapper.getElement('table');
    if(wrapper.hasClass('WRAPPER_AUTO_H') && wrapper.hasClass('WRAPPER_AUTO_W')){
      if(grid.getSize().y>wrapper.getSize().y){
        grid.setStyle('width',windowWidth-32);
      }
      wrapper.setStyle('height',$(document).getSize().y-wrapper.getPosition().y-160);
    }else if(wrapper.hasClass('WRAPPER_AUTO_H')){
      wrapper.setStyle('height',grid.getSize().y+18);
    }else if(wrapper.hasClass('WRAPPER_AUTO_W') && grid.getSize().y>wrapper.getSize().y){
      grid.setStyle('width',windowWidth-32);
    }
	}
}
setWrapperAuto();
window.addEvent('resize', setWrapperAuto);