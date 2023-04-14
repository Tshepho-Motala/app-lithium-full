package lithium.service.promo.pr.casino.roxor.controller.system;

import java.util.List;

import lithium.service.promo.client.dto.FieldData;
import lithium.service.promo.client.stream.provider.IPromoProvider;
import lithium.service.promo.pr.casino.roxor.dto.ExtraFieldType;
import lithium.service.promo.pr.casino.roxor.service.FieldDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system/promo/provider")
public class PromoProviderController implements IPromoProvider {

  @Autowired
  private FieldDataService fieldDataService;

  //  @GetMapping("/details/{field}")
  @Override
  public List<FieldData> fieldDetails(@PathVariable String field, @RequestParam("domainName") String domainName) {
    return fieldDataService.getFieldDataForType(domainName, "service-casino-provider-roxor", ExtraFieldType.fromType(field));
  }
}