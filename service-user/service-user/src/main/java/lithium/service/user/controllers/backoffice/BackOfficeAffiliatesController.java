package lithium.service.user.controllers.backoffice;

import java.util.Map;
import lithium.service.Response;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.objects.LabelValueDTO;
import lithium.service.user.services.AffiliateService;
import lithium.service.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping(path = "/backoffice")
public class BackOfficeAffiliatesController {

  @Autowired AffiliateService affiliateService;
  @Autowired UserService userService;

  /**
   *
   * @param id
   * @return
   */
  @GetMapping("/{domainName}/affiliates/{id}/references")
  public Response<Map<String, String>> getReferences(@PathVariable("id") Long id) {
    User user = userService.findOne(id);

    if( user != null ) {
      return Response.<Map<String, String>>builder().data(affiliateService.requestAffiliatePlayer(user)).build();
    }

    return null;
  }

  @PostMapping("/affiliates/list")
  public Page<LabelValueDTO> getReferences(
      @RequestParam(name = "page" , required = false, defaultValue = "0") Integer page,
      @RequestParam(name = "size" , required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(name = "name", required = false) String name) {

    Pageable pageRequest = PageRequest.of(page, pageSize);
    return affiliateService.findAffiliatesByName(name, pageRequest);
  }
}
