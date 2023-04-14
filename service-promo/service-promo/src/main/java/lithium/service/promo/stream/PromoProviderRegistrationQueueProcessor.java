package lithium.service.promo.stream;

import lithium.service.promo.client.dto.PromoProviderRegistration;
import lithium.service.promo.services.PromoProviderRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding( {PromoProviderQueueSinkV1.class} )
public class PromoProviderRegistrationQueueProcessor {

  @Autowired
  private PromoProviderRegistrationService promoProviderRegistrationService;

  @StreamListener( PromoProviderQueueSinkV1.INPUT )
  public void handlePromoProviderRegistrationV1(PromoProviderRegistration promoProviderRegistration) throws Exception {
    log.debug("Received a PromoProviderRegistration request from the v1 queue for processing. Registering new provider: " + promoProviderRegistration);

    promoProviderRegistrationService.register(promoProviderRegistration);
  }
}
