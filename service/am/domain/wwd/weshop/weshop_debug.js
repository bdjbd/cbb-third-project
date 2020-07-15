/**
 * 用户类
 */
function User(){
	this.openid="";
	this.nickName="";
	this.weshapName="";//商城登录帐号
	this.paswd="";//商城密码
	this.publicid="";
	this.saveData=new Date().getTime();
	this.isValid=false;
	this.member_code="";
	this.hobby="";
	this.email="";
	this.phone="";
	this.age="";
	this.gender="";
	this.postalCode="";
	this.token="";
	this.orgname="";
	//1，启用；2，冻结；
	this.data_status=1;
	this.memName="";
}

User.prototype.paramsUser=function(data){
	if(data!=null&&data!=""&&data!="undefined"){
		this.member_code=data.MEMBER_CODE;
		this.openid=data.OPENID;
		this.nickName=data.NICKNAME;
		this.weshapName=data.WSHOP_NAME;
		this.email=data.EMAIL;
		this.age=data.AGE;
		this.gender=data.GENDER;
		this.publicid=data.WCHAT_ACCOUNT;
		this.phone=data.PHONE;
		this.hobby=data.HOBBY;
		this.postalCode=data.POSTAL_CODE;
		this.token=data.TOKEN;
		this.orgname=data.ORGNAME;
		this.data_status=data.USTATUS;
		this.memName=data.MEM_NAME;
	}
}

User.prototype.saveUser=function (){
	localStorage.setItem("openid",this.openid);
	localStorage.setItem("nickName",this.nickName);
	localStorage.setItem("weshopName",this.weshapName);
	localStorage.setItem("paswd",this.paswd);
	localStorage.setItem("savaDate",new Date().getTime());
	localStorage.setItem("valid",this.isValid);
	localStorage.setItem("publicid",this.publicid);
	localStorage.setItem("member_code",this.member_code);
	localStorage.setItem("token",this.token);
	localStorage.setItem("phone",this.phone);
	localStorage.setItem("gender",this.gender);
	localStorage.setItem("email",this.email);
	localStorage.setItem("orgname",this.orgname);
	localStorage.setItem("data_status",this.data_status);
	localStorage.setItem("mem_name",this.memName);
}
User.prototype.saveUser2Cookie=function(){
	var userInfo=this.getCookieStr();
	document.cookie=escape(userInfo);
}
User.prototype.getUserInfo2Cookie=function(){
	var startIndex=document.cookie.indexOf("{\"openid\":");
	var endIndex=document.cookie.indexOf(";",startIndex);
	if(endIndex==-1){
		endIndex=document.cookie.toString().length;
	}
	var userInfo=unescape(document.cookie.substring(startIndex,endIndex));
	if(isEmpty(userInfo)){
		return;
	}
	var s=new Function("return "+userInfo)
	var ue=eval(s());
	this.openid=ue.openid;
	this.member_code=ue.member_code;
	this.token=ue.token;
}
User.prototype.getUser2Local=function(){
	this.openid=localStorage.getItem("openid");
	this.nickName=localStorage.getItem("nickName");
	this.weshapName=localStorage.getItem("weshopName");
	this.paswd=localStorage.getItem("paswd");
	this.saveDate=localStorage.getItem("savaDate");
	this.isValid=localStorage.getItem("valid");
	this.publicid=localStorage.getItem("publicid");
	this.member_code=localStorage.getItem("member_code");
	this.token=localStorage.getItem("token");
	this.gender=localStorage.getItem("gender");
	this.phone=localStorage.getItem("phone");
	this.email=localStorage.getItem("email");
	this.orgname=localStorage.getItem("orgname");
	this.data_status=localStorage.getItem("data_status");
	this.memName=localStorage.getItem("mem_name");
}

User.prototype.getJson=function(){
	return {"OPENID":this.openid,"NICKNAME":this.nickName,"WESHOPNAME":this.weshapName,"SAVEDATE":this.saveData,"ISVALID":this.isValid,"PUBLICID":this.publicid,"TOKEN":this.token};
}
User.prototype.getCookieStr=function(){
	return "{"+"\"openid\":\""+this.openid+"\",\"member_code\":\""+this.member_code+"\",\"token\":\""+this.token+"\"}";
}
/***
 * 地址类
 */
