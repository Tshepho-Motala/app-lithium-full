package lithium.service.casino.controllers;

import java.util.Date;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.casino.api.frontend.schema.PlayerBonusHistoryTableResponse;
import lithium.service.casino.client.data.BonusAllocate;
import lithium.service.casino.client.data.BonusAllocatev2;
import lithium.service.casino.client.data.BonusDisplayMinimal;
import lithium.service.casino.client.data.BonusHourly;
import lithium.service.casino.client.data.CasinoBonus;
import lithium.service.casino.client.data.CasinoBonusCheck;
import lithium.service.casino.client.data.CasinoBonusv2;
import lithium.service.casino.client.objects.BonusRevisionRequest;
import lithium.service.casino.client.objects.response.GetBonusInfoResponse;
import lithium.service.casino.data.entities.AutoBonusAllocation;
import lithium.service.casino.data.entities.Bonus;
import lithium.service.casino.data.entities.BonusFileUpload;
import lithium.service.casino.data.entities.BonusRequirementsDeposit;
import lithium.service.casino.data.entities.BonusRequirementsSignup;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.BonusRulesCasinoChip;
import lithium.service.casino.data.entities.BonusRulesCasinoChipGames;
import lithium.service.casino.data.entities.BonusRulesFreespinGames;
import lithium.service.casino.data.entities.BonusRulesFreespins;
import lithium.service.casino.data.entities.BonusRulesGamesPercentages;
import lithium.service.casino.data.entities.BonusRulesInstantReward;
import lithium.service.casino.data.entities.BonusRulesInstantRewardFreespin;
import lithium.service.casino.data.entities.BonusRulesInstantRewardFreespinGames;
import lithium.service.casino.data.entities.BonusRulesInstantRewardGames;
import lithium.service.casino.data.entities.BonusUnlockGames;
import lithium.service.casino.data.entities.GameCategory;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.entities.PlayerBonusPending;
import lithium.service.casino.data.entities.PlayerBonusToken;
import lithium.service.casino.data.objects.ActiveBonus;
import lithium.service.casino.data.objects.PlayerBonusHistoryDisplay;
import lithium.service.casino.data.objects.PlayerBonusHistoryPage;
import lithium.service.casino.data.projection.entities.PlayerBonusDisplay;
import lithium.service.casino.data.projection.entities.PlayerBonusFreespinHistoryProjection;
import lithium.service.casino.data.projection.entities.PlayerBonusHistoryActivationProjection;
import lithium.service.casino.data.projection.entities.PlayerBonusPendingProjection;
import lithium.service.casino.exceptions.InvalidBonusException;
import lithium.service.casino.exceptions.Status421InvalidFileDataException;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.casino.exceptions.Status510GeneralCasinoExecutionException;
import lithium.service.casino.service.AutoBonusAllocationService;
import lithium.service.casino.service.BonusTokenService;
import lithium.service.casino.service.CasinoBonusCasinoChipService;
import lithium.service.casino.service.CasinoBonusFileUploadService;
import lithium.service.casino.service.CasinoBonusFreespinService;
import lithium.service.casino.service.CasinoBonusInstantRewardFreespinService;
import lithium.service.casino.service.CasinoBonusInstantRewardService;
import lithium.service.casino.service.CasinoBonusService;
import lithium.service.casino.service.CasinoBonusUnlockGamesService;
import lithium.service.casino.service.CasinoMailSmsService;
import lithium.service.casino.service.CasinoTriggerBonusService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.user.client.objects.UserEvent;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static lithium.service.Response.Status.CONFLICT;
import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;
import static lithium.service.Response.Status.OK_SUCCESS;

//permission
//cancel user
// check vir selfde trans
// max payout check

@Slf4j
@RestController
@RequestMapping("/casino/bonus")
public class CasinoBonusController {
	@Autowired private TokenStore tokenStore;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private CasinoBonusService casinoBonusService;
	@Autowired private CasinoBonusFreespinService casinoBonusFreespinService;
	@Autowired private CasinoBonusInstantRewardService casinoBonusInstantRewardService;
	@Autowired private CasinoBonusInstantRewardFreespinService casinoBonusInstantRewardFreespinService;
	@Autowired private CasinoBonusCasinoChipService casinoBonusCasinoChipService;
	@Autowired private CasinoBonusUnlockGamesService casinoBonusUnlockGamesService;
	@Autowired private CasinoMailSmsService casinoMailSmsService;
	@Autowired private AutoBonusAllocationService autoBonusAllocationService;
	@Autowired private CasinoTriggerBonusService casinoTriggerBonusService;
	@Autowired private CasinoBonusFileUploadService casinoBonusFileUploadService;
	@Autowired private BonusTokenService bonusTokenService;
	@Autowired private LimitInternalSystemService limitInternalSystemService;
	@Autowired private LocaleContextProcessor localeContextProcessor;

	@GetMapping("/find/game/categories")
	public Response<Iterable<GameCategory>> gameCategories() throws UnsupportedEncodingException {
		return Response.<Iterable<GameCategory>>builder().status(OK).data(casinoBonusService.gameCategories()).build();
	}
	@GetMapping("/find/game/category/{category}")
	public Response<GameCategory> findByCasinoCategory(@PathVariable("category") String category) throws UnsupportedEncodingException {
		return Response.<GameCategory>builder().status(OK).data(casinoBonusService.findByCasinoCategory(category)).build();
	}
	@GetMapping("/delete/rule/percentage/{id}")
	public Response<Boolean> deleteBonusRulesGamesPercentage(@PathVariable("id") Long id) throws UnsupportedEncodingException {
		casinoBonusService.deleteBonusRulesGamesPercentage(id);
		return Response.<Boolean>builder().status(OK).data(true).build();
	}
	
	@DeleteMapping("/unlockgame/{id}")
	public Response<?> deleteBonusUnlockGame(@PathVariable("id") Long id) {
		casinoBonusUnlockGamesService.deleteUnlockGames(id);
		return Response.<Boolean>builder().status(OK).data(true).build();
	}
	
	@GetMapping("/find/rules/{bonusRevisionId}/percentages/categories")
	public Response<List<BonusRulesGamesPercentages>> percentageCategories(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		return Response.<List<BonusRulesGamesPercentages>>builder().status(OK).data(casinoBonusService.bonusRulesGamesPercentageCategories(bonusRevisionId)).build();
	}
	
	@GetMapping("/find/unlockgames/{bonusRevisionId}")
	public Response<List<BonusUnlockGames>> unlockgames(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		List<BonusUnlockGames> bonusUnlockGames = casinoBonusUnlockGamesService.unlockGames(bonusRevisionId);
		return Response.<List<BonusUnlockGames>>builder().status(OK).data(bonusUnlockGames).build();
	}
	
	@GetMapping("/find/freespins/{bonusRevisionId}")
	public Response<List<BonusRulesFreespins>> freespinRules(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		List<BonusRulesFreespins> bonusRulesFreespins = casinoBonusFreespinService.freespinRules(bonusRevisionId);
		for (BonusRulesFreespins brf:bonusRulesFreespins) {
			List<BonusRulesFreespinGames> games = casinoBonusFreespinService.freespinGamesPerRule(brf.getId());
			brf.setBonusRulesFreespinGames(games);
		}
		return Response.<List<BonusRulesFreespins>>builder().status(OK).data(bonusRulesFreespins).build();
	}

	@GetMapping("/find/casino-chip/{bonusRevisionId}")
	public Response<List<BonusRulesCasinoChip>> casinoChipRules(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		List<BonusRulesCasinoChip> bonusRulesCasinoChips = casinoBonusCasinoChipService.casinoChipRules(bonusRevisionId);
		for (BonusRulesCasinoChip brcc:bonusRulesCasinoChips) {
			List<BonusRulesCasinoChipGames> games = casinoBonusCasinoChipService.casinoChipGamesPerRule(brcc.getId());
			brcc.setBonusRulesCasinoChipGames(games);
		}
		return Response.<List<BonusRulesCasinoChip>>builder().status(OK).data(bonusRulesCasinoChips).build();
	}

	@GetMapping("/find/casino-chip/games/{bonusRevisionId}")
	public Response<List<BonusRulesCasinoChipGames>> casinoChipGames(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		return Response.<List<BonusRulesCasinoChipGames>>builder().status(OK).data(casinoBonusCasinoChipService.casinoChipGames(bonusRevisionId)).build();
	}

	@GetMapping("/find/freespin/games/{bonusRevisionId}")
	public Response<List<BonusRulesFreespinGames>> freespinGames(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		return Response.<List<BonusRulesFreespinGames>>builder().status(OK).data(casinoBonusFreespinService.freespinGames(bonusRevisionId)).build();
	}

	@GetMapping("/find/instant-reward/{bonusRevisionId}")
	public Response<List<BonusRulesInstantReward>> instantRewardRules(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		List<BonusRulesInstantReward> rewardRulesInstantRewards = casinoBonusInstantRewardService.instantRewardRules(bonusRevisionId);
		for (BonusRulesInstantReward rrir:rewardRulesInstantRewards) {
			List<BonusRulesInstantRewardGames> games = casinoBonusInstantRewardService.instantRewardGamesPerRule(rrir.getId());
			rrir.setBonusRulesInstantRewardGames(games);
		}
		return Response.<List<BonusRulesInstantReward>>builder().status(OK).data(rewardRulesInstantRewards).build();
	}

	@GetMapping("/find/instant-reward/games/{bonusRevisionId}")
	public Response<List<BonusRulesInstantRewardGames>> instantRewardGames(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		return Response.<List<BonusRulesInstantRewardGames>>builder().status(OK).data(casinoBonusInstantRewardService.instantRewardGames(bonusRevisionId)).build();
	}

