
    function jqueryManager_tb(data,success,unsuccess){
        $.ajax({
			async:false,
            type: "POST",
            url:"/p2p/"+data.action+".do",
            data:data,
            success:success,
            error:unsuccess 
        });
    }
