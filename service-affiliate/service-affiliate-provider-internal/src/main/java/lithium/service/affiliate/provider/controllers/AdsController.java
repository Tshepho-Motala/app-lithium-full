package lithium.service.affiliate.provider.controllers;

import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.affiliate.provider.data.entities.Ad;
import lithium.service.affiliate.provider.data.repositories.AdRepository;
import lithium.service.affiliate.provider.data.specifications.AdSpecifications;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;

@RestController
@RequestMapping("/ads")
public class AdsController {
	
	@Autowired AdRepository repository;
	
	@RequestMapping
	public DataTableResponse<Ad> list(
			@RequestParam(required=false) String brandMachineName,
			@RequestParam(required=false) Integer adType,
			DataTableRequest request 
		) {
		
		Specification<Ad> spec = null;
		
		if (brandMachineName != null) spec = Specification.where(AdSpecifications.findByBrandMachineName(brandMachineName));
		if (adType != null)  {
			Specification<Ad> newSpec = Specification.where(AdSpecifications.findByType(adType));
			spec = (spec == null)? newSpec: spec.and(newSpec);
		}
		
		Page<Ad> list = (spec == null)? repository.findAll(request.getPageRequest()) : repository.findAll(spec, request.getPageRequest());

		if (request.getSearchValue() != null) {
			StringTokenizer st = new StringTokenizer(request.getSearchValue());
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				Specification<Ad> newSpec = Specification.where(
						AdSpecifications.findByAny(token));
				spec = (spec == null)? newSpec: spec.and(newSpec);
			}
		}
		Page<Ad> filteredList = (spec == null)? list: repository.findAll(spec, request.getPageRequest());
		
		return new DataTableResponse<Ad>(request, list, filteredList);

	}
	
}
