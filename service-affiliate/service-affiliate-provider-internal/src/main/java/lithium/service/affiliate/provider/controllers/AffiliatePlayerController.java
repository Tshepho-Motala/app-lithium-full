package lithium.service.affiliate.provider.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.affiliate.client.exception.AdNotFoundException;
import lithium.service.affiliate.client.exception.AffiliateNotFoundException;
import lithium.service.affiliate.client.exception.AffiliatePlayerAlreadyExistsException;
import lithium.service.affiliate.client.exception.AffiliateProviderNotFoundException;
import lithium.service.affiliate.client.exception.UserNotFoundException;
import lithium.service.affiliate.client.exception.UserProviderNotFoundException;
import lithium.service.affiliate.client.objects.AffiliatePlayerBasic;
import lithium.service.affiliate.provider.data.entities.AffiliatePlayer;
import lithium.service.affiliate.provider.service.AffiliatePlayerService;
import lithium.service.affiliate.provider.service.AffiliateService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
// no tight coupling between client and service (so no implementation or dependence on service-client
public class AffiliatePlayerController {
	@Autowired private AffiliateService affiliateService;
	@Autowired private AffiliatePlayerService affiliatePlayerService;
	
	@RequestMapping("/affiliate/player/{affiliateGuid}/{bannerGuid}/add")
	public Response<AffiliatePlayer> addAffiliatePlayer(@PathVariable("affiliateGuid") String affiliateGuid, @PathVariable("bannerGuid") String bannerGuid, @RequestParam("playerGuid") String playerGuid) throws Exception {
		AffiliatePlayerBasic player = AffiliatePlayerBasic.builder()
				.primaryGuid(affiliateGuid)
				.secondaryGuid(bannerGuid)
				.playerGuid(playerGuid).build();

		return addAffiliatePlayer(player);
	}
	
	@RequestMapping("/affiliate/player/{affiliateGuid}/{bannerGuid}/{tertiaryGuid}/add")
	public Response<AffiliatePlayer> addAffiliatePlayer(@PathVariable("affiliateGuid") String affiliateGuid, @PathVariable("bannerGuid") String bannerGuid, @PathVariable("tertiaryGuid") String tertiaryGuid, @RequestParam("playerGuid") String playerGuid) throws Exception {
		AffiliatePlayerBasic player = AffiliatePlayerBasic.builder()
				.primaryGuid(affiliateGuid)
				.secondaryGuid(bannerGuid)
				.tertiaryGuid(tertiaryGuid)
				.playerGuid(playerGuid).build();

		return addAffiliatePlayer(player);
	}
	
	@RequestMapping("/affiliate/player/{affiliateGuid}/{bannerGuid}/{tertiaryGuid}/{quaternaryGuid}/add")
	public Response<AffiliatePlayer> addAffiliatePlayer(@PathVariable("affiliateGuid") String affiliateGuid, @PathVariable("bannerGuid") String bannerGuid, @PathVariable("tertiaryGuid") String tertiaryGuid, @PathVariable("quaternaryGuid") String quaternaryGuid, @RequestParam("playerGuid") String playerGuid) throws Exception {
		AffiliatePlayerBasic player = AffiliatePlayerBasic.builder()
				.primaryGuid(affiliateGuid)
				.secondaryGuid(bannerGuid)
				.tertiaryGuid(tertiaryGuid)
				.quaternaryGuid(quaternaryGuid)
				.playerGuid(playerGuid).build();

		return addAffiliatePlayer(player);
	}
	
	@RequestMapping("/affiliate/player/add")
	public Response<AffiliatePlayer> addAffiliatePlayer(@RequestBody AffiliatePlayerBasic player) throws Exception {
		
		Response<AffiliatePlayer> response = Response.<AffiliatePlayer>builder().build();
		try {
			
			AffiliatePlayer affiliatePlayer = affiliatePlayerService.addAffiliatePlayer(player);
			response.setStatus(Status.OK);
			response.setData(affiliatePlayer);
			
		} catch (AffiliateNotFoundException e) {
			response.setStatus(Status.INVALID_DATA);
			response.setMessage("Invalid affiliate guid.");
		} catch (AffiliateProviderNotFoundException e) {
			response.setStatus(Status.INTERNAL_SERVER_ERROR);
			response.setMessage("Affiliate provider not found.");
		} catch (AdNotFoundException e) {
			response.setStatus(Status.INVALID_DATA);
			response.setMessage("Invalid ad or banner guid.");
		} catch (UserProviderNotFoundException e) {
			response.setStatus(Status.INTERNAL_SERVER_ERROR);
			response.setMessage("User provider not found.");
		} catch (UserNotFoundException e) {
			response.setStatus(Status.INVALID_DATA);
			response.setMessage("Invalid user guid.");
		} catch (AffiliatePlayerAlreadyExistsException e) {
			response.setStatus(Status.CONFLICT);
			response.setMessage("The user is already affiliated");
		}

		return response;
	}

	
	@RequestMapping("/affiliate/player/find")
	public Response<AffiliatePlayer> findAffiliatePlayer(@RequestParam("playerGuid") String playerGuid) throws Exception {
		return Response.<AffiliatePlayer>builder().data(affiliatePlayerService.findUserByGuid(playerGuid)).status(Status.OK).build();
	}
	
	@RequestMapping("/affiliate/player/edit")
	public Response<AffiliatePlayer> editAffiliatePlayer(@RequestBody AffiliatePlayer player) throws Exception {
		
		Response<AffiliatePlayer> response = Response.<AffiliatePlayer>builder().build();
		
		try {
			
			AffiliatePlayer affiliatePlayer = affiliatePlayerService.editAffiliatePlayer(player);
			response.setStatus(Status.OK);
			response.setData(affiliatePlayer);
			
		} catch (AffiliateNotFoundException e) {
			response.setStatus(Status.INVALID_DATA);
			response.setMessage("Invalid affiliate guid.");
		} catch (AffiliateProviderNotFoundException e) {
			response.setStatus(Status.INTERNAL_SERVER_ERROR);
			response.setMessage("Affiliate provider not found.");
		} catch (AdNotFoundException e) {
			response.setStatus(Status.INVALID_DATA);
			response.setMessage("Invalid ad or banner guid.");
		} catch (UserNotFoundException e) {
			response.setStatus(Status.INVALID_DATA);
			response.setMessage("Invalid user guid.");
		}
		
		return response;
	}
}
