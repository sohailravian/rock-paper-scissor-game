package ae.gcaa.rpc.infrastructure;

import ae.gcaa.rpc.model.Participant;
import ae.gcaa.rpc.model.Utils;

/* @author Sheikh Muhammmad Sohail
 * @purpose to generate message converted into json or by json into object
 **/
public class MessageFactory {
	
	public static String createMessage(MessageType type, Participant participant,String contents) throws Exception{
		Message message=new Message(participant, type);
		if(!contents.isEmpty()){
			message.setBody(contents);
		}
		return Utils.convertMessageToJson(message);
	}
	
	public static Message createMessage(String json) throws Exception{
		Message message=Utils.convertJsonToMessage(json);
		return message;
	}
	
	
	
}
