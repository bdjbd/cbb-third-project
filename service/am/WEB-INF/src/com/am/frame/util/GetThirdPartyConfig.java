package com.am.frame.util;

/**
 * 获取第三方接口的配置文件属性值
 * @author 鲜琳
 *2017-05-16
 */
public class GetThirdPartyConfig {
	// 定义一个私有构造方法
    private GetThirdPartyConfig() { 
     
    }   
    //定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
    private static volatile GetThirdPartyConfig instance;  

    //定义一个共有的静态方法，返回该类型实例
    public static GetThirdPartyConfig getIstance() { 
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (instance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (GetThirdPartyConfig.class) {
                //未初始化，则初始instance变量
                if (instance == null) {
                    instance = new GetThirdPartyConfig();   
                }   
            }   
        }   
        return instance;   
    }
	
    
    /**
     * 获取合作身份者id
     * @return
     */
    public String getAlipayPartner()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("partner");
    }
    
    /**
     * 获取商户私钥
     * @return
     */
    public String getAlipayPrivateKey()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("private_key");
    }
    
    /**
     * 获取商户公钥
     * @return
     */
    public String getAlipayPublicKey()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("alipay_public_key");
    }
    
    /**
     * 获取异步通知页面路径
     * @return
     */
    public String getAlipayNotifyUrl()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("notify_url");
    }
    
    /**
     * 获取同步通知页面路径
     * @return
     */
    public String getAlipayReturnUrl()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("return_url");
    }
    
    /**
     * 获取手机网站异步通知页面路径
     * @return
     */
    public String getAlipayWapNotifyUrl()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("wapNotify_url");
    }
    
    /**
     * 获取手机网站同步通知页面路径
     * @return
     */
    public String getAlipayWapReturnUrl()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("wapReturn_url");
    }
    
    
    /**
     * 获取签名方式
     * @return
     */
    public String getAlipaySignType()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("sign_type");
    }
    
    /**
     * 获取日志存放路径
     * @return
     */
    public String getAlipayLogPath()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("log_path");
    }
    
    /**
     * 获取字符编码格式
     * @return
     */
    public String getAlipayInputCharset()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("input_charset");
    }
    
    /**
     * 获取支付类型
     * @return
     */
    public String getAlipayPaymentType()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("payment_type");
    }
    
    /**
     * 获取调用接口服务名
     * @return
     */
    public String getAlipayService()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("service");
    }
    
    /**
     * 获取WEB端支付回调地址
     * @return
     */
    public String getAlipayWebCallBack()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("webNotifyUrl");
    }
    
    /**
     * 获取公众平台支付订单支付页面URL
     * @return
     */
    public String getWechatMenuPayMentUrl()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("redirectMenuPaymentUrl");
    }
    
    /**
     * 获取公众平台微信菜单进入url
     * @return
     */
    public String getWechatMenuRedirectUrl()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("redirectMenuUrl");
    }
    
    /**
     * 获取公众号商户支付mch_id
     * @return
     */
    public String getWechatMenuMchId()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("mch_id");
    }
    
    /**
     * 获取公众账号支付退款APiClientCert路径
     * @return
     */
    public String getWechatMenuRefundCertUrl()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("MPWeiChartApiClientCertPath");
    }
    
    /**
     * 获取公众账号JSSDK debug模式
     * @return
     */
    public String getWechatMenuJSsdkDebug()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("MpWeiChartJSDebug");
    }
    
    /**
     * 获取公众账号支付PayApiKey
     * @return
     */
    public String getWechatMenuAPIKey()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("MpWeiPayAPIKey");
    }
    
    /**
     * 获取微信AppId
     * @return
     */
    public String getWechatAppId()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("weiChartAppId");
    }
    
    /**
     * 获取微信appSecret
     * @return
     */
    public String getWechatAppSecret()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("weiChartAppSecret");
    }
    
    /**
     * 获取微信支付回调通知URL
     * @return
     */
    public String getWechatPaymentCallback()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("weiChartNotifyUrl");
    }
    
    /**
     * 获取微信Token
     * @return
     */
    public String getWechatToken()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("weiChartToken");
    }
    
    /**
     * 获取微信支付完成后跳转路径
     * @return
     */
    public String getWechatMentToUrl()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("MPWeiChartInUrl");
    }
    
    /**
     * 获取商户微信支付授权地址IP
     * @return
     */
    public String getWechatIPSetting()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("wepay_ip_setting");
    }
    
    /**
     * 获取扫码回调地址
     * @return
     */
    public String getWechatScanNotifyUrl()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("wepay_mobile_notify");
    }
    /**
     * 获取智游宝接口参数
     */
    public String getZybIntefaceParame(String key)
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue(key);
    }
    
    /**
     * 获取智游宝企业账户
     */
    public String getZybuserName()
    {
    	return ThirdPartyPropertiesUtil.getPropertiesUtil().getValue("zyb_userName");
    }
}
