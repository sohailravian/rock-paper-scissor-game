package ae.gcaa.rpc.model;

import java.net.Socket;

public class Player extends Participant {
	
	private static final long serialVersionUID = -8126232631530147375L;
	
	public Player(String name, String ip,Socket socket){
		super(name,ip,socket);
		this.setWinCount(0);
		this.setSubmission(Submission.UNKNOWN);
	}
	

}
