package lithium.service.limit.controllers.backoffice;

import lithium.service.Response;
import lithium.service.limit.client.exceptions.Status479DomainAgeLimitException;
import lithium.service.limit.data.dto.EditDomainAgeLimitRange;
import lithium.service.limit.data.dto.SaveDomainAgeLimitDto;
import lithium.service.limit.data.entities.DomainAgeLimit;
import lithium.service.limit.services.AgeLimitService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/backoffice/domain-age-limit/v1")
public class BackofficeDomainAgeLimitController {

    private final AgeLimitService service;
    private final ModelMapper mapper;

    @Autowired
    public BackofficeDomainAgeLimitController(AgeLimitService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/set-age-limit")
    public Response<DomainAgeLimit> setDomainAgeLimit(@RequestBody SaveDomainAgeLimitDto ageLimitDto, Principal principal, Locale locale) {

        try {
            return Response.<DomainAgeLimit>builder()
                    .data(mapper.map(service.saveDomainLimit(ageLimitDto, principal, locale.toString()), DomainAgeLimit.class))
                    .status(Response.Status.OK_SUCCESS).build();
        } catch (Status479DomainAgeLimitException e) {
            return Response.<DomainAgeLimit>builder().status(Response.Status.CONFLICT).message(e.getMessage()).build();
        } catch (Exception e) {
            return Response.<DomainAgeLimit>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/edit-age-limit")
    public Response<DomainAgeLimit> editDomainAgeLimit(@RequestBody DomainAgeLimit ageLimitDto, Principal principal, Locale locale) {

        try {
            return Response.<DomainAgeLimit>builder()
                    .data(mapper.map(service.editDomainAgeLimit(ageLimitDto, principal, locale.toString()), DomainAgeLimit.class))
                    .status(Response.Status.OK_SUCCESS).build();
        } catch (Status479DomainAgeLimitException e) {
            return Response.<DomainAgeLimit>builder().status(Response.Status.CONFLICT).message(e.getMessage()).build();
        } catch (Exception e) {
            return Response.<DomainAgeLimit>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/edit-age-limit-min-max")
    public Response<List<DomainAgeLimit>> editDomainAgeLimitMinMax(@RequestBody EditDomainAgeLimitRange ageLimitDto, Principal principal, Locale locale) {

        try {
            return Response.<List<DomainAgeLimit>>builder()
                    .data(service.editDomainLimitAgeGroup(ageLimitDto, principal, locale.toString())
                            .stream()
                            .map(domainAgeLimit -> mapper.map(domainAgeLimit, DomainAgeLimit.class))
                            .collect(Collectors.toList()))
                    .status(Response.Status.OK_SUCCESS).build();

        } catch (Status479DomainAgeLimitException e) {
            return Response.<List<DomainAgeLimit>>builder().status(Response.Status.CONFLICT).message(e.getMessage()).build();
        } catch (Exception e) {
            return Response.<List<DomainAgeLimit>>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();

        }

    }

    @PostMapping("/set-age-limit-group")
    public Response<List<DomainAgeLimit>> setDomainAgeLimitGroup(@RequestBody List<SaveDomainAgeLimitDto> limitDtoList, Principal principal, Locale locale) {

        try {
            return Response.<List<DomainAgeLimit>>builder()
                    .data(service.saveDomainLimitGrp(limitDtoList, principal, locale.toString())
                            .stream()
                            .map(domainAgeLimit -> mapper.map(domainAgeLimit, DomainAgeLimit.class))
                            .collect(Collectors.toList()))
                    .status(Response.Status.OK_SUCCESS).build();
        } catch (Status479DomainAgeLimitException e) {
            return Response.<List<DomainAgeLimit>>builder().status(Response.Status.CUSTOM.id(e.getCode())).message(e.getMessage()).build();
        } catch (Exception e) {
            return Response.<List<DomainAgeLimit>>builder().message(e.getLocalizedMessage())
                    .status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/find-age-limits/{domainName}")
    public Response<List<DomainAgeLimit>> getAllByDomain(@PathVariable("domainName") String domainName) {
        return Response.<List<DomainAgeLimit>>builder().data(service.findAllDomainAgeLimit(domainName)).status(Response.Status.OK_SUCCESS).build();
    }

    @DeleteMapping("/remove-domain-age-limit/{id}")
    public Response<Boolean> removeAgeLimit(@PathVariable("id") Long id, Principal principal) {
        try {
            service.removeDomainAgeLimitSingle(id, principal);
            return Response.<Boolean>builder().data(true)
                    .status(Response.Status.OK_SUCCESS).build();
        } catch (Exception e) {
            return Response.<Boolean>builder().data(false).message(e.getLocalizedMessage())
                    .status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/remove-domain-age-limit-group")
    public Response<Boolean> removeGroupAgeLimit(@RequestBody List<Long> id, Principal principal) {
        try {
            service.removeDomainAgeLimitGroup(id, principal);
            return Response.<Boolean>builder().data(true).status(Response.Status.OK_SUCCESS).build();
        } catch (Exception e) {
            return Response.<Boolean>builder().data(false).message(e.getLocalizedMessage())
                    .status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-age-range/{age}/{domainName}")
    public Response<List<lithium.service.limit.client.objects.DomainAgeLimit>> checkAgeLimitRange(
            @PathVariable("age") Integer age,
            @PathVariable("domainName") String domainName
    ) {
        List<DomainAgeLimit> empty = new ArrayList<>();

        List<lithium.service.limit.client.objects.DomainAgeLimit> toSend = service.isWithinAgeRange(age, domainName, empty)
                .stream().map(item -> mapper.map(item, lithium.service.limit.client.objects.DomainAgeLimit.class)).collect(Collectors.toList());

        return Response.<List<lithium.service.limit.client.objects.DomainAgeLimit>>builder().data(toSend).status(Response.Status.OK_SUCCESS).build();
    }
}
