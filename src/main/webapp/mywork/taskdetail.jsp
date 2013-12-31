<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="java.net.URLDecoder"%>

<%

    request.setCharacterEncoding("ISO-8859-1");

    //指定输入编码

    response.setCharacterEncoding("UTF-8");

    //指定输出编码
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
		<script type="text/javascript" src="<%=request.getContextPath() %>/js/datagrid-detailview.js"></script>
	</head>
</head>
<body>
	<div id="WidgetRefreshCasesContainer" onclick="loadTask()"><div  style="display: none;"></div></div>
	<div id="backToInboxButton" onclick="window.parent.mywork_closeTabAndRefresh()"><div  style="display: none;"></div></div>
	<div title="任务" data-options="iconCls:'icon-ok'" style="overflow:auto;padding:10px;">  
		<!-- 工具条 -->
		<div style="padding:5px;border:1px solid #ddd;">  
	        <!--  <a id="backToInboxButton" href="#" class="easyui-linkbutton" data-options="plain:true" onclick="mywork_updateCurrentTab('代办任务','<%=request.getContextPath() %>/mywork/inbox.jsp')">返回</a>  -->  
	        <!--  <a id="backToInboxButton" href="#" class="easyui-linkbutton" data-options="plain:true" onclick="mywork_gotoTab('${param.returnTab}')">返回</a>  -->
	        <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-edit'" onclick="loadTask()">刷新</a>
	        <a id="fullscreen" target="_blank" href="#" class="easyui-linkbutton" data-options="plain:true" >全屏</a>
	        <a id="suspendTaskButton" href="#" class="easyui-linkbutton" data-options="plain:true" onclick="suspendTask()">暂停任务</a>
	        <a id="skipTaskButton" href="#" class="easyui-linkbutton" data-options="plain:true" >跳过任务</a>  
			<a href="#" class="easyui-linkbutton" data-options="plain:true" onclick="incHeight()">增加高度</a>  
	        <a id="btn-assign" href="#" class="easyui-splitbutton" data-options="menu:'#mmAssign',iconCls:'icon-ok'">分配</a>  
	        <a id="btn-priority" href="#" class="easyui-menubutton" data-options="menu:'#mmPriority',iconCls:'icon-help'">优先级</a>  
	    </div>  
	    <div id="mmAssign" style="width:150px;">  
	        <div data-options="iconCls:'icon-undo'">分配给我</div>  
	        <div data-options="iconCls:'icon-redo'">分配给...</div>  
	        <div>取消分配</div>  
	    </div>  
	    <div id="mmPriority" style="width:100px;">  
	        <div data-options="iconCls:'icon-ok'">一般</div>  
	        <div data-options="iconCls:'icon-cancel'">高</div>  
	        <div data-options="iconCls:'icon-high'">紧急</div>
	    </div>
	    
	    <div>
		<div id="infoPanel" style="height:100px;padding:30px;">任务执行错误，请联系管理员或者尝试跳过任务</div>
	    <iframe class="form_entry_frame"  id="tempid" frameborder="0" allowtransparency="true" src="" width="100%"  style="height:0"></iframe>
	    </div>  
	</div>
	<div title="历史" data-options="iconCls:'icon-search'" style="padding:0px;">  
		<table id="table_history" class="easyui-datagrid" title="历史" style="width:auto;height:150px"  
	           data-options="singleSelect:true,collapsible:true,url:'<%=request.getContextPath() %>/ajax/listHistory.action?processInstanceUUID=${param.processInstanceUUID }'">  
	    	<thead>  
	        	<tr>  
					<script>
					function formatDetail(value,row){
						return '<a href="#" onclick="openW(\''+ row.strUUID+ '\')">'+ value+'</a>';
					}
					</script>
	        		<th data-options="field:'strUUID',width:0,hidden:true">ID</th> 
	            	<th data-options="field:'activityLabel',width:160,formatter:formatDetail">任务</th>  
	            	<th data-options="field:'type',width:80">类型</th>  
	           		<th data-options="field:'readyDate',width:140"">启动时间</th>  
	           		<th data-options="field:'startedDate',width:140"">开始时间</th>
	           		<th data-options="field:'endedDate',width:140"">结束时间</th>
	           		<th data-options="field:'taskCandidates',width:100"">候选人</th>
	           		<th data-options="field:'taskUser',width:60"">执行人</th>
	           		<th data-options="field:'state',width:60"">状态</th>
	        	</tr>  
	     	</thead>  
	    </table>
	</div>  
	<div id="win" class="easyui-window" title="View History Window" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:80%;height:80%;padding:10px;">  
       加载中...  
    </div>
	<div title="备注" data-options="iconCls:'icon-help'" style="padding:0px;">  
	    <table id="table_feeds" class="easyui-datagrid" title="备注" style="width:auto;height:150px"  
	           data-options="singleSelect:true,collapsible:true,url:'<%=request.getContextPath() %>/ajax/listComments.action?processInstanceUUID=${param.processInstanceUUID }'">  
	    	<thead>  
	        	<tr>  
	            	<th data-options="field:'date',width:120">时间</th>  
	            	<th data-options="field:'userId',width:100">发言人</th>  
	           		<th data-options="field:'message',width:500">内容</th>  
	        	</tr>  
	     	</thead>  
	    </table>
	    <!-- 发布备注位置 -->    
	    <div>
	    <form id="feedback" action="<%=request.getContextPath() %>/ajax/saveComment.action" method="post">
	      <table>  
                <tr>  
                    <td>添加备注:</td>  
                    <td><input name="message" type="text"></input></td>
                    <td><input type="submit" value="保存"></input></td>
                    <td><input name="processInstanceUUID" type="hidden" value=${param.processInstanceUUID}></input></td>    
                </tr>  
             </table>   
	    </form>
	    <script type="text/javascript">  
        $(function(){  
            $('#feedback').form({  
				success:function(data){  
					$('#table_feeds').datagrid('reload');  
				}  
			})  
        });  
    </script>  
	    </div>
	         
	</div>  
	
	
