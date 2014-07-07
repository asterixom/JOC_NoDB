<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="style/Chat.css">
<link rel="stylesheet"
	href="jquery/jquery-ui/css/ui-lightness/jquery-ui-1.10.4.css">
<title>JOCChat</title>
<script src="jquery/jquery-2.1.1.js"></script>
<script src="jquery/jquery-ui/jquery-ui-1.10.4.js"></script>
<script src="func/Chat.js"></script>
<%@ page import="de.tinf13b2.joc.authentication.*"%>
<jsp:useBean id="users" class="de.tinf13b2.joc.authentication.Users"
	scope="application"></jsp:useBean>
<%
	users.init();
%>
<%
	String sessionID = null;
	if (request.getCookies() != null) {
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("sessionID")) {
				sessionID = cookie.getValue();
				break;
			}
		}
	}
%>
<script type="text/javascript">
	$("document")
			.ready(
					function() {
						$("#noCon").dialog({
							dialogClass : "no-close",
							"autoOpen" : false,
							"modal" : true,
							"buttons" : {
								"Refresh Page" : function() {
									window.location = "Chat.jsp";
								}
							},
							closeOnEscape : false
						});
<%if (!Users.validate(sessionID)) {%>
	$("#login").dialog({
							dialogClass : "no-close",
							"autoOpen" : true,
							"modal" : true,
							"buttons" : {
								"Login" : checkLogin,
								"Create Account" : createLogin
							},
							closeOnEscape : false
						});
<%} else {
				out.println("currentUser = \"" + Users.getName(sessionID)
						+ "\"");%>
	login();
						$("#login").dialog({
							dialogClass : "no-close",
							"autoOpen" : false,
							"modal" : true,
							"buttons" : {
								"Login" : checkLogin,
								"Create Account" : createLogin
							},
							closeOnEscape : false
						});
<%}%>
	$("#no-js").hide();

						//	$("#submit").click(function(){
						//		submit();
						//	});
					});
</script>
</head>
<body style="background-color: #CCCCCC; background-image: url(style/Background.png)">

	<audio id="sound_2" src="sounds/sound_2.mp3" preload="auto"></audio>
	<audio id="cecile" src="sounds/Cecile.mp3" preload="auto"></audio>
	<div id="header">
		<div id="banner">
			<img id="logo" SRC="style/JOCChat.png"
				onClick='document.getElementById("cecile").play();' ALT="some text"
				WIDTH="180" HEIGHT="65">
			<div id="logout" align="right">Eingeloggt als:</div>
			<div id="usernameField"></div>
			<input id="auslogbutton" type="button" value="Ausloggen"
				onClick="logout();">

		</div>
	</div>
	<div id="no-js">
		Auf dieser Seite wird Javascript benötigt!<br>This website needs
		javascript to work!
	</div>
	<div id="body">
		<div id="chat">
			<div id="chat-head">Chat mit:</div>
			<div id="output">
				<table id="outtab">
					<thead>
						<tr>
							<th>Autor</th>
							<th>Zeit</th>
							<th>Text</th>
						</tr>
					</thead>
					<tbody id="msg-:none">
						<tr>
							<td></td>
							<td></td>
							<td>Bitte wähle einen Chat aus -&gt;</td>
						</tr>
					</tbody>
					<tbody id="msg-:all" style="display: none;">
					</tbody>
				</table>
				<div id="bottom"></div>
			</div>
			<div id="input">
				<form method="POST" action="javascript:submit();">
					<input type="text" id="text" name="text"><input id="submit"
						type="submit" value="Senden">
				</form>
			</div>
		</div>
		<div id="groups-head">Chats</div>
		<div id="groups">
			<form action="javascript:newGroup();">
				<table>
					<thead>
						<tr>
							<th>Name</th>
							<th>Optionen</th>
						</tr>
					</thead>
					<tbody id="chats">
						<tr id="chat-:none" onClick="showChat(':none');" class="current">
							<td>NONE</td>
							<td></td>
						</tr>
						<tr id="chat-:all" onClick="showChat(':all');">
							<td>ALL</td>
							<td></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td><input type="text" maxlength="15" id="newGroup"></td>
							<td><input type="submit" value="Neu"></td>
						</tr>
					</tfoot>
				</table>
			</form>
		</div>
		<div id="online-head">Online</div>
		<div id="online">
			<table>
				<thead>
					<tr>
						<th>Name</th>
						<th>Optionen</th>
					</tr>
				</thead>
				<tbody id="people"></tbody>
			</table>
		</div>
	</div>
	<div id="login" title="Login">
		<form method="POST" action="javascript:checkLogin();">
			<table>
				<tr>
					<th>Name:</th>
					<td><input type="text" id="name"></td>
				</tr>
				<tr>
					<th>Passwort:</th>
					<td><input type="password" id="pass"
						onKeyPress="if(event.which==13){checkLogin();}"></td>
				</tr>
				<tr>
					<td colspan="2" id="login-error"></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="noCon" title="No Connection">
		Sorry, No Connection.<br>We are trying to get that chat back.
	</div>
</body>
</html>
