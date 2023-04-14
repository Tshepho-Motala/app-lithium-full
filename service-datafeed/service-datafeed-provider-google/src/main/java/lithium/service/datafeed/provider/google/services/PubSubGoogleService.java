package lithium.service.datafeed.provider.google.services;

import java.io.IOException;

public interface PubSubGoogleService {
    void sendMessage(String message, String domainName) throws IOException, InterruptedException;
}