	@GetMapping("/find/instant-reward-freespins/{bonusRevisionId}")
	public Response<List<BonusRulesInstantRewardFreespin>> instantRewardFreespinRules(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		List<BonusRulesInstantRewardFreespin> rewardRulesInstantRewardFreespins = casinoBonusInstantRewardFreespinService.instantRewardFreespinRules(bonusRevisionId);
		for (BonusRulesInstantRewardFreespin rrirfs:rewardRulesInstantRewardFreespins) {
			List<BonusRulesInstantRewardFreespinGames> games = casinoBonusInstantRewardFreespinService.instantRewardFreespinGamesPerRule(rrirfs.getId());
			rrirfs.setBonusRulesInstantRewardFreespinGames(games);
		}
		return Response.<List<BonusRulesInstantRewardFreespin>>builder().status(OK).data(rewardRulesInstantRewardFreespins).build();
	}

	@GetMapping("/find/instant-reward-freespins/games/{bonusRevisionId}")
	public Response<List<BonusRulesInstantRewardFreespinGames>> instantRewardFreespinGames(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		return Response.<List<BonusRulesInstantRewardFreespinGames>>builder().status(OK).data(casinoBonusInstantRewardFreespinService.instantRewardFreespinGames(bonusRevisionId)).build();
	}
	
	@GetMapping("/find/games/percentages/{bonusRevisionId}")
	public Response<List<BonusRulesGamesPercentages>> bonusRulesGamesPercentages(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		return Response.<List<BonusRulesGamesPercentages>>builder().status(OK).data(casinoBonusService.bonusRulesGamesPercentages(bonusRevisionId)).build();
	}
	
	@GetMapping("/find/requirements/deposit/{bonusRevisionId}")
	public Response<List<BonusRequirementsDeposit>> findDepositBonusRequirements(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		return Response.<List<BonusRequirementsDeposit>>builder().status(OK).data(casinoBonusService.findDepositBonusRequirements(bonusRevisionId)).build();
	}
	
	@GetMapping("/find/requirements/signup/{bonusRevisionId}")
	public Response<List<BonusRequirementsSignup>> findSignupBonusRequirements(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		return Response.<List<BonusRequirementsSignup>>builder().status(OK).data(casinoBonusService.findSignupBonusRequirements(bonusRevisionId)).build();
	}
	
