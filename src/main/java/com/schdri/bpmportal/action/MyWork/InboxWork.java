package com.schdri.bpmportal.action.MyWork;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.light.LightTaskInstance;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.schdri.bpm.BPMModule;
import com.schdri.bpmportal.dto.SimpleActivity;
import com.schdri.bpmportal.model.DatagridDtoModel;

@SuppressWarnings("serial")
public class InboxWork extends ActionSupport implements ModelDriven<DatagridDtoModel<SimpleActivity>> {
	private DatagridDtoModel<SimpleActivity> model=new DatagridDtoModel<SimpleActivity>();
	
	public String listInbox() throws Exception{
		//读取请求参数
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Cache-Control", "no-cache");
		
		model.loadDatagridParams(request);
		List<LightProcessInstance> result;
		Map<ProcessInstanceUUID, List<LightActivityInstance>>  activities;
		
	    result = BPMModule.getInstance().getLightParentProcessInstancesWithActiveUser(model.fromIndex(), model.getRownumber());
	    final Set<ProcessInstanceUUID> allTheProcessInstanceUUIDs = new HashSet<ProcessInstanceUUID>();
        for (LightProcessInstance processInstance : result) 
            allTheProcessInstanceUUIDs.add(processInstance.getUUID());
                
                
        //copy from bonita source
	    activities=BPMModule.getInstance().getLightActivityInstanceOfProcessInstances(allTheProcessInstanceUUIDs, false);
//		Collection<LightTaskInstance> tasks = BPMModule.getInstance().getLightTaskList(ActivityState.READY);
//        tasks.addAll(BPMModule.getInstance().getLightTaskList(ActivityState.EXECUTING));
//        tasks.addAll(BPMModule.getInstance().getLightTaskList(ActivityState.SUSPENDED));
        
		List<SimpleActivity> rows=new ArrayList<SimpleActivity>();
		for(Entry<ProcessInstanceUUID,List<LightActivityInstance>> activityEntry : activities.entrySet()){
			List<LightActivityInstance> llai=activityEntry.getValue();
			if (llai==null)
				continue;
			List<LightTaskInstance> tasks=BPMModule.getInstance().getTaskInstancesOfActivityInstances(llai,true);
			if (tasks.size()==0)
				continue;
			
			SimpleActivity t=new SimpleActivity();
			ProcessInstance pi=BPMModule.getInstance().getProcessInstance(activityEntry.getKey());
			LightProcessDefinition lpd = BPMModule.getInstance().getLightProcessDefinition(pi.getProcessDefinitionUUID());
			String[] cates=BPMModule.getInstance().getProcessBpmPortalCategroy(lpd);
			t.setCategory1(cates[0]);
			t.setCategory2(cates[1]);
			t.setRootProcessNameOrUrl(lpd.getLabel() + "  #" + pi.getUUID().toString().substring(pi.getUUID().toString().lastIndexOf("--") + 2));
			t.setLastUpdateDate(pi.getLastUpdate());
			String taskLink;
			String taskTitle;
			String taskDescription;
			
			if (tasks.size()>1){
				taskTitle="有"+tasks.size()+"个任务";
				taskDescription="";
			}
			else
			{
				LightTaskInstance task=tasks.get(0);
				taskTitle = task.getDynamicLabel() != null ? task.getDynamicLabel() : task.getActivityLabel();
				taskDescription = task.getDynamicDescription() != null ? (" - " + task.getDynamicDescription()) : "";
				if (task.isTaskAssigned())
					t.setTaskCandidates(task.getTaskUser());
				else
					t.setTaskCandidates(BPMModule.getInstance().getTaskCandidates(task.getUUID()).toString());
				int priority=task.getPriority();
				if (priority==0)
					t.setPriority("普通");
				else if (priority==1)
					t.setPriority("加快处理");
				else
					t.setPriority("必须马上处理");
			}
			taskLink="<a href=\"#\" onclick=\"mywork_openTab('"+getText("mywork.inbox")+ "-"+t.getRootProcessNameOrUrl()
	        		+"','"+ request.getContextPath()+"/mywork/taskiframe.jsp?returnTab="+getText("mywork.inbox")+"&processInstanceUUID="+ pi.getUUID().toString() +"')\"><b>" + taskTitle + "</b><i>" + taskDescription + "</i></a>";
				

			t.setTaskNameOrUrl(taskLink);
			rows.add(t);
		}
		//model.setTotal(tasks.size());
		model.setRows(rows);

		//排序
		if (model.getSort()!=null&& !model.getSort().isEmpty())
			model.Sort();
		else
			model.Sort("lastUpdateDate","desc");
		
		return SUCCESS;
	}
	public void validateListInbox(){
	
	}
	public DatagridDtoModel<SimpleActivity> getModel() {
		// TODO Auto-generated method stub
		return model;
	}
}
