/**
 * Created by Administrator on 2016/5/19.
 */
var myGeo = new BMap.Geocoder();
function getFromAddress(e){
    // 将地址解析结果显示在地图上,并调整地图视野
    myGeo.getPoint(e.value, function(point){

        if (point) {
            document.getElementById("from_long").value=point.lng;
            document.getElementById("from_lat").value=point.lat;

        }
    }, "西安市");
}

function getToAddress(e){
    // 将地址解析结果显示在地图上,并调整地图视野
    myGeo.getPoint(e.value, function(point){

        if (point) {
            document.getElementById("to_long").value=point.lng;
            document.getElementById("to_lat").value=point.lat;

        }
    }, "西安市");
}