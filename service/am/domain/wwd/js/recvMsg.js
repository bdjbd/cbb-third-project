function deleteRecvMsg(msgId){
	doSyncAjaxSubmit("/wwd/ws_recv_msg.list/delete.do?msgid="+msgId);
	location.reload();
}