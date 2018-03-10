package ae.gcaa.rpc.model;

import java.net.Socket;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Player extends Participant {
	
	private static final long serialVersionUID = -8126232631530147375L;
	
	@JsonIgnore
	private Submission submission;
	
	public Player(String name, String ip,Socket socket){
		super(name,ip,socket);
		this.setSubmission(Submission.ROCK);
		this.setWinCount(0);
	}
	
	public void takeTurn(Submission submission){
		this.setSubmission(submission);
	}
	
	public Submission getSubmission() {
		return submission;
	}

	public void setSubmission(Submission submission) {
		this.submission = submission;
	}


}
