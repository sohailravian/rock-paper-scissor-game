package ae.gcaa.rpc.model;

import java.io.BufferedReader;
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
	
	public void play(Socket socket) throws IOException {
		super.play();
	
	BufferedReader br = null;
	System.out.println(gameInstructionMessage());
	try{
		while(roundsCompleted()){
			
			IndividualRound round= new IndividualRound();
			
			System.out.println(playerOne.getName() + "'s Turn ");
			System.out.println(submissionOptionMessage());
			br = new BufferedReader(new InputStreamReader(System.in)); // Reading from System.in
			String input=br.readLine();
			if(!Submission.isQuited(input)){
				playerOne.setQuit(true);
				System.out.println(playerTwo.getName()+ " won the game." );
				break;
			}
			
			while(!Submission.isValidSubmission(input)){
				System.out.println("not a valid option try again.");
				input=br.readLine();
			}
			playerOne.takeTurn(Submission.valueOf(input.toUpperCase().trim()));
			
			System.out.println(playerTwo.getName() + "'s Turn ");
			System.out.println(this.submissionOptionMessage());
			br = new BufferedReader(new InputStreamReader(System.in));
			String input2=br.readLine();
			
			if(!Submission.isQuited(input2)){
				playerTwo.setQuit(true);
				System.out.println(playerOne.getName()+ " won the game." );
				break;
			}
			while(!Submission.isValidSubmission(input2)){
				System.out.println("not a valid option try again.");
				input2=br.readLine();
			}
			playerTwo.takeTurn(Submission.valueOf(input2.toUpperCase().trim()));
			
			nextRound(round);
			
		}
		
		//finish(So);
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("Game end abruptly. Please try to play a new game.");
	}
	finally{
			if(br!=null)
				br.close();
		}
		
	};
	
	/*@Override
	public void finish(Socket socket) throws IOException {
		super.finish(socket);
		System.out.println("The Winner Is : " +  ((Player)championOfAllRounds()).getName() + "       :) ");
	}*/
	
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
