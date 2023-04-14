package lithium.service.user.client;

import lithium.service.Response;
import lithium.service.user.client.objects.UserCategory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "service-user")
public interface UserCategoryClient {
	@RequestMapping("/system/user-categories")
	public List<UserCategory> getUserCategoriesOfUser(@RequestParam(name = "userId") Long userId);

	@RequestMapping("/system/user-categories/{domainName}")
	public List<UserCategory> getDomainUserCategories(@PathVariable(name="domainName") String domainName);
}
