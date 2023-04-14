package lithium.hazelcast;

import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface NamedInstance {
    Class<?> getClazz();

    Environment getEnvironment();

    default String getName() throws UnknownHostException {
        Package sourcePackage = getClazz().getPackage();
        String version = (sourcePackage == null ? null : sourcePackage.getImplementationVersion());
        if ((version == null) || (version.isEmpty())) version = "SNAPSHOT";

        return getEnvironment().getProperty("spring.application.name") + "-" +
                version + "-" + InetAddress.getLocalHost().getHostName();
    }

}
