package lithium.service.user.jobs;

import lithium.service.user.data.entities.User;
import lithium.service.user.services.UserService;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "lithium.services.user.guid-update-job.active")
public class GuidUpdateJob {
	@Autowired UserService userService;

	@Scheduled(fixedDelay=60000)
	protected void updatePlayerGuid() {
		log.debug("GuidUpdateJob Runnning.");
		try {
			List<User> nullGuidTop100 = userService.nullGuidTop100();
			nullGuidTop100.forEach(u -> {
				log.debug("Updating: " + u.guid());
				try {
					userService.save(u);
				} catch (Exception e) {
					log.error("Could not update user guid: "+u.guid()+", :"+ ExceptionMessageUtil.allMessages(e));
				}
			});
		} catch (Exception e) {
			log.error("Could not update player guids: "+e.getMessage(), e);
		}
	}
}
