//TODO Fx.Slide
function showBox(obj,boxId,action){
	var ss=obj.getAttribute('href') !=null
	if(ss){
		var b=obj.getAttribute('href').indexOf("=")>0
		if(b){
			var ss=obj.getAttribute('href').split("=")
			var sss=ss[0].split("?")
			if(sss[1]=='name'){
				ajaxPost( "/p2p/com.cdms.MenuAction.do?title='"+ss[1]+"'&time='"+new Date()+"'" )
			}
		}
	}
	obj = $(obj);
	var c = obj.getParent('div');
	//box
	var objs = c.getElements('div.box');
	if(objs && objs.length){
		objs.removeClass('box');
		objs.addClass('box_close');
	}
	objs = c.getElements('div.box_active');
	if(objs && objs.length){
		objs.removeClass('box_active');
		objs.addClass('box_close');
	}
	//<a>
	objs = c.getElements('a.node1_active');
	if(objs && objs.length){
		objs.removeClass('node1_active');
		objs.addClass('node1');
	}
	objs = c.getElements('a.node2_active');
	if(objs && objs.length){
		objs.removeClass('node2_active');
		objs.addClass('node2');
	}
	//本节点
	obj.removeClass('node1');
	obj.addClass('node1_active');
	//所属子节点
	var box = c.getElement('div[id='+boxId+']');
	if(box){
		box.removeClass('box_close');
		box.addClass('box');
	}
  //本节点有动作时，取消二级节点的选中
  if(action){
    $$('a.node2').removeClass('active');
  }
}
function showItem(obj){
	var ss=obj.getAttribute('href') !=null
	if(ss){
		var b=obj.getAttribute('href').indexOf("=")>0
		if(b){
			var ss=obj.getAttribute('href').split("=")
			var sss=ss[0].split("?")
			if(sss[1]=='name'){
				ajaxPost( "/p2p/com.cdms.MenuAction.do?title='"+ss[1]+"'&time='"+new Date()+"'" )
			}
		}
	}
  $$('a.node2').removeClass('active');
	obj.addClass('active');
}
// ajax 对象
function ajaxObject() {
		var xmlHttp;
		try {
			// Internet Explorer
			xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
				} 
		catch (e) {
				
				try {
					// Firefox, Opera 8.0+, Safari
				xmlHttp = new XMLHttpRequest();
					} catch (e) {
						try {
								xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
						} catch (e) {
								alert("您的浏览器不支持AJAX！");
								return false;
						}
				}
		}
		return xmlHttp;
	}
	
	// ajax post请求：
	function ajaxPost ( url   ) {
		var ajax = ajaxObject();
		ajax.open( "get" , encodeURI(url) , true );
		
		ajax.send(null);
	
	 } 