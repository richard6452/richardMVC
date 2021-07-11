package com.richardmvc.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class GetDispatcher {
    private Object instance;
    private Method method;
    private String[] parameterNames;
    private Class<?>[] parameterClasses;

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(String[] parameterNames) {
        this.parameterNames = parameterNames;
    }

    public Class<?>[] getParameterClasses() {
        return parameterClasses;
    }

    public void setParameterClasses(Class<?>[] parameterClasses) {
        this.parameterClasses = parameterClasses;
    }

    public ModelAndView invoke(HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
        Object[] arguments = new Object[parameterClasses.length];
        for(int i=0;i<parameterClasses.length;i++){
            String parameterName = parameterNames[i];
            Class<?> parameterClass = parameterClasses[i];
            if(parameterClass == HttpServletRequest.class){
                arguments[i] = request;
            }else if(parameterClass == HttpServletResponse.class){
                arguments[i] = response;
            }else if(parameterClass == HttpSession.class){
                arguments[i] = request.getSession();
            }else if(parameterClass == int.class){
                arguments[i] = Integer.valueOf(getOrDefault(request,parameterName,"0"));
            }else if(parameterClass == long.class){
                arguments[i] = Long.valueOf(getOrDefault(request,parameterName,"0"));
            }else if(parameterClass == boolean.class){
                arguments[i] = Boolean.valueOf(getOrDefault(request,parameterName,"false"));
            }else if(parameterClass == String.class){
                arguments[i] = getOrDefault(request,parameterName,"");
            }else{
                throw new RuntimeException("Missing type handler for "+ parameterClass);
            }
        }
        return (ModelAndView) this.method.invoke(this.instance,arguments);
    }

    private String getOrDefault(HttpServletRequest request,String name, String defaultValue){
        String s = request.getParameter(name);
        return s == null? defaultValue:s;
    }
}
