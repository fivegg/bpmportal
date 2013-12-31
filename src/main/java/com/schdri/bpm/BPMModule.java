/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, see <http://www.gnu.org/licenses/>.
 */
package com.schdri.bpm;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import org.ow2.bonita.facade.exception.UserNotFoundException;
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
import org.ow2.bonita.facade.runtime.command.WebGetStartableProcessesCommand;
import org.ow2.bonita.facade.uuid.AbstractUUID;
import org.ow2.bonita.facade.uuid.DocumentUUID;
import org.ow2.bonita.search.DocumentResult;
import org.ow2.bonita.search.DocumentSearchBuilder;
import org.ow2.bonita.util.Command;
import org.ow2.bonita.util.GroovyExpression;
import org.ow2.bonita.util.SimpleCallbackHandler;
import com.schdri.bpm.bonita.diagram.Diagram;

/**
 *
 * @author mgubaidullin
 */
public class BPMModule {

    final RuntimeAPI runtimeAPI;
    final QueryRuntimeAPI queryRuntimeAPI;
    final ManagementAPI managementAPI;
    final QueryDefinitionAPI queryDefinitionAPI;
    final RepairAPI repairAPI;
    final WebAPI webAPI;
    final IdentityAPI identityAPI;
    final BAMAPI bamAPI;
    final CommandAPI commandAPI;
    private static String userID;
	private LoginContext loginContext;
    private static BPMModule _instance=null;

    
    public static String getUserID() {
		return userID;
	}

	public static void setUserID(String userID) {
		BPMModule.userID = userID;
	}
	
    public static BPMModule getInstance(){
    	if (_instance==null)
    		_instance=new BPMModule();
    	return _instance;
    }
    
    private BPMModule() {
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }

        runtimeAPI = AccessorUtil.getRuntimeAPI();
        queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        managementAPI = AccessorUtil.getManagementAPI();
        queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        repairAPI = AccessorUtil.getRepairAPI();
        webAPI = AccessorUtil.getWebAPI();
        identityAPI = AccessorUtil.getIdentityAPI();
        bamAPI = AccessorUtil.getBAMAPI();
        commandAPI = AccessorUtil.getCommandAPI();
    }

    public void initContext() throws Exception {
        //if (Constants.APP_SERVER.startsWith("GLASSFISH")) {
            //ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
            //programmaticLogin.login(currentUserUID, "".toCharArray(), "processBaseRealm", false);
        //}
    	if (userID==null || userID.isEmpty())
    		//throw new Exception("BPMModule userID is null");
    		 Logger.getLogger(BPMModule.class.getName()).log(Level.SEVERE,"BPMModule userID is null,must call BPMModule.setUserID first" );
    	
    	SimpleCallbackHandler callbackHandler = new SimpleCallbackHandler(userID, "nopassword");
        loginContext = new LoginContext("BonitaStore", callbackHandler);
        loginContext.login();
        DomainOwner.setDomain(Constants.BONITA_DOMAIN);
        UserOwner.setUser(userID);
    }

    public Set<ProcessDefinition> getProcessDefinitions() throws Exception {
        initContext();
        return queryDefinitionAPI.getProcesses();
    }

    public boolean isUserAdmin() throws Exception {
        initContext();
        return managementAPI.isUserAdmin(userID);
    }

    public Set<ProcessDefinition> getProcessDefinitions(ProcessState state) throws Exception {
        initContext();
        return queryDefinitionAPI.getProcesses(state);
    }

    public LightProcessDefinition getLightProcessDefinition(ProcessDefinitionUUID pdUUID) throws Exception {
        initContext();
        return queryDefinitionAPI.getLightProcess(pdUUID);
    }

    public Set<LightProcessDefinition> getLightProcessDefinitions() throws Exception {
        initContext();
        return queryDefinitionAPI.getLightProcesses();
    }

    public Set<LightProcessDefinition> getAllowedLightProcessDefinitions() throws Exception {
        initContext();
        User user = identityAPI.findUserByUserName(userID);
        Set<String> membershipUUIDs = new HashSet<String>();
        for (Membership membership : user.getMemberships()) {
            membershipUUIDs.add(membership.getUUID());
        }
        List<Rule> userRules = managementAPI.getApplicableRules(RuleType.PROCESS_START, null, null, null, membershipUUIDs, null);

        Set<String> processException;
        Set<ProcessDefinitionUUID> processUUIDException = new HashSet<ProcessDefinitionUUID>();
        for (Rule r : userRules) {
            processException = r.getItems();
            for (String processID : processException) {
                processUUIDException.add(new ProcessDefinitionUUID(processID));
            }
        }
        Set<LightProcessDefinition> result = new HashSet<LightProcessDefinition>();
        for (LightProcessDefinition lpd : queryDefinitionAPI.getLightProcesses(processUUIDException)) {
            if (lpd.getState().equals(ProcessState.ENABLED)) {
                result.add(lpd);
            }
        }
        return result;
    }

    public Set<LightProcessDefinition> getLightProcessDefinitions(ProcessState state) throws Exception {
        initContext();
        return queryDefinitionAPI.getLightProcesses(state);
    }

    public void disableProcessDefinitions(ProcessDefinitionUUID uuid) throws Exception {
        initContext();
        managementAPI.disable(uuid);
    }

    public void enableProcessDefinitions(ProcessDefinitionUUID uuid) throws Exception {
        initContext();
        managementAPI.enable(uuid);
    }

    public void archiveProcessDefinitions(ProcessDefinitionUUID uuid) throws Exception {
        initContext();
        managementAPI.archive(uuid);
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        return runtimeAPI.instantiateProcess(uuid, vars);
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid, Map<String, Object> vars, Collection<InitialAttachment> initialAttachments) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        return runtimeAPI.instantiateProcess(uuid, vars, initialAttachments);
    }

    public ProcessInstanceUUID startNewProcess(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        return runtimeAPI.instantiateProcess(uuid);
    }

    public void saveProcessVariables(TaskInstance task, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        for (String key : vars.keySet()) {
            setProcessInstanceVariable(task.getProcessInstanceUUID(), key, vars.get(key));
        }
    }

    public void saveProcessVariables2(ActivityInstance activity, Map<String, Object> vars) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        for (String key : vars.keySet()) {
            setProcessInstanceVariable(activity.getProcessInstanceUUID(), key, vars.get(key));
        }
    }

    public Set<DataFieldDefinition> getProcessDataFields(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessDataFields(uuid);
    }

    public Set<DataFieldDefinition> getActivityDataFields(ActivityDefinitionUUID aduuid) throws ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getActivityDataFields(aduuid);
    }

    public DataFieldDefinition getProcessDataField(ProcessDefinitionUUID uuid, String varName) throws ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessDataField(uuid, varName);
    }

    public Map<String, ActivityDefinition> getProcessInitialActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcess(uuid).getInitialActivities();
    }

    public Set<ActivityDefinition> getProcessActivities(ProcessDefinitionUUID uuid) throws ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessActivities(uuid);
    }

    public Collection<TaskInstance> getTaskList(ActivityState state) throws Exception {
        initContext();
        return queryRuntimeAPI.getTaskList(state);
    }

    public Collection<LightTaskInstance> getLightTaskList(ActivityState state) throws Exception {
        initContext();
        return queryRuntimeAPI.getLightTaskList(state);
    }
    
    public Collection<LightTaskInstance> getLightTaskList(ProcessInstanceUUID instanceUUID, ActivityState state) throws Exception {
        initContext();
        return queryRuntimeAPI.getLightTaskList(instanceUUID, state);
    }

    public Set<ProcessInstance> getUserInstances() throws Exception {
        initContext();
        return queryRuntimeAPI.getUserInstances();
    }

    public Set<LightProcessInstance> getLightUserInstances() throws Exception {
        initContext();
        return queryRuntimeAPI.getLightUserInstances();
    }
    
    public TaskInstance startTask(ActivityInstanceUUID activityInstanceUUID, boolean assignTask) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        TaskInstance ti = getTaskInstance(activityInstanceUUID);
        if (ti != null && ti.getState().equals(ActivityState.READY)) {
            runtimeAPI.startTask(activityInstanceUUID, assignTask);
            return getTaskInstance(activityInstanceUUID);
        }
        return ti;
    }

    public void finishTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.finishTask(activityInstanceUUID, b);
    }

    public void finishTask(TaskInstance task, boolean b, Map<String, Object> pVars, Map<String, Object> aVars) throws TaskNotFoundException, IllegalTaskStateException, InstanceNotFoundException, VariableNotFoundException, Exception {
        initContext();
        runtimeAPI.setProcessInstanceVariables(task.getProcessInstanceUUID(), pVars);
        runtimeAPI.setActivityInstanceVariables(task.getUUID(), aVars);
        runtimeAPI.finishTask(task.getUUID(), b);
    }

