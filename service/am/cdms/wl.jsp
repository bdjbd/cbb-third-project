<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="java.lang.*"%>
<%
   request. setCharacterEncoding("UTF-8");
   
%>  

<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no, width=device-width">
   
    <link rel="stylesheet" href="http://cache.amap.com/lbs/static/main1119.css"/>
    <script src="http://webapi.amap.com/maps?v=1.4.4&key=16c83a3fef165e22dfd8a9f976e880ee&plugin=AMap.PolyEditor,AMap.CircleEditor,AMap.DistrictSearch,AMap.MouseTool"></script>
    <script type="text/javascript" src="http://cache.amap.com/lbs/static/addToolbar.js"></script>
    
	<link rel="stylesheet" href="./css/jquery-confirm.css" />
	<link rel="stylesheet" href="./css/bootstrap.min.css" />
	<link rel="stylesheet" href="./css/myAlert.css" />
<link rel="stylesheet" href="./css/bootstrap-table.min.css" />
<script type="text/javascript" src="./js/jquery.min.js"></script>
<script type="text/javascript" src="./js/bootstrap.min.js"></script>
<script src="./js/bootstrap-table.min.js"></script>  
<script src="./js/bootstrap-table-zh-CN.min.js"></script> 
<script src="./js/bootstrapValidator.min.js"></script>
  <script src="./js/jquery-confirm.js"></script>
  <script src="./js/jqueryManager.JS"></script>
   <script src="./js/jqueryManager_tb.JS"></script>
<!-- 引入样式 -->
	<link rel="stylesheet"
		href="https://cdn.bootcss.com/element-ui/2.3.4/theme-chalk/index.css">
	<!-- 先引入 Vue -->
	<script src="https://cdn.bootcss.com/vue/2.5.16/vue.js"></script>
	<!-- 引入组件库 -->
	<script src="https://cdn.bootcss.com/element-ui/2.3.4/index.js"></script>
<script type="text/javascript" src="./js/myAlert.js"></script>
</head>
<style type="text/css">

.amap-logo {
	display: none !important;
}
.amap-indoormap-floorbar-control{
    display: none !important;
}
.amap-copyright {
	display: none !important;
}

#mapContainer {
	padding-left: 300px;
	position: absolute;
}

#fenceinfo {
	width:300px;
	background-color: #fff;
	border-radius: 3px;
	position: absolute;
	top:10px;
	bottom:10px;
	left:10px;
	opacity:0.9;
	filter:alpha(opacity=90);
	box-shadow: 0 3px 14px rgba(0,0,100,0.6);
	overflow-x: hidden
}

#tip {
	background-color: #fff;
	padding: 10px;
	position: absolute;
	font-size: 14px;
	right: 10%;
	top: 10px;
	border-radius: 3px;
	line-height: 36px;
	opacity:0.9;
	filter:alpha(opacity=90);
	box-shadow: 0 3px 14px rgba(0,0,100,0.6);
}


