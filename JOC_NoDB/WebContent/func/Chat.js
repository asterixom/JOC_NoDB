var upd, upt;
var since = 0;
var pushRunning = false;
var currentUser = "";
var currentChat = ":none";

function checkLogin() {
	loginRequest(false);
}

function createLogin() {
	loginRequest(true);
}

function loginRequest(x) {
	var req = new XMLHttpRequest();
	req.open("POST", "func/Login.jsp", false);
	req.onreadystatechange = function receive() {
		if (req.readyState == 4) {
			var obj = JSON.parse(req.responseText);
			if (obj.login) {
				document.cookie = "sessionID=" + obj.sessionID;
				$("#login").dialog("close");
				login();
			} else {
				$("#login-error").text(obj.text);
			}
		}
	};
	req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	currentUser = $("#name").val();
	var cont = "name=" + currentUser + "&pass=" + escape($("#pass").val());
	if (x) {
		cont = cont + "&create=true";
	}
	req.send(cont);
}

function login() {
	$("#body").show();
	$("body").css("background-image","none");
	$("#header").show();
	$("#usernameField").text(currentUser);
	upd = setInterval(updateOnline, 10000);
	updateOnline();
	updateText();
	upt = setInterval(pushRequest, 1000);
	$("#name").val("");
	$("#pass").val("");
}

function logout(){
	init();
	location.reload();
}

function init() {
	document.cookie = "sessionID=0";
	clearInterval(upt);
	clearInterval(upd);
	$("body").css("background-image", "url(style/Background.png)");
	$("#body").hide();
	$("#header").hide();
	$("#login").dialog("open");
}

function showChat(chat) {
	// $("#outtab").find("tbody").each(function(index, element) {
	// $(element).hide();
	// });
	$(document.getElementById("msg-" + currentChat)).hide();
	document.getElementById("chat-" + currentChat).className = "";
	currentChat = chat;
	createChat(chat);
	document.getElementById("chat-" + chat).className = "current";
	$(document.getElementById("msg-" + chat)).show();
	document.getElementById("bottom").scrollIntoView();
}

function createChat(chat) {
	if (document.getElementById("msg-" + chat) == null) {
		$("#outtab").append(
				"<tbody id=\"msg-" + chat
						+ "\" style=\"display: none;\"></tbody>");
	}
	if (document.getElementById("chat-" + chat) == null) {
		$("#chats").append(
				"<tr id=\"chat-" + chat + "\" onClick=\"showChat('" + chat
						+ "');$($(this).find('td')[1]).text('')\"><td>" + chat
						+ "</td><td></td></tr>");
	}
}

function newGroup() {
	var gn = $("#newGroup").val();
	if(gn.substring(0, 1) != ':'){
		gn = ":" + gn;
	}
	if(gn.length<3){
		alert("Gruppennamen müssen minderstens 2 Zeichen enthalten!");
		return;
	}
	$("#newGroup").val("");
	sendCommand("create", gn);
}

function updateOnline() {
	var req = new XMLHttpRequest();
	req.open("GET", "func/Online.jsp", true);
	req.onreadystatechange = function receive() {
		if (req.readyState == 4) {
			var obj = JSON.parse(req.responseText);
			$("#people").html("");
			for ( var i = 0; i < obj.array.length; i++) {
				$("#people").append(
						"<tr onClick=\"showChat('" + obj.array[i]
								+ "');\"><td>" + obj.array[i]
								+ "</td><td></td></tr>");
			}
		}
	};
	req.send();
}

function pushRequest() {
	if (pushRunning)
		return;
	pushRunning = true;
	var req = new XMLHttpRequest();
	req.open("GET", "func/Update.jsp", true);
	req.onreadystatechange = function receive() {
		if (req.readyState == 4) {
			if (req.status <= 0 || req.status >= 500) {
				$("#noCon").dialog("open");
			} else {
				$("#noCon").dialog("close");
				var obj = JSON.parse(req.responseText);
				if (obj.update)
					updateText();
				if (!obj.online)
					init();
			}
			pushRunning = false;
		}
	};
	req.send();
}

