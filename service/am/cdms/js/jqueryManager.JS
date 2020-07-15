
    function jqueryManager(data,success,unsuccess){
        $.ajax({
            type: "POST",
            url:"/p2p/"+data.action+".do",
            data:data,
            success:success,
            error:unsuccess 
        });
    }