.editmap,.fencesetting{
	width:18px;
}
th,td{
	text-align:center;
}
.modal-content{
	height: 525px;
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
.close:hover, .close:focus {
    color: #2a6496;
    text-decoration: none;
    cursor: pointer;
    opacity: .8;
    filter: alpha(opacity=50);
}
.amap-toolbar {
    display: none;
    position: absolute;
    width: 52px;
    overflow: visible;
}
td{
     font-size: 12px;
       overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
	border:none;
}
th{
     font-size: 12px;
     border-left: none;
}
tr{
    border-left: none;
}
.fixed-table-body {
   
    overflow-y: auto;
   
}
.fixed-table-pagination .pagination-detail, .fixed-table-pagination div.pagination {
    margin-top: 0px;
    margin-bottom: 0px;
   
     height:0px
}
.fixed-table-container {
border:none;
}
.bootstrap-table .table thead>tr>th {
border-left: none;
}
.fixed-table-container tbody td {
  
}
.table {
   
    margin-bottom: 0px;
}
.table>tbody>tr>td, .table>tfoot>tr>td {
    padding: 8px;
    line-height: 1.42857143;
    vertical-align: super;
    border-top: 1px solid #ddd;
}

.fixed-table-pagination .pagination-info {
    line-height: 34px;
    margin-right: 5px;
    display: none;
}
.fixed-table-pagination .page-list {
    display: none;
}
.fixed-table-pagination div.pagination .pagination {
    
    margin-right: 40px;
}
.btn {
    display: block;
	}
#bc{
	display: none;
}
</style>
<body>
<div id="app">
<div id="container"></div>
<div id="fenceinfo">
			<div style="padding: 15px;border-bottom: 1px solid #e5e5e5;min-height: 16.42857143px;font-size: 14px;">
                <span>围栏信息</span>
                <!-- <span style="float: right;"><img title="新增" id="add" src="/img/add.png"></span> -->
            </div>
			<table id="table1">
				
			</table>

		</div>
		<div id="tip">
			<el-button id="polygon" size="small" title="绘制完成双击保存">手动绘制围栏</el-button>
			省：<select id='province' style="border-color: #c6e2ff;border-radius: 4px;height: 32px; width: 100px"
				onchange='search(this)'></select> 市：<select id='city'
				style="border-color: #c6e2ff;border-radius: 4px;height: 32px; width: 100px" onchange='search(this)'></select>
			区：<select id='district' style="border-radius: 4px;height: 32px; width: 100px"
				onchange='search(this)'></select> <select style="display: none;"
				id='street' style="border-color: #c6e2ff;width: 100px" onchange='setCenter(this)'></select>
			<el-button id="zdhz" size="small">范围自动绘制</el-button>
            <el-button id="bc" size="small" onClick="editor.closeEditPolygon()">保存</el-button>
            <!-- <el-button id="bc" size="small" onClick="editor.cancel()">取消</el-button> -->
		</div>






		<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
					<a class="close" data-dismiss="modal">×</a>
						<span class="modal-title" id="myModalLabel">围栏信息</span>
					</div>
					
					<div class="modal-body">
						<form class="form-horizontal" role="form" style="font-size: 14px;">
							<div class="form-group">
								<label for="fencename" class="col-sm-2 control-label">围栏名称</label>
								<div class="col-sm-10">
									<input type="text" class="form-control input-sm" id="fencename"
										placeholder="请输入围栏名称">
								</div>
							</div>
							<div class="form-group">
								<label for="orgcode" class="col-sm-2 control-label">所属机构</label>
								<div class="col-sm-10">
                                               <input type="text" readonly="readonly" class="form-control input-sm" id="orgcode">
                                            </div>
                                            
							</div>
							<div class="form-group">
								<label for="fencetype" class="col-sm-2 control-label">围栏类型</label>
								<div class="col-sm-10">
									<select class="form-control input-sm" id="fencetype">
										<option>监控进围栏</option>
										<option>监控出围栏</option>
									</select>
								</div>
							</div>
							
							<div class="form-group" style="height:120px;overflow-x: hidden;">
							<img class="col-sm-2 control-label" style="width:45px;margin-left: 10px;" id="addsj" src="/img/add.png">
							<label class="col-sm-2 control-label" style="margin-left: 33%;">监控时间</label>
								<table id="table">
				
			                    </table>
							</div>
							<div class="form-group" style="height: 120px; overflow-x: hidden">
								<img class="col-sm-2 control-label" style="width:45px;margin-left: 10px;" id="xzcl" src="/img/add.png">
							<label class="col-sm-2 control-label" style="margin-left: 33%;">绑定车辆</label>
								<table id="table2">
				
			                    </table>
							</div>
						</form>
					</div>
					<div class="modal-footer"
						style="height:50px;border: none;position: relative;top: -60px;right: 10px;">
						 
							
							<el-button id="save_wl" type="primary" data-dismiss="modal" size="small" icon="el-icon-check">保存</el-button> 
						</el-button-group>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal -->
		</div>
        

      
		<div class="modal fade" id="myModal_wl" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content" style="height: 350px;" >
					<div class="modal-header">
					<a class="close" data-dismiss="modal">×</a>
						<span class="modal-title" id="myModalLabel">围栏信息</span>
					</div>
					
					<div class="modal-body">
						<form class="form-horizontal" role="form" style="font-size: 14px;">
							<div class="form-group">
								<label for="fencename1" class="col-sm-2 control-label">围栏名称</label>
								<div class="col-sm-10">
									<input type="text" class="form-control input-sm" id="fencename1"
										placeholder="请输入围栏名称">
								</div>
							</div>
							<div class="form-group">
                                            <label for="fencetype" class="col-sm-2 control-label">所属机构</label>
                                            <div class="col-sm-10">
                                               <input type="text" readonly="readonly" class="form-control input-sm" id="orgcode1"
										      >
                                            </div>
                                            
                                        </div>
							<div class="form-group">
								<label for="fencetype1" class="col-sm-2 control-label">围栏类型</label>
								<div class="col-sm-10">
									<select class="form-control input-sm" id="fencetype1">
										<option>监控进围栏</option>
										<option>监控出围栏</option>
									</select>
								</div>
							</div>
													
						</form>
					</div>
					<div class="modal-footer"
						style="height:50px;border: none;position: relative;right: 10px;">						 							
							<el-button id="bc_wlxx" type="primary" data-dismiss="modal" size="small" icon="el-icon-check">保存</el-button> 
						</el-button-group>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal -->
		</div>

    <!-- 选择时间（Modal） -->
        <div class="modal fade" id="myModal_sj" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content" style="height: 190px;" >
					<div class="modal-header">
					<a class="close" data-dismiss="modal">×</a>
						<span class="modal-title" id="myModalLabel">选择时间</span>
					</div>
					
					<div class="modal-body">
					 <div class="form-group">
                                <label for="carnumber" class="col-sm-4 control-label">开始时间</label>
                                <div class="col-sm-6">
                                    <template>
                                        <div class="block">
                                            <el-time-picker 
                                            size="small" 
                                            style="width: 165px;" 
                                            v-model="value2"
                                            type="time" 
                                            placeholder="选择日期时间" 
                                            align="right" 
                                            id='value2' >
                                            
                                            </el-time-picker>
                                        </div>
                                    </template>

                                </div>
                            </div>
                            <div class="form-group">
                                <label for="carnumber" class="col-sm-4 control-label">结束时间</label>
                                <div class="col-sm-6">
                                    <template>
                                        <div class="block">
                                            <el-time-picker 
                                            size="small" 
                                            style="width: 165px;" 
                                            v-model="value3" 
                                            type="time" 
                                            placeholder="选择日期时间" 
                                            align="right" 
                                            id='value3'>
                                            </el-time-picker>
                                        </div>
                                    </template>
                                </div>
                            </div>
					</div>
					<div class="modal-footer"
						style="height:50px;border: none;position: relative;right: 10px;">						 							
							<el-button id="bc_wlsj" type="primary" size="small" icon="el-icon-check">确定</el-button> 
						</el-button-group>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal -->
		</div>

		<!-- 选择车辆模态框（Modal） -->
		 <div class="modal fade" id="myModal_cl" tabindex="0" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <a class="close" data-dismiss="modal">×</a>
                                    <span class="modal-title" id="myModalLabel">选择车辆</span>
                                </div>

                                <div class="modal-body">
                                    <form class="form-horizontal" role="form" style="font-size: 14px;">
                                      	<div class="form-group">
											<label for="orgcode" class="col-sm-2 control-label">车组</label>
											<div class="col-sm-7">
                                            	<select class="form-control input-sm" id="cargroup">
													<option></option>
												</select>
                                            </div>
										</div>
										<div class="form-group">
											<label for="orgcode" class="col-sm-2 control-label">车牌号</label>
											<div class="col-sm-7">
                                            	<input type="text" class="form-control input-sm" id="cph"
										      		placeholder="请输入车牌号">
                                            </div>
                                            <el-button class="col-sm-2" icon="el-icon-search" onclick="query()" size="small">查询</el-button> 
										</div>
                                        <div class="form-group" style="height: 230px; overflow-x: hidden">
                                            <label class="col-sm-2 control-label">车辆列表</label>
                                            <table class="table table-hover" style="font-size: 14px; height: 100px;">
                                                <thead>
                                                    <tr>
                                                        <th>
                                                        <input type="checkbox" id="choose" name="table"/>
                                                        </th>
                                                        <th>车牌号</th>
                                                        <th>车组名</th>

                                                    </tr>
                                                </thead>
                                                <tbody id="tb">

                                                </tbody>
                                            </table>
                                        </div>
                                    </form>

                                </div>
                                <div class="modal-footer" style="border: none;">
                                    <el-button type="primary" data-dismiss="modal" onclick="xzCar()" size="small" icon="el-icon-check">确定</el-button>
                                </div>
                            </div>
                            <!-- /.modal-content -->
                        </div>
                        <!-- /.modal -->
                    </div>
                
	</div>
	<script>
	   var Main = {
                        data() {
                            return {
                                value2: '',
                                value3: '',
                               
                          } 
                        }
                    }
                    var Ctor = Vue.extend(Main)
     new Ctor().$mount('#app')
  </script>
