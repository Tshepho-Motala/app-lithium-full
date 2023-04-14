package lithium.service.casino.provider.iforium.util;

import lithium.service.casino.provider.iforium.exception.UpstreamValidationFailedException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.objects.User;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public final class NullSafetyUtils {

    public static String getUserGuid(LoginEvent loginEvent) {
        return Optional.ofNullable(loginEvent.getUser())
                       .map(User::getGuid)
                       .orElseThrow(() -> new UpstreamValidationFailedException("Can't retrieve userGuid from loginEvent=" + loginEvent));
    }

    public static String getCountryCode(LoginEvent loginEvent) {
        return Optional.ofNullable(loginEvent.getCountryCode())
                       .orElseThrow(
                               () -> new UpstreamValidationFailedException("Can't retrieve countryCode from loginEvent=" + loginEvent));
    }

    public static String getDomainName(LoginEvent loginEvent) {
        return Optional.ofNullable(loginEvent.getDomain())
                       .map(lithium.service.user.client.objects.Domain::getName)
                       .orElseThrow(
                               () -> new UpstreamValidationFailedException("Can't retrieve domain.name from loginEvent=" + loginEvent));
    }

    public static String getCurrency(Domain domain) {
        return Optional.ofNullable(domain.getCurrency())
                       .orElseThrow(() -> new UpstreamValidationFailedException("Can't retrieve currency from domain=" + domain));
    }
}
