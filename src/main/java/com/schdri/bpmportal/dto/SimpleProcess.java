package com.schdri.bpmportal.dto;

public class SimpleProcess {
	private String processUUID;
	private String process;
	private String version;
	private String lastupdate;
	private String state;
	private String category1;
	private String category2;
	
	
	
	public String getCategory1() {
		return category1;
	}
	public void setCategory1(String category1) {
		this.category1 = category1;
	}
	public String getCategory2() {
		return category2;
	}
	public void setCategory2(String category2) {
		this.category2 = category2;
	}
	public String getProcessUUID() {
		return processUUID;
	}
	public void setProcessUUID(String processUUID) {
		this.processUUID = processUUID;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getLastupdate() {
		return lastupdate;
	}
	public void setLastupdate(String lastupdate) {
		this.lastupdate = lastupdate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
}
