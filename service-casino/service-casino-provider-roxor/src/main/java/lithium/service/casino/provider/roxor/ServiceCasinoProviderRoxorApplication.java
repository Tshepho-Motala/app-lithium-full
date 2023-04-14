package lithium.service.casino.provider.roxor;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.modules.ModuleInfo;
import lithium.rest.EnableRestTemplate;
import lithium.service.casino.EnableCasinoClient;
import lithium.service.casino.provider.roxor.api.exceptions.EnableCustomRoxorHttpErrorCodeExceptions;
import lithium.service.casino.provider.roxor.api.schema.gameplay.OperationTypeEnum;
import lithium.service.casino.provider.roxor.api.schema.gameplay.TypeEnum;
import lithium.service.casino.provider.roxor.config.GameplayOperationEventHandlerProperties;
import lithium.service.casino.provider.roxor.enums.RoxorGameSuppliers;
import lithium.service.casino.provider.roxor.storage.entities.OperationType;
import lithium.service.casino.provider.roxor.storage.entities.Type;
import lithium.service.casino.provider.roxor.storage.repositories.OperationTypeRepository;
import lithium.service.casino.provider.roxor.storage.repositories.TypeRepository;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.games.client.progressivejackpotfeedregister.EnableProgressiveJackpotFeedRegistrationService;
import lithium.service.games.client.progressivejackpotfeedregister.ProgressiveJackpotFeedRegistrationService;
import lithium.service.games.client.stream.EnableSupplierGameMetaDataStream;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.reward.client.EnableQueryRewardClient;
import lithium.service.user.client.service.EnableLoginEventClientService;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.EnumSet;

@LithiumService
@ComponentScan(basePackages = "lithium.service.casino.provider")
@EnableScheduling
@EnableJpaAuditing
@EnableDomainClient
@EnableCasinoClient
@EnableRestTemplate
@EnableLeaderCandidate
@EnableQueryRewardClient
@EnableLithiumServiceClients
@EnableLoginEventClientService
@EnableLimitInternalSystemClient
@EnableSupplierGameMetaDataStream
@EnableUserApiInternalClientService
@EnableCustomHttpErrorCodeExceptions
@EnableCustomRoxorHttpErrorCodeExceptions
@EnableProgressiveJackpotFeedRegistrationService
@EnableConfigurationProperties(GameplayOperationEventHandlerProperties.class)
public class ServiceCasinoProviderRoxorApplication extends LithiumServiceApplication {
	@Autowired OperationTypeRepository operationTypeRepository;
	@Autowired TypeRepository typeRepository;

	@Autowired
	private ProgressiveJackpotFeedRegistrationService progressiveJackpotFeedRegistrationService;

	@Autowired
	private ModuleInfo moduleInfo;


	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCasinoProviderRoxorApplication.class, args);
	}

	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		// Add supported operation types
		operationTypeRepository.findOrCreateByCode(OperationTypeEnum.START_GAME_PLAY.name(), () -> new OperationType());
		operationTypeRepository.findOrCreateByCode(OperationTypeEnum.FINISH_GAME_PLAY.name(), () -> new OperationType());
		operationTypeRepository.findOrCreateByCode(OperationTypeEnum.TRANSFER.name(), () -> new OperationType());
		operationTypeRepository.findOrCreateByCode(OperationTypeEnum.CANCEL_TRANSFER.name(), () -> new OperationType());
		operationTypeRepository.findOrCreateByCode(OperationTypeEnum.ACCRUAL.name(), () -> new OperationType());
		operationTypeRepository.findOrCreateByCode(OperationTypeEnum.CANCEL_ACCRUAL.name(), () -> new OperationType());
		operationTypeRepository.findOrCreateByCode(OperationTypeEnum.FREE_PLAY.name(), () -> new OperationType());
		operationTypeRepository.findOrCreateByCode(OperationTypeEnum.CANCEL_FREE_PLAY.name(), () -> new OperationType());
		// Add supported operation type type
		typeRepository.findOrCreateByCode(TypeEnum.DEBIT.name(), () -> new Type());
		typeRepository.findOrCreateByCode(TypeEnum.CREDIT.name(), () -> new Type());
		typeRepository.findOrCreateByCode(TypeEnum.JACKPOT_CREDIT.name(), () -> new Type());

		registerRoxorProgressiveJackpotFeeds();
	}

	private void registerRoxorProgressiveJackpotFeeds() {
		for (RoxorGameSuppliers progressiveSuppliersEnumList : EnumSet.allOf(RoxorGameSuppliers.class)) {
			progressiveJackpotFeedRegistrationService.create(moduleInfo.getModuleName(),
					progressiveSuppliersEnumList.getGameSupplierName());
			progressiveJackpotFeedRegistrationService.register();
		}
	}

}
