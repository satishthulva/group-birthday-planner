<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>HBDR App</title>
</head>
<body>

  <%
   String hello = "Hello there";
  
   List<String> parameters = new ArrayList<String>();
  
   Enumeration params = request.getParameterNames();
   if(params != null)
   {
       while(params.hasMoreElements())
       {
           parameters.add(request.getParameter((String)params.nextElement()));
       }
   }
  %>
  
  <h2>Greeting : <%= hello %></h2>
  <p>
   <%= parameters %>
  </p>

</body>
</html>