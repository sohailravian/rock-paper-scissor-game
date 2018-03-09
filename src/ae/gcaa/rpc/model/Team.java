package ae.gcaa.rpc.model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.MessageContext.Scope;

public class Team extends Participant {
	
	private List<Player> players;
	//private State teamState;
	private int size;
		
	public Team(Socket socket) {
		super(socket);
		this.size=2;
		this.players=new ArrayList<Player>();
	}
	
	public void addPlayer(Player player){
		
		if(this.getPlayers().size() >= size)
			throw new IllegalArgumentException("no more player can be added to team");
		
		this.getPlayers().add(player);
	} 
	
	public List<Player> getPlayers() {
		return players;
	}
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	/*public State getTeamState() {
		return teamState;
	}
	public void setTeamState(State teamState) {
		this.teamState = teamState;
	}*/
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}
