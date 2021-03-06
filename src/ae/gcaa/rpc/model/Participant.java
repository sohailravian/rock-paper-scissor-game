package ae.gcaa.rpc.model;

import java.io.Serializable;
import java.net.Socket;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Participant implements Serializable{
	
	private static final long serialVersionUID = -764363727531030443L;

	private String name;
	private String ip;
	private int winCount;
	private boolean quit;
	
	@JsonIgnore
	private Submission submission;
	
	@JsonIgnore
	private Socket socket;
	
	protected Participant(){};
	protected Participant(String name,String ip,Socket socket){
		this.setSocket(socket);
		this.setName(name);
		this.setIp(ip);
		this.setQuit(false);
		
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
	
	public Submission getSubmission() {
		return submission;
	}

	public void setSubmission(Submission submission) {
		this.submission = submission;
	}
	
	
	public void takeTurn(Submission submission){
		this.setSubmission(submission);
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
