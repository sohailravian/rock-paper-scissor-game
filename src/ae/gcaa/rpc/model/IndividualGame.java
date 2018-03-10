package ae.gcaa.rpc.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ae.gcaa.rpc.infrastructure.Message;
import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;

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
	public Participant announceChampionOfAllRounds(){
		if(getPlayerOne().getWinCount() > getPlayerTwo().getWinCount())
			return getPlayerOne();
		else if(getPlayerOne().getWinCount() < getPlayerTwo().getWinCount())
			return getPlayerTwo();
		return null;
	}
	
	public void play() throws Exception {
	
	super.play();
	
	DataOutputStream playerOneOutput=new DataOutputStream(this.getPlayerOne().getSocket().getOutputStream());
	DataInputStream playerOneInput=new DataInputStream(this.getPlayerOne().getSocket().getInputStream());
	DataOutputStream playerTwoOutput=new DataOutputStream(this.getPlayerTwo().getSocket().getOutputStream());
	DataInputStream playerTwoInput=new DataInputStream(this.getPlayerTwo().getSocket().getInputStream());
	
	// This will send message to players of individual game
	sendGameStartInstructionMessage(playerOneOutput,playerTwoOutput);
	
	try{
		while(!roundsCompleted()){
			
			IndividualRound round= new IndividualRound();
			
			
			playerTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.READ, playerTwo, playerOne.getName() + "'s Turn "));
			playerTwoOutput.flush();
			
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.WRITE, playerOne, submissionOptionMessage()));
			playerOneOutput.flush();
			
			
			Message playerOneSubmission = MessageFactory.createMessage(playerOneInput.readUTF()); // Reading from System.in
			//String input=br.readLine();
			if(!Submission.isQuited(playerOneSubmission.getBody())){
				playerOne.setQuit(true);
				break;
			}
			
			while(!Submission.isValidSubmission(playerOneSubmission.getBody())){
			//	System.out.println("not a valid option try again.");
				playerOneOutput.writeUTF("not a valid option try again.");
				playerOneSubmission=MessageFactory.createMessage(playerOneInput.readUTF()); // Reading from System.in;
			}
			playerOne.takeTurn(Submission.valueOf(playerOneSubmission.getBody().toUpperCase().trim()));
			
					
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.READ, playerOne, playerTwo.getName() + "'s Turn "));
			playerOneOutput.flush();
			
			playerTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.WRITE, playerTwo, submissionOptionMessage()));
			playerTwoOutput.flush();
			
			
			Message playerTwoSubmission = MessageFactory.createMessage(playerTwoInput.readUTF()); // Reading from System.in
			//String input=br.readLine();
			if(!Submission.isQuited(playerTwoSubmission.getBody())){
				playerOne.setQuit(true);
				break;
			}
			
			while(!Submission.isValidSubmission(playerTwoSubmission.getBody())){
			//	System.out.println("not a valid option try again.");
				playerTwoOutput.writeUTF("not a valid option try again.");
				playerTwoSubmission=MessageFactory.createMessage(playerTwoInput.readUTF()); // Reading from System.in;
			}
			playerTwo.takeTurn(Submission.valueOf(playerTwoSubmission.getBody().toUpperCase().trim()));
			
			
			nextRound(round);
			
		}
		
		if(playerOne.isQuit() || playerTwo.isQuit()){
			announceWinnerInCaseofQuit(playerOneOutput,playerTwoOutput);
		}else{
			finish();
		}
		//finish(So);
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("Game end abruptly. Please try to play a new game.");
	}
	finally{
		try{
			if(playerOneInput!=null)
				playerOneInput.close();
			if(playerOneOutput!=null)
				playerOneOutput.close();
			if(playerTwoInput!=null)
				playerTwoInput.close();
			if(playerTwoOutput!=null)	
				playerTwoOutput.close();
			
			playerOne.getSocket().close();
			playerTwo.getSocket().close();
	
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	};
	
	
	@Override
	public void finish() throws Exception {
		super.finish();
		
		DataOutputStream playerOneOutput=(DataOutputStream) this.getPlayerOne().getSocket().getOutputStream();
		DataOutputStream playerTwoOutput=(DataOutputStream) this.getPlayerTwo().getSocket().getOutputStream();
	
		Participant participant=announceChampionOfAllRounds();
		
		//mean game ended in draw
		if(participant==null){
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DRAW, playerOne, "Match is draw."));
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DRAW, playerTwo, "Match is draw."));
		}else{
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerOne, ((Player)participant).getName()+" won. Congratulation"));
			playerTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerTwo, ((Player)participant).getName()+" won. Congratulation"));
		}
	}
	
	@Override
	protected void sendGameStartInstructionMessage(DataOutputStream playerOneOut,DataOutputStream playerTwoOut)  {
					
		try{
			
			playerOneOut
			.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, this.getPlayerOne(), gameInstructionMessage()));
			playerOneOut.flush();
			
			playerTwoOut
			.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, this.getPlayerTwo(), gameInstructionMessage()));
			playerTwoOut.flush();
		
		}catch(Exception exception){
				exception.printStackTrace();
		
		}		
	}
	
	@Override
	protected void announceWinnerInCaseofQuit(DataOutputStream playerOneOut,DataOutputStream playerTwoOut) {
	
	try{
			if(getPlayerOne().isQuit()){
				playerOneOut.writeUTF("Winner is "+ playerTwo.getName());
				playerTwoOut.writeUTF("Winner is "+ playerTwo.getName());
			}if(getPlayerTwo().isQuit()){
				playerOneOut.writeUTF("Winner is "+ playerOne.getName());
				playerTwoOut.writeUTF("Winner is "+ playerOne.getName());
			}
			
		//	playerTwoOutput.
			
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
			   .append("4. QUIT").append('\n')
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
