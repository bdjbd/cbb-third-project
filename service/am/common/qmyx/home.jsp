<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="com.fastunit.jdbc.DB,com.fastunit.jdbc.DBFactory
,com.fastunit.jdbc.exception.JDBCException" %>
<%@ page import="org.json.JSONArray
,com.fastunit.MapList
,com.fastunit.Page
,com.fastunit.Row
,com.fastunit.context.ActionContext
,com.fastunit.jdbc.DB
,com.fastunit.jdbc.DBFactory
,com.fastunit.jdbc.Type
,com.fastunit.jdbc.exception.JDBCException
,com.fastunit.support.PageInterceptor
,com.fastunit.util.Checker
,com.am.frame.webapi.db.DBManager
,com.am.qmyx.home.QueryHomeData" %>
<html style="width:100%;height:100%;background-color: #F1F1F1;">
    <head>
	<%
    	//近一月推广员人数
    	String totalNumber = QueryHomeData.getIstance().queryTotalNumber();
    	//近一月订单量
    	String orderTotalNumber = QueryHomeData.getIstance().queryOrderTotalNumber("-1 month");
    	//近一月检票数
    	String theTicketNumber = QueryHomeData.getIstance().queryTheTicketNumber();
    	//近一月订单金额
    	String orderMoney = QueryHomeData.getIstance().queryOrderMoney();
    	//近一周订单量
    	String weeklyOrderQuantity = QueryHomeData.getIstance().queryOrderTotalNumber("-7 day");
    	//近三月订单量
    	String marchOrderQuantity = QueryHomeData.getIstance().queryOrderTotalNumber("-3 month");
    	//公告
    	String list_notice = QueryHomeData.getIstance().queryNotice();
    	//近一月订单金额图表
    	String echarts_money_data = QueryHomeData.getIstance().queryDateTotalMoney();
    	//近一月订单数图表
    	String list_order_total_data = QueryHomeData.getIstance().queryDateOrderTotal();
    	//近一月检票数图表
    	String list_theticket_total_data = QueryHomeData.getIstance().queryDateTheticketTotal();
    	//近一月推广员人数图表
    	String list_member_total_data = QueryHomeData.getIstance().queryDateMemberTotal();
    	

    %>

    	<!-- 最新版本的 Bootstrap 核心 CSS 文件 -->
		<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
		<!-- 可选的 Bootstrap 主题文件（一般不用引入） -->
		<link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css" >
		<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
		<script src="js/jquery.min.js"></script>
		<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<!-- 百度echarts图表 -->
		<script src="js/echarts.min.js"></script>

		<style>
			.button_div{
				border-radius: 500px;
				width: 70px;
				height: 70px;
				border: 2px solid #ddd;
				text-align: center;
				line-height: 3.5;
				margin: auto;
				color: #5c85ca;
				font-size: 20px;
			}
			.price_div{
				width: 70px;
				height: 70px;
				text-align: center;
				line-height: 3.5;
				font-size: 20px;
				margin: auto;
				color: #fc8824;
			}

			.order_div{
				margin-top: 20px;
			    margin-left: 10px;
			    border-bottom: 1px solid #ddd;
			    padding-bottom: 13px;
			    padding-left: 6px;
			}
			.Refresh_button
			{
				float: right;
    			color: #a2a3a4;
    			cursor: pointer;
			}
			.list-group-item{
				border-radius: 0px !important;
			}
			.list-group{
				box-shadow: none !important;
    			border-radius: 0px !important;
			}
			.echart_list{
				border-radius: 0px;
				height: 65.5px;
				cursor: pointer;
			}
			.list-notice{
				height: 41.9px;
				border: 0px;
				color: red;
			}
			.time_class{
				font-size: 10px;
				margin-left: 8px;
				margin-right: 10px;
			}
			.time_title{
				font-size: 10px;
				margin-left: 11px;
				color: red;
			}
			.img_new{
				background-image: url(new.png);
			    background-size: cover;
			    width: 26px;
			    height: 26px;
			    float: right;
			    background-repeat: no-repeat;
			    margin-top: -3px;
			}
			.img_point{
			    height: 41px;
				/*margin-top: -17px;*/
			}
			.img_background{
				background-image: url(background.png) ;
				background-size:115px 17px;
			    background-repeat: no-repeat;
			    margin-top: -3px;
			}
			.cr{
				text-align: right;
			}
			.cl{
				text-align: left;
			}

		</style>

        <title>统计</title>
    </head>
    <body style="background-color: #F1F1F1;">
	   <div style="width: 99%; padding-top: 10px;margin: auto;">
	   		<div style="width: 70%;float: left;background-color: #fff;padding-top: 18px;">
		   		<div style="border-bottom: 1px solid #ddd;padding-bottom: 13px;">
		   			<div class="row">
					  <div class="col-md-3 text-center">
					  	<div class="button_div"><%=totalNumber%></div>
					  	<div style="padding-top: 5px;">近一个月推广员数</div>
					  </div>
					  <div class="col-md-3 text-center">
					  	<div class="button_div"><%=orderTotalNumber%></div>
					  	<div style="padding-top: 5px;">近一个月订单量</div>
					  </div>
					  <div class="col-md-3 text-center">
					  	<div class="button_div"><%=theTicketNumber%></div>
					  	<div style="padding-top: 5px;">近一个月检票</div>
					  </div>
					  <div class="col-md-3 text-center">
					  	<div class="price_div" style="width: auto;"><span>￥</span><%=orderMoney%></div>
					  	<div style="padding-top: 5px;">近一个月订单金额</div>
					  </div>
					</div>
		   		</div>

		   		<div style="height: 5px;background-color: #F1F1F1;"></div>
		   		<div>
		   			<div class="row" style="height: 70px;">
					  <div class="col-xs-6 col-md-4">
					  	<div class="order_div">
					  		近一周订单量
					  		<i class="glyphicon glyphicon-repeat Refresh_button" onclick="refresh()"></i>
					  	</div>
					  	<div style="height:183px;">
					  		<div id="main" style="height:170px;"></div>
					  	</div>
					  </div>
					  <div class="col-xs-6 col-md-4">
					  	<div class="order_div">近一月订单量<i class="glyphicon glyphicon-repeat Refresh_button" onclick="refresh()"></i></div>
					  	<div>
					  		<div id="main_2" style="height:170px;"></div>
					  	</div>
					  </div>
					  <div class="col-xs-6 col-md-4">
					  	<div class="order_div">近三月订单量
					  	<i class="glyphicon glyphicon-repeat Refresh_button" style="left: -10px;" onclick="refresh()"></i></div>
					  	<div>
					  		<div id="main_3" style="height:170px;"></div>
					  	</div>
					  </div>
					</div>
		   		</div>
	   		</div>
	   		<div style="width: 29.5%;float: right;">
	   			<div class="row" style="background: #fff;margin: 0px;font-size: 12px;padding-top: 13.5px;padding-bottom: 10px;">
				  <div class="col-md-8 cl">系统公告</div>
				  <div class="col-md-4 cr"><a href="/am_bdp/qmyx_announcement_management.do?m=s&clear=am_bdp.qmyx_announcement_management.query">更多</a></div>
				</div>
	   			<ul class="list-group" style="margin-bottom:0px;height: 328.33px;background: #fff;" id="list_notice">
				</ul>
	   		</div>
	   </div>

	   	<div style="width: 99%;margin: auto;clear: both;padding-top: 5px;">
	   		<div style="width: 27%;float: left;">
	   			<ul class="list-group" style="margin-bottom: 6px;box-shadow: none;">
				  <li class="list-group-item echart_list" onclick="select('1')">
				  	<div style="float: left;line-height: 2;color: #5c85ca;"><span style="font-size: large;">￥</span><span style="font-size: 20px;"><%=orderMoney%></span></div>
				  	<div style="margin-left: 110px;line-height: 3;text-align: right;">近一个月订单金额</div>
				  </li>
				  <li class="list-group-item echart_list" onclick="select('2')">
				  	<div style="float: left;line-height: 2;color: #5c85ca;"><span style="font-size: 20px;"><%=orderTotalNumber%></span></div>
				  	<div style="margin-left: 125px;line-height: 3;text-align: right;">近一个月订单量</div>
				  </li>
				  <li class="list-group-item echart_list" onclick="select('3')">
				  	<div style="float: left;line-height: 2;color: #5c85ca;"><span style="font-size: 20px;"><%=theTicketNumber%></span></div>
				  	<div style="margin-left: 139px;line-height: 3;text-align: right;">近一个月检票</div>
				  </li>
				  <li class="list-group-item echart_list" onclick="select('4')">
				  	<div style="float: left;line-height: 2;color: #5c85ca;"><span style="font-size: 20px;"><%=totalNumber%></span></div>
				  	<div style="margin-left: 110px;line-height: 3;text-align: right;">近一个月推广员数</div>
				  </li>
				</ul>
	   		</div>
	   		<div style="width: 73%;float: left;background-color: #fff;height: 257px;margin-top: 0.5px;overflow-x: auto;overflow-y: hidden;">
	   			<div id="echart_title" style="text-align: center;margin-top: 20px;"></div>
	   			<div id="echart_statistics" style="height: 220px;"></div>
	   		</div>
	   	</div>



		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title" id="myModalLabel">
							
						</h4>
					</div>
					<div class="modal-body" id="myModalContent">
						在这里添加一些文本
					</div>
					
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>

    </body>
    <script type="text/javascript">

    	function refresh(){
		    window.location.reload();//刷新当前页面.
		     
		    //或者下方刷新方法
		    //parent.location.reload()刷新父亲对象（用于框架）--需在iframe框架内使用
		    // opener.location.reload()刷新父窗口对象（用于单开窗口
		  //top.location.reload()刷新最顶端对象（用于多开窗口）
		}


    	//显示公告
    	$("#list_notice").html("<%=list_notice%>");



    	//处理点击公告弹出模态层
    	function show_model(params){
    		$("#myModalLabel").html($(params).attr("data-title"));
    		$("#myModalContent").html($(params).attr("data-title"));
    		$('#myModal').modal('show');
    	};




    	var echarts_title="";
    	
    	var myChart = echarts.init(document.getElementById('main'));
    	var myChart_2 = echarts.init(document.getElementById('main_2'));
    	var myChart_3 = echarts.init(document.getElementById('main_3'));
    	var echart_statistics = echarts.init(document.getElementById('echart_statistics'));

		var option = {
		    series: [
		        {
		            name:'近一周订单量',
		            type:'pie',
		            radius: ['45%', '65%'],
		            avoidLabelOverlap: false,
		            color:['#fc8824','transparent'],
		            label: {
		                normal: {
		                    show: true,
		                    position: 'center',
		                    textStyle:{
		                        fontSize:'30',
		                        color:'#000'
		                    }
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: true
		                }
		            },
		            data:[
		                {value:70, name:'<%=weeklyOrderQuantity%>'},
		                {value:30, name:''}
		            ]
		        }
		    ]
		};

		var option_2 = {
		    series: [
		        {
		            name:'近一月订单量',
		            type:'pie',
		            radius: ['45%', '65%'],
		            avoidLabelOverlap: false,
		            color:['#3dc9fc','transparent'],
		            label: {
		                normal: {
		                    show: true,
		                    position: 'center',
		                    textStyle:{
		                        fontSize:'30',
		                        color:'#000'
		                    }
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: true
		                }
		            },
		            data:[
		                {value:70, name:'<%=orderTotalNumber%>'},
		                {value:30, name:''}
		            ]
		        }
		    ]
		};

		var option_3 = {
		    series: [
		        {
		            name:'近三月订单量',
		            type:'pie',
		            radius: ['45%', '65%'],
		            avoidLabelOverlap: false,
		            color:['#94c432','transparent'],
		            label: {
		                normal: {
		                    show: true,
		                    position: 'center',
		                    textStyle:{
		                        fontSize:'30',
		                        color:'#000'
		                    }
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: true
		                }
		            },
		            data:[
		                {value:70, name:'<%=marchOrderQuantity%>'},
		                {value:30, name:''}
		            ]
		        }
		    ]
		};



		var echart_statisticsoption = {
		    tooltip: {
		        trigger: 'axis'
		    },
		    legend: {
		        data:[""]
		    },
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '3%',
		        containLabel: true
		    },
		    xAxis: {
		        type: 'category',
		        boundaryGap: false,
		        data: []
		    },
		    yAxis: {
		        type: 'value'
		    },
		    series: [
		        {
		            name:"",
		            type:'line',
		            stack: '总量',
		            data:[]
		        }
		    ]
		};




		myChart.setOption(option);
		myChart_2.setOption(option_2);
		myChart_3.setOption(option_3);

		function select(type)
    	{
    		if(type=='1')
    		{
    			var data =<%=echarts_money_data%>;
    			$("#echart_title").html("近一个月订单金额趋势图");
    			echart_statisticsoption.legend.data=["订单额(单位:元)"];
    			echart_statisticsoption.series[0].name="订单额(单位:元)";
    			echart_statisticsoption.xAxis.data=data.date;
    			echart_statisticsoption.series[0].data=data.data;
    		}else if(type=='2')
    		{
    			var data =<%=list_order_total_data%>;
				$("#echart_title").html("近一个月订单量趋势图");
				echart_statisticsoption.legend.data=["订单量(单位:个)"];
    			echart_statisticsoption.series[0].name="订单量(单位:个)";
    			echart_statisticsoption.xAxis.data=data.date;
    			echart_statisticsoption.series[0].data=data.data;
    		}else if(type=='3')
    		{
    			var data =<%=list_theticket_total_data%>;
				$("#echart_title").html("近一个月检票趋势图");
				echart_statisticsoption.legend.data=["检票额(单位:元)"];
    			echart_statisticsoption.series[0].name="检票额(单位:元)";
    			echart_statisticsoption.xAxis.data=data.date;
    			echart_statisticsoption.series[0].data=data.data;
    		}else
    		{
    			var data =<%=list_member_total_data%>;
    			$("#echart_title").html("近一个月推广员数趋势图");
    			echart_statisticsoption.legend.data=["推广员数(单位:位)"];
    			echart_statisticsoption.series[0].name="推广员数(单位:位)";
    			echart_statisticsoption.xAxis.data=data.date;
    			echart_statisticsoption.series[0].data=data.data;
    		}
    		echart_statistics.setOption(echart_statisticsoption);
    	};
    	select('1');
    </script>
</html>
