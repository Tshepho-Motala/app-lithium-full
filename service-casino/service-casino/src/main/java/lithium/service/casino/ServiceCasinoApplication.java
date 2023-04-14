package lithium.service.casino;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.metrics.EnableLithiumMetrics;
import lithium.service.accounting.client.service.EnableAccountingClientService;
import lithium.service.accounting.client.transactiontyperegister.EnableTransactionTypeRegisterService;
import lithium.service.accounting.client.transactiontyperegister.TransactionTypeRegisterService;
import lithium.service.casino.controllers.CasinoBonusCreateController;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.domain.client.stream.EnableDomainEventsStream;
import lithium.service.event.client.stream.EnableEventStream;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.geo.client.stream.EnableGeoQueueStream;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.service.promo.client.stream.EnableMissionStatsStream;
import lithium.service.rabbit.exchange.EnableLithiumRabbitExchangeCreation;
import lithium.service.reward.client.EnablePlayerRewardUpdateClient;
import lithium.service.sms.client.stream.EnableSMSStream;
import lithium.service.stats.client.stream.EnableStatsStream;
import lithium.service.user.client.service.EnableLoginEventClientService;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static lithium.casino.CasinoTransactionLabels.GAME_PROVIDER_ID;

@Slf4j
@EnableAsync
@LithiumService
@EnableSMSStream
@EnableScheduling
@EnableMailStream
@EnableJpaAuditing
@EnableStatsStream
@EnableEventStream
@EnableDomainClient
@EnableGeoQueueStream
@EnableProviderClient
@EnableLithiumMetrics
@EnableLeaderCandidate
@EnableChangeLogService
@EnableNotificationStream
@EnableMissionStatsStream
@EnableDomainEventsStream
@EnableLithiumServiceClients
@EnableGatewayExchangeStream
@EnablePlayerRewardUpdateClient
@EnableAccountingClientService
@EnableLoginEventClientService
@EnableLimitInternalSystemClient
@EnableUserApiInternalClientService
@EnableLithiumRabbitExchangeCreation
@EnableCustomHttpErrorCodeExceptions
@EnableTransactionTypeRegisterService
public class ServiceCasinoApplication extends LithiumServiceApplication implements CasinoTransactionLabels {

