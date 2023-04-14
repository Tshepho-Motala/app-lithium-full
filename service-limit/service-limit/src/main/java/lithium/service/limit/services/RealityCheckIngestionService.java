package lithium.service.limit.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.limit.client.objects.RealityCheckMigrationDetails;
import lithium.service.limit.data.entities.User;
import lithium.service.limit.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealityCheckIngestionService {


    private final RealityCheckService realityCheckService;

    private final UserRepository userRepository;

    @TimeThisMethod
    public void initiateRealityCheck(RealityCheckMigrationDetails details) throws  Exception{
        SW.start("Reality Checks" + details.getCustomerID());

            User user = userRepository.findOrCreateByGuid(details.getPlayerGuid(), User :: new);
            realityCheckService.findOrCreateRealityCheck(
                    user.getGuid(),
                    details.getRealityCheckInterval()
            );
        SW.stop();
    }
}
