function testFunction(){
	alert("test function");
}

var subMenu = new Tree('subMenu','sub menu');
subMenu.add(new Tree('SubMenu1','SubMenu1'));
subMenu.add(new Tree('SubMenu2','SubMenu2'));

var menu = new Tree('menu','menu');
menu.add(new Tree('menu1','js方法',testFunction));
menu.add(subMenu);
menu.add(new Tree('menu3','sina','http://www.sina.com.cn'));
menu.add(new Tree('menu4','menu4'));
menu.add(new Tree('menu5','menu5'));

$(document).addEvent('contextmenu',function(e){
	menu.writeMenu(e);
	return false;
});
