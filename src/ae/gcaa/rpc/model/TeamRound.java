package ae.gcaa.rpc.model;

import java.io.DataOutputStream;

import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;

public class TeamRound extends Round {
	private Team winner;
	private Team loser;
	
	public TeamRound() { 
		super();
	}
	
	protected void roundCompletedSettlement(Participant winner,Participant loser){
		
		winner.setWinCount(winner.getWinCount()+1);
		setWinner((Team)winner);
		setLoser((Team)loser);
	} 

	@Override
	public boolean decideRoundResult(Game game) throws Exception{
		
		TeamGame teamGame= (TeamGame) game;
		
		DataOutputStream participantOneDataOutput=new DataOutputStream(teamGame.getTeamOne().getSocket().getOutputStream()) ; 
		DataOutputStream participantTwoDataOutput=new DataOutputStream(teamGame.getTeamTwo().getSocket().getOutputStream()) ; 
		
		Team teamOne=teamGame.getTeamOne();
		Team teamTwo=teamGame.getTeamTwo();
		
		Submission teamOneSubmission= teamOne.getSubmission();
		Submission teamTwoSubmission= teamTwo.getSubmission();
		
		boolean firstTeamWins= winner(teamOneSubmission, teamTwoSubmission);
		boolean secondTeamWins= winner(teamTwoSubmission,teamOneSubmission);
		
		if(!firstTeamWins && !secondTeamWins){
			String sameRound= Utils.stringMessageBuilder(Game.EMPTY_LINE,Game.NEW_LINE,Game.MATCH_DRAW,Game.NEW_LINE); 
			participantOneDataOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, teamOne, sameRound));
			participantTwoDataOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, teamTwo, sameRound));
			
			return false;
		}
		
		if(firstTeamWins){
			roundCompletedSettlement(teamOne, teamTwo);
		}else{
			roundCompletedSettlement(teamTwo, teamOne);
		}
		
		String winnerRound= Utils.stringMessageBuilder(Game.EMPTY_LINE,Game.NEW_LINE,TeamGame.TEAM_ROUND_WINNER,this.getWinner().name,Game.NEW_LINE);
		participantOneDataOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, teamOne, winnerRound));
		participantTwoDataOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, teamTwo, winnerRound));
		
		return true;
	}

	public Team getLoser() {
		return loser;
	}

	public void setLoser(Team loser) {
		this.loser = loser;
	}

	public Team getWinner() {
		return winner;
	}

	public void setWinner(Team winner) {
		this.winner = winner;
	}
	
	
	


}
