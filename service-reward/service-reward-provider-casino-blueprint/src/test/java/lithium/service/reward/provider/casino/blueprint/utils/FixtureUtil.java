package lithium.service.reward.provider.casino.blueprint.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.stream.Stream;

public class FixtureUtil {

    public static String fixture(String fixturePath)  {
        String path = null;
        try {
            path = MessageFormat.format("fixtures/{0}", fixturePath);
            ClassPathResource resource = new ClassPathResource(path);
            return new String(Files.readAllBytes(resource.getFile().toPath()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(MessageFormat.format("Fixture file {0} could not be processed, does the the file exist?, message:{1}", path, e.getMessage()));
        }
    }
}