function sendText(recipient, text) {
	var req = new XMLHttpRequest();
	req.open("POST", "func/Messages.jsp", true);
	req.onreadystatechange = function receive() {
		if (req.readyState == 4) {
			var obj = JSON.parse(req.responseText);
			createChat(recipient);
			message = replaceAll(text, "<", "&lt");
			if (obj.boole) {
				if (recipient.substring(0, 1) != ":") {
				$(document.getElementById("msg-" + recipient)).append(
					"<tr class='own'><td>" + currentUser + "</td><td>"
							+ getTimeToString(obj.since) + "</td><td><pre>"
							+ message + "</pre></td></tr>");
				}
			} else {
				$(document.getElementById("msg-" + recipient)).append(
					"<tr class='own'><td>" + currentUser
							+ "</td><td><b>FAILED!</b></td><td><pre>"
							+ message + "</pre></td></tr>");
			}
			document.getElementById("bottom").scrollIntoView();
		}
	};
	req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	var cont = "recipient=" + escape(recipient) + "&sendmsg=" + escape(text);// +"&since="+since;
	req.send(cont);
}

function updateText() {
	var req = new XMLHttpRequest();
	req.open("POST", "func/Messages.jsp", true);
	req.onreadystatechange = function receive() {
		if (req.readyState == 4) {
			var obj = JSON.parse(req.responseText);
			addText(obj);
			since = obj.since;
		}
	};
	req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	if(since<=0){
		req.send("since=" + since);
	}else{
		req.send();
	}
}

function addText(obj) {
	if (obj.msgs.length > 0) {
		document.getElementById("sound_2").play();
	}
	for ( var i = 0; i < obj.msgs.length; i++) {
		for ( var j = 0; j < obj.msgs[i].msgs.length; j++) {
			var message = obj.msgs[i].msgs[j].message;
			// message = replaceAll(replaceAll(message,"&","&amp;"),"<","&lt;");
			// alert(message);
			// $("#msg-all").append("<tr><td>"+obj.msgs[i].sender+"</td><td>"+getTimeToString(obj.msgs[i].msgs[j].time)+"</td><td><pre>"+message+"</pre></td></tr>");
			createChat(obj.msgs[i].sender);
			if(currentChat!=obj.msgs[i].sender){
				document.getElementById("chat-" + obj.msgs[i].sender).className = "neu";
			}
			if(obj.msgs[i].sender==currentUser){
				own = " class='own'";
			}else{
				own = "";
			}
//			$($("#chat-" + obj.msgs[i].sender).find("td")[1]).text("NEU!");
			$("#msg-" + obj.msgs[i].sender).append(
					"<tr"+own+"><td>" + obj.msgs[i].sender + "</td><td>"
							+ getTimeToString(obj.msgs[i].msgs[j].time)
							+ "</td><td><pre>" + message + "</pre></td></tr>");
//			var p = $("#msg-" + obj.msgs[i].sender).find("pre");
//			p[p.length-1].scrollIntoView();
			document.getElementById("bottom").scrollIntoView();
		}
	}
	for ( var i = 0; i < obj.groupmsg.length; i++) {
		//$(document.getElementById("msg-" + obj.groupmsg[i].group)).text();
		for ( var j = 0; j < obj.groupmsg[i].msgs.length; j++) {
			var message = obj.groupmsg[i].msgs[j].message;
			// message = replaceAll(replaceAll(message,"&","&amp;"),"<",
			// "&lt;");
			// alert(message);
			// $("#msg-all").append("<tr><td>"+obj.msgs[i].sender+"</td><td>"+getTimeToString(obj.msgs[i].msgs[j].time)+"</td><td><pre>"+message+"</pre></td></tr>");
			createChat(obj.groupmsg[i].group);
			if(currentChat!=obj.groupmsg[i].group){
				document.getElementById("chat-" + obj.groupmsg[i].group).className = "neu";
			}
			if(obj.groupmsg[i].msgs[j].sender==currentUser){
				own = " class='own'";
			}else{
				own = "";
			}
//			$($(document.getElementById("chat-"+obj.groupmsg[i].group)).find("td")[1]).text("NEU!");
			$(document.getElementById("msg-" + obj.groupmsg[i].group)).append(
					"<tr"+own+"><td>" + obj.groupmsg[i].msgs[j].sender + "</td><td>"
							+ getTimeToString(obj.groupmsg[i].msgs[j].time)
							+ "</td><td><pre>" + message + "</pre></td></tr>");
//			var p = $(document.getElementById("msg-" + obj.groupmsg[i].group)).find("pre");
//			p[p.length-1].scrollIntoView();
			document.getElementById("bottom").scrollIntoView();
		}
	}
}