	@GetMapping("/find/id/{bonusId}")
	public Response<Bonus> findById(@PathVariable("bonusId") Long bonusId) throws UnsupportedEncodingException {
		Bonus bonus = casinoBonusService.findBonus(bonusId);
		return Response.<Bonus>builder().status(OK).data(bonus).build();
	}
	@GetMapping("/find/{bonusRevisionId}")
	public Response<Bonus> find(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws UnsupportedEncodingException {
		return Response.<Bonus>builder().status(OK).data(casinoBonusService.findCurrentBonus(bonusRevisionId)).build();
	}
	@GetMapping("/find/bonusrevision/{bonusRevisionId}")
	public Response<BonusRevision> findByBonusRevisionId(@PathVariable("bonusRevisionId") Long bonusRevisionId) throws Exception {
		return Response.<BonusRevision>builder().status(OK).data(casinoBonusService.findBonusRevisionById(bonusRevisionId)).build();
	}
	@GetMapping("/find/bonusrevision/byid/{bonusId}")
	public Response<BonusRevision> findByBonusId(@PathVariable("bonusId") Long bonusId) throws Exception {
		return Response.<BonusRevision>builder().status(OK).data(casinoBonusService.findBonus(bonusId).getCurrent()).build();
	}
	@GetMapping("/find/active")
	public Response<PlayerBonusDisplay> active(Principal principal) throws UnsupportedEncodingException {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
		return active(util.guid());
	}
	@GetMapping("/find/active/p")
	public Response<PlayerBonusDisplay> active(@RequestParam("playerGuid") String playerGuid) throws UnsupportedEncodingException {
		return Response.<PlayerBonusDisplay>builder().status(OK).data(casinoBonusService.playerBonusDisplay(URLDecoder.decode(playerGuid, "UTF-8"), null)).build();
	}

	@GetMapping("/find/history/p")
	public Response<PlayerBonusHistoryPage> history(@RequestParam("playerGuid") String playerGuid, @RequestParam("page") int page) throws UnsupportedEncodingException {
		Page<PlayerBonusHistory> playerBonusHistoryPage = casinoBonusService.findPlayerBonusHistory(URLDecoder.decode(playerGuid, "UTF-8"), PageRequest.of(page, 5, Sort.by(Direction.DESC, "startedDate")));
		List<PlayerBonusHistoryDisplay> playerBonusHistoryDisplayList = new ArrayList<PlayerBonusHistoryDisplay>();
		for (PlayerBonusHistory playerBonusHistory: playerBonusHistoryPage.getContent()) {
			PlayerBonusFreespinHistoryProjection playerBonusFreespinHistoryProjection = casinoBonusFreespinService.playerBonusFreespinHistoryProjection(playerBonusHistory.getId());
			PlayerBonusHistoryDisplay playerBonusHistoryDisplay = PlayerBonusHistoryDisplay.builder()
					.playerBonusHistory(playerBonusHistory)
					.playerBonusFreespinHistoryProjection(playerBonusFreespinHistoryProjection)
					.build();
			playerBonusHistoryDisplayList.add(playerBonusHistoryDisplay);
		}
		PlayerBonusHistoryPage pbhp = PlayerBonusHistoryPage.builder()
				.list(playerBonusHistoryDisplayList)
				.hasMore(playerBonusHistoryPage.hasNext())
				.build();
		return Response.<PlayerBonusHistoryPage>builder().status(OK).data(pbhp).build();
	}

	@PostMapping("/find/bonus-token/table/player")
	public DataTableResponse<PlayerBonusToken> bonusTokenPlayerTable(@RequestParam("playerGuid") String playerGuid, DataTableRequest dataTableRequest) throws UnsupportedEncodingException {
		Page<PlayerBonusToken> playerBonusTokenPage = bonusTokenService.findActiveBonusTokensForPlayer(playerGuid, dataTableRequest);
		return new DataTableResponse<PlayerBonusToken>(dataTableRequest, playerBonusTokenPage);
	}

	@PostMapping("/find/bonus-token/active/table")
	public DataTableResponse<ActiveBonus> bonusTokensTable(@RequestParam(value = "domains[]", required = false) String domains, @RequestParam(value = "bonusCodes[]", required = false) String bonusCodes,
														   @RequestParam(value = "status", required = false) String status,@RequestParam("dateRangeFrom") @DateTimeFormat(pattern="yyyy-MM-dd") Date dateRangeFrom,
														   @RequestParam("dateRangeTo") @DateTimeFormat(pattern="yyyy-MM-dd") Date dateRangeTo,
														   DataTableRequest dataTableRequest) {
		dataTableRequest.setPageRequest(PageRequest.of(dataTableRequest.getPageRequest().getPageNumber(),
				dataTableRequest.getPageRequest().getPageSize() > 100 ? 100 : dataTableRequest.getPageRequest().getPageSize(),
				Sort.by(Direction.DESC, "startedDate")));

		String [] activeDomains;
		Page<ActiveBonus> activeBonuses;
		if(domains != null && domains.length() > 0) {
			activeDomains = domains.split(",");
			String[] bonuses = (bonusCodes != null && bonusCodes.length() != 0 ) ? bonusCodes.split(",") : null;
			activeBonuses = casinoBonusService.findActiveBonusTokens(activeDomains, bonuses, status, dateRangeFrom, dateRangeTo, dataTableRequest.getPageRequest());
		}else {
			activeBonuses = new SimplePageImpl<>(new ArrayList<>(), 0, 0, 0);
		}
		return new DataTableResponse<>(dataTableRequest, activeBonuses);
	}

	@GetMapping("/find/bonus-token/codes")
	public Response<List<String>> bonusTokenCodes(@RequestParam(value = "domains") String domains) {
		String [] bonusCodeDomains = (domains != null && domains.length() != 0) ? domains.split(",") : null;
		List<String> bonusCodesForActiveTokens = casinoBonusService.findBonusCodesForActiveTokens(bonusCodeDomains);
		return Response.<List<String>>builder().status(OK).data(bonusCodesForActiveTokens).build();
	}

	@GetMapping("/find/{domainName}/{type}")
	public Response<Long> find(
		@PathVariable("domainName") String domainName,
		@PathVariable("type") Integer type
	) throws Exception {
		log.info("Searching for empty bonusCode on "+domainName+" type : "+type);
		Bonus bonus = casinoBonusService.findBonus("", domainName, type);
		if (casinoBonusService.bonusValid(bonus)) {
			return Response.<Long>builder().status(OK).build();
		} else {
			return Response.<Long>builder().status(NOT_FOUND).build();
		}
	}
	
	@GetMapping("/find/{domainName}/{type}/public/all")
	public Response<List<BonusRevision>> findPublicBonusList(
		@PathVariable("domainName") String domainName,
		@PathVariable("type") Integer type,
		@RequestParam(required=false, name="triggerType") Integer triggerType
	) throws Exception {
		List<BonusRevision> revisionList = new ArrayList<>();

		revisionList = casinoBonusService.findPublicBonusList(domainName, type, triggerType);

		return Response.<List<BonusRevision>>builder().data(revisionList).status(Status.OK).build();
	}
	
	@GetMapping("/find/{domainName}/{type}/public/all/v2")
	public Response<List<BonusDisplayMinimal>> findPublicBonusListv2(
		@PathVariable("domainName") String domainName,
		@PathVariable("type") Integer type,
		@RequestParam(required=false, name="triggerType") Integer triggerType
	) throws Exception {
		List<BonusRevision> revisionList = new ArrayList<>();
		List<BonusDisplayMinimal> resultList = new ArrayList<>();

		revisionList = casinoBonusService.findPublicBonusList(domainName, type, triggerType);
		revisionList.forEach(revision -> {
			resultList.add(BonusDisplayMinimal.builder()
					.bonusId(revision.getBonus().getId())
					.bonusCode(revision.getBonusCode())
					.bonusName(revision.getBonusName())
					.bonusDescription(revision.getBonusDescription())
					.image(revision.getGraphic() == null ? null : revision.getGraphic().getImage())
					.build());
		});
		return Response.<List<BonusDisplayMinimal>>builder().data(resultList).status(Status.OK).build();
	}
	
	@GetMapping("/find/{domainName}/{type}/public")
	public Response<List<BonusRevision>> findPublicBonusList(
		@PathVariable("domainName") String domainName,
		@PathVariable("type") Integer type,
		@RequestParam(required=false, name="triggerType") Integer triggerType,
		Principal principal
	) throws Exception {
		List<BonusRevision> revisionList = new ArrayList<>();
		
		if (principal != null) {
			LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
			revisionList = casinoBonusService.findPublicBonusList(domainName, type, triggerType, util.guid());
			
		} else {
			revisionList = casinoBonusService.findPublicBonusList(domainName, type, triggerType);
		}
		
		return Response.<List<BonusRevision>>builder().data(revisionList).status(Status.OK).build();
	}
	
	@GetMapping("/find/hourly")
	public Response<List<BonusHourly>> findPlayerHourlyBonus(
		Principal principal
	) throws Exception {
		if (principal != null) {
			LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
			log.info("hourly bonuses for :"+util.guid());
			
			List<BonusHourly> revisionList = casinoBonusService.findHourlyBonus(util.domainName(), util.guid());
			
			return Response.<List<BonusHourly>>builder().data(revisionList).status(Status.OK).build();
		}
		return Response.<List<BonusHourly>>builder().status(Status.NOT_FOUND).build();
	}
	
	@PostMapping("/register/frontend")
	public Response<Long> registerForFrontendBonus(
		@RequestParam("bonusCode") String bonusCode,
		Principal principal,
		@RequestParam(value = "locale", required = false) String locale
	) throws Exception {
		if (principal != null) {
			localeContextProcessor.setLocaleContextHolder(locale, principal);
			LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
			log.info("register frontend bonuses for :"+util.guid());

			//Check if the player can particapate in promotions
			limitInternalSystemService.checkPromotionsAllowed(util.guid());

			boolean registerFrontendBonus = casinoBonusService.registerFrontendBonus(util.domainName(), util.guid(), bonusCode);
			if (registerFrontendBonus) {
				return Response.<Long>builder().status(OK).build();
			} else {
				log.info("Player could not register for frontend bonus. ("+util.guid()+") :: "+bonusCode);
				return Response.<Long>builder().status(CONFLICT).message("Not eligible for bonus.").build();
			}
		}
		return Response.<Long>builder().status(CONFLICT).message("Not eligible for bonus.").build();
	}
	
	@PostMapping("/register/hourly")
	public Response<Long> registerForHourlyBonus(
		@RequestParam("bonusCode") String bonusCode,
		Principal principal,
		@RequestParam(value = "locale", required = false) String locale
	) throws Exception {
		if (principal != null) {
			localeContextProcessor.setLocaleContextHolder(locale, principal);
			LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
			log.info("register hourly bonuses for :"+util.guid());

			//Check if the player can particapate in promotions
			limitInternalSystemService.checkPromotionsAllowed(util.guid());

			boolean registerHourlyBonus = casinoBonusService.registerHourlyBonus(util.domainName(), util.guid(), bonusCode);
			if (registerHourlyBonus) {
				return Response.<Long>builder().status(OK).build();
			} else {
				log.info("Player could not register for hourly bonus. ("+util.guid()+") :: "+bonusCode);
				return Response.<Long>builder().status(CONFLICT).message("Not eligible for bonus.").build();
			}
		}
		return Response.<Long>builder().status(CONFLICT).message("Not eligible for bonus.").build();
	}
	
	@GetMapping("/find/{domainName}/{type}/public/player/v2")
	public Response<List<BonusDisplayMinimal>> findPublicBonusListv2ByPlayerGuid(
		@PathVariable("domainName") String domainName,
		@PathVariable("type") Integer type,
		@RequestParam(required=false, name="triggerType") Integer triggerType,
		@RequestParam("playerGuid") String playerGuid
	) {
		List<BonusRevision> revisionList = new ArrayList<>();
		List<BonusDisplayMinimal> resultList = new ArrayList<>();
		
		revisionList = casinoBonusService.findPublicBonusList(domainName, type, triggerType, playerGuid);
		revisionList.forEach(revision -> {
			resultList.add(BonusDisplayMinimal.builder()
					.bonusId(revision.getBonus().getId())
					.bonusCode(revision.getBonusCode())
					.bonusName(revision.getBonusName())
					.bonusDescription(revision.getBonusDescription())
					.image(revision.getGraphic() == null ? null : revision.getGraphic().getImage())
					.build());
		});
		
		return Response.<List<BonusDisplayMinimal>>builder().data(resultList).status(Status.OK).build();
	}
	
	@GetMapping("/find/{domainName}/{type}/public/v2")
	public Response<List<BonusDisplayMinimal>> findPublicBonusListv2(
		@PathVariable("domainName") String domainName,
		@PathVariable("type") Integer type,
		@RequestParam(required=false, name="triggerType") Integer triggerType,
		Principal principal
	) throws Exception {
		List<BonusRevision> revisionList = new ArrayList<>();
		
		if (principal != null) {
			LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
			List<BonusDisplayMinimal> resultList = new ArrayList<>();

			revisionList = casinoBonusService.findPublicBonusList(domainName, type, triggerType, util.guid());
			revisionList.forEach(revision -> {
				resultList.add(BonusDisplayMinimal.builder()
						.bonusId(revision.getBonus().getId())
						.bonusCode(revision.getBonusCode())
						.bonusName(revision.getBonusName())
						.bonusDescription(revision.getBonusDescription())
						.image(revision.getGraphic() == null ? null : revision.getGraphic().getImage())
						.build());
			});
			return Response.<List<BonusDisplayMinimal>>builder().data(resultList).status(Status.OK).build();
			
		} else {
			return findPublicBonusListv2(domainName, type, triggerType);
		}
	}

	@PostMapping("/find/revisions")
	public Response<List<BonusRevision>> findRevisions(@RequestBody List<BonusRevisionRequest> requests) {
		Map<String, BonusRevision> bonusRevisionMap = new LinkedHashMap<>();
		List<BonusRevision> bonusRevisions = new ArrayList<>();
		for (BonusRevisionRequest request: requests) {
			if (!bonusRevisionMap.containsKey(request.getDomainName().toLowerCase() + "_" + request.getBonusCode() + "" + request.getBonusType())) {
				BonusRevision bonusRevision = casinoBonusService.findLastBonusRevision(request.getBonusCode(), request.getDomainName(), request.getBonusType());
				if (bonusRevision != null) bonusRevisions.add(bonusRevision);
				bonusRevisionMap.put(request.getDomainName().toLowerCase() + "_" + request.getBonusCode() + "" + request.getBonusType(), bonusRevision);
			}
		}
		return Response.<List<BonusRevision>>builder().data(bonusRevisions).status(OK).build();
	}
	
	@GetMapping("/find/revision/{domainName}/{bonusType}/{bonusCode}")
	public Response<BonusRevision> findLastBonusRevision(
		@PathVariable("domainName") String domainName,
		@PathVariable("bonusType") Integer bonusType,
		@PathVariable("bonusCode") String bonusCode
	) throws Exception {
		return Response.<BonusRevision>builder().data(casinoBonusService.findLastBonusRevision(bonusCode, domainName, bonusType)).status(OK).build();
	}
	
	@GetMapping("/find/revision/{domainName}/{bonusType}")
	public Response<BonusRevision> findLastBonusRevision(
		@PathVariable("domainName") String domainName,
		@PathVariable("bonusType") Integer bonusType
	) throws Exception {
		return findLastBonusRevision(domainName, bonusType, "");
	}
	
	@GetMapping("/find/{domainName}/{type}/{bonusCode}")
	public Response<Long> find(
		@PathVariable("domainName") String domainName,
		@PathVariable("type") Integer type,
		@PathVariable("bonusCode") String bonusCode
	) throws Exception {
		log.debug("Searching for bonusCode: "+bonusCode+" on "+domainName+" type : "+type);
		Bonus bonus = casinoBonusService.findBonus(bonusCode, domainName, type);
		if (casinoBonusService.bonusValid(bonus)) {
			return Response.<Long>builder().data(bonus.getCurrent().getId()).status(OK).build();
		} else {
			return Response.<Long>builder().status(NOT_FOUND).build();
		}
	}
	
	@PostMapping("/check/{type}")
	public Response<Boolean> bonusValidForPlayerPreCheck(
		@RequestBody CasinoBonusCheck casinoBonusCheck,
	 	@PathVariable("type") Integer type
	) throws Exception {
		Bonus bonus = null;
		if (casinoBonusCheck.getBonusId() != null)
			bonus = casinoBonusService.findBonus(casinoBonusCheck.getBonusId());
		else if (casinoBonusCheck.getBonusCode() != null) 
			bonus = casinoBonusService.findBonus(casinoBonusCheck.getBonusCode(), casinoBonusCheck.getDomainName(), type);
		try {
			if (bonus != null) {
				if (casinoBonusService.isBonusDepositRequirementsMet(bonus.getCurrent(), casinoBonusCheck.getDepositCents())) {
					if (casinoBonusService.isBonusValidForPlayerPreCheck(bonus, casinoBonusCheck.getPlayerGuid())) {
						return Response.<Boolean>builder().data(true).status(OK).build();
					}
				} else {
					return Response.<Boolean>builder().data(false).data2(Arrays.asList("The amount you are trying to deposit does not qualify for this bonus.")).status(NOT_FOUND).build();
				}
			}
		} catch (InvalidBonusException e) {
			return Response.<Boolean>builder().data(false).data2(JsonStringify.listToString(e.getErrorMessages())).status(NOT_FOUND).build();
		}
		return Response.<Boolean>builder().data(false).data2(Arrays.asList("Invalid bonus code entered")).status(NOT_FOUND).build();
	}
	
	@PostMapping("/cancel/active")
	public Response<Boolean> cancel(Principal principal) throws Exception {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
		return cancel(util.guid());
	}
	@GetMapping("/cancel/active/p")
	public Response<Boolean> cancel(@RequestParam("playerGuid") String playerGuid) throws Exception {
		PlayerBonus playerBonus = casinoBonusService.findCurrentBonus(playerGuid);
		if (playerBonus == null) return Response.<Boolean>builder().status(CONFLICT).message("No active bonus found.").build();
		log.info("cancel " + playerBonus);
		casinoBonusService.completeCancelledBonus(playerBonus);
		return Response.<Boolean>builder().status(OK).data(false).build();
	}
	
	@GetMapping("/cancel/pending/p")
	public Response<Boolean> cancelPending(@RequestParam("playerGuid") String playerGuid, @RequestParam("pendingBonusId") Long pendingBonusId) throws Exception {
		return Response.<Boolean>builder().status(OK).data(casinoBonusService.cancelPendingBonus(playerGuid, pendingBonusId)).build();
	}

	@GetMapping("/cancel/bonus-token/p")
	public Response<Boolean> cancelPlayerBonusToken(@RequestParam("playerGuid") String playerGuid, @RequestParam("playerBonusTokenId") Long playerBonusTokenId) throws Exception {
		return Response.<Boolean>builder()
				.status(OK)
				.data(bonusTokenService.cancelBonusTokenBoolean(playerGuid, playerBonusTokenId))
				.build();
	}
	
	@PostMapping("/cancel/pending")
	public Response<Boolean> cancel(Principal principal, Long pendingBonusId) throws Exception {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
		return cancelPending(util.guid(), pendingBonusId);
	}
	
	@GetMapping("/info")
	public Response<PlayerBonus> info(Principal principal) throws UnsupportedEncodingException {
//		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
//		Response<PlayerBonus> active = active(util.guid());
//		log.info("PlayerBonus: "+active);
//		if (active.isSuccessful()) {
//			PlayerBonus pb = active.getData();
//			if (pb != null) {
////				casinoBonusFreespinService.freespinsComplete(active.getData());
//			}
//		}
		return null;
	}
	@GetMapping("/info/ext")
	public Response<GetBonusInfoResponse> infoExternal(@RequestParam("playerGuid") String playerGuid, @RequestParam("provider") String provider, @RequestParam("domainName") String domainName, @RequestParam("gameId") String gameId) throws Exception {
		GetBonusInfoResponse bonusInfoResponse = casinoBonusService.externalBonusInfo(playerGuid, provider, domainName, gameId);
		log.info("GetBonusInfoResponse :"+bonusInfoResponse);
		return Response.<GetBonusInfoResponse>builder().status(OK).data(bonusInfoResponse).build();
	}
	
	@GetMapping("/cancel/ext")
	public Response<GetBonusInfoResponse> infoExternal(@RequestParam("extBonusId") Integer extBonusId, @RequestParam("provider") String provider, @RequestParam("domainName") String domainName, @RequestParam("gameId") String gameId, @RequestParam("userId") String userId) throws Exception {
		casinoBonusFreespinService.cancelFreespins(extBonusId, provider, domainName, gameId, userId);
		return Response.<GetBonusInfoResponse>builder().status(OK).build();
	}
	
	@PostMapping("/auto/register/signup")
	public Response<Long> registerForSignupBonusAuto(
			@RequestBody CasinoBonus casinoBonus,
			LithiumTokenUtil util,
			HttpServletRequest req,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		try {
			localeContextProcessor.setLocaleContextHolder(locale, util.domainName());
			if (casinoBonus.getPlayerGuid() == null) Response.<Long>builder().status(INTERNAL_SERVER_ERROR).message("Required param playerGuid is missing.").build();
			if (casinoBonus.getBonusId() == null) Response.<Long>builder().status(INTERNAL_SERVER_ERROR).message("Required param bonusId is missing.").build();
			if (casinoBonus.getToken() == null) Response.<Long>builder().status(INTERNAL_SERVER_ERROR).message("Required param token is missing.").build();

			//Check if the player can particapate in promotions
			limitInternalSystemService.checkPromotionsAllowed(casinoBonus.getPlayerGuid());

			boolean validRequest = autoBonusAllocationService.checkRequest(util.domainName(), casinoBonus.getToken());
			if (!validRequest) {
				return Response.<Long>builder().status(CONFLICT).message("Token " + casinoBonus.getToken() + " already exists.").build();
			}
			String[] domainAndPlayer = casinoBonus.getPlayerGuid().split("/");
			if (domainAndPlayer.length != 2) return Response.<Long>builder().status(CONFLICT).message("playerGuid is malformed.").build();
			autoBonusAllocationService.saveRequest(util.domainName(), casinoBonus.getToken(), req.getRemoteAddr(), util.guid(), casinoBonus.getPlayerGuid(), AutoBonusAllocationService.BONUS_TYPE_SIGNUP, casinoBonus.getBonusId(), null);
			return registerForSignupBonusById(casinoBonus, locale);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Long>builder().status(INTERNAL_SERVER_ERROR).message("Could not register bonus.").build();
		}
	}
	
	@PostMapping("/manual/register/signup")
	public Response<Long> registerForSignupBonusManual(
			@RequestBody CasinoBonus casinoBonus,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		String[] domainAndPlayer = casinoBonus.getPlayerGuid().split("/");
		if (domainAndPlayer.length != 2) return Response.<Long>builder().status(CONFLICT).message("Could not register bonus.").build();

		localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);

		//Check if the player can particapate in promotions
		limitInternalSystemService.checkPromotionsAllowed(casinoBonus.getPlayerGuid());

		Bonus bonus = casinoBonusService.findSignupBonus(casinoBonus.getBonusCode(), domainAndPlayer[0]);
		log.info("Found Signup Bonus ("+casinoBonus.getPlayerGuid()+") :: "+bonus);
		if (bonus == null) return Response.<Long>builder().status(CONFLICT).message("Invalid Bonus.").build();
		return registerSignupBonus(bonus, domainAndPlayer[0], casinoBonus.getPlayerGuid(), domainAndPlayer[1]);
	}

