package lithium.service.promo.controllers.backoffice;

import lithium.service.promo.client.dto.FieldData;
import lithium.service.promo.client.objects.ActivityBO;
import lithium.service.promo.client.objects.CategoryBO;
import lithium.service.promo.data.entities.PromoProvider;
import lithium.service.promo.data.projections.PromoActivityProjection;
import lithium.service.promo.data.projections.ProviderCategory;
import lithium.service.promo.services.InternalPromoProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/backoffice/{domainName}/provider")
public class PromoProviderController {

    @Autowired
    private InternalPromoProviderService promoProviderService;

    @GetMapping("/list")
    public List<PromoProvider> getProviderList() {
        return promoProviderService.findAllProviders();
    }

    @GetMapping("/list-by-category/{category}")
    public List<PromoProvider> listProviderByCategory(@PathVariable("category") String category) {
        return promoProviderService.findProvidersInCatetory(category);
    }

    @GetMapping("/list-activity-by-category/{category}")
    public List<String> listActivityByCategory(@PathVariable("category") String category) {
        return promoProviderService.findActivityWithCategory(category);
    }

    @GetMapping("/{providerUrl}/{field}")
    public List<FieldData> fieldDetails(@PathVariable String domainName, @PathVariable String providerUrl, @PathVariable String field) {
        return promoProviderService.getProviderFieldDetails(domainName, providerUrl, field);
    }

    @GetMapping("/categories")
    public List<CategoryBO> getProviderCategories() {
        return promoProviderService.getCategories();
    }
}