function getTimeToString(time) {
	var messageTime = new Date();
	if (time != null)
		messageTime.setTime(time);
	var timeString = "";
	if (new Date().getFullYear() == messageTime.getFullYear()
			&& new Date().getMonth() == messageTime.getMonth()
			&& new Date().getDate() == messageTime.getDate()) {
		if (messageTime.getHours() < 10) {
			timeString = "0" + messageTime.getHours();
		} else {
			timeString = messageTime.getHours();
		}
		if (messageTime.getMinutes() < 10) {
			timeString = timeString + ":0" + messageTime.getMinutes();
		} else {
			timeString = timeString + ":" + messageTime.getMinutes();
		}
		if (messageTime.getSeconds() < 10) {
			timeString = timeString + ":0" + messageTime.getSeconds();
		} else {
			timeString = timeString + ":" + messageTime.getSeconds();
		}
	} else {
		if (messageTime.getDate() < 10) {
			timeString = "0" + messageTime.getDate();
		} else {
			timeString = messageTime.getDate();
		}
		if (messageTime.getMonth() + 1 < 10) {
			timeString = timeString + ".0" + (messageTime.getMonth() + 1);
		} else {
			timeString = timeString + "." + (messageTime.getMonth() + 1);
		}
		timeString = timeString + "." + messageTime.getFullYear();
		if (messageTime.getHours() < 10) {
			timeString = timeString + " 0" + messageTime.getHours();
		} else {
			timeString = timeString + " " + messageTime.getHours();
		}
		if (messageTime.getMinutes() < 10) {
			timeString = timeString + ":0" + messageTime.getMinutes();
		} else {
			timeString = timeString + ":" + messageTime.getMinutes();
		}
		if (messageTime.getSeconds() < 10) {
			timeString = timeString + ":0" + messageTime.getSeconds();
		} else {
			timeString = timeString + ":" + messageTime.getSeconds();
		}
	}
	return timeString;
}

function sendCommand(cmd,param){
	var req = new XMLHttpRequest();
	req.open("POST", "func/Commands.jsp", true);
	req.onreadystatechange = function receive() {
		if (req.readyState == 4) {
			var obj = JSON.parse(req.responseText);
			if(obj.text.length>0) {
				alert(obj.text);
			}
		}
	};
	req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	var cont = "command=" + cmd + "&params=" + param + "&chat=" + currentChat;
	req.send(cont);
}

function submit() {
	var text = $("#text").val();
	if(text.length<=0){
		alert("Leere Texte werden nicht gesendet!");
	}
	if(text.substring(0,1)=="/"){
		var lz = text.indexOf(" ");
		var cmd="",params="";
		if(lz>-1 && lz<text.length-1){
			cmd = text.substring(1,lz);
			params = text.substring(lz+1);
		}else{
			cmd = text.substring(1).replace(" ","");
		}
		sendCommand(cmd,params);
		$("#text").val("");
	}else{
		// var value = $("#text").val();
		// var dp = value.indexOf("#");
		// if(dp>-1){
		// var to = value.substring(0,dp);
		// var text = value.substring(dp+1);
		// if(value!=""){
		// sendText(to, text);
		// }
		// $("#text").val(to+"#");
		// }
		sendText(currentChat, text);
		$("#text").val("");
	}
}

function escapeRegExp(string) {
	return string.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
}

function replaceAll(string, find, replace) {
	return string.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}
