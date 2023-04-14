package lithium.service.xp.controllers.admin;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.xp.data.entities.Scheme;
import lithium.service.xp.services.SchemeService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@RestController
@RequestMapping("/admin/scheme")
@Slf4j
public class SchemeAdminController {
	@Autowired SchemeService service;
	
	@GetMapping("/table")
	public DataTableResponse<Scheme> table(@RequestParam("domains") List<String> domains, LithiumTokenUtil tokenUtil, DataTableRequest request) {
		Page<Scheme> table = service.findByDomains(domains,
				request.getSearchValue(), request.getPageRequest(), tokenUtil);
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/{domainName}/getActiveScheme")
	public Response<Scheme> getActiveScheme(@PathVariable("domainName") String domainName) {
		Scheme scheme = null;
		try {
			scheme = service.findActiveScheme(domainName);
			return Response.<Scheme>builder().data(scheme).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Scheme>builder().data(scheme).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	 
	@GetMapping("/{id}/get")
	public Response<Scheme> get(@PathVariable("id") Scheme scheme) {
		return Response.<Scheme>builder().data(scheme).status(OK).build();
	}
	
	@PostMapping("/create")
	public Response<Scheme> create(@RequestBody lithium.service.xp.client.objects.Scheme schemePost, Locale locale) {
		Scheme scheme = null;
		try {
			scheme = service.create(schemePost, locale);
			return Response.<Scheme>builder().data(scheme).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Scheme>builder().data(scheme).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/{id}/edit")
	public Response<Scheme> edit(@PathVariable("id") Long id, @RequestBody lithium.service.xp.client.objects.Scheme schemePost) {
		Scheme scheme = null;
		try { 
			scheme = service.edit(id, schemePost);
			return Response.<Scheme>builder().data(scheme).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Scheme>builder().data(scheme).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}
