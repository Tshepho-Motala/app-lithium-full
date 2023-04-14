package lithium.service.casino.controllers;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.Period;
import lithium.service.casino.data.entities.Bonus;
import lithium.service.casino.data.entities.BonusRequirementsDeposit;
import lithium.service.casino.data.entities.BonusRequirementsSignup;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.BonusRulesFreespinGames;
import lithium.service.casino.data.entities.BonusRulesFreespins;
import lithium.service.casino.data.entities.BonusRulesGamesPercentages;
import lithium.service.casino.data.entities.GameCategory;
import lithium.service.casino.data.entities.WageringRequirements;
import lithium.service.casino.data.objects.BonusCreate;
import lithium.service.casino.data.objects.BonusEdit;
import lithium.service.casino.data.repositories.GameCategoryRepository;
import lithium.service.casino.data.repositories.WageringRequirementsRepository;
import lithium.service.casino.exceptions.Status404BonusNotFoundException;
import lithium.service.casino.exceptions.Status405BonusDeleteNotAllowedException;
import lithium.service.casino.service.CasinoBonusService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import javax.validation.Valid;
import java.security.Principal;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/casino")
public class CasinoBonusCreateController {
	@Autowired CasinoBonusService bonusService;
	@Autowired WageringRequirementsRepository wageringRequirementsRepository;
	@Autowired GameCategoryRepository gameCategoryRepository;
	@Autowired ChangeLogService changeLogService;
	@Autowired LithiumTokenUtilService tokenService;

	@PostMapping(value = "/bonus")
	public Response<Bonus> save(@RequestBody @Valid BonusEdit bonusEdit, BindingResult bindingResult, Principal principal) throws Exception {
		log.debug("bonusEdit:"+bonusEdit);
		LithiumTokenUtil util = tokenService.getUtil(principal);
		bonusEdit.setPrincipalEdit(util.guid());
		Bonus bonus = bonusService.saveBonus(bonusEdit);
		
		log.debug("bonus:"+bonus);
		
		return Response.<Bonus>builder().data(bonus).build();
	}

	@DeleteMapping("/bonus/remove")
	public Response deleteBonus(
			@RequestParam("id") Long id,
			Principal principal
	) throws Status404BonusNotFoundException, Status405BonusDeleteNotAllowedException {
		bonusService.deleteBonus(id, principal);
		return Response.<Bonus>builder().status(Response.Status.OK).message("Bonus with id " + id + " was successfully deleted").build();
	}

//	@GetMapping(value = "/bonus/gamepercentage") //{domainName}/{gameGuid}/{bonusRevisionId}")
//	public @ResponseBody Response<BonusRulesGamesPercentages> gamepercentage(
//		@RequestParam("domainName") String domainName,
//		@RequestParam("gameGuid") String gameGuid,
//		@RequestParam("bonusRevisionId") Long bonusRevisionId
//	) throws Exception {
//		Collection<?> distributedObjects = client.getDistributedObjects();
//		log.info("distributedObjects: "+distributedObjects);
//		for (Object o:distributedObjects) {
//			IMap<String, Object> map = (IMap)o;
//			log.info("map : "+map);
//			try {
//				if (map.getName().equalsIgnoreCase("lithium.service.casino.gamepercentage")) {
////					map.evictAll();
//					Map<?, Object> m = map.getAll(map.keySet());
//					log.info("m : "+m);
//					for (Object value:m.values()) {
//						log.info("value:"+value);
//					}
//				}
//			} catch (Exception e) {
//				log.error("   ::   "+e.getMessage());
//			}
//		}
//		return Response.<BonusRulesGamesPercentages>builder().data(bonusService.getGamePercentage(domainName, gameGuid, bonusRevisionId)).build();
//	}
	
	@GetMapping(value = "/bonus/{bonusId}/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable("bonusId") Long bonusId, @RequestParam int p) throws Exception {
		return changeLogService.listLimited(ChangeLogRequest.builder()
			.entityRecordId(bonusId)
			.entities(new String[] { "bonus" })
			.page(p)
			.build()
		);
	}
	
