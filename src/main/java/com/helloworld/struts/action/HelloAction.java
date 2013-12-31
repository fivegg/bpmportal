package com.helloworld.struts.action;

import com.opensymphony.xwork2.ActionSupport;

public class HelloAction extends ActionSupport{
	private String userName;//保存请求参数用户姓名
	private String reslutStr;//存放处理结果

	/** 重载ActionSupport类的execute方法 */
	public String execute(){
		reslutStr="这是业务控制器HelloAction处理的结果内容!";
		return SUCCESS;
	}
	/** 手动进行表单验证 */
	public void validate(){
		//用户姓名不能为空
		if(userName==null||userName.trim().length()<1){
			addFieldError("userName",getText("username.error"));
		}
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getReslutStr() {
		return reslutStr;
	}
	public void setReslutStr(String reslutStr) {
		this.reslutStr = reslutStr;
	}	
}
