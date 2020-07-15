var dbName="weshop";
var version=1;
var db;
var openDb;
var erroeDb;
var request=indexedDB.open(dbName,version);
request.onupgradeneeded = function(e) {
    console.log("Upgrading...");
    db=request.result;
    var store=db.createObjectStore("user",{keyPath:"member_code"});
    var tokenIndex=store.createIndex("by_token","token");
    var openidIndex=store.createIndex("by_openid","openid",{unique:true});
}
request.onsuccess = function(e) {
    db=request.result;
    openDb=true;
    alert("onsuccess! "+db);
    getUserInfo2DB('youlesong2013',null);
    saveUserInfo2DB(user);
}
request.onerror=function(){
	alert("indexdb Error!");
	openDb=true;
	erroeDb=true;
}
/**
 * 从数据库获取用户数据
 * @params token token
 * @params callBack 回调函数
 */
function getUserInfo2DB(token,callBack){
	console.log("openDb");
	if(!openDb){
		console.log("读取用户出错了!");
		return ;
	}
	var tx=db.transaction("user","readonly");
	var store=tx.objectStore("user");
	var index=store.index("by_token");
	var coursor=index.openCursor(IDBKeyRange.only(token));
	coursor.addEventListener("success",function(e){
		var cursor=e.result||e.target.result;
		if(cursor){
			if(token==cursor.value.token){
				console.log("获取到用户数据："+cursor.value.openid);
				alert("获取到用户数据："+cursor.value.openid);
				if(callBack){
					callBack(cursor);
				}
				return;
			}
		}
		cursor.continue();
	},false);
}
/****
 * 将数据保存到数据库
 */
function saveUserInfo2DB(user){
	if(erroeDb||!openDb){
		console.log("保存用户出错了!");
		return ;
	}
	var t=db.transaction("user","readwrite");
	var store=t.objectStore("user");
	store.put({
		openid:user.openid,
		token:user.token,
		phone:user.phone,
		member_code:user.member_code
	});
}