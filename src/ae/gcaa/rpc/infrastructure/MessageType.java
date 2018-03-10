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
			
	};
	
	private MessageType(String code){
		this.setCode(code);
	}
	private String code;
	
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
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return code;
	}

}
