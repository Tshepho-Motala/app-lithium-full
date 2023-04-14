package lithium.service.promo.services;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.promo.client.dto.FieldData;
import lithium.service.promo.client.objects.CategoryBO;
import lithium.service.promo.client.stream.provider.IPromoProvider;
import lithium.service.promo.data.entities.PromoProvider;
import lithium.service.promo.data.projections.PromoActivityProjection;
import lithium.service.promo.data.repositories.ActivityRepository;
import lithium.service.promo.data.repositories.CategoryRepository;
import lithium.service.promo.data.repositories.PromoProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternalPromoProviderService {

    private final PromoProviderRepository promoProviderRepository;

    private final CategoryRepository categoryRepository;

    private final ActivityRepository activityRepository;

    private final LithiumServiceClientFactory lithiumServiceClientFactory;

    public List<CategoryBO> getCategories() {
        return categoryRepository.findAll().stream().map(c -> CategoryBO.builder()
                .name(c.getName())
                .id(c.getId())
                .build())
                .toList();
    }

    public List<PromoProvider> findProvidersInCatetory(String category) {
        return promoProviderRepository.findByCategoryName(category);
    }

    public List<String> findActivityWithCategory(String category) {
        return activityRepository.findByPromoProviderCategoryName(category)
                .stream()
                .map(PromoActivityProjection::getName)
                .distinct()
                .toList();
    }

    public List<PromoProvider> findAllProviders() {
        return (List<PromoProvider>) promoProviderRepository.findAll();
    }

    public List<FieldData> getProviderFieldDetails(String domainName, String providerUrl, String field) {
        return getClientForProvider(providerUrl).fieldDetails(field, domainName);
    }

    public IPromoProvider getClientForProvider(String providerUrl) {
        IPromoProvider client = null;
        try {
            client = lithiumServiceClientFactory.target(IPromoProvider.class, providerUrl, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(String.format("Failed to initialise promo provider client for provider %s", providerUrl), e);
        }

        return client;
    }
}
