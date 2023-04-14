package lithium.service.casino.provider.iforium.util;

import com.google.common.io.Resources;

import java.io.IOException;

import static com.google.common.base.Charsets.UTF_8;

public class Fixtures {

    public static String fixture(String fixtureLocation) {
        try {
            return Resources.toString(Resources.getResource(fixtureLocation), UTF_8).trim();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to load fixture file", e);
        }
    }

    public static String fixture(String fixtureLocation, Object... formatArgs) {
        return String.format(fixture(fixtureLocation), formatArgs);
    }
}