package ae.gcaa.rpc.model;

public enum State{
	IDLE(0),
	WAITING(1),
	TURN(2),
	WON(3),
	LOST(4);
	
	private State(int code){
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
