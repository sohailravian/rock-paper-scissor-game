package ae.gcaa.rpc.model;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Game {
	
	private String gameId;
	private GameState gameState;
	private int totalRounds;
	private List<Round> rounds; 
	
	protected Game(int rounds){
		this.setGameState(GameState.STARTED);
		this.setRounds(new ArrayList<Round>());
		this.setTotalRounds(rounds);
	}
	
	protected abstract void nextRound(Round round);
	protected abstract void sendGameStartInstructionMessage(DataOutputStream participantOneOut,DataOutputStream participantTwoOut);
	protected abstract Participant announceChampionOfAllRounds();
	protected abstract void announceWinnerInCaseofQuit(DataOutputStream participantOneOut,DataOutputStream participantTwoOut);

	
	protected boolean currentRoundTied(Round round,Game game){
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





}
