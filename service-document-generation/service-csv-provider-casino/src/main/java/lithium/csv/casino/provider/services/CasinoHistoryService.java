package lithium.csv.casino.provider.services;

import java.util.Map;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.client.CasinoBetHistoryCsvGenerationClient;
import lithium.service.casino.client.objects.CasinoBetHistoryCsv;
import lithium.service.casino.client.objects.request.CommandParams;
import lithium.service.casino.client.objects.response.CasinoBetHistoryCsvResponse;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.csv.provider.services.CsvProviderAdapter;
import lithium.service.document.generation.client.objects.CommonCommandParams;
import lithium.service.document.generation.client.objects.CsvContent;
import lithium.service.document.generation.client.objects.CsvDataResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CasinoHistoryService implements CsvProviderAdapter<CommonCommandParams> {

    private LithiumServiceClientFactory lithiumServiceClientFactory;

    @Override
    public Class<? extends CsvContent> getContentType() {
        return CasinoBetHistoryCsv.class;
    }
    @Override
    public Class<? extends CsvContent> getContentType(Map<String, String> parameters) {
        return getContentType();
    }

    @Override
    public CsvDataResponse getCsvData(CommonCommandParams params, int page) throws Status500InternalServerErrorException {
        try {
            CasinoBetHistoryCsvGenerationClient client = lithiumServiceClientFactory.target(CasinoBetHistoryCsvGenerationClient.class,
                    "service-casino-search", true);

            CommandParams copyParams = new CommandParams(params.getParamsMap());
            CasinoBetHistoryCsvResponse response = client.getList(copyParams);

            return new CsvDataResponse(response.getCasinoBetHistoryCsvList(), response.getTotalPages());
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage());
            throw new Status500InternalServerErrorException(e.getMessage(), e.fillInStackTrace());
        }
    }
}
