package de.tinf13b2.joc.authentication;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

public class Users {

	private static Properties prop;
	private static String location;
	private static MessageDigest md;
	private static TreeMap<Integer, Session> sessions;
	private static TreeMap<String, Integer> hashs;
	// private boolean inited = false;
	private static Thread running;
	private volatile static boolean updateOnline = false;

	public Users() {
		init();
	}

	public static void init() {
		// if(inited){
		// return this;
		// }else{
		// inited = true;
		// }
		if (sessions == null){
			sessions = new TreeMap<Integer, Session>();
			hashs = new TreeMap<String, Integer>();
		}
		if (md == null) {
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}
		}
		if (location == null)
			location = System.getProperty("user.home") + "/JOCChat/users.prop";
		if (prop == null) {
			prop = new Properties();
			try {
				if (!new File(location).exists()) {
					new File(location).getParentFile().mkdirs();
				}
				prop.load(new FileReader(location));
			} catch (IOException e) {
				end();
			}
		}
		if (running == null) {
			running = new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (Iterator<Entry<Integer, Session>> i = sessions
								.entrySet().iterator(); i.hasNext();) {
							Entry<Integer, Session> e = i.next();
							if (!e.getValue().checkOnline()) {
								hashs.remove(e.getValue().getName());
								i.remove();
							}
							e.getValue().notifyX();
						}
					}
				}
			});
			running.start();
		}
	}

	public static void setPass(String name, String pass) {
		prop.put(name, new String(md.digest(pass.getBytes())));
		end();
	}

	public static boolean checkPass(String name, String pass) {
		String p = (String) prop.get(name);
		if (p == null) {
			return false;
		} else {
			return new String(md.digest(pass.getBytes())).equals(p);
		}
	}

	public static boolean removePass(String name) {
		boolean deleted = prop.remove(name) != null;
		end();
		return deleted;
	}

	public static boolean hasUser(String name) {
		return prop.containsKey(name);
	}

	public static void end() {
		try {
			prop.store(new FileWriter(location), "The Users of JOCChat");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setSession(String name, Session sess) {
		int hash = sess.hashCode();
		hashs.put(name, hash);
		sessions.put(hash,sess);
	}

	public static void removeSession(String name) {
		sessions.remove(hashs.get(name));
		hashs.remove(name);
	}
	
	public static void removeSession(Integer hash) {
		removeSession(sessions.get(hash).getName());
	}
	
	public static Session getSession(Integer hash){
		return sessions.get(hash);
	}
	
	public static Session getSessionByName(String name){
		try{
			return sessions.get(hashs.get(name));
		}catch(NullPointerException e){
			return null;
		}
	}
	
	public static Session getSession(String hash){
		try{
			return sessions.get(Integer.parseInt(hash));
		}catch(NumberFormatException | NullPointerException e){
			return null;
		}
	}

	public static boolean isOnline(String name) {
		Integer hash;
		if ((hash = hashs.get(name)) == null || !sessions.containsKey(hash)) {
			return false;
		}
		return sessions.get(hash).isOnline();
	}

	public static Set<String> getOnline() {
		//System.out.println(hashs.size());
		return hashs.keySet();
	}
	
	public static boolean validate(String sessionID){
		if(sessionID==null || getSession(sessionID)== null || !getSession(sessionID).checkOnline()){
			return false;
		}else{
			return true;
		}
	}
	public static boolean validate(Integer sessionID){
		if(sessionID==null || getSession(sessionID)== null || !getSession(sessionID).checkOnline()){
			return false;
		}else{
			return true;
		}
	}
	
	public static String getName(String sessionID){
		try{
			return sessions.get(Integer.parseInt(sessionID)).getName();
		}catch(NullPointerException | NumberFormatException e){
			return null;
		}
	}
	
	public static String getName(Integer sessionID){
		try{
			return sessions.get(sessionID).getName();
		}catch(NullPointerException e){
			return null;
		}
	}
	
}
