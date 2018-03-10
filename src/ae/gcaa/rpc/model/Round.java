package ae.gcaa.rpc.model;

public abstract class Round {
	protected Round(){}
	
	public abstract boolean decideRoundResult(Game game);
	
	protected abstract void roundCompletedSettlement(Participant participantOne, Participant participantTwo);
	
	protected boolean winner(Submission playerOneSubmission,Submission playerTwoSubmission){
		if(Submission.PAPER.equals(playerOneSubmission) && Submission.ROCK.equals(playerTwoSubmission)){
			return true;
		}else if(Submission.SCISSORS.equals(playerOneSubmission) && Submission.PAPER.equals(playerTwoSubmission)){
			return true;
		}else if(Submission.ROCK.equals(playerOneSubmission) && Submission.SCISSORS.equals(playerTwoSubmission)){
			return true;
		}
		return false;
	}
}
