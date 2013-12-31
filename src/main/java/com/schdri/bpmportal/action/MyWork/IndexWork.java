package com.schdri.bpmportal.action.MyWork;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ow2.bonita.light.LightProcessDefinition;

import com.opensymphony.xwork2.ActionSupport;
import com.schdri.bpm.BPMModule;
import com.schdri.bpmportal.dto.TreeItem;


public class IndexWork extends ActionSupport  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5072376897554521887L;
	private List<TreeItem> menuTree=new ArrayList<TreeItem>();
	
	
	public List<TreeItem> getMenuTree() {
		return menuTree;
	}

	public void setMenuTree(List<TreeItem> menuTree) {
		this.menuTree = menuTree;
	}

	private TreeItem findItemOrCreateNew(List<TreeItem> tree,String searchText) throws Exception{
		TreeItem tiCurrent=null;
		if (tree==null)
			throw new Exception("findItemOrCreateNew input param:tree shouldn't be null");
			
		for(TreeItem ti : tree){
			if (ti.getText().equals(searchText)){
				tiCurrent=ti;
				break;
			}
		}
		
		if (tiCurrent==null){
			tiCurrent=new TreeItem(searchText,searchText,null);
			tiCurrent.setOpen(false);
			tiCurrent.setChildren(new ArrayList<TreeItem>());
			tree.add(tiCurrent);
		}
		return tiCurrent;
	}
	
	public void getStartProcessMenu() throws Exception  {
		TreeItem rootStartProcessMenu=new TreeItem("启动流程","启动流程",new ArrayList<TreeItem>());
		menuTree.add(rootStartProcessMenu);
		
		Set<LightProcessDefinition> ps= BPMModule.getInstance().getStartableProcesses();
		for(LightProcessDefinition lpd: ps){
			String[] cates=BPMModule.getInstance().getProcessBpmPortalCategroy(lpd);
			TreeItem tiCurrent=findItemOrCreateNew(rootStartProcessMenu.getChildren(),cates[0]);
			
			if (cates[1]!=null && !cates[1].isEmpty()){
				tiCurrent=findItemOrCreateNew(tiCurrent.getChildren(),cates[1]);
				//tiCurrent.setChildren(new ArrayList<TreeItem>());
			}
			TreeItem leaf=new TreeItem(lpd.getUUID().getValue(),lpd.getLabel(),null);
			leaf.setExtraInfo(lpd.getUUID().getValue());
			leaf.setLeaf(true);
			tiCurrent.getChildren().add(leaf);
		}
	}
	public String index() throws Exception{
		getStartProcessMenu();
		return SUCCESS;
	}


}
