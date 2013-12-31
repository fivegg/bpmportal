<%@ page contentType="text/html; charset=utf-8" language="java" %>
<% String type=request.getParameter("type"); %>
<div style=\"padding:10px\">
<table id="table_<%= type %>" class="easyui-datagrid" style="width:auto;height:auto"  
            data-options="pagination:true,url:'<%=request.getContextPath() %>/ajax/listInvolved.action?type=<%= type %>',rownumbers:true,singleSelect:true,toolbar:toolbar,fitColumns:true,sortName:'lastupdate',sortOrder:'desc'" 
            title="" iconCls="icon-save"  autoRowHeight="false" >
	<thead>  
		<tr>  
			<th field="category1" width="100"  sortable="true" >类型</th>  
			<th field="category2" width="100"  sortable="true" >二级类型</th>    
			<th field="process" width="300"  sortable="true">流程</th>  
			<th field="state" width="100"  sortable="true" >状态</th>  
			<th field="lastupdate" width="140" align="right"  sortable="true">修改时间</th>  
			<th field="version" align="right"  sortable="true">版本</th>  
		</tr>  
	</thead>  
</table>  
<script type="text/javascript">  
        var toolbar = [{  
            text:'刷新',  
            iconCls:'icon-reload',  
            handler:function(){$('#table_<%= type %>').datagrid('reload')}  
        }];  
</script>
</div>