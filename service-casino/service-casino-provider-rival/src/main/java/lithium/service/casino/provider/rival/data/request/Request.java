package lithium.service.casino.provider.rival.data.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE)
@ToString
public abstract class Request {
	@JsonProperty(value="playerid")
	private String playerId;
	@JsonProperty(value="sessionid")
	private String sessionId;
	@JsonProperty(value="hmac")
	private String hash;
	@JsonProperty(value="function")
	private String function;
	
	Request(Map<String,String> allParams) {
		playerId = allParams.get("playerid");
		sessionId = allParams.get("sessionid");
		hash = allParams.get("hmac");
		function = allParams.get("function");
	}
//	
//	public void storeCalculatedHash(String password) {
//		hash = calculateHash(password);
//	}
}
