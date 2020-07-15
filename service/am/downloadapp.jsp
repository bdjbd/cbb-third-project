<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ page import="java.lang.*" %>
<%@ page import="com.fastunit.Var" %>



<script>
var downloadAppPath='<%=Var.get("APP_ANDROID_PATH_org")%>';
var downloadAppIosPath='<%=Var.get("APP_IOS_PATH_org")%>';
</script>

<html>
    <head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>下载查保宝查勘端 <%=Var.get("APP_VER_org")%></title>
    <script type="text/javascript">
        var browser = {
            versions: function () {
                var u = navigator.userAgent, app = navigator.appVersion;
                return {
                    trident: u.indexOf('Trident') > -1,
                    presto: u.indexOf('Presto') > -1,
                    webKit: u.indexOf('AppleWebKit') > -1,
                    gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,
                    mobile: !!u.match(/AppleWebKit.*Mobile.*/),
                    ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
                    android: u.indexOf('Android') > -1 || u.indexOf('Adr') > -1,
                    iPhone: u.indexOf('iPhone') > -1,
                    iPad: u.indexOf('iPad') > -1,
                    webApp: u.indexOf('Safari') == -1,
                    weixin: u.indexOf('MicroMessenger') > -1,
                    qq: u.match(/\sQQ/i) == " qq"
                };
            }(),
            language: (navigator.browserLanguage || navigator.language).toLowerCase()
        };
        if (browser.versions.ios) {
            window.location.href = downloadAppIosPath;
        } else if (browser.versions.android) {
            window.location.href = downloadAppPath;
        } else {
            window.location.href = downloadAppPath;
        }
    </script>
</head>
<body>
</body>
</html>