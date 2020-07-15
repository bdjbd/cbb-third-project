<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.lang.*"%>
<%@ page import="com.fastunit.Var"%>
<%@page import="com.fastunit.util.Checker"%>
<%
String cph=request.getParameter("cph");
if(Checker.isEmpty(cph)){
    cph="";
}
%>
<!-- 重点参数：renderOptions -->
<html lang="zh-CN">

<head>
    <!-- 原始地址：//webapi.amap.com/ui/1.0/ui/misc/PathSimplifier/examples/navigators.html -->
    <!-- <base href="//webapi.amap.com/ui/1.0/ui/misc/PathSimplifier/examples/" /> -->

    <meta charset="utf-8">
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no, width=device-width">
    <title>轨迹回放</title>
    <script type="text/javascript" src='//webapi.amap.com/maps?v=1.4.3&key=16c83a3fef165e22dfd8a9f976e880ee'></script>
    <script src="http://cache.amap.com/lbs/static/es5.min.js"></script>
    <!-- UI组件库 1.0 -->
    <script src="//webapi.amap.com/ui/1.0/main.js?v=1.0.11"></script>
    <link rel="stylesheet" href="./css/bootstrap.min.css" />
    <link rel="stylesheet" type="text/css" href="./css/xcConfirm.css"/>
    <script type="text/javascript" src="./js/jquery.min.js"></script>
    <script type="text/javascript" src="./js/bootstrap.min.js"></script>
	<!-- 引入样式 -->
	<link rel="stylesheet"
		href="https://cdn.bootcss.com/element-ui/2.3.4/theme-chalk/index.css">
	<!-- 先引入 Vue -->
	<script src="https://cdn.bootcss.com/vue/2.5.16/vue.js"></script>
	<!-- 引入组件库 -->
	<script src="https://cdn.bootcss.com/element-ui/2.3.4/index.js"></script>
    <script src="./js/jqueryManager.JS"></script>
    <script src="./js/xcConfirm.js" type="text/javascript" charset="utf-8"></script>
    
    <style>
        .alert {
    display: none;
    z-index: 99999;
    padding: 15px;
    border: 1px solid transparent;
    border-radius: 4px;

	position: fixed;
    top: 0px;
    left: 0px;
    right: 0px;
    bottom: 0px;
    margin: auto;
	width: 150px;
	height: 50px;
	overflow: hidden;
    opacity:0.9;
}

.alert-success {
	text-align: center;
    color: #333333;
    background-color: #FFFFFF;
    border-color: #FFFFFF;
}

.alert-info {
    color: #31708f;
    background-color: #d9edf7;
    border-color: #bce8f1;
}

.alert-warning {
    color: #8a6d3b;
    background-color: #fcf8e3;
    border-color: #faebcc;
}

.alert-danger {
    color: #a94442;
    background-color: #f2dede;
    border-color: #ebccd1;
}
        .amap-logo {
            display: none !important;
        }

        .amap-copyright {
            display: none !important;
        }

        #container {
            width: 100%;
            height: 100%;
            margin: 0px;
            /* margin-bottom: 200px; */
        }

        #bottom {
            height: 200px;
            overflow: auto;
            background-color: #fff;
            border-radius: 3px;
            position: absolute;
            bottom: 10px;
            left: 10px;
            right: 10px;
            opacity: 0.9;
            filter: alpha(opacity=90);
            box-shadow: 0 3px 14px rgba(0, 0, 100, 0.6);
        }

        #tip {
            width: 300px;
            background-color: #fff;
            padding: 10 20px;
            position: absolute;
            font-size: 14px;
            right: 10px;
            top: 10px;
            border-radius: 3px;
            opacity: 0.9;
            filter: alpha(opacity=90);
            box-shadow: 0 3px 14px rgba(0, 0, 100, 0.6);
        }

        #max {
            width: 20px;
            height: 20px;
            display: none;
            position: fixed;
            bottom: 0px;
            right: 0px;
        }

        .close {
            float: right;
            font-size: 21px;
            font-weight: 700;
            line-height: 1;
            color: #409EFF;
            text-shadow: 0 1px 0 #fff;
            opacity: 0.8;
            filter: alpha(opacity=20);
        }

        .close:hover,
        .close:focus {
            color: #2a6496;
            text-decoration: none;
            cursor: pointer;
            opacity: .8;
            filter: alpha(opacity=50);
        }

        img{
            cursor:pointer ;
        }
        #img1,#img2{
            display: none;
        }
        
        #img3,#img4{
            cursor:default ;
        }

        .fixed_headers1 thead tr {
            display: block;
            position: relative;
        }
        .fixed_headers1 tbody {
            display: block;
            overflow: auto;
        }
        /* 每列宽度 */

        .fixed_headers1 td:nth-child(1),
        .fixed_headers1 th:nth-child(1) {
            min-width: 100px;
            /* 滚动条 */
            text-overflow:ellipsis;
            white-space:nowrap;
            overflow:hidden;
        }
        .fixed_headers1 td:nth-child(2),
        .fixed_headers1 th:nth-child(2) {
            min-width: 220px;
        }
        .fixed_headers1 td:nth-child(3),
        .fixed_headers1 th:nth-child(3) {
            min-width: 100px;
        }

        .fixed_headers1 td:nth-child(4),
        .fixed_headers1 th:nth-child(4) {
            min-width: 100px;
        }

        .fixed_headers1 td:nth-child(5),
        .fixed_headers1 th:nth-child(5) {
            min-width: 550px;
        }

        .fixed_headers1 td:nth-child(6),
        .fixed_headers1 th:nth-child(6) {
            min-width: 420px;
        }

        .fixed_headers thead tr {
            display: block;
            position: relative;
        }
        .fixed_headers tbody {
            display: block;
            overflow: auto;
        }
        /* 每列宽度 */

        .fixed_headers td:nth-child(1),
        .fixed_headers th:nth-child(1) {
            min-width: 100px;
            /* 滚动条 */
            text-overflow:ellipsis;
            white-space:nowrap;
            overflow:hidden;
        }
        .fixed_headers td:nth-child(2),
        .fixed_headers th:nth-child(2) {
            min-width: 230px;
        }
        .fixed_headers td:nth-child(3),
        .fixed_headers th:nth-child(3) {
            min-width: 230px;
        }
    </style>
