package lithium.service.casino.provider.supera.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import lithium.service.Response.Status;
import lithium.service.casino.client.objects.response.AwardBonusResponse;
import lithium.service.casino.client.objects.response.Bonus;
import lithium.service.casino.client.objects.response.CancelBonusResponse;
import lithium.service.casino.client.objects.response.CheckBonusResponse;
import lithium.service.casino.client.objects.response.FreeroundBetConfigs;
import lithium.service.casino.client.objects.response.GameBetConfigResponse;
import lithium.service.casino.client.objects.response.GetBonusInfoResponse;
import lithium.service.casino.provider.supera.SuperaModuleInfo;
import lithium.service.casino.provider.supera.data.response.FreeroundAddResponse;
import lithium.service.casino.provider.supera.data.response.FreeroundBetDetails;
import lithium.service.casino.provider.supera.data.response.FreeroundBetsResponse;
import lithium.service.casino.provider.supera.data.response.FreeroundRemoveResponse;
import lithium.service.casino.provider.supera.data.response.FreeroundShowResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping(value="/casino/frb")
public class FreeroundBonusController {
	
	@Autowired
	protected SuperaModuleInfo moduleInfo;
	
	@Autowired private FreeroundAddController freeroundAddController;
	@Autowired private FreeroundBetsController freeroundBetsController;
	@Autowired private FreeroundShowController freeroundShowController;
	@Autowired private FreeroundRemoveController freeroundRemoveController;
	
	@Autowired private ModelMapper mapper;
	
	/**
	 * Sets the freeround bonus for a customer on the specified games
	 * 
	 * @param request
	 * @param apiAuthentication
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws RestClientException
	 */
	@RequestMapping(value = "/awardbonus")
	public @ResponseBody lithium.service.casino.client.objects.response.AwardBonusResponse awardBonus(
		@RequestBody lithium.service.casino.client.objects.request.AwardBonusRequest inRequest
	) throws Exception {
		log.info("AwardBonusRequest1 : "+inRequest);

		FreeroundAddResponse response = freeroundAddController.freeroundAdd(
				inRequest.getGames(), 
				inRequest.getUserId(), 
				inRequest.getFrbBetConfigId(), 
				inRequest.getRounds(),
				(inRequest.getDuration()!=null)?((Integer)inRequest.getDuration()/1000):null,
				(inRequest.getStartTime()!=null)?new DateTime(inRequest.getStartTime()):null,
				(inRequest.getExpirationTime()!=null)?new DateTime(inRequest.getExpirationTime()):null,
				inRequest.getDomainName());
		log.info("AwardBonusRequestResponse: "+response);
		if (response.getStatus() != HttpStatus.OK.value()) {
			log.error("AwardBonusRequestResponse: "+response);
			lithium.service.casino.client.objects.response.AwardBonusResponse finalResponse = new AwardBonusResponse(response.getResponse().getId());
			finalResponse.setResult(Status.INTERNAL_SERVER_ERROR.id()+"");
			return finalResponse;
		}
		
		lithium.service.casino.client.objects.response.AwardBonusResponse finalResponse = new AwardBonusResponse(response.getResponse().getId());
		finalResponse.setResult(Status.OK.id()+"");
		finalResponse.setCode(Status.OK.id()+"");
		log.debug("AwardBonusResponse: "+finalResponse);
		return finalResponse;
	}
	
	/**
	 * In case of network failure while awarding freeround bonus, the status of
	 * the bonus can be checked
	 * 
	 * @param request
	 * @param apiAuthentication
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws RestClientException
	 */
	@RequestMapping(value = "/checkbonus")
	public @ResponseBody lithium.service.casino.client.objects.response.CheckBonusResponse checkBonus(
		@RequestBody lithium.service.casino.client.objects.request.CheckBonusRequest inRequest
	) throws Exception {
		log.info("FreeroundBetController (unimplemented)" + inRequest);
		
		FreeroundShowResponse response = freeroundShowController.freeroundShow(inRequest.getGameId(), inRequest.getUserId(), inRequest.getDomainName());
		
		if (response.getStatus() != HttpStatus.OK.value()) {
			log.error("Check bonus response error: " + response);
			return new CheckBonusResponse(-1);
		}
		
		
		lithium.service.casino.client.objects.response.CheckBonusResponse finalResponse = new CheckBonusResponse(Integer.parseInt(inRequest.getExtBonusId()));
		
		return finalResponse;
	}
	
