package lithium.service.casino.provider.iforium.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public final class WebUtils {

    private static final Pattern WHITESPACE_PATTERN =  Pattern.compile("\\s");

    public static String getFirstIpFromXForwardedFor(String xForwardedFor) {
        return WHITESPACE_PATTERN.matcher(xForwardedFor)
                                 .replaceAll("")
                                 .split(",")[0];
    }
}