</head>

<body>
<div id="app">
    <div id="outer-box">
        <div id="container"></div>
    </div>
    <div id="tip">
        <form class="form-horizontal" role="form">
            <div class="form-group">
                <label for="carnumber"  class="col-sm-4 control-label" >车牌号码</label>
                <div class="col-sm-6">
                    <input style="height:32px;"  placeholder="请输入车牌号" class="el-input__inner" name="cph"></input>
                </div>
                <img style="padding-top: 6px;" class="col-sm-2" src="/img/add.png" data-toggle="modal" data-target="#myModal">
            </div>
            <div class="form-group">
                <label for="carnumber" class="col-sm-4 control-label">开始时间</label>
                <div class="col-sm-6">
                    <template>
                        <div class="block">
                            <el-date-picker 
                            size="small" 
                            style="width: 165px;" 
                            v-model="value2"
                            type="datetime" 
                            placeholder="选择日期时间" 
                            align="right" :picker-options="pickerOptions1"
                            id='value2' >
                            
                            </el-date-picker>
                        </div>
                    </template>

                </div>
            </div>
            <div class="form-group">
                <label for="carnumber" class="col-sm-4 control-label">结束时间</label>
                <div class="col-sm-6">
                    <template>
                        <div class="block">
                            <el-date-picker 
                            size="small" 
                            style="width: 165px;" 
                            v-model="value3" 
                            type="datetime" 
                            placeholder="选择日期时间" 
                            align="right" :picker-options="pickerOptions2"
                            id='value3'>
                            </el-date-picker>
                        </div>
                    </template>
                </div>
            </div>
            <!-- icon="el-icon-search" -->
            <el-button id="cx"
            style="float: right;width: 100px;" 
            size="small" 
            v-on:click="query">查询</el-button>
        </form>
    </div>
    <div id="max">
        <img src="/img/max.png">
    </div>
    <div id="bottom">
        <div class="row" style="margin: 0px; padding: 0; border-bottom: 1px solid #e5e5e5; min-height: 16.42857143px; font-size: 14px;">
            <div class="col-md-4" style="height: 50px; line-height: 50px; margin: 0px; padding: 0; padding-left: 10px;">轨迹数据</div>
            <div id="toolsbar" class="col-md-1" style="width: 56px; margin-top: 11px; padding-right: 0;">
                <img style="width: 30px;" src="/img/export.png" onclick="daochu()">
            </div>
            <div id="routes-container" class="col-md-7 row" style="margin: 0px; padding: 0; text-align: center; padding-top: 10px;">
            </div>
            <div class="col">
                <el-button size="small" id="min" style="float: right;">
                    <img src="/img/min.png" style="width: 14px;"> </el-button>
            </div>




        </div>
        
        <table id="t1" class="table coma-base-table fixed_headers1" style="font-size: 14px;margin-bottom: 0px;">   
            <thead>
                        <tr>
                            <th>车牌号</th>
                            <th>时间</th>
                            <th>速度km/h</th>
                            <th>里程km</th>
                            <th>定位</th>
                            <th>状态</th>
                        </tr>
                    </thead>
                <tbody id="location"></tbody>                                
                <tfoot>
                    <tr><td align='center' colspan="6" onclick="jz()" id="jzgd">无数据</td></tr>
                </tfoot>
            </table>
