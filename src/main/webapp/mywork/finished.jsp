<%@ page contentType="text/html; charset=utf-8" language="java" %>
<div style=\"padding:10px\">
<table id="table_finished" class="easyui-datagrid" style="width:auto;height:auto"  
            data-options="url:'<%=request.getContextPath() %>/ajax/listFinished.action',rownumbers:true,singleSelect:true,toolbar:toolbar,fitColumns:true,sortName:'lastUpdateDate',sortOrder:'desc'" 
            title="" iconCls="icon-save"  autoRowHeight="false" >
	<thead>  
		<tr>  
			<th field="UUID" width="0" hidden="true">taskUUID</th>  
			<th field="category1" width="100"  sortable="true" >类型</th>  
			<th field="category2" width="100"  sortable="true" >二级类型</th>    
			<th field="rootProcessNameOrUrl" width="200"  sortable="true">流程</th>  
			<th field="taskNameOrUrl" width="480"  sortable="true" >任务</th>  
			<th field="lastUpdateDate" width="140" align="right"  sortable="true">修改时间</th>  
		</tr>  
	</thead>  
</table>  
<script type="text/javascript">  
        var toolbar = [{  
            text:'刷新',  
            iconCls:'icon-reload',  
            handler:function(){$('#table_finished').datagrid('reload')}  
        }];  
</script>
</div>