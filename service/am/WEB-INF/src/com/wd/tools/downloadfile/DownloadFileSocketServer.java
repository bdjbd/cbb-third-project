package com.wd.tools.downloadfile;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *   说明:
 * 		离线文件Socket下载类
 *   @creator	岳斌
 *   @create 	2013-3-6 
 *   @revision	$Id
 */
public class DownloadFileSocketServer {
	
	/**每次读取文件的大小 1024*500  **/
	public static final int BUFFER_SIZE=1024*500;
	/**端口号 10010**/
	public static final int port=10010;
	
	public DownloadFileSocketServer(){
		init();
	}
	
	/**初始化配置**/
	private void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ServerSocket ss;
				try {
					ss = new ServerSocket(port);
					ExecutorService threadPool=Executors.newCachedThreadPool();
					while(true){
						Socket cliencScoket=ss.accept();
						threadPool.submit(new DownloadFileThread(cliencScoket));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
	}
}
