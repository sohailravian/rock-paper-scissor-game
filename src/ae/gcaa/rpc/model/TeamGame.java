package ae.gcaa.rpc.model;

import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;


public class TeamGame extends Game {

	public TeamGame(int rounds,Team teamOne,Team teamTwo){
		super(rounds);
		this.setTeamOne(teamOne);
		this.setTeamTwo(teamTwo);
	}
	
	private Team teamOne;
	private Team teamTwo;
	
	public static String ENTER_NAME= " Enter you team name. ";
	public static String TEAM_WINNER="*********** Winning team is. ";
	public static String TEAM_ROUND_WINNER=" ========== Welcome to the next round. Round winner is Team ";
	
	@Override
	protected void nextRound(Round round) throws Exception {
		if(currentRoundTied(round,this)){
			getRounds().add(round);
		}
		
		this.teamOne.setSubmission(Submission.UNKNOWN);
		this.teamTwo.setSubmission(Submission.UNKNOWN);
		
	}
	@Override
	protected void sendGameStartInstructionMessage(DataOutputStream outputResponse) {
		try{
			outputResponse.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, null, gameInstructionMessage()));
			outputResponse.flush();
		}catch(Exception exception){
				exception.printStackTrace();
		}	
		
	}
	@Override
	protected Participant announceChampionOfAllRounds() {
		if(getTeamOne().getWinCount() > getTeamTwo().getWinCount())
			return teamOne;
		else if(teamOne.getWinCount() < getTeamTwo().getWinCount())
			return teamTwo;
		return null;
	}
	@Override
	protected void announceWinnerInCaseofQuit(Participant participantOne,Participant participantTwo) throws Exception {
		DataOutputStream participantOneOutput=new DataOutputStream(participantOne.getSocket().getOutputStream());
		DataOutputStream participantTwoOutput=new DataOutputStream(participantTwo.getSocket().getOutputStream());
		
		try{
			Team winner= null;
			if(getTeamOne().isQuit()){
				winner=teamTwo;
			}else{
				winner=teamOne;
			}
			
			String winMessage= Utils.stringMessageBuilder(Game.EMPTY_LINE,Game.NEW_LINE,TeamGame.TEAM_WINNER, winner.getName(), Game.NEW_LINE,Game.EMPTY_LINE);
				
			participantOneOutput.writeUTF(MessageFactory.createMessage(MessageType.WIN, teamOne, winMessage));
			participantTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.WIN, teamTwo, winMessage));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void play() throws Exception {
		this.setGameState(GameState.STARTED);
		
		try{
			while(!roundsCompleted()){
				
				TeamRound round= new TeamRound();					
				/* Waiting for players to enter their choice
				 * */
				
				while(this.teamOne.getSubmission().isUnknown() && this.teamTwo.getSubmission().isUnknown()){
				
					Callable<Void> playerOneFuture = new ParticipantTurnThread(teamOne);
					Callable<Void> playerTwoFuture = new ParticipantTurnThread(teamTwo);
						
					List<Callable<Void>> futures= Arrays.asList(playerOneFuture,playerTwoFuture);
					getExecutor().invokeAll(futures);
	
					if(teamOne.isQuit() || teamTwo.isQuit()){
						announceWinnerInCaseofQuit(this.teamOne,this.teamTwo);
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
			 if(teamOne.getSocket()!=null) teamOne.getSocket().close();
			 if(teamTwo.getSocket()!=null) teamTwo.getSocket().close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public Team getTeamOne() {
		return teamOne;
	}
	public void setTeamOne(Team teamOne) {
		this.teamOne = teamOne;
	}
	public Team getTeamTwo() {
		return teamTwo;
	}
	public void setTeamTwo(Team teamTwo) {
		this.teamTwo = teamTwo;
	}

	
}