<script>
    var arr='';
    var orgcode='';
    var wlid='';
	var cz_data='';
	var orgid='' ;
	var pname='';
	var orgname='';
	var editor={};
    var data = [];
	window.parent.iframe_noCache_url();
    //amhone/app/main_frame/manage.html调用，用来给orgid赋值
	function getorgid(orid){
	    orgid=orid
	}
    
     var queryOrgName={
                        action:'com.cdms.QueryOrgName',                       
					    orgid:orgid, 
                      }
          jqueryManager_tb(queryOrgName,function(json){
                       orgname=json
                       $("#orgcode1").val(orgname);
                        });
    
		  var Data={
                        action:'com.cdms.guiji.GuiJiFindCarGroup',
                       
					    orgid:orgid,
					   
                      }
          jqueryManager(Data,function(json){
                       cz_data=json
                        });  
		 var Data1={
                        action:'com.cdms.guiji.FindP',                       
					    orgid:orgid,
					   
                      }
          jqueryManager_tb(Data1,function(json){
                       pname=json
                        });
		var selDom = $("#orgcode")
		for(var i=0;i<cz_data.length;i++){
              selDom.append("<option value='"+cz_data[i].orgname+"'>"+cz_data[i].orgname+"</option>");
			}
			
		//加载围栏列表
      function loadtable1(orgid){
        $("#table1").bootstrapTable('destroy'); 
		  orgid=orgid
       $('#table1').bootstrapTable({  		   
            url: "/p2p/com.cdms.Fence.do",  
            queryParamsType: 'limit',              //默认值为 'limit' ,在默认情况下 传给服务端的参数为：offset,limit,sort              
            method: "post",  
		    contentType: "application/x-www-form-urlencoded",
            pagination: true, 
			queryParams:queryParams,
            pageNumber: 1,  
            pageSize: 10,  
            pageList: [10, 20, 50, 100],  
            sidePagination: "server",         //分页方式：client客户端分页，server服务端分页（*）  
            striped: true,                    //是否显示行间隔色  
            cache: true, 
			dataType:"json",
            uniqueId: "id",
            height:420,               //每一行的唯一标识，一般为主键列    
            paginationPreText: "上一页",  
            paginationNextText: "下一页",  
            columns: [               
                { title: '围栏名称', field: 'electronic_fence_name' },  
                { title: '所属机构', field: 'orgname' }, 
				{  field: 'id' ,visible:false},
				{  field: 'type' ,visible:false},
				{  field: 'zonename' ,visible:false},
				{  field: 'orgcode' ,visible:false},
				{  field: 'electronic_fence_range' ,visible:false},
			    {
                 field: 'operate',
                 title: '操作',
			     events: 'operateEvents1',
                 formatter: operateFormatter1 //自定义方法，添加操作按钮
             }
              
            ]  ,
				responseHandler: function(res) {
				return res;
               
            },

          
    });  
      
};  

 function queryParams(params){
        return{
            //每页多少条数据
			orgid:orgid,
            pageSize:params.limit+"",
            //请求第几页
            pageIndex:params.pageNumber,
           
        }
    }