//    public void finishTask(TaskInstance task, boolean b, Map<String, Object> pVars, Map<String, Object> aVars, Map<AttachmentInstance, byte[]> attachments) throws TaskNotFoundException, IllegalTaskStateException, InstanceNotFoundException, VariableNotFoundException, Exception {
//        initContext();
//        runtimeAPI.setProcessInstanceVariables(task.getProcessInstanceUUID(), pVars);
//        runtimeAPI.setActivityInstanceVariables(task.getUUID(), aVars);
//        for (AttachmentInstance a : attachments.keySet()) {
//            System.out.println(a.getProcessInstanceUUID() + " " + a.getName() + " " + a.getFileName() + " " + attachments.get(a).length);
//        }
//        runtimeAPI.addAttachments(attachments);
//        runtimeAPI.finishTask(task.getUUID(), b);
//    }
    public void finishTask(TaskInstance task, boolean b, Map<String, Object> pVars, Map<String, Object> aVars, Map<Document, byte[]> attachments) throws TaskNotFoundException, IllegalTaskStateException, InstanceNotFoundException, VariableNotFoundException, Exception {
        initContext();
        runtimeAPI.setProcessInstanceVariables(task.getProcessInstanceUUID(), pVars);
        runtimeAPI.setActivityInstanceVariables(task.getUUID(), aVars);
        for (Document a : attachments.keySet()) {
            System.out.println(a.getName() + " " + a.getContentSize() + " " + attachments.get(a).length);
            runtimeAPI.addDocumentVersion(a.getUUID(), true, a.getContentFileName(), a.getContentMimeType(), attachments.get(a));
        }
        runtimeAPI.finishTask(task.getUUID(), b);
    }

    public void addAttachment(ProcessInstanceUUID instanceUUID, String name, String fileName, byte[] value) throws Exception {
        initContext();
        runtimeAPI.addAttachment(instanceUUID, name, fileName, value);
    }

    public byte[] getAttachmentValue(String processUUID, String name) throws Exception {
        initContext();
        System.out.println("-------------------------------------------- name = " + name);
        AttachmentInstance attachmentInstance = queryRuntimeAPI.getLastAttachment(new ProcessInstanceUUID(processUUID), name, new Date());
        return queryRuntimeAPI.getAttachmentValue(attachmentInstance);
    }

    public List<AttachmentInstance> getLastAttachments(ProcessInstanceUUID instanceUUID, String regex) throws Exception {
        initContext();
        return new ArrayList<AttachmentInstance>(queryRuntimeAPI.getLastAttachments(instanceUUID, regex));
    }

    public List<AttachmentInstance> getLastAttachments(ProcessInstanceUUID instanceUUID, Set<String> attachmentNames) throws Exception {
        initContext();
        return new ArrayList<AttachmentInstance>(queryRuntimeAPI.getLastAttachments(instanceUUID, attachmentNames));
    }

    public TaskInstance assignTask(ActivityInstanceUUID activityInstanceUUID, String user) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        TaskInstance ti = getTaskInstance(activityInstanceUUID);
        if (ti != null && ti.isTaskAssigned() && !ti.getTaskUser().equals(user)) {
            return null;
        }
        runtimeAPI.assignTask(activityInstanceUUID, user);
        return getTaskInstance(activityInstanceUUID);
    }

    public TaskInstance assignAndStartTask(ActivityInstanceUUID activityInstanceUUID, String user) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        TaskInstance ti = getTaskInstance(activityInstanceUUID);
        if (ti != null && ti.isTaskAssigned() && !ti.getTaskUser().equals(user)) {
            return null;
        }
        runtimeAPI.assignTask(activityInstanceUUID, user);
        if (ti != null && ti.getState().equals(ActivityState.READY)) {
            runtimeAPI.startTask(activityInstanceUUID, true);
        }
        return getTaskInstance(activityInstanceUUID);
    }

    public TaskInstance resumeTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.resumeTask(activityInstanceUUID, b);
        return getTaskInstance(activityInstanceUUID);
    }

    public TaskInstance suspendTask(ActivityInstanceUUID activityInstanceUUID, boolean b) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.suspendTask(activityInstanceUUID, b);
        return getTaskInstance(activityInstanceUUID);
    }

    public void setProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName, Object varValue) throws InstanceNotFoundException, VariableNotFoundException, Exception {
        initContext();
        runtimeAPI.setProcessInstanceVariable(piUUID, varName, varValue);
    }

    public void setActivityInstanceVariable(ActivityInstanceUUID aiuuid, String varName, Object varValue) throws InstanceNotFoundException, VariableNotFoundException, Exception {
        initContext();
        runtimeAPI.setActivityInstanceVariable(aiuuid, varName, varValue);
    }

    public Map<String, Object> getActivityInstanceVariables(ActivityInstanceUUID aiUUID) throws ActivityNotFoundException, Exception, ActivityNotFoundException, ActivityNotFoundException, ActivityNotFoundException, ActivityNotFoundException {
        initContext();
        return queryRuntimeAPI.getActivityInstanceVariables(aiUUID);
    }

    public Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, Exception {
        initContext();
        return queryRuntimeAPI.getProcessInstanceVariables(piUUID);
    }

    public Object getProcessInstanceVariable(ProcessInstanceUUID piUUID, String varName) throws InstanceNotFoundException, Exception {
        initContext();
        return queryRuntimeAPI.getProcessInstanceVariable(piUUID, varName);
    }

    public ActivityDefinition getProcessActivity(ProcessDefinitionUUID pdUUID, String ActivityName) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessActivity(pdUUID, ActivityName);
    }

    public ParticipantDefinition getProcessParticipant(ProcessDefinitionUUID pdUUID, String participant) throws ParticipantNotFoundException, ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessParticipant(pdUUID, participant);
    }

    public Set<ProcessDefinition> getProcesses() throws Exception {
        initContext();
        return queryDefinitionAPI.getProcesses();
    }

    public Set<ProcessDefinition> getProcesses(ProcessState ps) throws Exception {
        initContext();
        return queryDefinitionAPI.getProcesses(ps);
    }

    public ProcessDefinition getProcessDefinition(ProcessDefinition pd) throws ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcess(pd.getUUID());
    }

    public ProcessDefinition deploy(BusinessArchive bar) throws DeploymentException, ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        ProcessDefinition result = managementAPI.deploy(bar);
        return result;
    }

    public ProcessDefinition deploy(BusinessArchive bar, String emptyCategoryName) throws DeploymentException, ProcessNotFoundException, VariableNotFoundException, Exception {
        initContext();
        ProcessDefinition result = managementAPI.deploy(bar);
        // add to empty category
        if (result.getCategoryNames().isEmpty()) {
            Set<String> emptyCategory = new HashSet<String>(1);
            emptyCategory.add(emptyCategoryName);
            if (webAPI.getCategories(emptyCategory).isEmpty()) {
                webAPI.addCategory(emptyCategoryName, "", "", "");
            }
            webAPI.setProcessCategories(result.getUUID(), emptyCategory);
        }
        // create PROCESS_START rule for process
        Set<ProcessDefinitionUUID> processes = new HashSet<ProcessDefinitionUUID>(1);
        processes.add(result.getUUID());
        Rule rule = managementAPI.createRule(result.getUUID().toString(), result.getName(), "PROCESS_START Rule for ProcessDefinitionUUID" + result.getUUID().toString(), RuleType.PROCESS_START);
        managementAPI.addExceptionsToRuleByUUID(rule.getUUID(), processes);
        return result;
    }

    public Rule createRule(String name, String label, String description, RuleType type) throws Exception {
        initContext();
        return managementAPI.createRule(name, label, description, type);
    }

    public <E extends AbstractUUID> void addExceptionsToRuleByUUID(final String ruleUUID, final Set<E> exceptions) throws Exception {
        initContext();
        managementAPI.addExceptionsToRuleByUUID(ruleUUID, exceptions);
    }

    public <E extends AbstractUUID> void removeExceptionsFromRuleByUUID(final String ruleUUID, final Set<E> exceptions) throws Exception {
        initContext();
        managementAPI.removeExceptionsFromRuleByUUID(ruleUUID, exceptions);
    }

    public void deployJar(String jarName, byte[] body) throws Exception {
        initContext();
        if (managementAPI.getAvailableJars().contains(jarName)) {
            managementAPI.removeJar(jarName);
        }
        managementAPI.deployJar(jarName, body);
    }

    public void removeJar(String jarName) throws Exception {
        initContext();
        if (managementAPI.getAvailableJars().contains(jarName)) {
            managementAPI.removeJar(jarName);
        }
        managementAPI.removeJar(jarName);
    }

    public void deleteProcess(ProcessDefinition pd) throws UndeletableInstanceException, UndeletableProcessException, ProcessNotFoundException, Exception {
        initContext();
        managementAPI.deleteProcess(pd.getUUID());
        Rule rule = findRule(pd.getUUID().toString());
        managementAPI.deleteRuleByUUID(rule.getUUID());
    }

    public void deleteAllProcessInstances(ProcessDefinition pd) throws Exception {
        initContext();
        runtimeAPI.deleteAllProcessInstances(pd.getUUID());
    }

    public Set<ProcessInstance> getProcessInstances() throws Exception {
        initContext();
        return queryRuntimeAPI.getProcessInstances();
    }

    public Set<LightProcessInstance> getLightProcessInstances() throws Exception {
        initContext();
        return queryRuntimeAPI.getLightProcessInstances();
    }

    public Set<LightProcessInstance> getLightProcessInstances(ProcessDefinitionUUID pduuid) throws Exception {
        initContext();
        return queryRuntimeAPI.getLightProcessInstances(pduuid);
    }

    public Set<ProcessInstance> getProcessInstancesByUUID(ProcessDefinitionUUID piUUID) throws Exception {
        initContext();
        return queryRuntimeAPI.getProcessInstances(piUUID);
    }

    public Set<ProcessInstance> getProcessInstances(ProcessDefinitionUUID piUUID, InstanceState state) throws ProcessNotFoundException, Exception {
        initContext();
        Set<ProcessInstance> result = new HashSet<ProcessInstance>();
        Set<ProcessInstance> pis = queryRuntimeAPI.getProcessInstances(piUUID);
        for (ProcessInstance pi : pis) {
            if (pi.getInstanceState().equals(state)) {
                result.add(pi);
            }
        }
        return result;
    }

    public Set<LightProcessInstance> getProcessInstancesByStatus(InstanceState state) throws Exception {
        initContext();
        Set<LightProcessInstance> result = new HashSet<LightProcessInstance>();
        Set<LightProcessInstance> pis = getLightProcessInstances();
        for (LightProcessInstance pi : pis) {
            if (pi.getInstanceState().equals(state)) {
                result.add(pi);
            }
        }
        return result;
    }

    public Set<LightActivityInstance> getActivityInstances() throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        Set<LightActivityInstance> result = new HashSet();
        try {
            Set<LightProcessInstance> pis = queryRuntimeAPI.getLightProcessInstances();
            for (LightProcessInstance pi : pis) {
                result.addAll(queryRuntimeAPI.getLightActivityInstances(pi.getProcessInstanceUUID()));
            }
        } catch (InstanceNotFoundException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public Set<LightActivityInstance> getActivityInstances(ProcessDefinitionUUID pduuid) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        Set<LightActivityInstance> result = new HashSet();
        try {
            Set<LightProcessInstance> pis = queryRuntimeAPI.getLightProcessInstances(pduuid);
            for (LightProcessInstance pi : pis) {
                result.addAll(queryRuntimeAPI.getLightActivityInstances(pi.getProcessInstanceUUID()));
            }
        } catch (InstanceNotFoundException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public List<LightActivityInstance> getActivityInstances(ProcessInstanceUUID processInstanceUUID) throws Exception {
        initContext();
        //return queryRuntimeAPI.getActivityInstances(processInstanceUUID);
        return queryRuntimeAPI.getLightActivityInstancesFromRoot(processInstanceUUID);
    }

    public Set<LightActivityInstance> getLightActivityInstances(ProcessInstanceUUID processInstanceUUID) throws Exception {
        initContext();
        return queryRuntimeAPI.getLightActivityInstances(processInstanceUUID);
    }

    public ActivityInstance getActivityInstance(ActivityInstanceUUID activityInstanceUUID) throws ActivityNotFoundException, Exception {
        initContext();
        return queryRuntimeAPI.getActivityInstance(activityInstanceUUID);
    }
    public Set<String> getTaskCandidates(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException{
    	return queryRuntimeAPI.getTaskCandidates(activityInstanceUUID);
    }
    
    public TaskInstance getTaskInstance(ActivityInstanceUUID activityInstanceUUID) throws ProcessNotFoundException, Exception {
        initContext();
        try {
            return queryRuntimeAPI.getTask(activityInstanceUUID);
        } catch (TaskNotFoundException tex) {
            tex.printStackTrace();
            return null;
        }
    }

    public void deleteProcessInstance(ProcessInstanceUUID piUUID) throws InstanceNotFoundException, InstanceNotFoundException, InstanceNotFoundException, UndeletableInstanceException, Exception {
        initContext();
        runtimeAPI.deleteProcessInstance(piUUID);
    }

    public ProcessDefinition getProcessDefinition(ProcessDefinitionUUID pdUUID) throws ProcessNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcess(pdUUID);
    }

    public ActivityDefinition getProcessActivityDefinition(ActivityInstance ai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
    }

    public ActivityDefinition getProcessActivityDefinition(LightActivityInstance lai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessActivity(lai.getProcessDefinitionUUID(), lai.getActivityName());
    }

    public ActivityDefinition getTaskDefinition(ActivityInstance ai) throws ProcessNotFoundException, ActivityNotFoundException, Exception {
        initContext();
        return queryDefinitionAPI.getProcessActivity(ai.getProcessDefinitionUUID(), ai.getActivityName());
    }

    public void assignTask(ActivityInstanceUUID activityInstanceUUID, Set<String> users) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.assignTask(activityInstanceUUID, users);
    }

    public void assignTask(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.assignTask(activityInstanceUUID);
    }

    public void setActivityInstancePriority(ActivityInstanceUUID activityInstanceUUID, int priority) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.setActivityInstancePriority(activityInstanceUUID, priority);
    }

    public TaskInstance unassignTask(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException, IllegalTaskStateException, Exception {
        initContext();
        runtimeAPI.unassignTask(activityInstanceUUID);
        return getTaskInstance(activityInstanceUUID);
    }

    public void addProcessMetaData(ProcessDefinitionUUID processDefinitionUUID, String key, String value) throws Exception {
        initContext();
        runtimeAPI.addProcessMetaData(processDefinitionUUID, key, value);
    }

    public void deleteProcessMetaData(ProcessDefinitionUUID processDefinitionUUID, String key) throws Exception {
        initContext();
        runtimeAPI.deleteProcessMetaData(processDefinitionUUID, key);
    }

    public Map<String, String> getProcessMetaData(ProcessDefinitionUUID processDefinitionUUID) throws Exception {
        initContext();
        return queryDefinitionAPI.getProcess(processDefinitionUUID).getMetaData();
    }

    public byte[] getProcessDiagramm(LightProcessInstance pi) throws Exception {
        initContext();
        Map<String, byte[]> resource = queryDefinitionAPI.getBusinessArchive(pi.getProcessDefinitionUUID()).getResources();
        byte[] img = null;
        byte[] proc = null;
        for (String key : resource.keySet()) {
            if (key.substring(key.length() - 4, key.length()).equals("proc")) {
                proc = resource.get(key);
            } else if (key.equals(pi.getProcessDefinitionUUID().toString() + ".png")) {
                img = resource.get(key);
            }
        }
        File x = new File("AAA.png");
        FileOutputStream fos = new FileOutputStream(x);
        fos.write(img);
        fos.close();

        Diagram d = new Diagram(img, proc, queryRuntimeAPI.getLightActivityInstances(pi.getRootInstanceUUID()));
        return d.getImage();
    }

//    public XMLTaskDefinition getXMLTaskDefinition(ProcessDefinitionUUID pdUUID, String stepName) throws Exception {
//        XMLProcessDefinition process = getXMLProcessDefinition(pdUUID);
//        return process.getTasks().get(stepName);
//    }
//
//    public XMLProcessDefinition getXMLProcessDefinition(ProcessInstanceUUID processInstanceUUID) throws Exception {
//        return getXMLProcessDefinition(processInstanceUUID.getProcessDefinitionUUID());
//    }
//
//    public XMLProcessDefinition getXMLProcessDefinition(ProcessDefinitionUUID processDefinitionUUID) throws Exception {
//        initContext();
//        Map<String, byte[]> resource = queryDefinitionAPI.getBusinessArchive(processDefinitionUUID).getResources();
//        byte[] proc = null;
//        for (String key : resource.keySet()) {
//            if (key.substring(key.length() - 4, key.length()).equals("proc")) {
//                proc = resource.get(key);
//            }
//        }
//        BonitaFormParcer bfb = new BonitaFormParcer(proc);
//        return bfb.getProcess();
//    }
    public byte[] getProcessDiagramm(ProcessDefinitionUUID processDefinitionUUID) throws Exception {
        initContext();
        Map<String, byte[]> resource = queryDefinitionAPI.getBusinessArchive(processDefinitionUUID).getResources();
        byte[] img = null;
        for (String key : resource.keySet()) {
            if (key.substring(key.length() - 3, key.length()).equals("png")) {
                img = resource.get(key);
            }
        }
        return img;
    }

    public Map<String, byte[]> getBusinessArchive(ProcessDefinitionUUID processDefinitionUUID) throws Exception {
        initContext();
        return queryDefinitionAPI.getBusinessArchive(processDefinitionUUID).getResources();
    }

    public void stopExecution(ProcessInstanceUUID piUUID, String stepName) throws Exception {
        initContext();
        repairAPI.stopExecution(piUUID, stepName);
    }

    public ActivityInstanceUUID startExecution(ProcessInstanceUUID piUUID, String stepName) throws Exception {
        initContext();
        return repairAPI.startExecution(piUUID, stepName);
    }

    public ActivityInstanceUUID reStartExecution(ProcessInstanceUUID piUUID, String stepName) throws Exception {
        initContext();
        repairAPI.stopExecution(piUUID, stepName);
        return repairAPI.startExecution(piUUID, stepName);
    }

    public LightProcessDefinition setProcessCategories(ProcessDefinitionUUID pduuid, Set<String> set) throws Exception {
        initContext();
        return webAPI.setProcessCategories(pduuid, set);
    }

    public void deleteCategories(Set<String> set) throws Exception {
        initContext();
        webAPI.deleteCategories(set);
    }

    public void addCategory(String string, String string1, String string2, String string3) throws Exception {
        initContext();
        webAPI.addCategory(string, string1, string2, string3);
    }

    public Set<Category> getAllCategories() throws Exception {
        initContext();
        return webAPI.getAllCategories();
    }

    public Object evaluateGroovyExpression(String expression, ActivityInstance ai, boolean propagate) throws InstanceNotFoundException, GroovyException, Exception {
        initContext();
        return runtimeAPI.evaluateGroovyExpression(expression, ai.getUUID(), false, propagate);

    }
    public Object evaluateGroovyExpression(String expression, ActivityInstance ai,boolean useActivityScope, boolean propagate) throws InstanceNotFoundException, GroovyException, Exception {
        initContext();
        return runtimeAPI.evaluateGroovyExpression(expression, ai.getUUID(), useActivityScope, propagate);

    }
    public Object evaluateGroovyExpression(String expression, ActivityInstance ai, Map<String,Object> context, boolean useActivityScope, boolean propagate) throws InstanceNotFoundException, GroovyException, Exception {
        initContext();
        return runtimeAPI.evaluateGroovyExpression(expression, ai.getUUID(),context, useActivityScope, propagate);

    }

    public Object evaluateExpression(String expression, ActivityInstance ai, boolean propagate) throws InstanceNotFoundException, GroovyException, Exception {
        if (expression != null && GroovyExpression.isGroovyExpression(expression)) {
            initContext();
            return runtimeAPI.evaluateGroovyExpression(expression, ai.getUUID(), false, propagate);
        } else {
            return expression;
        }
    }

    public Object evaluateExpression(String expression, ProcessDefinitionUUID pduuid) throws InstanceNotFoundException, GroovyException, Exception {
        if (expression != null && GroovyExpression.isGroovyExpression(expression)) {
            initContext();
            return runtimeAPI.evaluateGroovyExpression(expression, pduuid);
        } else {
            return expression;
        }
    }

    public Object evaluateGroovyExpression(String expression, ProcessDefinitionUUID pduuid) throws InstanceNotFoundException, GroovyException, Exception {
        initContext();
        return runtimeAPI.evaluateGroovyExpression(expression, pduuid);
    }
    public Object evaluateGroovyExpression(String expression, ProcessDefinitionUUID pduuid,Map<String,Object> context) throws InstanceNotFoundException, GroovyException, Exception {
        initContext();
        return runtimeAPI.evaluateGroovyExpression(expression, pduuid,context);
    }

    public Map<String, Object> evaluateGroovyExpressions(Map<String, String> expressions,
            ActivityInstanceUUID activityUUID, Map<String, Object> context, boolean useActivityScope, boolean propagate)
            throws InstanceNotFoundException, ActivityNotFoundException, GroovyException {
        if (!expressions.isEmpty()) {
            return runtimeAPI.evaluateGroovyExpressions(expressions, activityUUID, context, useActivityScope, propagate);
        } else {
            return null;
        }
    }

    public Map<String, Object> evaluateGroovyExpressions(Map<String, String> expressions,
            ProcessDefinitionUUID processDefinitionUUID, Map<String, Object> context)
            throws InstanceNotFoundException, ProcessNotFoundException, GroovyException {
        return runtimeAPI.evaluateGroovyExpressions(expressions, processDefinitionUUID, context);
    }

    public Map<String, Object> evaluateGroovyExpressions(Map<String, String> expression, ProcessInstanceUUID processInstanceUUID,
            Map<String, Object> context, boolean useInitialVariableValues, boolean propagate)
            throws InstanceNotFoundException, GroovyException {
        return runtimeAPI.evaluateGroovyExpressions(expression, processInstanceUUID, context, useInitialVariableValues, propagate);
    }

    public void cancelProcessInstance(ProcessInstanceUUID piuuid) throws Exception {
        initContext();
        runtimeAPI.cancelProcessInstance(piuuid);
    }

    public void addComment(ProcessInstanceUUID piuuid, String message, String userId) throws InstanceNotFoundException, Exception {
        initContext();
        runtimeAPI.addComment(piuuid, message, userId);
    }

    public void addComment(ActivityInstanceUUID aiuuid, String message, String userId) throws InstanceNotFoundException, Exception {
        initContext();
        runtimeAPI.addComment(aiuuid, message, userId);
    }

    public List<Comment> getCommentFeed(ProcessInstanceUUID piuuid) throws InstanceNotFoundException, Exception {
        initContext();
        return queryRuntimeAPI.getCommentFeed(piuuid);
    }

    public ProcessInstance getProcessInstance(ProcessInstanceUUID piuuid) throws Exception {
        initContext();
        return queryRuntimeAPI.getProcessInstance(piuuid);
    }

    public List<User> getAllUsers() throws Exception {
        initContext();
        return identityAPI.getAllUsers();
    }
    
    public List<User> getAllUsersInGroup(String groupUUID) throws Exception {
        initContext();
        return identityAPI.getAllUsersInGroup(groupUUID);
    }
    public List<Role> getAllRoles() throws Exception {
        initContext();
        return identityAPI.getAllRoles();
    }

    public List<Group> getAllGroups() throws Exception {
        initContext();
        return identityAPI.getAllGroups();
    }
    
    public List<Group> getChildrenGroupsByUUID(String groupUUID) throws Exception  {
        initContext();
        return identityAPI.getChildrenGroupsByUUID(groupUUID);
    }
    
    public List<ProfileMetadata> getAllProfileMetadata() throws Exception {
        initContext();
        return identityAPI.getAllProfileMetadata();
    }

    public User addUser(String username, String password, String firstName, String lastName, String title, String jobTitle, String managerUserUUID, Map<String, String> profileMetadata) throws Exception {
        initContext();
        return identityAPI.addUser(username, password, firstName, lastName, title, jobTitle, managerUserUUID, profileMetadata);
    }

    public void removeUserByUUID(String userUUID) throws Exception {
        initContext();
        identityAPI.removeUserByUUID(userUUID);
    }

    public ProfileMetadata addProfileMetadata(String name, String label) throws Exception {
        initContext();
        return identityAPI.addProfileMetadata(name, label);
    }

    public ProfileMetadata addProfileMetadata(String name) throws Exception {
        initContext();
        return identityAPI.addProfileMetadata(name);
    }

    public void removeProfileMetadataByUUID(String profileMetadataUUID) throws Exception {
        initContext();
        identityAPI.removeProfileMetadataByUUID(profileMetadataUUID);
    }

    public Role addRole(String name, String label, String description) throws Exception {
        initContext();
        return identityAPI.addRole(name, label, description);
    }

    public void removeRoleByUUID(String roleUUID) throws Exception {
        initContext();
        identityAPI.removeRoleByUUID(roleUUID);
    }

    public Role updateRoleByUUID(String roleUUID, String name, String label, String description) throws Exception {
        initContext();
        return identityAPI.updateRoleByUUID(roleUUID, name, label, description);
    }

    public ProfileMetadata updateProfileMetadataByUUID(String profileMetadataUUID, String name, String label) throws Exception {
        initContext();
        return identityAPI.updateProfileMetadataByUUID(profileMetadataUUID, name, label);
    }

    public Group addGroup(String name, String label, String description, String parentGroupUUID) throws Exception {
        initContext();
        return identityAPI.addGroup(name, label, description, parentGroupUUID);
    }

    public Group addGroup(String name, String parentGroupUUID) throws Exception {
        initContext();
        return identityAPI.addGroup(name, parentGroupUUID);
    }

    public Group updateGroupByUUID(String groupUUID, String name, String label, String description, String parentGroupUUID) throws Exception {
        initContext();
        return identityAPI.updateGroupByUUID(groupUUID, name, label, description, parentGroupUUID);
    }

    public void removeGroupByUUID(String groupUUID) throws Exception {
        initContext();
        identityAPI.removeGroupByUUID(groupUUID);
    }

    public void updateUserProfessionalContactInfo(String userUUID, String email, String phoneNumber, String mobileNumber, String faxNumber, String building, String room, String address, String zipCode, String city, String state, String country, String website) throws Exception {
        initContext();
        identityAPI.updateUserProfessionalContactInfo(userUUID, email, phoneNumber, mobileNumber, faxNumber, building, room, address, zipCode, city, state, country, website);
    }

    public void updateUserPersonalContactInfo(String userUUID, String email, String phoneNumber, String mobileNumber, String faxNumber, String building, String room, String address, String zipCode, String city, String state, String country, String website) throws Exception {
        initContext();
        identityAPI.updateUserPersonalContactInfo(userUUID, email, phoneNumber, mobileNumber, faxNumber, building, room, address, zipCode, city, state, country, website);
    }

    public User updateUserByUUID(String userUUID, String username, String firstName, String lastName, String title, String jobTitle, String managerUserUUID, Map<String, String> profileMetadata) throws Exception {
        initContext();
        return identityAPI.updateUserByUUID(userUUID, username, firstName, lastName, title, jobTitle, managerUserUUID, profileMetadata);
    }

    public User updateUserPassword(String userUUID, String password) throws Exception {
        initContext();
        return identityAPI.updateUserPassword(userUUID, password);
    }

    public void setUserMemberships(String userUUID, Collection<String> membershipUUIDs) throws Exception {
        initContext();
        identityAPI.setUserMemberships(userUUID, membershipUUIDs);
    }

    public void addMembershipToUser(String userUUID, String membershipUUID) throws Exception {
        initContext();
        identityAPI.addMembershipToUser(userUUID, membershipUUID);
    }

    public void addMembershipsToUser(String userUUID, Collection<String> membershipUUIDs) throws Exception {
        initContext();
        identityAPI.addMembershipsToUser(userUUID, membershipUUIDs);
    }

    public Membership getMembershipByUUID(String membershipUUID) throws Exception {
        initContext();
        return identityAPI.getMembershipByUUID(membershipUUID);
    }

    public void removeMembershipFromUser(String userUUID, String membershipUUID) throws Exception {
        initContext();
        identityAPI.removeMembershipFromUser(userUUID, membershipUUID);
    }

    public void removeMembershipsFromUser(String userUUID, Collection<String> membershipUUIDs) throws Exception {
        initContext();
        identityAPI.removeMembershipsFromUser(userUUID, membershipUUIDs);
    }

    public Membership getMembershipForRoleAndGroup(String roleUUID, String groupUUID) throws Exception {
        initContext();
        return identityAPI.getMembershipForRoleAndGroup(roleUUID, groupUUID);
    }

    public User findUserByUserName(String userName) throws Exception {
        initContext();
        return identityAPI.findUserByUserName(userName);
    }

    public boolean checkUserCredentials(String username, String password) throws Exception {
        initContext();
        return managementAPI.checkUserCredentials(username, password);
    }

    public Rule findRule(String ruleName) throws Exception {
        initContext();
        for (Rule rule : managementAPI.getAllRules()) {
            if (rule.getName().equals(ruleName)) {
                return rule;
            }
        }
        return null;
    }

    public void applyRuleToEntities(final String ruleUUID, final Collection<String> userUUIDs, final Collection<String> roleUUIDs, final Collection<String> groupUUIDs, final Collection<String> membershipUUIDs, final Collection<String> entityIDs) throws Exception {
        initContext();
        managementAPI.applyRuleToEntities(ruleUUID, userUUIDs, roleUUIDs, groupUUIDs, membershipUUIDs, entityIDs);
    }

    public void removeRuleFromEntities(final String ruleUUID, final Collection<String> userUUIDs, final Collection<String> roleUUIDs, final Collection<String> groupUUIDs, final Collection<String> membershipUUIDs, final Collection<String> entityIDs) throws Exception {
        initContext();
        managementAPI.removeRuleFromEntities(ruleUUID, userUUIDs, roleUUIDs, groupUUIDs, membershipUUIDs, entityIDs);
    }

    public void addMetaData(String key, String value) throws Exception {
        initContext();
        managementAPI.addMetaData(key, value);
    }

    public String getMetaData(String key) throws Exception {
        initContext();
        return managementAPI.getMetaData(key);
    }

    public String getUserMetadata(String metadataName) throws Exception {
        initContext();
        User user = identityAPI.findUserByUserName(userID);
        for (ProfileMetadata profileMetadata : user.getMetadata().keySet()) {
            if (profileMetadata.getName().equals(metadataName)) {
                return user.getMetadata().get(profileMetadata);
            }
        }
        return null;
    }

    public <T extends Object> T execute(Command<T> cmnd) throws Exception {
        initContext();
        return commandAPI.execute(cmnd);
    }

    public Set<AttachmentDefinition> getAttachmentDefinitions(ProcessDefinitionUUID pduuid) throws Exception {
        initContext();
        return queryDefinitionAPI.getAttachmentDefinitions(pduuid);
    }

    public Set<InitialAttachment> getProcessAttachments(ProcessDefinitionUUID pduuid) throws Exception {
        initContext();
        return queryDefinitionAPI.getProcessAttachments(pduuid);
    }

    public DocumentResult searchDocuments(final DocumentSearchBuilder builder, final int fromResult, final int maxResults) throws Exception {
        initContext();
        return queryRuntimeAPI.searchDocuments(builder, fromResult, maxResults);
    }

    public byte[] getDocumentContent(DocumentUUID duuid) throws Exception {
        initContext();
        return queryRuntimeAPI.getDocumentContent(duuid);
    }
    
    //added by robin
    public Map<String,Long> getProcInstCategoryCount(){
    	return null;
    }
    
    //added by robin
    //involved,Gets LightProcessInstances that are not sub-process instances and having the given userId member of the involved user order by pagingCriterion.
    public List<LightProcessInstance> 	getLightParentProcessInstancesWithInvolvedUser(int fromIndex, int pageSize) throws Exception{
    	initContext();
    	return queryRuntimeAPI.getLightParentProcessInstancesWithInvolvedUser(this.userID, fromIndex, pageSize);
    }
    // started by,Returns at most pageSize instances started by the logged user
    public  List<LightProcessInstance> getLightParentUserInstances(int fromIndex, int pageSize)  throws Exception {
        initContext();
        return queryRuntimeAPI.getLightParentUserInstances(fromIndex, pageSize);
    }
    //inbox
    public List<LightProcessInstance> getLightParentProcessInstancesWithActiveUser(int fromIndex, int pageSize) throws Exception{
    	 initContext();
         return queryRuntimeAPI.getLightParentProcessInstancesWithActiveUser(getUserID(), fromIndex, pageSize);
    }
    //get process instance's activity instance
    public Map<ProcessInstanceUUID, List<LightActivityInstance>> getLightActivityInstanceOfProcessInstances( Set<ProcessInstanceUUID> instanceUUIDs,boolean considerSystemTaks ) throws Exception {
        final APIAccessor accessor = new StandardAPIAccessorImpl();
        final QueryRuntimeAPI journalQueryRuntimeAPI = accessor.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        final QueryRuntimeAPI allQueryRuntimeAPI = accessor.getQueryRuntimeAPI();
        
        Map<ProcessInstanceUUID, List<LightActivityInstance>> activities;
        Map<ProcessInstanceUUID, List<LightActivityInstance>> activitesFailed;
        
        if(considerSystemTaks){
            final Map<ProcessInstanceUUID, LightActivityInstance> lastUpdatedActivities = allQueryRuntimeAPI.getLightLastUpdatedActivityInstanceFromRoot(instanceUUIDs, considerSystemTaks);
            activities = new HashMap<ProcessInstanceUUID, List<LightActivityInstance>>();
            for (Map.Entry<ProcessInstanceUUID, LightActivityInstance> entry : lastUpdatedActivities.entrySet()) {
                activities.put(entry.getKey(), Arrays.asList(entry.getValue()));
              }
        } else {
            activities = journalQueryRuntimeAPI.getLightActivityInstancesFromRoot(instanceUUIDs, ActivityState.READY);
            activitesFailed = journalQueryRuntimeAPI.getLightActivityInstancesFromRoot(instanceUUIDs, ActivityState.FAILED);
            activities.putAll(activitesFailed);
            final Set<ProcessInstanceUUID> instancesWithoutReadyTask = new HashSet<ProcessInstanceUUID>();
            
            //因为这段代码会加入其它activity，例如failed，我已改动了上面，下面这个就不用了。另外getLightLastUpdatedActivityInstanceFromRoot也不明白什么意思
//            for (ProcessInstanceUUID instanceUUID : instanceUUIDs) {
//                if (activities.get(instanceUUID) == null || activities.get(instanceUUID).isEmpty()) {
//                  instancesWithoutReadyTask.add(instanceUUID);
//                }
//              }
//              
//              if (!instancesWithoutReadyTask.isEmpty()) {
//                
//                final Map<ProcessInstanceUUID, LightActivityInstance> lastUpdatedActivities = allQueryRuntimeAPI.getLightLastUpdatedActivityInstanceFromRoot(instancesWithoutReadyTask, considerSystemTaks);
//                
//                for (Map.Entry<ProcessInstanceUUID, LightActivityInstance> entry : lastUpdatedActivities.entrySet()) {
//                  final List<LightActivityInstance> list = new ArrayList<LightActivityInstance>();
//                  list.add(entry.getValue());
//                  activities.put(entry.getKey(), list);
//                }
//              }
        }
        return activities;
      }
    public List<LightTaskInstance> getTaskInstancesOfActivityInstances(List<LightActivityInstance> activities,boolean onlyMy) throws TaskNotFoundException{
    	List<LightTaskInstance> tasks=new ArrayList<LightTaskInstance>();
    	for(LightActivityInstance lai:activities){
    		if (lai.isTask()){
    			if (!onlyMy)
    				tasks.add(lai.getTask());
    			else if (isMyTask(lai.getTask()))
    				tasks.add(lai.getTask());
    		}
    	}
    	return tasks;
    }
    //get Chinese name by process label
    public String getProcessLabel(LightProcessInstance pi) throws ProcessNotFoundException, Exception {
    	return this.getProcessDefinition(pi.getProcessDefinitionUUID()).getLabel();
    }
    
    //get tasks of given state,返回给定的process instance 的给定状态的活动,返回的活动不一定属于当前用户的，可以为任何人的
    public List<LightActivityInstance> getActivityInstances(ProcessInstanceUUID piUUID,List<ActivityState> stateSet) throws Exception{
    	List<LightActivityInstance>  temp=getActivityInstances(piUUID);
    	List<LightActivityInstance>   res=new ArrayList<LightActivityInstance>();
    	for(LightActivityInstance ai : temp){
    		for(ActivityState s: stateSet){
    			if (ai.getState().equals(s)){
    				res.add(ai);
    				break;
    			}
    		}
    	}
    	return res;
    }
    //判断是否是当前用户的任务
    public boolean isMyTask(LightTaskInstance task) throws TaskNotFoundException{
    	if (task.isTaskAssigned())
    		if (task.getTaskUser().equals(getUserID()))
    			return true;
    	if (this.getTaskCandidates(task.getUUID()).contains(getUserID()))
    		return true;
    	return false;
    }
    //返回bpmportal定义的流程类型：格式为"数字.类型名称",例如1.项目管理 2.质量控制。目前只分两类
    public String[] getProcessBpmPortalCategroy(LightProcessDefinition lpd){
    	String[] tempList={"未分类",""};
    	
    	Set<String> categoryList=lpd.getCategoryNames();
    	if (categoryList.size()==0){
    		
    	}
    	else if (categoryList.size()==1){
    		for(String cat:categoryList){
    			tempList[0]=cat;
    		}
    	}
    	else{
    		for(String cat:categoryList){
    			String[] temp=cat.split("\\.");  //  注：.是正则表达式特殊符号，必须转义
    			try{
    				int index=Integer.parseInt(temp[0]);
    				tempList[index-1]=temp[1];
    			}
    			catch(NumberFormatException ex){
    				continue;
    			}
    		}
    	}
    	return tempList;
    }

	public void skipActivity(ActivityInstanceUUID aiid) throws ActivityNotFoundException, IllegalTaskStateException {
		runtimeAPI.skip(aiid, null);
	}
	
	public Set<LightProcessDefinition>  getStartableProcesses() throws Exception{
		Set<LightProcessDefinition> result=null;
		
		initContext();
		final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();
		
		User user=null;
		try{
			user=identityAPI.findUserByUserName(getUserID());
		}
		catch(UserNotFoundException ex){
			user=null;
		}
        final Set<String> theUserRoles;
        final Set<String> theUserGroups;
        final Set<String> theUserMemberships;
        final String theUserID;
        if (user != null) {
            theUserID = user.getUUID();
            theUserGroups = new HashSet<String>();
            theUserRoles = new HashSet<String>();
            theUserMemberships = new HashSet<String>();
            
            for (Membership theMembership : user.getMemberships()) {
                theUserMemberships.add(theMembership.getUUID());
                theUserGroups.add(theMembership.getGroup().getUUID());
                theUserRoles.add(theMembership.getRole().getUUID());
            }
            result=theCommandAPI.execute(new WebGetStartableProcessesCommand(theUserID,theUserRoles,theUserGroups,theUserMemberships,getUserID()));
        }
        else
        	result=theCommandAPI.execute(new WebGetStartableProcessesCommand(null,null,null,null,getUserID()));
        return result;
	}
}
