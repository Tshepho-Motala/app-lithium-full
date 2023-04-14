package lithium.service.user.threshold.controllers.system;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SecurityRequirement( name = "LithiumSystemToken" )
@RequestMapping( "/system/notification/v1" )
public interface SystemLossLimitVisibility {

  @Operation( summary = "Send Loss Limit Visibility notification to player inbox.", tags = {"Loss Limit Visibility"} )
  @PostMapping( "/send-loss-limit-visibility-notification" )
  void sendLossLimitVisibilityNotification(@RequestParam( required = false ) String playerGuid, @RequestParam( required = false ) String messageType);
}
