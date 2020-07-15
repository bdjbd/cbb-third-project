<%@ page contentType="image/jpeg" import="com.fastunit.user.login.ValidateCode,javax.imageio.ImageIO"%> 
<% 
out.clear();//for resin
response.setHeader("Pragma","No-cache"); 
response.setHeader("Cache-Control","no-cache"); 
response.setDateHeader("Expires", 0); 
ImageIO.write(ValidateCode.createImage(request), "JPEG", response.getOutputStream()); 
%> 