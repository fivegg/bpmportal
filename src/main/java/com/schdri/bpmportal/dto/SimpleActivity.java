package com.schdri.bpmportal.dto;

import java.util.Date;

import org.apache.struts2.json.annotations.JSON;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition.Type;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.uuid.ActivityDefinitionUUID;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightTaskInstance;


public class SimpleActivity   {
	private String activityName ;
	private ActivityInstanceUUID activityInstanceUUID ;
	private boolean isTask ;

	private Date startedDate ;
	private Date endedDate ;
	private Date readyDate ;
	private String state ;
	private String activityLabel ;
	private String activityDescription ;
	private Date lastUpdateDate ;
	private Date expectedEndDate ;
	private String priority ;
	private String type ; //  public static enum Type {    Automatic, Human, Timer, Decision, Subflow, SendEvents, ReceiveEvent, ErrorEvent, SignalEvent  }
	private ActivityDefinitionUUID activityDefinitionUUID;
	private ProcessDefinitionUUID processDefinitionUUID ;
	private ProcessInstanceUUID processInstanceUUID ;
	private ProcessInstanceUUID rootInstanceUUID ;
	private String taskUser ;
	private String startedBy ;
	private String endedBy ;
	private Date createdDate ;
	private boolean isTaskAssigned ;
	
	//add by robin
	private ProcessDefinitionUUID rootProcessUUID;
	private String rootProcessNameOrUrl;
	private String taskNameOrUrl;
	private String taskCandidates;
	//private String strUUID;
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
	public String getStrUUID() {
		return this.getUUID().getValue();
	}
	public void setStrUUID(String u) {
		//this.strUUID=u;
		this.setUUID(new ActivityInstanceUUID(u));
	}
	
	public String getTaskCandidates() {
		return taskCandidates;
	}
	public void setTaskCandidates(String taskCandidates) {
		this.taskCandidates = taskCandidates;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public ActivityInstanceUUID getUUID() {
		return activityInstanceUUID;
	}
	
	public ActivityDefinitionUUID getActivityDefinitionUUID() {
		return activityDefinitionUUID;
	}
	public void setActivityDefinitionUUID(
			ActivityDefinitionUUID activityDefinitionUUID) {
		this.activityDefinitionUUID = activityDefinitionUUID;
	}
	public void setUUID(ActivityInstanceUUID activityInstanceUUID) {
		this.activityInstanceUUID = activityInstanceUUID;
	}
	public boolean isTask() {
		return isTask;
	}
	public void setTask(boolean isTask) {
		this.isTask = isTask;
	}
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Date getStartedDate() {
		return startedDate;
	}
	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Date getEndedDate() {
		return endedDate;
	}
	public void setEndedDate(Date endedDate) {
		this.endedDate = endedDate;
	}
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Date getReadyDate() {
		return readyDate;
	}
	public void setReadyDate(Date readyDate) {
		this.readyDate = readyDate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getActivityLabel() {
		return activityLabel;
	}
	public void setActivityLabel(String activityLabel) {
		this.activityLabel = activityLabel;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Date getExpectedEndDate() {
		return expectedEndDate;
	}
	public void setExpectedEndDate(Date expectedEndDate) {
		this.expectedEndDate = expectedEndDate;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ProcessDefinitionUUID getProcessDefinitionUUID() {
		return processDefinitionUUID;
	}
	public void setProcessDefinitionUUID(ProcessDefinitionUUID processDefinitionUUID) {
		this.processDefinitionUUID = processDefinitionUUID;
	}
	public ProcessInstanceUUID getProcessInstanceUUID() {
		return processInstanceUUID;
	}
	public void setProcessInstanceUUID(ProcessInstanceUUID processInstanceUUID) {
		this.processInstanceUUID = processInstanceUUID;
	}
	public ProcessInstanceUUID getRootInstanceUUID() {
		return rootInstanceUUID;
	}
	public void setRootInstanceUUID(ProcessInstanceUUID rootInstanceUUID) {
		this.rootInstanceUUID = rootInstanceUUID;
	}
	public String getTaskUser() {
		return taskUser;
	}
	public void setTaskUser(String taskUser) {
		this.taskUser = taskUser;
	}
	public String getStartedBy() {
		return startedBy;
	}
	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}
	public String getEndedBy() {
		return endedBy;
	}
	public void setEndedBy(String endedBy) {
		this.endedBy = endedBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public boolean isTaskAssigned() {
		return isTaskAssigned;
	}
	public void setTaskAssigned(boolean isTaskAssigned) {
		this.isTaskAssigned = isTaskAssigned;
	}
	public ProcessDefinitionUUID getRootProcessUUID() {
		return rootProcessUUID;
	}
	public void setRootProcessUUID(ProcessDefinitionUUID rootProcessUUID) {
		this.rootProcessUUID = rootProcessUUID;
	}
	public String getRootProcessNameOrUrl() {
		return rootProcessNameOrUrl;
	}
	public void setRootProcessNameOrUrl(String rootProcessName) {
		this.rootProcessNameOrUrl = rootProcessName;
	}
	public String getTaskNameOrUrl() {
		return taskNameOrUrl;
	}
	public void setTaskNameOrUrl(String taskNameOrUrl) {
		this.taskNameOrUrl = taskNameOrUrl;
	}
	
}