//加载围栏时间列表
function loadtable(id){
			 $("#table").bootstrapTable('destroy'); 
       $('#table').bootstrapTable({  		   
            url: "/p2p/com.cdms.Fence_sj.do?wlid="+id+"",  
            queryParamsType: '',              //默认值为 'limit' ,在默认情况下 传给服务端的参数为：offset,limit,sort              
            method: "post",  
            pagination: true,  
            pageNumber: 1,  
            pageSize: 50,  
            pageList: [10, 20, 50, 100],  
            sidePagination: "server",         //分页方式：client客户端分页，server服务端分页（*）  
            striped: true,                    //是否显示行间隔色  
            cache: false, 
			dataType:"json",
            uniqueId: "id",               //每一行的唯一标识，一般为主键列  
            height:100,  
            paginationPreText: "上一页",  
            paginationNextText: "下一页",  
            columns: [               
                { title: '开始时间', field: 'start_time' },  
                { title: '结束时间', field: 'end_time' }, 
				{  field: 'id' ,visible:false},
				{  field: 'type' ,visible:false},
				{  field: 'zonename' ,visible:false},
			    {
                 field: 'operate',
                 title: '操作',
			     events: 'operateEvents2',
                 formatter: operateFormatter2 //自定义方法，添加操作按钮
             }
              
            ]  ,
				
          
    });  
      
};     

