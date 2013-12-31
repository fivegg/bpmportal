package com.schdri.bpm;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;

import org.ow2.bonita.facade.BAMAPI;
import org.ow2.bonita.facade.CommandAPI;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.exception.UndeletableProcessException;
import org.ow2.bonita.facade.APIAccessor;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RepairAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.WebAPI;
import org.ow2.bonita.facade.def.element.AttachmentDefinition;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ParticipantDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.DeploymentException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ParticipantNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.facade.exception.UndeletableInstanceException;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.ProfileMetadata;
import org.ow2.bonita.facade.identity.Role;
import org.ow2.bonita.facade.identity.User;
import org.ow2.bonita.facade.impl.StandardAPIAccessorImpl;
import org.ow2.bonita.facade.privilege.Rule;
import org.ow2.bonita.facade.privilege.Rule.RuleType;
import org.ow2.bonita.facade.runtime.Category;
import org.ow2.bonita.facade.runtime.Comment;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.uuid.ActivityDefinitionUUID;
import org.ow2.bonita.identity.auth.DomainOwner;
import org.ow2.bonita.identity.auth.UserOwner;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.light.LightTaskInstance;
import org.ow2.bonita.util.GroovyException;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.Document;
import org.ow2.bonita.facade.runtime.InitialAttachment;
import org.ow2.bonita.facade.uuid.AbstractUUID;
import org.ow2.bonita.facade.uuid.DocumentUUID;
import org.ow2.bonita.search.DocumentResult;
import org.ow2.bonita.search.DocumentSearchBuilder;
import org.ow2.bonita.util.Command;
import org.ow2.bonita.util.GroovyExpression;
import org.ow2.bonita.util.SimpleCallbackHandler;

import com.schdri.bpm.bonita.diagram.Diagram;


public class BPMService {
    private String currentUserUID;
    private LoginContext loginContext;
    public BPMService(String currentUserUID ){
    	this.currentUserUID=currentUserUID;
    }
    private void initContext() throws Exception {
        //if (Constants.APP_SERVER.startsWith("GLASSFISH")) {
            //ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
            //programmaticLogin.login(currentUserUID, "".toCharArray(), "processBaseRealm", false);
        //}
    	SimpleCallbackHandler callbackHandler = new SimpleCallbackHandler(currentUserUID, "nopassword");
        loginContext = new LoginContext("BonitaStore", callbackHandler);
        loginContext.login();
        DomainOwner.setDomain(Constants.BONITA_DOMAIN);
        UserOwner.setUser(currentUserUID);
    }
    public Map<ProcessInstanceUUID, List<LightActivityInstance>> getInboxTask( int fromIndex, int pageSize ) throws Exception{
        final APIAccessor accessor = new StandardAPIAccessorImpl();
        final QueryRuntimeAPI journalQueryRuntimeAPI = accessor.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        final QueryRuntimeAPI allQueryRuntimeAPI = accessor.getQueryRuntimeAPI();
     
    	initContext();
    	List<LightProcessInstance> instanceUUIDs=journalQueryRuntimeAPI.getLightParentProcessInstancesWithActiveUser(currentUserUID, fromIndex, pageSize);
        final Set<ProcessInstanceUUID> allTheProcessInstanceUUIDs = new HashSet<ProcessInstanceUUID>();
        for (LightProcessInstance processInstance : instanceUUIDs) {
            allTheProcessInstanceUUIDs.add(processInstance.getUUID());
        }
        Map<ProcessInstanceUUID, List<LightActivityInstance>> activities=journalQueryRuntimeAPI.getLightActivityInstancesFromRoot(allTheProcessInstanceUUIDs, ActivityState.READY);
        return activities;
    }
    public Map<ProcessInstanceUUID, List<LightActivityInstance>> getStartedProcess( int fromIndex, int pageSize ) throws Exception{
        final APIAccessor accessor = new StandardAPIAccessorImpl();
        final QueryRuntimeAPI allQueryRuntimeAPI = accessor.getQueryRuntimeAPI();
     
    	initContext();                                                  
    	List<LightProcessInstance> instanceUUIDs=allQueryRuntimeAPI.getLightParentUserInstances(fromIndex, pageSize);
        final Set<ProcessInstanceUUID> allTheProcessInstanceUUIDs = new HashSet<ProcessInstanceUUID>();
        for (LightProcessInstance processInstance : instanceUUIDs) {
            allTheProcessInstanceUUIDs.add(processInstance.getUUID());
        }
        Map<ProcessInstanceUUID, List<LightActivityInstance>> activities=allQueryRuntimeAPI.getLightActivityInstancesFromRoot(allTheProcessInstanceUUIDs, ActivityState.READY);
        return activities;
    }
    public Map<ProcessInstanceUUID, List<LightActivityInstance>> getInvolvedProcess( int fromIndex, int pageSize ) throws Exception{
        final APIAccessor accessor = new StandardAPIAccessorImpl();
        final QueryRuntimeAPI allQueryRuntimeAPI = accessor.getQueryRuntimeAPI();
     
    	initContext();                                                  
    	List<LightProcessInstance> instanceUUIDs=allQueryRuntimeAPI.getLightParentProcessInstancesWithInvolvedUser(currentUserUID, fromIndex, pageSize);
        final Set<ProcessInstanceUUID> allTheProcessInstanceUUIDs = new HashSet<ProcessInstanceUUID>();
        for (LightProcessInstance processInstance : instanceUUIDs) {
            allTheProcessInstanceUUIDs.add(processInstance.getUUID());
        }
        Map<ProcessInstanceUUID, List<LightActivityInstance>> activities=allQueryRuntimeAPI.getLightActivityInstancesFromRoot(allTheProcessInstanceUUIDs, ActivityState.READY);
        return activities;
    }
    public Map<ProcessInstanceUUID, List<LightActivityInstance>> getFinishedTask( int fromIndex, int pageSize ) throws Exception{
        final APIAccessor accessor = new StandardAPIAccessorImpl();
        final QueryRuntimeAPI allQueryRuntimeAPI = accessor.getQueryRuntimeAPI();
     
    	initContext();                                                  
    	List<LightProcessInstance> instanceUUIDs=allQueryRuntimeAPI.getLightParentProcessInstancesWithInvolvedUser(currentUserUID, fromIndex, pageSize);
        final Set<ProcessInstanceUUID> allTheProcessInstanceUUIDs = new HashSet<ProcessInstanceUUID>();
        for (LightProcessInstance processInstance : instanceUUIDs) {
            allTheProcessInstanceUUIDs.add(processInstance.getUUID());
        }
        Map<ProcessInstanceUUID, List<LightActivityInstance>> activities=allQueryRuntimeAPI.getLightActivityInstancesFromRoot(allTheProcessInstanceUUIDs, ActivityState.FINISHED);
        return activities;
    }
}