	@GetMapping(value = "/bonus/search/{bonusType}/{search}")
	public Response<Iterable<Bonus>> search(
		@PathVariable("bonusType") Integer bonusType,
		@PathVariable("search") String search
	) throws Exception {
		return Response.<Iterable<Bonus>>builder().data(bonusService.findTop50ByBonusCodeOrBonusName(bonusType, search)).build();
	}
	
	@GetMapping(value = "/bonus/revision/{bonusRevisionId}/mark/enabled/{enabled}")
	public Response<BonusRevision> markBonusRevisionEnabled(
		@PathVariable("bonusRevisionId") Long bonusRevisionId,
		@PathVariable("enabled") Boolean enabled,
		Principal principal
	) throws Exception {
		BonusRevision bonusRevision = bonusService.setBonusRevisionEnabled(bonusRevisionId, enabled);
		LithiumTokenUtil util = tokenService.getUtil(principal);
		List<ChangeLogFieldChange> clfc = Collections.singletonList(ChangeLogFieldChange.builder().field("enabled").fromValue(!enabled + "").toValue(enabled + "").build());
		changeLogService.registerChangesWithDomain("bonus", ((enabled)?"enable":"disable"),bonusRevision.getBonus().getId(),util.guid(), null, null, clfc, Category.BONUSES, SubCategory.BONUS_REVISION, 0, bonusRevision.getDomain().getName());
		
		return Response.<BonusRevision>builder().status(Status.OK).data(bonusRevision).build();
	}
	
//	@GetMapping(value = "/bonus/{bonusId}/mark/edit/{bonusRevisionId}")
//	public Response<Bonus> markBonusRevisionEdit(
//		@PathVariable("bonusId") Long bonusId,
//		@PathVariable("bonusRevisionId") Long bonusRevisionId,
//		Principal principal
//	) throws Exception {
//		Bonus bonus = bonusService.findBonus(bonusId);
//		Bonus bonusCopy = Bonus.builder().build();
//		BeanUtils.copyProperties(bonusCopy, bonus);
//		BonusRevision bonusRevision = bonusService.findBonusRevisionById(bonusRevisionId);
//		bonus.setEdit(bonusRevision);
//		bonus.setEditUser(principal.getName());
//		bonusService.saveBonus(bonus);
//		
//		List<ChangeLogFieldChange> clfc = changeLogService.compare(bonus, bonusCopy, new String[] { "editUser", "edit.id" });
//		changeLogService.registerChanges("bonus", "markbonusedit", bonusId, principal.getName(), null, null, clfc);
//		
//		return Response.<Bonus>builder().status(Status.OK).data(bonus).build();
//	}
	
	@GetMapping(value = "/bonus/{bonusId}/mark/current/{bonusRevisionId}")
	public Response<Bonus> markBonusRevisionCurrent(
		@PathVariable("bonusId") Long bonusId,
		@PathVariable("bonusRevisionId") Long bonusRevisionId,
		Principal principal
	) throws Exception {
		Bonus bonus = bonusService.findBonus(bonusId);
		Bonus bonusCopy = Bonus.builder().build();
		BeanUtils.copyProperties(bonusCopy, bonus);
		BonusRevision bonusRevision = bonusService.findBonusRevisionById(bonusRevisionId);
		bonus.setCurrent(bonusRevision);
		bonus.setEdit(null);
		bonus.setEditUser(null);
		bonusService.saveBonus(bonus);
		LithiumTokenUtil util = tokenService.getUtil(principal);
		
//		List<ChangeLogFieldChange> clfc = changeLogService.compare(bonus, bonusCopy, new String[] { "current.id" });
		List<ChangeLogFieldChange> clfc = Arrays.asList(ChangeLogFieldChange.builder().field("current.id").fromValue((bonusCopy.getCurrent()!=null)?bonusCopy.getCurrent().getId()+"":"").toValue(bonus.getCurrent().getId()+"").build());
		changeLogService.registerChangesWithDomain("bonus", "markbonuscurrent", bonusId, util.guid(), null, null, clfc, Category.BONUSES, SubCategory.BONUS_REVISION, 0, bonusRevision.getDomain().getName());
		
		return Response.<Bonus>builder().status(Status.OK).data(bonus).build();
	}
	
