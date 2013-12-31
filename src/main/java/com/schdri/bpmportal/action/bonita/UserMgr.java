package com.schdri.bpmportal.action.bonita;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.User;

import com.opensymphony.xwork2.ActionSupport;
import com.schdri.bpm.BPMModule;
import com.schdri.bpm.BPMUtil;
import com.schdri.bpmportal.dto.TreeItem;

public class UserMgr extends ActionSupport {
	//get filter parameters like role or init root group
	private String userRoleFilter="";
	private String groupInitFilter="";
	
	//out 
	private List<TreeItem> userTree;
		
	
	public String getUserRoleFilter() {
		return userRoleFilter;
	}
	public void setUserRoleFilter(String userRoleFilter) {
		this.userRoleFilter = userRoleFilter;
	}
	public String getGroupInitFilter() {
		return groupInitFilter;
	}
	public void setGroupInitFilter(String groupInitFilter) {
		this.groupInitFilter = groupInitFilter;
	}
	public List<TreeItem> getUserTree() {
		return userTree;
	}
	public void setUserTree(List<TreeItem> userTree) {
		this.userTree = userTree;
	}
	
	
	private List<TreeItem> setGroupAndChildren(List<Group> groups,String rootPath,String groupInitFilter,String userRoleFilter) throws Exception{
		List<TreeItem> result=new ArrayList<TreeItem>();
		for(Group g: groups){
			TreeItem item=new TreeItem();
			item.setId(g.getUUID());
			item.setText(g.getLabel());
			item.setPath(rootPath + "//" + g.getName());
			
			boolean bSearch;
			boolean bAdd;
			if (item.getPath().length()<groupInitFilter.length()){
				bSearch=groupInitFilter.startsWith(item.getPath()) ? true:false;
				bAdd=false;
			}
			else{
				bSearch=item.getPath().startsWith(groupInitFilter)? true:false;
				bAdd=bSearch;
			}
				
			//过滤不合格的组
			if (bSearch){
				List<TreeItem> childTreeItems=new ArrayList<TreeItem>();
				
				List<Group> childrenGroups=BPMModule.getInstance().getChildrenGroupsByUUID(g.getUUID());
				if (childrenGroups==null || childrenGroups.size()==0)
					item.setChildren(null);
				else
					childTreeItems=setGroupAndChildren(childrenGroups,item.getPath(),groupInitFilter,userRoleFilter);
				
				if (bAdd){ //当前组符合要求，添加进结果
					//add users
					
					boolean bAddUser=true;
					//check role filter
					List<User> users=BPMModule.getInstance().getAllUsersInGroup(g.getUUID());
					for(User u : users){
						if (userRoleFilter!=null && !userRoleFilter.isEmpty()){
							Set<Membership> ms=u.getMemberships();
							for(Membership m : ms){
								if (m.getRole().getName().equals(userRoleFilter)){
									bAddUser=true;
									break;
								}
								bAddUser=false;
							}
						}
						if (bAddUser){
							TreeItem uitem=new TreeItem();
							uitem.setId(u.getUUID());
							uitem.setText(u.getUsername());
							uitem.setChildren(null);
							//uitem.setUser(true);
							uitem.setPath(item.getPath()+"//"+uitem.getText()); //useless
							
							childTreeItems.add(uitem);
						}
					}
					if (childTreeItems.size()>0){
						item.setChildren(childTreeItems);
						result.add(item);
					}
				}
				else{ //当前组符合不符合要求，将子搜索结果添加进结果（有可能子结果符合要求,例如filer:/root/project,current:/root,child:/root/project）
					if (childTreeItems.size()>0)
						result.addAll(childTreeItems);
				}
			}
		}
		return result;
	}
	public String selectUser() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Cache-Control", "no-cache");
		
		userRoleFilter= new String(userRoleFilter.getBytes("ISO-8859-1"),"utf-8");
		groupInitFilter= new String(groupInitFilter.getBytes("ISO-8859-1"),"utf-8");
		
		userTree=new ArrayList<TreeItem>();
		List<Group> rootGroups=BPMUtil.getAllRootGroup(null);
		userTree=setGroupAndChildren(rootGroups,"",groupInitFilter,userRoleFilter);
		return SUCCESS;
	}
}
