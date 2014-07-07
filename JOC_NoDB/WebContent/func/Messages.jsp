<%@page import="java.util.Map.Entry"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="de.tinf13b2.joc.chat.*"%>
<%@ page import="de.tinf13b2.joc.authentication.*"%>
<%@ page import="java.util.concurrent.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map.*"%>
<%
	Users.init();
%>
{ "since": "<%=System.currentTimeMillis()%>", "msgs" : [
<%
	boolean b = false;
	Chat chat = Chat.getInstance();
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
		String sendmsg = "", recipient = "";
		if ((sendmsg = request.getParameter("sendmsg")) != null && (recipient = request.getParameter("recipient")) != null) {
			b = chat.senden(sess.getName(),recipient,sendmsg.replaceAll("&", "&amp;").replaceAll("\\\\", "&#92;").replaceAll("\"", "&quot;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
		} else {
			Long l = null;
			try {
				l = Long.parseLong(request.getParameter("since"));
			} catch (Exception e) {
				//l = new Long(0);
			}
			ConcurrentHashMap<String, ConcurrentNavigableMap<Long, String>> msgs = chat
					.getMessagesBySender(sess.getName(), l);
			if (msgs != null) {
				String minus = "";
				for (Entry<String, ConcurrentNavigableMap<Long, String>> entry : msgs
						.entrySet()) {
					out.print(minus + "{ \"sender\": \""
							+ entry.getKey() + "\", \"msgs\": [");
					String plus = "";
					for (Entry<Long, String> msg : entry.getValue()
							.entrySet()) {
						out.print(plus);
						plus = ",";
						out.print("{ \"time\": \"" + msg.getKey()
								+ "\",");
						out.print("\"message\": \"" + msg.getValue()
								+ "\"}");
					}
					out.print("]}");
					minus = ",";
				}
			}
%>
], "groupmsg": [
<%
	HashMap<String, TreeMap<Long, String[]>> groupmsg = chat
					.getGroupMessages(sess.getName(), l);
			if (groupmsg != null) {
				String minus = "";
				for (Entry<String, TreeMap<Long, String[]>> entry : groupmsg
						.entrySet()) {
					out.print(minus + "{ \"group\": \""
							+ entry.getKey() + "\", \"msgs\": [");
					String plus = "";
					for (Entry<Long, String[]> msg : entry.getValue()
							.entrySet()) {
						out.print(plus);
						plus = ",";
						out.print("{ \"time\": \"" + msg.getKey()
								+ "\",");
						out.print("\"sender\": \"" + msg.getValue()[0]
								+ "\",");
						out.print("\"message\": \"" + msg.getValue()[1]
								+ "\"}");
					}
					out.print("]}");
					minus = ",";
				}
			}
		}
	}
%>
],"boole":<%=b%>}
