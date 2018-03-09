package ae.gcaa.rpc.model;

public abstract class Round {
	protected Round(){}
	
	public abstract boolean decideRoundResult(Game game);
	
	protected abstract void roundCompletedSettlement(Participant participantOne, Participant participantTwo);
	
	protected boolean winner(Submission player1Submission,Submission player2Submission){
		if(Submission.PAPER.equals(player1Submission) && Submission.ROCK.equals(player2Submission)){
			return true;
		}else if(Submission.SCISSOR.equals(player1Submission) && Submission.PAPER.equals(player2Submission)){
			return true;
		}else if(Submission.ROCK.equals(player1Submission) && Submission.SCISSOR.equals(player2Submission)){
			return true;
		}
		return false;
	}
}
