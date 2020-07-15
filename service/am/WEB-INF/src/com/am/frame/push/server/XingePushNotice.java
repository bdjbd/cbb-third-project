package com.am.frame.push.server;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;
import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.Style;
import com.tencent.xinge.XingeApp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class XingePushNotice
{
  private long accessId = 2200213920L;

  private String secretKey = "f7ab454a906a0ab423de766ea5cdfabd";

  public XingePushNotice(Long accessId, String secretKey)
  {
    this.accessId = accessId.longValue();
    this.secretKey = secretKey;
  }

  public Message setAndroidMessage(JSONObject jso)
    throws Exception
  {
    Message message = new Message();
    message.setType(1);

    message.setStyle(new Style(0, 1, 1, 0, 0));

    if (jso != null)
    {
      if (jso.has("title"))
      {
        message.setTitle(jso.getString("title"));
      }
      if (jso.has("content"))
      {
        message.setContent(jso.getString("content"));
      }
      if (jso.has("url"))
      {
        Map map = new HashMap();
        map.put("url", jso.getString("url"));

        message.setCustom(map);
      }

    }

    ClickAction action = new ClickAction();

    message.setAction(action);

    return message;
  }

  public MessageIOS setIosMessage(JSONObject jso)
    throws Exception
  {
    MessageIOS message = new MessageIOS();

    if (jso.has("content"))
    {
      message.setAlert(jso.getString("content"));
    }
    if (jso.has("url"))
    {
      Map map = new HashMap();
      map.put("url", Boolean.valueOf(jso.has("url")));

      message.setCustom(map);
    }

    message.setBadge(1);

    message.setSound("beep.wav");

    return message;
  }

  public JSONObject sendSingleDevice(DB db, JSONObject contentJso, MapList mapList)
    throws Exception
  {
    JSONObject resultJson = new JSONObject();

    JSONObject androidJson = null;

    JSONObject iosJson = null;

    XingeApp push = new XingeApp(this.accessId, this.secretKey);
    try
    {
      if (!Checker.isEmpty(mapList))
      {
        Message androidMesg = setAndroidMessage(contentJso);

        MessageIOS iosMesg = setIosMessage(contentJso);

        for (int i = 0; i < mapList.size(); i++)
        {
          if ("1".equals(mapList.getRow(i).get("mobile_type")))
          {
            androidJson = push.pushSingleDevice(mapList.getRow(i).get("xtoken"), androidMesg);
          }
          else if ("2".equals(mapList.getRow(i).get("mobile_type")))
          {
            iosJson = push.pushSingleDevice(mapList.getRow(i).get("xtoken"), iosMesg, 1);
          }

        }

        if ((androidJson.length() > 0) || (iosJson.length() > 0))
        {
          if (("0".equals(androidJson.getString("ret_code"))) || ("0".equals(iosJson.getString("ret_code"))))
          {
            resultJson.put("code", "0");
            resultJson.put("msg", "success");
          }
          else
          {
            resultJson.put("code", "999");
            resultJson.put("msg", androidJson.get("err_msg"));
          }

        }

      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return resultJson;
  }

  public JSONObject sendALlListDevice(JSONObject contentJso, List iosList, List androidList)
  {
    JSONObject resultJson = new JSONObject();

    JSONObject androidJson = null;

    JSONObject iosJson = null;

    int androidPushId = 0;

    int iosPushId = 0;

    JSONObject androidPushJson = null;

    JSONObject iosPushJson = null;

    XingeApp push = new XingeApp(this.accessId, this.secretKey);
    try
    {
      if (androidList.size() > 0)
      {
        Message androidMesg = setAndroidMessage(contentJso);

        androidPushJson = push.createMultipush(androidMesg);

        if (androidPushJson.length() > 0)
        {
          if ("0".equals(androidPushJson.getString("ret_code")))
          {
            androidPushId = androidPushJson.getJSONObject("result").getInt("push_id");
            androidJson = push.pushDeviceListMultiple(androidPushId, androidList);
          }

        }

      }

      if (iosList.size() > 0)
      {
        MessageIOS iosMesg = setIosMessage(contentJso);

        iosPushJson = push.createMultipush(iosMesg, 0);

        if (iosPushJson.length() > 0)
        {
          if ("0".equals(iosPushJson.getString("ret_code")))
          {
            iosPushId = iosPushJson.getJSONObject("result").getInt("push_id");
            iosJson = push.pushDeviceListMultiple(iosPushId, iosList);
          }

        }

      }

      if ((androidJson.length() > 0) || (iosJson.length() > 0))
      {
        if (("0".equals(androidJson.getString("ret_code"))) || ("0".equals(iosJson.getString("ret_code"))))
        {
          resultJson.put("code", "0");
          resultJson.put("msg", "success");
        }
        else
        {
          resultJson.put("code", "999");
          resultJson.put("msg", androidJson.get("err_msg"));
        }

      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return resultJson;
  }

  public JSONObject sendALlDevice(JSONObject contentJso)
  {
    JSONObject resultJson = new JSONObject();

    JSONObject androidJson = null;

    JSONObject iosJson = null;

    XingeApp push = new XingeApp(this.accessId, this.secretKey);
    try
    {
      Message androidMesg = setAndroidMessage(contentJso);

      androidJson = push.pushAllDevice(0, androidMesg);

      MessageIOS iosMesg = setIosMessage(contentJso);

      iosJson = push.pushAllDevice(0, iosMesg, 1);

      if ((androidJson.length() > 0) || (iosJson.length() > 0))
      {
        if (("0".equals(androidJson.getString("ret_code"))) || ("0".equals(iosJson.getString("ret_code"))))
        {
          resultJson.put("code", "0");
          resultJson.put("msg", "success");
        }
        else
        {
          resultJson.put("code", "999");
          resultJson.put("msg", androidJson.get("err_msg"));
        }

      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return resultJson;
  }
}