//加载围栏车辆列表
function loadtable2(id){
		$("#table2").bootstrapTable('destroy'); 
       $('#table2').bootstrapTable({  		   
            url: "/p2p/com.cdms.Fence_cl.do?wlid="+id+"",  
            queryParamsType: '',              //默认值为 'limit' ,在默认情况下 传给服务端的参数为：offset,limit,sort              
            method: "post",  
            pagination: true,  
            pageNumber: 1,
            pageSize: 50, 
            pageList: [10, 20, 50, 100],  
            sidePagination: "server",         //分页方式：client客户端分页，server服务端分页（*）  
            striped: true,                    //是否显示行间隔色  
            cache: false, 
			dataType:"json",
            uniqueId: "id",//每一行的唯一标识，一般为主键列  
            height:100,               
            paginationPreText: "上一页",  
            paginationNextText: "下一页",  
            columns: [               
                { title: '车牌号码', field: 'cph' },  
                { title: '车组名', field: 'orgname' }, 
				{  field: 'id' ,visible:false},
				{  field: 'type' ,visible:false},
				{  field: 'zonename' ,visible:false},
			    {
                 field: 'operate',
                 title: '操作',
			     events: 'operateEvents3',
                 formatter: operateFormatter3 //自定义方法，添加操作按钮
             }
              
            ] 
          
    });  
      
};     
function operateFormatter1(value, row, index) {//赋予的参数
     return [
         '<a class="scan" href="javascript:void("'+value+'")"><img id="scan" src="../img/fencesetting.png" style="width:20px;padding:2px"></a>',
         '<a class="update" href="javascript:void(0)" ><img src="../img/editmap.png" style="width:20px;padding:2px"></a>',
         '<a class="delete1" href="javascript:void(0)" ><img  src="../img/setting_delete.png" style="width:20px;padding:2px"></a>'
         
     ].join('');
 }
 function operateFormatter2(value, row, index) {//赋予的参数
     return [
        
         '<img class="delete2" src="../img/setting_delete.png" style="width:20px;padding:2px">'
         
     ].join('');
 }
  function operateFormatter3(value, row, index) {//赋予的参数
     return [
        
         '<img class="delete3" src="../img/setting_delete.png" style="width:20px;padding:2px">'
         
     ].join('');
 }
 //设置围栏
 

    
var wlid='';
window.operateEvents1 = {  
        'click .scan': function(e, value, row, index) {
             $("#myModal").modal('show');
            $("#fencename").val(row.electronic_fence_name);  
            $("#orgcode").val(row.orgname);
			 $("#fencetype").val(row.type);  
            wlid=row.id;
			
			orgcode=row.orgcode;
           loadtable(wlid)
		   loadtable2(wlid)
        } ,
			 'click .update': function(e, value, row, index) { 
                map.clearMap();
                $("#bc").css("display", "inline"); 
			    wlid=row.id;
			   var arrs=row.electronic_fence_range;
                 
			   arr=eval ("(" + arrs+ ")");
			    
	editor._polygonEditor= new AMap.PolyEditor(map, editor._polygon=(function(){
        			
        return new AMap.Polygon({
            map: map,
            path: arr,
            strokeColor: "#0000ff",
            strokeOpacity: 1,
            strokeWeight: 3,
            fillColor: "#f5deb3",
            fillOpacity: 0.35
        });
            
    })());
                map.setFitView();
                   
				editor._polygonEditor.open();
        } ,
		'click .delete1':function(e,value,row,index){
		     
		     disp_confirm(row)
		
		
		}	
    }; 
//删除时间
   window.operateEvents2 = {  
	  
		'click .delete2':function(e,value,row,index){
		     disp_confirm1(row)	
		}	
    }; 

//删除车辆

 window.operateEvents3 = {         
		'click .delete3':function(e,value,row,index){	     
		     disp_confirm2(row)	
		}	
    }; 
//修改围栏范围
editor.closeEditPolygon=function(){
	editor._polygonEditor.close();
	data =  editor._polygon.getPath();
    var Data={
                action:'com.cdms.DzelSave',
                electronic_fence_range: getString(data),
				fencetype:$("#fencetype").val(),
				id:wlid,						
				type:'3'
            }
    jqueryManager(Data,function(json){
            $.myAlert({title:'提示',message:'保存成功',callback:function(){location.reload()}})
          
    });
    }




//保存围栏
 $('#save_wl').click(function () {
	   var Data={
                        action:'com.cdms.DzelSave',
                        electronic_fence_name:$("#fencename").val(),
						orgname:$("#orgcode").val(),
                        fencetype:$("#fencetype").val(),
					    id:wlid,
						type:'2'
                      }
          jqueryManager(Data,function(json){
						   $.myAlert({title:'提示',message:'保存成功',callback:function(){location.reload()}})
                        });  
					   
       
    });

 loadtable1(orgid)
 //添加围栏
 var $add = $('#add');
  $add.click(function () {
	     $("#myModal_wl").modal('show');
       
    });
  //添加时间
  $('#addsj').click(function () {
	     $("#myModal_sj").modal('show');
       
    });
 //选择车辆
  $('#xzcl').click(function () {
	     $("#myModal_cl").modal('show');
        czquery();
       
    });
    
