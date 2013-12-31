package com.schdri.bpm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.ow2.bonita.env.EnvConstants;
import org.ow2.bonita.env.GlobalEnvironmentFactory;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.User;
import org.ow2.bonita.facade.identity.impl.GroupImpl;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.identity.auth.DomainOwner;
import org.ow2.bonita.light.LightTaskInstance;

public class BPMUtil {
	private static SessionFactoryImplementor getSessionFactory(
			final String domain, final String sessionFactoryName)
			throws Exception {
		final SessionFactory sessionFactory = (SessionFactory) GlobalEnvironmentFactory
				.getEnvironmentFactory(domain).get(sessionFactoryName);
		if (sessionFactory instanceof SessionFactoryImplementor) {
			return (SessionFactoryImplementor) sessionFactory;
		}
		return null;
	}

	// return bonita hibernate sessionfactory
	public static SessionFactory getSessionFactory(boolean bJournal) throws Exception {
		String domain = DomainOwner.getDomain();
		String configurationName = bJournal? EnvConstants.HB_CONFIG_CORE : // 获取bonita_journal库的连接
											EnvConstants.HB_CONFIG_HISTORY;//获取bonita_history库的连接
		final SessionFactory sessionFactory = getSessionFactory(domain,
				configurationName.replaceAll("-configuration",
						"-session-factory"));
		return sessionFactory;
	}
	
	public static Session getSession(boolean bJournal) throws Exception{
		SessionFactory sf=getSessionFactory(bJournal);
		return sf.openSession();
	}
	public static void initContext() throws Exception{
		BPMModule.getInstance().initContext();
	}
	public static List<Group> getAllRootGroup(Session s) throws Exception{
		initContext();
		if (s==null)
			s=getSession(true);
		List<Group> result=new ArrayList<Group>();
		Query q=s.createQuery("SELECT group_    FROM org.ow2.bonita.facade.identity.impl.GroupImpl as group_    WHERE group_.parentGroup=null      ORDER BY group_.name");
		Iterator<GroupImpl> it=(Iterator<GroupImpl>)q.list().iterator();
		while(it.hasNext()){
			result.add(new GroupImpl(it.next()));
		}
		s.close();
		return result;
	}
	
	public static String getUserXPTaskUrl(String procDefUUID,
			String activityDefUUID, String activityInsUUID, String host,
			String port, boolean modeForm, boolean modeView) {

		StringBuilder url = new StringBuilder();
		if (host != null && !host.isEmpty()) {
			url.append("http://");
			url.append(host);
		}
		if (port != null && !port.isEmpty()) {
			url.append(":");
			url.append(port);
		}
		url.append("/bonita/console/homepage?locale=zh");
		url.append("&theme=");
		url.append(procDefUUID);

		url.append("&task=");
		url.append(activityInsUUID);
		url.append("&mode=");
		if (modeForm)
			url.append("form");
		else
			url.append("app");
		
		url.append("#form=");
		url.append(activityDefUUID);
		if (!modeView)
			url.append("$entry");
		else
			url.append("$view");

		return url.toString();
	}

	public static String getUserXPProcessViewOrOverviewUrl(String procDefUUID,String procInsUUID,
			String host, String port, boolean modeForm, boolean modeView) {
		StringBuilder url = new StringBuilder();
		if (host != null && !host.isEmpty()) {
			url.append("http://");
			url.append(host);
		}
		if (port != null && !port.isEmpty()) {
			url.append(":");
			url.append(port);
		}
		url.append("/bonita/console/homepage?locale=zh");
		url.append("&theme=");
		url.append(procDefUUID);
		url.append("&instance=");
		url.append(procInsUUID);
		url.append("&mode=");
		if (modeForm)
			url.append("form");
		else
			url.append("app");
		if (!modeView)
			url.append("&recap=true");
		
		url.append("#form=");
		url.append(procDefUUID);
		url.append("$recap");

		return url.toString();
	}

	public static String getUserXPProcessUrl(String procDefUUID, String host,
			String port, boolean modeForm) {
		StringBuilder url = new StringBuilder();
		if (host != null && !host.isEmpty()) {
			url.append("http://");
			url.append(host);
		}
		if (port != null && !port.isEmpty()) {
			url.append(":");
			url.append(port);
		}
		url.append("/bonita/console/homepage?locale=zh");
		url.append("&theme=");
		url.append(procDefUUID);

		url.append("&process=");
		url.append(procDefUUID);
		
		url.append("&mode=");
		if (modeForm)
			url.append("form");
		else
			url.append("app");
		url.append("&autoInstantiate=false");
		
		url.append("#form=");
		url.append(procDefUUID);
		url.append("$entry");

		return url.toString();
	}

	
}