</div>
    <!-- 选择车辆模态框（Modal） -->
    <div class="modal fade" id="myModal" tabindex="0" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <a class="close" data-dismiss="modal">×</a>
                    <span class="modal-title" id="myModalLabel">选择车辆</span>
                </div>

                <div class="modal-body" style="padding-bottom: 0px;">
                    <form class="form-horizontal" role="form" style="font-size: 14px;">
                        <div class="form-group">
                            <label for="fencetype" class="col-sm-2 control-label">车组</label>
                            <div class="col-sm-7">
                                <select class="form-control input-sm" id="fencetype">
                                    
                                </select>
                            </div>
                            <el-button class="col-sm-2" icon="el-icon-search" onclick="czquery()" size="small">查询</el-button>
                        </div>


                        <div class="form-group" style="height: 350px; overflow-x: hidden">
                            <label class="col-sm-2 control-label">车辆列表</label>
                            <table class="table table-hover coma-base-table fixed_headers" style="font-size: 14px;padding-bottom: 0px;">
                                <thead>
                                    <tr>
                                        <th>
                                        </th>
                                        <th>车牌号</th>
                                        <th>车组名</th>
                                    </tr>
                                </thead>
                                <tbody id="tb">

                                </tbody>
                                <tfoot>
                                    <tr><td style="display:block;" align='center' colspan="6" id="nodata">无数据</td></tr>
                                </tfoot>
                            </table>
                        </div>
                    </form>

                </div>
                <div class="modal-footer" style="border: none;padding-top: 0px;">
                    <el-button type="primary" data-dismiss="modal" onclick="xzCar()" size="small" icon="el-icon-check">确定</el-button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal -->
    </div>
</div>
            <!-- 栾浪浪js -->
