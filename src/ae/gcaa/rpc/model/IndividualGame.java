package ae.gcaa.rpc.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import ae.gcaa.rpc.infrastructure.Message;
import ae.gcaa.rpc.infrastructure.MessageFactory;
import ae.gcaa.rpc.infrastructure.MessageType;

public class IndividualGame extends Game {
	
	private Player playerOne;
	private Player playerTwo;

	public IndividualGame(int rounds,Player playerOne,Player playerTwo){
		super(rounds);
		this.setPlayerOne(playerOne);
		this.setPlayerTwo(playerTwo);
	}
	
	@Override
	public void nextRound(Round round) throws Exception{
		if(currentRoundTied(round,this)){
			getRounds().add(round);
		}
		
		this.playerOne.setSubmission(Submission.UNKNOWN);
		this.playerTwo.setSubmission(Submission.UNKNOWN);
	}
	
	@Override
	public Participant announceChampionOfAllRounds(){
		if(getPlayerOne().getWinCount() > getPlayerTwo().getWinCount())
			return playerOne;
		else if(playerOne.getWinCount() < getPlayerTwo().getWinCount())
			return playerTwo;
		return null;
	}
	
	public void play() throws Exception {
	
		super.play();
		try{
			while(!roundsCompleted()){
				
				IndividualRound round= new IndividualRound();
				while(this.playerOne.getSubmission().isUnknown() && this.playerTwo.getSubmission().isUnknown()){
				
					Callable<Void> playerOneFuture = new ParticipantTurnThread(playerOne);
					Callable<Void> playerTwoFuture = new ParticipantTurnThread(playerTwo);
						
					List<Callable<Void>> futures= Arrays.asList(playerOneFuture,playerTwoFuture);
					getExecutor().invokeAll(futures);
					/*playerOneFuture.start();
					playerOneFuture.join();
					playerTwoFuture.start();*/
					
				/*	synchronized (this) {
					    this.wait();
					}*/ 
					
					if(playerOne.isQuit() || playerTwo.isQuit()){
						announceWinnerInCaseofQuit(this.playerOne,this.playerTwo);
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
		}finally{
			try{
			 if(playerOne.getSocket()!=null) playerOne.getSocket().close();
			 if(playerTwo.getSocket()!=null) playerTwo.getSocket().close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		
	};
	
	
	@Override
	public void finish() throws Exception {
		super.finish();
		
		DataOutputStream playerOneOutput=new DataOutputStream(playerOne.getSocket().getOutputStream());
		DataOutputStream playerTwoOutput=new DataOutputStream(playerTwo.getSocket().getOutputStream());
	
		Participant participant=announceChampionOfAllRounds();
		
		StringBuilder builder=new StringBuilder("**********************************************").append("\n");
		//mean game ended in draw
		if(participant==null){
			builder.append("************* Match is draw *************").append("\n");
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DRAW, playerOne, builder.toString()));
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DRAW, playerTwo, builder.toString()));
		}else{
			builder.append("*********** Winner is Mr. "+ ((Player)participant).getName() +" **************")
			.append("**********************************************");
			playerOneOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerOne, builder.toString()));
			playerTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, playerTwo,builder.toString()));
		}
		
	}
	
	@Override
	protected void sendGameStartInstructionMessage(DataOutputStream outputResponse)  {
		try{
			outputResponse.writeUTF(MessageFactory.createMessage(MessageType.DISPLAY, null, gameInstructionMessage()));
			outputResponse.flush();
		}catch(Exception exception){
				exception.printStackTrace();
		}		
	}
	
	@Override
	protected void announceWinnerInCaseofQuit(Participant participantOne,Participant participantTwo) throws Exception {
		DataOutputStream participantOneOutput=new DataOutputStream(participantOne.getSocket().getOutputStream());
		DataOutputStream participantTwoOutput=new DataOutputStream(participantTwo.getSocket().getOutputStream());
		
		StringBuilder builder=new StringBuilder("**********************************************").append("\n");
		
		try{
			Player winner= null;
			if(getPlayerOne().isQuit()){
				winner=playerTwo;
			}else{
				winner=playerOne;
			}
			
			builder.append("*********** Winner is Mr. "+ winner.getName() +" **************").append("\n")
			.append("**********************************************");
			
			participantOneOutput.writeUTF(MessageFactory.createMessage(MessageType.WIN, playerOne, builder.toString()));
			participantTwoOutput.writeUTF(MessageFactory.createMessage(MessageType.WIN, playerTwo, builder.toString()));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	public Player getPlayerOne() {
		return playerOne;
	}

	public void setPlayerOne(Player playerOne) {
		this.playerOne = playerOne;
	}

	public Player getPlayerTwo() {
		return playerTwo;
	}

	public void setPlayerTwo(Player playerTwo) {
		this.playerTwo = playerTwo;
	}



}
