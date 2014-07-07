package de.tinf13b2.joc.chat;


public class Commands {
public static String command(String user, String chat, String command, String params){
	Chat c = Chat.getInstance();
	boolean ok = true;
	String text = "";
	String[] arr = {};
	switch(command){
	case "add":
		arr = params.split(" ");
		if(arr.length<=0){
			arr = new String[]{""};
		}
		for(String person : arr){
			if(!c.addUser(user, chat, person)){
				ok = false;
				text += "Kein Zugriff oder User "+person+" existiert nicht!";
			}
		}
		return "\"ok\": "+ok+", \"text\": \""+text+"\"";
	case "create":
		if(!params.matches("^:[A-Za-z_.-]+$")){
			ok = false;
			text += "Gruppennamen dürfen nur aus A-Za-z_.- bestehen!";
		}else if(!c.createChat(user, params)){
			ok = false;
			text += "Gruppe existiert bereits!";
		}
		return "\"ok\": "+ok+", \"text\": \""+text+"\"";
	case "remove":
		arr = params.split(" ");
		if(arr.length<=0){
			arr = new String[]{""};
		}
		for(String person : arr){
			if(!c.removeUser(user, chat, person)){
				ok = false;
				text += "Kein Zugriff oder User "+person+" existiert nicht!";
			}
		}
		return "\"ok\": "+ok+", \"text\": \""+text+"\"";
	default:
		return "\"ok\": false, \"text\": \"Not implemented!\"";
	}
}
}
