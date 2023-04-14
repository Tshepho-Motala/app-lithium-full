package service.casino.provider.cataboom.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import service.casino.provider.cataboom.entities.Campaign;
import service.casino.provider.cataboom.repositories.CampaignRepository;
import service.casino.provider.cataboom.specifications.CampaignSpecifications;

@Slf4j
@RestController
@RequestMapping("/cataboomcampaigns/{domainName}")
public class CampaignController {
	@Autowired
	CampaignRepository repository;
	@Autowired
	private LithiumServiceClientFactory services;
	
	@GetMapping("/table")
	public DataTableResponse<Campaign> table(
		@PathVariable("domainName") String domainName,
		LithiumTokenUtil tokenUtil,
		DataTableRequest request
	) {
		Page<Campaign> table = findByDomain(domainName,
				request.getSearchValue(), request.getPageRequest(), tokenUtil);
		return new DataTableResponse<>(request, table);
	}
	
	public Page<Campaign> findByDomain(String domainName, String searchValue, Pageable pageable, LithiumTokenUtil tokenUtil) {
		
		Specification<Campaign> spec = Specification.where(CampaignSpecifications.domain(domainName));

		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<Campaign> s = Specification.where(CampaignSpecifications.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<Campaign> result = repository.findAll(spec, pageable);
		return result;
	}

	@RequestMapping("/{id}")
	public Response<Campaign> findCampaign(@PathVariable("id") Campaign campaign) {
		return Response.<Campaign>builder().status(OK).data(campaign).build();
	}

	@DeleteMapping("/delete/{id}")
	public Response<Campaign> delete(@PathVariable("id") Campaign campaign) {
		
		try {
			repository.delete(campaign);
			return Response.<Campaign>builder().status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Campaign>builder().status(INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/createCampaign")
	public Response<Campaign> update(@RequestBody @Valid Campaign campaign,
			@PathVariable("domainName") String domainName) {
		
		campaign.setDomainName(domainName);
		repository.save(campaign);
		return Response.<Campaign>builder().data(campaign).build();
	}

	@PostMapping("/toggleEnable/{id}")
	public Response<Campaign> toggleEnabled(@PathVariable("domainName") String domainName,
			@PathVariable("id") Campaign campaign) {
		
		try {
			campaign = toggleEnable(campaign);
			return Response.<Campaign>builder().data(campaign).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Campaign>builder().data(campaign).status(INTERNAL_SERVER_ERROR).build();
		}
	}

	private ProviderClient getProviderService() {
		ProviderClient cl = null;
		try {
			cl = services.target(ProviderClient.class, "service-domain", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting provider properties", e);
		}
		return cl;
	}

	public Campaign toggleEnable(Campaign campaign) {
		campaign.setEnabled(!campaign.getEnabled());
		return repository.save(campaign);
	}

}
