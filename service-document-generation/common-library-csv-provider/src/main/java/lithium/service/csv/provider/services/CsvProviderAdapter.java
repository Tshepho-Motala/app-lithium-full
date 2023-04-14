package lithium.service.csv.provider.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.document.generation.client.objects.CommandParams;
import lithium.service.document.generation.client.objects.CommonCommandParams;
import lithium.service.document.generation.client.objects.CsvContent;
import lithium.service.document.generation.client.objects.CsvDataResponse;

import java.util.Map;


public interface CsvProviderAdapter<T extends CommandParams> {
    CsvDataResponse getCsvData(T params, int page) throws Status500InternalServerErrorException;

    Class<? extends CsvContent> getContentType();
    Class<? extends CsvContent> getContentType(Map<String, String> parameters);

    default CommandParams buildCommandParams(Map<String, String> paramsMap) {
        return new CommonCommandParams(paramsMap);
    }
}
