package cn.hylexus.jt808.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpAsyncMap {
	 private static Logger log = LoggerFactory.getLogger(HttpAsyncMap.class);

	  private static Map<String, CloseableHttpAsyncClient> map = new ConcurrentHashMap();

	  public static void addGatewayChannel(String id) {
	    CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault();
	    asyncClient.start();
	    map.put(id, asyncClient);
	    log.info(">>>>>[HttpPost新增终端线程] = " + id);
	    log.info("[当前异步HttpPost线程数]<<<<< = " + map.size());
	  }

	  public static CloseableHttpAsyncClient getGatewayChannel(String id) {
	    return ((CloseableHttpAsyncClient)map.get(id));
	  }

	  public static void removeGatewayChannel(String id)
	  {
	    CloseableHttpAsyncClient asyncClient = getGatewayChannel(id);
	    try {
	      asyncClient.close();
	      map.remove(id);
	      log.info(">>>>>[HttpPost移除终端线程] = " + id);
	      log.info("[当前异步HttpPost线程数]<<<<< = " + map.size());
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    map.remove(id);
	  }
}
