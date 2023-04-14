package lithium.service.promo.data.projections;

import org.springframework.beans.factory.annotation.Value;

public interface ProviderCategory {
    @Value("#{target.category}")
    public String getName();
}
