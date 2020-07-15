package com.am.frame.notice.action;

import com.am.frame.notice.service.SendNoticeManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class SendNoticeAction extends DefaultAction
{
  @Override
public void doAction(DB db, ActionContext ac)
    throws Exception
  {
    Table table = ac.getTable("am_notice");
    db.save(table);
    String id = table.getRows().get(0).getValue("id");

    String contentId = table.getRows().get(0).getValue("jump_id");

    String url = table.getRows().get(0).getValue("notice_type");

    String sql = "select * from am_notice where id='" + id + "'";
    System.err.println("执行查询sql："+sql);
    MapList mapList = db.query(sql);

    if (!Checker.isEmpty(mapList))
    {
      String sqls = "select * from am_advert_type  where id  = '" + url + "'";

      MapList lst = db.query(sqls);

      String routeUrl = "";

      if (!Checker.isEmpty(lst))
      {
        routeUrl = lst.getRow(0).get("route_url");
      }

      String targetUrl = routeUrl.replaceAll("\\[ID\\]", contentId);

      String updateAdvertSql = "UPDATE am_notice  SET url ='" + targetUrl + "',count=(COALESCE(count,0)+1 ),senddate='now()',send_status = '2'  WHERE id='" + id + "' ";

      db.execute(updateAdvertSql);

      ac.setSessionAttribute("am_bdp.am_notice_form.id", id);
    }

    MapList noticemapList = db.query(sql);

    String member_label = noticemapList.getRow(0).get("member_label");

    SendNoticeManager sendNotice = new SendNoticeManager();

    if ("2".equals(noticemapList.getRow(0).get("send_range")))
    {
    	//循环标签查询会员信息
		if(!Checker.isEmpty(member_label))
		{
			System.err.println("用户选择的全部标签："+member_label);
			boolean flag = false;
			//有逗号分隔，说明选择了多个标签
			if(member_label.indexOf(",")>0)
			{
				
				String [] arr = member_label.split(",");
				
				if(arr.length>0)
				{
						
					for (int i = 0; i < arr.length; i++) 
					{
						//第i个标签的值为 arr[i]
						System.err.println("第"+i+"个标签为："+arr[i]);
						sendNotice.sendByUserMenber (db,noticemapList,arr[i]);
						
					}
						
				}
			}else
			{
				sendNotice.sendByUserMenber (db,noticemapList,member_label);
			}
		}

    }
    else if ("3".equals(noticemapList.getRow(0).get("send_range")))
    {
    	//使用会员账户
		sendNotice.sendOne (db,noticemapList,noticemapList.getRow(0).get("member_id"));
    }
    else if ("1".equals(noticemapList.getRow(0).get("send_range")))
    {
    	//发送全部用户
		sendNotice.sendAllUserMenber(db,noticemapList);
    }
  }
}