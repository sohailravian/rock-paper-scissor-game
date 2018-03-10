package ae.gcaa.rpc.model;

public class IndividualRound extends Round {
	private Player winner;
	private Player loser;
	
	public IndividualRound() { 
		super();
	}
	
	protected void roundCompletedSettlement(Participant winner,Participant loser){
		
		winner.setWinCount(winner.getWinCount()+1);
		setWinner((Player)winner);
		setLoser((Player)loser);
	} 

	public boolean decideRoundResult(Game game){
		
		IndividualGame individualGame= (IndividualGame) game;
		
		Player playerOne=individualGame.getPlayerOne();
		Player playerTwo=individualGame.getPlayerTwo();
		
		Submission playerOneSubmission= playerOne.getSubmission();
		Submission playerTwoSubmission= playerTwo.getSubmission();
		
		boolean firstPlayerWins= winner(playerOneSubmission, playerTwoSubmission);
		boolean secondPlayerWins= winner(playerTwoSubmission,playerOneSubmission);
		
		if(!firstPlayerWins && !secondPlayerWins){
			return false;
		}
		
		if(firstPlayerWins){
			roundCompletedSettlement(playerOne, playerTwo);
		}else{
			roundCompletedSettlement(playerTwo, playerOne);
		}
		
		return true;
	}
	
	
	public Player getWinner() {
		return winner;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
	}

	public Player getLoser() {
		return loser;
	}

	public void setLoser(Player loser) {
		this.loser = loser;
	}


}
