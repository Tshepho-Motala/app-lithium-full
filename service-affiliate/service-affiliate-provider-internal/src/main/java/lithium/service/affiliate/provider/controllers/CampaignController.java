package lithium.service.affiliate.provider.controllers;

import java.security.Principal;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.client.changelog.ChangeLogService;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.affiliate.provider.data.entities.Campaign;
import lithium.service.affiliate.provider.data.repositories.CampaignRepository;

@RestController
@RequestMapping("/campaigns/{campaignId}")
public class CampaignController {
	
	@Autowired CampaignRepository repo;
	@Autowired ChangeLogService changeLogService;
	
	@GetMapping Response<Campaign> get(@PathVariable Long campaignId) {
		return Response.<Campaign>builder().data(repo.findOne(campaignId)).status(Status.OK).build();
	}
	
	@PutMapping Response<Campaign> put(@PathVariable("campaignId") Campaign target, @RequestBody Campaign source, Principal principal) throws Exception {
		changeLogService.registerChangesWithDomain("campaign", "modify", target.getId(), principal.getName(), null, null, changeLogService.copy(source, target, new String[] {
			"name",
			"archived"
		}), Category.SUPPORT, SubCategory.AFFILIATE, 0, target.getGuid().substring(0, target.getGuid().indexOf('/')));
		return new Response<Campaign>(repo.save(target));
	}

}
