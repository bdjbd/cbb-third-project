var myGeo = new BMap.Geocoder();
function getAddress(e){
	// 将地址解析结果显示在地图上,并调整地图视野
	myGeo.getPoint(e.value, function(point){

	if (point) {
		document.getElementById("address_longitud").value=point.lng;
		document.getElementById("address_latitude").value=point.lat;

	}
	}, "西安市");
}