<%@ page contentType="text/html; charset=utf-8" language="java" %>
<div style=\"padding:10px\">
<table id="table_inbox" class="easyui-datagrid" style="width:auto;height:auto"  
            data-options="pagination:true,url:'<%=request.getContextPath() %>/ajax/listInbox.action',rownumbers:true,singleSelect:true,toolbar:toolbar,fitColumns:true,sortName:'lastUpdateDate',sortOrder:'desc'" 
            title="" iconCls="icon-save"  autoRowHeight="false" >
	<thead>  
		<tr>  
			<th field="priority" width="50"  sortable="true" >优先级</th>
			<th field="category1" width="100"  sortable="true" >类型</th>  
			<th field="category2" width="100"  sortable="true" >二级类型</th>    
			<th field="rootProcessNameOrUrl" width="200"  sortable="true">流程</th>  
			<th field="taskNameOrUrl" width="400"  sortable="true" >任务</th>  
			<th field="taskCandidates" width="140"  sortable="true" >候选人</th>  
			<th field="lastUpdateDate" width="140" align="right"  sortable="true">修改时间</th>  
		</tr>  
	</thead>  
</table>  
<script type="text/javascript">  
        var toolbar = [{  
            text:'刷新',  
            iconCls:'icon-reload',  
            handler:function(){$('#table_inbox').datagrid('reload')}  
        }];  
</script>
</div>