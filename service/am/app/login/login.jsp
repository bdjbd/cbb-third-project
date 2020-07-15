<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>E快修</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="css/bootstrap.css" rel="stylesheet">

    <link href="css/flat-ui.css" rel="stylesheet">
    <link rel="shortcut icon" href="images/favicon.ico">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements. All other JS at the end of file. -->
    <!--[if lt IE 9]>
      <script src="js/html5shiv.js"></script>
    <![endif]-->
  </head>
  <body>
    <div class="container">
        <div class="login">
        <div class="login-screen">
            <div class="login-icon">
            <img src="images/login/icon.png" alt="E快修" />
            <h4>E快修</h4>
          </div>
          <div class="login-form">
              <form  method="post"  action="/app/login/login.do">
            <div class="control-group">
              <input type="text" name="userName" class="login-field" value="" placeholder="用户名" id="login-name" />
              <label class="login-field-icon fui-man-16" for="login-name"></label>
            </div>

            <div class="control-group">
              <input type="password"  name="password" class="login-field" value="" placeholder="密码" id="login-pass" />
              <label class="login-field-icon fui-lock-16" for="login-pass"></label>
            </div>
			<a class="btn btn-primary btn-large btn-block" onclick=doSubmit('')>登录</a>
			<input type="hidden" name="login.login" value="" />
           
            <a class="login-link" href="#">忘记密码</a>
            </form>
             <input type="submit"  value="提交" />
          </div>
        </div>
      </div>
    </div> <!-- /container -->

    <!-- Load JS here for greater good =============================
    <script src="js/jquery-1.8.2.min.js"></script>
    <script src="js/jquery-ui-1.10.0.custom.min.js"></script>
    <script src="js/jquery.dropkick-1.0.0.js"></script>
    <script src="js/custom_checkbox_and_radio.js"></script>
    <script src="js/custom_radio.js"></script>
    <script src="js/jquery.tagsinput.js"></script>
    <script src="js/bootstrap-tooltip.js"></script>
    <script src="js/jquery.placeholder.js"></script>
    <script src="http://vjs.zencdn.net/c/video.js"></script>
    <script src="js/application.js"></script>
    [if lt IE 8]>
      <script src="js/icon-font-ie7.js"></script>
      <script src="js/icon-font-ie7-24.js"></script>
    <![endif]
    <script type="text/javascript">
      var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
      document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
    </script>
    <script type="text/javascript">
      try{
        var pageTracker = _gat._getTracker("UA-19972760-2");
        pageTracker._trackPageview();
        } catch(err) {}
    </script>
    -->
  </body>
</html>
