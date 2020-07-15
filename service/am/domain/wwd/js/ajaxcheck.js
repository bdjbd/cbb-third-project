function doActionWithAjaxValidation(ajaxUrl,actionUrl){
doSyncAjaxSubmit(ajaxUrl,'msg');
if(!$('msg').get('html')){
       doSubmit(actionUrl);
}
}