function Address(){
	this.memberCode=null;
	this.addresId=null;
	/**省市区详细地址**/
	this.provinec=null;
	/**市**/
	this.city=null;
	/**区**/
	this.area=null;
	/**详细地址**/
	this.detail=null;
	this.phone=null;
	this.postCode=null;
	this.recvName=null;
	this.pcz="";
}

function getAddrForJSON(data){
	var addr=new Address();
	addr.memberCode=data.MEMBER_CODE;
	addr.addresId=data.ADDRES_ID;
	addr.provinec=data.RECV_PROVINCE;
	addr.city=data.RECV_CITY;
	addr.area=data.RECV_AREA;
	addr.detail=data.RECV_DETAIL;
	addr.phone=data.RECV_PHONE;
	addr.recvName=data.RECV_NAME;
	addr.postCode=data.RECV_POSTAL_CODE;
	addr.pcz=data.PRONAME+" "+data.CITYNAME+" "+data.ZONENAME;
	return addr;
}

Address.prototype.getJSON=function(){
	return {AREA:this.area,MEMBERCODE:this.memberCode,ADDRESID:this.addresId,PROVINCE:this.provinec,CITY:this.city,DETAIL:this.detail,PHONE:this.phone,RECVNAME:this.recvName,POSTCODE:this.postCode};
}


	function showDialog(msg) {
				$.mobile.loading('show', {
					text : '',
					textVisible :true,
					theme : 'a',
					textonly : false,
					html : '<h1>'+msg+'</h1>'
				});
			}
	function closeDialog(){
				$.mobile.loading('hide');
	}
	
	
/**
 * 获取用户信息
 */