	/**
	 * Used in cases where there is a need to apply the bonus to an older revision
	 * @param casinoBonusv2
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/manual/register/signup/v2")
	public Response<Long> registerForSignupBonusManualv2(
			@RequestBody CasinoBonusv2 casinoBonusv2,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		String[] domainAndPlayer = casinoBonusv2.getPlayerGuid().split("/");
		if (domainAndPlayer.length != 2) return Response.<Long>builder().status(CONFLICT).message("Could not register bonus.").build();

		localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);

		//Check if the player can particapate in promotions
		limitInternalSystemService.checkPromotionsAllowed(casinoBonusv2.getPlayerGuid());

		BonusRevision bonusRevision = null;
		if (casinoBonusv2.getRevisionId() != null && casinoBonusv2.getRevisionId() > 0) {
			bonusRevision = casinoBonusService.findBonusRevisionById(casinoBonusv2.getRevisionId());
			log.info("Found Signup Bonus Revision ("+casinoBonusv2.getPlayerGuid()+") :: "+bonusRevision);
		}
		if (bonusRevision == null) {
			Bonus bonus = casinoBonusService.findSignupBonus(casinoBonusv2.getBonusCode(), domainAndPlayer[0]);
			bonusRevision = bonus.getCurrent();
			log.info("Found Signup Bonus (" + casinoBonusv2.getPlayerGuid() + ") :: " + bonus);
		}
		if (bonusRevision == null) return Response.<Long>builder().status(CONFLICT).message("Invalid Bonus.").build();

		return registerSignupBonusv2(bonusRevision, domainAndPlayer[0], casinoBonusv2.getPlayerGuid(), domainAndPlayer[1]);
	}

	
	@PostMapping("/manual/register/trigger")
	public Response<Long> registerForTriggerBonus(
			@RequestBody BonusAllocate bonusAllocate,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		log.info("/manual/register/trigger request ("+bonusAllocate.getPlayerGuid()+") :: "+bonusAllocate);
		String[] domainAndPlayer = bonusAllocate.getPlayerGuid().split("/");
		if (domainAndPlayer.length != 2) return Response.<Long>builder().status(CONFLICT).message("Could not register bonus.").build();
		localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);
		//Check if the player can particapate in promotions
		limitInternalSystemService.checkPromotionsAllowed(bonusAllocate.getPlayerGuid());

		casinoTriggerBonusService.processTriggerBonus(bonusAllocate);
		return Response.<Long>builder().status(OK).build();
	}


	@PostMapping("/manual/register/trigger/v2")
	public Response<Long> registerForTriggerBonusv2(
			@RequestBody BonusAllocatev2 bonusAllocatev2,
			LithiumTokenUtil tokenUtil,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		log.info("/manual/register/trigger/v2 (post) request ("+bonusAllocatev2.getPlayerGuid()+") :: "+bonusAllocatev2);
		localeContextProcessor.setLocaleContextHolder(locale, tokenUtil.domainName());
		//Check if the player can particapate in promotions
		limitInternalSystemService.checkPromotionsAllowed(bonusAllocatev2.getPlayerGuid());

		casinoTriggerBonusService.processTriggerOrTokenBonusWithCustomMoney(bonusAllocatev2, BonusRevision.BONUS_TYPE_TRIGGER, tokenUtil);
		return Response.<Long>builder().status(OK).build();
	}

	@RequestMapping(method=RequestMethod.POST, path="/manual/register/trigger/v2/csv", consumes="multipart/form-data")
	public Response<Map<String, String>> registerForTriggerBonusUsingCsv(
			@RequestPart("bonusCode") String bonusCode,
			@RequestPart("revisionId") String revisionId,
			@RequestPart("image") MultipartFile csvfile,
			LithiumTokenUtil util,
			@RequestParam(value = "locale", required = false) String locale
	) throws
			Status510GeneralCasinoExecutionException,
			Status421InvalidFileDataException {
		log.info("/manual/register/trigger/v2/csv request ("+bonusCode+") :: "+util.guid() + " csvFileSize: " + csvfile.getSize());
		localeContextProcessor.setLocaleContextHolder(locale, util.domainName());
		Map<String, String> resultMap = casinoBonusFileUploadService
				.processTriggerBonusFileUpload(
						bonusCode,
						revisionId != null ? Long.parseLong(revisionId) : null,
						csvfile,
						util);
		return Response.<Map<String, String>>builder().status(OK_SUCCESS).data(resultMap).build();
	}

	@GetMapping("/manual/register/trigger/v2")
	public Response<Map<String, String>> registerForTriggerBonusWithOptionalCustomAmount(
			@RequestParam("bonusCode") String bonusCode,
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam(name = "customFreeMoneyAmountDecimal", required = false) Double customFreeMoneyAmountDecimal,
			@RequestParam(name = "bonusRevisionId", required = false) Long bonusRevisionId,
			LithiumTokenUtil util,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		log.info("/manual/register/trigger/v2 (get) request ("+bonusCode+") :: "+util.guid());
		localeContextProcessor.setLocaleContextHolder(locale, util.domainName());
		//Check if the player can particapate in promotions
		limitInternalSystemService.checkPromotionsAllowed(playerGuid);

		casinoTriggerBonusService.processTriggerOrTokenBonusWithCustomMoney(
				BonusAllocatev2.builder()
						.bonusCode(bonusCode)
						.playerGuid(playerGuid)
						.customAmountDecimal(customFreeMoneyAmountDecimal)
						.bonusRevisionId(bonusRevisionId)
						.build(), BonusRevision.BONUS_TYPE_TRIGGER, util);

		return Response.<Map<String, String>>builder().status(OK).message("Seems everything is fine").build();
	}

	@PostMapping("/manual/register/bonus-token/v2")
	public Response<Long> registerForBonusTokenWithOptionalCustomAmount(
			@RequestBody BonusAllocatev2 bonusAllocatev2,
			LithiumTokenUtil tokenUtil,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		log.info("/manual/register/bonus-token/v2 (post) request ("+bonusAllocatev2.getPlayerGuid()+") :: "+bonusAllocatev2);
		localeContextProcessor.setLocaleContextHolder(locale, tokenUtil.domainName());
		limitInternalSystemService.checkPromotionsAllowed(bonusAllocatev2.getPlayerGuid());

		casinoTriggerBonusService.processTriggerOrTokenBonusWithCustomMoney(bonusAllocatev2, BonusRevision.BONUS_TYPE_BONUS_TOKEN, tokenUtil);
		return Response.<Long>builder().status(OK).build();
	}

	@GetMapping("/manual/register/bonus-token/v2")
	public Response<Map<String, String>> registerForBonusTokenWithOptionalCustomAmount(
			@RequestParam("bonusCode") String bonusCode,
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam(name = "customBonusTokenAmountDecimal", required = false) Double customBonusTokenAmountDecimal,
			@RequestParam(name = "bonusRevisionId", required = false) Long bonusRevisionId,
			LithiumTokenUtil util,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		log.info("/manual/register/bonus-token/v2 (get) request ("+bonusCode+") :: "+util.guid());
		localeContextProcessor.setLocaleContextHolder(locale, util.domainName());
		limitInternalSystemService.checkPromotionsAllowed(playerGuid);

		casinoTriggerBonusService.processTriggerOrTokenBonusWithCustomMoney(
				BonusAllocatev2.builder()
						.bonusCode(bonusCode)
						.playerGuid(playerGuid)
						.customAmountDecimal(customBonusTokenAmountDecimal)
						.bonusRevisionId(bonusRevisionId)
						.build(), BonusRevision.BONUS_TYPE_BONUS_TOKEN, util);

		return Response.<Map<String, String>>builder().status(OK).message("Seems everything is fine").build();
	}

	@RequestMapping(method=RequestMethod.POST, path="/manual/register/bonus-token/v2/csv", consumes="multipart/form-data")
	public Response<Map<String, String>> registerForBonusTokenBonusUsingCsv(
			@RequestPart("bonusCode") String bonusCode,
			@RequestPart("revisionId") String revisionId,
			@RequestPart("image") MultipartFile csvfile,
			LithiumTokenUtil util,
			@RequestParam(value = "locale", required = false) String locale
	) throws
			Status510GeneralCasinoExecutionException,
			Status421InvalidFileDataException {
		log.info("/manual/register/bonus-token/v2/csv request ("+bonusCode+") :: "+util.guid() + " csvFileSize: " + csvfile.getSize());
		localeContextProcessor.setLocaleContextHolder(locale, util.domainName());
		Map<String, String> resultMap = casinoBonusFileUploadService
				.processTriggerBonusFileUpload(
						bonusCode,
						revisionId != null ? Long.parseLong(revisionId) : null,
						csvfile,
						util);
		return Response.<Map<String, String>>builder().status(OK_SUCCESS).data(resultMap).build();
	}
	
	@PostMapping("/register/signup")
	public Response<Long> registerForSignupBonus(
			@RequestBody CasinoBonus casinoBonus,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		log.info("/register/signup request ("+casinoBonus.getPlayerGuid()+") :: "+casinoBonus);

		String[] domainAndPlayer = casinoBonus.getPlayerGuid().split("/");
		if (domainAndPlayer.length != 2) return Response.<Long>builder().status(CONFLICT).message("Could not register bonus.").build();

		localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);

		limitInternalSystemService.checkPromotionsAllowed(casinoBonus.getPlayerGuid());

		if (casinoBonusService.findPlayerBonusHistory(casinoBonus.getPlayerGuid(), casinoBonus.getBonusCode()).size() > 0) {
			log.info("Player already has bonus history ?? Not a Signup?. ("+casinoBonus.getPlayerGuid()+")");
			return Response.<Long>builder().status(CONFLICT).message("Existing player for signup bonus ?").build();
		}

		Bonus bonus = casinoBonusService.findSignupBonus(casinoBonus.getBonusCode(), domainAndPlayer[0]);
		log.info("Found Signup Bonus ("+casinoBonus.getPlayerGuid()+") :: "+bonus);
		if (bonus == null) return Response.<Long>builder().status(CONFLICT).message("Invalid Bonus.").build();
		return registerSignupBonus(bonus, domainAndPlayer[0], casinoBonus.getPlayerGuid(), domainAndPlayer[1]);
	}
	
	@PostMapping("/register/signupbyid")
	public Response<Long> registerForSignupBonusById(
			@RequestBody CasinoBonus casinoBonus,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		log.info("/register/signupbyid request ("+casinoBonus.getPlayerGuid()+") :: "+casinoBonus);

		String[] domainAndPlayer = casinoBonus.getPlayerGuid().split("/");
		if (domainAndPlayer.length != 2) return Response.<Long>builder().status(CONFLICT).message("Could not register bonus.").build();

		localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);

		limitInternalSystemService.checkPromotionsAllowed(casinoBonus.getPlayerGuid());

		Bonus bonus = casinoBonusService.findBonus(casinoBonus.getBonusId());
		log.info("Found Signup Bonus ("+casinoBonus.getPlayerGuid()+") :: "+bonus);
		if (bonus == null) return Response.<Long>builder().status(CONFLICT).message("Invalid Bonus.").build();
		
		return registerSignupBonus(bonus, domainAndPlayer[0], casinoBonus.getPlayerGuid(), domainAndPlayer[1]);
	}
	
	@RequestMapping(method=RequestMethod.POST, path="/find/bonushistory")
	public List<lithium.service.casino.client.data.PlayerBonusHistory> findBonusHistoryByDateRange(String playerGuid, String rangeStart, String rangeEnd) throws Exception {
		return casinoBonusService.findBonusHistoryByDateRange(playerGuid, DateTime.parse(rangeStart).toDate(), DateTime.parse(rangeEnd).toDate());
	}
	
	private Response<Long> registerSignupBonus(Bonus bonus, String domainName, String playerGuid, String userName) throws Exception {
		return registerSignupBonusv2(bonus.getCurrent(), domainName, playerGuid, userName);
	}

	private Response<Long> registerSignupBonusv2(BonusRevision bonusRevision, String domainName, String playerGuid, String userName) throws Exception {
		// Signup bonus is never instant or if it is, it will be handled in the code below where a check is done of active bonus.
		boolean instantBonus = false;
		if (bonusRevision == null) return Response.<Long>builder().status(CONFLICT).message("Invalid Bonus.").build();
		
		if (casinoBonusService.findCurrentBonus(playerGuid) != null)  return Response.<Long>builder().status(CONFLICT).message("Pleayer already has an active bonus.").build();

		if (casinoBonusService.bonusValidForPlayer(bonusRevision, playerGuid)) {
			PlayerBonusHistory pbh = casinoBonusService.savePlayerBonusHistory(bonusRevision, instantBonus);
			PlayerBonus pb = casinoBonusService.savePlayerBonus(pbh, playerGuid, instantBonus);
			pbh = casinoBonusService.updatePlayerBonusHistory(pbh, pb, 0L);
			log.info("Saved player bonus. ("+playerGuid+") :: "+pb);
			List<ChangeLogFieldChange> clfc = changeLogService.copy(pbh, new PlayerBonusHistory(), new String[] { "startedDate", "bonus" });
			changeLogService.registerChangesWithDomain("user.bonus", "create", -1L, playerGuid, null, null, clfc, Category.BONUSES, SubCategory.BONUS_REGISTER, 0, domainName);
			log.info("Checking for free spins connected to bonus. ("+playerGuid+") :: "+pb);
			casinoBonusService.triggerFreeMoney(pbh, instantBonus);
			casinoBonusService.triggerAdditionalFreeMoney(pbh, instantBonus);
			casinoBonusService.triggerExternalBonusGame(pbh);
			casinoBonusFreespinService.triggerFreeSpins(pbh, instantBonus);
			casinoBonusUnlockGamesService.triggerUnlockGames(pbh);
			
			if (bonusRevision.getActivationNotificationName() != null && !bonusRevision.getActivationNotificationName().isEmpty()) {
				casinoBonusService.streamBonusNotification(playerGuid, bonusRevision.getActivationNotificationName());
			}
			
			try {
				UserEvent userEventPlayerBonus = casinoBonusService.registerUserEventPlayerBonus(domainName, userName, casinoBonusService.playerBonusDisplay(pb.getPlayerGuid(), null));
				log.debug("User event player bonus (" + userEventPlayerBonus + ")");
			} catch (Exception e) {
				log.error("Failed to register user event for player bonus (" + pb + "), " + e.getMessage(), e);
			}
			
			try {
				casinoMailSmsService.sendBonusMail(CasinoMailSmsService.BONUS_STATE_ACTIVATE, pbh, null);
			} catch (Exception e) {
				log.error("Failed to send bonus activate email " + pb, e);
			}

			try {
				casinoMailSmsService.sendBonusSms(CasinoMailSmsService.BONUS_STATE_ACTIVATE, pbh, null);
			} catch (Exception e) {
				log.error("Failed to send bonus activate sms " + pb, e);
			}
			
			log.info("Checking if bonus should be instant. ("+playerGuid+") :: "+pb);
			casinoBonusService.isBonusCompleted(pb);
			
			return Response.<Long>builder().status(OK).build();
		} else {
			log.info("Player not eligible. ("+playerGuid+") :: "+bonusRevision);
			return Response.<Long>builder().status(CONFLICT).message("Not eligible for bonus.").build();
		}
	}

	@PostMapping("/auto/register/deposit")
	public Response<Long> registerForDepositBonusAuto(
			@RequestBody CasinoBonus casinoBonus,
			LithiumTokenUtil util,
			HttpServletRequest req,
			@RequestParam(value = "locale", required = false) String locale) {
		try {
			localeContextProcessor.setLocaleContextHolder(locale, util.domainName());
			if (casinoBonus.getPlayerGuid() == null) Response.<Long>builder().status(INTERNAL_SERVER_ERROR).message("Required param playerGuid is missing.").build();
			if (casinoBonus.getBonusId() == null) Response.<Long>builder().status(INTERNAL_SERVER_ERROR).message("Required param bonusId is missing.").build();
			if (casinoBonus.getToken() == null) Response.<Long>builder().status(INTERNAL_SERVER_ERROR).message("Required param token is missing.").build();
			if (casinoBonus.getAmountCents() == null) Response.<Long>builder().status(INTERNAL_SERVER_ERROR).message("Required param amountCents is missing.").build();

			limitInternalSystemService.checkPromotionsAllowed(casinoBonus.getPlayerGuid());

			boolean validRequest = autoBonusAllocationService.checkRequest(util.domainName(), casinoBonus.getToken());
			if (!validRequest) {
				return Response.<Long>builder().status(CONFLICT).message("Token " + casinoBonus.getToken() + " already exists.").build();
			}
			String[] domainAndPlayer = casinoBonus.getPlayerGuid().split("/");
			if (domainAndPlayer.length != 2) return Response.<Long>builder().status(CONFLICT).message("playerGuid is malformed.").build();
			AutoBonusAllocation aba = autoBonusAllocationService.saveRequest(util.domainName(), casinoBonus.getToken(), req.getRemoteAddr(), util.guid(), casinoBonus.getPlayerGuid(), AutoBonusAllocationService.BONUS_TYPE_DEPOSIT, casinoBonus.getBonusId(), casinoBonus.getAmountCents());
			aba = autoBonusAllocationService.writeUserEvent(aba, domainAndPlayer[0], domainAndPlayer[1], casinoBonus.getAmountCents());
			casinoBonus.setUserEventId(aba.getUserEventId());
			return registerForDepositBonusById(casinoBonus, locale);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Long>builder().status(INTERNAL_SERVER_ERROR).message("Could not register bonus.").build();
		}
	}
	
	@PostMapping("/register/deposit")
	public Response<Long> registerForDepositBonus(
			@RequestBody CasinoBonus casinoBonus,
			Principal principal,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		localeContextProcessor.setLocaleContextHolder(locale, principal);
		log.info("/register/deposit request ("+principal.getName()+") :: "+casinoBonus);
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();

		limitInternalSystemService.checkPromotionsAllowed(casinoBonus.getPlayerGuid());

		Bonus bonus = casinoBonusService.findDepositBonus(casinoBonus.getBonusCode(), util.domainName());
		if (bonus == null) return Response.<Long>builder().status(CONFLICT).message("The bonus code does not exist.").build();
		return registerDepositBonus(casinoBonus.getUserEventId(), util.domainName(), util.guid(), util.username(), bonus);
	}
	
	@PostMapping("/register/depositbyid")
 	public Response<Long> registerForDepositBonusById(
			 @RequestBody CasinoBonus casinoBonus,
			 @RequestParam(value = "locale", required = false) String locale) throws Exception {
 		log.info("/register/depositbyid request ("+casinoBonus.getPlayerGuid()+") :: "+casinoBonus);

		String[] domainAndPlayer = casinoBonus.getPlayerGuid().split("/");
		if (domainAndPlayer.length != 2) return Response.<Long>builder().status(CONFLICT).message("Could not register bonus.").build();

		localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);

		limitInternalSystemService.checkPromotionsAllowed(casinoBonus.getPlayerGuid());

 		Bonus bonus = casinoBonusService.findBonus(casinoBonus.getBonusId());
 		if (bonus == null) return Response.<Long>builder().status(CONFLICT).message("The bonus code does not exist.").build();
 		return registerDepositBonus(casinoBonus.getUserEventId(), domainAndPlayer[0], casinoBonus.getPlayerGuid(), domainAndPlayer[1], bonus);
 	}
	
	@PostMapping("/manual/register/deposit")
	public Response<Long> registerForDepositBonus(
			@RequestBody CasinoBonus casinoBonus,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		String[] domainAndPlayer = casinoBonus.getPlayerGuid().split("/");
		if (domainAndPlayer.length != 2) return Response.<Long>builder().status(CONFLICT).message("Could not register bonus.").build();

		localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);

		limitInternalSystemService.checkPromotionsAllowed(casinoBonus.getPlayerGuid());

		Bonus bonus = casinoBonusService.findDepositBonus(casinoBonus.getBonusCode(), domainAndPlayer[0]);
		if (bonus == null) return Response.<Long>builder().status(CONFLICT).message("The bonus code does not exist.").build();
		return registerDepositBonus(casinoBonus.getUserEventId(), domainAndPlayer[0], casinoBonus.getPlayerGuid(), domainAndPlayer[1], bonus);
	}

	@PostMapping("/manual/register/deposit/v2")
	public Response<Long> registerForDepositBonusv2(
			@RequestBody CasinoBonusv2 casinoBonusv2,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {

		String[] domainAndPlayer = casinoBonusv2.getPlayerGuid().split("/");
		if (domainAndPlayer.length != 2) return Response.<Long>builder().status(CONFLICT).message("Could not register bonus.").build();

		localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);

		limitInternalSystemService.checkPromotionsAllowed(casinoBonusv2.getPlayerGuid());

		BonusRevision bonusRevision = null;
		if (casinoBonusv2.getRevisionId() != null && casinoBonusv2.getRevisionId() > 0) {
			bonusRevision = casinoBonusService.findBonusRevisionById(casinoBonusv2.getRevisionId());
			log.info("Found deposit Bonus Revision ("+casinoBonusv2.getPlayerGuid()+") :: "+bonusRevision);
		}
		if (bonusRevision == null) {
			Bonus bonus = casinoBonusService.findDepositBonus(casinoBonusv2.getBonusCode(), domainAndPlayer[0]);
			if (bonus == null)
				return Response.<Long>builder().status(CONFLICT).message("The bonus code does not exist.").build();
		}

		return registerDepositBonusv2(casinoBonusv2.getUserEventId(), domainAndPlayer[0], casinoBonusv2.getPlayerGuid(), domainAndPlayer[1], bonusRevision);
	}
	
	private Response<Long> registerDepositBonus(Long userEventId, String domainName, String playerGuid, String userName, Bonus bonus) throws Exception {
		return registerDepositBonusv2(userEventId, domainName, playerGuid, userName, bonus.getCurrent());
	}

	private Response<Long> registerDepositBonusv2(Long userEventId, String domainName, String playerGuid, String userName, BonusRevision bonusRevision) throws Exception {
		UserEvent userEvent = casinoBonusService.getUserEvent(domainName, userName, userEventId);
		if (userEvent == null) {
			log.info("No userevent found.. Missing deposit ("+playerGuid+")");
			return Response.<Long>builder().status(CONFLICT).message("Invalid user event.").build();
		} else {
			if (!userEvent.getUser().guid().equalsIgnoreCase(playerGuid)) {
				log.info("Unmatched userevent found. ("+playerGuid+") :: userEvent : "+userEvent);
				return Response.<Long>builder().status(CONFLICT).message("Unmatched user event.").build();
			}
			if (userEvent.getReceived()) {
				log.info("Userevent already marked as received. ("+playerGuid+") :: userEvent : "+userEvent);
				return Response.<Long>builder().status(CONFLICT).message("It seems you have already responded to this dialog on another window.").build();
			}
			if (!userEvent.getType().equals("CASHIER_DEPOSIT") && !userEvent.getType().equals("MANUAL_DEPOSIT_BONUS_DEP") && !userEvent.getType().equals("AUTO_DEPOSIT_BONUS_DEP")) {
				log.info("Userevent incorrect type received. ("+playerGuid+") :: userEvent : "+userEvent);
				return Response.<Long>builder().status(CONFLICT).message("UserEvent with id (" + userEventId + ") is of incorrect type").build();
			}
		}
		//Need to know this to bypass all requirements and allow bonus allocation
		boolean manualDepositType = userEvent.getType().equals("MANUAL_DEPOSIT_BONUS_DEP");
		ArrayList<String> manualDepositRequirementSkipList = new ArrayList<>();
		
		Long depositCents = Long.parseLong(userEvent.getData());
		
		if (manualDepositType && casinoBonusService.getPlayerRealBalanceExcludingBonusBalance(playerGuid, domainName) < depositCents) {
			return Response.<Long>builder().status(CONFLICT).message("The player does not have sufficient balance to allocate bonus.").build();
		}

		if (bonusRevision == null) return Response.<Long>builder().status(CONFLICT).message("The bonus code does not exist.").build();
		
		boolean queueBonus = true;
		PlayerBonus currentActiveBonus = casinoBonusService.findCurrentBonus(playerGuid);
		if (currentActiveBonus != null && !queueBonus) {
			log.info("Player has active bonus. ("+playerGuid+") " + bonusRevision.getBonus() + " " + currentActiveBonus);
			Long currentCasinoBonusBalance = casinoBonusService.getCasinoBonusBalance(currentActiveBonus);
			Long cancelOnDepositMinimumAmount = bonusRevision.getCancelOnDepositMinimumAmount();
			boolean mayCancel = currentActiveBonus.getCurrent().getBonus().isPlayerMayCancel();
			if ((cancelOnDepositMinimumAmount != null) && (currentCasinoBonusBalance <= cancelOnDepositMinimumAmount))  {
				mayCancel = true;
			}
			if (!mayCancel) {
				if (!manualDepositType) {
					return Response.<Long>builder().status(CONFLICT).message("You have a running bonus that may not be cancelled.").build();
				} else {
					manualDepositRequirementSkipList.add("Player has a running bonus that can not be cancelled, it will now be cancelled.");
					mayCancel = true;
				}
			}
		}
		
		if (casinoBonusService.bonusValidForPlayer(bonusRevision, playerGuid)) {
			Bonus dependsOnBonus = bonusRevision.getDependsOnBonus();
			if (dependsOnBonus != null) {
				List<PlayerBonusHistory> playerParentBonusHistory = casinoBonusService.findPlayerBonusHistory(playerGuid, dependsOnBonus.getCurrent().getBonusCode());
				if (playerParentBonusHistory.size() == 0) {
//					for (PlayerBonusHistory pbh:playerParentBonusHistory) {
//						if (!pbh.getCompleted()) {
//							log.info("Bonus code prerequisites not completed yet. ("+principal.getName()+") :: "+bonus);
//							return Response.<Long>builder().status(CONFLICT).message("To register for this bonus you will first need to complete the '"+dependsOnBonus.getCurrent().getBonusName()+"' bonus.").build();
//						}
//					}
//				} else {
					if (!manualDepositType) {
						log.info("Bonus code prerequisites not completed yet. ("+playerGuid+") :: "+bonusRevision.getBonus());
						return Response.<Long>builder().status(CONFLICT).message("To register for this bonus you will first need to register for the '"+dependsOnBonus.getCurrent().getBonusName()+"' bonus.").build();
					} else {
						manualDepositRequirementSkipList.add("The bonus needs a prerequisite bonus to be allocated first, it will now be allocated without this prerequisite bonus.");
					}
				}
			}
			
			if (!casinoBonusService.checkMaxRedeemableValid(bonusRevision, playerGuid)) {
				if (!manualDepositType) {
					log.info("Bonus code usage exceeded for player. ("+playerGuid+") :: "+bonusRevision.getBonus());
					return Response.<Long>builder().status(CONFLICT).message("You have already taken up this bonus as many times as are allowed.").build();
				} else {
					manualDepositRequirementSkipList.add("Bonus code usage exceeded for player, this will now be ignored.");
				}
			}
			
//			Integer maxRedeemable = bonusRevision.getMaxRedeemable();
//			List<PlayerBonusHistory> playerBonusHistory = casinoBonusService.findPlayerBonusHistory(playerGuid, bonusCode);
//			if (maxRedeemable != null) {
//				
//				if (playerBonusHistory.size() >= maxRedeemable) {
//					log.info("Bonus code usage exceeded for player. ("+principal.getName()+") :: "+bonus);
//					return Response.<Long>builder().status(CONFLICT).message("Bonus code usage exceeded for player..").build();
//				}
//			}
			
			SummaryAccountTransactionType stt = casinoBonusService.checkDeposits(bonusRevision.getDomain().getName(), playerGuid, Period.GRANULARITY_TOTAL);
			Integer forDepositNumber = bonusRevision.getForDepositNumber();
			if (forDepositNumber != null) {
				if (stt != null) {
					if (forDepositNumber.longValue() != (stt.getTranCount())) {
						if (!manualDepositType) {
							log.info("Bonus code meant for specific deposit("+forDepositNumber+"). ("+playerGuid+") :: "+bonusRevision.getBonus());
							return Response.<Long>builder().status(CONFLICT).message("This bonus is only valid for your "+ casinoBonusService.getNumberSuffix(forDepositNumber) + " deposit.").build();
						} else {
							manualDepositRequirementSkipList.add("This bonus is only valid for your "+ casinoBonusService.getNumberSuffix(forDepositNumber) + " deposit, it will now be allocated.");
						}
					}
				} else {
					if (!manualDepositType) {
					log.info("Bonus code meant for specific deposit("+forDepositNumber+"). ("+playerGuid+") :: "+bonusRevision.getBonus());
					return Response.<Long>builder().status(CONFLICT).message("This bonus is only valid for your "+ casinoBonusService.getNumberSuffix(forDepositNumber) + " deposit.").build();
					} else {
						manualDepositRequirementSkipList.add("This bonus is only valid for your "+ casinoBonusService.getNumberSuffix(forDepositNumber) + " deposit, it will now be allocated.");
					}
				}
			}
			
			BigDecimal percentage = new BigDecimal(0);
			BigDecimal bonusAmount = new BigDecimal(0);
			BonusRequirementsDeposit brd = casinoBonusService.bonusRequirementsDeposit(bonusRevision.getId(), depositCents, manualDepositType);
			if (brd == null) {
				brd = casinoBonusService.findTop1BonusRequirementsDeposit(bonusRevision.getId());
				if (brd == null) {
					brd = BonusRequirementsDeposit.builder().bonusPercentage(0).wagerRequirements(0).build();
					//No bonus requirements were saved for this bonus
					bonusAmount = new BigDecimal(0);
				} else {
					if (depositCents < brd.getMaxDeposit() && !manualDepositType) {
						log.info("Deposit amount does not qualify for this bonus. ("+playerGuid+") :: "+bonusRevision.getBonus());
						return Response.<Long>builder().status(CONFLICT).message("The amount you have deposited does noet allow you to qualify for this bonus.").build();
					} else {
						if (brd.getBonusPercentage() != null) percentage = new BigDecimal(brd.getBonusPercentage()).movePointLeft(2);
						bonusAmount = new BigDecimal(brd.getMaxDeposit()).multiply(percentage);
						if (manualDepositType && depositCents < brd.getMaxDeposit()) {
							manualDepositRequirementSkipList.add("The amount you have deposited does noet allow you to qualify for this bonus, this will now be ignored.");
						}
					}
				}
			} else {
				if (brd.getBonusPercentage() != null) percentage = new BigDecimal(brd.getBonusPercentage()).movePointLeft(2);
				bonusAmount = new BigDecimal(depositCents).multiply(percentage);
			}
			
			if (currentActiveBonus != null && !queueBonus) {
				log.info("Cancelling current active bonus : "+currentActiveBonus+" to be replaced with "+bonusRevision.getBonus());
				List<ChangeLogFieldChange> clfc = changeLogService.copy(currentActiveBonus.getCurrent(), new PlayerBonusHistory(), new String[] { "startedDate", "bonus" });
				changeLogService.registerChangesWithDomain("user.bonus", "create", userEvent.getUser().getId(), playerGuid, null, null, clfc, Category.BONUSES, SubCategory.BONUS_REGISTER, 0, playerGuid.substring(0, playerGuid.indexOf('/')));
				casinoBonusService.completeCancelledBonus(currentActiveBonus);
			} else if (queueBonus && currentActiveBonus != null) {
				PlayerBonusPending pbp = casinoBonusService.savePlayerBonusPending(bonusRevision.getBonus(), ((depositCents+bonusAmount.longValue())*brd.getWagerRequirements()), bonusAmount.longValue(), brd.getBonusPercentage(), playerGuid, depositCents, null, bonusRevision.getId());
				casinoBonusService.transferToPendingBonus(pbp);
				casinoBonusService.markUserEventReceived(domainName, userName, userEventId);
				return Response.<Long>builder().status(OK).data2(manualDepositRequirementSkipList).build();
			}

			if ((queueBonus && currentActiveBonus == null) || !queueBonus) {
				activatePlayerBonus(bonusRevision, depositCents, bonusAmount.longValue(), brd.getBonusPercentage() != null ? brd.getBonusPercentage() : 0, brd.getWagerRequirements(), playerGuid, userEvent.getUser().getId(), domainName, userName);
				casinoBonusService.markUserEventReceived(domainName, userName, userEventId);
				return Response.<Long>builder().status(OK).data2(manualDepositRequirementSkipList).build();
			}
			
			return null; //this is dead code
		} else {
			return Response.<Long>builder().status(CONFLICT).message("The bonus code you have entered is not valid.").build();
		}
	}
	
	private void activatePlayerBonus(BonusRevision bonusRevision, long depositCents, long bonusAmount, int bonusPercentage, int wagerRequirement, String playerGuid, long userEventUserId, String domainName, String userName) throws Exception {
		// This is for a deposit bonus, we will not ever assume it is instant
		boolean instantBonus = false;
		PlayerBonusHistory pbh = casinoBonusService.savePlayerBonusHistory(bonusRevision, ((depositCents+bonusAmount)*wagerRequirement), bonusAmount, bonusPercentage, instantBonus);
		PlayerBonus pb = casinoBonusService.savePlayerBonus(pbh, playerGuid, instantBonus);
		pbh = casinoBonusService.updatePlayerBonusHistory(pbh, pb, depositCents);
		
		List<ChangeLogFieldChange> clfc = changeLogService.copy(pbh, new PlayerBonusHistory(), new String[] { "startedDate", "bonus" });
		String comment = String.format("Bonus with code %s was granted", bonusRevision.getBonusCode());
		changeLogService.registerChangesWithDomain("user.bonus", "create", userEventUserId, playerGuid, comment, null, clfc, Category.BONUSES, SubCategory.BONUS_REGISTER, 0, domainName);

		
		casinoBonusService.triggerOnDeposit(pb);
		casinoBonusFreespinService.triggerFreeSpins(pbh, instantBonus);
		casinoBonusUnlockGamesService.triggerUnlockGames(pbh);
		casinoBonusService.triggerFreeMoney(pbh, instantBonus);
		casinoBonusService.triggerAdditionalFreeMoney(pbh, instantBonus);
		casinoBonusService.triggerExternalBonusGame(pbh);
		log.info("PlayerBonus : "+pb);
		
		String activationNotificationName = bonusRevision.getActivationNotificationName();
		if (activationNotificationName != null && !activationNotificationName.isEmpty()) {
			casinoBonusService.streamBonusNotification(playerGuid, activationNotificationName);
		}
		
		try {
			UserEvent userEventPlayerBonus = casinoBonusService.registerUserEventPlayerBonus(domainName, userName, casinoBonusService.playerBonusDisplay(pb.getPlayerGuid(), null));
			log.debug("User event player bonus (" + userEventPlayerBonus + ")");
		} catch (Exception e) {
			log.error("Failed to register user event for player bonus (" + pb + "), " + e.getMessage(), e);
		}
		
		try {
			casinoMailSmsService.sendBonusMail(CasinoMailSmsService.BONUS_STATE_ACTIVATE, pbh, null);
		} catch (Exception e) {
			log.error("Failed to send bonus activate email " + pb, e);
		}

		try {
			casinoMailSmsService.sendBonusSms(CasinoMailSmsService.BONUS_STATE_ACTIVATE, pbh, null);
		} catch (Exception e) {
			log.error("Failed to send bonus activate sms " + pb, e);
		}
		
		casinoBonusService.isBonusCompleted(pb);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/table")
	public DataTableResponse<Bonus> table(
		@RequestParam(name="enabled", required=false) List<Boolean> status,
		@RequestParam(name="bonusType", required=false) List<Integer> bonusType,
		@RequestParam(name="domains", required=false) List<String> domains,
		DataTableRequest request,
		Principal principal
	) throws Exception {
		log.debug("Status:"+status+" Type:"+bonusType+" Domains:"+domains+" Request:"+request);
		LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();
		if (domains == null) {
			domains = tokenUtil.domains();
			log.debug("domains : "+domains);
		}
		
		Page<Bonus> bonusListPage = casinoBonusService.findBonusList(request, status, domains, bonusType);
		
		return new DataTableResponse<>(request, bonusListPage);
	}
	
	@GetMapping("/table/activation")
	public DataTableResponse<PlayerBonusHistoryActivationProjection> tableActivation(
		@RequestParam(name="bonusRevisionId", required=false) Long bonusRevisionId,
		@RequestParam(name="status", required=false) List<String> statuses,
		DataTableRequest request,
		Principal principal
	) throws Exception {
		log.debug("bonusRevisionId:"+bonusRevisionId+" status:"+statuses+" Request:"+request);
		
		Page<PlayerBonusHistoryActivationProjection> bonusActivationListPage = casinoBonusService.findBonusActivationList(request, bonusRevisionId, statuses);
		
		return new DataTableResponse<>(request, bonusActivationListPage);
	}
	
	@PostMapping("/table/pending")
	public DataTableResponse<PlayerBonusPendingProjection> tablePending(
		@RequestParam(name="playerGuid", required=true) String playerGuid,
		DataTableRequest request,
		Principal principal
	) throws Exception {
		log.debug("Pending bonus list request for player:" +playerGuid+ " Request:"+request);
		
		Page<PlayerBonusPendingProjection> bonusPendingListPage = casinoBonusService.findPendingBonusList(request, playerGuid);
		
		return new DataTableResponse<>(request, bonusPendingListPage);
	}
	
	@PostMapping("/table/pending/{bonusRevisionId}")
	public DataTableResponse<PlayerBonusPendingProjection> tablePendingForBonusRevision(
		@PathVariable("bonusRevisionId") BonusRevision bonusRevision,
		DataTableRequest request,
		Principal principal
	) throws Exception {
		log.debug("Pending bonus list request for bonusRevision:" +bonusRevision.getId()+ " Request:"+request);
		
		Page<PlayerBonusPendingProjection> bonusPendingListPage = casinoBonusService.findPendingBonusList(request, bonusRevision);
		
		return new DataTableResponse<>(request, bonusPendingListPage);
	}

	@PostMapping("/table/history")
	public DataTableResponse<PlayerBonusHistoryTableResponse> tableHistory(
			@RequestParam(name="playerGuid", required=true) String playerGuid,
			DataTableRequest request
	) throws Exception {
		log.info("Bonus history list request for player:" +playerGuid+ " Request:"+request);

		request.setPageRequest(PageRequest.of(request.getPageRequest().getPageNumber(),
											   request.getPageRequest().getPageSize() > 100 ? 100 : request.getPageRequest().getPageSize(),
							Sort.by(Direction.DESC, "startedDate")));

		Page<PlayerBonusHistory> playerBonusHistoryPage = casinoBonusService.findBonusHistoryList(request, playerGuid);

		List<PlayerBonusHistoryTableResponse> list = new ArrayList<>();
		for(PlayerBonusHistory pbh: playerBonusHistoryPage){
			PlayerBonusFreespinHistoryProjection playerBonusFreespinHistoryProjection = casinoBonusFreespinService.playerBonusFreespinHistoryProjection(pbh.getId());

			PlayerBonusHistoryTableResponse playerBonusHistoryTableResponse = PlayerBonusHistoryTableResponse.builder()
					.id(pbh.getId())
					.startedDate(pbh.getStartedDate())
					.playThroughCents(pbh.getPlayThroughCents())
					.playThroughRequiredCents(pbh.getPlayThroughRequiredCents())
					.triggerAmount(pbh.getTriggerAmount())
					.bonusAmount(pbh.getBonusAmount())
					.bonusPercentage(pbh.getBonusPercentage())
					.completed(pbh.getCompleted())
					.cancelled(pbh.getCancelled())
					.expired(pbh.getExpired())
					.bonus(pbh.getBonus())
					.playerBonus(pbh.getPlayerBonus())
					.customFreeMoneyAmountCents(pbh.getCustomFreeMoneyAmountCents())
					.customBonusTokenAmountCents(pbh.getCustomBonusTokenAmountCents())
					.requestId(pbh.getRequestId())
					.description(pbh.getDescription())
					.clientId(pbh.getClientId())
					.sessionId(pbh.getSessionId())
					.playerBonusFreespinHistoryProjection(playerBonusFreespinHistoryProjection)
					.build();
			list.add(playerBonusHistoryTableResponse);
		}


		Page<PlayerBonusHistoryTableResponse> bonusHistoryResponse = new SimplePageImpl<>(list, request.getPageRequest().getPageNumber(), request.getPageRequest().getPageSize(), playerBonusHistoryPage.getTotalElements());

		return new DataTableResponse<>(request, bonusHistoryResponse);
	}

	@PostMapping("/table/csv/{bonusRevisionId}")
	public DataTableResponse<BonusFileUpload> tableCsvForBonusRevision(
			@PathVariable("bonusRevisionId") BonusRevision bonusRevision,
			DataTableRequest request,
			Principal principal
	) throws Exception {
		log.debug("Csv bonus allocation list request for bonusRevision:" +bonusRevision.getId()+ " Request:"+request);

		Page<BonusFileUpload> bonusCsvListPage = casinoBonusFileUploadService.findCsvFileList(request, bonusRevision);

		return new DataTableResponse<>(request, bonusCsvListPage);
	}

	@PostMapping("/csv/download")
	@ResponseBody
	public void downloadCsvFile(@RequestParam("bonusFileUploadId") String bonusFileUploadId, HttpServletResponse response
	) throws Status421InvalidFileDataException,
			Status422InvalidParameterProvidedException,
			Status510GeneralCasinoExecutionException {
		casinoBonusFileUploadService.getCsvFile(bonusFileUploadId, response);
	}
}
