package ae.gcaa.rpc.model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Team extends Participant {
	
	private static final long serialVersionUID = -403097635755019382L;
	private List<Player> players;
	private int size;
		
	public Team(String name,String ip,Socket socket) {
		super(name,ip,socket);
		this.size=1;
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