//添加围栏信息
 $('#bc_wlxx').click(function(){
 	
	 var electronic_fence_name=$("#fencename1").val();
     if(electronic_fence_name!=''&&typeof(electronic_fence_name)!='undefined'){
        var fencetype=$("#fencetype1").val();
     var Data={
                action:'com.cdms.DzelSave',
                electronic_fence_name:electronic_fence_name,
			    orgname:orgname,
                fencetype:fencetype,
			    id:wlid,
			    type:'2'
    }
    jqueryManager(Data,function(json){
        location.reload()
    });

    var index =  $('#table1').bootstrapTable('getData').length;
         $('#table1').bootstrapTable('insertRow', {
            index: index,
            row: {
                electronic_fence_name: electronic_fence_name,
                orgname: orgname,
				fencetype:fencetype,
                id:wlid,
            }
		 })
     }else{
        $.myAlert({title:'提示',message:'保存失败，围栏名称不能为空',callback:function(){}})
     }
	 
        });
//添加围栏时间
 $('#bc_wlsj').click(function(){
	 var start_time =$("#value2").val();
	 var end_time=$("#value3").val();
     console.info('start_time.length='+start_time.length);
     if(start_time.length==0||end_time.length==0){
        alert('时间不能为空！')

     }else{
        var id=uuid().replace(/-/g, "");
     var Data={
                        action:'com.cdms.WlsjSave',
                        start_time:start_time,
						end_time:end_time,
                        wlid:wlid,
					    id:id,
						type:'1'
                      }
          jqueryManager(Data,function(json){
                        });

      var index =  $('#table').bootstrapTable('getData').length;
         $('#table').bootstrapTable('insertRow', {
            index: index,
            row: {
                start_time: start_time,
                end_time: end_time,
				id:id

            }
         })
         $('#myModal_sj').modal('hide');
     }	 
	 
        });

	$("#choose").change(function(){
			if($("input[id='choose']").is(':checked') ) {
				$("input[name='table']").each(function(){ 
					$("input[name='table']").prop("checked", true);
				})
			}else{
				$("input[name='table']").each(function(){ 
					$("input[name='table']").removeAttr("checked");
				})
				
			}
	})
	
	//选择机构时刷新车辆列表
	$("#cargroup").change(function(){
        orgcode = $("#cargroup").val();
        //更新车辆列表
        czquery();
	})

