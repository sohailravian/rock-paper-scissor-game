package ae.gcaa.rpc.infrastructure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ae.gcaa.rpc.model.Confirmation;
import ae.gcaa.rpc.model.Game;
import ae.gcaa.rpc.model.GameMode;
import ae.gcaa.rpc.model.IndividualGame;
import ae.gcaa.rpc.model.Participant;
import ae.gcaa.rpc.model.Player;
import ae.gcaa.rpc.model.Team;
import ae.gcaa.rpc.model.TeamGame;
import ae.gcaa.rpc.model.Utils;

/* This thread class start player/team thread
 * @argument constructor argument is socket connection on which player/team will communicate with server  
 */

public class ClientServerThread extends Thread{
	
	/* This shared list is for creating players pool.
	*/
	
	public static volatile List<GamePooledParticipant> registeredParticipants= new ArrayList<GamePooledParticipant>();
	
	private static volatile List<GamePooledParticipant> championshipParticipant= new ArrayList<GamePooledParticipant>();
	
	
	private Socket socket;
	public ClientServerThread(Socket socket){
		this.setSocket(socket);
	}
	
	
	/* This method will be called whenever new player/team will be connected to server
	 */
	@Override
	public void run() {
		Game game=null;
		DataInputStream datain= null; 
		DataOutputStream dataOut=null;
		
		try{
			
			// Server will use this so write message/s to player/team
			dataOut=new DataOutputStream(socket.getOutputStream());
			datain=new DataInputStream(socket.getInputStream());
			
			/* This method will return player/team selection from user
			 */
			GameMode gameMode=gameModeSelection(dataOut, datain);
			
			if(gameMode.isIndividual()){
					
				startIndividualGame(dataOut, datain, game);
					
			}else if(gameMode.isTeam()){
				startTeamGame(dataOut, datain, game);
			}
		}catch(Exception e){
			e.printStackTrace();
		}			
	}
	
			 
	/**
	 * This method will return player/team selection from user 
	 * @param dataOut
	 * @param datain
	 * @param game
	 * @return GameMode
	 * @throws Exception
	 */
 
	private GameMode gameModeSelection(DataOutputStream dataOut,DataInputStream datain) throws Exception{
		
		//This is to show connecting participant to select game type Individual/Team
		dataOut.writeUTF(Game.gameSelectionMessage());
		dataOut.flush();
		
		// Accepting participant input for game type
		String gameSelection=datain.readUTF().trim().toUpperCase();
		
		/* User can select one of the options
		 * I -> individual
		 * T -> Team 
		 * keep on asking correct information
		*/
		
		while(!GameMode.isValidGameMode(gameSelection)){
			dataOut.writeUTF(MessageFactory.createMessage(MessageType.INVALID, null, " Please select valid options "+ GameMode.validOptions()));
			gameSelection=MessageFactory.createMessage(datain.readUTF()).getBody().toUpperCase();
		}
		
		return GameMode.gameModeOfValue(gameSelection);
	}
	
	
	/**
	 * This method will kick start the game amongst individual players 
	 * @param dataOut
	 * @param datain
	 * @param game
	 * @throws Exception
	 */
	
