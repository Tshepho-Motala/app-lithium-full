package lithium.service.casino.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.leader.LeaderCandidate;
import lithium.service.casino.client.data.EBalanceAdjustmentComponentType;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.entities.Winner;
import lithium.service.casino.data.entities.WinnerAugmentation;
import lithium.service.casino.data.objects.FrontendWinner;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.casino.data.repositories.WinnerAugmentationRepository;
import lithium.service.casino.data.repositories.WinnerRepository;
import lithium.service.domain.client.stream.DomainEventsStream;
import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WinnerFeedService {
	@Autowired
	private WinnerRepository winnerRepo;
	@Autowired
	private WinnerAugmentationRepository winnerAugRepo;
	@Autowired
	private DomainRepository domainRepo;
	@Autowired
	private DomainEventsStream domainEventStream;
	@Autowired
	private GameCacheService gameCacheService;
	
	@Autowired
	private GatewayExchangeStream gatewayExchangeStream;
	
	@Autowired
	private UserService userService;
	
	@Autowired LeaderCandidate leaderCandidate;
	
	//TODO: Add this to a domain config in future
	@Value("${lithium.service.casino.winnerfeed.wincents.threshold:5000}")
	private Long winnerThreshold;
	
	@Value("${lithium.service.casino.winnerfeed.uniquewinneronly:true}")
	private Boolean uniqueWinnerOnly;
	
	@Value("${lithium.service.casino.winnerfeed.nowin.thresholdminutes:5}")
	private Integer noWinThresholdMinutes;
	
	public List<Winner> getWinnersList(String domainName) {
		return winnerRepo.findTop50ByDomainNameOrderByCreatedDateDesc(domainName);
	}
	
	public List<FrontendWinner> getFrontendWinnersList(String domainName) {
		ArrayList<Winner> winnerList = (ArrayList<Winner>) winnerRepo.findTop50ByDomainNameOrderByCreatedDateDesc(domainName);
		ArrayList<FrontendWinner> feWinnerList = new ArrayList<>(winnerList.size());
		for (Winner winner: winnerList) {
			if (winner.getGuidHash() == null) {
				performEnrichmentForWinner(winner);
				winnerRepo.save(winner);
			}
			FrontendWinner feWinner = FrontendWinner.builder()
			.amount(winner.getAmount())
			.createdDate(winner.getCreatedDate())
			.domainName(winner.getDomain().getName())
			.firstName(winner.getFirstName())
			.gameName(winner.getGameName())
			.guid(winner.getGuidHash())
			.build();
			feWinnerList.add(feWinner);
		}
		return feWinnerList;
	}
	
	void performEnrichmentForWinner(Winner winner) {
		
		User user = userService.svcUserGetUser(winner.getDomain().getName(), winner.getUserName());

		if (user != null) {
			winner.setGuidHash(userService.hashSha256UserGuid(user));
			winner.setFirstName(user.getFirstName());
		}

	}
	
	public void addWinner(Winner winner) {
		if (winner.getAmount() >= winnerThreshold) {
			if (uniqueWinnerOnly != null && uniqueWinnerOnly == true) {
				 Winner repoWinner = winnerRepo.findFirst1ByDomainNameAndUserName(winner.getDomain().getName(), winner.getUserName());
				 if (repoWinner != null) winnerRepo.delete(repoWinner);
			}
			performEnrichmentForWinner(winner);
			winnerRepo.save(winner);
			
			try {
				FrontendWinner feWinner = FrontendWinner.builder()
				.amount(winner.getAmount())
				.createdDate(winner.getCreatedDate())
				.domainName(winner.getDomain().getName())
				.firstName(winner.getFirstName())
				.gameName(winner.getGameName())
				.guid(winner.getGuidHash())
				.build();
				gatewayExchangeStream.process(winner.getDomain().getName()+"/"+"scratchcardwinners", "scratchcardwinners", new ObjectMapper().writeValueAsString(feWinner));
				gatewayExchangeStream.process(winner.getDomain().getName()+"/"+"winnersfeedlist", "winnersfeedlist", new ObjectMapper().writeValueAsString(feWinner));
			} catch (JsonProcessingException e) {
				log.error("Unable to publish winner update", e);
			}
			//domainEventStream.process(winner.getDomain().getName(), "getWinnerUpdate", "New winner recorded", winner.toString());
		}
	}
	
	@Scheduled(cron="${lithium.service.casino.winnerfeed.cleanup.cron:*/60 * * * * *}")
	public void winnerListCleanup() {
		
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		
		Iterable<Domain> domainList = domainRepo.findAll();
		
		domainList.forEach(domain -> {
			List<Winner> winnerList = winnerRepo.findTop50ByDomainNameOrderByIdDesc(domain.getName());
			if (winnerList.size() < 50) return;
			
			Long smallestId = Long.MAX_VALUE;
			for(Winner winner : winnerList) {
				if (smallestId > winner.getId()) smallestId = winner.getId();
			};
			if (smallestId.longValue() != Long.MAX_VALUE) {
				winnerRepo.deleteAll(winnerRepo.findByIdLessThanAndDomainName(smallestId, domain.getName()));
			}
		});
	}
	
	public void addExtraWinner(WinnerAugmentation winner) {
		winnerAugRepo.save(winner);
	}
	
	/**
	 * Run through domains, if no winner was recorded for 5 minutes, add a winner from the manual winners table 
	*/
	@Scheduled(cron="${lithium.service.casino.winnerfeed.augmentation.cron:*/60 * * * * *}")
	public void augmentWinnersList() {
		
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		
		Iterable<Domain> domainList = domainRepo.findAll();
		DateTime tmpDate = new DateTime();
		final DateTime winnerReplacementDate = tmpDate.minusMinutes(noWinThresholdMinutes);
		domainList.forEach(domain -> {
			
			Winner lastWinner = winnerRepo.findTop1ByDomainNameOrderByCreatedDateDesc(domain.getName());
			if (lastWinner != null && lastWinner.getCreatedDate().before(winnerReplacementDate.toDate())) {
				WinnerAugmentation waug = winnerAugRepo.findTop1ByDomainNameOrderByIdAsc(domain.getName());
				
				if (waug == null) {
					log.trace("There are no winners in the augmentation list. Please add some for domain: " + domain.getName());
				} else {
					Winner winner = Winner.builder()
							.amount(waug.getAmount() < winnerThreshold ? waug.getAmount()+winnerThreshold : waug.getAmount())
							.createdDate((DateTime.now()).toDate())
							.domain(waug.getDomain())
							.gameName(waug.getGameName())
							.userName(waug.getUserName())
							.build();
					
					addWinner(winner);
					
					winnerAugRepo.deleteById(waug.getId());
				}
			}
		});
	}

	@Async
	public void addWinner(final BetRequest request) {
		request.getGameGuid();
		addWinner(Winner.builder()
		.amount(request.getWin())
		.domain(findOrCreateDomain(request.getDomainName()))
		.gameName(gameCacheService.findGameNameByGuidAndDomain(request.getGameGuid(), request.getDomainName()))
		.userName(request.getUserGuid())
		.build());
	}
	
	@Async
	public void addWinner(final BalanceAdjustmentRequest request) {
		request.getAdjustmentComponentList().forEach(adjustmentComponent -> {
			if (adjustmentComponent.getAdjustmentType() == EBalanceAdjustmentComponentType.CASINO_WIN ||
				adjustmentComponent.getAdjustmentType() == EBalanceAdjustmentComponentType.CASINO_FREEROUND_WIN ||
				adjustmentComponent.getAdjustmentType() == EBalanceAdjustmentComponentType.CASINO_FREEROUND_FEATURE_WIN) {
				request.getGameGuid();
				addWinner(Winner.builder()
				.amount(adjustmentComponent.getAmountAbs())
				.domain(findOrCreateDomain(request.getDomainName()))
				.gameName(gameCacheService.findGameNameByGuidAndDomain(request.getGameGuid(), request.getDomainName()))
				.userName(request.getUserGuid())
				.build());
			}
		});
	}

	public void addWinner(lithium.service.casino.client.data.Winner winner) {
		addWinner(Winner.builder()
		.amount(winner.getAmount())
		.domain(findOrCreateDomain(winner.getDomainName()))
		.gameName(winner.getGameName())
		.userName(winner.getUserName())
		.build());
	}
	
	@Retryable
	public Domain findOrCreateDomain(String name) {
		Domain domain = domainRepo.findByName(name);
		if (domain == null) {
			domain = Domain.builder().name(name).build();
			domainRepo.save(domain);
		}
		return domain;
	}
}
