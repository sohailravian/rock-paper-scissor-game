package ae.gcaa.rpc.model;

import java.io.DataOutputStream;

public class TeamGame extends Game {

	protected TeamGame(int rounds) {
		super(rounds);
	}
	private Team teamOne;
	private Team teamTwo;
	
	public static String ENTER_NAME= " Enter you team name. ";
	public static String INDIVIDUAL_WINNER="*********** Winning team is. ";
	
	@Override
	protected void nextRound(Round round) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void sendGameStartInstructionMessage(
			DataOutputStream participantOutput) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected Participant announceChampionOfAllRounds() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void announceWinnerInCaseofQuit(Participant participantOne,
			Participant participantTwo) throws Exception {
		// TODO Auto-generated method stub
		
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
