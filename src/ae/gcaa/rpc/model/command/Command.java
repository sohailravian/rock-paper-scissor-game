package ae.gcaa.rpc.model.command;

import java.io.Serializable;

public interface Command extends Serializable {
	
	static final long serialVersionUID = 653464558841487220L;
	
	public void execute();
}
