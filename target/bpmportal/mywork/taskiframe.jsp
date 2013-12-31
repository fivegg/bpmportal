<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
if (request.getParameter("processTheme")!=null ){
%>
<iframe class="task_iframe"  frameborder="0" allowtransparency="true" src="<%=request.getContextPath() %>/mywork/startprocess.jsp?processTheme=${param.processTheme }" width="100%"  style="height:100%"></iframe>
<%}
else { %>
<iframe class="task_iframe"  frameborder="0" allowtransparency="true" src="<%=request.getContextPath() %>/mywork/taskdetail.jsp?processInstanceUUID=${param.processInstanceUUID }" width="100%"  style="height:100%"></iframe>
<%} %>
