package lithium.service.casino.client;

import lithium.service.Response;
import lithium.service.casino.client.data.BonusRevision;
import lithium.service.casino.client.data.CasinoBonus;
import lithium.service.casino.client.data.CasinoBonusCheck;
import lithium.service.casino.client.objects.BonusRevisionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-casino", path="/casino/bonus")
public interface CasinoBonusClient {
	@RequestMapping(method=RequestMethod.POST, path="/find/revisions")
	Response<List<BonusRevision>> findRevisions(@RequestBody List<BonusRevisionRequest> requests);

	@RequestMapping(method=RequestMethod.GET, path="/find/{domainName}/{type}/{bonusCode}")
	Response<Long> find(
		@PathVariable("domainName") String domainName,
		@PathVariable("type") Integer type,
		@PathVariable("bonusCode") String bonusCode
	) throws Exception;

	@RequestMapping(method=RequestMethod.GET, path="/find/{domainName}/0/{bonusCode}")
	Response<Long> findSignupBonus(
		@PathVariable("domainName") String domainName,
		@PathVariable("bonusCode") String bonusCode
	) throws Exception;
	
	@RequestMapping(method=RequestMethod.GET, path="/find/{domainName}/1/{bonusCode}")
	Response<Long> findDepositBonus(
		@PathVariable("domainName") String domainName,
		@PathVariable("bonusCode") String bonusCode
	) throws Exception;
	
	@RequestMapping(method=RequestMethod.POST, path="/check/1")
 	Response<Boolean> checkDepositBonusValidForPlayer(
 		@RequestBody CasinoBonusCheck casinoBonusCheck
 	) throws Exception;
	
	@RequestMapping(method=RequestMethod.POST, path="/manual/register/deposit")
	Response<Long> registerForDepositBonus(@RequestBody CasinoBonus casinoBonus) throws Exception;
	@RequestMapping(method=RequestMethod.POST, path="/register/depositbyid")
 	Response<Long> registerForDepositBonusById(@RequestBody CasinoBonus casinoBonus) throws Exception;
	@RequestMapping(method=RequestMethod.POST, path="/register/signup")
	Response<Long> registerForSignupBonus(@RequestBody CasinoBonus casinoBonus) throws Exception;
	@RequestMapping(method=RequestMethod.POST, path="/register/signupbyid")
	Response<Long> registerForSignupBonusById(@RequestBody CasinoBonus casinoBonus) throws Exception;
	@RequestMapping(method=RequestMethod.GET, path="/find/bonusrevision/{bonusRevisionId}")
	Response<BonusRevision> findByBonusRevisionId(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws Exception;
	@RequestMapping(method=RequestMethod.GET, path="/find/bonusrevision/byid/{bonusId}")
	Response<BonusRevision> findByBonusId(@PathVariable("bonusId") Long bonusId);
	
	@RequestMapping(method=RequestMethod.POST, path="/find/bonushistory")
	public List<lithium.service.casino.client.data.PlayerBonusHistory> findBonusHistoryByDateRange(@RequestParam("playerGuid") String playerGuid, @RequestParam("rangeStart") String rangeStart, @RequestParam("rangeEnd") String rangeEnd) throws Exception;

}