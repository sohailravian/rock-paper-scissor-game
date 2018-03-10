package ae.gcaa.rpc.model;

public enum Submission {
	ROCK("R"),
	PAPER("P"),
	SCISSORS("S"),
	QUIT("Q");
	
	private String code;
	private Submission(String code){
		this.setCode(code);
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public static boolean isValidSubmission(String submission){
		try{
			Submission enteredSubmission= Submission.valueOf(submission.toUpperCase());
			if(ROCK.equals(enteredSubmission) || PAPER.equals(enteredSubmission)|| SCISSORS.equals(submission)){
				return true;
			}
		}catch(IllegalArgumentException e){
			return false;
		}
		return true;
	}
	
	public static boolean isQuited(String submisison){
		try{
			Submission enteredSubmission= Submission.valueOf(submisison);
			if(QUIT.equals(enteredSubmission)|| QUIT.getCode().equalsIgnoreCase(submisison))
				return true;
		}catch(IllegalArgumentException exception){
			return false;
		}
		return false;
	}
	
}
