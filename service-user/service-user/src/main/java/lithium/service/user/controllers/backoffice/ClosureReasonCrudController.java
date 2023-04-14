package lithium.service.user.controllers.backoffice;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.objects.ClosureReasonBasic;
import lithium.service.user.data.entities.ClosureReason;
import lithium.service.user.services.ClosureReasonService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(path = "/backoffice/{domainName}/closure-reasons-crud")
public class ClosureReasonCrudController {
    @Autowired
    ClosureReasonService closureReasonService;
    @Autowired
    ChangeLogService changeLogService;

    @GetMapping("/table")
    public DataTableResponse<ClosureReason> table(@PathVariable("domainName") String domainName, DataTableRequest request, @RequestParam("hideDeleted") Boolean hideDeleted) {
        Page<ClosureReason> table = closureReasonService.table(domainName, request.getSearchValue(), request.getPageRequest(), hideDeleted);
        return new DataTableResponse<>(request, table);
    }

    @DeleteMapping("/delete/{id}")
    public Response<Void> delete(
            @PathVariable("domainName") String domainName,
            @PathVariable("id") Long closureReasonId,
            @RequestParam("comment") String comment,
            LithiumTokenUtil lithiumTokenUtil
    ) {
        try {
            closureReasonService.delete(closureReasonId, comment, lithiumTokenUtil.guid());

            return Response.<Void>builder().status(Status.OK).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.<Void>builder().status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add")
    public Response<ClosureReason> add(@PathVariable("domainName") String domainName,
                                       @RequestBody ClosureReasonBasic closureReasonBasic, LithiumTokenUtil lithiumTokenUtil) throws Exception {
        ClosureReason closureReason = closureReasonService.add(closureReasonBasic, domainName, lithiumTokenUtil.guid());
        return Response.<ClosureReason>builder().data(closureReason).status(Status.OK).build();
    }

    @PostMapping("/save")
    public Response<ClosureReason> save(@RequestBody ClosureReasonBasic closureReasonBasic, LithiumTokenUtil lithiumTokenUtil) {
        ClosureReason closureReason = closureReasonService.save(closureReasonBasic, lithiumTokenUtil.guid());
        return Response.<ClosureReason>builder().data(closureReason).status(Status.OK).build();
    }

    @GetMapping("/findById")
    public Response<ClosureReason> findById(
            @PathVariable("domainName") String domainName,
            @RequestParam("id") ClosureReason providerAuthClient
    ) {
        return Response.<ClosureReason>builder().data(providerAuthClient).status(Status.OK).build();
    }

    @GetMapping(value = "/{id}/changelogs")
    private @ResponseBody
    Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
      return changeLogService.listLimited(ChangeLogRequest.builder()
              .entityRecordId(id)
              .entities(new String[]{"closureReason"})
              .page(p)
              .build()
      );
    }

}
