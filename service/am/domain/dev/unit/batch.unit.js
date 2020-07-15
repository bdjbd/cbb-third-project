function doBacth(){
	if (confirm('将执行单元批量保存，你确认吗？')){
		doSubmit('/dev/unit.batch/save.do');
	}
}