function loadUserInf(openid,token,callback){
	$.get("/weshop/member.do",{openid:openid,token:token},function(data,textStatus){
		if(textStatus=="success"){
			callback(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

function registerUser(openid,token,callBack){
	$.get("/weshop/registerUser.do",{openid:openid,token:token},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

/**
 * 获取用订单信息
 */
function loadOrders(membercode,callBack){
	$.get("/weshop/getOrders.do?membercode="+membercode,function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}
/**
 * 获取用户地址信息
 */
function loadAddress(membercode,callBack){
	$.get("/weshop/getAddress.do?membercode="+membercode,function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

/**
 * 加载省信息
 * @param callBack 数据加载成功回调函数 参数data，返回的数据 JSONArray
 */
function loadProvinces(callBack){
	$.get("/weshop/loadProvinces.do",function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

/**
 * 根据省份加载对于的城市信息
 * @param provinceId  省份ID
 * @param callBack 回调函数
 */
function loadCityByProvinec(provinceId,callBack){
	$.get("/weshop/loadCityByProvinec.do",{provinceid:provinceId},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

/**
 * 根据城市ID获取区，县信息
 * @param cityID 城市ID
 * @param callBack 回调函数
 */
function loadZoneByCityId(cityID,callBack){
	$.get("/weshop/loadZoneByCityId.do",{cityId:cityID},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

/**
 * 保存收货地址
 * @param saveData
 * @param type 
 */
function saveAddress(addr,type,callBack){
		$.post("/weshop/saveMemberAddr.do?type="+type,addr.getJSON(),function(data,textStatus){
			if(textStatus=="success"){
				callBack(eval("("+replaceBDPstr(data)+")"));
			}else{
				showNetWorkException();
			}
		});
}

/***
 * 根据公众帐号ID获取商品列表
 * @param publicid
 */
function loadShopListView(pid,callBack){
	$.post("/weshop/loadShopListView.do",{PID:pid},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

function getComdityById(cid,callBacke){
	$.post("/weshop/getComdityById.do",{CID:cid},function(data,textStatus){
		if(textStatus="success"){
			callBacke(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

/**
 * 提交订单
 * @param order
 * @param callBack
 */
function submitOrders(order,callBack){
	$.post("/weshop/submitOrder.do",order,function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}
/**
 * 根据公众帐号获取分类
 * @param pid 公众帐号ID
 * @param parendId 父Id
 * @param callBack  回调函数
 */
function loadShopClass(pid,parendId,callBack){
	$.post("/weshop/loadShopClass.do",{PID:pid,PARENTID:parendId},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

/**
 * 根据公众帐号和商品分类获取商品
 * @param pid  公众帐号ID
 * @param cid  商品分类ID
 * @param callBack  回调函数
 */
function loadShopListViewByClass(pid,cid,callBack){
	$.post("/weshop/loadShopListViewByClass.do",{PID:pid,CID:cid},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

function loadShopByPrice(pid,callBack){
	$.post("/weshop/loadShopByPrice.do",{PID:pid},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}
function loadShopByAmount(pid,callBack){
	$.post("/weshop/loadShopByAmount.do",{PID:pid},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}
function getOrgnameByToken(pid,callBack){
	$.post("/weshop/getOrgnameByToken.do",{PID:pid},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}
/**
 * 网络异常显示
 */
function showNetWorkException(){
	showDialog("网络异常，请稍后重试！");
	setTimeout("closeDialog()",1500);
}

/**
 * 网络异常显示
 */
function tops(msg){
	showDialog(msg);
	setTimeout("closeDialog()",1500);
}

function isEmpty(obj){
	if(obj==null)return true;
	if(obj=="")return true;
	if(obj=="undefined")return true;
	if(obj=="null")return true;
	if(obj.toString().trim()==null)return true;
	if(obj.toString().trim().length==0)return true;
	return false;
}

function showDebug(msg){
	alert(msg);
}

function showDebugs(obj){
	var msg="";
	for(var p in obj){
		msg+=p+"->"+obj[p];
	}
	alert(msg);
}

/**
 * 检查是否为邮箱
 * @param emailStr 邮箱地址
 * @returns 是邮箱，返回为true，否则返回为false；
 */
function isEmail(emailStr){
	var emailPatt=/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	return emailPatt.test(emailStr);
}
/**
 * 检查是否为电话号码
 * @param phoneStr 手机电话号码
 * @returns 是电话号码，返回为True，否则返回为false；
 */
function isPhone(phoneStr){
	var phonePatt=/^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/;
	return phonePatt.test(phoneStr);
}
/**
 * 检查是否为邮编
 * @param postCodeStr
 * @returns 是邮编，返回为true，否则为false；
 */
function isPostCode(postCodeStr){
	var postCodePatt=/\d{6}/;
	if(postCodeStr.length!=6){
		return false;
	}
	return postCodePatt.test(postCodeStr);
}
/**
 * 坚持是否是数字
 * @param str 字符串
 * @returns 是数字，返回true，不是返回false
 */
function isNumber(str){
	var numberPatt=/^\d{1,5}$/;
	return numberPatt.test(str);
}
/**
 * 警告提示框
 * @param alertStr
 */
function alertDialog(alertStr){
	$.mobile.loading( 'show', {
		  text:alertStr ,
		  textVisible: true,
		  theme:'e',
		  textonly: true,
		  html:"<img src='images/alert01.jpg' style='width:2em;height:2em;' />&nbsp;&nbsp;<span>"+alertStr+"</span>"
		  });
	setTimeout("$.mobile.loading(\"hide\");",1800);
}

function loadingData(msg){
	$.mobile.loading('show', {
		  text:msg ,
		  textVisible: true,
		  theme:'a',
		  textonly: false,
		  html:null
		  });
}

function replaceBDPstr(str){
	//"&lt;&gt;=&^%$#@!&lt;!--[]--&gt;"
	str=str.replace(/&lt;/g,"<");
	str=str.replace(/&gt;/g,">");
	return str;
}
/**
 * 根据关键字搜索商品
 * @param  key 关键字
 * @param   token
 * @param  callBack 回调函数
 */
function getSearchShopList(key,token,callBack){
	$.post("/weshop/getSearchShopList.do",{KEY:key,TOKEN:token},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	  });
}