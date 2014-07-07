<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="de.tinf13b2.joc.chat.*"%>
<%@ page import="de.tinf13b2.joc.authentication.*"%>
{
<%
Users.init();
String sessionID = null;
if (request.getCookies() != null) {
	for (Cookie cookie : request.getCookies()) {
		if (cookie.getName().equals("sessionID")) {
			sessionID = cookie.getValue();
			break;
		}
	}
}
if (Users.validate(sessionID)) {
	Session sess = Users.getSession(sessionID);
	sess.ping();
	String user = sess.getName();
	String command,params,chat;
	if ((command = request.getParameter("command")) != null) {
		if((params = request.getParameter("params")) == null){
			params = "";
		}
		if((chat = request.getParameter("chat")) == null){
			chat = "";
		}
		out.println(Commands.command(user, chat, command, params));
	}else{
		out.println("\"ok\": false, \"text\": \"No command!\"");
	}
}else{
	out.println("\"ok\": false, \"text\": \"Not logged in!\"");
}
%>
}