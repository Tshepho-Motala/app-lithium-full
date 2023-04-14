package lithium.csv.provider.threshold.service;

import static lithium.csv.provider.threshold.service.ThresholdsFilterRequestParams.buildThresholdFilter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lithium.csv.provider.threshold.config.CsvThresholdProviderConfigurationProperties;
import lithium.csv.provider.threshold.data.ThresholdDepositHistoryCsv;
import lithium.csv.provider.threshold.data.ThresholdHistoryCsv;
import lithium.csv.provider.threshold.data.ThresholdLossLimitHistoryCsv;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.client.objects.Granularity;
import lithium.service.csv.provider.services.CsvProviderAdapter;
import lithium.service.document.generation.client.objects.CommandParams;
import lithium.service.document.generation.client.objects.CsvContent;
import lithium.service.document.generation.client.objects.CsvDataResponse;
import lithium.service.user.threshold.client.UserThresholdClient;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryDto;
import lithium.service.user.threshold.client.dto.ThresholdsFilterRequest;
import lithium.service.user.threshold.client.enums.EType;
import lithium.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ThresholdsRecordsService implements CsvProviderAdapter<ThresholdsFilterRequestParams> {

    private LithiumServiceClientFactory lithiumServiceClientFactory;

    private CsvThresholdProviderConfigurationProperties properties;

    @Override
    public Class<? extends CsvContent> getContentType() {
        return ThresholdHistoryCsv.class;
    }

    @Override
    public Class<? extends CsvContent> getContentType(Map<String, String> parameters) {
        if (parameters.containsKey("typeName")) {
            String typeName = parameters.get("typeName");
            if (typeName.equalsIgnoreCase("TYPE_DEPOSIT_LIMIT")) {
                return ThresholdDepositHistoryCsv.class;
            } else if (typeName.equalsIgnoreCase("TYPE_LOSS_LIMIT")) {
                return ThresholdLossLimitHistoryCsv.class;
            }
        }
        return getContentType();
    }

    @Override
    public CommandParams buildCommandParams(Map<String, String> paramsMap) {
        return new ThresholdsFilterRequestParams(paramsMap);
    }

    @Override
    public CsvDataResponse getCsvData(ThresholdsFilterRequestParams params, int page) throws Status500InternalServerErrorException {
        try {
            ThresholdsFilterRequest filter = buildThresholdFilter(params);

            log.trace("ThresholdsFilterRequest: "+filter);

            UserThresholdClient client = lithiumServiceClientFactory.target(UserThresholdClient.class, "service-user-threshold", true);

            DataTableResponse<PlayerThresholdHistoryDto> thresholdHistoryDTOPage = client.getThresholdLimits(
                filter.getDomains()[0],
                filter.getPlayerGuid(),
                new String []{filter.getTypeName()},
                (StringUtil.isEmpty(filter.getGranularity()))?null:Integer.parseInt(filter.getGranularity()),
                filter.getStartDateTime(),
                filter.getEndDateTime()
            );

            switch (EType.fromName(filter.getTypeName())) {
                case TYPE_LOSS_LIMIT -> {
                    return new CsvDataResponse(
                        collectLossLimitTransactionData(thresholdHistoryDTOPage.getData()),
                        thresholdHistoryDTOPage.getRecordsTotalPages());
                }
                case TYPE_DEPOSIT_LIMIT -> {
                    return new CsvDataResponse(
                        collectDepositTransactionData(thresholdHistoryDTOPage.getData()),
                        thresholdHistoryDTOPage.getRecordsTotalPages());
                }
                default -> {
                    return new CsvDataResponse(null, 0);
                }
            }
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage());
            throw new Status500InternalServerErrorException(e.getMessage(), e.fillInStackTrace());
        }
    }

    private List<ThresholdLossLimitHistoryCsv> collectLossLimitTransactionData(List<PlayerThresholdHistoryDto> transactions) {
        return transactions.stream()
                .map(t -> buildLossLimitThresholdHistoryCsv(t))
                .toList();
    }
    private List<ThresholdDepositHistoryCsv> collectDepositTransactionData(List<PlayerThresholdHistoryDto> transactions) {
        return transactions.stream()
            .map(t -> buildDepositThresholdHistoryCsv(t))
            .toList();
    }

    private ThresholdLossLimitHistoryCsv buildLossLimitThresholdHistoryCsv(PlayerThresholdHistoryDto t) {
        ThresholdLossLimitHistoryCsv historyCsv = new ThresholdLossLimitHistoryCsv();
        historyCsv.setUsername(t.getUser().getUsername());
        historyCsv.setDomainName(t.getUser().getDomain().getName());
        historyCsv.setPlayerId(t.getUser().getUserId());
        historyCsv.setThresholdHitDate(t.getThresholdHitDate().toString());
        historyCsv.setThresholdHit(getThresholdHit((Granularity.fromType(t.getGranularity()))));
        historyCsv.setLossLimit(getLossLimit(t));
        historyCsv.setLossLimitUsed(getLossLimitUsed(t));
        historyCsv.setAccountCreationDate(t.getAccountCreationDate().toString());
        historyCsv.setThreshold(t.getThresholdRevision().getPercentage().toPlainString()+"%");

        return historyCsv;
    }
    private ThresholdDepositHistoryCsv buildDepositThresholdHistoryCsv(PlayerThresholdHistoryDto t) {
        ThresholdDepositHistoryCsv historyCsv = new ThresholdDepositHistoryCsv();
        historyCsv.setUsername(t.getUser().getUsername());
        historyCsv.setDomainName(t.getUser().getDomain().getName());
        historyCsv.setPlayerId(t.getUser().getUserId());
        historyCsv.setThresholdHitDate(t.getThresholdHitDate().toString());
        historyCsv.setThresholdHit(getThresholdHit((Granularity.fromType(t.getGranularity()))));
        historyCsv.setLossLimit(getLossLimit(t));
        historyCsv.setLossLimitUsed(getLossLimitUsed(t));
        historyCsv.setAccountCreationDate(t.getAccountCreationDate().toString());
        historyCsv.setThreshold(t.getAmount().toPlainString());
        historyCsv.setWithdrawalAmount(getNonNullValue(t.getWithdrawalAmount().toPlainString()));
        historyCsv.setDepositAmount(getNonNullValue(t.getDepositAmount().toPlainString()));
        historyCsv.setNetLifetimeDepositAmount(getNonNullValue(t.getNetLifetimeDepositAmount().toPlainString()));

        return historyCsv;
    }

    private static String getNonNullValue(String amount) {
        return Optional.ofNullable(amount).orElse("");
    }

    private static String getDate(String milliseconds) {
        DateFormat simple = new SimpleDateFormat(
                "dd MMM yyyy HH:mm:ss");
        Date result = new Date(Long.parseLong(milliseconds));
        return simple.format(result);
    }

    private String getLossLimit(PlayerThresholdHistoryDto t) {
        return switch (Granularity.fromType(t.getGranularity())) {
            case GRANULARITY_DAY -> t.getDailyLimit().toPlainString();
            case GRANULARITY_WEEK -> t.getWeeklyLimit().toPlainString();
            case GRANULARITY_MONTH -> t.getMonthlyLimit().toPlainString();
            case GRANULARITY_YEAR -> "-";
            default -> "";
        };
    }
    private String getLossLimitUsed(PlayerThresholdHistoryDto t) {
        return switch (Granularity.fromType(t.getGranularity())) {
            case GRANULARITY_DAY -> t.getDailyLimitUsed().toPlainString();
            case GRANULARITY_WEEK -> t.getWeeklyLimitUsed().toPlainString();
            case GRANULARITY_MONTH -> t.getMonthlyLimitUsed().toPlainString();
            case GRANULARITY_YEAR -> "-";
            default -> "";
        };
    }

    private String getThresholdHit(Granularity granularity) {
        ///TODO: Needs translations
        return switch (granularity) {
            case GRANULARITY_DAY -> "Daily";
            case GRANULARITY_WEEK -> "Weekly";
            case GRANULARITY_MONTH -> "Monthly";
            case GRANULARITY_YEAR -> "Annual";
            default -> "";
        };
    }
}
