package ae.gcaa.rpc.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import ae.gcaa.rpc.infrastructure.Message;
import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;
import ae.gcaa.rpc.model.Player;
import ae.gcaa.rpc.model.Submission;

public class Client{
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
			
			//String ip=(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
			Player participant=new Player(name, ONLINE_GAME_SEVER_IP, socket);
			Message readMessage=null;
		//	String clientInput=
			
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
}
