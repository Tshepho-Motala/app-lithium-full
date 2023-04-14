package lithium.service.limit.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.service.cashier.client.internal.TransactionProcessingCodeFE;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.limit.data.dto.DomainRestrictionSetDto;
import lithium.service.limit.data.entities.DomainRestriction;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.Restriction;
import lithium.service.limit.enums.AlternativeMessageAction;
import lithium.service.limit.services.RestrictionService;
import lithium.service.limit.services.UserRestrictionService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/backoffice/restrictions")
@Slf4j
public class BackofficeRestrictionsController {
    @Autowired
    private RestrictionService service;
    @Autowired
    private UserRestrictionService userRestrictionService;

    @Autowired
    private ObjectMapper mapper;

    @GetMapping
    public Response<Iterable<Restriction>> restrictions() {
        return Response.<Iterable<Restriction>>builder().data(service.restrictions()).status(Response.Status.OK).build();
    }

    @GetMapping("/sets/{domainName}")
    public Response<List<DomainRestrictionSetDto>> domainRestrictionSets(@PathVariable("domainName") String domainName,
                                                                      LithiumTokenUtil tokenUtil) {
        try {
            DomainValidationUtil.validate(domainName, "RESTRICTIONS_VIEW", tokenUtil);
            List<DomainRestrictionSet> set = service.findByDomainNameAndEnabledTrue(domainName);
            List<DomainRestrictionSetDto> dto = set.stream()
                    .map(this::convertToDto)
                    .toList();
            return Response.<List<DomainRestrictionSetDto>>builder().data(dto)
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Unable to find sets [domainName=" + domainName + "] " + e.getMessage(), e);
            return Response.<List<DomainRestrictionSetDto>>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @GetMapping("/{id}")
    public Response<DomainRestrictionSetDto> findById(@PathVariable("id") Long id, LithiumTokenUtil tokenUtil) {
        try {
            DomainRestrictionSet set = service.find(id);
            DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_VIEW", tokenUtil);
            return Response.<DomainRestrictionSetDto>builder().data(convertToDto(set)).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("DomainRestrictionSet not found [id=" + id + "] " + e.getMessage(), e);
            return Response.<DomainRestrictionSetDto>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @GetMapping("/table")
    public DataTableResponse<DomainRestrictionSetDto> table(
            @RequestParam("domains") String[] domains,
            @RequestParam(name = "enabled", required = false) Boolean enabled,
            DataTableRequest request,
            LithiumTokenUtil tokenUtil
    ) {
        DomainValidationUtil.filterDomainsWithRole(domains, "RESTRICTIONS_VIEW", tokenUtil);
        if (domains.length == 0) return new DataTableResponse<>(request, new ArrayList<>());
        Page<DomainRestrictionSet> table = service.find(domains, enabled, request.getSearchValue(),
                request.getPageRequest());
        return new DataTableResponse<>(request, table.map(this::convertToDto));
    }

    @GetMapping("/list")
    public Response<List<DomainRestrictionSetDto>> list(
            @RequestParam("domains") String[] domains,
            @RequestParam(name = "enabled", required = false) Boolean enabled,
            LithiumTokenUtil tokenUtil
    ) {
        DomainValidationUtil.filterDomainsWithRole(domains, "RESTRICTIONS_VIEW", tokenUtil);
        if (domains.length == 0) return Response.<List<DomainRestrictionSetDto>>builder()
                .data(new ArrayList<>())
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
        List<DomainRestrictionSet> list = service.find(domains, enabled);

        return Response.<List<DomainRestrictionSetDto>>builder()
                .data(list.stream().map(this::convertToDto).toList())
                .status(Response.Status.OK)
                .build();
    }

    @PostMapping("/create")
    public Response<DomainRestrictionSetDto> create(@RequestBody DomainRestrictionSet set, LithiumTokenUtil tokenUtil) {
        try {
            DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_ADD", tokenUtil);
            set = service.create(set, tokenUtil.guid());
            return Response.<DomainRestrictionSetDto>builder().data(convertToDto(set)).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to create domain restriction set [set=" + set + "]");
            return Response.<DomainRestrictionSetDto>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/changename")
    public Response<DomainRestrictionSetDto> changeName(@PathVariable("id") Long id,
                                                        @RequestParam("newName") String newName,
                                                        LithiumTokenUtil tokenUtil)
            throws Status500InternalServerErrorException {
        try {
            DomainRestrictionSet set = service.find(id);
            DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_EDIT", tokenUtil);
            set = service.changeName(set, newName, tokenUtil.guid());
            return Response.<DomainRestrictionSetDto>builder().data(convertToDto(set)).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to change domain restriction set name [id=" + id + ", newName=" + newName + "] "
                    + e.getMessage(), e);
            return Response.<DomainRestrictionSetDto>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/delete")
    public Response<DomainRestrictionSetDto> delete(@PathVariable("id") Long id,
                                                    LithiumTokenUtil tokenUtil)
            throws Status500InternalServerErrorException {
        try {
            DomainRestrictionSet set = service.find(id);
            if (userRestrictionService.isDomainRestrictionSetUsed(set)) {
                log.error("Failed to delete domain restriction set [id=" + id + "] " +
                        ".Active restrictions cannot be deleted. Lift the restriction from all players before deleting ");
                return Response.<DomainRestrictionSetDto>builder().status(Response.Status.CONFLICT)
                        .message("Active restrictions cannot be deleted. Lift the restriction from all players before deleting").build();
            }
            DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_EDIT", tokenUtil);
            set = service.deleteSet(set, tokenUtil.guid());
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to delete domain restriction set [id=" + id + "] " + e.getMessage(), e);
            return Response.<DomainRestrictionSetDto>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/toggle/enabled")
    public Response<DomainRestrictionSetDto> toggleEnabled(
            @PathVariable("id") Long id,
            LithiumTokenUtil tokenUtil
    ) {
        try {
            DomainRestrictionSet set = service.find(id);
            DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_EDIT", tokenUtil);
            set = service.toggleEnabled(set, tokenUtil.guid());
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to toggle enabled flag on domain restriction set [id=" + id + "] " + e.getMessage(), e);
            return Response.<DomainRestrictionSetDto>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/toggle/dwh-visible")
    public Response<DomainRestrictionSetDto> toggleDwhVisibility(
            @PathVariable("id") Long id,
            LithiumTokenUtil tokenUtil
    ) {
        try {
            DomainRestrictionSet set = service.find(id);
            DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_EDIT", tokenUtil);
            set = service.toggleDwhVisibility(set, tokenUtil.guid());
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to toggle enabled 'DWH visible' flag on domain restriction set [id=" + id + "] " + e.getMessage(), e);
            return Response.<DomainRestrictionSetDto>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/toggle/communicate-to-player")
    public Response<DomainRestrictionSetDto> toggleCommunicateToPlayer(
            @PathVariable("id") Long id,
            LithiumTokenUtil tokenUtil
    ) {
        try {
            DomainRestrictionSet set = service.find(id);
            DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_EDIT", tokenUtil);
            set = service.toggleCommunicateToPlayer(set, tokenUtil.guid());
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to toggle communicateToPlayer flag on domain restriction set [id=" + id + " for userguid: " + tokenUtil.guid() + "]" + e.getMessage(), e);
            return Response.<DomainRestrictionSetDto>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/restriction/add")
    public Response<DomainRestrictionSetDto> addRestriction(
            @PathVariable("id") Long id,
            @RequestBody DomainRestriction restriction,
            LithiumTokenUtil tokenUtil
    ) {
        try {
            DomainRestrictionSet set = service.find(id);
            DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_EDIT", tokenUtil);
            set = service.addRestriction(set, restriction, tokenUtil.guid());
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to add restriction to domain restriction set [id=" + id + ", restriction=" + restriction + "] "
                    + e.getMessage(), e);
            return Response.<DomainRestrictionSetDto>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/restriction/{restrictionId}/update")
    public Response<DomainRestrictionSetDto> updateRestriction(
            @PathVariable("id") Long id,
            @PathVariable("restrictionId") Long restrictionId,
            @RequestBody DomainRestriction restrictionUpdate,
            LithiumTokenUtil tokenUtil
    ) {
        try {
            DomainRestrictionSet set = service.find(id);
            DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_EDIT", tokenUtil);
            DomainRestriction restriction = service.findDomainRestriction(restrictionId);
            set = service.updateRestriction(set, restriction, restrictionUpdate, tokenUtil.guid());
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to update restriction on domain restriction set [id=" + id + ", restrictionId=" + restrictionId
                    + ", restrictionUpdate=" + restrictionUpdate + "] " + e.getMessage(), e);
            return Response.<DomainRestrictionSetDto>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/restriction/{restrictionId}/delete")
    public Response<DomainRestrictionSetDto> deleteRestriction(
            @PathVariable("id") Long id,
            @PathVariable("restrictionId") Long restrictionId,
            LithiumTokenUtil tokenUtil
    ) {
        try {
            DomainRestrictionSet set = service.find(id);
            DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_EDIT", tokenUtil);
            DomainRestriction restriction = service.findDomainRestriction(restrictionId);
            set = service.deleteRestriction(set, restriction, tokenUtil.guid());
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to delete restriction on domain restriction set [id=" + id
                    + ", restrictionId=" + restrictionId + "] " + e.getMessage(), e);
            return Response.<DomainRestrictionSetDto>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }


    @GetMapping("/{id}/changelogs")
    public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p,
                                                         LithiumTokenUtil tokenUtil) throws Exception {
        DomainRestrictionSet set = service.find(id);
        DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_EDIT", tokenUtil);
        return service.getChangeLogs(id, new String[]{"DomainRestrictionSet", "DomainRestrictionSet.restriction"}, p);
    }

    @RequestMapping(value = "/{id}/update-altmessage-count/{action}", method = RequestMethod.POST)
    public @ResponseBody Response<DomainRestrictionSetDto> updateSetAltMessageCount(@PathVariable Long id, @PathVariable String action,
                                                                                    LithiumTokenUtil tokenUtil) throws Exception {
        DomainRestrictionSet set = service.find(id);

        if (set == null) {
            return Response.<DomainRestrictionSetDto>builder().status(Response.Status.BAD_REQUEST)
                    .message("An invalid DomainRestrictionSet was supplied")
                    .build();
        }

        DomainValidationUtil.validate(set.getDomain().getName(), "RESTRICTIONS_EDIT", tokenUtil);
        AlternativeMessageAction alternativeMessageAction = AlternativeMessageAction.fromName(action);

        if (alternativeMessageAction == null) {
            return Response.<DomainRestrictionSetDto>builder().status(Response.Status.BAD_REQUEST)
                    .message("Invalid action suppled.")
                    .build();
        }

        DomainRestrictionSet domainRestrictionSet = service.updateAltMessageCount(set.getId(), alternativeMessageAction);
        DomainRestrictionSetDto dto = convertToDto(domainRestrictionSet);
        return Response.<DomainRestrictionSetDto>builder()
                .status(Response.Status.OK_SUCCESS)
                .data(dto)
                .build();
    }

    @GetMapping("/restriction-outcome-action-codes")
    public Response<List<TransactionProcessingCodeFE>> restrictionOutcomeActionCodes() {
        List<TransactionProcessingCodeFE> wrappedCodes = Arrays.asList(TransactionProcessingCode.values())
                .stream()
                .map(code -> new TransactionProcessingCodeFE(code.name()))
                .collect(Collectors.toList());
        return Response.<List<TransactionProcessingCodeFE>>builder()
                .data(wrappedCodes)
                .status(Response.Status.OK)
                .build();
    }

    @PostMapping("/{id}/change-place-actions")
    public Response<DomainRestrictionSetDto> changePlaceActions(@PathVariable("id") Long id,
                                                                @RequestParam(value = "actions", required = false) List<TransactionProcessingCode> actionsFE,
                                                                LithiumTokenUtil tokenUtil) {
        try {
            DomainRestrictionSet set = service.find(id);
            List<TransactionProcessingCode> actions = Optional.ofNullable(actionsFE).orElse(new ArrayList<>());

            set = service.updatePlaceActions(set, actions, tokenUtil);
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.<DomainRestrictionSetDto>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/change-lift-actions")
    public Response<DomainRestrictionSetDto> changeLiftActions(@PathVariable("id") Long id,
                                                               @RequestParam(value = "actions", required = false) List<TransactionProcessingCode> actionsFE,
                                                               LithiumTokenUtil tokenUtil) {
        try {
            DomainRestrictionSet set = service.find(id);
            List<TransactionProcessingCode> actions = Optional.ofNullable(actionsFE).orElse(new ArrayList<>());

            set = service.updateLiftActions(set, actions, tokenUtil);
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.<DomainRestrictionSetDto>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/update-exclude-tag-id")
    public Response<DomainRestrictionSetDto> updateExcludeTag(@PathVariable("id") Long id, @RequestParam(value = "excludeTagId", required = false) Long excludeTagId,
                                                           LithiumTokenUtil util) {
        try {
            DomainRestrictionSet set = service.updateExcludeTag(id, excludeTagId, util);
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.<DomainRestrictionSetDto>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/change-template")
    public Response<DomainRestrictionSetDto> changeTemplateName(@PathVariable("id") Long id,
                                                                @RequestParam(name = "templateName", required = false) String templateName,
                                                                @RequestParam("isPlace") boolean isPlace,
                                                                LithiumTokenUtil tokenUtil) {
        try {
            DomainRestrictionSet set = service.find(id);
            set = service.updateTemplateName(set, templateName, isPlace, tokenUtil);
            return Response.<DomainRestrictionSetDto>builder()
                    .data(convertToDto(set))
                    .status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.<DomainRestrictionSetDto>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    private DomainRestrictionSetDto convertToDto(DomainRestrictionSet set) {
        DomainRestrictionSetDto dto = mapper.convertValue(set, DomainRestrictionSetDto.class);
        String errorMessage = service.getRestrictionErrorMessageTranslation(set);
        dto.setErrorMessage(errorMessage);
        return dto;
    }
}
