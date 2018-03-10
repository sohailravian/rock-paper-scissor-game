package ae.gcaa.rpc.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import ae.gcaa.rpc.infrastructure.Message;
import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;
import ae.gcaa.rpc.model.Participant;
import ae.gcaa.rpc.model.Player;

public class Client2 {
	private static final int ONLINE_GAME_SEVER_PORT=3333;
	private static final String ONLINE_GAME_SEVER_IP="localhost";
	
	public static void main(String[] args) {
		Socket socket=null;
		DataInputStream dataIn=null;
		DataOutputStream dataOut=null;
		BufferedReader br=null;
		
		String name="";
	//	Socket openingSocket=null;
		
		try {
			socket = new Socket(ONLINE_GAME_SEVER_IP,ONLINE_GAME_SEVER_PORT);
			dataIn=new DataInputStream(socket.getInputStream());  
			dataOut=new DataOutputStream(socket.getOutputStream());  
			
			System.out.println(dataIn.readUTF());
			
			// Player input for game type
			br=new BufferedReader(new InputStreamReader(System.in));  
			
			// Write player selection for game type
			String gameType=br.readLine();
			dataOut.writeUTF(gameType);
			dataOut.flush();
			
			// Plyer name
			System.out.println(dataIn.readUTF());
			br=new BufferedReader(new InputStreamReader(System.in));
			name=br.readLine();
			dataOut.writeUTF(name);
			dataOut.flush();
		
			// Game round
			System.out.println(dataIn.readUTF());
			br=new BufferedReader(new InputStreamReader(System.in));
			dataOut.writeUTF(br.readLine());
			dataOut.flush();
			
			
			Player participant=new Player(name, ONLINE_GAME_SEVER_IP, socket);
			
			Message readMessage=MessageFactory.createMessage(dataIn.readUTF());
			while(!readMessage.getType().isWin() && !readMessage.getType().isLose() 
												 && !readMessage.getType().isDraw() &&  !br.readLine().equalsIgnoreCase("quit")){
				if(readMessage.getParticipant().equals(participant)){
					if(readMessage.getType().isWrite()){
						System.out.println(readMessage.getBody());
						//br=new BufferedReader(new InputStreamReader(System.in));
						dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, participant, br.readLine()));
					}else if(readMessage.getType().isRead() && readMessage.getType().isDisplay()){
						System.out.println(readMessage.getBody());
					}
				} // end of if for same user check
				
				
			}
			
			System.out.println(readMessage.getBody());
			
			
			
			
		/*	String serverMsgs= dataIn.readUTF();
			System.out.println(serverMsgs);
			
			
			while(!br.readLine().equalsIgnoreCase("QUIT") && !serverMsgs.contains("Winner is")){
				
				serverMsgs=dataIn.readUTF();
				System.out.println(serverMsgs);
				
				br=new BufferedReader(new InputStreamReader(System.in));
				dataOut.writeUTF(br.readLine());
				dataOut.flush();
				
			}*/
			
			//socket.close();
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
				try {
					/*if(dataIn!=null)
						dataIn.close();
					if(dataOut!=null)
						dataOut.close();
					if(br!=null)
						br.close();*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			
		}
		  
		
	}
}
