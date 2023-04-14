package lithium.service.access.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;
import static lithium.service.Response.Status.NOT_FOUND;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import lithium.service.access.client.objects.ListBasic;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.access.data.entities.Domain;
import lithium.service.access.data.entities.List;
import lithium.service.access.data.entities.ListType;
import lithium.service.access.data.repositories.DomainRepository;
import lithium.service.access.data.repositories.ListRepository;
import lithium.service.access.data.repositories.ListTypeRepository;
import lithium.service.access.data.specifications.ListSpecification;
import lithium.service.access.services.DomainService;
import lithium.service.access.services.ExternalDomainService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;

@RestController
@RequestMapping("/lists")
public class ListsController {
    @Autowired
    ExternalDomainService externalDomainService;
    @Autowired
    DomainService domainService;
    @Autowired
    ListTypeRepository listTypeRepository;
    @Autowired
    ListRepository listRepository;
    @Autowired
    ChangeLogService changeLogService;
    @Autowired
    DomainRepository domainRepository;

    @GetMapping(value = "/table")
    public DataTableResponse<List> table(
        @RequestParam("domainNamesCommaSeperated") String domainNamesCommaSeperated,
        DataTableRequest request,
        LithiumTokenUtil tokenUtil
    ) {
        String[] domainNames = domainNamesCommaSeperated.split(",");
        DomainValidationUtil.filterDomainsWithRoles(domainNames, tokenUtil, "ACCESSCONTROL_VIEW", "ACCESSCONTROL_EDIT", "ACCESSCONTROL_ADD",
            "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
        if (domainNames.length > 0) {
            java.util.List<String> domainsList = Arrays.asList(domainNames);
            Specification<List> spec = Specification.where(ListSpecification.domainIn(domainsList));
            if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
                Specification<List> s = Specification.where(ListSpecification.anyContains(request.getSearchValue()));
                spec = (spec == null) ? s : spec.and(s);
            }
            Page<List> lists = listRepository.findAll(spec, request.getPageRequest());
            return new DataTableResponse<>(request, lists);
        } else {
            return new DataTableResponse<>(request, new ArrayList<List>());
        }
    }

    @PostMapping(value = "/create")
    public Response<List> create(
        @RequestBody lithium.service.access.client.objects.ListBasic listBasic,
        LithiumTokenUtil tokenUtil
    ) throws Exception {
      try {
        DomainValidationUtil.validate(listBasic.getDomainName(), "ACCESSCONTROL_ADD", tokenUtil);
        lithium.service.domain.client.objects.Domain externalDomain = externalDomainService.findByName(listBasic.getDomainName());
        Domain domain = domainService.findOrCreate(externalDomain.getName());
        ListType listType = listTypeRepository.findByName(listBasic.getType());
        if (listType == null) throw new Exception("List type not found!");
        List list = listRepository.save(
            List.builder()
                .domain(domain)
                .name(listBasic.getName())
                .description(listBasic.getDescription())
                .listType(listType)
                .enabled(listBasic.isEnabled())
                .build()
        );
        java.util.List<ChangeLogFieldChange> clfc = changeLogService.copy(list, new List(), new String[]{
            "domain", "name", "description", "listType", "enabled"});
        changeLogService.registerChangesWithDomain("list", "create", list.getId(), tokenUtil.guid(), null, null, clfc, Category.ACCESS,
            SubCategory.ACCESS_RULE, 0, list.getDomain().getName());
        return Response.<List>builder().data(list).status(OK).build();
      } catch (Exception e) {
        return Response.<List>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
      }
    }

    @GetMapping(value = "/find/{domainName}")
    public Response<java.util.List<List>> findByDomain(@PathVariable("domainName") String domainName) {
        java.util.List<List> lists = listRepository.findByDomainName(domainName);
        if (lists.isEmpty()) {
            return Response.<java.util.List<List>>builder().status(NOT_FOUND).build();
        }
        return Response.<java.util.List<List>>builder().data(lists).status(OK).build();
    }

    @GetMapping(value = "/findByName/{domainName}")
    public Response<List> findByDomainAndName(
            @PathVariable("domainName") String domainName,
            @RequestParam("listName") String listName
    ) {
        List list = listRepository.findByDomainNameAndName(domainName, listName);
        if (list == null) {
            return Response.<List>builder().status(NOT_FOUND).build();
        }
        return Response.<List>builder().data(list).status(OK).build();
    }

    @GetMapping(value = "/find/{domainName}/{listTypeName}/{enabled}")
    public Response<java.util.List<List>> findByDomainNameAndListTypeName(
            @PathVariable("domainName") String domainName,
            @PathVariable("listTypeName") String listTypeName,
            @PathVariable("enabled") Boolean enabled
    ) {
        java.util.List<List> lists = listRepository.findByDomainNameAndListTypeNameAndEnabled(domainName, listTypeName, enabled);
        if (lists.isEmpty()) {
            return Response.<java.util.List<List>>builder().status(NOT_FOUND).build();
        }
        return Response.<java.util.List<List>>builder().data(lists).status(OK).build();
    }
}
