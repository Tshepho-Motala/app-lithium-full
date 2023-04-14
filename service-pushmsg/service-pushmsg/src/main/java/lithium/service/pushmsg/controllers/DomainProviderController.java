package lithium.service.pushmsg.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.pushmsg.data.entities.DomainProvider;
import lithium.service.pushmsg.data.entities.DomainProviderProperty;
import lithium.service.pushmsg.services.DomainProviderService;
import lithium.service.pushmsg.client.objects.DomainProviderBasic;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/domainProvider")
@Slf4j
public class DomainProviderController {
	@Autowired DomainProviderService domainProviderService;
	
	@GetMapping("/domain/{domainName}")
	private Response<List<DomainProvider>> domainProviders(@PathVariable("domainName") String domainName) {
		return Response.<List<DomainProvider>>builder().data(domainProviderService.findAll(domainName)).status(Status.OK).build();
	}
	
	@PostMapping("/addDomainProvider")
	private Response<DomainProvider> domainProviderAdd(@RequestBody DomainProviderBasic domainProviderBasic) {
		DomainProvider domainProvider = domainProviderService.addDomainProvider(domainProviderBasic.getDomainName(), domainProviderBasic.getDescription(), domainProviderBasic.getProviderId());
		return Response.<DomainProvider>builder().data(domainProvider).status(Status.OK).build();
	}
	
	@PutMapping("/{id}/props")
	public Response<?> updateProperties(
		@PathVariable("id") DomainProvider domainProvider,
		@RequestBody List<DomainProviderProperty> domainProviderProperties
	) throws Exception {
		log.info("Updating DomainProviderProperties : "+domainProviderProperties);
		
		return Response.<List<DomainProviderProperty>>builder()
			.data(domainProviderService.saveProperties(domainProvider, domainProviderProperties))
			.status(Status.OK)
			.build();
	}
	
	@GetMapping("/{id}/props")
	public Response<?> propertiesWithDefaults(@PathVariable("id") Long domainProviderId) {
		return Response.<List<DomainProviderProperty>>builder()
			.data(domainProviderService.propertiesWithDefaults(domainProviderId))
			.status(Status.OK)
			.build();
	}
	
	@GetMapping("/{id}/props/nodef")
	public Response<?> properties(@PathVariable("id") Long domainProviderId) {
		return Response.<List<DomainProviderProperty>>builder()
			.data(domainProviderService.propertiesNoDefaults(domainProviderId))
			.status(Status.OK)
			.build();
	}
	
	@DeleteMapping("/{domainProviderId}/prop/{domainProviderPropertyId}")
	public Response<?> deleteProperty(
		@PathVariable("domainProviderId") Long domainProviderId,
		@PathVariable("domainProviderPropertyId") DomainProviderProperty domainProviderProperty
	) {
		return Response.<DomainProviderProperty>builder()
			.data(domainProviderService.removeProperty(domainProviderProperty))
			.status(Status.OK)
			.build();
	}
	
	@PostMapping("/update")
	public Response<?> update(@RequestBody DomainProvider domainProvider) {
		return Response.<DomainProvider>builder().data(domainProviderService.save(domainProvider)).status(Status.OK).build();
	}
	
	@DeleteMapping("/{domainProviderId}")
	public Response<?> delete(
		@PathVariable("domainProviderId") DomainProvider domainProvider
	) {
		return Response.<DomainProvider>builder()
			.data(domainProviderService.delete(domainProvider))
			.status(Status.OK)
			.build();
	}
	
	@PutMapping("/update/multiple")
	public Response<?> updateMultiple(
		@RequestBody List<DomainProvider> domainProviders
	) {
		List<DomainProvider> dps = new ArrayList<>();
		domainProviders.forEach(dp -> {
			dps.add(domainProviderService.save(dp));
		});
		return Response.<List<DomainProvider>>builder()
			.data(dps)
			.status(Status.OK)
			.build();
	}
}