package de.tinf13b2.joc.chat;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import de.tinf13b2.joc.authentication.Users;

public class Chat {
	private ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>>> msgs;
	private ConcurrentHashMap<String, ConcurrentSkipListSet<String>> groups;
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> sinces;
	private static Chat chat;

	private Chat() {
		sinces = new ConcurrentHashMap<String, ConcurrentHashMap<String, Long>>();
		msgs = new ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>>>();
		groups = new ConcurrentHashMap<String, ConcurrentSkipListSet<String>>();
		groups.put(":none", new ConcurrentSkipListSet<String>());
		groups.put(":all", new ConcurrentSkipListSet<String>());
		senden("sys:tem", ":all", "Hello World! Type /add to be notified on new messages. /remove to cancel notifications.");
	}

	public boolean addUser(String commander,String group,String user){
//		System.out.print("Adding "+user+" to "+group+"...");
		if(":all".equals(group)){
			try{
				groups.get(":all").add(commander);
				return true;
			}catch(Exception e){
				return false;
			}
		}
//		System.out.print("3...");
		if(!groups.containsKey(group)){
			return false;
		}
//		System.out.print("2...");
		if(!Users.hasUser(user)){
			return false;
		}
//		System.out.print("1...");
		ConcurrentSkipListSet<String> set = groups.get(group);
		if(!set.contains(commander)){
			return false;
		}
//		System.out.print("0...");
		set.add(user);
		if(!sinces.containsKey(user)){
			sinces.put(user, new ConcurrentHashMap<String, Long>());
		}
		sinces.get(user).put(group, new Long(0));
		return true;
	}
	
	public boolean removeUser(String commander,String group,String user){
//		System.out.print("Removing "+user+" from "+group+"...");
		if(":all".equals(group)){
			try{
				groups.get(":all").remove(commander);
				return true;
			}catch(Exception e){
				return false;
			}
		}else{
			return false;
		}
////		System.out.print("3...");
//		if(!groups.containsKey(group)){
//			return false;
//		}
////		System.out.print("2...");
//		if(!Users.hasUser(user)){
//			return false;
//		}
////		System.out.print("1...");
//		ConcurrentSkipListSet<String> set = groups.get(group);
//		if(!set.contains(commander)){
//			return false;
//		}
////		System.out.print("0...");
//		set.add(user);
//		if(!sinces.containsKey(user)){
//			sinces.put(user, new ConcurrentHashMap<String, Long>());
//		}
//		sinces.get(user).put(group, new Long(0));
//		return true;
	}
	