<script type="text/javascript">
function getWidth(percent){  
	return $(window).width() * percent;  
}  
function getHeight(percent){  
	return $(window).height() * percent;  
}  

function openW(strUUID){
	var w=getWidth(0.8);
	var h=getHeight(0.8);
	$('#win').window({height:h,width:w});
	$('#win').window('center');
	$('#win').window('open');
	$('#win').window('refresh', '<%=request.getContextPath() %>/mywork/taskViewForm.action?processInstanceUUID=${param.processInstanceUUID}&uuid='+strUUID);  
}
function refreshFrame(frame_id,frame_url){
	var $ifm=$(".form_entry_frame:first");
	$ifm.attr('id',frame_id);
	$ifm.attr('src',frame_url+"&rnd=".concat(Math.random()));
	$ifm.removeAttr('style'); //remove height setted by bonita
	//var ifm=$ifm[0];
	//ifm.contentWindow.location.reload();
	
	$('#fullscreen').attr("href",frame_url.replace("mode=form","mode=app"));
}	
function switchUI(ready){
	if (ready){
		var $ifm=$(".form_entry_frame:first");
		$ifm.show();
		$('#infoPanel').hide();
	}
	else{
		$(".form_entry_frame:first").hide();
		$('#infoPanel').show();
	}
}
function loadTask(){
	var $ifm=$(".form_entry_frame:first");
	$ifm.removeAttr('style'); //remove height setted by bonita

	$.getJSON('<%=request.getContextPath() %>/ajax/runTask.action?processInstanceUUID=${param.processInstanceUUID }',
	function(taskInstance){
		if (taskInstance.activityDefinitionUUID==null)
			disableTaskControl();
		else{
			if (taskInstance.state=='READY'){
				switchUI(true);
				refreshFrame(taskInstance.activityDefinitionUUID.value,taskInstance.taskNameOrUrl);
			}
			else{
				switchUI(false);
				$('#skipTaskButton').click(function(){
								skipTask(taskInstance.strUUID);
								});
			}
			reloadComments();
			reloadHistory();
		}
	}
	);
}
function skipTask(uuid){
	$.getJSON('<%=request.getContextPath() %>/ajax/skipActivity.action?uuid='+uuid,
	function(info){
		//alert(info.message);
		$('#infoPanel').text(info.message);
		reloadComments();
		reloadHistory();
	}
	);
}
function incHeight(){
	var f=$('.form_entry_frame:first');
	f.height(f.height()+100);
}
function refresh(){
	$('.form_entry_frame:first')[0].contentWindow.location.reload();
	reloadComments();
	reloadHistory();
}
function reloadComments(){
	$('#table_feeds').datagrid('reload');  
}
function reloadHistory(){
	$('#table_history').datagrid('reload');  
}
function disableTaskControl(){
	$('#btn-assign').menubutton('disable');
	$('#btn-priority').menubutton('disable')
	$('.form_entry_frame:first').attrib('display','none');
}
loadTask();
</script>
</body>
</html>