	/**
	 * Cancel a freeround bonus
	 * 
	 * @param request
	 * @param apiAuthentication
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws RestClientException 
	 */
	@RequestMapping(value = "/cancelbonus")
	public @ResponseBody lithium.service.casino.client.objects.response.CancelBonusResponse cancelBonus(
		@RequestBody lithium.service.casino.client.objects.request.CancelBonusRequest inRequest
	) throws Exception {
		
		log.info("FreeroundBetController " + inRequest);
		
		FreeroundRemoveResponse response = freeroundRemoveController.freeroundRemove(inRequest.getGameId(), inRequest.getUserId(), inRequest.getBonusId(), inRequest.getDomainName());
		
		if (response.getStatus() != HttpStatus.OK.value()) {
			log.error("AwardBonusRequestResponse: "+response);
			lithium.service.casino.client.objects.response.CancelBonusResponse finalResponse = new CancelBonusResponse();
			finalResponse.setResult(Status.INTERNAL_SERVER_ERROR.id()+"");
			return finalResponse;
		}
		
		lithium.service.casino.client.objects.response.CancelBonusResponse finalResponse = new CancelBonusResponse();
		finalResponse.setCode(Status.OK.id()+"");
		finalResponse.setResult(Status.OK.id()+"");
		
		return finalResponse;
	}
	
	/**
	 * Gets bonus info for a customer
	 * 
	 * @param request
	 * @param apiAuthentication
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws RestClientException 
	 */
	@RequestMapping(value = "/getbonusinfo")
	public @ResponseBody lithium.service.casino.client.objects.response.GetBonusInfoResponse getBonusInfo(
		@RequestBody lithium.service.casino.client.objects.request.GetBonusInfoRequest inRequest
	) throws Exception {
		FreeroundShowResponse response = freeroundShowController.freeroundShow(inRequest.getGameId(), inRequest.getUserId(), inRequest.getDomainName());
		
		if (response.getStatus() != HttpStatus.OK.value()) {
			log.error("Check bonus info response error: " + response);
			return new GetBonusInfoResponse(null);
		}
		
		List<Bonus> bonusList = new ArrayList<>();
		
		for(FreeroundBetDetails fbd: response.getResponse()) {
			Bonus b = new Bonus();
			b.setBonusId(fbd.getId());
			b.setAwardedDate(null);
			b.setDescription(fbd.getBet().toString());
			b.setDuration(null);
			b.setExpirationTime(null);
			b.setGameIds(inRequest.getGameId());
			b.setRounds(fbd.getCount());
			b.setRoundsLeft(fbd.getCount()-fbd.getUsed());
			b.setStartTime(null);
			
			bonusList.add(b);
		}
		
		lithium.service.casino.client.objects.response.GetBonusInfoResponse finalResponse = new GetBonusInfoResponse(bonusList);
		finalResponse.setCode(Status.OK.id()+"");
		finalResponse.setResult(Status.OK.id()+"");
		
		return finalResponse;

	}
	
	@RequestMapping(value = "/gamebetconfig")
	public @ResponseBody lithium.service.casino.client.objects.response.GameBetConfigResponse handleGetGameBetConfig(
		@RequestBody lithium.service.casino.client.objects.request.GameBetConfigRequest inRequest
	) throws Exception {
		
		log.info("FreeroundBetConfigController " + inRequest);
		
		FreeroundBetsResponse response = freeroundBetsController.freeroundBets(inRequest.getGameId(), inRequest.getUserId(), inRequest.getDomainName());
		
		if (response.getStatus() != HttpStatus.OK.value()) {
			log.error("GameBetConfigResponse: "+response);
			lithium.service.casino.client.objects.response.GameBetConfigResponse finalResponse = new GameBetConfigResponse(null);
			finalResponse.setResult(Status.INTERNAL_SERVER_ERROR.id()+"");
			return finalResponse;
		}
		
		lithium.service.casino.client.objects.response.GameBetConfigResponse finalResponse = 
				new GameBetConfigResponse(mapper.map(response.getResponse(), FreeroundBetConfigs.class));
		finalResponse.setCode(Status.OK.id()+"");
		finalResponse.setResult(Status.OK.id()+"");
		
		return finalResponse;
	}
}