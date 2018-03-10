package ae.gcaa.rpc.model;

/* This class/enum is for Game mode 
 */

public enum GameMode{
	INDIVIDUAL("I"){
		public boolean isIndividual(){
			return true;
		}
	},
	TEAM("T"){
		public boolean isTeam(){
			return true;
		}
	};
	private String code;
	private GameMode(String code){
		this.code=code;
	} 
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public boolean isIndividual(){
		return false;
	}
	
	public boolean isTeam(){
		return false;
	}
	
	public static boolean isValidGameMode(String mode){
		if(GameMode.INDIVIDUAL.code.equals(mode))
			return true;
		if(GameMode.TEAM.code.equals(mode))
			return true;
		
		return false;
	}
	
	public static GameMode gameModeOfValue(String mode){
		if(mode.equalsIgnoreCase(GameMode.INDIVIDUAL.code)){
			return GameMode.INDIVIDUAL;
		}
		else{
			return GameMode.TEAM;
		}
	}
	public static String validOptions() {
		StringBuilder builder=new StringBuilder();
		builder.append("[").append(GameMode.INDIVIDUAL.getCode()).append(",").append(GameMode.TEAM.getCode()).append("]");
		return builder.toString();
	}
	
}
