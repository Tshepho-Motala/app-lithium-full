package lithium.service.access.services;

import lithium.service.access.controllers.ListController;
import lithium.service.access.data.entities.AccessControlList;
import lithium.service.access.data.entities.Value;
import lithium.service.access.data.repositories.AccessControlListRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.BasicUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InternalIpListService {
    @Autowired
    private AccessControlListRepository accessControlListRepository;
    @Autowired
    private ListController listValueService;

    @Transactional
    @Scheduled(fixedRateString = "${lithium.service.access.check-expired-ip-interval-in-milliseconds:60000}",
            initialDelayString = "${lithium.service.access.check-expired-ip-delay-in-milliseconds:60000}")
    public void cleanExpiredIp() throws Exception {
        for (AccessControlList acl : accessControlListRepository.findAllByIpResetTimeNotNull()) {
            Integer ipResetTime = acl.getIpResetTime();
            lithium.service.access.data.entities.List list = acl.getList();
            List<Value> expiredIps = list.getValues().stream()
                    .filter(value -> value.getDateAdded().plusSeconds(ipResetTime).isBeforeNow())
                    .collect(Collectors.toList());
            if (!expiredIps.isEmpty()) {
                log.info("Clean expired ips: " + expiredIps.stream().map(Value::getData).collect(Collectors.toList()) +
                        " from " + list.getName());
                for (Value expiredIp : expiredIps) {
                    listValueService.removeListValue(list, expiredIp.getId(), new BasicUserPrincipal("system"));
                }
            }
        }
    }
}
