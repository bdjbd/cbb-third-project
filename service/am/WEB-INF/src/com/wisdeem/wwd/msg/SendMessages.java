package com.wisdeem.wwd.msg;

/**
 * 2013年12月10日 15:46:24
 * 群发消息Action
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;
import com.wisdeem.wwd.WeChat.exception.WeChatInfaceException;
import com.wisdeem.wwd.WeChat.server.Oauth2AuthorServer;

public class SendMessages extends DefaultAction {
	private String flag = "";

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String hostUrl = Oauth2AuthorServer.hostUrl;
		// 公众帐号
		String public_id = ac
				.getRequestParameter("ws_fsendmess.form.public_id");
		String fsend_id = UUID.randomUUID().toString();
		// 会员编号 即：消息接收者
		String members = ac
				.getRequestParameter("ws_fsendmess.form.to_usercode");
		// 消息类型
		String msgType = ac.getRequestParameter("ws_fsendmess.form.msg_type");
		// 消息内容
		String content = ac.getRequestParameter("ws_fsendmess.form.content");
		// 标题
		String title = ac.getRequestParameter("ws_fsendmess.form.title");
		// 封面
		String imgcover = ac
				.getRequestParameter("ws_fsendmess.form.imgcoverfilename");
		// 正文
		String description = ac
				.getRequestParameter("ws_fsendmess.form.description");
		// 原文链接
		String url = ac.getRequestParameter("ws_fsendmess.form.url").trim();
		// 当为公有公众帐号时，获取场景id
		String getSceneIdSQL = "select scene_id from WS_ACTDETAL where public_id="
				+ public_id;
		MapList sceneIdMap = db.query(getSceneIdSQL);
		String scene_id = "";
		if (sceneIdMap.size() > 0) {
			scene_id = sceneIdMap.getRow(0).get("scene_id").trim();
		}
		Map<String, String> msgMap = new HashMap<String, String>();
		content = ("A" + content).trim().substring(1);
		title = ("A" + title).trim().substring(1);
		description = ("A" + description).trim().substring(1);
		//文本内容替换
		//[sc]=商品编号
		//商品url  http://hostip/domain/wwd/weshop/deatil.jsp?pid=show01&cid=215&orgidp=org
		//[gw]=企业信息编号 
		//http://127.0.0.1:8080/domain/wwd/weshop/news.jsp?orgid=org&cid=4#newdetailsPage?nid=13
		
		if(hostUrl==null){
			Oauth2AuthorServer.getInstance();// 初始化Oauth2AuthorServer.hostUrl的值。
			hostUrl = Oauth2AuthorServer.hostUrl;
		}
		//获取机构，public_id，token
		String sql="SELECT pa.orgid,public_id,token FROM ws_public_accounts AS pa "
				+ " LEFT JOIN aorg AS org ON pa.orgid=org.orgid "
				+ " WHERE pa.public_id="+public_id;
		MapList maps=db.query(sql);
		if(Checker.isEmpty(maps)){
			return ;
		}
		//替换[sc]=
		content=replaceSCStr(db, hostUrl, content, "", maps.getRow(0).get("token"), maps.getRow(0).get("orgid"));
		//替换[gw]=
		content=replaceGWStr(db, hostUrl, content, "", maps.getRow(0).get("token"), maps.getRow(0).get("orgid"));
		
		//url替换代码
		url=urlReplace(db, hostUrl, url, "", maps.getRow(0).get("token"), maps.getRow(0).get("orgid"));
		
		/**
		 * 图文消息不支持<a>标签
		description=replaceSCStr(db, hostUrl, content, "", maps.getRow(0).get("token"), maps.getRow(0).get("orgid"));
		description=replaceGWStr(db, hostUrl, content, "", maps.getRow(0).get("token"), maps.getRow(0).get("orgid"));
		**/
		msgMap.put("public_id", public_id);
		msgMap.put("msgType", msgType);
		msgMap.put("content", content);
		msgMap.put("title", title);
		msgMap.put("imgcover", imgcover);
		msgMap.put("description", description);
		msgMap.put("url", url);
		msgMap.put("fsend_id", fsend_id);
		msgMap.put("scene_id", scene_id);
		msgMap.put("orgid", ac.getVisitor().getUser().getOrgId());

		// 消息接收者的openid
		String[] memCode = members.split(",");// 选择群发对象的数量
		// 根据会员编号找到对应会员的openid
		ArrayList<String> openidStr = new ArrayList<String>();
		for (int i = 0; i < memCode.length; i++) {
			String selOpenidSql = "select openid from ws_member where member_code="
					+ memCode[i];
			MapList list = db.query(selOpenidSql);
			String openid = list.getRow(0).get("openid");
			openidStr.add(openid);
		}
		int unUsedLen = Utils.MessStatistics(public_id);
		String[] memcount = members.split(",");// 选择群发对象的数量
		if (memcount.length > unUsedLen) {
			if (unUsedLen == 0) {
				ac.getActionResult().addErrorMessage(
						"今日剩余" + unUsedLen + "条消息可发送！");
				ac.getActionResult().setSuccessful(false);
				return;
			} else {
				ac.getActionResult().addErrorMessage(
						"今日剩余" + unUsedLen + "条消息可发送,请重新选择群发对象！");
				ac.getActionResult().setSuccessful(false);
				return;
			}
		}
		try {
			if ("1".equals(msgType)) {
				if ("".equals(content.trim())) {
					ac.getActionResult().addErrorMessage("内容：不能为空");
					ac.getActionResult().setSuccessful(false);
					return;
				} else {
					// 发送文字消息
					flag = Utils.sendMessage(msgMap, openidStr, "");
				}
			} else if ("6".equals(msgType)) {
				if (imgcover == null) {
					ac.getActionResult().addErrorMessage("封面：不能为空");
					ac.getActionResult().setSuccessful(false);
					return;
				}

				// http://www.wisdeem.cn http://yuebin616.oicp.net
				
				if (hostUrl == null || "".equals(hostUrl.trim())) {
					Oauth2AuthorServer.getInstance();// 初始化Oauth2AuthorServer.hostUrl的值。
					hostUrl = Oauth2AuthorServer.hostUrl;
				}
				String imgurl = hostUrl + File.separator + "files"
						+ File.separator + "WS_FSENDMESS" + File.separator
						+ fsend_id + File.separator + "imgcover"
						+ File.separator + imgcover;
				/**
				// news2
				msgMap.put("news2", ac.getRequestParameter("ws_fsendmess.form.news2"));
				msgMap.put("fimage2",
						builderImgUrl(hostUrl, fsend_id, "fimage2", ac.getRequestParameter("ws_fsendmess.form.fimage2")));
				msgMap.put("news2url",
						ac.getRequestParameter("ws_fsendmess.form.news2url"));
				// news3
				msgMap.put("news3", ac.getRequestParameter("ws_fsendmess.form.news3"));
				msgMap.put("fimage3",
						builderImgUrl(hostUrl, fsend_id, "fimage3", ac.getRequestParameter("ws_fsendmess.form.fimage3")));
				msgMap.put("news3url",
						ac.getRequestParameter("ws_fsendmess.form.news3url"));

				// news4
				msgMap.put("news4", ac.getRequestParameter("ws_fsendmess.form.news4"));
				msgMap.put("fimage4",
						builderImgUrl(hostUrl, fsend_id, "fimage4", ac.getRequestParameter("ws_fsendmess.form.fimage4")));
				msgMap.put("news4url",
						ac.getRequestParameter("ws_fsendmess.form.news4url"));

				// news5
				msgMap.put("news5", ac.getRequestParameter("ws_fsendmess.form.news5"));
				msgMap.put("fimage5",
						builderImgUrl(hostUrl, fsend_id, "fimage5", ac.getRequestParameter("ws_fsendmess.form.fimage5")));
				msgMap.put("news5url",
						ac.getRequestParameter("ws_fsendmess.form.news5url"));
				// news6
				msgMap.put("news6", ac.getRequestParameter("ws_fsendmess.form.news6"));
				msgMap.put("fimage6",
						builderImgUrl(hostUrl, fsend_id, "fimage6", ac.getRequestParameter("ws_fsendmess.form.fimage6")));
				msgMap.put("news6url",
						ac.getRequestParameter("ws_fsendmess.form.news6url"));

				// news7
				msgMap.put("news7", ac.getRequestParameter("ws_fsendmess.form.news7"));
				msgMap.put("fimage7",
						builderImgUrl(hostUrl, fsend_id, "fimage7", ac.getRequestParameter("ws_fsendmess.form.fimage7")));
				msgMap.put("news7url",
						ac.getRequestParameter("ws_fsendmess.form.news7url"));

				// news8
				msgMap.put("news8", ac.getRequestParameter("ws_fsendmess.form.news8"));
				msgMap.put("fimage8",
						builderImgUrl(hostUrl, fsend_id, "fimage8", ac.getRequestParameter("ws_fsendmess.form.fimage8")));
				msgMap.put("news8url",
						ac.getRequestParameter("ws_fsendmess.form.news8url"));

				// news9
				msgMap.put("news9", ac.getRequestParameter("ws_fsendmess.form.news9"));
				msgMap.put("fimage9",
						builderImgUrl(hostUrl, fsend_id, "fimage9", ac.getRequestParameter("ws_fsendmess.form.fimage9")));
				msgMap.put("news9url",
						ac.getRequestParameter("ws_fsendmess.form.news9url"));

				// news10
				msgMap.put("news10", ac.getRequestParameter("ws_fsendmess.form.news10"));
				msgMap.put("fimage10",
						builderImgUrl(hostUrl, fsend_id, "fimage10", ac.getRequestParameter("ws_fsendmess.form.fimage10")));
				msgMap.put("news10url",
						ac.getRequestParameter("ws_fsendmess.form.news10url"));
				**/
				
				// 发送图文消息
				flag = Utils.sendMessage(msgMap, openidStr, imgurl);
			}
		} catch (WeChatInfaceException e) {
			flag = "2";
			e.printStackTrace();
		}
		Table table = ac.getTable("WS_FSENDMESS");
		TableRow tableTr = table.getRows().get(0);
		// 发送成功插入数据库
		if (flag.equals("1")) {
			tableTr.setValue("msg_status", 1);
			ac.getActionResult().addSuccessMessage("发送成功");
		}
		if (flag.equals("2") || flag == "" || flag == null) {
			tableTr.setValue("msg_status", 2);
			ac.getActionResult().addErrorMessage("发送失败");
		}
		tableTr.setValue("fsend_id", fsend_id);
		tableTr.setValue("content", content);
		tableTr.setValue("title", title);
		tableTr.setValue("description", description);
		tableTr.setValue("url", url);
		tableTr.setValue("public_id", public_id);
		db.save(table);
		Table ctable = new Table("wwd", "WS_RECIVELIST");
		TableRow ctableTr = ctable.addInsertRow();
		for (int i = 0; i < openidStr.size(); i++) {
			ctableTr.setValue("recive_id", UUID.randomUUID().toString());
			ctableTr.setValue("fsend_id", fsend_id);
			ctableTr.setValue("recive_code", memCode[i]);
			ctableTr.setValue("openid", openidStr.get(i));
			db.save(ctable);
		}
	}
	
	
	/**
	 * 替换URL中的 [sc]=和[gw]=
	 * @param db
	 * @param hostUrl
	 * @param content
	 * @param string
	 * @param string2
	 * @param string3
	 * @return
	 */
	private String urlReplace(DB db,String hostUrl,String href,String replaStr,String token,String orgid)throws JDBCException {
		if(href.contains("[sc]=")){
			String src=href.substring(href.indexOf("[sc]=")).split(" ")[0];
			String shopCode=src.substring(5);
			//根据商品编号查询商品名称
			String shopSQL="SELECT COMDITY_ID,name,comdity_code  FROM ws_commodity_name WHERE comdity_code='"+shopCode+"' ";
			MapList shopMap=db.query(shopSQL);
			if(Checker.isEmpty(shopMap)){
				return href;
			}
			
			System.out.println("ShopCode:"+shopCode);
			
			 href=hostUrl+"/domain/wwd/weshop/deatil.jsp?pid="+token+"&cid="+shopMap.getRow(0).get("comdity_id")+"&orgidp="+orgid;
		}
		if(href.contains("[gw]=")){
			String gw=href.substring(href.indexOf("[gw]=")).split(" ")[0];
			String gwcode=gw.substring(5);
			//根据信息编号查询信息名称
			String newsSQL="SELECT title,nid,cid  FROM newsdetail  WHERE newscode='"+gwcode+"' AND datastatus=2 ";
			MapList newsMap=db.query(newsSQL);
			if(Checker.isEmpty(newsMap)){
				return href;
			}
			
			System.out.println("ShopCode:"+gwcode);
			//http://127.0.0.1:8080/domain/wwd/weshop/news.jsp?orgid=org&cid=4#newdetailsPage?nid=13
			href=hostUrl+"/domain/wwd/weshop/news.jsp?orgid="+orgid+"&cid="+newsMap.getRow(0).get("cid")
					+"#newdetailsPage?nid="+newsMap.getRow(0).get("nid");
		}
		
		return href;
	}

	private String builderImgUrl(String hostUrl,String fsend_id,String filed,String imgcover){
		return hostUrl + File.separator + "files"
				+ File.separator + "WS_FSENDMESS" + File.separator
				+ fsend_id + File.separator + filed
				+ File.separator + imgcover;
	}
	
	/**
	 * 替换[sc]=
	 * @param db
	 * @param hostUrl
	 * @param content
	 * @param replaStr
	 * @param token
	 * @param orgid
	 * @return
	 * @throws JDBCException
	 */
	private String replaceSCStr(DB db,String hostUrl,String content,String replaStr,String token,String orgid) throws JDBCException{

		if(content.contains("[sc]=")){
			String src=content.substring(content.indexOf("[sc]=")).split(" ")[0];
			String shopCode=src.substring(5);
			//根据商品编号查询商品名称
			String shopSQL="SELECT COMDITY_ID,name,comdity_code  FROM ws_commodity_name WHERE comdity_code='"+shopCode+"' ";
			MapList shopMap=db.query(shopSQL);
			if(Checker.isEmpty(shopMap)){
				return content;
			}
			
			System.out.println("ShopCode:"+shopCode);
			
			String href=hostUrl+"/domain/wwd/weshop/deatil.jsp?pid="+token+"&cid="+shopMap.getRow(0).get("comdity_id")+"&orgidp="+orgid;
			href="<a href='"+href+"'>"+shopMap.getRow(0).get("name")+"</a>";
			content=content.replace("[sc]=","");
			content=content.replace(shopCode,href);
			content=replaceSCStr(db, hostUrl, content, replaStr, token, orgid);
		}
		
		return content;
	}
	
	private String replaceGWStr(DB db,String hostUrl,String content,String replaStr,String token,String orgid) throws JDBCException{
		if(content.contains("[gw]=")){
			String gw=content.substring(content.indexOf("[gw]=")).split(" ")[0];
			String gwcode=gw.substring(5);
			//根据信息编号查询信息名称
			String newsSQL="SELECT title,nid,cid  FROM newsdetail  WHERE newscode='"+gwcode+"' AND datastatus=2 ";
			MapList newsMap=db.query(newsSQL);
			if(Checker.isEmpty(newsMap)){
				return content;
			}
			
			System.out.println("ShopCode:"+gwcode);
			//http://127.0.0.1:8080/domain/wwd/weshop/news.jsp?orgid=org&cid=4#newdetailsPage?nid=13
			String href=hostUrl+"/domain/wwd/weshop/news.jsp?orgid="+orgid+"&cid="+newsMap.getRow(0).get("cid")
					+"#newdetailsPage?nid="+newsMap.getRow(0).get("nid");
			href="<a href='"+href+"'>"+newsMap.getRow(0).get("title")+"</a>";
			content=content.replace("[gw]=","");
			content=content.replace(gwcode,href);
			content=replaceGWStr(db, hostUrl, content, replaStr, token, orgid);
		}
		
		return content;
	}
}