	@PostMapping(value = "/bonus/create")
	public Response<Bonus> createBonus(
		@RequestBody BonusCreate bonusCreate,
		Principal principal
	) throws Exception {
		LithiumTokenUtil util = tokenService.getUtil(principal);

		bonusCreate.setCreatedBy(util.guid());
		log.info("Create new bonus : "+bonusCreate);
		
		Bonus bonus = bonusService.createNewBonus(bonusCreate);
		
		List<ChangeLogFieldChange> clfc = Arrays.asList(ChangeLogFieldChange.builder().field("id").fromValue("").toValue(bonus.getId()+"").build());
		changeLogService.registerChangesWithDomain("bonus", "create", bonus.getId(), util.guid(), null, null, clfc, Category.BONUSES, SubCategory.BONUS_CREATE, 0, bonusCreate.getDomainName());
		
		return Response.<Bonus>builder().status(Status.OK).data(bonus).build();
	}
	
	@GetMapping(value = "/bonus/copy/{bonusRevisionId}")
	public Response<Bonus> copyBonusRevision(
		@PathVariable("bonusRevisionId") Long bonusRevisionId,
		Principal principal
	) throws Exception {
		log.info("Copy bonus revision for revision id : "+bonusRevisionId);
		BonusRevision bonusRevisionOld = bonusService.findBonusRevisionById(bonusRevisionId);
		BonusRevision bonusRevisionNew = bonusService.createNewBonusRevision(bonusRevisionId);
		LithiumTokenUtil util = tokenService.getUtil(principal);
		
		Bonus bonus = bonusRevisionOld.getBonus();
		if (bonus == null) {
			bonus = bonusService.createNewBonus();
		}
		bonus.setEdit(bonusRevisionNew);
		bonus.setEditUser(util.guid());
		bonus = bonusService.saveBonus(bonus);
		
//		List<BonusRequirementsDeposit> bonusRequirementsDepositList = 
		bonusService.copyBonusRulesFreespins(bonusRevisionId, bonusRevisionNew);
		bonusService.copyBonusRulesInstantReward(bonusRevisionId, bonusRevisionNew);
		bonusService.copyBonusRulesInstantRewardFreespin(bonusRevisionId, bonusRevisionNew);
		bonusService.copyBonusRulesCasinoChip(bonusRevisionId, bonusRevisionNew);
		bonusService.copyBonusRequirementsDeposit(bonusRevisionId, bonusRevisionNew);
		bonusService.copyBonusRulesGamePercentagesAndCategoryPercentages(bonusRevisionId, bonusRevisionNew);
		
		List<ChangeLogFieldChange> clfc = changeLogService.compare(bonusRevisionNew, bonusRevisionOld, new String[] { "id" });
		changeLogService.registerChangesWithDomain("bonus", "copybonus", bonus.getId(), util.guid(), null, null, clfc, Category.BONUSES, SubCategory.BONUS_REVISION, 0, bonusRevisionNew.getDomain().getName());
		
		return Response.<Bonus>builder().status(Status.OK).data(bonus).build();
	}
	
	@GetMapping("/bonus/revisions/table")
	public DataTableResponse<BonusRevision> revisionsTable(
		@RequestParam(required=true) Long bonusId,
		DataTableRequest request,
		Principal principal
	) throws Exception {
		log.info("bonus revisions table request " + request.toString());
		Page<BonusRevision> revisions = bonusService.findBonusRevisionsByBonusId(request, bonusId);
		
		return new DataTableResponse<>(request, revisions);
	}
	
