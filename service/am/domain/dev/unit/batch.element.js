function doBacth(){
	if (confirm('将执行元素批量保存，你确认吗？')){
		doSubmit('/dev/element.batch/save.do');
	}
}

