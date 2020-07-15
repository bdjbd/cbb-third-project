/*用户选择*/

function openDialog()
{
	var public_id = document.getElementsByName("ws_fsendmess.form.public_id")[0].value;
	var iTop = (window.screen.availHeight-30-450)/2; //获得窗口的垂直位置;
	var iLeft = (window.screen.availWidth-10-900)/2; //获得窗口的水平位置;

	window.open('/wwd/select.user.do?clear=wwd.select.user&m=s&public_id='+public_id+'',null,'top='+iTop+',left='+iLeft+',height=500,width=900,titlebar=no,menubar=no,titlebar=no,modal=yes,toolbar=no,location=no,scrollbars=yes,resizable=no');
}

/**
*功能:将项目信息的值写回父页面
**/

function setSelect()
{
	
	var selectArray = $$('input[name=_s_select.user.list]');
	//会员名称
	var nicknameArray = $$('input[name=select.user.list.nickname]');
    //会员编号
	var membercodeArray = $$('input[name=select.user.list.member_code]');
	
	
	var nickname = "";
	var member_code = ""; 
	var selectLength=0;

	for(var i=0;i<selectArray.length;i++)
		{

		if(selectArray[i].value=='1')
			{
				nickname += nicknameArray[i].value +",";
				member_code += membercodeArray[i].value +",";
			}	
		}

    nickname = nickname.substring(0,nickname.length-1); 
	member_code = member_code.substring(0,member_code.length-1); 
	window.opener.document.getElementsByName("ws_fsendmess.form.to_username")[0].value=member_code;
	window.opener.document.getElementsByName("ws_fsendmess.form.to_usercode")[0].value=member_code;
	closeDialog();
}

/**
*功能:关闭子窗体
**/
function closeDialog()
{   
	window.close(); 
}

/**
*查询
*/
function closeDialog()
{   
	window.close(); 
}

//对选中的会员再次打开时依旧为选中状态
function initUser(){
	//父业面的用户编号
	var userId = window.opener.document.getElementsByName('ws_fsendmess.form.to_usercode')[0].value;
	var idArray=userId.split(',');

	if(userId){
		//初始化选中状态
		var selectBoxs=document.getElements('input[name=_s_select.user.list]');
		//弹出页面列表的用户编号
		var userIdArray=document.getElements('input[name=select.user.list.member_code]');
		if(selectBoxs && selectBoxs.length>0){
			for(var i=0;i<selectBoxs.length;i++){
				for(var j=0;j<idArray.length;j++){
					if(idArray[j]==userIdArray[i].value){
						selectBoxs[i].value = '1';
						selectBoxs[i].parentNode.childNodes[1].checked=true;
					}
				}
				
			}
		}
	}
}