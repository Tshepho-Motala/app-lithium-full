package lithium.service.affiliate.provider.controllers;

import java.security.Principal;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.affiliate.provider.data.entities.Campaign;
import lithium.service.affiliate.provider.data.repositories.CampaignRepository;
import lithium.service.affiliate.provider.data.specifications.CampaignSpecifications;
import lithium.service.affiliate.provider.service.AffiliateService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;

@RestController
@RequestMapping("/campaigns")
public class CampaignsController {
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	private CampaignRepository repository;
	
	@Autowired 
	private AffiliateService affiliateService;
	
	@GetMapping
	public DataTableResponse<Campaign> list(
			@RequestParam(required=false) String brandMachineName,
			@RequestParam(required=false) Integer adType,
			@RequestParam(required=false) Boolean archived,
			DataTableRequest request, Principal principal
			
		) {
		
		if (archived == null) archived = Boolean.FALSE;
		
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
		
		//TODO: Get the affiliate guid from the user guid
		String affiliateId = "unknown_affiliate";
		Specification<Campaign> spec =
				Specification.where(CampaignSpecifications.deleted(false))
				.and(CampaignSpecifications.archived(archived))
				.and(CampaignSpecifications.findByAffiliate(affiliateId));
		
		if (brandMachineName != null) spec = spec.and(CampaignSpecifications.findByBrand(brandMachineName));
		
		Page<Campaign> list = repository.findAll(spec, request.getPageRequest());

		if (request.getSearchValue() != null) {
			StringTokenizer st = new StringTokenizer(request.getSearchValue());
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				Specification<Campaign> newSpec = Specification.where(CampaignSpecifications.any(token));
				spec = (spec == null)? newSpec: spec.and(newSpec);
			}
		}
		Page<Campaign> filteredList = repository.findAll(spec, request.getPageRequest());
		
		return new DataTableResponse<Campaign>(request, list, filteredList);

	}
	
	@PostMapping
	public Response<Campaign> create(@RequestBody Campaign campaign, Principal principal) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
	//	campaign.getCurrent().setAffiliate(affiliateService.findOrCreate(util.guid()));
		return Response.<Campaign>builder().data(repository.save(campaign)).build();
	}

}
