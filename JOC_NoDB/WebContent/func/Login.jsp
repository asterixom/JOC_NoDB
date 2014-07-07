<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="de.tinf13b2.joc.authentication.*"%>
<jsp:useBean id="users" class="de.tinf13b2.joc.authentication.Users"
	scope="application"></jsp:useBean>
<%
	users.init();
	String n = request.getParameter("name");
	String p = request.getParameter("pass");
	String c = request.getParameter("create");
	if (n != null && n.length() > 0 && p != null && p.length() > 0) {
		if (c != null && c.length() > 0) {
			if (users.hasUser(n)) {
%>
					{"login": false, "text": "Name bereits vorhanden!"}
<%
			} else if (n.startsWith(":")) {
%>
					{"login": false, "text": "Dein Name darf nicht mit : beginnen!"}
<%
			} else if (!n.matches("^[A-Za-z_.-]+$")) {
%>
					{"login": false, "text": "Dein Name darf nur aus A-Za-z_.- bestehen!"}
<%
			} else if (n.length() > 15) {
%>
					{"login": false, "text": "Dein Name darf nicht länger als 15 Zeichen sein!"}
<%
			} else if (n.length() < 3) {
%>
				{"login": false, "text": "Dein Name muss minderstens 3 Zeichen lang sein!"}
<%
			} else {
				users.setPass(n, p);
%>
					{"login": false, "text": "Account erstellt, bitte einloggen!"}
<%
			}
		} else {
			if (users.checkPass(n, p)) {
				Session sess = new Session();
				sess.setName(n);
				sess.setOnline(true);
				users.setSession(n, sess);
%>
				{"login": true, "sessionID":<%=sess.hashCode()%>}
<%
			} else {
%>
				{"login": false, "text": "Name und/oder Passwort nicht korrekt!"}
<%
			}
		}
	} else {
%>
		{"login": false, "text": "Name und/oder Passwort leer!"}
<%
	}
%>