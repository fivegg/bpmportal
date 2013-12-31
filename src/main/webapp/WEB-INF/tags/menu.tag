<%@ tag language="java" pageEncoding="UTF-8"  import="java.util.*" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ attribute name="selected" %>
<%  
	String menu[]={"主页","我的工作","设计管理","报表","查询"};
	String href[]={request.getContextPath(),"mywork.jsp","#","#","#"};
%>
   <div class="row_1 cf">
    <div class="company_logo cf">
     <img src="../images/xtd_star.png" height="54" width="60" />
     <p><span>设计院业务系统测试</span></p>
    </div>
   </div>
   <div class="row_2 cf">
    <ul class="hmenubar cf">
     <% String m,h;
     	for(int i=0;i<menu.length;i++){
    		m=menu[i];
    		h=href[i];
    		if (m.equals(selected))
	     		out.print("<li><a class=\"clicked\" href=\"");
    		else
    			out.print("<li><a href=\"");
    	
    		out.print(h);
    		out.print("\">");
    		out.print(m);
    		out.println("</a>");
     	}
     %>
     <script type="text/javascript" src="scripts/menu_selection.js"></script>
    </ul>
   </div>