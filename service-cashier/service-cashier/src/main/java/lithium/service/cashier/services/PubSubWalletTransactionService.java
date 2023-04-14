package lithium.service.cashier.services;


import lithium.service.cashier.client.objects.WalletTransactionsChanges;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionRemark;
import lithium.service.cashier.data.repositories.TransactionRemarkRepository;
import lithium.service.datafeed.provider.google.exeptions.PubSubInternalErrorException;
import lithium.service.datafeed.provider.google.objects.DataType;
import lithium.service.datafeed.provider.google.objects.PubSubMessage;
import lithium.service.datafeed.provider.google.service.EnablePubSubExchangeStream;
import lithium.service.datafeed.provider.google.service.ServicePubSubStream;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static lithium.service.domain.client.DomainSettings.PUB_SUB_WALLETS;


@Slf4j
@Service
@AllArgsConstructor
@EnablePubSubExchangeStream
public class PubSubWalletTransactionService {

    private final ServicePubSubStream servicePubSubStream;
    private final TransactionRemarkRepository transactionRemarkRepository;
    private final CachingDomainClientService cachingDomainClientService;
    private final TransactionService transactionService;

    public boolean isPubSubChannelActivated (String domainName){
        Domain domain = null;
        try {
            domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        } catch (Status550ServiceDomainClientException e) {
            log.error("can't find domain from cachingDomainClientService" + e.getMessage());
            return false;
        }
        Optional<String> labelValue = domain.findDomainSettingByName(PUB_SUB_WALLETS.key());
        String result = labelValue.orElse(PUB_SUB_WALLETS.defaultValue());
        return result.equalsIgnoreCase("true");
    }

    public void buildAndSendWalletTransactionMessage(Transaction transaction, Boolean isFirstDeposit) {
        if (isPubSubChannelActivated(transaction.getDomainMethod().getDomain().getName())) {
            try {
                TransactionRemark remark = transactionRemarkRepository.findTop1ByTransaction(transaction);

                WalletTransactionsChanges change = WalletTransactionsChanges
                        .builder()
                        .domain(transaction.getDomainMethod().getDomain().getName())
                        .eventSource(transaction.getDomainMethod().getName())
                        .eventType(transaction.getTransactionType().name())
                        .accountId(transaction.getUser().getId().toString())
                        .playerGuid(transaction.getUser().getGuid())
                        .value(transaction.getAmountCents())
                        .status(transaction.getCurrent().getStatus().getCode())
                        .statusDetail(transaction.getCurrent().getSource())
                        .createdDate(transaction.getCreatedOn())
                        .updatedDate(transaction.getCurrent().getTimestamp())
                        .transactionId(transaction.getId())
                        .transactionRemark(remark != null ? remark.getMessage() : "no remark")
                        .isFirstDeposit(isFirstDeposit)
                        .declineReason(transaction.getDeclineReason())
                        .build();
                addMessageToQueue(change, transaction.getDomainMethod().getDomain().getName());
                log.info("Message " + change + " sent to pup sub channel");
            } catch (PubSubInternalErrorException e) {
                log.error("can not send a pub-sub message to google service" + e.getMessage());
            }
        }
    }

    private void addMessageToQueue(WalletTransactionsChanges change, String domainName) throws PubSubInternalErrorException {
        servicePubSubStream.processWalletChange(
                PubSubMessage
                        .builder()
                        .timestamp(new Date().getTime())
                        .data(change)
                        .dataType(DataType.WALLET_TRANSACTIONS)
                        .eventType(change.getEventType())
                        .domainName(domainName)
                        .build()
        );
    }
}
