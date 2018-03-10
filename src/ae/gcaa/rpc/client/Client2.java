package ae.gcaa.rpc.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import ae.gcaa.rpc.infrastructure.Message;
import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;
import ae.gcaa.rpc.model.Player;

public class Client2{
	private static final int ONLINE_GAME_SEVER_PORT=3333;
	private static final String ONLINE_GAME_SEVER_IP="127.0.0.1";
	
	public static void main(String[] args) {
		Socket socket=null;
		DataInputStream dataIn=null;
		DataOutputStream dataOut=null;
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
	
		String name="";
		
		try {
			socket = new Socket(ONLINE_GAME_SEVER_IP,ONLINE_GAME_SEVER_PORT);
			dataIn=new DataInputStream(socket.getInputStream());  
			dataOut=new DataOutputStream(socket.getOutputStream());  
			
			System.out.println(dataIn.readUTF());
			
			// Player input for game type
		
			// Write player selection for game type
			String gameType=br.readLine();
			dataOut.writeUTF(gameType);
			dataOut.flush();
			
			// Plyer name
			System.out.println(dataIn.readUTF());
			name=br.readLine();
			dataOut.writeUTF(name);
			dataOut.flush();
		
			// Game round
			System.out.println(dataIn.readUTF());
			String input=br.readLine();
			dataOut.writeUTF(input);
			dataOut.flush();
			
			String ip=(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
			Player participant=new Player(name, ONLINE_GAME_SEVER_IP, socket);
			Message readMessage=null;
		//	String clientInput=
			
			while(!input.equalsIgnoreCase("quit")){
				System.out.println("Waiting...");
				String message=dataIn.readUTF();
				readMessage=MessageFactory.createMessage(message);
				if(readMessage.getType().isWin() && readMessage.getType().isLose() && readMessage.getType().isDraw()){
					break;
				}
				
				//check if message is for same client
				if(readMessage.getParticipant().equals(participant)){
					
					if(readMessage.getType().isWrite()){
						System.out.println(readMessage.getBody());
						input=br.readLine();
						dataOut.writeUTF(MessageFactory.createMessage(MessageType.WRITE, participant, input));
					}else if(readMessage.getType().isRead() || readMessage.getType().isDisplay()){
						System.out.println(readMessage.getBody());
						//dataOut.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, participant, "Message has been red"));
					}
				} // end of if for same user check
				
				
			}
			
			System.out.println(readMessage.getBody());
			
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
}
