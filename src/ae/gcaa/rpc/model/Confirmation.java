package ae.gcaa.rpc.model;

/* This class/enum is for Game mode 
 */

public enum Confirmation{
	YES("Y"){
		public boolean isYes(){
			return true;
		}
	},
	NO("N"){
		public boolean isNo(){
			return true;
		}
	};
	private String code;
	private Confirmation(String code){
		this.code=code;
	} 
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public boolean isYes(){
		return false;
	}
	
	public boolean isNo(){
		return false;
	}
	
	public static boolean isValidOption(String mode){
		if(Confirmation.YES.code.equalsIgnoreCase(mode))
			return true;
		if(Confirmation.NO.code.equalsIgnoreCase(mode))
			return true;
		
		return false;
	}
	
	public static Confirmation confirmationOfValue(String mode){
		if(mode.equalsIgnoreCase(Confirmation.YES.code)){
			return Confirmation.YES;
		}
		else{
			return Confirmation.NO;
		}
	}
	public static String validOptions() {
		StringBuilder builder=new StringBuilder();
		builder.append("[").append(Confirmation.YES.getCode()).append(",").append(Confirmation.NO.getCode()).append("]");
		return builder.toString();
	}
	
}
