package com.schdri.bpmportal.action.MyWork;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightProcessInstance;


import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.schdri.bpm.BPMModule;
import com.schdri.bpmportal.dto.SimpleProcess;
import com.schdri.bpmportal.model.DatagridDtoModel;
import com.schdri.util.Tools;

@SuppressWarnings("serial")
public class InvolvedWork extends ActionSupport implements ModelDriven<DatagridDtoModel<SimpleProcess>> {
	private DatagridDtoModel<SimpleProcess> model=new DatagridDtoModel<SimpleProcess>();
	private String type;
	public String listInvolved() throws Exception{
		//读取请求参数
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Cache-Control", "no-cache");
		try{
			type=request.getParameter("type");
		}
		catch(Exception ex){
			type="involved";
		}
		model.loadDatagridParams(request);
		
		String tabLabel;
        List<LightProcessInstance> processInstances;
        if (type.equals("start")){
        	processInstances= BPMModule.getInstance().getLightParentUserInstances(model.fromIndex(),model.getRownumber());
        	tabLabel=getText("mywork.startedbyme");
        }
        else{
        	processInstances= BPMModule.getInstance().getLightParentProcessInstancesWithInvolvedUser(model.fromIndex(),model.getRownumber());
        	tabLabel=getText("mywork.involved");
        }
                
		List<SimpleProcess> rows=new ArrayList<SimpleProcess>();
		for(LightProcessInstance pi : processInstances){
			SimpleProcess t=new SimpleProcess();
			t.setProcessUUID(pi.getUUID().toString());

			LightProcessDefinition lpd = BPMModule.getInstance().getLightProcessDefinition(pi.getProcessDefinitionUUID());
			String[] cates=BPMModule.getInstance().getProcessBpmPortalCategroy(lpd);
			t.setCategory1(cates[0]);
			t.setCategory2(cates[1]);
			
            String pdUUID = pi.getProcessDefinitionUUID().toString();
            String pdLabel=BPMModule.getInstance().getProcessLabel(pi);
            String piName=pdLabel + "  #" + pi.getNb();
            
	        String piLink="<a href=\"#\" onclick=\"mywork_openTab('"+ tabLabel +"-"+ piName
	        		+"','"+ request.getContextPath()+"/mywork/taskiframe.jsp?returnTab="+ tabLabel+"&processInstanceUUID="+t.getProcessUUID().toString()+"')\">" + piName + "</a>";

            t.setProcess(piLink);
            t.setVersion(pdUUID.split("--")[1]);
            t.setLastupdate(Tools.DateToString(pi.getLastUpdate()));
            t.setState(getText("InstanceState."+pi.getInstanceState().toString()));
			rows.add(t);
		}
		//排序
		model.setRows(rows);
		if (model.getSort()!=null&& !model.getSort().isEmpty())
			model.Sort();
		else
			model.Sort("lastupdate","desc");
		
		return SUCCESS;
	}
	public void validateListInbox(){
	
	}
	public DatagridDtoModel<SimpleProcess> getModel() {
		// TODO Auto-generated method stub
		return model;
	}
}