	public boolean createChat(String commander, final String group){
		if (groups.containsKey(group)) {
			return false;
		}
		ConcurrentSkipListSet<String> names = new ConcurrentSkipListSet<String>();
		names.add(commander);
		names.add("sys:tem");
		groups.put(group, names);
		if(!sinces.containsKey(commander)){
			sinces.put(commander, new ConcurrentHashMap<String, Long>());
		}
		sinces.get(commander).put(group, new Long(0));
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
				}finally{
					senden("sys:tem",group,"Chat created! Add other people with '/add user_name'.");
				}
			}
		}).start();
		return true;
	}
	
	public boolean senden(String sender, final String empfaenger, String Nachricht) {
		// System.out.println(empfaenger);
		if(empfaenger.length()<3){
			return false;
		}
		if (empfaenger.startsWith(":")) {
			if(":all".equals(empfaenger)){
				
			}else if(":none".equals(empfaenger)){
				return false;
			}else if (!groups.containsKey(empfaenger) || !groups.get(empfaenger).contains(sender)) {
					return false;
			}
		}
		if(Nachricht.length()<=0){
			return false;
		} else {
			// System.out.print("Sending message.");
			if (!msgs.containsKey(empfaenger)) {
				msgs.put(
						empfaenger,
						new ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>>());
			}
			ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>> emp = msgs
					.get(empfaenger);
			// System.out.print(".");
			if (!emp.containsKey(sender)) {
				emp.put(sender, new ConcurrentSkipListMap<Long, String>());
			}
			ConcurrentSkipListMap<Long, String> send = emp.get(sender);
			send.put(System.currentTimeMillis(), Nachricht);
			// System.out.print(".");
			if (empfaenger.startsWith(":")) {
				if(!sinces.containsKey(empfaenger)){
					sinces.put(empfaenger, new ConcurrentHashMap<String, Long>());
				}
				ConcurrentHashMap<String, Long> ss = sinces.get(empfaenger);
				if(!ss.containsKey(empfaenger)){
					ss.put(empfaenger, new Long(0));
				}
				new Thread(new Runnable() {
						
						@Override
						public void run() {
							for(String name : groups.get(empfaenger).toArray(new String[0])){
								try {
									Users.getSessionByName(name).update();
								} catch (NullPointerException e) {
								}
							}
						}
					}).start();
			}else{
				if(!sinces.containsKey(sender)){
					sinces.put(sender, new ConcurrentHashMap<String, Long>());
				}
				ConcurrentHashMap<String, Long> ss = sinces.get(sender);
				if(!ss.containsKey(sender)){
					ss.put(sender, new Long(0));
				}
				try {
					Users.getSessionByName(empfaenger).update();
				} catch (NullPointerException e) {
				}
			}
			// System.out.println(" Done.");
			return true;
		}
		// for(Entry<String, ConcurrentHashMap<String,
		// ConcurrentSkipListMap<Long, String>>> c : msgs.entrySet()){
		// System.out.println(""+c.getKey());
		// for(Entry<String, ConcurrentSkipListMap<Long, String>> a :
		// c.getValue().entrySet()){
		// System.out.println("	"+a.getKey());
		// for(Entry<Long,String> e : a.getValue().entrySet()){
		// System.out.println("		"+e.getKey()+" : "+e.getValue());
		// }
		// }
		// }
	}

	public ConcurrentNavigableMap<Long, String> getMessages(String to,String from){
		try{
			Long since = sinces.get(to).get(from);
			if(!sinces.containsKey(to)){
				sinces.put(to, new ConcurrentHashMap<String, Long>());
			}
			ConcurrentHashMap<String, Long> ss = sinces.get(to);
			if(ss.containsKey(from)){
				ss.put(from, System.currentTimeMillis());
			}else{
				ss.put(from, new Long(0));
			}
			return getMessages(to, from, since);
		}catch(NullPointerException e){
			return null;
		}
	}
	
	public ConcurrentNavigableMap<Long, String> getMessages(String to,
			String from, Long since) {
		if(since == null){
			return getMessages(to, from);
		}
		try {
			return msgs.get(to).get(from).tailMap(since);
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public TreeMap<Long, String[]> getMessagesByTime(String to) {
		try{
			ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>> emp = msgs
					.get(to);
			TreeMap<Long, String[]> tmp = new TreeMap<Long, String[]>(); // tmp = TreeMap oder Temp ^^
			for (Entry<String, ConcurrentSkipListMap<Long, String>> entry : emp
					.entrySet()) {
				Long since = new Long(0);
				try{
					since = sinces.get(to).get(entry.getKey());
				}catch(NullPointerException e){
				}
				if(!sinces.containsKey(to)){
					sinces.put(to, new ConcurrentHashMap<String, Long>());
				}
				ConcurrentHashMap<String, Long> ss = sinces.get(to);
				if(ss.containsKey(entry.getKey())){
					ss.put(entry.getKey(), System.currentTimeMillis());
				}else{
					ss.put(entry.getKey(), new Long(0));
				}
				for (Entry<Long, String> msg : entry.getValue().tailMap(since)
						.entrySet()) {
					long time = msg.getKey();
					while (tmp.containsKey(time)) {
						time++;
					}
					tmp.put(time,
							new String[] { entry.getKey(), msg.getValue() });
				}
			}
			return tmp;
		}catch(NullPointerException e){
			return null;
		}
	}
	
	public TreeMap<Long, String[]> getMessagesByTime(String to, Long since) {
		if(since == null){
			return getMessagesByTime(to);
		}
		try {
			ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>> emp = msgs
					.get(to);
			TreeMap<Long, String[]> tmp = new TreeMap<Long, String[]>(); // tmp
																			// =
																			// TreeMap
																			// oder
																			// Temp
																			// ^^
			for (Entry<String, ConcurrentSkipListMap<Long, String>> entry : emp
					.entrySet()) {
				for (Entry<Long, String> msg : entry.getValue().tailMap(since)
						.entrySet()) {
					long time = msg.getKey();
					while (tmp.containsKey(time)) {
						time++;
					}
					tmp.put(time,
							new String[] { entry.getKey(), msg.getValue() });
				}
			}
			return tmp;
		} catch (NullPointerException e) {
			return null;
		}
	}

	public ConcurrentHashMap<String, ConcurrentNavigableMap<Long, String>> getMessagesBySender(String to) {
		// try{
		ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>> emp = msgs
				.get(to);
		if (emp == null || emp.size() <= 0) {
			return null;
		}
		ConcurrentHashMap<String, ConcurrentNavigableMap<Long, String>> tmp = new ConcurrentHashMap<String, ConcurrentNavigableMap<Long, String>>();
		for (Entry<String, ConcurrentSkipListMap<Long, String>> entry : emp
				.entrySet()) {
			Long since = new Long(0);
			try{
				since = sinces.get(to).get(entry.getKey());
			}catch(NullPointerException e){
			}
			if(!sinces.containsKey(to)){
				sinces.put(to, new ConcurrentHashMap<String, Long>());
			}
			ConcurrentHashMap<String, Long> ss = sinces.get(to);
			if(ss.containsKey(entry.getKey())){
				ss.put(entry.getKey(), System.currentTimeMillis());
			}else{
				ss.put(entry.getKey(), new Long(0));
			}
			ConcurrentNavigableMap<Long, String> tmp2 = entry.getValue()
					.tailMap(since);
			if (!tmp2.isEmpty()){
				tmp.put(entry.getKey(), tmp2);}
		}
		return tmp;
		// }catch(NullPointerException e){
		// System.out.println("NULL!!!");
		// return null;
		// }
	}
	
	public ConcurrentHashMap<String, ConcurrentNavigableMap<Long, String>> getMessagesBySender(String to, Long since) {
		if(since == null){
			return getMessagesBySender(to);
		}
		// try{
		ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>> emp = msgs
				.get(to);
		if (emp == null || emp.size() <= 0) {
			return null;
		}
		ConcurrentHashMap<String, ConcurrentNavigableMap<Long, String>> tmp = new ConcurrentHashMap<String, ConcurrentNavigableMap<Long, String>>();
		for (Entry<String, ConcurrentSkipListMap<Long, String>> entry : emp
				.entrySet()) {
			ConcurrentNavigableMap<Long, String> tmp2 = entry.getValue()
					.tailMap(since);
			if (!tmp2.isEmpty())
				tmp.put(entry.getKey(), tmp2);
		}
		return tmp;
		// }catch(NullPointerException e){
		// System.out.println("NULL!!!");
		// return null;
		// }
	}
	
	public HashMap<String, TreeMap<Long, String[]>> getGroupMessages(String to) {
		HashMap<String, TreeMap<Long, String[]>> ret = new HashMap<String, TreeMap<Long, String[]>>();
		for (Entry<String, ConcurrentSkipListSet<String>> ent : groups
				.entrySet()) {
			if (ent.getValue().contains(to) || ent.getKey().equals(":all")) {
				ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>> emp = msgs.get(ent.getKey());
				TreeMap<Long, String[]> lol = new TreeMap<Long, String[]>();
//				System.out.println(ent.getKey());
				Long since = null;
				try{
					since = sinces.get(to).get(ent.getKey());
				}catch(NullPointerException e){
				}
				if(since==null){
					since = new Long(0);
				}
				if(!sinces.containsKey(to)){
					sinces.put(to, new ConcurrentHashMap<String, Long>());
				}
				ConcurrentHashMap<String, Long> ss = sinces.get(to);
				if(ss.containsKey(ent.getKey())){
					ss.put(ent.getKey(), new Long(System.currentTimeMillis()));
				}else{
					ss.put(ent.getKey(), new Long(0));
				}
				for (Entry<String, ConcurrentSkipListMap<Long, String>> entry : emp.entrySet()) {
					for (Entry<Long, String> msg : entry.getValue().tailMap(since).entrySet()) { // .tailMap(since)
						long time = msg.getKey();
						while (lol.containsKey(time)) {
							time++;
						}
						lol.put(time,
								new String[] { entry.getKey(), msg.getValue() });
//						System.out.println("	" + entry.getKey() + " (" + time + ") -> " + msg.getValue());
					}
				}
				ret.put(ent.getKey(), lol);
			}
		}
		return ret;
	}

	public HashMap<String, TreeMap<Long, String[]>> getGroupMessages(String to,
			Long since) {
		if(since == null){
			return getGroupMessages(to);
		}
		HashMap<String, TreeMap<Long, String[]>> ret = new HashMap<String, TreeMap<Long, String[]>>();
		for (Entry<String, ConcurrentSkipListSet<String>> ent : groups
				.entrySet()) {
			if (ent.getValue().contains(to) || ent.getKey().equals(":all")) {
				ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>> emp = msgs.get(ent.getKey());
				TreeMap<Long, String[]> lol = new TreeMap<Long, String[]>();
//				System.out.println(ent.getKey());
				for (Entry<String, ConcurrentSkipListMap<Long, String>> entry : emp.entrySet()) {
					for (Entry<Long, String> msg : entry.getValue().entrySet()) { // .tailMap(since)
						long time = msg.getKey();
						while (lol.containsKey(time)) {
							time++;
						}
						lol.put(time,
								new String[] { entry.getKey(), msg.getValue() });
//						System.out.println("	" + entry.getKey() + " (" + time + ") -> " + msg.getValue());
					}
				}
				ret.put(ent.getKey(), lol);
			}
		}
		return ret;
	}

	public TreeMap<Long, String[]> getGroupMessages(String to,String group) {
			if (group.equals(":all") || groups.get(group).contains(to)) {
				ConcurrentHashMap<String, ConcurrentSkipListMap<Long, String>> emp = msgs.get(group);
				TreeMap<Long, String[]> lol = new TreeMap<Long, String[]>();
//				System.out.println(ent.getKey());
				Long since = null;
				try{
					since = sinces.get(to).get(group);
				}catch(NullPointerException e){
				}
				if(since==null){
					since = new Long(0);
				}
				if(!sinces.containsKey(to)){
					sinces.put(to, new ConcurrentHashMap<String, Long>());
				}
				ConcurrentHashMap<String, Long> ss = sinces.get(to);
				if(ss.containsKey(group)){
					ss.put(group, new Long(System.currentTimeMillis()));
				}else{
					ss.put(group, new Long(0));
				}
				for (Entry<String, ConcurrentSkipListMap<Long, String>> entry : emp.entrySet()) {
					for (Entry<Long, String> msg : entry.getValue().tailMap(since).entrySet()) { // .tailMap(since)
						long time = msg.getKey();
						while (lol.containsKey(time)) {
							time++;
						}
						lol.put(time,
								new String[] { entry.getKey(), msg.getValue() });
//						System.out.println("	" + entry.getKey() + " (" + time + ") -> " + msg.getValue());
					}
				}
				return lol;
			}
		return null;
	}
	
	public static Chat getInstance() {
		if (chat == null) {
			chat = new Chat();
		}
		return chat;
	}

}
