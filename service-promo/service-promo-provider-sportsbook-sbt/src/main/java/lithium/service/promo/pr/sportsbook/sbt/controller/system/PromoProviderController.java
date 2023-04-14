package lithium.service.promo.pr.sportsbook.sbt.controller.system;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lithium.service.promo.client.dto.FieldData;
import lithium.service.promo.client.stream.provider.IPromoProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
//@RequestMapping("/system/promo/provider")
public class PromoProviderController implements IPromoProvider {

  @Override
  //  @GetMapping("/details/{field}")
  public List<FieldData> fieldDetails(@PathVariable String field, @RequestParam("domainName") String domainName) {
    log.debug("Controller reached with : "+field);
    return Arrays.asList(FieldData.builder()
            .value(field)
            .label(field)
            .build());
  }
}