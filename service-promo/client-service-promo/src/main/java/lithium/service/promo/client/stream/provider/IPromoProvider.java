package lithium.service.promo.client.stream.provider;

import java.util.List;
import lithium.service.promo.client.dto.FieldData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(path = "/system/promo/provider")
public interface IPromoProvider {
  @RequestMapping(value = "/details/{field}", method = RequestMethod.GET)
  List<FieldData> fieldDetails(@PathVariable("field") String field, @RequestParam("domainName") String domainName);
}
