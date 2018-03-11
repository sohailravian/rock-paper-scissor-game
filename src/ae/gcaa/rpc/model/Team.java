package ae.gcaa.rpc.model;

import java.net.Socket;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Team extends Participant {
	
	private static final long serialVersionUID = -403097635755019382L;
	
	@JsonIgnore
	private boolean inChampionship;
	
	protected Team(){}
	public Team(String name, String ip,Socket socket){
		super(name,ip,socket);
		this.setWinCount(0);
		this.setSubmission(Submission.UNKNOWN);
		this.setInChampionship(false);
	}

	public boolean isInChampionship() {
		return inChampionship;
	}
	public void setInChampionship(boolean inChampionship) {
		this.inChampionship = inChampionship;
	}
}
