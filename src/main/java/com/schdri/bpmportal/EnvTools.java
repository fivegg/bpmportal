package com.schdri.bpmportal;

public class EnvTools {
	public static final String dbsource="java:/comp/env/isobpm/default/extend";
	static ThreadLocal<EnvTools> currentEnvironment = new ThreadLocal<EnvTools>();
	protected EnvTools(){
		
	}
	/** gets the most inner open environment. */
	public static EnvTools getCurrent() {
	    return currentEnvironment.get();
	}
	public String getUserName()
	{
		return "test"; 
	}
}
