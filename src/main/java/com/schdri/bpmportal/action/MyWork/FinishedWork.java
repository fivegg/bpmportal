package com.schdri.bpmportal.action.MyWork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.schdri.bpm.BPMModule;
import com.schdri.bpmportal.dto.SimpleActivity;
import com.schdri.bpmportal.model.DatagridDtoModel;


@SuppressWarnings("serial")
public class FinishedWork extends ActionSupport implements ModelDriven<DatagridDtoModel<SimpleActivity>> {
	private DatagridDtoModel<SimpleActivity> model=new DatagridDtoModel<SimpleActivity>();
	
	public String listFinished() throws Exception{
		//读取请求参数
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Cache-Control", "no-cache");
		
		model.loadDatagridParams(request);
		
		Collection<LightTaskInstance> tasks = BPMModule.getInstance().getLightTaskList(ActivityState.FINISHED);
                
		List<SimpleActivity> rows=new ArrayList<SimpleActivity>();
		for(LightTaskInstance task : tasks){
			SimpleActivity t=new SimpleActivity();
			t.setUUID(task.getUUID());
			ProcessInstance pi=BPMModule.getInstance().getProcessInstance(task.getRootInstanceUUID());
			LightProcessDefinition lpd = BPMModule.getInstance().getLightProcessDefinition(pi.getProcessDefinitionUUID());
	        String procTitle=lpd.getLabel() + "  #" + task.getRootInstanceUUID().toString().substring(task.getRootInstanceUUID().toString().lastIndexOf("--") + 2);
			String procLink="<a href=\"#\" onclick=\"mywork_openTab('"+getText("mywork.finished") + "-"+ procTitle
	        		+"','"+  request.getContextPath()+"/mywork/taskiframe.jsp?returnTab="+getText("mywork.finished")+"&processInstanceUUID="+pi.getUUID().getValue()+"')\">" + procTitle + "</a>";
			
			String[] cates=BPMModule.getInstance().getProcessBpmPortalCategroy(lpd);
			t.setCategory1(cates[0]);
			t.setCategory2(cates[1]);
			
			t.setRootProcessNameOrUrl(procLink);
			
			String taskTitle = task.getDynamicLabel() != null ? task.getDynamicLabel() : task.getActivityLabel();
	        String taskDescription = task.getDynamicDescription() != null ? (" - " + task.getDynamicDescription()) : "";
	        
			t.setTaskNameOrUrl("<b>"+taskTitle+ "</b><i>" + taskDescription + "</i>");
			t.setLastUpdateDate(task.getLastUpdateDate());
			rows.add(t);
		}
		//排序
		model.setRows(rows);
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