package lithium.service.affiliate.provider.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.affiliate.provider.data.entities.Ad;
import lithium.service.affiliate.provider.data.entities.Campaign;
import lithium.service.affiliate.provider.data.entities.CampaignAd;
import lithium.service.affiliate.provider.data.repositories.CampaignAdRepository;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;

@RestController
@RequestMapping("/campaigns/{campaignId}/ads")
public class CampaignAdController {
	
	@Autowired CampaignAdRepository repo;
	
	@GetMapping DataTableResponse<CampaignAd> get(@PathVariable("campaignId") Campaign c, DataTableRequest request) {		
		return new DataTableResponse<>(request, repo.findByCampaignId(c.getId(), request.getPageRequest()));
	}
	
	@PostMapping Response<CampaignAd> post(@PathVariable("campaignId") Campaign c, @RequestBody Ad ad) {
		return Response.<CampaignAd>builder().data(repo.save(CampaignAd.builder().ad(ad).campaign(c).build())).status(Status.OK).build();
	}

}
