package lithium.service.cashier.services;

import lithium.cashier.CashierTransactionLabels;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingSummaryAccountLabelValueClient;
import lithium.service.accounting.objects.SummaryLabelValueTotal;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.data.entities.Image;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.objects.ProcessorAccountDetails;
import lithium.service.cashier.data.repositories.ProcessorUserCardRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProcessorAccountSummaryService {
    @Autowired
    private UserService userService;
    @Autowired
    private ProcessorUserCardRepository processorUserCardRepository;
    @Autowired
    private LithiumServiceClientFactory services;
    @Autowired
    private ImageService imageService;
    @Autowired
    Environment environment;

    public List<ProcessorAccountDetails> getProcessorAccountDetailsByUser(String userGuid, String domainName) throws LithiumServiceClientFactoryException {
        List<ProcessorUserCard> processorAccounts = processorUserCardRepository.findByUser(userService.findOrCreate(userGuid));
        List<String> paymentIds = processorAccounts.stream().map(ProcessorUserCard::getId).map(String::valueOf).collect(Collectors.toList());
        if (paymentIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<SummaryLabelValueTotal> accounts = getProcessorAccountSummary(domainName, paymentIds, userGuid);

        List<ProcessorAccountDetails> processorAccountDetails = processorAccounts
            .stream()
            .map(method -> {
                Image image = method.getDomainMethodProcessor().getDomainMethod().getMethod().getImage();
                Optional<SummaryLabelValueTotal> deposit = accounts.stream()
                    .filter(a -> a.getLabelValue().equals(String.valueOf(method.getId()))
                        && a.getAccountCode().equals(CashierTranType.DEPOSIT.value()))
                    .findAny();
                Optional<SummaryLabelValueTotal> withdraw = accounts.stream()
                    .filter(a -> a.getLabelValue().equals(String.valueOf(method.getId()))
                        && a.getAccountCode().equals(CashierTranType.PAYOUT.value()))
                    .findAny();

                BigDecimal depositSum = deposit.map(SummaryLabelValueTotal::getDebitCents)
                    .map(BigDecimal::new).orElse(BigDecimal.ZERO);

                BigDecimal withdrawSum = withdraw.map(SummaryLabelValueTotal::getCreditCents)
                    .map(BigDecimal::new).orElse(BigDecimal.ZERO);

                String currencyCode = deposit.map(SummaryLabelValueTotal::getCurrencyCode)
                    .orElse(withdraw.map(SummaryLabelValueTotal::getCurrencyCode).orElse(""));

                return ProcessorAccountDetails.builder()
                    .id(method.getId())
                    .processorIcon(image)
                    .name(method.getViewName())
                    .nameOnPayEntry(method.getName())
                    .depositSum(depositSum.movePointLeft(2))
                    .withdrawSum(withdrawSum.movePointLeft(2))
                    .netDeposit(depositSum.subtract(withdrawSum).movePointLeft(2))
                    .currencyCode(currencyCode)
                    .status(lithium.service.cashier.data.objects.ProcessorAccountStatus.builder()
                        .id(method.getStatus().getId())
                        .name(method.getStatus().getName())
                        .build())
                    .verified(method.getVerified())
                    .verificationError(Optional.ofNullable(method.getFailedVerification()).map(m -> m.getName()).orElse(null))
                    .contraAccount(method.getContraAccount())
                    .expiryDate(method.getExpiryDate())
                    .build();
            })
            .sorted(Comparator.comparing(ProcessorAccountDetails::getName))
            .collect(Collectors.toList());
        moveProcessorAccountDetailsWithHistoricStatusToTheEnd(processorAccountDetails);
        return processorAccountDetails;
    }

    private void moveProcessorAccountDetailsWithHistoricStatusToTheEnd(List<ProcessorAccountDetails> processorAccountDetails) {
        Comparator<ProcessorAccountDetails> historicStatus = (o1, o2) -> {
            if (o1.getStatus().getName().contains(PaymentMethodStatusType.HISTORIC.getName()) && !o2.getStatus().getName().contains(PaymentMethodStatusType.HISTORIC.getName())) {
                return 1;
            } else if (!o1.getStatus().getName().contains(PaymentMethodStatusType.HISTORIC.getName()) && o2.getStatus().getName().contains(PaymentMethodStatusType.HISTORIC.getName())) {
                return -1;
            }
            return 0;
        };
        processorAccountDetails.sort(historicStatus);
    }

    private List<SummaryLabelValueTotal> getProcessorAccountSummary(String domainName, List<String> processorAccountIds, String userGuid) throws LithiumServiceClientFactoryException {
        Boolean readOnlyEnabled = environment.getProperty("lithium.enable-read-only", Boolean.class, false);
        String provider = readOnlyEnabled ? "service-accounting-provider-readonly" : "service-accounting-provider-internal";
        AccountingSummaryAccountLabelValueClient client = services.target(AccountingSummaryAccountLabelValueClient.class, provider, true);
        Response<List<SummaryLabelValueTotal>> response = client.findSummaryLabelValueTotal(domainName, CashierTransactionLabels.PLAYER_PAYMENT_METHOD_REFERENCE, processorAccountIds, userGuid);
        return response.getData();
    }
    
}