	@RequestMapping(value = "/loaddefaultbonusses")
	public Response<Boolean> loadDefaultBonusses(@RequestParam("domainName") String domainName) throws Exception {
		/* http://www.luckybetz.com/?promotion=free-spins */
		bonusService.createBonus(
			domainName,
			BonusRevision.builder().bonusCode("").bonusType(BonusRevision.BONUS_TYPE_SIGNUP).bonusName("20 Free Spins")
				.maxRedeemable(1).validDays(30)
				.enabled(true).cancelOnBetBiggerThanBalance(true).cancelOnDepositMinimumAmount(500L)
				.playerMayCancel(true).visibleToPlayer(true).playThroughRequiredType(BonusRevision.PLAYTHROUGH_TYPE_BONUS_VALUE).build(),
			BonusRequirementsSignup.builder().wagerRequirements(0).build(),
			null,
			defaultBonusRulesGamesPercentages(),
			new BonusRulesFreespinGames[] { //Vegas Road Trip
				BonusRulesFreespinGames.builder().gameId("30207").bonusRulesFreespins(BonusRulesFreespins.builder().freespins(20).provider("service-casino-provider-nucleus").build()).build(),
				BonusRulesFreespinGames.builder().gameId("30208").bonusRulesFreespins(BonusRulesFreespins.builder().freespins(20).provider("service-casino-provider-nucleus").build()).build()
			}
		);
		/* http://www.luckybetz.com/?promotion=welcome */
		Bonus welcome1 = bonusService.createBonus(
				domainName,
				BonusRevision.builder().bonusCode("WELCOME1").bonusType(BonusRevision.BONUS_TYPE_DEPOSIT).bonusName("Welcome Bonus 1")
					.maxRedeemable(1).validDays(30).forDepositNumber(1)
					.enabled(true).cancelOnBetBiggerThanBalance(true).cancelOnDepositMinimumAmount(500L)
					.playerMayCancel(true).visibleToPlayer(true).playThroughRequiredType(BonusRevision.PLAYTHROUGH_TYPE_BONUS_VALUE).build(),
				null,
				new BonusRequirementsDeposit[] { BonusRequirementsDeposit.builder().minDeposit(2500L).maxDeposit(25000L).bonusPercentage(125).wagerRequirements(35).build()},
				defaultBonusRulesGamesPercentages(),
				new BonusRulesFreespinGames[] { //Mermaid's Treasure
					BonusRulesFreespinGames.builder().gameId("30103").bonusRulesFreespins(BonusRulesFreespins.builder().freespins(125).provider("service-casino-provider-nucleus").build()).build()
				}
		);
		
		Bonus welcome2 = bonusService.createBonus(
				domainName,
				BonusRevision.builder().bonusCode("WELCOME2").bonusType(BonusRevision.BONUS_TYPE_DEPOSIT).bonusName("Welcome Bonus 2")
					.maxRedeemable(1).validDays(30).forDepositNumber(2).dependsOnBonus(welcome1)
					.enabled(true).cancelOnBetBiggerThanBalance(true).cancelOnDepositMinimumAmount(500L)
					.playerMayCancel(true).visibleToPlayer(true).playThroughRequiredType(BonusRevision.PLAYTHROUGH_TYPE_BONUS_VALUE).build(),
				null,
				new BonusRequirementsDeposit[] { BonusRequirementsDeposit.builder().minDeposit(2500L).maxDeposit(25000L).bonusPercentage(100).wagerRequirements(35).build()	},
				defaultBonusRulesGamesPercentages(),
				new BonusRulesFreespinGames[] { //Firecrackers
					BonusRulesFreespinGames.builder().gameId("30004").bonusRulesFreespins(BonusRulesFreespins.builder().freespins(84).provider("service-casino-provider-nucleus").build()).build(),
					BonusRulesFreespinGames.builder().gameId("30005").bonusRulesFreespins(BonusRulesFreespins.builder().freespins(84).provider("service-casino-provider-nucleus").build()).build()
				}
		);
		
		bonusService.createBonus(
				domainName,
				BonusRevision.builder().bonusCode("WELCOME3").bonusType(BonusRevision.BONUS_TYPE_DEPOSIT).bonusName("Welcome Bonus 3")
					.maxRedeemable(1).validDays(30).forDepositNumber(3).dependsOnBonus(welcome2)
					.enabled(true).cancelOnBetBiggerThanBalance(true).cancelOnDepositMinimumAmount(500L)
					.playerMayCancel(true).visibleToPlayer(true).playThroughRequiredType(BonusRevision.PLAYTHROUGH_TYPE_BONUS_VALUE).build(),
				null,
				new BonusRequirementsDeposit[] { BonusRequirementsDeposit.builder().minDeposit(2500L).maxDeposit(30000L).bonusPercentage(150).wagerRequirements(40).build()	},
				defaultBonusRulesGamesPercentages(),
				new BonusRulesFreespinGames[] { //Fruit Serenity
					BonusRulesFreespinGames.builder().gameId("30102").bonusRulesFreespins(BonusRulesFreespins.builder().freespins(125).provider("service-casino-provider-nucleus").build()).build(),
					BonusRulesFreespinGames.builder().gameId("30130").bonusRulesFreespins(BonusRulesFreespins.builder().freespins(125).provider("service-casino-provider-nucleus").build()).build()
				}
		);
		
		/* http://www.luckybetz.com/?promotion=600-in-bonuses */
		bonusService.createBonus(
				domainName,
				BonusRevision.builder().bonusCode("50MONTHLY").bonusType(BonusRevision.BONUS_TYPE_DEPOSIT).bonusName("4-UP 50% Deposit Bonus")
					.maxRedeemable(4).maxRedeemableGranularity(Period.GRANULARITY_MONTH).validDays(30)
					.enabled(true).cancelOnBetBiggerThanBalance(true).cancelOnDepositMinimumAmount(500L)
					.playerMayCancel(true).visibleToPlayer(true).playThroughRequiredType(BonusRevision.PLAYTHROUGH_TYPE_BONUS_VALUE).build(),
				null,
				new BonusRequirementsDeposit[] { BonusRequirementsDeposit.builder().minDeposit(2500L).maxDeposit(15000L).bonusPercentage(50).wagerRequirements(35).build()	},
				defaultBonusRulesGamesPercentages(),
				null
		);
		
		/* http://www.luckybetz.com/?promotion=early-bird */
		bonusService.createBonus(
				domainName,
				BonusRevision.builder().bonusCode("EARLYBIRD").bonusType(BonusRevision.BONUS_TYPE_DEPOSIT).bonusName("EarlyBird Bonus")
					.maxRedeemable(1).maxRedeemableGranularity(Period.GRANULARITY_DAY).validDays(30)
					.activeDays(DayOfWeek.MONDAY.getValue() + "")
					.activeStartTime(new LocalTime(4, 0, 0).toDateTimeToday().toDate())
					.activeEndTime(new LocalTime(7, 0, 0).toDateTimeToday().toDate())
					.activeTimezone("EST")
					.enabled(true).cancelOnBetBiggerThanBalance(true).cancelOnDepositMinimumAmount(500L)
					.playerMayCancel(true).visibleToPlayer(true).playThroughRequiredType(BonusRevision.PLAYTHROUGH_TYPE_BONUS_VALUE).build(),
				null,
				new BonusRequirementsDeposit[] { BonusRequirementsDeposit.builder().minDeposit(2500L).maxDeposit(20000L).bonusPercentage(50).wagerRequirements(35).build()	},
				defaultBonusRulesGamesPercentages(),
				null
		);
		
		bonusService.createBonus(
			domainName,
			BonusRevision.builder().bonusCode("100BONUS").bonusType(BonusRevision.BONUS_TYPE_DEPOSIT).bonusName("100% First Deposit")
				.maxRedeemable(1).maxRedeemableGranularity(Period.GRANULARITY_DAY).validDays(30)
				.enabled(true).cancelOnBetBiggerThanBalance(true).cancelOnDepositMinimumAmount(500L)
				.startingDate(new LocalDateTime(2017, 2, 3, 0, 1, 0).toDate())
				.startingDateTimezone("EST")
				.expirationDate(new LocalDateTime(2017, 2, 9, 23, 59, 59).toDate())
				.expirationDateTimezone("EST")
				.playerMayCancel(true).visibleToPlayer(true).playThroughRequiredType(BonusRevision.PLAYTHROUGH_TYPE_BONUS_VALUE).build(),
			null,
			new BonusRequirementsDeposit[] { BonusRequirementsDeposit.builder().minDeposit(2500L).maxDeposit(30000L).bonusPercentage(100).wagerRequirements(35).build()},
			defaultBonusRulesGamesPercentages(),
			null
	);
		return Response.<Boolean>builder().status(Status.OK).data(true).build();
	}
	
