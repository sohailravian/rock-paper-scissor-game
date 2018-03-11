package ae.gcaa.rpc.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import ae.gcaa.rpc.infrastructure.ClientServerThread;
import ae.gcaa.rpc.infrastructure.Message;
import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;
import ae.gcaa.rpc.model.GameMode;
import ae.gcaa.rpc.model.Player;
import ae.gcaa.rpc.model.Submission;

/* This class is main Gaming Console. It acts as server and client both
 * 1. In the beginning if user select server so it will start as a server
 * 2. In case user select client this will connect to server.
 * 
 * @author  Shiekh Muhammad Sohail
 * @param client mode or server mode (CLIENT , SERVER)
 * In case of CLIENT user need to enter IP address of server to connect (you can check the machine ip by using command 'ipconfig -all'). 
 */


public class GameConsole {
	
	/* Some random port to start server at  
	*/
	private static final int ONLINE_GAME_SEVER_PORT=3333;
	
	/* Main method to start the game. User will pas the arguments to make it client or server
	 */
	public static void main(String[] args) {
		if(args.length<3){
			System.out.println("Please provide following argument \n 1. game mode \n 2. your machine ip \n 3. server ip to connect ");
			System.exit(0);
		}else{
			String gameMode=args[0];
			String clientIP=args[1];
			String serverIP=args[2];
			if(gameMode.equalsIgnoreCase("S")){
				serverMode();
			}else{
				clietMode(clientIP, serverIP);
			}
		}
		
	}
	
	
	public static void serverMode(){
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
	
	public static void clietMode(String clientIP,String serverIP){
	
		
		Socket socket=null;
		DataInputStream dataIn=null;
		DataOutputStream dataOut=null;
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
	
		String name="";
		
		try {
			socket = new Socket(serverIP,ONLINE_GAME_SEVER_PORT);
			dataIn=new DataInputStream(socket.getInputStream());  
			dataOut=new DataOutputStream(socket.getOutputStream());  
			
			System.out.println(dataIn.readUTF());
			
			// Player input for game type
		
			// Write player selection for game type
			String gameType=br.readLine();
			dataOut.writeUTF(gameType);
			dataOut.flush();
			
			
			// Plyer name or invalid selection message
			Message kickStartMessage= MessageFactory.createMessage(dataIn.readUTF());
			while(kickStartMessage.getType().isInvalid()){
				System.out.println(kickStartMessage.getBody());
				dataOut.writeUTF(MessageFactory.createMessage(MessageType.READ, null, br.readLine()));
				kickStartMessage=MessageFactory.createMessage(dataIn.readUTF());
			}
			
			System.out.println(kickStartMessage.getBody());
			name=br.readLine();
			dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, name));
			dataOut.flush();
		
			// Game round
			Message gameRounds= MessageFactory.createMessage(dataIn.readUTF());
			System.out.println(gameRounds.getBody());
			String input=br.readLine();
			dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, input));
			dataOut.flush();
			
			Player participant=new Player(name, clientIP, socket);
			Message readMessage=null;
				while(true){
					System.out.println("Waiting...");
					String message=dataIn.readUTF();
					readMessage=MessageFactory.createMessage(message);
					if( input.equalsIgnoreCase(Submission.QUIT.name()) || readMessage.getType().isWin() || readMessage.getType().isLose() || readMessage.getType().isDraw()){
						System.out.println(readMessage.getBody());
						break;
					}				
					
					else if(readMessage.getType().isDisplay()){
						System.out.println(readMessage.getBody());
					}		//check if message is for same client
					else if(readMessage.getParticipant() !=null && readMessage.getParticipant().equals(participant)){
						
						if(readMessage.getType().isWrite()){
							System.out.println(readMessage.getBody());
							input=br.readLine();
							dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, participant, input));
						}else if(readMessage.getType().isRead() || readMessage.getType().isDisplay()){
							System.out.println(readMessage.getBody());
						}
					} // end of if for same user check
				}
			
		//	
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
				try {
					if(dataIn!=null)
						dataIn.close();
					if(dataOut!=null)
						dataOut.close();
					if(br!=null)
						br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
		}
		  
		
	}
	
	
	/* This method will start a new player/team thread
	 * @param Socket
	 */
	public void startNewThread(Socket socket){
		new ClientServerThread(socket).start();
	}
	
	public static void main2(String[] args) {
		System.out.println(GameMode.validOptions());
	}
	
	
}
