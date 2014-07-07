<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="de.tinf13b2.joc.authentication.*"%>
<%
Users.init();
	String sessionID = null;
	if(request.getCookies() != null){
		for(Cookie cookie : request.getCookies()){
			if(cookie.getName().equals("sessionID")){
				sessionID = cookie.getValue();
				break;
			}
		}	
	}
	boolean update = false;
	try{
		update = Users.getSession(sessionID).waitForText();
	}catch(NullPointerException e){
	}
	
%>
{ "update":<%= update %>, "online": <%=Users.validate(sessionID) %> }