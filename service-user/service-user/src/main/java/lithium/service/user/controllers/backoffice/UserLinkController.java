package lithium.service.user.controllers.backoffice;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lithium.exceptions.Status469InvalidInputException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserLink;
import lithium.service.user.data.entities.UserLinkType;
import lithium.service.user.exceptions.Status100InvalidInputDataException;
import lithium.service.user.services.UserLinkService;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserLinkController {

  private UserLinkService userLinkService;
  private UserService userService;

  @Autowired
  public UserLinkController(UserLinkService userLinkService, UserService userService) {
    this.userLinkService = userLinkService;
    this.userService = userService;
  }

  @RequestMapping("/backoffice/user-link/add-user-link")
  @ResponseBody
  public List<UserLink> addUserLink(
      @RequestParam("primaryUserGuid") String primaryUserGuid,
      @RequestParam("secondaryUserGuid") String secondaryUserGuid,
      @RequestParam("userLinkTypeCode") String userLinkTypeCode,
      @RequestParam("linkNote") String linkNote,
      LithiumTokenUtil util)
      throws Status100InvalidInputDataException {
    List<UserLink> userLinks = new ArrayList<>();
    userLinks.add(userLinkService.addUserLink(primaryUserGuid, secondaryUserGuid, userLinkTypeCode, linkNote, util));
    userLinks.add(userLinkService.addUserLink(secondaryUserGuid, primaryUserGuid, userLinkTypeCode, linkNote, util));
    return userLinks;
  }

  @RequestMapping("/backoffice/user-link/update-user-link")
  @ResponseBody
  public UserLink updateUserLink(
      @RequestParam("userLinkId") Long userLinkId,
      @RequestParam("linkNote") String linkNote,
      @RequestParam(name="deleted", required = false) Boolean deleted, Principal principal)
      throws Status100InvalidInputDataException {
    return userLinkService.updateUserLinkNote(userLinkId, linkNote, deleted, principal);
  }

  @RequestMapping("/backoffice/user-link/type-list")
  @ResponseBody
  public ArrayList<UserLinkType> getUserLinkTypes() {
    return userLinkService.getUserLinkTypeList();
  }

  @RequestMapping("/backoffice/user-link/list-by-user")
  @ResponseBody
  public ArrayList<UserLink> findUserLinksByUser(@RequestParam("userGuid") String userGuid)
      throws Status100InvalidInputDataException {
    //TODO: There might be a need to also lookup user in a secondary user link capacity in future, now method, also both
    return userLinkService.findUserLinksByUser(getFromGuid(userGuid));
  }

  private User getFromGuid(String userGuid) {
    return userService.findFromGuid(userGuid);
  }

  @RequestMapping("/backoffice/user-link/table")
  public DataTableResponse<UserLink> table(
      @RequestParam(name = "domainNames", required = false) String[] domainNames,
      @RequestParam(name = "ecosystemName", required = false) String ecosystemName,
      DataTableRequest request,
      LithiumTokenUtil tokenUtil
  ) throws
      Status469InvalidInputException,
      Status550ServiceDomainClientException {
    return userLinkService.findUserLinks(ecosystemName, domainNames, request, tokenUtil);
  }

  @RequestMapping(value = "/backoffice/user-link/{userLinkId}", method = RequestMethod.PUT)
  @ResponseBody
  public UserLink updateUserLink(
      @PathVariable("userLinkId") Long userLinkId,
      @RequestParam(value = "linkNote") String linkNote,
      @RequestParam(name="linkTypeCode") String linkTypeCode, Principal principal)
      throws Status100InvalidInputDataException {
    return userLinkService.updateUserLink(userLinkId, linkTypeCode, linkNote, principal, true);
  }
}
