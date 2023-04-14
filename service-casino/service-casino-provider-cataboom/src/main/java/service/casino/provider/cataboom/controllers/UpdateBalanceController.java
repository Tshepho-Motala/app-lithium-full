
package service.casino.provider.cataboom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController; 	

import lithium.service.casino.client.data.BonusAllocate;
import lombok.extern.slf4j.Slf4j;
import service.casino.provider.cataboom.entities.Campaign;
import service.casino.provider.cataboom.entities.PrizeFullfilment;
import service.casino.provider.cataboom.objects.ReceivedParams;
import service.casino.provider.cataboom.objects.ResponseObj;
import service.casino.provider.cataboom.repositories.CampaignRepository;
import service.casino.provider.cataboom.repositories.InitialLinkRepository;
import service.casino.provider.cataboom.repositories.PrizeFullfilmentRepository;
import service.casino.provider.cataboom.repositories.UserRepository;
import service.casino.provider.cataboom.services.CataboomService;
import service.casino.provider.cataboom.services.UserService;
import service.casino.provider.cataboom.stream.CATConversionQueueProcessor;

@Slf4j
@RestController
public class UpdateBalanceController {
	@Autowired
	CataboomService service;
	@Autowired
	CATConversionQueueProcessor catService;
	@Autowired
	CampaignRepository repository;
	@Autowired
	PrizeFullfilmentRepository prizerepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	UserService userService;
	@Autowired
	InitialLinkRepository linkRepo;
	
	//endpoint required by cataboom to give bonus to player
	@PostMapping(value="/cataboom/prizefulfill")
	public ResponseObj giveBonus(@RequestBody ReceivedParams rp ) throws CataboomFailedException {
		
		log.info("reached update balance method " + rp.toString());
		ResponseObj response = new ResponseObj();
		
		String bonusCode = rp.getCampaignid() + "_" + rp.getWinlevel();
		log.info("bonusCode: " + bonusCode);
		BonusAllocate ba = BonusAllocate.builder().bonusCode(bonusCode).playerGuid(rp.getAccountid()).build();

		int winLevel = Integer.parseInt(rp.getWinlevel());
		if (winLevel < 1 || winLevel > 4) {
			response.setCode(1300);
			response.setMessage("Invalid win level");
			return response;
		}

		Campaign campToken = repository.findByToken(rp.getToken());
		if (campToken == null) {
			response.setCode(1000);
			response.setMessage("Unauthorized");
			return response;
		}

		Campaign campCampName = repository.findByCampaignName(rp.getCampaignid());
		if (campCampName == null) {
			response.setCode(1100);
			response.setMessage("Unrecognized Campaign ID");
			return response;
		}

		Campaign campFinal = repository.findByTokenAndCampaignName(rp.getToken(), rp.getCampaignid());
		if (campFinal == null ||campFinal.getEnabled()==false) {
			response.setCode(1400);
			response.setMessage("Unrecognized Campaign info");
			return response;
		}

		try {
			
			userService.createUserIfNotExist(rp.getAccountid());
			PrizeFullfilment pfs = PrizeFullfilment.builder()
					.description(rp.getDescription())
					.prizepin(rp.getPrizepin())
					.token(rp.getToken())
					.campaignid(rp.getCampaignid())
					.playerid(rp.getAccountid())
					.prizecode(rp.getPrizecode())
					.winlevel(rp.getWinlevel())
					.prizelink(rp.getPrizelink())
					.playcode(rp.getPlayid())
					.initialLink(linkRepo.findByPlayid(rp.getPlayid()))
					.user(userRepo.findByPlayerGuid(rp.getAccountid()))
					.build();
			prizerepo.save(pfs);
			catService.handle(ba);
			response.setCode(0);
			response.setMessage("Success");
			return response;
		} 
		catch (Exception e) {
			log.info(e.getMessage());
		}
		return response;
	}
	
	public static class CataboomFailedException extends Exception {
		public CataboomFailedException() {
			super();
		}

		public CataboomFailedException(String message) {
			super(message);
		}

		public CataboomFailedException(String message, Throwable cause) {
			super(message, cause);
		}

		public CataboomFailedException(Throwable cause) {
			super(cause);
		}
	}
	
}
	