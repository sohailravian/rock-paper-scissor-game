package ae.gcaa.rpc.model;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ae.gcaa.rpc.infrastructure.Message;

public class Utils {
	
	private static final ObjectMapper OBJECT_MAPPER=new ObjectMapper();
	
	static{
		OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
	}
	
	public static String convertMessageToJson(Message message) throws Exception{
		return getObjectMapper().writeValueAsString(message);
	}
	
	public static Message convertJsonToMessage(String json) throws Exception{
		return getObjectMapper().readValue(json, Message.class);
	}
	
	public static ObjectMapper getObjectMapper() {
		return OBJECT_MAPPER;
	}

}
