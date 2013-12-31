<%@ page contentType="text/html; charset=utf-8" language="java"  errorPage="" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@include file="../common/web_header.jsp" %>

<script type="text/javascript">
	function mywork_openTabByA(plugin,action){
	    var title=$(plugin).html();
	    mywork_openTab(title,action);
	}
	    
	function mywork_openTab(title,url){
		if ($('#tabsControl').tabs('exists',title)){
			$('#tabsControl').tabs('select', title);
		} else {
			$('#tabsControl').tabs('add',{
				title:title,
				href:url,
				closable:true,
				extractor:function(data){
					return data;
				}
			});
		}
	}
	function mywork_updateCurrentTab(title,url){
		//update the selected panel with new title and content
		var tab = $('#tabsControl').tabs('getSelected');  // get selected panel
		$('#tabsControl').tabs('update', {
			tab: tab,
			options: {
				title: title,
				href: url  // the new content URL
			}
		});
	}
	function mywork_gotoTab(title){
		$('#tabsControl').tabs('select',title);
	}
	function mywork_closeTab(){
		var tab = $('#tabsControl').tabs('getSelected');  // get selected panel
		var index = $('#tabsControl').tabs('getTabIndex',tab);
		$('#tabsControl').tabs('close',index);
	}
	function mywork_closeTabAndRefresh(){
		mywork_closeTab();
		$('#table_inbox').datagrid('reload');
	}
</script>

<div region="west" border="false" split="true" title="Plugins" style="width:250px;padding:5px;">
	<ul class="easyui-tree">
		<li >
			<span>我的工作</span>
			<ul><li><a href="#" onclick="mywork_openTabByA(this,'<s:url value='/mywork/inbox.jsp' />')"><s:text name="mywork.inbox" /></a></li>
				<li><a href="#" onclick="mywork_openTabByA(this,'<s:url value='/mywork/involved.jsp?type=start' />')"><s:text name="mywork.startedbyme" /></a></li>
				<li><a href="#" onclick="mywork_openTabByA(this,'<s:url value='/mywork/involved.jsp?type=involved' />')"><s:text name="mywork.involved" /></a></li>
				<li><a href="#" onclick="mywork_openTabByA(this,'<s:url value='/mywork/finished.jsp' />')"><s:text name="mywork.finished" /></a></li>
			</ul>
		</li>
		<li>
			<s:iterator value="menuTree" id="item">
				<span><s:property value="#item.text"/></span>
				<s:if test="#item.children!=null && #item.children.size()>0">
				<ul>
					<s:iterator value="#item.children" id="itemCat1">
					<s:if test="#itemCat1.isOpen()|| #itemCat1.isLeaf()" >
						<li>
					</s:if>
					<s:else >
						<li   data-options="state:'closed'">  
					</s:else>
					<span><s:property value="#itemCat1.text" /></span>
						<s:if test="#itemCat1.children!=null && #itemCat1.children.size()>0">
							<ul>
								<s:iterator value="#itemCat1.children" id="itemCat2">
								<s:if test="#itemCat2.isOpen()|| #itemCat2.isLeaf()" >
								<li>
								</s:if>
								<s:else >
								<li   data-options="state:'closed'">  
								</s:else>
									<s:if test="#itemCat2.isLeaf()" >
										<a href="#" onclick="mywork_openTabByA(this,'<s:url value='/mywork/taskiframe.jsp' ><s:param name="processTheme" value="#itemCat2.id" /></s:url>')"><s:property value="#itemCat2.text"/></a>
									</s:if>
									<s:else>
										<span><s:property value="#itemCat2.text" /></span>
										<s:if test="#itemCat2.children!=null && #itemCat2.children.size()>0">
										<ul>
											<s:iterator value="#itemCat2.children" id="itemProc">
											<li>
											<a href="#" onclick="mywork_openTabByA(this,'<s:url value='/mywork/taskiframe.jsp' ><s:param name="processTheme" value="#itemProc.id" /></s:url>')"><s:property value="#itemProc.text"/></a>
											</li>
											</s:iterator>
										</ul>
										</s:if>
									</s:else>
									
								</li>
								</s:iterator>
							</ul>
						</s:if>
					</li>
					</s:iterator>
				</ul>
				</s:if>
			</s:iterator>
		</li>
	</ul>

</div>
<div region="center" border="false">
	<div id="tabsControl" class="easyui-tabs" fit="true" border="false" plain="true">
		<div title="Welcome" href="intro.html"></div>
	</div>
</div>

		   
<%@include file="../common/web_footer.jsp" %>
