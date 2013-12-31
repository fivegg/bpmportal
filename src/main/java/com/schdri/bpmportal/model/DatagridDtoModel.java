package com.schdri.bpmportal.model;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.schdri.bpmportal.dto.SimpleActivity;
import com.schdri.util.SortList;

public class DatagridDtoModel<E> {
	private String sort;
	private String order;
	private int pagenumber;
	private int rownumber;
	private int total=-1;
	private List<E> rows;
	
	public int fromIndex(){
		return (pagenumber-1)*rownumber;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public int getTotal() {
		return rows.size();
	}

	public List<E> getRows() {
		return rows;
	}
	public void setRows(List<E> rows) {
		this.rows = rows;
	}
	public int getPagenumber() {
		return pagenumber;
	}
	public void setPagenumber(int page) {
		this.pagenumber = page;
	}
	public int getRownumber() {
		return rownumber;
	}
	public void setRownumber(int rownumber) {
		this.rownumber = rownumber;
	}
	public void Sort(){
		Sort(sort,order);
	}
	
	public void Sort(String sortParamName,String order){
		//排序
		this.sort=sortParamName;
		this.order=order;
		
		String sortFunName,sortOrder;
		SortList<E> sortList=new SortList<E>();
		if (getSort()!=null&& !getSort().isEmpty() && !getSort().equals("toString"))
			sortFunName="get"+getSort().substring(0,1).toUpperCase()+getSort().substring(1);
		else
			sortFunName="toString";
		
		if (getOrder()==null || getOrder().isEmpty())
			sortOrder="desc";
		else
			sortOrder=getOrder();
		sortList.Sort(rows,sortFunName, sortOrder);
	}
	public void loadDatagridParams(HttpServletRequest request){
		
		try{
			if (getPagenumber()==0)
				setPagenumber(Integer.parseInt(request.getParameter("page")));
		}
		catch(Exception ex){
		}
		try{
			if (getRownumber()==0)
				setRownumber(Integer.parseInt(request.getParameter("rows")));
		}
		catch(Exception ex){
			setRownumber(9999);
		}
		try{
			if (getOrder()==null)
				setOrder(request.getParameter("order"));
		}
		catch(Exception ex){
			setOrder("toString");
		}
		try{
			if (getSort()==null)
				setSort(request.getParameter("sort"));
		}
		catch(Exception ex){
			setSort("desc");
		}
	}
}
