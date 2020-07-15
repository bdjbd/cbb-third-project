
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <script src="downloadapp/jquery.min.js" type="text/javascript"></script>
    </head>
    <body>
	终端编号<input id="sn" value="66666">
	<br>
	纬度<input id="lat" value="34.321273">
	<br>
	经度<input id="lng" value="108.946651">
	<br>
	里程<input id="m" value="1000">
	<br>
	油耗<input id="o" value="100">
	<br>
    <button id="send">位置上报</button>

    </body>
    <script>
		
        $("#send").click(function () {
			var lat = $('#lat').val();
			var lng = $('#lng').val();
			var sn = $('#sn').val();
			var m = $('#m').val();
			var o = $('#o').val();
			console.info('纬度='+lat+'，经度='+lng);
			var data = {
	"terminalId": sn,
	"alarmFlag": {
		"alarmFlag_0": 0,
		"alarmFlag_1": 0,
		"alarmFlag_2": 0,
		"alarmFlag_3": 0,
		"alarmFlag_4": 0,
		"alarmFlag_5": 0,
		"alarmFlag_6": 0,
		"alarmFlag_7": 0,
		"alarmFlag_8": 0,
		"alarmFlag_9": 0,
		"alarmFlag_10": 0,
		"alarmFlag_11": 0,
		"alarmFlag_12": 0,
		"alarmFlag_13": 0,
		"alarmFlag_14": 0,
		"alarmFlag_15": 0,
		"alarmFlag_16": 0,
		"alarmFlag_17": 0,
		"alarmFlag_18": 0,
		"alarmFlag_19": 0,
		"alarmFlag_20": 0,
		"alarmFlag_21": 0,
		"alarmFlag_22": 0,
		"alarmFlag_23": 0,
		"alarmFlag_24": 0,
		"alarmFlag_25": 0,
		"alarmFlag_26": 0,
		"alarmFlag_27": 0,
		"alarmFlag_28": 0,
		"alarmFlag_29": 0,
		"alarmFlag_30": 0,
		"alarmFlag_31": 0
	},
	"status": {
		"status_0": 1,
		"status_1": 1,
		"status_2": 0,
		"status_3": 0,
		"status_4": 0,
		"status_5": 0,
		"status_6": 0,
		"status_7": 0,
		"status_8": 0,
		"status_9": 0,
		"status_10":0,
		"status_11": 0,
		"status_12": 0,
		"status_13": 0,
		"status_14": 0,
		"status_15": 0,
		"status_16": 0,
		"status_17": 0,
		"status_18": 0,
		"status_19": 0,
		"status_20": 0,
		"status_21": 0,
		"status_22": 0,
		"status_23": 0,
		"status_24": 0,
		"status_25": 0,
		"status_26": 0,
		"status_27": 0,
		"status_28": 0,
		"status_29": 0,
		"status_30": 0,
		"status_31": 0
	},
	"location": {
		"latitude": lat,
		"longitude": lng,
		"high": 12,
		"speed": 14,
		"direc": 2,
		"datetime": "20180515094130"
	},
	"appendInfo": {
		"mileage": m,
		"oilmass": o,
		"drivingRecordspeed": 23,
		"alarmEventId": "1qw345"
	},
	"ObdRealTimeDataRpt": {
		"levelVoltage": 32,
		"engineSpeed": 12,
		"instantaneousSpeed": 34,
		"throttleOpening": 43,
		"engineLoad": 34,
		"coolantTemperature": 23,
		"instFuelConsumption": 43,
		"totalMileage":26,
		"accOilConsumption": 66
	},
	"ObdOverDataRpt": {
		"averageSpeed": 23,
		"averageFuelConsumption": 14,
		"travelMileage": 65,
		"oilConsumption": 72
	},
	"TerminalVehicleRpt": {
		"startDateTime": "2080101120000",
		"referenceSource": 1,
		"travelDuration": 2,
		"resetDuration": 3,
		"rapidAccelerationCnt": 0,
		"rapidDecelerationCnt": 0,
		"oilConsumption": 0,
		"drivingMileage": 0,
		"sharpTurnCnt": 0,
		"steepRoadCnt": 0
	}
};

            $.ajax({
                url:'/AmRes/com.am.nw.api.ApiReportingTerminalLocationData.do',
                type:"POST",
                data:{terminalLocationParams:JSON.stringify(data)},
                dataType:'json',
                success:function(data){
                            alert(JSON.stringify(data));
                }
        } );
        });
    </script>
</html>
