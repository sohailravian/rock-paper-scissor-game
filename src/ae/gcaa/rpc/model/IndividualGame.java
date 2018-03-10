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
		if(currentRoundTied(round,this)){
			getRounds().add(round);
		}
	}
	
	@Override
	public Participant announceChampionOfAllRounds(){
		if(getPlayerOne().getWinCount() > getPlayerTwo().getWinCount())
			return playerOne;
		else if(playerOne.getWinCount() < getPlayerTwo().getWinCount())
			return playerTwo;
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
			
			playerTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.WRITE, playerTwo, "Please enter your choice."));
			playerTwoOutput.flush();
			
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.WRITE, playerOne, "Please enter your choice."));
			playerOneOutput.flush();
			
			boolean plaerOneTookTurn=false;
			boolean plaerTwoTookTurn=false;
			
			while(!plaerOneTookTurn && !plaerTwoTookTurn){
			
				Message playerOneSubmission = MessageFactory.createMessage(playerOneInput.readUTF()); // Reading from System.in
				while(true){
				
					//String input=br.readLine();
					if(Submission.isQuited(playerOneSubmission.getBody())){
						playerOne.setQuit(true);
						break;
					}
					
					if(Submission.isValidSubmission(playerOneSubmission.getBody())){
					//	System.out.println("not a valid option try again.");
						playerOne.setSubmission(Submission.valueOf(playerOneSubmission.getBody().toUpperCase().trim()));
						playerOne.takeTurn(Submission.valueOf(playerOneSubmission.getBody().toUpperCase().trim()));
						plaerOneTookTurn=true;
						
						break;
					}
					
					playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.WRITE, playerOne, "not a valid option try again."));
					playerOneSubmission=MessageFactory.createMessage(playerOneInput.readUTF()); // Reading from client
					
				}
				
				//Player two waiting
				Message playerTwoSubmission = MessageFactory.createMessage(playerTwoInput.readUTF()); // Reading from System.in
				while(true){
				
					//String input=br.readLine();
					if(Submission.isQuited(playerTwoSubmission.getBody())){
						playerTwo.setQuit(true);
						break;
					}
					
					if(Submission.isValidSubmission(playerTwoSubmission.getBody())){
					//	System.out.println("not a valid option try again.");
						playerTwo.setSubmission(Submission.valueOf(playerTwoSubmission.getBody().toUpperCase().trim()));
						playerTwo.takeTurn(Submission.valueOf(playerTwoSubmission.getBody().toUpperCase().trim()));
						plaerTwoTookTurn=true;
						break;
					}
					
					playerTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.WRITE, playerTwo, "not a valid option try again."));
					playerTwoSubmission=MessageFactory.createMessage(playerOneInput.readUTF()); // Reading from client
					
				}
				
				
			}
			
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
		
		DataOutputStream playerOneOutput=new DataOutputStream(playerOne.getSocket().getOutputStream());
		DataOutputStream playerTwoOutput=new DataOutputStream(playerTwo.getSocket().getOutputStream());
	
		Participant participant=announceChampionOfAllRounds();
		
		StringBuilder builder=new StringBuilder("**********************************************").append("\n");
		//mean game ended in draw
		if(participant==null){
			builder.append("************* Match is draw *************").append("\n");
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DRAW, playerOne, builder.toString()));
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DRAW, playerTwo, builder.toString()));
		}else{
			builder.append("*********** Winner is Mr. "+ ((Player)participant).getName() +" **************")
			.append("**********************************************");
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerOne, builder.toString()));
			playerTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerTwo,builder.toString()));
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
	StringBuilder builder=new StringBuilder("**********************************************").append("\n");
		
	try{
			Player winner= null;
			if(getPlayerOne().isQuit()){
				winner=playerTwo;
			}else{
				winner=playerOne;
			}
			
			builder.append("*********** Winner is Mr. "+ winner.getName() +" **************").append("\n")
			.append("**********************************************");
			
			playerOneOut.writeUTF(MessageFactory.createMessage(MessageType.WIN, playerOne, builder.toString()));
			playerTwoOut.writeUTF(MessageFactory.createMessage(MessageType.WIN, playerTwo, builder.toString()));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public String gameInstructionMessage(){
		StringBuilder builder=new StringBuilder();
		builder.append("************************************************* INDIVIDUAL GAME *****************************************************").append('\n')
			   .append("**************** There are four options to select while playing the game and these are listed below *******************").append('\n')
			   .append(Submission.ROCK.name()).append('\n')
			   .append(Submission.PAPER.name()).append('\n')
			   .append(Submission.SCISSORS.name()).append('\n')
			   .append(Submission.QUIT.name()).append('\n')
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
