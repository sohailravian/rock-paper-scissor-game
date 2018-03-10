package ae.gcaa.rpc.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import ae.gcaa.rpc.model.Game;
import ae.gcaa.rpc.model.IndividualGame;
import ae.gcaa.rpc.model.Participant;
import ae.gcaa.rpc.model.Player;

public class GameConsole {
	
	public static List<GamePooledParticipant> registeredParticipants= new ArrayList<GamePooledParticipant>();
	private static final int ONLINE_GAME_SEVER_PORT=3333;
	
	public static Participant gameStartParticipant(Participant participant,int noOfRounds){
		
		Participant pooledParticipant=null;
		for (GamePooledParticipant registeredParticipant : registeredParticipants) {
			 if(registeredParticipant.getParticipant().getClass().equals(participant.getClass()) && registeredParticipant.getNoOfRounds() == noOfRounds){
				 pooledParticipant=registeredParticipant.getParticipant();
				 //result=true;
				 break;
			 }
		}
		
		if(pooledParticipant!=null){
			registeredParticipants.remove(pooledParticipant);
		}else{
			registeredParticipants.add(new GamePooledParticipant(participant,noOfRounds));
		}
		
		return pooledParticipant;
	}
	
	
	public static class GamePooledParticipant{
		
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
	
	public static void main1(String[] args) throws IOException {
		//Game game=new Game(3,2);
		Game game=null;
	
	//	Player player1=new Player("sohail", "10.152.20.22");
		int player1Rounds= 3;
	//	gameStart(player1,player1Rounds);
		
	//	Player player2=new Player("Ali", "10.152.20.22");
		int player2Rounds= 3;
		//Team team=new Team();
		//if(!gameStart(player2,player2Rounds)){
			System.exit(0);
	//	}
		
	//	game=new IndividualGame(player2Rounds,player1,player2);
		
	//	game.play();
			
	}
	
	public static void main(String[] args) {
		ServerSocket serverSocket=null;
		try{
			serverSocket=new ServerSocket(ONLINE_GAME_SEVER_PORT);
			while(true){
				Socket socket=serverSocket.accept();
				new clientServerThread(socket).start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
				
		}
		
	}
	
	public static class clientServerThread extends Thread{
		
		private Socket socket;
		public clientServerThread(Socket socket){
			this.setSocket(socket);
		}
		
		@Override
		public void run() {
			Game game=null;
			DataInputStream datain= null; 
			DataOutputStream dataOut=null;
			
			try{
				dataOut=new DataOutputStream(socket.getOutputStream());
				
				//This is to show connecting paricipant to select game type Individual/Team
				dataOut.writeUTF(Game.gameSelectionMessage());
				dataOut.flush();
				
				// Accepting participant input for game type
				datain=new DataInputStream(socket.getInputStream());
				String gameSelection=datain.readUTF().trim();
				if(gameSelection.equalsIgnoreCase("I")){
						
						// Enter player name
						dataOut.writeUTF("Enter your name.");
						dataOut.flush();
						// Read player name
						String name=datain.readUTF();
						// Enter rouds
						dataOut.writeUTF("Enter rounds to play.");
						// Read player option for round
						int rounds= Integer.parseInt(datain.readUTF());
						String ip=(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
						
						Player player=new Player(name, ip,socket);
						Player playerTwo= (Player) gameStartParticipant(player, rounds);
						
						if(playerTwo!=null){
							game=new IndividualGame(rounds,player,playerTwo);
							game.play();
						}
						
			//			socket.close();
						
						
				}else if(gameSelection.equalsIgnoreCase("T")){
					
				}else{
					dataOut.writeUTF("Invalid option. Please try to start game with valid option.");
					dataOut.flush();
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

		public Socket getSocket() {
			return socket;
		}

		public void setSocket(Socket socket) {
			this.socket = socket;
		}
	}
	
	
}
