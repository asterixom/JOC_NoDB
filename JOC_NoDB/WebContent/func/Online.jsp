<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<jsp:useBean id="users" class="de.tinf13b2.joc.authentication.Users" scope="application"></jsp:useBean>
<% users.init(); 
Set<String> s = users.getOnline();
%>
{
"length":<%= s.size() %>,
"array":[
<% 
if(s.size()>0){
	Iterator<String> i = s.iterator();
	while(true){
		out.print("\""+i.next()+"\"");
		if(i.hasNext()){
			out.print(",");
		}else{
			break;
		}
	}
}
%>
]}