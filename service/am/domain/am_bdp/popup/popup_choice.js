/**
 * Created by Administrator on 2016/6/20
 */

function openDialog(s,popupUnitName){
    var index=0;
    var elementName = s.get('name');
    if(s.parentNode.parentNode.id!=""){
        index = s.parentNode.parentNode.id;
    }
    window.open('/am_bdp/'+popupUnitName+'.do?parentid='+elementName+'&index='+index+'' +
        '&clear=am_bdp.'+popupUnitName+'.query ',
        '',
        'height=600,width=900,top=10,left=100,scrollbars =yes,toolbar=no,menubar=no');
}


/**
 * 确认选择一条 弹出数据
 * @param unitName:当前单元名称，
 */

function confirmData(unitName){
    //当前弹出层list页面所有数据
    var selectArray=$$('input[name=_s_'+unitName+']');
    //当前弹出层list页面所有数据的id
    var idArray=document.getElementsByName(""+unitName+".id");
    //获取隐藏域中的存入session中的值
    var parentid=document.getElementsByName(""+unitName+".parentid")[0].value;
    //索引
    var index=document.getElementsByName(""+unitName+".index")[0].value;
    var finished = false;
    for(var i=0;i<selectArray.length;i++){
        if(selectArray[i].value=='1'){
            window.top.opener.document.getElementsByName(""+parentid+"")[index].value=idArray[i].value;
            finished = true;
            break;
        }
    }
    if(finished){
        window.top.close();
    }
}

/**
 * 处理地址传值
 * @param paras
 * @returns {*}
 */
function request(paras)
{
    var url = location.href;
    var paraString = url.substring(url.indexOf("?")+1,url.length).split("&");
    var paraObj = {}
    for (i=0; j=paraString[i]; i++){
        paraObj[j.substring(0,j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=")+1,j.length);
    }
    var returnValue = paraObj[paras.toLowerCase()];
    if(typeof(returnValue)=="undefined"){
        return "";
    }else{
        return returnValue;
    }
}

/**
 * 选择发送范围 1：全部；2：使用会员标签  3：使用会员帐号
 */
function messageFrom(s){

	if(s.value==2){
        document.getElementById("memberLabelID").parentNode.setAttribute("style","display: normal;");
        // document.getElementsByName("am_notice_form.member_label")[0].parentNode.style="display:normal;";
        document.getElementById("memberLabelName").parentNode.setAttribute("style","display: none;");
    }if(s.value==3){
        
        document.getElementById("memberLabelID").parentNode.setAttribute("style","display: none;");
        // document.getElementsByName("am_notice_form.member_label")[0].parentNode.style="display:none;";
		document.getElementById("memberLabelName").parentNode.setAttribute("style","display: normal;");
    }else if(s.value==1||!s.value){
        document.getElementById("memberLabelID").parentNode.setAttribute("style","display: none;");
        // document.getElementsByName("am_notice_form.member_label")[0].parentNode.style="display:none;";
        document.getElementById("memberLabelName").parentNode.setAttribute("style","display: none;");
    }
}

function hiddenmessageFrom(){
    if(location.search.split("?")[1].split("&")[0]=="m=e"){
        var selectValue={value:$$("select[name=am_notice_form.send_range]")[0].value};
        messageFrom(selectValue);
    }
}