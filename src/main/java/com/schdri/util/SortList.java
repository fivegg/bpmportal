package com.schdri.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortList<E>{   
	@SuppressWarnings("unchecked")
	public void Sort(List<E> list, final String method, final String sort){  
        Collections.sort(list, new Comparator() {             
            public int compare(Object a, Object b) {  
                int ret = 0;  
                try{  
                    Method m1 = ((E)a).getClass().getMethod(method, null);  
                    Method m2 = ((E)b).getClass().getMethod(method, null);  
                    Object ret1=m1.invoke(((E)a), null);
                    Object ret2=m2.invoke(((E)b), null);
                    if(sort != null && "desc".equals(sort)){ //倒序
                    	if (ret1 instanceof Comparable)
                    		ret = ((Comparable)ret2).compareTo((Comparable)ret1);
                    	else
                    		ret = ret2.toString().compareTo(ret1.toString());
                    }
                    else{
	                	if (ret1 instanceof Comparable)
	                		ret = ((Comparable)ret1).compareTo((Comparable)ret2);
	                	else
	                		ret = ret1.toString().compareTo(ret2.toString());
                    }
                }catch(NoSuchMethodException ne){  
                    System.out.println(ne);  
                }catch(IllegalAccessException ie){  
                    System.out.println(ie);  
                }catch(InvocationTargetException it){  
                    System.out.println(it);  
                }  
                return ret;  
            }  
         });  
    }  
}
