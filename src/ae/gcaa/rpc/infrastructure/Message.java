package ae.gcaa.rpc.infrastructure;

import java.io.Serializable;
import ae.gcaa.rpc.model.Participant;

public class Message implements Serializable {

	private static final long serialVersionUID = 8674918511930494619L;

	private Participant participant;
	private String body;
	private MessageType type;
	
	protected Message(){};
	
	public Message(Participant participant,MessageType type){
		this.setParticipant(participant);
		this.setType(type);
		//this.setCommand(command);
	}
	
	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
}
