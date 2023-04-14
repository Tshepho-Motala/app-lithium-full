package lithium.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import lithium.leader.EnableLeaderCandidate;
import lithium.service.accounting.client.AccountingTransactionTypeClient;
import lithium.service.casino.client.stream.EnableTriggerBonusStream;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.gateway.client.stream.EnableGatewayExchangeStream;
import lithium.service.product.client.objects.CashierLabels;
import lithium.service.product.client.objects.CashierTranType;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;

@LithiumService
@EnableLeaderCandidate
@EnableTriggerBonusStream
@EnableLithiumServiceClients
@EnableGatewayExchangeStream
public class ServiceCatalogApplication extends LithiumServiceApplication {
	@Autowired
	private LithiumServiceClientFactory clientFactory;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCatalogApplication.class, args);
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		AccountingTransactionTypeClient transactionTypeService = clientFactory.target(AccountingTransactionTypeClient.class,"service-accounting-provider-internal", true);
		//For accounting, the player balance will always be in a negative to show player has balance (plb is liability to company)
		{
			Long ttid = transactionTypeService.create(CashierTranType.PRODUCT_PURCHASE.value()).getData().getId();
			transactionTypeService.addAccount(ttid, CashierTranType.PLAYER_BALANCE.value(), true, false); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CashierTranType.PRODUCT_PURCHASE.value(), false, true);
			transactionTypeService.addUniqueLabel(ttid, CashierLabels.TRAN_ID_LABEL.value(), false, null, null, CashierTranType.PRODUCT_PURCHASE.value());
			transactionTypeService.addLabel(ttid, CashierLabels.PROVIDER_GUID_LABEL.value(), true, null, null);
			transactionTypeService.addLabel(ttid, CashierLabels.PROCESSING_METHOD_LABEL.value(), true, null, null);
			transactionTypeService.addLabel(ttid, CashierLabels.DOMAIN_METHOD_PROCESSOR_ID.value(), true, null, null);
			transactionTypeService.addLabel(ttid, CashierLabels.PRODUCT_ID.value(), true, null, null);
			transactionTypeService.addLabel(ttid, CashierLabels.CASHIER_TRAN_ID.value(), true, null, null);
		}
		{
			Long ttid = transactionTypeService.create(CashierTranType.PRODUCT_PAYOUT.value()).getData().getId();
			transactionTypeService.addAccount(ttid, CashierTranType.PLAYER_BALANCE.value(), false, true);
			transactionTypeService.addAccount(ttid, CashierTranType.PRODUCT_PAYOUT.value(), true, false);
			transactionTypeService.addLabel(ttid, CashierLabels.PAYOUT_ID.value(), true, null, null);
			transactionTypeService.addLabel(ttid, CashierLabels.PRODUCT_ID.value(), true, null, null);
			transactionTypeService.addLabel(ttid, CashierLabels.TRAN_ID_LABEL.value(), true, null, null);
		}
	}
}
