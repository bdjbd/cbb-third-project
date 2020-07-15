
AMap.service('AMap.Geocoder',function(){//回调函数
    //实例化Geocoder
	geocoder = new AMap.Geocoder({
        //城市，默认：“全国”
    });
    //TODO: 使用geocoder 对象完成相关功能
});

function getAddress(){

	var proid = document.getElementById('proid');   
	var proname = proid.options[proid.selectedIndex].text;
	var cityid = document.getElementById('cityid');   
	var cityname = cityid.options[cityid.selectedIndex].text;
	var zoneid = document.getElementById('zoneid');   
	var zonename = zoneid.options[zoneid.selectedIndex].text;
	var address = document.getElementsByName('cdms_case_edit.form.address');
	var myaddress = proname+cityname+zonename+address[0].value;
	//定义经度
	var lng = "";
	//定义纬度
	var lat = "";

    //地理编码,返回地理编码结果
    geocoder.getLocation(myaddress, function(status, result) {

        if (status === 'complete' && result.info === 'OK') {
			//取得json数组
			var geocode = result.geocodes;

			if(geocode.length=='1'){
				lng = geocode[0].location.lng;
				lat	= geocode[0].location.lat;
				document.getElementById('lng').value=lng;
				document.getElementById('lat').value=lat;
				doSubmit('/cdms/cdms_case_edit.form/submit.do');
			}	
        }
    });
}

