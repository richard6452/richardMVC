package com.richardmvc.framework;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.richardmvc.annotations.MyGetMapping;
import com.richardmvc.controller.UserController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/")
public class DispatcherServlet extends HttpServlet {
    private Map<String, GetDispatcher> getMappings = new HashMap<>();
    private ViewEngine viewEngine;

    @Override
    public void init() throws ServletException {
        try {
            this.getMappings = scanGetInControllers();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //this.postMappings = scanPostInControllers();
        this.viewEngine = new ViewEngine(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ModelAndView mv = null;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        GetDispatcher getDispatcher = getMappings.get(path);
        try {
            mv = getDispatcher.invoke(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintWriter pw = resp.getWriter();
        try {
            this.viewEngine.render(mv, pw);
        } catch (PebbleException e) {
            e.printStackTrace();
        }
        pw.flush();
    }

    private Map<String, GetDispatcher> scanGetInControllers() throws ClassNotFoundException {
        Map<String, GetDispatcher> getMapping = new HashMap<>();
        Class<?> userController = Class.forName("com.richardmvc.controller.UserController");
        for(Method method : userController.getDeclaredMethods()){
            if(method.isAnnotationPresent(MyGetMapping.class)){
                GetDispatcher getDispatcher = new GetDispatcher();
                getDispatcher.setMethod(method);
                List<Parameter> parameters = Arrays.asList(method.getParameters());
                List<String> parameterNames = parameters.stream().map(Parameter::getName).collect(Collectors.toList());
                getDispatcher.setParameterNames(parameterNames.toArray(new String[0]));
                getDispatcher.setParameterClasses(method.getParameterTypes());
                getDispatcher.setInstance(new UserController());
                getMapping.put(method.getAnnotation(MyGetMapping.class).value(),getDispatcher);
            }
        }
        return getMapping;
    }
}
