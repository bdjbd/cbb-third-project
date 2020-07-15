/**
 *  获取页面Kindeditor的内容并保存
 * @param {String} elementNames  fastUnit使用Kindeditor元素的名称，多个名称使用逗号(,)分开。
 * @param {String} dbFileds  fastUnit单元与表对应的字段(unitName.elementName)，多个字段用逗号(,)分开，顺序与元素名称对应。
 * @param {String}  saveUrl  保存按钮。 /域名/单元名/元素名.do。提交是doSubmit()参数。
 * @param {String}  提交模式 doSumbit he doConfirm
 */
function getEditorTextSubmit(elementNames, dbFileds, saveUrl,submitType) {
	var arNames = elementNames.split(",");
	var arDbField = dbFileds.split(",")
	editor.sync();
	if (arNames.length == arDbField.length) {
		for (var i = 0; i < arNames.length; i++) {
			//alert(arNames[i]);
			//alert(arDbField[i]);
			var html = document.getElementsByName(arNames[i]);
			//alert(arNames+"values:"+html[i].value);
			///alert(arDbField[i]);
			var dbfiled = document.getElementsByName(arDbField[i]);
			//alert("dbfileds[i]:  "+dbfiled[i]);
			// var valus=html[i].value.replace(/"/,"@");
			// var values=encodeURI(html[i].value);			var values=html[0].value;			dbfiled[0].value=values;
			//alert(arNames[i]+"最后结果:"+dbfiled[0].value);
		}
	}
	var typeStr=new String(submitType);
	if(typeStr.toUpperCase()=="DOSUBMIT"){
		doSubmit(saveUrl);
	}else{
		doConfirm(saveUrl);
	}
}
/**
 * 
 * @param {Object} elementNames  FastUnit使用Kindeditor元素的名称，多个名称使用逗号(,)分开。
 * @param {Object} dbFileds   fastUnit单元与表对应的字段(unitName.elementName)，多个字段用逗号(,)分开，顺序与元素名称对应。
 * @param {Object} saveUrl    保存按钮。 /域名/单元名/元素名.do。提交是doSubmit()参数。
 * @param {Object} nextTaskId  表达提交任务号
 */
function getEditorTextSelectUser(elementNames, dbFileds, saveUrl,nextTaskId){
	var arNames = elementNames.split(",");
	var arDbField = dbFileds.split(",")
	editor.sync();
	if (arNames.length == arDbField.length) {
		for (var i = 0; i < arNames.length; i++) {
			var html = document.getElementsByName(arNames[i]);
			//alert(arNames+"values:"+html[i].value);
			///alert(arDbField[i]);
			var dbfiled = document.getElementsByName(arDbField[i]);
			//alert("dbfileds[i]:  "+dbfiled[i]);
			// var valus=html[i].value.replace(/"/,"@");
			// var values=encodeURI(html[0].value);			var values=html[0].value;
			//alert(values)
			dbfiled[0].value = values;
			//alert("dbfiled value last:"+dbfiled[i].value);
		}
	}
	autoSelectFlowUser(saveUrl,nextTaskId)
}

function getEditorText1(eleNamesStr, eleFieldsStr, saveUrl) {
	var eleNameArray = eleNamesStr.split(",");
	var eleFieldArray = eleFieldsStr.split(",");
	editor.sync();

	if (eleNameArray.length != eleFieldArray.length) {
		alert("这儿不相等啊！");
	}
	var valueDiv=document.getElementsByName(eleNameArray[0]+"Div");
	alert("Hidden div name:"+valueDiv.name);
	valueDiv.name=eleFieldArray[0];
	var html= document.getElementsByName(eleNameArray[0]);
	valueDiv.innerHTML=html[0].value;
	alert("Change Hidden div name:"+valueDiv.name);
	alert("Change Hidden div innerHTML:"+valueDiv.innerHTML);
	
	
	// doSubmit(saveUrl);
}
