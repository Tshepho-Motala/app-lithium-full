package lithium.service.casino.controllers;

import lithium.service.domain.client.util.LocaleContextProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import lithium.service.casino.client.CasinoFrbClient;
import lithium.service.casino.client.objects.request.AwardBonusRequest;
import lithium.service.casino.client.objects.request.CancelBonusRequest;
import lithium.service.casino.client.objects.request.CheckBonusRequest;
import lithium.service.casino.client.objects.request.GetBonusInfoRequest;
import lithium.service.casino.client.objects.response.AwardBonusResponse;
import lithium.service.casino.client.objects.response.CancelBonusResponse;
import lithium.service.casino.client.objects.response.CheckBonusResponse;
import lithium.service.casino.client.objects.response.GetBonusInfoResponse;
import lithium.service.casino.client.objects.response.UpdateBonusIdResponse;
import lithium.service.casino.service.FrbService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class FreeRoundBonusController {
	@Autowired 
	private FrbService frbService;
	@Autowired
	LocaleContextProcessor localeContextProcessor;

	@RequestMapping(value = "/casino/frb/awardbonus")
	public @ResponseBody AwardBonusResponse handleAwardBonus(
		@RequestBody AwardBonusRequest request,
		@RequestParam(value = "locale", required = false) String locale
	) throws Exception {
		localeContextProcessor.setLocaleContextHolder(locale, request.getDomainName());
		CasinoFrbClient frbClient = frbService.getCasinoProviderFrbClient(request.getProviderGuid());
		
		if (frbClient != null) {
			log.debug("AwardBonusRequest : "+request);
			AwardBonusResponse response = frbClient.handleAwardBonus(request);
			frbService.dispatchAwardFrbEventToUser(request, response);
			log.debug("AwardBonusResponse: "+response);
			return response;
		}
		return null;
	}
	
	@RequestMapping(value = "/casino/frb/checkbonus")
	public @ResponseBody CheckBonusResponse handleCheckBonus(
		@RequestBody CheckBonusRequest request
	) throws Exception {
		CasinoFrbClient frbClient = frbService.getCasinoProviderFrbClient(request.getProviderGuid());
		
		if (frbClient != null) {
			return frbClient.handleCheckBonus(request);
		}
		
		return null;
	}
	
	@RequestMapping(value = "/casino/frb/cancelbonus")
	public @ResponseBody CancelBonusResponse handleCancelBonus(
		@RequestBody CancelBonusRequest request
	) throws Exception {
		CasinoFrbClient frbClient = frbService.getCasinoProviderFrbClient(request.getProviderGuid());
		
		if(frbClient != null) {
			return frbClient.handleCancelBonus(request);
		}
		
		return null;
	}

	@RequestMapping(value = "/casino/frb/getbonusinfo")
	public @ResponseBody GetBonusInfoResponse handleGetBonusInfo(
		@RequestBody GetBonusInfoRequest request
	) throws Exception {
		CasinoFrbClient frbClient = frbService.getCasinoProviderFrbClient(request.getProviderGuid());
		
		if (frbClient != null) {
			return frbClient.handleGetBonusInfo(request);
		}
		
		return null;
	}
	
	@RequestMapping(value = "/casino/frb/updateexternalid")
	public void updateExternalBonusId(
		@RequestBody UpdateBonusIdResponse request
	) {
		frbService.updateExternalBonusId(request);
	}	
}
