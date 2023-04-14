package lithium.service.user.threshold.client;


import lithium.exceptions.Status500InternalServerErrorException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient( name = "service-user-threshold" )
public interface LossLimitVisibilitySystemClient {

  @PostMapping( "/system/notification/v1/send-loss-limit-visibility-notification" )
  void sendLossLimitVisibilityNotification(@RequestParam( required = false ) String playerGuid, @RequestParam( required = false ) String messageType)
  throws Status500InternalServerErrorException;
}
