package lithium.service.user.controllers;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLog;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@Slf4j
@RestController
@RequestMapping("/{domain}/comments/{id}")
public class PlayerCommentController {
    @Autowired ChangeLogService changeLogService;
    @Autowired LithiumTokenUtilService tokenService;

    @PostMapping(value="/add")
    private Response<String> addComment(@PathVariable("id") Long id, @RequestBody String comment, Principal principal, @PathVariable("domain") String domainName) {
        try {
            List<ChangeLogFieldChange> clfc = new ArrayList<>();
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.comment", "comment", id, principal.getName(),
                tokenService.getUtil(principal), comment, null, clfc, Category.ACCOUNT, SubCategory.SUPPORT, 0, domainName);
            return Response.<String>builder().data(comment).status(OK).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.<String>builder().data(comment).status(INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value="/last")
    private Response<ChangeLog> lastComment(@PathVariable Long id) throws Exception {
        Page<ChangeLog> changeLog = changeLogService.listLimitedPaged(
                ChangeLogRequest.builder()
                        .entityRecordId(id)
                        .entities(new String[] { "user.comment" })
                        .types(new String[] { "comment" })
                        .page(0)
                        .pageSize(1)
                        .sortDirection("desc")
                        .sortField("changeDate")
                        .build()
        ).getData();

        return Response.<ChangeLog>builder()
                .data(changeLog.getContent().size() > 0? changeLog.getContent().get(0) : null)
                .status(OK)
                .build();
    }

    @GetMapping(value="/table")
    private DataTableResponse<ChangeLog> comments(@PathVariable Long id, DataTableRequest request) throws Exception {
        Iterator<Order> orders = request.getPageRequest().getSort().iterator();
        Order order = orders.next();
        Page<ChangeLog> changeLogs = changeLogService.listLimitedPaged(
                ChangeLogRequest.builder()
                        .entityRecordId(id)
                        .entities(new String[] { "user.comment" })
                        .types(new String[] { "comment" })
                        .page(request.getPageRequest().getPageNumber())
                        .pageSize(request.getPageRequest().getPageSize())
                        .sortDirection(order.getDirection().name())
                        .sortField(order.getProperty())
                        .searchValue(request.getSearchValue())
                        .build()
        ).getData();
        return new DataTableResponse<>(request, changeLogs);
    }

}
