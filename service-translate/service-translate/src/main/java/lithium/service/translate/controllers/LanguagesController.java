package lithium.service.translate.controllers;

import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.service.translate.data.specifications.LanguageSpecification;

@RestController
@RequestMapping("/apiv1/languages")
public class LanguagesController {

	@Autowired LanguageRepository repository;
	
	@RequestMapping("/enabled")
	public List<Language> enabled() {
		return repository.findByEnabled(true); 
	}

	@RequestMapping("/all")
	public Iterable<Language> all() {
		return repository.findAll(Sort.by(new Sort.Order(Direction.ASC, "description")));
	}

	@RequestMapping("/list")
	public DataTableResponse<Language> list(DataTableRequest request, @RequestParam(name="enabled", required=false) Boolean enabled) {
		
		Specification<Language> spec = null;
				
		if (enabled != null) spec = Specification.where(LanguageSpecification.enabled(enabled));
		
		Page<Language> list = (spec == null)? repository.findAll(request.getPageRequest()) : repository.findAll(spec, request.getPageRequest());

		StringTokenizer st = new StringTokenizer(request.getSearchValue());
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			Specification<Language> newSpec = Specification.where(
					LanguageSpecification.anyContains(token));
			spec = (spec == null)? newSpec: spec.and(newSpec);
		}
		Page<Language> filteredList = (spec == null)? list: repository.findAll(spec, request.getPageRequest());
		
		return new DataTableResponse<Language>(request, list, filteredList);
	}

}
