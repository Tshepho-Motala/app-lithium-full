package lithium.service.casino.client;

import lithium.exceptions.Status425DateParseException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.client.objects.request.CommandParams;
import lithium.service.casino.client.objects.response.CasinoBetHistoryCsvResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="service-casino-search", path="/system/bethistory")
public interface CasinoBetHistoryCsvGenerationClient {

    @PostMapping(value = "/get-casino-best-history-list")
    CasinoBetHistoryCsvResponse getList(
            @RequestBody CommandParams params) throws Status425DateParseException, Status500InternalServerErrorException;
}
