package ae.gcaa.rpc.infrastructure;

public enum MessageType {
	DISPLAY("I"){
		public boolean isDisplay(){
			return true;
		}
	},
	READ("R"){
		public boolean isRead(){
			return true;
		}
	},
	WRITE("W"){
		public boolean isWrite(){
			return true;
		}
	},
	LOSE("L"){
		public boolean isLose(){
			return true;
		}
	},
	WIN("W"){
		public boolean isWin(){
			return true;
		}
	},
	DRAW("D"){
		public boolean isDraw(){
			return true;
		}
	},
	INVALID("N"){
		public boolean isInvalid(){
			return true;
		}
	};
	
	private MessageType(String code){
		this.setCode(code);
	}
	private String code;
	
	public static MessageType messageTypeValueOf(String code){
		if(MessageType.DISPLAY.code.equalsIgnoreCase(code))
			return MessageType.DISPLAY;
		else if(MessageType.DRAW.code.equalsIgnoreCase(code))
			return MessageType.DRAW;
		else if(MessageType.INVALID.code.equalsIgnoreCase(code))
			return MessageType.INVALID;
		else if(MessageType.LOSE.code.equalsIgnoreCase(code))
			return MessageType.LOSE;
		else if(MessageType.READ.code.equalsIgnoreCase(code))
			return MessageType.READ;
		else if(MessageType.WIN.code.equalsIgnoreCase(code))
			return MessageType.WIN;
		return MessageType.WRITE;
	}
	
	
	public boolean isDisplay(){
		return false;
	}
	
	public boolean isRead(){
		return false;
	}
	
	public boolean isWrite(){
		return false;
	}
	
	public boolean isLose(){
		return false;
	}
	
	public boolean isWin(){
		return false;
	}
	public boolean isDraw(){
		return false;
	}
	public boolean isInvalid(){
		return false;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return code;
	}

}
