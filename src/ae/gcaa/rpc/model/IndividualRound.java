package ae.gcaa.rpc.model;

import java.io.DataOutputStream;
import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;

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

	@Override
	public boolean decideRoundResult(Game game) throws Exception{
		
		IndividualGame individualGame= (IndividualGame) game;
		
		DataOutputStream participantOneDataOutput=new DataOutputStream(individualGame.getPlayerOne().getSocket().getOutputStream()) ; 
		DataOutputStream participantTwoDataOutput=new DataOutputStream(individualGame.getPlayerTwo().getSocket().getOutputStream()) ; 
		
		Player playerOne=individualGame.getPlayerOne();
		Player playerTwo=individualGame.getPlayerTwo();
		
		Submission playerOneSubmission= playerOne.getSubmission();
		Submission playerTwoSubmission= playerTwo.getSubmission();
		
		boolean firstPlayerWins= winner(playerOneSubmission, playerTwoSubmission);
		boolean secondPlayerWins= winner(playerTwoSubmission,playerOneSubmission);
		
		if(!firstPlayerWins && !secondPlayerWins){
			String sameRound= Utils.stringMessageBuilder(Game.EMPTY_LINE,Game.NEW_LINE,Game.MATCH_DRAW,Game.NEW_LINE); 
			participantOneDataOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerOne, sameRound));
			participantTwoDataOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerTwo, sameRound));
			
			return false;
		}
		
		if(firstPlayerWins){
			roundCompletedSettlement(playerOne, playerTwo);
		}else{
			roundCompletedSettlement(playerTwo, playerOne);
		}
		
		String winnerRound= Utils.stringMessageBuilder(Game.EMPTY_LINE,Game.NEW_LINE,IndividualGame.INDIVIDUAL_ROUND_WINNER,this.getWinner().getName(),Game.NEW_LINE); 
		participantOneDataOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerOne, winnerRound));
		participantTwoDataOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerTwo, winnerRound));
		
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
