package com.baidu.yun.core.log;

public class NullYunLogHandler implements YunLogHandler {

    @Override
	public void onHandle(YunLogEvent event) {
        // to nothing
    }

}
