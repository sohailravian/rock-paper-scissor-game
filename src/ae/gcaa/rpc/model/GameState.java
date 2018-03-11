package ae.gcaa.rpc.model;

public enum GameState {

	STARTED(0),
	ENDED(1)
	;
	
	private GameState(int code){
		this.code=code;
	}
	
	private int code;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
