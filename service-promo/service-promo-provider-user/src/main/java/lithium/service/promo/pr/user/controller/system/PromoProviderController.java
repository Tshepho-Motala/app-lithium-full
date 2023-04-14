package lithium.service.promo.pr.user.controller.system;

import java.util.List;

import lithium.service.promo.client.dto.FieldData;
import lithium.service.promo.client.stream.provider.IPromoProvider;
import lithium.service.promo.pr.user.dto.ExtraFieldType;
import lithium.service.promo.pr.user.service.FieldDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/promo/provider")
public class PromoProviderController implements IPromoProvider {

  private final FieldDataService fieldDataService;

  @Override
  public List<FieldData> fieldDetails(@PathVariable String field, @RequestParam("domainName") String domainName) {
    log.info("Controller reached with : "+field);
    return fieldDataService.getFieldDataForType(ExtraFieldType.fromType(field));
  }
}