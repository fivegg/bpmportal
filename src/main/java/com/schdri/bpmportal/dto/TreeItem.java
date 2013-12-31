package com.schdri.bpmportal.dto;

import java.util.List;

import org.apache.struts2.json.annotations.JSON;

public class TreeItem {
	private String id; //used by combotree
	private String text; //display name
	private List<TreeItem> children;  //leaf item
	private String path; //used for group like /root/gourp1/group2
	private String extraInfo;
	private boolean isLeaf;
	private boolean isOpen;
	
	
	public TreeItem(){
	}
	public TreeItem(String id,String text,List<TreeItem> children){
		this.id=id;
		this.text=text;
		this.children=children;
	}
	

	
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	public boolean isLeaf() {
		return isLeaf;
	}
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@JSON(name="id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@JSON(name="text")
	public String getText() {
		return text;
	}
	public void setText(String name) {
		this.text = name;
	}

	public List<TreeItem> getChildren() {
		return children;
	}
	public void setChildren(List<TreeItem> children) {
		this.children = children;
	}
	
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	
}
