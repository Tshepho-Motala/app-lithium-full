package lithium.service.user.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.user.client.objects.User;

@FeignClient(name="service-user")
public interface UserSignupClient {
	@RequestMapping(path = "/signupevents/export")
	public List<User> exportByDateRange(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate);
}