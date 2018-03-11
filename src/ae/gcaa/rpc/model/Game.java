package ae.gcaa.rpc.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ae.gcaa.rpc.infrastructure.Message;
import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;

public abstract class Game {
	
	
	private ExecutorService executor;
	private String gameId;
	private GameState gameState;
	private int totalRounds;
	private List<Round> rounds; 
	
	public static String WELCOME_TO_GAME="********** Welcome to the Game *****************";
	public static String ENTER_ROUNDS=" Enter rounds to play. ";
	public static String NEW_LINE="\n";
	public static String EMPTY_STARS_LINE="************************************************";
	public static String MATCH_DRAW="****************** Match is draw ***************";
	
	
	
	protected Game(int rounds){
		this.setGameState(GameState.STARTED);
		this.setRounds(new ArrayList<Round>());
		this.setTotalRounds(rounds);
		this.executor=Executors.newCachedThreadPool();
	}
	
	protected abstract void nextRound(Round round) throws Exception;
	protected abstract void sendGameStartInstructionMessage(DataOutputStream participantOutput);
	protected abstract Participant announceChampionOfAllRounds();
	protected abstract void announceWinnerInCaseofQuit(Participant participantOne,Participant participantTwo) throws Exception ;

	
	protected boolean currentRoundTied(Round round,Game game) throws Exception{
		return round.decideRoundResult(game);
	}
	
	public boolean roundsCompleted(){
		return rounds.size() >= totalRounds;
	}
	
	public void play() throws Exception{
		this.setGameState(GameState.STARTED);
	}
	public void finish() throws Exception{
		this.setGameState(GameState.ENDED);
	//	socket.close();
	}
	
	public String submissionOptionMessage(){
		StringBuilder builder=new StringBuilder();
		builder.append("Enter one of below options").append('\n').append("1. ROCK").append('\n').append("2. PAPER").append('\n').append("3. SCISSORS");
		return builder.toString();
	}
	
	/* This method is to show message to client to play as individual or team
	 */
	public static String gameSelectionMessage(){
		StringBuilder builder=new StringBuilder();
		builder.append(" ********************** This Game has two modes ************************").append('\n')
			   .append(GameMode.INDIVIDUAL.getCode()).append("  ->	Stands for individual.").append('\n')
			   .append(GameMode.TEAM.getCode()).append("  ->	Stands for team.").append('\n')
			   .append(" ***********************************************************************");
				
		return builder.toString();
	} 
	
	
	public class ParticipantTurnThread implements Callable<Void>{
		private Participant participant;
		public ParticipantTurnThread(Participant participant){
			this.setParticipant(participant);
		}
		
		public Participant getParticipant() {
			return participant;
		}
		public void setParticipant(Participant participant) {
			this.participant = participant;
		}

		@Override
		public Void call() throws Exception {
			
			DataOutputStream responseOutput=null;
			DataInputStream responseInput=null;
			try{
				
					responseOutput=new DataOutputStream(participant.getSocket().getOutputStream());
					responseInput=new DataInputStream(participant.getSocket().getInputStream());
					
					responseOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, null, gameInstructionMessage()));
					responseOutput.flush();
					
					responseOutput.writeUTF(MessageFactory.createMessage(MessageType.WRITE, participant, "Please enter your choice."));
					responseOutput.flush();
					
					Message submission = MessageFactory.createMessage(responseInput.readUTF()); // Reading from System.in
					
					/*synchronized (this) {
						this.notify();
					}
					notify();*/
					
					while(true){
						
						String participantInput= submission.getBody().trim().toUpperCase();
						
						//String input=br.readLine();
						if(Submission.isQuited(participantInput)){
							participant.setQuit(true);
							participant.takeTurn(Submission.valueOf(participantInput));
							break;
						}
						
						if(Submission.isValidSubmission(participantInput)){
							participant.takeTurn(Submission.valueOf(participantInput));
							break;
						}
						
						responseOutput.writeUTF(MessageFactory.createMessage(MessageType.WRITE, participant, "not a valid option try again."));
						submission=MessageFactory.createMessage(responseInput.readUTF()); // Reading from client
					}
				
			}catch(Exception exception){
				exception.printStackTrace();
				throw new Exception(exception);
			}finally{
					try{
						/*if(responseInput!=null){
							responseInput.close();
						}if(responseOutput!=null){
							responseOutput.close();
						}*/
					}catch(Exception e){
						e.printStackTrace();
					}
			}
			
			return null;
		}
		
	}
	
	public String gameInstructionMessage(){
		StringBuilder builder=new StringBuilder();
		builder.append("**************** Below are four options ****************").append('\n')
			   .append(Submission.ROCK.name()).append('\n')
			   .append(Submission.PAPER.name()).append('\n')
			   .append(Submission.SCISSORS.name()).append('\n')
			   .append(Submission.QUIT.name()).append('\n')
			   .append("*************** In case of QUITING the game other player wil win.");
		return builder.toString(); 
	}
	
	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public List<Round> getRounds() {
		return rounds;
	}

	public void setRounds(List<Round> rounds) {
		this.rounds = rounds;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public int getTotalRounds() {
		return totalRounds;
	}

	public void setTotalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}





}
