package com.helloworld.struts.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class test {
	public static void f(){
		Map<String,String> menu=new HashMap<String,String>() {{
			put("主页","/");
			put("我的工作","mywork.jsp");
			put("项目管理","#");
			put("查询","#");
		}};
		for(Entry<String,String> e : menu.entrySet()){
			//if (e.getKey())
		}
	}
}
