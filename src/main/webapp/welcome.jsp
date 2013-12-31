<%@ page contentType="text/html; charset=utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
  <head>
    <title><s:text name="title.text"/></title>
  </head>  
  <body>
  	${userName},<s:text name="label.welcome"/><br/>
  	${reslutStr}
  </body>
</html>
