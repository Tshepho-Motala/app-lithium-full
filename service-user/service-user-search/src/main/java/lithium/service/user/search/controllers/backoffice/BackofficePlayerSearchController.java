package lithium.service.user.search.controllers.backoffice;

import lithium.service.client.datatable.DataTablePostRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.data.entities.User;
import lithium.service.user.search.services.user.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/players")
public class BackofficePlayerSearchController {

  @Autowired
  @Qualifier(value = "user.UserService")
  UserService userService;

  @RequestMapping("/table")
  public DataTableResponse<User> table(
      @RequestParam("order[0][column]") String orderColumn,
      @RequestParam("order[0][dir]") String orderDirection,
      DataTablePostRequest request,
      LithiumTokenUtil tokenUtil
  ) throws Status550ServiceDomainClientException {
    Sort sort = request.getPageRequest().getSort();
    if (orderColumn.equals("8"))
      sort = Sort.by(Sort.Direction.fromString(orderDirection),"lastName");
    request.setPageRequest(PageRequest.of(request.getPageRequest().getPageNumber(),
        Math.min(request.getPageRequest().getPageSize(), 100),
        sort));

    log.debug("Players table request " + request + "");

    Page<User> users = userService.buildUserTable(request, tokenUtil);
    log.debug("Page<User> users : " + users);
    return new DataTableResponse<>(request, users);
  }
}