//选择车辆
function xzCar(){
		
		var carList = $("input[class='checked_pro']:checked");
		for (var i = 0; i < carList.length; i++) {
		var cdata= jQuery.parseJSON(carList[i].value)
				   var cph=cdata.cph;
			       var orgname=cdata.orgname;
				   var car_id=cdata.id;
                   var id=uuid().replace(/-/g, "");
				   var Data={
                        action:'com.cdms.WlclSave',
                        id:id,
						car_id:car_id,
                        wlid:wlid,
					    
                      }
          jqueryManager(Data,function(json){
                        });
               	 var index =  $('#table2').bootstrapTable('getData').length;
         $('#table2').bootstrapTable('insertRow', {
            index: index,
            row: {
                cph: cph,
                orgname: orgname,
				id:id

            }
		 })

	}
                         
	           					
}
               
                  function carxx(Data){
                  $(".clear").remove();  //移出,避免重复添加.
                    jqueryManager(Data,function(json){
                        if(json!=''){
                          eval("data="+json); 
						  
                        data.forEach(d => {
							  var data={id:d.CAR_ID,cph:d.LICENSE_PLATE_NUMBER,orgname:d.ORGNAME}
							  data=JSON.stringify(data)
                            $("#tb").append('<tr class="clear"><td><input class="checked_pro" name="table" id="'+d.DEVICE_SN_NUMBER+'" type="checkbox" value='+data+'></td><td>'+d.LICENSE_PLATE_NUMBER+'</td><td>'+d.ORGNAME+'</td></tr>')
                        });
                    }
                      }) 
                  }
                  function czquery(){ 
                    orgcode=$("#cargroup").val();
                    var Data={
                        action:'com.cdms.FindCarListByorgid',
                        orgid:orgid,//当前登录机构
                        orgcode:orgcode,//当前选择机构
                        wlid:wlid
                      }
                      carxx(Data) 
                  }
                  //车牌号模糊搜索刷新车辆列表
                  function query(){
                  var cph = $("#cph").val();
                    var Data={
                        action:'com.cdms.FindCarListByorgid',
                        cph:cph,
                        wlid:wlid
                      }
                      carxx(Data) 
                  }  
 				function initcargroup(){
 					$(".group").remove();  //移出,避免重复添加.
 					var initcargroup={
                        action:'com.cdms.InitCarGroup',
                        orgid:orgid
                      } 
                    jqueryManager(initcargroup,function(json){
                        if(json!=''){
                        
                          eval("data="+json); 
						  
                        data.forEach(d => {
							  var data={orgid:d.ORGID,orgname:d.ORGNAME}
							  data=JSON.stringify(data)
                            $("#cargroup").append('<option class="group" value="'+d.ORGID+'">'+d.ORGNAME+'</option>')
                        });
                    }
                      })
 				}
 				initcargroup();
	//新增机构
      function orgxz(orgname){
	     var orgname=orgname;
	    $("#orgcode").val(orgname);
	  } 
	  
   //修改机构
     function orgup(orgname){
	     var orgname=orgname;
	    $("#orgcode1").val(orgname);
	  } 

 //地图
 var  district, polygons  = [], citycode;
   var citySelect = document.getElementById('city');
    var districtSelect = document.getElementById('district');
    var areaSelect = document.getElementById('street');

    var editorTool, map = new AMap.Map("container", {
        resizeEnable: true,
        zoom: 18
       
    });
     var mouseTool = new AMap.MouseTool(map);
    AMap.event.addDomListener(document.getElementById('polygon'), 'click', function() {
    	map.clearMap();
        mouseTool.polygon();
    }, false);
	//手动绘制围栏双击完成
    AMap.event.addListener(mouseTool, 'draw', function(type,obj) {
    	var polygonItem = type.obj;
    	var path = polygonItem.getPath();//取得绘制的多边形的每一个点坐标
    	if(path.length<=3){
    		$.myAlert({title:'提示',message:'请至少选择3个点！',callback:function(){map.clearMap();}})
     	}else{
     	
     		wlid=uuid().replace(/-/g, "");
        	var Data={
            	action:'com.cdms.DzelSave',
            	electronic_fence_range: getString(path),
				fencetype:$("#fencetype").val(),
				orgname:orgname,
				id:wlid,						
				type:'1'
        	}
        	jqueryManager(Data,function(json){
        		map.clearMap();// 清除地图覆盖物    
        		$("#myModal_wl").modal('show'); 
        	})
     	}

	}); 
    //行政区划查询
    var opts = {
        subdistrict: 1,   //返回下一级行政区
        showbiz:false,  //最后一级返回街道信息
		extensions:'base'//不绘制省级边界，因为点太多了
    };
    district = new AMap.DistrictSearch(opts);//注意：需要使用插件同步下发功能才能这样直接使用
    district.search(pname, function(status, result) {
        if(status=='complete'){
            getData(result.districtList[0]);
        }
    });
	var bounds='';
	
    function getData(data,level) {
         bounds = data.boundaries;		
        if (bounds) {
            for (var i = 0, l = bounds.length; i < l; i++) {
                var polygon = new AMap.Polygon({
                    map: map,
                    strokeWeight: 1,
                    strokeColor: '#CC66CC',
                    fillColor: '#CCF3FF',
                    fillOpacity: 0.5,
                    path: bounds[i]
                });
                polygons.push(polygon);
            }
            map.setFitView();//地图自适应
        }
       
        
        
        //清空下一级别的下拉列表
        if (level === 'province') {
            citySelect.innerHTML = '';
            districtSelect.innerHTML = '';
            areaSelect.innerHTML = '';
        } else if (level === 'city') {
            districtSelect.innerHTML = '';
            areaSelect.innerHTML = '';
        } else if (level === 'district') {
            areaSelect.innerHTML = '';
        }

        var subList = data.districtList;
        if (subList) {
            var contentSub = new Option('--请选择--');
            var curlevel = subList[0].level;
            var curList =  document.querySelector('#' + curlevel);
            curList.add(contentSub);
            for (var i = 0, l = subList.length; i < l; i++) {
                var name = subList[i].name;
                var levelSub = subList[i].level;
                var cityCode = subList[i].citycode;
                contentSub = new Option(name);
                contentSub.setAttribute("value", levelSub);
                contentSub.center = subList[i].center;
                contentSub.adcode = subList[i].adcode;
                curList.add(contentSub);
            }
        }
        
    }
    function search(obj) {
        if(obj.options.selectedIndex===0){
            //如果市选择空，则清空区的下拉选项
            districtSelect.innerHTML = '';
            areaSelect.innerHTML = '';
            map.clearMap();
            bounds='';
        }else{

        
            //清除地图上所有覆盖物
            for (var i = 0, l = polygons.length; i < l; i++) {
                polygons[i].setMap(null);
            }
                var option = obj[obj.options.selectedIndex];
            var keyword = option.text; //关键字
            var adcode = option.adcode;
            district.setLevel(option.value); //行政区级别
            district.setExtensions('all');
            //行政区查询
            //按照adcode进行查询可以保证数据返回的唯一性
            district.search(adcode, function(status, result) {
                if(status === 'complete'){
                    getData(result.districtList[0],obj.id);
                }
            });
        }
    }
    function setCenter(obj){
        map.setCenter(obj[obj.options.selectedIndex].center)
    }


    
    
    //自动绘制
    $('#zdhz').click(function(){
	    var bound='';
        if(typeof(bounds)=='undefined'||bounds===''){
                
                $.myAlert({title:'提示',message:'请选择围栏绘制范围！',callback:function(){}})
        }else{
            for(i = 0; i <bounds.length; i++){
                bound+=getString(bounds[i])+',';
            }
            bound=bound.substring(0,bound.length-1)

            wlid=uuid().replace(/-/g, "");
            var Data={
                action:'com.cdms.DzelSave',
                electronic_fence_range:bound,
                fencetype:$("#fencetype").val(),
                id:wlid,
                orgname:orgname,						
                type:'1'
            }
            jqueryManager(Data,function(json){
                    $("#myModal_wl").modal('show');    
            })
        }   
    });  
  
  //生成uuid
  function uuid(len, radix) {
          var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
          var chars = CHARS, uuid = [], i;
          radix = radix || chars.length;

          if (len) {
            // Compact form
            for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
          } else {
            // rfc4122, version 4 form
            var r;
            // rfc4122 requires these characters
            uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
            uuid[14] = '4';
            // Fill in random data.  At i==19 set the high bits of clock sequence as
            // per rfc4122, sec. 4.1.5
            for (i = 0; i < 36; i++) {
              if (!uuid[i]) {
                r = 0 | Math.random()*16;
                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
              }
            }
          }
          return uuid.join('');
        };

  //二维数组转字符串
   function getString( objarr ){
　　var typeNO = objarr.length;
  　 var tree = '[';
 　　for (var i = 0 ;i < typeNO ; i++){
   　　　tree += '[';
   　　　tree +='"'+ objarr[i].lng+'",';
   　　　tree +='"'+ objarr[i].lat+'"';
  　　　 tree += ']';
  　　　 if(i<typeNO-1){
    　　 　　tree+=',';
 　　　  }
  　 }
  　 tree+=']';
  　 return tree;
}
//提示框
 function disp_confirm(row)
  {
  var r=confirm("确定删除此围栏？删除后不能恢复！")
  if (r==true)
    {
        // $('#table1').bootstrapTable('remove', {
        //     field: 'id',
        //     values: row.id
        // });
       var Data={
                        action:'com.cdms.Dzeldelete',                    		   
					    id:row.id,						
						type:'1'
                      }
          jqueryManager(Data,function(json){
              console.info(orgid);
                        loadtable1(orgid)
                        });
	

    }
  else
    {
    
    }
  }
  function disp_confirm1(row)
  {
  var r=confirm("确定删除此时间段？删除后不能恢复！")
  if (r==true)
    {
        // $('#table').bootstrapTable('remove', {
        //     field: 'id',
        //     values: row.id
        // });
       var Data={
                        action:'com.cdms.Dzeldelete',                    		   
					    id:row.id,						
						type:'2'
                      }
          jqueryManager(Data,function(json){
                            console.info(wlid);
                            loadtable(wlid)
                        });
	

    }
  else
    {
    
    }
  }
  function disp_confirm2(row)
  {
  var r=confirm("确定删除此车辆？删除后不能恢复！")
  if (r==true)
    {
        // $('#table2').bootstrapTable('remove', {
        //     field: 'id',
        //     values: row.id
        // });
       var Data={
                        action:'com.cdms.Dzeldelete',                    		   
					    id:row.id,						
						type:'3'
                      }
          jqueryManager(Data,function(json){
                        loadtable2(wlid)
                        });
	

    }
  else
    {
    
    }
  }
</script>
</body>
</html>