	@Autowired
	private TransactionTypeRegisterService transactionTypeService;
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private CasinoBonusCreateController createController;

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoApplication.class, args);
	}
	
	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
	@Override
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		
		if (isLoadTestData()) {
			try {
				createController.loadDefaultBonusses("luckybetz");
			} catch (Exception ex) {
				log.error("Could not load test data.", ex);
			}
		}


		//For accounting, the player balance will always be in a negative to show player has balance (plb is liability to company)
		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_BET.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_BET.toString(), false, true);
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYER_BALANCE_FREEGAME.value(), true, false);
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYER_BALANCE_OPERATOR_MIGRATION.value(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_BET.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, GAME_PROVIDER_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, lithium.casino.CasinoTransactionLabels.LOGIN_EVENT_ID, true, true, false);
		}

		//For accounting, the player balance will always be in a negative to show player has balance (plb is liability to company)
		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_RESERVE.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.PLAYER_BALANCE.toString(), true, false); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_RESERVED_FUNDS.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoAccountTypeCodes.SPORTS_RESERVED_FUNDS.toString());
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_RESERVE_CANCEL.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.PLAYER_BALANCE.toString(), false, true); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_RESERVED_FUNDS.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoAccountTypeCodes.SPORTS_RESERVED_FUNDS.toString());
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_RESERVE_COMMIT.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.PLAYER_BALANCE.toString(), true, true);
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_RESERVED_FUNDS.toString(), true, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoAccountTypeCodes.SPORTS_RESERVED_FUNDS.toString());
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_BET.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.PLAYER_BALANCE.toString(), true, false);
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_BET.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.SPORTS_BET.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, lithium.casino.CasinoTransactionLabels.LOGIN_EVENT_ID, true, true, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_DEBIT.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.PLAYER_BALANCE.toString(), true, false);
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_DEBIT.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.SPORTS_DEBIT.toString());
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_WIN.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.PLAYER_BALANCE.toString(), false, true);
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_WIN.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.SPORTS_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, lithium.casino.CasinoTransactionLabels.LOGIN_EVENT_ID, true, true, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_LOSS.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.PLAYER_BALANCE.toString(), false, true); //TODO: This should not be a credit tran, it is zero valued for now, so should be ok
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_LOSS.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.SPORTS_LOSS.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_RESETTLEMENT.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_RESETTLEMENT.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.SPORTS_RESETTLEMENT.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_FREE_BET.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.PLAYER_BALANCE.toString(), true, false);
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_FREE_BET.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.SPORTS_FREE_BET.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_FREE_WIN.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.PLAYER_BALANCE.toString(), false, true);
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_FREE_WIN.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.SPORTS_FREE_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_FREE_LOSS.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.PLAYER_BALANCE.toString(), false, true);
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_FREE_LOSS.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.SPORTS_FREE_LOSS.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.SPORTS_FREE_RESETTLEMENT.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoAccountTypeCodes.SPORTS_FREE_RESETTLEMENT.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.SPORTS_FREE_RESETTLEMENT.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.VIRTUAL_BET.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false);
			transactionTypeService.addAccount(ttid, CasinoTranType.VIRTUAL_BET.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.VIRTUAL_BET.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, lithium.casino.CasinoTransactionLabels.LOGIN_EVENT_ID, true, true, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.VIRTUAL_FREE_BET.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false);
			transactionTypeService.addAccount(ttid, CasinoTranType.VIRTUAL_FREE_BET.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.VIRTUAL_FREE_BET.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, PLAYER_BONUS_TOKEN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_WIN.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_WIN.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, GAME_PROVIDER_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, lithium.casino.CasinoTransactionLabels.LOGIN_EVENT_ID, true, true, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_WIN_JACKPOT.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_WIN.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, GAME_PROVIDER_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, lithium.casino.CasinoTransactionLabels.LOGIN_EVENT_ID, true, true, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_LOSS.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_LOSS.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_LOSS.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.VIRTUAL_WIN.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.VIRTUAL_WIN.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.VIRTUAL_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
			transactionTypeService.addLabel(ttid, lithium.casino.CasinoTransactionLabels.LOGIN_EVENT_ID, true, true, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.VIRTUAL_FREE_WIN.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.VIRTUAL_FREE_WIN.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.VIRTUAL_FREE_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_TOKEN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.VIRTUAL_LOSS.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.VIRTUAL_LOSS.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.VIRTUAL_LOSS.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.VIRTUAL_FREE_LOSS.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.VIRTUAL_FREE_LOSS.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.VIRTUAL_FREE_LOSS.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_TOKEN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_BET_FREESPIN.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_BET.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_BET.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, GAME_PROVIDER_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_BET_FREESPIN_ROLLBACK.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_BET.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_REVERSE_LABEL, false, CasinoTranType.CASINO_BET.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_WIN_FREESPIN.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_WIN.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, GAME_PROVIDER_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_WIN_FREESPIN_JACKPOT.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_WIN.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, GAME_PROVIDER_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_VOID.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_VOID.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_VOID.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_LOSS_FREESPIN.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true);
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_LOSS_FREESPIN.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_LOSS_FREESPIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_NEGATIVE_BET.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true);
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_NEGATIVE_BET.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_NEGATIVE_BET.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
		}
		
		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_BET_ROLLBACK.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_BET.toString(), true, false);
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYER_BALANCE_FREEGAME.value(), false, true);
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_BET_ROLLBACK.value(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_REVERSE_LABEL, false, CasinoTranType.CASINO_BET.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.VIRTUAL_BET_VOID.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.VIRTUAL_BET_VOID.toString(), true, false);
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_TOKEN_ID, false); // Will work to remove requirement for label (LIVESCORE-636)
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.VIRTUAL_FREE_BET_VOID.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.VIRTUAL_FREE_BET_VOID.toString(), true, false);
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_TOKEN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_WIN_ROLLBACK.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_WIN.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_REVERSE_LABEL, false, CasinoTranType.CASINO_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_WIN_JACKPOT_ROLLBACK.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_WIN.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_REVERSE_LABEL, false, CasinoTranType.CASINO_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_WIN_FREESPIN_ROLLBACK.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_WIN.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_REVERSE_LABEL, false, CasinoTranType.CASINO_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_WIN_FREESPIN_JACKPOT_ROLLBACK.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_WIN.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_REVERSE_LABEL, false, CasinoTranType.CASINO_WIN.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_NEGATIVE_BET_ROLLBACK.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false);
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_NEGATIVE_BET.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_REVERSE_LABEL, false, CasinoTranType.CASINO_NEGATIVE_BET.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create("TRANSFER_TO_CASINO_BONUS").getData().getId();
			transactionTypeService.addAccount(ttid, "PLAYER_BALANCE", true, true);
			transactionTypeService.addLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addLabel(ttid, BONUS_REVISION_ID, true);
		}
		
		{
			Long ttid = transactionTypeService.create("TRANSFER_FROM_CASINO_BONUS").getData().getId();
			transactionTypeService.addAccount(ttid, "PLAYER_BALANCE", true, true);
			transactionTypeService.addAccount(ttid, "CASINO_BONUS_EXPIRED", true, true);
			transactionTypeService.addAccount(ttid, "CASINO_BONUS_CANCEL", true, true);
			transactionTypeService.addAccount(ttid, "CASINO_BONUS_MAXPAYOUT_EXCESS", true, true);
			transactionTypeService.addLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addLabel(ttid, BONUS_REVISION_ID, true);
		}
		
		{
			Long ttid = transactionTypeService.create("CASINO_BONUS_ACTIVATE").getData().getId();
			transactionTypeService.addAccount(ttid, "PLAYER_BALANCE", false, true);
			transactionTypeService.addAccount(ttid, "CASINO_BONUS_ACTIVATE", true, false);
			transactionTypeService.addLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addLabel(ttid, BONUS_REVISION_ID, true);
		}
		
		{
			Long ttid = transactionTypeService.create("CASINO_BONUS_CANCEL").getData().getId();
			transactionTypeService.addAccount(ttid, "PLAYER_BALANCE", true, false);
			transactionTypeService.addAccount(ttid, "CASINO_BONUS_CANCEL", false, true);
			transactionTypeService.addLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addLabel(ttid, BONUS_REVISION_ID, true);
		}
		
		{
			Long ttid = transactionTypeService.create("CASINO_BONUS_EXPIRED").getData().getId();
			transactionTypeService.addAccount(ttid, "PLAYER_BALANCE", true, false);
			transactionTypeService.addAccount(ttid, "CASINO_BONUS_EXPIRED", false, true);
			transactionTypeService.addLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addLabel(ttid, BONUS_REVISION_ID, true);
		}
		
		{
			Long ttid = transactionTypeService.create("CASINO_BONUS_MAXPAYOUT_EXCESS").getData().getId();
			transactionTypeService.addAccount(ttid, "PLAYER_BALANCE", true, false);
			transactionTypeService.addAccount(ttid, "CASINO_BONUS_MAXPAYOUT_EXCESS", false, true);
			transactionTypeService.addLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addLabel(ttid, BONUS_REVISION_ID, true);
		}
		{
			Long ttid = transactionTypeService.create(CasinoTranType.TRANSFER_TO_CASINO_BONUS_PENDING.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, true);
		//	transactionTypeService.addLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addLabel(ttid, BONUS_REVISION_ID, true);
		}
		
		{
			Long ttid = transactionTypeService.create(CasinoTranType.TRANSFER_FROM_CASINO_BONUS_PENDING.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, true);
			transactionTypeService.addLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addLabel(ttid, BONUS_REVISION_ID, true);
		}
		
		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_BONUS_PENDING.toString()).getData().getId();
			transactionTypeService.addAccount(ttid,  CasinoTranType.PLAYERBALANCE.toString(), false, true);
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_BONUS_PENDING.toString(), true, false);
//			transactionTypeService.addLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addLabel(ttid, BONUS_REVISION_ID, true);
		}
		
		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_BONUS_PENDING_CANCEL.toString()).getData().getId();
			transactionTypeService.addAccount(ttid,  CasinoTranType.CASINO_BONUS_PENDING.toString(), false, true);
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false);
//			transactionTypeService.addLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addLabel(ttid, BONUS_REVISION_ID, true);
		}
		{
			Long ttid = transactionTypeService.create(CasinoTranType.TRANSFER_TO_CASINO_ESCROW.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.PLAYERBALANCE.toString());
		}
		{
			Long ttid = transactionTypeService.create(CasinoTranType.TRANSFER_FROM_CASINO_ESCROW.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.PLAYERBALANCE.toString());
		}
		
		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_XP_GAIN.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true);
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_XP_GAIN.toString(), true, false);
			transactionTypeService.addLabel(ttid, BET_ACCOUNTING_TRANSACTION_ID, false);
			transactionTypeService.addLabel(ttid, XP_SCHEME_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.NEGATIVE_BALANCE_ADJUST.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true);
			transactionTypeService.addAccount(ttid, CasinoTranType.NEGATIVE_BALANCE_ADJUST.toString(), true, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.JACKPOT_ACCRUAL.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYER_JACKPOT_ACCRUALS.toString(), true, true, 1000000);
			transactionTypeService.addAccount(ttid, "JACKPOT_ACCRUALS", true, true, 1000000);
			transactionTypeService.addOptionalLabel(ttid, ACCRUAL_ID, false);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addOptionalLabel(ttid, PLATFORM_CODE, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.JACKPOT_ACCRUAL_CANCEL.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYER_JACKPOT_ACCRUALS.toString(), true, true);
			transactionTypeService.addAccount(ttid, "JACKPOT_ACCRUALS", true, true);
			transactionTypeService.addOptionalLabel(ttid, ACCRUAL_ID, false);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addOptionalLabel(ttid, PLATFORM_CODE, true);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_ADHOC_CREDIT.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_ADHOC_CREDIT.toString(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_ADHOC_CREDIT.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, lithium.casino.CasinoTransactionLabels.LOGIN_EVENT_ID, true, true, false);
		}

		{
			Long ttid = transactionTypeService.create(CasinoTranType.CASINO_ADHOC_DEBIT.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, CasinoTranType.PLAYERBALANCE.toString(), true, false); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CasinoTranType.CASINO_ADHOC_DEBIT.toString(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CasinoTranType.CASINO_ADHOC_DEBIT.toString());
			transactionTypeService.addOptionalLabel(ttid, PLAYER_BONUS_HISTORY_ID, true);
			transactionTypeService.addOptionalLabel(ttid, BONUS_REVISION_ID, true);
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, GAME_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, lithium.casino.CasinoTransactionLabels.LOGIN_EVENT_ID, true, true, false);
		}

		transactionTypeService.register();
	}

}