<script type="text/javascript">
var navgtr;
function jump(t) {
    // $(t).css("background-color","#409EFF").siblings().css("background","none");
    navgtr.moveToPoint($(t).attr("id"),0)
	navgtr.pause()
    scroll();
	
};
function mapload(dl,name){
    var map = new AMap.Map('container', {
        resizeEnable: true
    });
    AMapUI.load(['ui/misc/PathSimplifier', 'lib/$'], function (PathSimplifier, $) {
        //轨迹展示组件目前依赖Canvas进行绘制，因此不支持IE9及以下浏览器。
        if (!PathSimplifier.supportCanvas) {
            alert('当前环境不支持 Canvas！');
            return;
        }
        //定义一个全局的colors数组
        var colors = ["#3366cc"];
        //轨迹图标数组，正常使用中、车辆维修中、终端维护中、车辆报废中，停车、怠速(报警、离线)
        var iconarr = ["","../img/green/car1.png","../img/gray/car1.png","../img/gray/car1.png","../img/gray/car1.png","../img/blue/car1.png","../img/orange/car1.png","../img/red/car1.png","../img/gray/car1.png"];
        //行车轨迹颜色数组，正常行驶用绿色，超速报警、出区域报警、非工作时间用车报警用不同颜色连线表示
        var colorarr = ["#0bb650","#3c7fff","#ff9600","#ff0000"];
        
        ////创建轨迹展示组件实例
        var pathSimplifierIns = new PathSimplifier({
            zIndex: 100,//绘制用图层的叠加顺序值 。如果需要叠加在地图的最上层，可以设置一个较大的值
            map: map, //所属的地图实例
            getPath: function (pathData, pathIndex) {
                //返回轨迹数据中的节点坐标信息
                return pathData.path;
            },
            getHoverTitle: function (pathData, pathIndex, pointIndex) {
                //返回鼠标悬停时显示的信息
                if (pointIndex >= 0) {
                    //鼠标悬停在某个轨迹节点上
                    return pathData.name + '，点:' + pointIndex + '/' + pathData.path.length;
                }
                //鼠标悬停在节点之间的连线上
                return pathData.name + '，点数量' + pathData.path.length;
            },
            //绘制引擎的构造参数
            renderOptions: {
                //轨迹线的样式
                pathLineStyle: {
                    strokeStyle:'#000000',
                    //方向箭头样式，lineWidth>=4 时有效,为true时直接使用默认配置
                    dirArrowStyle: false
                },
                //根据轨迹索引和zoom返回样式配置
                getPathStyle: function (pathItem, zoom) {
                    var color = colors[pathItem.pathIndex],//依次取颜色
                        lineWidth = Math.round(3*Math.pow(1.1, zoom - 3));//线宽随zoom增大
                    return {
                        //轨迹线的样式 
                        pathLineStyle: {
                            strokeStyle: color,//线颜色
                            lineWidth: lineWidth//线宽度
                        },
                        //轨迹线处于选中状态时的样式
                        pathLineSelectedStyle: {
                            lineWidth: lineWidth + 2
                        },
                        //轨迹巡航器的样式
                        pathNavigatorStyle: {
                            fillStyle: color//填充色
                        }
                    }
                },
                pathLinePassedStyle: {
                    strokeStyle:colorarr[0]
                }
            }
        });

        var pathNavigs = [];
        function onload() {
            //图片加载成功，重新绘制一次
            pathSimplifierIns.renderLater();
        }

        function onerror(e) {
            alert('图片加载失败！');
        }
        
        function getNavg(pathIndex) {

            if (!pathNavigs[pathIndex]) {
                console.log(pathIndex)
                //创建一个轨迹巡航器
                navgtr = pathSimplifierIns.createPathNavigator(pathIndex, {
                    loop: false,//循环播放
                    speed: parseInt($('#speedInp_' + pathIndex).val()),
                    pathNavigatorStyle: {//轨迹巡航器的样式
                        width: 16,
                        height: 32,
                        content: PathSimplifier.Render.Canvas.getImageContent('../img/green/car1.png', onload, onerror),//巡航器的内容
                        strokeStyle: null,//描边色
                        fillStyle: null,//填充色
                        pathLinePassedStyle: {
                            lineWidth: 10,//线宽度
                            strokeStyle:colorarr[0]
                        }
                    }
                });
                
                pathSimplifierIns.on('pointClick', function(e, info){
                    // info.pathData 即是相关的轨迹数据，如果info.pointIndex >= 0，则表示由轨迹上的节点触发
                    var index = info.pointIndex+1;
                    navgtr.moveToPoint(index,0);
					navgtr.pause()
                    scroll();
					
                });
                var tr;
                var index=navgtr.getCursor().idx;
                var timer2 = setInterval(scroll, 20);
				//列表滚动
                function scroll(){
                    if(index !== navgtr.getCursor().idx){
                        index = navgtr.getCursor().idx;
                        console.info('当前滚动到的第几个点'+index);
                        if(index !=""){
                            for(i=0;i<t1.rows.length;i++){
                                t1.rows[i].bgColor='';
                            }
                            //返回该列的所在行tr
                            tr=document.getElementById(index);
                            tr.bgColor='#409EFF';
                            window.location.href="#"+index;
                        } 
                    }
                    var lenght = pathSimplifierIns.getPathData(0).path.length;
                    console.info('pathSimplifierIns.getPathData(0).path.length='+lenght);
                    if(index==lenght-1){
                        // $('<div>').appendTo('body').addClass('alert alert-success').html('轨迹播放完毕！').show().delay(1500).fadeOut();
                        window.clearInterval(timer2);
                        navgtr.destroy();
                    }
                    
                }
                
                function changeNavgContent(){
                    //返回当前所处的索引位置
                    var cursor = navgtr.getCursor();
                        //获取到pathNavigatorStyle的引用
                        var pathNavigatorStyle = navgtr.getStyleOptions();
                        //覆盖修改
                        //车辆状态
                        var carstate = pathSimplifierIns.getPathData(0).path[cursor.idx][2];
                        //报警码
                        var alarmcode = pathSimplifierIns.getPathData(0).path[cursor.idx][3];


                        //轨迹图标等于iconarr[当前位置的车辆状态],从iconarr[1]开始，因为车辆状态没有0（车辆位置信息表需要加车辆状态，api中保存）
                        for (var i=1;i<=iconarr.length;i++)
                        {
                            if(carstate==i){
                                pathNavigatorStyle.content=PathSimplifier.Render.Canvas.getImageContent(iconarr[i], onload, onerror)
                                
                            }
                        }
                        if(carstate=='7'){
                                    //当车辆状态为报警时，查询车辆位置运行表的报警码，根据报警码显示不同的轨迹颜色
                                    //经过路径的样式
                                    if(typeof(alarmcode)!=="undefined"){
                                        var alarm = alarmcode.split(",");
                                        for (var j=0;j<alarm.length;j++)
                                        {
                                            if(alarm[j]=='alarmFlag_1')
                                            {//超速报警
                                                navgtr._opts.pathNavigatorStyle.pathLinePassedStyle.strokeStyle=colorarr[1];

                                            }
                                            if(alarm[j]=='alarmFlag_37')
                                            {//进出区域报警
                                                navgtr._opts.pathNavigatorStyle.pathLinePassedStyle.strokeStyle=colorarr[2];

                                            }
                                            if(alarm[j]=='alarmFlag_36')
                                            {//非工作时间报警
                                            navgtr._opts.pathNavigatorStyle.pathLinePassedStyle.strokeStyle=colorarr[3];

                                            }

                                        }
                                    }
                                }
                    //重新绘制
                    pathSimplifierIns.renderLater();
                }
                var $markerContent = $('<div class="markerInfo"></div>');

                $markerContent.html(pathSimplifierIns.getPathData(pathIndex).name);

                navgtr.marker = new AMap.Marker({
                    icon: "../img/car65.png",
                    offset: new AMap.Pixel(12, -10),
                    content: $markerContent.get(0),
                    map: map
                });
                
                var $msg = $('#routes-container').find('div.route-item[data-idx="' +
                    pathIndex + '"]').find('.msg');
                var timer;
                navgtr.on('move', function () {
                    navgtr.marker.setPosition(navgtr.getPosition());
                    $("#img0").hide();
                    $("#img1").show();
                    $("#img2").hide();
                    $("#img3").attr("src", "/img/stop2.png");
                    $("#img4").attr("src", "/img/destroy2.png");
                    $("#img3,#img4").css("cursor", "pointer");
                });

                navgtr.onDestroy(function () {
                    pathNavigs[pathIndex] = null;
                    navgtr.marker.setMap(null);
                    $msg.html('');
                    $("#img0").show();
                    $("#img1").hide();
                    $("#img2").hide();
                    $("#img3").attr("src", "/img/stop.png");
                    $("#img4").attr("src", "/img/destroy.png");
                    $("#img3,#img4").css("cursor", "default");
                });

                navgtr.on('start', function () {
                    timer = setInterval(changeNavgContent, 500);                   
                    navgtr._startTime = Date.now();
                    navgtr._startDist = this.getMovedDistance();
                    $("#img0").hide();
                    $("#img1").show();
                    $("#img2").hide();
                    $("#img3").attr("src", "/img/stop2.png");
                    $("#img4").attr("src", "/img/destroy2.png");
                    $("#img3,#img4").css("cursor", "pointer");
                });

                navgtr.on('resume', function () {
                    navgtr._startTime = Date.now();
                    navgtr._startDist = this.getMovedDistance();
                    $("#img0").hide();
                    $("#img1").show();
                    $("#img2").hide();
                    $("#img3").attr("src", "/img/stop2.png");
                    $("#img4").attr("src", "/img/destroy2.png");
                    $("#img3,#img4").css("cursor", "pointer");
                });

                navgtr.on('pause', function () {
                    $("#img0").hide();
                    $("#img1").hide();
                    $("#img2").show();
                    $("#img3").attr("src", "/img/stop.png");
                    $("#img4").attr("src", "/img/destroy2.png");
                    $("#img3").css("cursor", "default");
                    $("#img4").css("cursor", "pointer");
                });

                navgtr.on('stop', function () {
                    window.clearInterval(timer);
                    $("#img0").show();
                    $("#img1").hide();
                    $("#img2").hide();
                    $("#img3").attr("src", "/img/stop.png");
                    $("#img4").attr("src", "/img/destroy2.png");
                    $("#img3").css("cursor", "default");
                    $("#img4").css("cursor", "pointer");
                });

                navgtr.on('stop pause', function () {
                    window.clearInterval(timer);
                    navgtr._movedTime = Date.now() - navgtr._startTime;
                    navgtr._movedDist = this.getMovedDistance() - navgtr._startDist;

                    navgtr._realSpeed = (navgtr._movedDist / navgtr._movedTime * 3600);

                    var msgInfo = {
                        '状态': this.getNaviStatus(),
                        '设定速度': this.getSpeed() + ' km/h',
                        '总行进距离': Math.round(this.getMovedDistance() / 1000) + ' km',
                        '本段行进距离': Math.round(navgtr._movedDist / 1000) + ' km',
                        '本段耗时': (navgtr._movedTime / 1000) + ' s',
                        '本段实际速度': Math.round(navgtr._realSpeed) + ' km/h'
                    };

                    $msg.html('<pre>' + JSON.stringify(msgInfo, null, 2) + '</pre>');

                    refreshNavgButtons();
                });

                navgtr.on('move', function () {
                    var msgInfo = {
                        '状态': this.getNaviStatus(),
                        '设定速度': this.getSpeed() + ' km/h',
                        '总行进距离': Math.round(this.getMovedDistance() / 1000) + ' km'
                    };

                    $msg.html('<pre>' + JSON.stringify(msgInfo, null, 2) + '</pre>');
                });
                
                pathNavigs[pathIndex] = navgtr;
            }

            return pathNavigs[pathIndex];
        }

        var navigBtnsConf = [{
            name: '开始',
            action: 'start',
            enableExp: 'navgStatus === "stop" || navgStatus === "pause"'
        }
        , {
            name: '暂停',
            action: 'pause',
            enableExp: 'navgStatus === "moving"'
        }, {
            name: '恢复',
            action: 'resume',
            enableExp: 'navgStatus === "pause"'
        }
        , {
            name: '停止',
            action: 'stop',
            enableExp: 'navgStatus === "moving"'
        }, {
            name: '销毁',
            action: 'destroy',
            enableExp: 'navgExists'
        }];

        function refreshNavgButtons() {

            $('#routes-container').find('div.route-item').each(function () {

                var pathIndex = parseInt($(this).attr('data-idx'), 0);

                if (pathIndex < 0) {
                    return;
                }

                var navgStatus = 'stop',
                    navgExists = !!pathNavigs[pathIndex];

                if (navgExists) {
                    navgStatus = pathNavigs[pathIndex].getNaviStatus();
                }

                $(this).find('.navigBtn').each(function () {

                    var btnIdx = parseInt($(this).attr('data-btnIdx'));

                    $(this).prop('disabled', !eval(navigBtnsConf[btnIdx].enableExp));

                });

            });
        }

        function initRoutesContainer(data) {

            $('#routes-container').on('click', '.navigBtn', function () {
                
                var pathIndex = parseInt($(this).closest('.route-item').attr('data-idx'), 0);
                console.log(pathIndex)
                var navg = getNavg(pathIndex);

                navg[$(this).attr('data-act')]();

                refreshNavgButtons();
            });

            for (var i = 0, len = data.length; i < len; i++) {
                initRouteItem(data[i], i);
            }
            refreshNavgButtons();
        }

        function initRouteItem(pathData, idx) {

            var $routeItem = $('<div class="route-item" style="width: 295px;"></div>');

            $routeItem.attr('data-idx', idx);

            for (var i = 0, len = navigBtnsConf.length; i < len; i++) {

                $('<img id="img'+i+'" style="padding-right:15px;" class="navigBtn col" data-btnIdx="' + i + '" data-act="' + navigBtnsConf[i].action + '" src="/img/' + navigBtnsConf[i].action + '.png">').html(navigBtnsConf[i].name).appendTo($routeItem);
            }

            $speedBox = $('<div class="speedBox col-md-5" style="padding-left: 0px;padding-top: 5px;float:right;"></div>').appendTo($routeItem);

            var speedTxt = $('<span><span>').appendTo($speedBox);

            var speedRangeInput = $('<input id="speedInp_' + idx +
                '" class="speedRange" type="range" min="1000" max="10000" step="1000" value="5000" />').appendTo($speedBox);

            function updateSpeedTxt() {
                var speed = parseInt(speedRangeInput.val(), 10);
                if (pathNavigs[idx]) {
                    pathNavigs[idx].setSpeed(speed);
                }
            }
            speedRangeInput.on('change', updateSpeedTxt);

            updateSpeedTxt();
            
            $speedBox.appendTo($routeItem);            
            $("#routes-container").empty()
            $routeItem.appendTo('#routes-container');

        }

        window.pathSimplifierIns = pathSimplifierIns;

        var d1 = [{ "name": name, "path": dl }]
        var flyRoutes = [];


        d1.push.apply(d1, flyRoutes);

        pathSimplifierIns.setData(d1);

        initRoutesContainer(d1);

        pathSimplifierIns.on('selectedPathIndexChanged', function (e, info) {

        });

        pathSimplifierIns.on('pointClick pointMouseover pointMouseout', function (e, record) {
            //console.log(e.type, record);
        });

        pathSimplifierIns.on('pathClick pathMouseover pathMouseout', function (e, record) {
            //console.log(e.type, record);
        });
    }
    );
}
            
            
</script>
<script type="text/javascript">
    $('#toolsbar').css('display','none');
        function GetQueryString(name)
        {
            var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            console.log( unescape(r[2]))
            if(r!=null)return  unescape(r[2]); return null;
        }
        function daochu()
        {    
            var url = "/p2p/com.cdms.guiji.GuiJiDaoChu.do?cph="+$("input[name='cph']").val()
            +'&startime='+$("#value2").val()
            +'&endtime='+$("#value3").val()
            location.href=url;
            }
        function locationList(d)
        {
            jqueryManager(d,function(json){
				console.info(json);
                if(json!==''&&json!='[]'){
                    $('#location').css('height','90px')
                    $('#jzgd').css("display","none")                        
                    eval("data="+json); 
                    data.forEach(d => {
                    
                    d.ID = d.ID-1;
                    $("#location").append("<tr onclick='jump(this)' style='cursor:pointer;' id='"+d.ID+"'>"
                        +"<th>"+d.LICENSE_PLATE_NUMBER+"</th>"
                        +"<th>"+d.POSITIONING_TIME+"</th>"
                        +"<th>"+d.SPEED+"km/h</th>"
                        +"<th>"+d.CURRENT_MILEAGE+"km</th>"
                        +"<th>"+d.LOCATION+"</th>"
                        +"<th>"+d.CAR_STATE+"</th>"
                        +"</tr>")
                    });
					var dl=[];
					eval("data="+json); 
                                    var name=''
                                    data.forEach(d => {
                                        name=d.LICENSE_PLATE_NUMBER
                                        var ddl=[d.LNG,d.LAT,d.DRIVING_BEHAVIOR_STATUS,d.ALARM_STATUS,d.STATE_OF_VEHICLE,d.ID]
                                        dl.push(ddl)
                                    });
                                    mapload(dl,name)
					
                }else{
                    $('#location').css('height','0')
                    $('#jzgd').css("display","block")
                }
            })
        }
        // function jz()
        // {
        //     if($('#jzgd').attr('name')!=null){
        //         $('#jzgd').attr('name',eval($('#jzgd').attr('name')+"+1"))
        //         console.log($('#jzgd').attr('name'))
        //         var d={
        //             action:'com.cdms.guiji.GuiJiFindLocaltionList',
        //             cph:$("input[name='cph']").val(),
        //             startime:$("#value2").val(),
        //             endtime:$("#value3").val(),
        //             pagefenye:'off',
        //             page:$('#jzgd').attr('name')
        //         }
        //         locationList(d)
        //     }
            
        // }
        function xzCar()
        {
            $("input[name='cph']").val($("input[name='table']:checked").val())
        }
        function loadcz()
        {    
            var d={
            action:'com.cdms.guiji.GuiJiFindCarGroup',
            orgid:GetQueryString('orgid')
            }
            jqueryManager(d,function(json){
                if(json!=''){
                    eval("data="+json); 
                    data.forEach(d => {
                        $("#fencetype").append('<option value="'+d.ORGID+'">'+d.ORGNAME+'</option>')    
                    });
                }
            })
        }
        function carxx(Data)
        {
            jqueryManager(Data,function(json){
                if(json!=''&&json!=='[]'){
                    $('#tb').css('height','250px');
                    $('#nodata').css('display','none');
                    eval("data="+json);
                    data.forEach(d => {
                        $("#tb").append('<tr ><td><input name="table" id="'+d.DEVICE_SN_NUMBER+'" type="radio" value='+d.LICENSE_PLATE_NUMBER+'></td><td>'+d.LICENSE_PLATE_NUMBER+'</td><td>'+d.ORGNAME+'</td></tr>')
                    });
                }else{
                    $('#tb').css('height','0');
                    $('#nodata').css('display','block');
                }
            }) 
        }
        function czquery()
        {
            $("#tb").empty();
            var Data={
                action:'com.cdms.guiji.GuiJiFindCarListByorgid',
                orgid:$("#fencetype").val()
            }
            carxx(Data) 
        } 
        $(function()
        {
            loadcz()
            var Data={
                action:'com.cdms.guiji.GuiJiFindCarListByorgid'
            } 
        })
