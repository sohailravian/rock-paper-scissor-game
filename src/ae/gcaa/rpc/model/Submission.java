package ae.gcaa.rpc.model;

public enum Submission {
	
	ROCK("R"){
		public boolean isRock(){
			return true;
		}
	},
	PAPER("P"){
		public boolean isPaper(){
			return true;
		}
	},
	SCISSORS("S"){
		public boolean isScissors(){
			return true;
		}
	},
	QUIT("Q"){
		public boolean isQuit(){
			return true;
		}
	},
	UNKNOWN("U"){
		public boolean isUnknown(){
			return true;
		}
	};
	
	private String code;
	private Submission(String code){
		this.setCode(code);
	}
	
	
	public boolean isRock(){
		return false;
	}
	public boolean isPaper(){
		return false;
	}
	public boolean isScissors(){
		return false;
	}
	public boolean isQuit(){
		return false;
	}
	public boolean isUnknown(){
		return false;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public static boolean isValidSubmission(String submission){
		try{
			Submission enteredSubmission= Submission.valueOf(submission.toUpperCase() );
			if(ROCK.equals(enteredSubmission) || PAPER.equals(enteredSubmission)|| SCISSORS.equals(submission) ||QUIT.equals(submission)){
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
