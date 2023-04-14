package service.casino.provider.cataboom.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedParams {
private String token;
private String campaignid;
private String playerid; //Catboom seems to have changed their API without updating docs. This is now accountid.
private String accountid;
private String playid;// Cataboom likes changing their stuff without updating docs
private String playcode; 
private String winlevel;
@JsonProperty(required=false)
private String prizelink;
@JsonProperty(required=false)
private String prizecode;
@JsonProperty(required=false)
private String prizepin;
@JsonProperty(required=false)
private String description;

public String getAccountid() {
	if (accountid == null) {
		return playerid;
	}
	return accountid;
}


public String getPlayid() {
	if (playid == null) {
		return playcode;
	}
	return playid;
}

}
