package com.schdri.bpmportal.action.MyWork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.Comment;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightTaskInstance;

import com.opensymphony.xwork2.ActionSupport;
import com.schdri.bpm.BPMModule;
import com.schdri.bpm.BPMUtil;
import com.schdri.bpm.SetCredentialsInSessionFilter;
import com.schdri.bpmportal.dto.SimpleActivity;
import com.schdri.bpmportal.model.DatagridDtoModel;

@SuppressWarnings("serial")
public class TaskDetail extends ActionSupport {
	private String uuid; //input
	private String processInstanceUUID; //input
	private String message;
	private SimpleActivity taskInstance; //output
	private DatagridDtoModel<Comment> commentsTable; //output
	private DatagridDtoModel<SimpleActivity> historyTable; //output
	
	private static final Logger LOGGER = Logger.getLogger(TaskDetail.class.getName());
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getProcessInstanceUUID() {
		return processInstanceUUID;
	}
	public void setProcessInstanceUUID(String processInstanceUUID) {
		this.processInstanceUUID = processInstanceUUID;
	}
	public DatagridDtoModel<Comment> getCommentsTable() {
		return commentsTable;
	}
	
	public DatagridDtoModel<SimpleActivity> getHistoryTable() {
		return historyTable;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public SimpleActivity getTaskInstance()
	{
		return taskInstance;
	}
	
	//action
	public String runTask(){
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Cache-Control", "no-cache");
		
		taskInstance=new SimpleActivity();
		LightTaskInstance ins=null;
		try {
			ProcessInstanceUUID piid=new ProcessInstanceUUID(processInstanceUUID);
			List<LightActivityInstance> ais=BPMModule.getInstance().getActivityInstances(piid,Arrays.asList(ActivityState.READY,ActivityState.FAILED));
			List<LightTaskInstance> tasks=BPMModule.getInstance().getTaskInstancesOfActivityInstances(ais, true);
			for(LightTaskInstance ai :tasks){
				ins=ai;
				break;
			}
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ins==null){
			taskInstance.setActivityDefinitionUUID(null);
		}
		else{
			String url=BPMUtil.getUserXPTaskUrl(ins.getProcessDefinitionUUID().getValue(), ins.getActivityDefinitionUUID().getValue(),ins.getUUID().getValue(), null,null,true,false);
			LOGGER.log(Level.WARNING, "runTask.aciton:"+ins.getUUID());
			taskInstance.setUUID(ins.getUUID());
			taskInstance.setState(ins.getState().toString());
			taskInstance.setTaskNameOrUrl(url);
			taskInstance.setActivityName(ins.getActivityName());
			taskInstance.setActivityDefinitionUUID(ins.getActivityDefinitionUUID());  //used for iframe id,which bonita form used to auto resize frame
			//taskInstance.setProcessInstanceUUID(ins.getProcessInstanceUUID());
			//taskInstance.setRootInstanceUUID(ins.getRootInstanceUUID());
		}
		return SUCCESS;
	}
	public String listComments() throws InstanceNotFoundException, Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Cache-Control", "no-cache");
		
		ProcessInstanceUUID piid=new ProcessInstanceUUID(processInstanceUUID);
		commentsTable=new DatagridDtoModel<Comment>();
		commentsTable.setRows(BPMModule.getInstance().getCommentFeed(piid));
		return SUCCESS;
	}
	public String saveComment() throws InstanceNotFoundException, Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Cache-Control", "no-cache");
		
		ProcessInstanceUUID piid=new ProcessInstanceUUID(processInstanceUUID);
		String str = new String(message.getBytes("ISO-8859-1"),"utf-8");
		BPMModule.getInstance().addComment(piid, str,BPMModule.getUserID());
		return SUCCESS;
	}
	public String listHistory() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Cache-Control", "no-cache");
		
		ProcessInstanceUUID piid=new ProcessInstanceUUID(processInstanceUUID);
		List<LightActivityInstance> activities= BPMModule.getInstance().getActivityInstances(piid);
		historyTable=new DatagridDtoModel<SimpleActivity>();
		List<SimpleActivity> rows=new ArrayList<SimpleActivity>();
		for (LightActivityInstance lai : activities) {
			if (lai.isTask()){
				SimpleActivity sa=new SimpleActivity();
				//sa.setUUID(lai.getUUID());
				sa.setStrUUID(lai.getUUID().getValue());
				sa.setActivityLabel(lai.getActivityLabel());
				//sa.setType(getText("ActivityType."+lai.getType().toString()));
				sa.setType(lai.isTask()?"人工任务":"程序自动执行");
				sa.setReadyDate(lai.getReadyDate());
				sa.setStartedDate(lai.getStartedDate());
				sa.setEndedDate(lai.getEndedDate());
				sa.setState(getText("ActivityState."+lai.getState().toString()));
				sa.setTaskCandidates(BPMModule.getInstance().getTaskCandidates(lai.getUUID()).toString());
				//sa.setTaskCandidates(lai.getTask().getTaskCandidates().toString());
				
				sa.setTaskUser(lai.getTask().getTaskUser());
				rows.add(sa);
			}
		}
		
		historyTable.setRows(rows);
		historyTable.Sort("readyDate","desc");
		return SUCCESS;
	}
	public String taskViewForm() throws ActivityNotFoundException, Exception{
		//input processInstanceUUID,uuid(for activeinstanceuuid)
		//ProcessInstanceUUID piid=new ProcessInstanceUUID(processInstanceUUID);
		ActivityInstanceUUID aiid=new ActivityInstanceUUID(uuid);
		ActivityInstance ai=BPMModule.getInstance().getActivityInstance(aiid);
		taskInstance=new SimpleActivity();
		taskInstance.setRootProcessNameOrUrl(ai.getProcessDefinitionUUID().getValue());
		String url=BPMUtil.getUserXPTaskUrl(ai.getProcessDefinitionUUID().getValue(), ai.getActivityDefinitionUUID().getValue(),ai.getUUID().getValue(), null,null,true,true);
		taskInstance.setTaskNameOrUrl(url);
		return SUCCESS;
	}
	public String skipActivity()  {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Cache-Control", "no-cache");
		
		ActivityInstanceUUID aiid=new ActivityInstanceUUID(uuid);
		try {
			BPMModule.getInstance().skipActivity(aiid);
			message="跳过此任务，请关闭此窗口或刷新";
		} catch (ActivityNotFoundException e) {
			// TODO Auto-generated catch block
			message="没有找到此任务，请关闭此窗口或刷新";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			message=e.toString();
		}
		
		return SUCCESS;
	}
}
