package lithium.service.limit.controllers.backoffice;


import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.data.entities.RealityCheckSetRequest;
import lithium.service.limit.data.entities.RealityCheckSet;
import lithium.service.limit.data.entities.RealityCheckTrackDataFE;
import lithium.service.limit.data.entities.User;
import lithium.service.limit.services.RealityCheckService;
import lithium.service.user.client.exceptions.UserNotFoundException;

import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/backoffice/reality-check/v1/{domainName}")
public class BackofficeRealityCheckController {

    @Autowired
    RealityCheckService userRealityCheckService;


    @GetMapping("/getlistinmillis")
    public Response<List<Integer>> getDefaultTimersInMs(@PathVariable String domainName) throws Status550ServiceDomainClientException {
        return Response.<List<Integer>>builder()
                .data(userRealityCheckService.getListOfDefaultTimersInMilliseconds(domainName))
                .status(Response.Status.OK).build();
    }
    @GetMapping("/getlistinmins")
    public Response<List<Double>> getDefaultTimersInMns(@PathVariable String domainName) throws Status550ServiceDomainClientException {
        return Response.<List<Double>>builder()
                .data(userRealityCheckService.getListOfDefaultTimersInMinutes(domainName))
                .status(Response.Status.OK).build();
    }


    @GetMapping("/get")
    public Response<RealityCheckSet> get(
            @PathVariable("domainName") String domainName,
            @RequestParam("guid") String guid) {
        try {
            return Response.<RealityCheckSet>builder()
                    .data(userRealityCheckService.getCurrentRealitySet(guid))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to get reality check period  [guid=" + guid + "] " + e.getMessage(), e);
            return Response.<RealityCheckSet>builder().data(null).status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/set")
    public Response<RealityCheckSet> set(
            LithiumTokenUtil token,
            @PathVariable("domainName") String domainName,
            @RequestBody RealityCheckSetRequest request) {
        try {
            return Response.<RealityCheckSet>builder()
                    .data(userRealityCheckService.setRealityCheckTimerTime(token.guid(), request.getPlayerGuid(), (request.getNewRealityCheckTime()*60000), token))
                    .status(Response.Status.OK).build();
        } catch (UserNotFoundException | Status500InternalServerErrorException e) {
            log.error("Failed to set reality check period  [guid=" + request.getPlayerGuid() + "] " + e.getMessage(), e);
            return Response.<RealityCheckSet>builder().data(null).status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();

        }
    }

    @GetMapping("/audit")
    public DataTableResponse<RealityCheckTrackDataFE> audit(User user, DataTableRequest request) {
        return new DataTableResponse<>(request, userRealityCheckService.getListOfTrackData(user.getGuid(), request.getPageRequest()));
    }
}
