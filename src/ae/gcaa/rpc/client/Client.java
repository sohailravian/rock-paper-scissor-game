package ae.gcaa.rpc.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
	private static final int ONLINE_GAME_SEVER_PORT=3333;
	private static final String ONLINE_GAME_SEVER_IP="localhost";
	
	public static void main(String[] args) {
		Socket socket=null;
		DataInputStream dataIn=null;
		DataOutputStream dataOut=null;
		BufferedReader br=null;
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
			dataOut.writeUTF(br.readLine());
			dataOut.flush();
		
			// Game round
			System.out.println(dataIn.readUTF());
			br=new BufferedReader(new InputStreamReader(System.in));
			dataOut.writeUTF(br.readLine());
			dataOut.flush();
			
			String serverMsgs= dataIn.readUTF();
			System.out.println(serverMsgs);
			
			
			while(!br.readLine().equalsIgnoreCase("QUIT") || !serverMsgs.equals("1")){
				
				serverMsgs=dataIn.readUTF();
				System.out.println(serverMsgs);
				
				br=new BufferedReader(new InputStreamReader(System.in));
				dataOut.writeUTF(br.readLine());
				dataOut.flush();
				
			}
			
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
