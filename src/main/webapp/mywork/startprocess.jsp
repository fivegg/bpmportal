<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.schdri.bpm.BPMUtil"%>
<%@page import="java.net.URLDecoder"%>

<%
	//指定输入编码
    //request.setCharacterEncoding("ISO-8859-1");
	//指定输出编码
    response.setCharacterEncoding("UTF-8");
	String procUUID=request.getParameter("processTheme");
	String url=BPMUtil.getUserXPProcessUrl(procUUID, null, null,true);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/themes/default/easyui.css">
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/themes/icon.css">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/main.css"  />
        
		<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.8.0.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.easyui.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.ba-hashchange.min.js"></script>
	</head>
</head>
<body>
	<div id="WidgetRefreshCasesContainer" onclick="checkHash()"><div  style="display: none;"></div></div>
	<div id="backToInboxButton" onclick="checkHash()"><div  style="display: none;"></div></div>
	<div title="任务" data-options="iconCls:'icon-ok'" style="overflow:auto;padding:10px;">  
	    <div>
	    <iframe class="form_entry_frame"  id="<%=procUUID %>" frameborder="0" allowtransparency="true" src="<%=url %>" width="100%"  ></iframe>
	    </div>  
	</div>
	
<script type="text/javascript">
function checkHash(){
	$(window).hashchange();
}
// Bind an event to window.onhashchange that, when the hash changes, gets the
// hash and adds the class "selected" to any matching nav link.
$(window).hashchange(function(){
	var hash = location.hash;
  
	// Set the page title based on the hash.
	hash = ( hash.replace('#CaseEditor/jou:', '' ) || '' ) ;
	//alert(hash);
	if (hash!='')
		window.location.href='taskdetail.jsp?processInstanceUUID=' + hash;
})

</script>
</body>
</html>