package lithium.service.xp.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.xp.data.entities.Scheme;
import lithium.service.xp.services.SchemeService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/scheme/{domainName}")
@Slf4j
public class SchemeController {
	@Autowired SchemeService service;
	
	@GetMapping("/get")
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
}