	private BonusRulesGamesPercentages[] defaultBonusRulesGamesPercentages() {
		return new BonusRulesGamesPercentages[] {
			/* Progressive Slots */
			BonusRulesGamesPercentages.builder().percentage(0).gameGuid("service-casino-provider-rival/92").build(), /* One Million Reels BC */
			BonusRulesGamesPercentages.builder().percentage(0).gameGuid("service-casino-provider-rival/123").build(), /* Money Magic */
			BonusRulesGamesPercentages.builder().percentage(0).gameGuid("service-casino-provider-rival/98").build(), /* Major Moolah */
			BonusRulesGamesPercentages.builder().percentage(0).gameGuid("service-casino-provider-rival/875").build(), /* Jackpot Five Times Wins */
			/* Table Games */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/31").build(), /* Paigow Poker */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/10").build(), /* Red Dog */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/30").build(), /* Let-it-Ride */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/7").build(), /* Roulette – American */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/322").build(), /* Roulette – American */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/28").build(), /* Roulette – European */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/329").build(), /* Roulette – European */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/8").build(), /* Craps */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/23").build(), /* Casino Battle */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/5").build(), /* Blackjack - Multi-hand */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/274").build(), /* Blackjack */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/3").build(), /* Blackjack */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/27").build(), /* Three Card Poker */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/6").build(), /* Baccarat */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30235").build(), //American Blackjack
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30236").build(), //American Blackjack
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30237").build(), //American Blackjack
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30238").build(), //American Blackjack
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30247").build(), //Single Deck Blackjack
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30248").build(), //Single Deck Blackjack
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30249").build(), //Single Deck Blackjack
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30250").build(), //Single Deck Blackjack
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30266").build(), //American Roulette
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30261").build(), //European Roulette
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30306").build(), //VIP European Roulette
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30307").build(), //VIP American Roulette
			/* Live Casino */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-livedealer/4").build(), /* Roulette */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-livedealer/5").build(), /* Casino Hold'Em */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-livedealer/1").build(), /* Blackjack */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-livedealer/2").build(), /* BlackJack Unlimited */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-livedealer/3").build(), /* Baccarat */
			/* Video Poker */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/20").build(), /* Joker Poker */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/12").build(), /* Jacks or Better */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/57").build(), /* Double Joker */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/55").build(), /* Deuces and Joker */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30275").build(), /* Deuces Wild */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30274").build(), /* Deuces Wild */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30273").build(), /* Deuces Wild */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-nucleus/30272").build(), /* Deuces Wild */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/14").build(), /* Deuces Wild */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/59").build(), /* Tens or Better */
			BonusRulesGamesPercentages.builder().percentage(10).gameGuid("service-casino-provider-rival/13").build(), /* Aces and Faces */
		};
	}
	
	@RequestMapping(value = "/loaddefaultgamecategories")
	public void loadDefaultGameCategories() {
		gameCategoryRepository.save(GameCategory.builder().casinoCategory("slots").displayName("Slot Games").gameCategories(String.join(",", Arrays.asList("slots","video_slots","5_reel_slots","3_reel_slots","i_slots","story_slots"))).build());
		gameCategoryRepository.save(GameCategory.builder().casinoCategory("livedealer").displayName("Live Dealer").gameCategories(String.join(",", Arrays.asList("livedealer"))).build());
		gameCategoryRepository.save(GameCategory.builder().casinoCategory("table_games").displayName("Table Games").gameCategories(String.join(",", Arrays.asList("table_games","blackjack","multi_hand","casino_battle","craps", "baccarat", "casino_battle", "let_it_ride", "paigow_poker", "red_dog", "roulette", "three_card_poker", "table"))).build());
		gameCategoryRepository.save(GameCategory.builder().casinoCategory("poker").displayName("Poker Games").gameCategories(String.join(",", Arrays.asList("video_poker","paigow_poker","three_card_poker","video poker","multihand poker", "pyramid poker"))).build());
	}
	
	@RequestMapping(value = "/loaddefaultwageringrequirements")
	public void loadDefaultWageringRequirements() {
		wageringRequirementsRepository.save(WageringRequirements.builder().minimum(0).maximum(149).wager(35).build());
		wageringRequirementsRepository.save(WageringRequirements.builder().minimum(150).maximum(199).wager(40).build());
		wageringRequirementsRepository.save(WageringRequirements.builder().minimum(200).wager(45).build());
	}
}
