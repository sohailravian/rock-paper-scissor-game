package ae.gcaa.rpc.model;

import java.net.Socket;

public class Team extends Participant {
	
	private static final long serialVersionUID = -403097635755019382L;
	
	public Team(String name, String ip,Socket socket){
		super(name,ip,socket);
		this.setWinCount(0);
		this.setSubmission(Submission.UNKNOWN);
	}
	
}
