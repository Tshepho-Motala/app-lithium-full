package service.casino.provider.cataboom.mock;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import service.casino.provider.cataboom.objects.ReceivedParams;
@RestController
public class ReceivedParamsMockController {
	
	@RequestMapping("/getPrizeInfo")
	public ReceivedParams getPrizeInfo() {
		String token="";
		String campaignid = "";
		String accountid = "";
		String playid = "";
		String winlevel = "4";
		ReceivedParams obj= new ReceivedParams();
		obj.setToken(token);
		obj.setAccountid(accountid);
		obj.setCampaignid(campaignid);
		obj.setPlayid(playid);
		obj.setWinlevel(winlevel);
		
		return obj;
		
	}
}
