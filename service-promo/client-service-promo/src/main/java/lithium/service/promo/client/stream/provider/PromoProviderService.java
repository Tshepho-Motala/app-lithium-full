package lithium.service.promo.client.stream.provider;

import lithium.service.promo.client.dto.PromoProviderRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnBean( annotation = EnablePromoProvider.class )
public class PromoProviderService {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  private IPromoProvider promoProvider;

  private PromoProviderService(IPromoProvider promoProvider) {
    this.promoProvider = promoProvider;
  }

  public void registerPromoProvider(PromoProviderRegistration promoProvider) {
    log.info("Registering Promo Provider: " + promoProvider);
    //    channel.channel().send(MessageBuilder.withPayload(promoProvider).build());
    rabbitTemplate
            .convertAndSend("promoproviderqueuev1", "#", promoProvider);
  }
}
