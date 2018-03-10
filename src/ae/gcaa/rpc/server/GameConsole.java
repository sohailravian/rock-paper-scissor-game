package ae.gcaa.rpc.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;
import ae.gcaa.rpc.model.Game;
import ae.gcaa.rpc.model.GameMode;
import ae.gcaa.rpc.model.IndividualGame;
import ae.gcaa.rpc.model.Participant;
import ae.gcaa.rpc.model.Player;

/* This class is main Gaming Console. It acts as server and client both
 * 1. In the beginning if user select server so it will start as a server
 * 2. In case user select client this will connect to server.
 * 
 * @author  Shiekh Muhammad Sohail
 * @param client mode or server mode (CLIENT , SERVER)
 * In case of CLIENT user need to enter IP address of server to connect (you can check the machine ip by using command 'ipconfig -all'). 
 */


public class GameConsole {
	
	
	/* This shared list is for creating players pool.
	*/
	
	public static volatile List<GamePooledParticipant> registeredParticipants= new ArrayList<GamePooledParticipant>();
	
	
	/* Some random port to start server at  
	*/
	private static final int ONLINE_GAME_SEVER_PORT=3333;
	
	
	
	/* This method is check/add player to players pool. Whenever some player/team connects to server it will check for matching player
	* from this pool to start game with.  
	*/
	
	public Participant gameStartParticipant(Participant participator,int noOfRounds){
		
		Participant pooledParticipant=null;
		for (GamePooledParticipant registeredParticipant : registeredParticipants) {
			 if(registeredParticipant.getParticipant().getClass().equals(participator.getClass()) && registeredParticipant.getNoOfRounds() == noOfRounds){
				 pooledParticipant=registeredParticipant.getParticipant();
				 //result=true;
				 break;
			 }
		}
		
		if(pooledParticipant!=null){
			registeredParticipants.remove(pooledParticipant);
		}else{
			registeredParticipants.add(new GamePooledParticipant(participator,noOfRounds));
		}
		
		return pooledParticipant;
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
	
	
	/* Main method to start the game. User will pas the arguments to make it client or server
	 */
	public static void main(String[] args) {
		ServerSocket serverSocket=null;
		try{
			serverSocket=new ServerSocket(ONLINE_GAME_SEVER_PORT);
			GameConsole console=new GameConsole();
			
			/* Server is up and waiting for players to join the game
			 */
			while(true){
				
				/* Server will start a new player thread
				 */
				console.startNewThread(serverSocket.accept());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{ /* In all cases need to close the server socket*/
			try{
				if(serverSocket!=null)
					serverSocket.close();
			}catch(Exception exception){
				exception.printStackTrace();
			}
		}
		
	}
	
	/* This method will start a new player/team thread
	 * @param Socket
	 */
	public void startNewThread(Socket socket){
		new clientServerThread(socket).start();
	}
	
	public static void main2(String[] args) {
		System.out.println(GameMode.validOptions());
	}
	
	/* This thread class start player/team thread
	 * @argument constructor argument is socket connection on which player/team will communicate with server  
	 */
	
	public class clientServerThread extends Thread{
		
		private Socket socket;
		public clientServerThread(Socket socket){
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
						
						// Enter player name
						dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, "********** Welcome to the Game *****************"+ "\n" +"Enter your name."));
						dataOut.flush();
						// Read player name
						String name=MessageFactory.createMessage(datain.readUTF()).getBody();
						
						// Enter rouds
						dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, "Enter rounds to play."));
						// Read player option for round
						int rounds= Integer.parseInt(MessageFactory.createMessage(datain.readUTF()).getBody());
						String ip=(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
						
						Player player=new Player(name, ip,socket);
						Player playerTwo= (Player) gameStartParticipant(player, rounds);
						
						if(playerTwo!=null){
							game=new IndividualGame(rounds,player,playerTwo);
							game.play();
						}
						
						
				}else if(gameMode.isTeam()){
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
				/*	if(datain !=null)
						datain.close();
					if(dataOut!=null)
						dataOut.close();*/
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			
		}
		
		

		/* This method will return player/team selection from user
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

		public Socket getSocket() {
			return socket;
		}

		public void setSocket(Socket socket) {
			this.socket = socket;
		}
	}
	
	
}
