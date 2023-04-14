package lithium.service.limit.controllers.backoffice;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.LimitSystemAccess;
import lithium.service.limit.data.objects.LimitSystemAccessUpdate;
import lithium.service.limit.data.repositories.DomainRepository;
import lithium.service.limit.data.repositories.LimitsSystemAccessRepository;
import lithium.service.limit.data.repositories.VerificationStatusRepository;
import lithium.service.limit.services.LimitSystemAccessService;
import lithium.service.limit.services.PlayerLimitService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/backoffice/{domainName}/domain-restrictions")
public class BackofficeLimitSystemAccessController {

    private LimitsSystemAccessRepository limitsSystemAccessRepository;
    private VerificationStatusRepository verificationStatusRepository;
    private DomainRepository domainRepository;
    private final ChangeLogService changeLogService;
    private LimitSystemAccessService limitSystemAccessService;

    @GetMapping("/list-limits")
    public DataTableResponse<LimitSystemAccess> findLimitsByDomainName(@RequestParam(name="domain", required=false) String domainName) throws Status500LimitInternalSystemClientException {
        log.debug("Listing all system access limits by domain name = "+domainName);
        Iterable<LimitSystemAccess> limits ;
        try {
            limits = limitSystemAccessService.getListLimits(domainRepository.findOrCreateByName(domainName, Domain::new));
        } catch (Exception e) {
            log.error("Exception durind getting limit access list");
            throw new Status500LimitInternalSystemClientException(e);
        }
        return new DataTableResponse<>(new DataTableRequest(), (List<LimitSystemAccess>) limits);
    }

    @PostMapping("/save-limit-system-access")
    public Response<LimitSystemAccess> saveLimitSystemAccess(@RequestBody LimitSystemAccessUpdate limit, Principal principal) throws Exception {
        LimitSystemAccess oldlimitSystemAccess = limitsSystemAccessRepository
                .findByDomainNameAndVerificationStatus(
                        limit.getDomainName(),
                        verificationStatusRepository.findOne(limit.getVerificationId()));
        Domain domain = domainRepository.findByName(limit.getDomainName());
        LimitSystemAccess newlimitSystemAccess = new LimitSystemAccess(
                oldlimitSystemAccess.getId(),
                oldlimitSystemAccess.getDomainName(),
                oldlimitSystemAccess.getVerificationStatus(),
                limit.isLogin(),
                limit.isDeposit(),
                limit.isWithdraw(),
                limit.isBetPlacement(),
                limit.isCasino());

        try {
            String additionalInfo = oldlimitSystemAccess.getVerificationStatus().getCode();
            List<ChangeLogFieldChange> fieldChanges = changeLogService.copy(newlimitSystemAccess, oldlimitSystemAccess, new String[] {"login", "deposit", "withdraw","betPlacement","casino", "verificationStatus.code"});
            changeLogService.registerChangesWithDomain("limit.access", "edit", domain.getId(), principal.getName(), "Edited Verification Code: "+ oldlimitSystemAccess.getVerificationStatus().getCode() + System.lineSeparator() +limit.getComment(), null,
                    fieldChanges, Category.FINANCE, SubCategory.FINANCE, 0, limit.getDomainName());

        } catch (Exception ex) {
            log.error("Exception durind save limit access changes:" + ex);
            return Response.<LimitSystemAccess>builder().status(INTERNAL_SERVER_ERROR).build();
        }
        return Response.<LimitSystemAccess>builder().status(OK).data(limitsSystemAccessRepository.save(newlimitSystemAccess)).build();
    }

    @GetMapping("/changelogs")
    public @ResponseBody Response<ChangeLogs> changeLogs(
            @PathVariable("domainName") String domainName,
            @RequestParam int p
    ) throws Exception {
        Domain domain = domainRepository.findByName(domainName);
        return changeLogService.listLimited(
                ChangeLogRequest.builder()
                        .entityRecordId(domain.getId())
                        .entities(new String[] { "limit.access" })
                        .page(p)
                        .build()
        );
    }
}
