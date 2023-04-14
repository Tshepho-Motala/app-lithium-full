package lithium.service.raf.services;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.xp.client.XPClient;
import lithium.service.xp.client.objects.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalXPService {

    @Autowired
    private LithiumServiceClientFactory services;

    public Level getUserLevel(String userGuid,String domainName) throws LithiumServiceClientFactoryException {
        XPClient xpClient = services.target(XPClient.class, "service-xp", true);
        Response<Level> response = xpClient.getLevel(userGuid,domainName);
        if (response.isSuccessful()) {
            return response.getData();
        }
        return null;
    }
}
