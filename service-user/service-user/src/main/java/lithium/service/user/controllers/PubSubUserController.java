package lithium.service.user.controllers;

import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.services.PubSubUserService;
import lithium.service.user.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/system/user/pub-sub/")
public class PubSubUserController {

  private final PubSubUserService pubSubUserService;
  private final UserService userService;

  @PostMapping(value = "/push")
  public ResponseEntity<String> pushToPubSub(@RequestParam(value = "guid") String guid, Principal principal)
      throws Status500LimitInternalSystemClientException, Status550ServiceDomainClientException {
    pubSubUserService.buildAndSendPubSubAccountChange(userService.findFromGuid(guid), principal, PubSubEventType.ACCOUNT_UPDATE);
    return ResponseEntity.ok("");
  }
}
