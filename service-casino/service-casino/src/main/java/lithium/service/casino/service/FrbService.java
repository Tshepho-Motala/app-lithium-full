package lithium.service.casino.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.Response;
import lithium.service.casino.client.CasinoFrbClient;
import lithium.service.casino.client.objects.request.AwardBonusRequest;
import lithium.service.casino.client.objects.response.AwardBonusResponse;
import lithium.service.casino.client.objects.response.UpdateBonusIdResponse;
import lithium.service.casino.data.entities.PlayerBonusFreespinHistory;
import lithium.service.casino.data.repositories.PlayerBonusFreespinHistoryRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.Game;
import lithium.service.user.client.UserEventClient;
import lithium.service.user.client.objects.UserEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FrbService {
	public static final String FREESPIN_USER_EVENT = "freespins";
	
	@Autowired
	private LithiumServiceClientFactory services;
	
	@Autowired
	private PlayerBonusFreespinHistoryRepository playerBonusFreespinHistoryRepository;

	
	public HashMap<String, CasinoFrbClient> getCasinoProviderFrbClientMap() throws Exception {

		HashMap<String, CasinoFrbClient> frbClientHash = new HashMap<>();
		getActiveProviders().forEach(p-> {
			log.debug("Getting frb client for: " + p);
			CasinoFrbClient frbClient = getCasinoProviderFrbClient(p.getUrl());
			if(frbClient != null) {
				frbClientHash.put(p.getUrl(), frbClient);
			}
			log.debug("Finished getting frb client for: " + p);
		});

		return frbClientHash;
	}
	
	public CasinoFrbClient getCasinoProviderFrbClient(String providerUrl) {
		if (providerUrl == null || providerUrl.trim().isEmpty()) {
			log.error("Provider url for frb client was empty", new Exception("stupid frb"));
		}
		try {
			log.debug("Getting frb client for: " + providerUrl);
			return services.target(CasinoFrbClient.class, providerUrl, true);
		} catch (Exception e) {
			log.error("Problem getting frb client for provider: " + providerUrl, e);
		}
		return null;
	}
	
	public void dispatchAwardFrbEventToUser(AwardBonusRequest abRequest, AwardBonusResponse abResponse) {
		log.info("AwardBonusRequest: "+abRequest+", AwardBonusResponse: "+abResponse);
		Integer bonusId = ((abResponse!=null) && (abResponse.getBonusId()!=null))?abResponse.getBonusId():-1;
		if (((bonusId != null) && (bonusId > 0)) || abResponse.getResult().equalsIgnoreCase("OK")) {
			try {
				Response<UserEvent> userEvent = getUserEventService().registerEvent(
					abRequest.getDomainName(),
					abRequest.getUserId().substring(abRequest.getUserId().indexOf("/") + 1, abRequest.getUserId().length()),
					UserEvent.builder()
					.type(FREESPIN_USER_EVENT)
					.data(buildAwardFreespinPayload(abRequest))
					.message("Freespins allocated")
					.build()
				);
				log.info("Frb user event sent with response: " + userEvent);
			} catch (Exception e) {
				log.warn("Problem processing user freespin award event", e);
			}
		} else {
			log.warn("Did not dispatch freespin allocation event since no bonus id was returned from provider");
		}
	}
	
	private String buildAwardFreespinPayload(AwardBonusRequest request) {
		final String gamesPipeSep = request.getGames();
		final String providerGuid = request.getProviderGuid();
		Long numberOfFreespins = request.getRounds().longValue();
		String domainName = request.getDomainName();
		FrbGamePayloadObject payload = new FrbGamePayloadObject();
		payload.setNumberOfFreespins(numberOfFreespins);
		ObjectMapper mapper = new ObjectMapper();
		
		Iterable<Game> gameList;
		try {
			gameList = getGameService().listDomainGames(domainName).getData();
		} catch (Exception e1) {
			log.warn("Unable to get game list for domain: " + domainName, e1);
			return null;
		}
		
		StreamSupport.stream(gameList.spliterator(), false)
		.filter(g -> {
			if (g.getProviderGuid().equalsIgnoreCase(providerGuid)) {
				Optional<String> gameFound = Arrays.stream(gamesPipeSep.split("\\|"))
				.filter(gid -> gid.equals(g.getProviderGameId()))
				.findAny();
				if (gameFound.isPresent()) {
					return true;
				}
			}
			return false;
		}).forEach(g -> {
			payload.getGames().add(g);
		});
		
		try {
			return mapper.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			log.warn("Unable to parse payload for award freespins to json: " + payload, e);
		}
		return null;
	}
	
	private ProviderClient getProviderClient() throws Exception {
		return services.target(ProviderClient.class,"service-domain", true);
	}
	
	private Iterable<Provider> getActiveProviders() throws Exception {
		ProviderClient pc = getProviderClient();
		
		ArrayList<Provider> activeProviderList = new ArrayList<>();
		
		Iterable<Provider> providerList = pc.listAllProvidersByType(ProviderConfig.ProviderType.CASINO.type()).getData();
		providerList.forEach(p -> {
			
			if(p.getEnabled() == true) {
				activeProviderList.add(p);
			}
			
		});
		
		return activeProviderList;
	}
	
	private UserEventClient getUserEventService() throws Exception {
		UserEventClient cl = null;
		
		cl = services.target(UserEventClient.class, "service-user", true);
		
		return cl;
	}
	
	private GamesClient getGameService() throws Exception {
		GamesClient cl = null;
		
		cl = services.target(GamesClient.class, "service-games", true);
		
		return cl;
	}

	@Data
	@ToString
	@NoArgsConstructor
	@EqualsAndHashCode
	class FrbGamePayloadObject implements Serializable {
		private static final long serialVersionUID = 1L;
		Long numberOfFreespins = 0L;
		List<Game> games = new ArrayList<>();
	}
	
	public void updateExternalBonusId(UpdateBonusIdResponse request) {
		
		PlayerBonusFreespinHistory pbfh = playerBonusFreespinHistoryRepository.findByPlayerBonusHistoryIdAndExtBonusId(request.getExternalBonusId(), -1);
		pbfh.setExtBonusId(request.getBonusId());
		
		pbfh = playerBonusFreespinHistoryRepository.save(pbfh);
		
		log.info("Updated external bonus id using retry" + pbfh.toString());
		
	}
}
