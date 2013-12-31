function bpm_selectUserForBonita(control,groupInitFilter,userRoleFilter){
	var d="<div id=\"dlg_bpm_selectUser\" class=\"easyui-dialog\" title=\"select user\" style=\"width:400px;height:200px;padding:10px\"><input id=\"cc\" value=\"\"  style=\"width:360px;\" multiple >  </div>";
	$('body').append(d);
	
	try{
		var url='';
		if (groupInitFilter!=null && groupInitFilter.length)
			url='groupInitFilter='+groupInitFilter;
		if (userRoleFilter!=null && userRoleFilter.length){
			if (url.length>0)
				url=url+'&';
			url='userRoleFilter='+userRoleFilter;
		}
		url='/bpmportal/ajax/selectUser.action?'+url;
		$('#cc').combotree({  
			url: url,  
			required: true  
		}); 
		$('#cc').combotree('tree').tree({onlyLeafCheck:true});
		$('#dlg_bpm_selectUser').dialog({ 
					iconCls: 'icon-save',  
					buttons: [{  
						text:'确定',  
						iconCls:'icon-ok',  
						handler:function(){  
							var str=$('#cc').combotree('getText');
							document.getElementById(control).getElementsByTagName("input")[0].value =str;
							//alert(str); 
							try{
							$('#dlg_bpm_selectUser').dialog('close');
							}
							catch(e){}
							$('#dlg_bpm_selectUser').remove();
						}  
					},{  
						text:'取消',  
						handler:function(){  
							try{
							$('#dlg_bpm_selectUser').dialog('close');
							}
							catch(e){}
							$('#dlg_bpm_selectUser').remove();
						}  
					}]  
				});
	}catch(e){
		$('#dlg_bpm_selectUser').remove();
	}
}
function bpm_selectUserForBonitaBak1(control){
	$("<div id=\"dlg_bpm_selectUser\" class=\"easyui-dialog\" title=\"选择用户\" style=\"width:400px;height:200px;padding:10px\"><form id=\"form_selectUser\"><select id=\"cc\" class=\"easyui-combotree\" data-options=\"url:'http://localhost:8080/bpmportal/temp/tree_data1.json'\" multiple style=\"width:360px;\"></select></form></div>").appendTo('body');

	$('#dlg_bpm_selectUser').dialog({ 
                iconCls: 'icon-save',  
                buttons: [{  
                    text:'确定',  
                    iconCls:'icon-ok',  
                    handler:function(){  
						var str=$('#cc').combotree('getText');
                        control.value =str;
						alert(str); 
						try{
						$('#dlg_bpm_selectUser').dialog('close');
						}
						catch(e){}
						$('#dlg_bpm_selectUser').remove();
                    }  
                },{  
                    text:'取消',  
                    handler:function(){  
                        $('#dlg_bpm_selectUser').dialog('close');
						$('#dlg_bpm_selectUser').remove();
                    }  
                }]  
            });

}
function bpm_selectUser(){
	$("<div id=\"dlg_bpm_selectUser\" class=\"easyui-dialog\" title=\"select user\" style=\"width:400px;height:200px;padding:10px\"><form id=\"form_selectUser\"><select id=\"cc\" class=\"easyui-combotree\" data-options=\"url:'http://localhost:8080/bpmportal/temp/tree_data1.json'\" multiple style=\"width:360px;\"></select></form></div>").appendTo('body');
	
	$('#dlg_bpm_selectUser').dialog({ 
                iconCls: 'icon-save',  
                buttons: [{  
                    text:'OK',  
                    iconCls:'icon-ok',  
                    handler:function(){  
						var str=$('#cc').combotree('getText');
                        alert(str); 
                    try{
						$('#dlg_bpm_selectUser').dialog('close');
						}
						catch(e){}
						$('#dlg_bpm_selectUser').remove();
                    }  
                },{  
                    text:'Cancel',  
                    handler:function(){  
                        try{
						$('#dlg_bpm_selectUser').dialog('close');
						}
						catch(e){}
						$('#dlg_bpm_selectUser').remove();
                    }  
                }]  
            });
}