</script>
<script >
                    //var d = [{ "name": "", "path": [[116.405289, 39.904987], [116.406265, 39.905015], [116.406441, 39.905018], [116.406647, 39.905018], [116.406647, 39.905018], [116.40667, 39.904457], [116.4067, 39.903893], [116.4067, 39.903473], [116.4067, 39.902996], [116.406731, 39.902462], [116.406731, 39.901714], [116.406731, 39.901463], [116.406731, 39.901031], [116.406738, 39.901001], [116.406746, 39.900829], [116.406746, 39.900829], [116.405731, 39.90081], [116.404793, 39.900787], [116.403557, 39.900745], [116.403076, 39.90073], [116.402534, 39.900673], [116.401474, 39.900539], [116.399826, 39.900299], [116.399635, 39.900291], [116.398956, 39.900257], [116.396423, 39.900181], [116.396111, 39.900188], [116.396019, 39.900192], [116.395035, 39.900261], [116.393898, 39.900356], [116.393372, 39.900391], [116.393089, 39.900398], [116.392677, 39.90041], [116.3918, 39.900387], [116.390381, 39.900356], [116.389412, 39.900326], [116.388039, 39.900288], [116.386444, 39.900227], [116.385536, 39.900208], [116.384285, 39.900158], [116.384132, 39.90015], [116.382462, 39.900108], [116.381516, 39.900082], [116.381226, 39.900074], [116.380417, 39.90004], [116.378304, 39.899963], [116.377563, 39.89994], [116.377243, 39.899929], [116.377014, 39.899918], [116.374428, 39.899857], [116.374237, 39.899849], [116.373009, 39.899792], [116.37278, 39.89978], [116.371475, 39.899742], [116.371162, 39.899731], [116.368927, 39.899662], [116.368629, 39.899647], [116.367584, 39.899612], [116.366348, 39.899574], [116.36554, 39.899548], [116.365135, 39.89954], [116.364464, 39.899509], [116.363419, 39.899483], [116.363297, 39.899479], [116.363197, 39.899475], [116.360748, 39.899395], [116.359116, 39.899349], [116.358742, 39.899326], [116.358383, 39.899292], [116.358025, 39.899254], [116.357719, 39.899197], [116.357445, 39.899136], [116.356979, 39.899002], [116.356781, 39.898941], [116.356277, 39.898754], [116.355415, 39.898373], [116.355415, 39.898373], [116.355072, 39.898262], [116.354843, 39.898209], [116.354355, 39.898121], [116.354149, 39.898094], [116.352913, 39.898006], [116.35038, 39.897816], [116.350136, 39.8978], [116.349876, 39.897789], [116.349632, 39.897781], [116.34951, 39.897778], [116.349182, 39.897797], [116.348473, 39.89785], [116.347588, 39.897907], [116.346939, 39.897926], [116.345535, 39.897877], [116.343033, 39.897736], [116.339699, 39.89756], [116.338036, 39.897495], [116.337288, 39.897453], [116.336502, 39.897411], [116.334221, 39.897285], [116.332024, 39.897182]] }]
                    //时间减天计算 dayNumber为要减天数,data为要减时间,默认为当前时间
                    function subDay(dayNumber, date) {
                        date = date ? date : new Date();
                        var ms = dayNumber * (1000 * 60 * 60 * 24)
                        var newDate = new Date(date.getTime() - ms);
                        return newDate;
                    }
                
                    var Main = {
                        data() {
                            return {
                                value2: subDay(1/12),
                                value3: subDay(0) ,
                                //结束时间选择范围
                                pickerOptions1:{
                                    disabledDate: (time) => {
                                    let beginDateVal = this.value3
                                    if (beginDateVal) {
                                        return (subDay(7,new Date(beginDateVal))>new Date(time))||(new Date(time)>new Date(beginDateVal))
                                    }
                                    }        
                                },
                                //开始时间选择范围
                                pickerOptions2:{
                                    disabledDate: (time) => {
                                    let beginDateVal = this.value2
                                    if (beginDateVal) {
                                        return (subDay(-7,new Date(beginDateVal))<new Date(time))||(new Date(time)<new Date(beginDateVal))
                                    }
                                    }        
                                },
                                query: function (event) {
                                    $('<div>').appendTo('body').addClass('alert alert-success').html('正在查询中!').show().delay(500).fadeOut();
                                    $("#cx").text("正在查询中...")
                                    $("#tb").empty();
                                    var b=$("input[name='cph']").val()!=''&&$("#value2").val()!=''&&$("#value3").val()!=''
                                    if(b){
                                        
                                        
                                        var d={
                                            action:'com.cdms.guiji.GuiJiFindLocaltionList',
                                            cph:$("input[name='cph']").val(),
                                            startime:$("#value2").val(),
                                            endtime:$("#value3").val(),
                                            pagefenye:'off',
                                        }
										console.info(d);
										$('#location').empty()
										locationList(d)
										$('#toolsbar').css('display','block');
                                            $("#cx").text("查询")
                                        
                                        
                                    }else{
					                    window.wxc.xcConfirm("请输入完整的查询条件");
                                    }
                                }
                            };
                           
                        }
                    }
                    var Ctor = Vue.extend(Main)
                    new Ctor().$mount('#app') 
                </script>
                <script type="text/javascript" charset="UTF-8">

                    var map = new AMap.Map('container', {
                        resizeEnable: true
                    })
                    var cph = '<%=cph%>'
                    if(typeof(cph) == "undefined"){
                        cph='';
                    }
                    $("input[name='cph']").val(cph)
                    
                        

                        var d={
                            action:'com.cdms.guiji.GuiJiFindLocaltionList',
                            cph:'<%=cph%>',
                            
							startime:$("#value2").val(),
                            endtime:$("#value3").val(),
                            pagefenye:'off',
                            }
							$('#location').empty()
						console.info(d);
                        locationList(d)
                        
                        
                </script>
                <script>
                    $(function () {
                        $("#min").click(function () {
                            $('#bottom').hide();
                            $("#max").show();
                        });
                        $("#max").click(function () {
                            $('#bottom').show();
                            $("#max").hide();
                        });
                    });
</script>
</body>
</html>