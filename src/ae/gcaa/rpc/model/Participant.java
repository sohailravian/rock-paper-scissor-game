package ae.gcaa.rpc.model;

import java.net.Socket;

public class Participant {
	
	protected int winCount;
	protected boolean quit=false;
	protected Socket socket;
	protected Participant(Socket socket){
		this.setSocket(socket);
	}
	
	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public boolean isQuit() {
		return quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	
}
