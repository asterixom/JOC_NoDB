package de.tinf13b2.joc.authentication;


public class Session {
	private String name;
	private boolean online = false;
	private long lastPing;
	private volatile boolean updateText;
	public Session(){
		ping();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean loggedin) {
		this.online = loggedin;
	}
	public boolean checkOnline(){
		if(online && System.currentTimeMillis()-lastPing>60000){
			online = false;
		}
		return online;
	}
	public void ping(){
		lastPing = System.currentTimeMillis();
		online = true;
	}
	
	public synchronized void notifyX(){
		notifyAll();
	}
	
	public synchronized void update(){
			updateText = true;
			notifyAll();
	}
	public synchronized boolean waitForText(){
		ping();
		try{
//			int i = 0;
//			while(update == null && i<300){
//				Thread.sleep(100);
//				i++;
//			}
			long waitingSince = System.currentTimeMillis();
			while(!updateText&&System.currentTimeMillis()-waitingSince<30000){
				wait();
			}
		}catch(InterruptedException e){
			
		}
		boolean temp = updateText;
		updateText = false;
		return temp;
	}
}
