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
import ae.gcaa.rpc.model.Team;
import static ae.gcaa.rpc.model.Utils.*;

/* This class is main Gaming Console. It acts as server and client both
 * 1. In the beginning if user select server so it will start as a server
 * 2. In case user select client this will connect to server.
 * 
 * @author  Sheikh Muhammad Sohail
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
			printMessageToConsole("Please provide following argument \n 1. game mode \n 2. your machine ip \n 3. server ip to connect ");
			System.exit(0);
		}else{
			String gameMode=args[0];
			String clientIP=args[1];
			String serverIP=args[2];
			
			/* starting main class with client or server. This will fork the behavior.
			 */
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
			
			/* Server is up and waiting for connections
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
	
		try {
			socket = new Socket(serverIP,ONLINE_GAME_SEVER_PORT);
			dataIn=new DataInputStream(socket.getInputStream());  
			dataOut=new DataOutputStream(socket.getOutputStream());  
			
			printMessageToConsole(dataIn.readUTF());
			
			// Write player selection for game type
			String gameType=br.readLine();
			dataOut.writeUTF(gameType);
			dataOut.flush();
			
			// Player name or invalid selection message
			Message gameSelection= MessageFactory.createMessage(dataIn.readUTF());
				
			while(gameSelection.getType().isInvalid()){
				
				printMessageToConsole(gameSelection.getBody());
				gameType=br.readLine();
				dataOut.writeUTF(MessageFactory.createMessage(MessageType.READ, null, gameType));
				gameSelection=MessageFactory.createMessage(dataIn.readUTF());
		
			}
			
			printMessageToConsole(gameSelection.getBody());
			
			if(GameMode.gameModeOfValue(gameType).isIndividual()){
				individualPlayerClient(clientIP,socket,dataIn,dataOut,br);
			}else{
				teamPlayerClient( clientIP, socket, dataIn, dataOut, br);
			}
		
		}catch(Exception exception){
			exception.printStackTrace();
		}	
		
		
	}
	
	
	public static void individualPlayerClient(String clientIP,Socket socket,DataInputStream dataIn,DataOutputStream dataOut,BufferedReader br){
		try {
			
			String name=br.readLine();
			dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, name));
			dataOut.flush();
		
			// Game rounds
			Message gameRounds= MessageFactory.createMessage(dataIn.readUTF());
			System.out.println(gameRounds.getBody());
			String input=br.readLine();
			dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, input));
			dataOut.flush();
			
			Player participant=new Player(name, clientIP, socket);
			Message readMessage=null;
				
			while(true){
					
					printMessageToConsole("Waiting...");
					String message=dataIn.readUTF();
					readMessage=MessageFactory.createMessage(message);
					
					if(input.equalsIgnoreCase(Submission.QUIT.name()) || readMessage.getType().isWin() || readMessage.getType().isLose() || readMessage.getType().isDraw()){
						printMessageToConsole(readMessage.getBody());
						break;
					}
					
					else if(readMessage.getType().isDisplay()){
							printMessageToConsole(readMessage.getBody());
					}
					
					//check if message is for same client
					else if(readMessage.getParticipant() !=null && readMessage.getParticipant().equals(participant)){
						
							if(readMessage.getType().isWrite()){
								printMessageToConsole(readMessage.getBody());
								input=br.readLine();
								dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, participant, input));
							}else if(readMessage.getType().isRead() || readMessage.getType().isDisplay()){
								printMessageToConsole(readMessage.getBody());
							}
					} // end of if for same user check
				}
			
		//	
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
				try {
					if(dataIn!=null)
						dataIn.close();
					if(dataOut!=null)
						dataOut.close();
					if(br!=null)
						br.close();
					if(socket!=null)
						socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
		}
	}
	
	public static void teamPlayerClient(String clientIP,Socket socket,DataInputStream dataIn,DataOutputStream dataOut,BufferedReader br){
		try {
			
			// taking input for name
			String name=br.readLine();
			dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, name));
			dataOut.flush();
		
			// taking input for name rounds
			Message gameRounds= MessageFactory.createMessage(dataIn.readUTF());
			printMessageToConsole(gameRounds.getBody());
			String input=br.readLine();
			dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, input));
			dataOut.flush();
			
			// Championship participation confirmation
			
			Message champioshionConfirmationMsg= MessageFactory.createMessage(dataIn.readUTF());
			printMessageToConsole(champioshionConfirmationMsg.getBody());
			dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, null, br.readLine()));
			dataOut.flush();
			
			
			while(champioshionConfirmationMsg.getType().isInvalid()){
				printMessageToConsole(champioshionConfirmationMsg.getBody());
				dataOut.writeUTF(MessageFactory.createMessage(MessageType.READ, null, br.readLine()));
				champioshionConfirmationMsg=MessageFactory.createMessage(dataIn.readUTF());
			}
			
			Team participant=new Team(name, clientIP, socket);
			Message readMessage=null;
				while(true){
					printMessageToConsole("Waiting...");
					String message=dataIn.readUTF();
					readMessage=MessageFactory.createMessage(message);
					if( input.equalsIgnoreCase(Submission.QUIT.name()) || readMessage.getType().isWin() || readMessage.getType().isLose() || readMessage.getType().isDraw()){
						printMessageToConsole(readMessage.getBody());
						break;
					}				
					
					else if(readMessage.getType().isDisplay()){
						printMessageToConsole(readMessage.getBody());
					}		//check if message is for same client
					else if(readMessage.getParticipant() !=null && readMessage.getParticipant().equals(participant)){
						
						if(readMessage.getType().isWrite()){
							printMessageToConsole(readMessage.getBody());
							input=br.readLine();
							dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, participant, input));
						}else if(readMessage.getType().isRead() || readMessage.getType().isDisplay()){
							printMessageToConsole(readMessage.getBody());
						}
					} // end of if for same user check
				}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		/* In any case close the buffered reader, socket, input and output stream
		 */
		finally{
				try {
					if(dataIn!=null)
						dataIn.close();
					if(dataOut!=null)
						dataOut.close();
					if(socket!=null)
						socket.close();
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
	
	
}
