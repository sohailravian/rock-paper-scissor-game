package ae.gcaa.rpc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import ae.gcaa.rpc.infrastructure.ClientServerThread;
import ae.gcaa.rpc.model.GameMode;

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
		new ClientServerThread(socket).start();
	}
	
	public static void main2(String[] args) {
		System.out.println(GameMode.validOptions());
	}
	
	
}
