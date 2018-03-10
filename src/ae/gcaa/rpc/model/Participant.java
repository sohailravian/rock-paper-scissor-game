package ae.gcaa.rpc.model;

import java.io.Serializable;
import java.net.Socket;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Participant implements Serializable{
	
	private static final long serialVersionUID = -764363727531030443L;

	protected String name;
	protected String ip;

	protected int winCount;
	protected boolean quit=false;
	
	@JsonIgnore
	protected Socket socket;
	protected Participant(){};
	protected Participant(String name,String ip,Socket socket){
		this.setSocket(socket);
		this.setName(name);
		this.setIp(ip);
		
	}
	
	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public boolean isQuit() {
		return quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	
	public static void main(String[] args) {
		Participant player1=new Player("soh","127.0.0.1",null);
		Player player2=new Player("soh","127.0.0.1",null);
		System.out.println(player1.equals(player2));
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Participant))
			return false;
		Participant other = (Participant) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	
	
}
