package ae.gcaa.rpc.model;

import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;

public class IndividualGame extends Game {
	
	private Player playerOne;
	private Player playerTwo;
	
	public static String ENTER_NAME= " Enter you name. ";
	public static String INDIVIDUAL_WINNER="=============== Winner is Mr. ";
	public static String INDIVIDUAL_ROUND_WINNER=" ========== Welcome to the next round. Round winner is Mr. ";

	public IndividualGame(int rounds,Player playerOne,Player playerTwo){
		super(rounds);
		this.setPlayerOne(playerOne);
		this.setPlayerTwo(playerTwo);
	}
	
	@Override
	public void nextRound(Round round) throws Exception{
		if(currentRoundTied(round,this)){
			getRounds().add(round);
		}
		
		this.playerOne.setSubmission(Submission.UNKNOWN);
		this.playerTwo.setSubmission(Submission.UNKNOWN);
	}
	
	@Override
	public Participant announceChampionOfAllRounds(){
		if(getPlayerOne().getWinCount() > getPlayerTwo().getWinCount())
			return playerOne;
		else if(playerOne.getWinCount() < getPlayerTwo().getWinCount())
			return playerTwo;
		return null;
	}
	
	/* Main method where game start between individual players 
	 **/
	
	public void play() throws Exception {
	
		this.setGameState(GameState.STARTED);
		
		try{
			while(!roundsCompleted()){
				
				IndividualRound round= new IndividualRound();
						
				/* Waiting for players to enter their choice
				 * */
				
				while(this.playerOne.getSubmission().isUnknown() && this.playerTwo.getSubmission().isUnknown()){
				
					Callable<Void> playerOneFuture = new ParticipantTurnThread(playerOne);
					Callable<Void> playerTwoFuture = new ParticipantTurnThread(playerTwo);
						
					List<Callable<Void>> futures= Arrays.asList(playerOneFuture,playerTwoFuture);
					getExecutor().invokeAll(futures);
	
					if(playerOne.isQuit() || playerTwo.isQuit()){
						announceWinnerInCaseofQuit(this.playerOne,this.playerTwo);
						break;
					}
				}
				
			   /* This will move the game to next round and save the player of the round
				*/
				nextRound(round);
				
			}
			
			
		   /* In case everything goes smooth this method will be called.
			* This will calculate the result depends on all rounds
			*/
			finish();
		
	
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Game end abruptly. Please try to play a new game.");
		}
		
		/* After successfully completing all rounds, close the socket for individual clients
		 **/
		finally{
			try{
			 if(playerOne.getSocket()!=null) playerOne.getSocket().close();
			 if(playerTwo.getSocket()!=null) playerTwo.getSocket().close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		
	};
	
	
	/* This method will get winner of all the rounds
	 **/
	
	@Override
	public void finish() throws Exception {
		super.finish();
		
		DataOutputStream playerOneOutput=new DataOutputStream(playerOne.getSocket().getOutputStream());
		DataOutputStream playerTwoOutput=new DataOutputStream(playerTwo.getSocket().getOutputStream());
	
		/* This method will find out the winner of rounds
		 */	
		
		Participant participant=announceChampionOfAllRounds();
		
		/* Sending Messages to competing players
		 **/
		
		if(participant==null){
			String drawMessage=Utils.stringMessageBuilder(Game.EMPTY_LINE,Game.NEW_LINE,Game.MATCH_DRAW);
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DRAW, playerOne, drawMessage));
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DRAW, playerTwo, drawMessage));
		}else{
			String winMessage=
			Utils.stringMessageBuilder(Game.EMPTY_LINE,Game.NEW_LINE,IndividualGame.INDIVIDUAL_WINNER, ((Player)participant).getName(),Game.NEW_LINE,Game.EMPTY_LINE);
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerOne,winMessage));
			playerTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerTwo,winMessage));
		}
		
	}
	
	@Override
	protected void sendGameStartInstructionMessage(DataOutputStream outputResponse)  {
		try{
			outputResponse.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, null, gameInstructionMessage()));
			outputResponse.flush();
		}catch(Exception exception){
				exception.printStackTrace();
		}		
	}
	
	@Override
	protected void announceWinnerInCaseofQuit(Participant participantOne,Participant participantTwo) throws Exception {
		DataOutputStream participantOneOutput=new DataOutputStream(participantOne.getSocket().getOutputStream());
		DataOutputStream participantTwoOutput=new DataOutputStream(participantTwo.getSocket().getOutputStream());
		
		try{
			Player winner= null;
			if(getPlayerOne().isQuit()){
				winner=playerTwo;
			}else{
				winner=playerOne;
			}
			
			String winMessage= Utils.stringMessageBuilder(Game.EMPTY_LINE,Game.NEW_LINE,TeamGame.TEAM_WINNER, winner.getName(), Game.NEW_LINE,Game.EMPTY_LINE);
			
			participantOneOutput.writeUTF(MessageFactory.createMessage(MessageType.WIN, playerOne, winMessage));
			participantTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.WIN, playerTwo, winMessage));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
