package lithium.service.mail.services;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.objects.placeholders.*;
import lithium.service.mail.data.entities.EmailTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Slf4j
public class PlaceholderService {
    @Autowired
    private LithiumServiceClientFactory servicesFactory;

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[\\w+.]+%");

    public Response<Set<Placeholder>> buildPlaceHolders(EmailTemplate template, String recipientGuid, Long transactionId) {

        try {

            Set<String> placeholderKeys = extractKeys(template.getCurrent().getBody());
            placeholderKeys.addAll(extractKeys(template.getCurrent().getSubject()));

            Set<SourceService> serviceUrls = resolveSourceServices(placeholderKeys);

            Set<Placeholder> completePlaceholders = new HashSet<>();

            for (SourceService plService : serviceUrls) {

                CompletePlaceholdersClient client = servicesFactory.target(CompletePlaceholdersClient.class, plService.serviceUrl(), true);

                if (recipientGuid != null && !recipientGuid.isEmpty()) {
                    Response<Set<Placeholder>> response = client.getPlaceholdersByGuid(recipientGuid);
                    if (response.getStatus().equals(Response.Status.OK)) {
                        completePlaceholders.addAll(response.getData());
                    } else {
                        log.error("Can't get placeholders from:" + plService.serviceUrl() + " for guid:" + recipientGuid);
                        return response;
                    }
                }

                if (transactionId != null && plService.equals(SourceService.SERVICE_CASHIER)) {
                    Response<Set<Placeholder>> response = client.getPlaceholdersByTransactionId(transactionId);
                    if (response.getStatus().equals(Response.Status.OK)) {
                        completePlaceholders.addAll(response.getData());
                    } else {
                        log.error("Can't get placeholders from:" + plService.serviceUrl() + " for transactionId:" + transactionId);
                        return response;
                    }
                }

                if (plService.equals(SourceService.SERVICE_DOMAIN)) {
                    Response<Set<Placeholder>> response = client.getPlaceholdersByDomainName(template.getDomain().getName());
                    if (response.getStatus().equals(Response.Status.OK)) {
                        completePlaceholders.addAll(response.getData());
                    } else {
                        log.error("Can't get placeholders from:" + plService.serviceUrl() + " for domainName:" + template.getDomain().getName());
                        return response;
                    }
                }
            }

            Set<Placeholder> result = buildResultSet(placeholderKeys, completePlaceholders);

            return Response.<Set<Placeholder>>builder().data(result).build();
        } catch (Exception e) {
            log.error("Can't build placeholders because:" + e.getMessage(), e);
            return Response.<Set<Placeholder>>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }
    private Set<Placeholder> buildResultSet(Set<String> placeholderKeys, Set<Placeholder> completePlaceholders) {
        Map<String, Placeholder> completePlaceholdersMap = completePlaceholders.stream()
                .collect(Collectors.toMap(Placeholder::getKey, Function.identity()));
        Set<Placeholder> result = placeholderKeys.stream()
                .map(key -> completePlaceholdersMap.getOrDefault(key, PlaceholderBuilder.createEmptyPlaceholder(key)))
                .collect(Collectors.toSet());
        return result;
    }

    private Set<SourceService> resolveSourceServices(Set<String> placeholderKeys) {
        return placeholderKeys.stream()
                .map(PlaceholderBuilder::fromKey)
                .filter(Objects::nonNull)
                .filter(PlaceholderBuilder::isAutoComplete)
                .distinct()
                .map(PlaceholderBuilder::service)
                .collect(Collectors.toSet());
    }

    private Set<String> extractKeys(String body) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(body);
        Set<String> placeholderKeys = new HashSet<>();
        while (matcher.find()) {
            String key = matcher.group(0);
            placeholderKeys.add(key);
        }
        return placeholderKeys;
    }
}
