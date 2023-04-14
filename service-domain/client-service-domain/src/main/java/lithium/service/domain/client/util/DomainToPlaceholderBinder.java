package lithium.service.domain.client.util;

import lithium.service.client.objects.placeholders.EntityToPlaceholderBinder;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.objects.Domain;

import java.util.HashSet;
import java.util.Set;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOMAIN_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOMAIN_SUPPORT_EMAIL;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOMAIN_URL;


public class DomainToPlaceholderBinder implements EntityToPlaceholderBinder {
    private Domain domain;

    public DomainToPlaceholderBinder(Domain domain) {
        this.domain = domain;
    }

    @Override
    public Set<Placeholder> completePlaceholders() {
        Set<Placeholder> placeholders = new HashSet<Placeholder>();
        placeholders.add(DOMAIN_NAME.from(domain.getDisplayName()));
        placeholders.add(DOMAIN_URL.from(domain.getUrl()));
        placeholders.add(DOMAIN_SUPPORT_EMAIL.from(domain.getSupportEmail()));
        return placeholders;
    }
}
