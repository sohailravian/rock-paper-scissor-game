package ae.gcaa.rpc.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class IndividualGame extends Game {
	
	private Player playerOne;
	private Player playerTwo;

	public IndividualGame(int rounds,Player playerOne,Player playerTwo){
		super(rounds);
		this.setPlayerOne(playerOne);
		this.setPlayerTwo(playerTwo);
	}
	
	@Override
	public void nextRound(Round round){
		if(currentRoundTied(round)){
			getRounds().add(round);
		}
	}
	
	@Override
	public Participant championOfAllRounds(){
		if(getPlayerOne().getWinCount() > getPlayerTwo().getWinCount())
			return getPlayerOne();
		else if(getPlayerOne().getWinCount() < getPlayerTwo().getWinCount())
			return getPlayerTwo();
		return null;
	}
	
	public void play() throws IOException {
		super.play();
	
	BufferedReader br = null;
	
	DataOutputStream playerOneOutput=new DataOutputStream(this.getPlayerOne().getSocket().getOutputStream());
	DataInputStream playerOneInput=new DataInputStream(this.getPlayerOne().getSocket().getInputStream());
	DataOutputStream playerTwoOutput=new DataOutputStream(this.getPlayerTwo().getSocket().getOutputStream());;
	DataInputStream playerTwoInput=new DataInputStream(this.getPlayerTwo().getSocket().getInputStream());;
	
	
	playerOneOutput.writeUTF(gameInstructionMessage());
	playerOneOutput.flush();
	playerTwoOutput.writeUTF(gameInstructionMessage());
	playerTwoOutput.flush();
	//System.exit(0);
	//System.out.println(gameInstructionMessage());
	
	
	try{
		while(!roundsCompleted()){
			
			IndividualRound round= new IndividualRound();
			playerTwoOutput.writeUTF(playerOne.getName() + "'s Turn ");
			playerTwoOutput.flush();
			playerOneOutput.writeUTF((submissionOptionMessage()));
			playerOneOutput.flush();
			
			String playerOneSubmission = playerOneInput.readUTF(); // Reading from System.in
			//String input=br.readLine();
			if(!Submission.isQuited(playerOneSubmission)){
				playerOne.setQuit(true);
				break;
			}
			
			while(!Submission.isValidSubmission(playerOneSubmission)){
			//	System.out.println("not a valid option try again.");
				playerOneOutput.writeUTF("not a valid option try again.");
				playerOneSubmission=playerOneInput.readUTF(); // Reading from System.in;
			}
			playerOne.takeTurn(Submission.valueOf(playerOneSubmission.toUpperCase().trim()));
			
			
			
			playerOneOutput.writeUTF(playerOne.getName() + "'s Turn ");
			playerOneOutput.flush();
			playerTwoOutput.writeUTF((submissionOptionMessage()));
			playerTwoOutput.flush();
			
			String playerTwoSubmission = playerTwoInput.readUTF();
			//String input2=br.readLine();
			
			if(!Submission.isQuited(playerTwoSubmission)){
				playerTwo.setQuit(true);
			//	System.out.println(playerOne.getName()+ " won the game." );
				break;
			}
			while(!Submission.isValidSubmission(playerTwoSubmission)){
				playerTwoOutput.writeUTF("not a valid option try again.");
				playerTwoSubmission=playerTwoInput.readUTF();
			}
			
			playerTwo.takeTurn(Submission.valueOf(playerTwoSubmission.toUpperCase().trim()));
			nextRound(round);
			
		}
		
		if(playerOne.isQuit() || playerTwo.isQuit()){
			announceWinnerInCaseofQuit();
		}else{
			finish();
		}
		//finish(So);
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("Game end abruptly. Please try to play a new game.");
	}
	finally{
		playerOneInput.close();
		playerOneOutput.close();
		playerTwoInput.close();
		playerTwoOutput.close();
		
		playerOne.getSocket().close();
		playerTwo.getSocket().close();

		}
		
	};
	
	/*@Override
	public void finish(Socket socket) throws IOException {
		super.finish(socket);
		System.out.println("The Winner Is : " +  ((Player)championOfAllRounds()).getName() + "       :) ");
	}*/
	
	@Override
	protected void announceWinnerInCaseofQuit() {
		try{
			if(getPlayerOne().isQuit()){
				((DataOutputStream)getPlayerOne().getSocket().getOutputStream()).writeUTF("Winner is "+ playerTwo.getName());
				((DataOutputStream)getPlayerTwo().getSocket().getOutputStream()).writeUTF("Winner is "+ playerTwo.getName());
			}if(getPlayerTwo().isQuit()){
				((DataOutputStream)getPlayerOne().getSocket().getOutputStream()).writeUTF("Winner is "+ playerTwo.getName());
				((DataOutputStream)getPlayerTwo().getSocket().getOutputStream()).writeUTF("Winner is "+ playerTwo.getName());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public String gameInstructionMessage(){
		StringBuilder builder=new StringBuilder();
		builder.append("************************************************* INDIVIDUAL GAME *****************************************************").append('\n')
			   .append("**************** There are four options to select while playing the game and these are listed below *******************").append('\n')
			   .append("1. ROCK").append('\n')
			   .append("2. PAPER").append('\n')
			   .append("3. SCISSOR").append('\n')
			   .append("4. QUIT\\Q").append('\n')
			   .append("*************** By Entering QUIT/Q player will be quiting the game and other player wil win.");
		return builder.toString(); 
	}
	
	public Player getPlayerOne() {
		return playerOne;
	}

	public void setPlayerOne(Player playerOne) {
		this.playerOne = playerOne;
	}

	public Player getPlayerTwo() {
		return playerTwo;
	}

	public void setPlayerTwo(Player playerTwo) {
		this.playerTwo = playerTwo;
	}


}
