package lithium.service.geo.neustar.controller;

import lithium.service.geo.neustar.exception.UnauthorizedAPIKeyException;
import lithium.service.geo.neustar.objects.IpInfo;
import lithium.service.geo.neustar.response.GeolocationResponse;
import lithium.service.geo.neustar.services.NeustarCompatibleMaxMindLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class NeustarCompatibleGeoController {

    @Value("${lithium.service.geo.neustar-compatible-api-key}")
    private String apiKey;

    @Autowired
    private NeustarCompatibleMaxMindLookupService neustarCompatibleMaxMindLookupService;

    @GetMapping(value = "/neustar/geodirectory/v1/ipinfo/{address:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GeolocationResponse> location(@PathVariable String address, @RequestParam(value = "apikey") String apiKey) {

        if (!apiKey.equals(this.apiKey)) {
            throw new UnauthorizedAPIKeyException();
        }

        IpInfo ipInfo = neustarCompatibleMaxMindLookupService.lookup(address);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GeolocationResponse.builder()
                                         .ipInfo(ipInfo)
                                         .build()
                     );

    }

}