	private void startIndividualGame(DataOutputStream dataOut, DataInputStream datain,Game game) throws Exception{
		
		/* Enter player name 
		 */
		dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, Utils.stringMessageBuilder(IndividualGame.WELCOME_TO_GAME + IndividualGame.NEW_LINE + IndividualGame.ENTER_NAME)));
		dataOut.flush();
		
		/* Read player name 
	    */
		String name=MessageFactory.createMessage(datain.readUTF()).getBody();
		
		/* Enter rounds
		 */		
		dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, Utils.stringMessageBuilder(IndividualGame.ENTER_ROUNDS)));
		
		
		/* Read player option for round 
		*/
		int rounds= Integer.parseInt(MessageFactory.createMessage(datain.readUTF()).getBody());
		String ip=(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
		
		Player player=new Player(name, ip,socket);
		
		/* Start the game amongst two player having same rounds and game type (individual)
		 */
		
		Player playerTwo= (Player) gameStartParticipant(player, rounds);
		if(playerTwo!=null){
			game=new IndividualGame(rounds,player,playerTwo);
			game.play();
		}
		
	}
	
	/**
	 * This method will kick start the game amongst individual players 
	 * @param dataOut
	 * @param datain
	 * @param game
	 * @throws Exception
	 */
	
	private void startTeamGame(DataOutputStream dataOut, DataInputStream datain,Game game) throws Exception{
		
		/* Enter player name 
		 */
		dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, Utils.stringMessageBuilder(TeamGame.WELCOME_TO_GAME + TeamGame.NEW_LINE + TeamGame.ENTER_NAME)));
		dataOut.flush();
		
		/* Read Team name 
	    */
		String name=MessageFactory.createMessage(datain.readUTF()).getBody();
		
		/* Enter rounds
		 */		
		dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, Utils.stringMessageBuilder(TeamGame.ENTER_ROUNDS)));
		dataOut.flush();
		
		/* Read Team option for round 
		*/
		int rounds= Integer.parseInt(MessageFactory.createMessage(datain.readUTF()).getBody());
	
		
		/* asking for championship
		 */		
		dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, Utils.stringMessageBuilder(TeamGame.CHAMPIONSHIP_CONFIRMATION,Confirmation.validOptions())));
		dataOut.flush();
		
		String confirmation= MessageFactory.createMessage(datain.readUTF()).getBody().toUpperCase();
		while(!Confirmation.isValidOption(confirmation)){
			dataOut.writeUTF(MessageFactory.createMessage(MessageType.INVALID, null, Utils.stringMessageBuilder(" Please select valid options ", GameMode.validOptions())));
			dataOut.flush();
			confirmation=MessageFactory.createMessage(datain.readUTF()).getBody().toUpperCase();
		}
		
		
		String ip=(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
		
		
		Team team=new Team(name, ip,socket);
		/* Start the game amongst two teams having same rounds and game type (Team)
		*/
		
		Team teamTwo=null;
		
		if(Confirmation.confirmationOfValue(confirmation).isYes()){
			team.setInChampionship(true);
			teamTwo=(Team) gameStartParticipantForChampionship(team, rounds);
		}else{
			teamTwo= (Team) gameStartParticipant(team, rounds);
		}
				
		if(teamTwo!=null){
			game=new TeamGame(rounds,team,teamTwo);
			game.play();
		}
		
	}
	
	
	
	/*	This method is check/add player to players pool. Whenever some player/team connects to server it will check for matching player
	* from this pool to start game with.  
	*/
	
	private Participant gameStartParticipant(Participant participator,int noOfRounds){
		
		GamePooledParticipant pooledParticipant=null;
		for (GamePooledParticipant registeredParticipant : registeredParticipants) {
			 if(registeredParticipant.getParticipant().getClass().equals(participator.getClass()) && registeredParticipant.getNoOfRounds() == noOfRounds){
				 pooledParticipant=registeredParticipant;
				 //result=true;
				 break;
			 }
		}
		
		if(pooledParticipant!=null){
			registeredParticipants.remove(pooledParticipant);
		}else{
			registeredParticipants.add(new GamePooledParticipant(participator,noOfRounds));
			return null;
		}
		
		return pooledParticipant.getParticipant();
	}
	
	/*	This method is check/add player to players pool. Whenever some player/team connects to server it will check for matching player
	* from this pool to start game with.  
	*/
	
	private Participant gameStartParticipantForChampionship(Participant participator,int noOfRounds){
		
		GamePooledParticipant pooledParticipant=null;
		for (GamePooledParticipant registeredParticipant : championshipParticipant) {
			 if(registeredParticipant.getParticipant().getClass().equals(participator.getClass()) && registeredParticipant.getNoOfRounds() == noOfRounds){
				 pooledParticipant=registeredParticipant;
				 //result=true;
				 break;
			 }
		}
		
		if(pooledParticipant!=null){
			championshipParticipant.remove(pooledParticipant);
		}else{
			championshipParticipant.add(new GamePooledParticipant(participator,noOfRounds));
			return null;
		}
		
		return pooledParticipant.getParticipant();
	}
	
	/* Class for creating pooled participant on the fly
	 */
	
	public class GamePooledParticipant{
		
		private Participant participant;
		private int noOfRounds;
		public GamePooledParticipant(Participant participant,int noOfRounds){
			this.setParticipant(participant);
			this.setNoOfRounds(noOfRounds);
		}		
		public int getNoOfRounds() {
			return noOfRounds;
		}
		public void setNoOfRounds(int noOfRounds) {
			this.noOfRounds = noOfRounds;
		}
		public Participant getParticipant() {
			return participant;
		}
		public void setParticipant(Participant participant) {
			this.participant = participant;
		}
		
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}