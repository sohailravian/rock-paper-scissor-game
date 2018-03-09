package ae.gcaa.rpc.model;

import java.io.IOException;
import java.net.Socket;
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
		//if(rounds%2!=1)
			//throw new IllegalArgumentException("Rounds should be odd so it will definelty give some reuslt. No use of wasting time in draw.");
	}
	
	
	public abstract void nextRound(Round round);
	
	public abstract Participant championOfAllRounds();
	
	protected boolean currentRoundTied(Round round){
		return round.decideRoundResult(this);
	}
	

	public boolean roundsCompleted(){
		return rounds.size() < totalRounds;
	}
	
	public void play() throws IOException{
		this.setGameState(GameState.STARTED);
	}
	public void finish(Socket socket) throws IOException{
		this.setGameState(GameState.ENDED);
		socket.close();
	}
	
	public String submissionOptionMessage(){
		StringBuilder builder=new StringBuilder();
		builder.append("Enter one of below options").append('\n').append("1. ROCK").append('\n').append("2. PAPER").append('\n').append("3. SCISSOR");
		return builder.toString();
	}
	
	public static String gameSelectionMessage(){
		StringBuilder builder=new StringBuilder();
		builder.append(" ********************** This Game has two modes ************************").append('\n')
			   .append(" 1. I which stands for individual").append('\n')
			   .append(" 2. T which stands for team").append('\n')
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
