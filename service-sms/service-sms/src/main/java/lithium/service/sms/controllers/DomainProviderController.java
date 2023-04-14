package lithium.service.sms.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.sms.client.objects.DomainProviderBasic;
import lithium.service.sms.data.entities.DomainProvider;
import lithium.service.sms.data.entities.DomainProviderProperty;
import lithium.service.sms.services.DomainProviderService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/domainProvider")
@Slf4j
public class DomainProviderController {
	@Autowired DomainProviderService domainProviderService;
	
	@GetMapping("/domain/{domainName}")
	private Response<List<DomainProvider>> domainProviders(@PathVariable("domainName") String domainName, LithiumTokenUtil tokenUtil) {
		try {
			DomainValidationUtil.validate(domainName, "SMS_CONFIG", tokenUtil);
			return Response.<List<DomainProvider>>builder().data(domainProviderService.findAll(domainName)).status(Status.OK).build();
		} catch (Exception e) {
			return Response.<List<DomainProvider>>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/addDomainProvider")
	private Response<DomainProvider> domainProviderAdd(@RequestBody DomainProviderBasic domainProviderBasic, LithiumTokenUtil tokenUtil) {
		try {
			DomainValidationUtil.validate(domainProviderBasic.getDomainName(), "SMS_CONFIG", tokenUtil);
			DomainProvider domainProvider = domainProviderService.addDomainProvider(domainProviderBasic.getDomainName(), domainProviderBasic.getDescription(), domainProviderBasic.getProviderId());
			return Response.<DomainProvider>builder().data(domainProvider).status(Status.OK).build();
		} catch (Exception e) {
			return Response.<DomainProvider>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PutMapping("/{id}/props")
	public Response<?> updateProperties(
		@PathVariable("id") DomainProvider domainProvider,
		@RequestBody List<DomainProviderProperty> domainProviderProperties,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		log.info("Updating DomainProviderProperties : "+domainProviderProperties);
		try {
			DomainValidationUtil.validate(domainProvider.getDomain().getName(), "SMS_CONFIG", tokenUtil);
			return Response.<List<DomainProviderProperty>>builder()
					.data(domainProviderService.saveProperties(domainProvider, domainProviderProperties))
					.status(Status.OK)
					.build();
		} catch (Exception e) {
			return Response.<List<DomainProviderProperty>>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping("/{id}/props")
	public Response<?> propertiesWithDefaults(@PathVariable("id") DomainProvider domainProvider, LithiumTokenUtil tokenUtil) {
		try {
			DomainValidationUtil.validate(domainProvider.getDomain().getName(), "SMS_CONFIG", tokenUtil);
			return Response.<List<DomainProviderProperty>>builder()
					.data(domainProviderService.propertiesWithDefaults(domainProvider.getId()))
					.status(Status.OK)
					.build();
		} catch (Exception e) {
			return Response.<List<DomainProviderProperty>>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping("/{id}/props/nodef")
	public Response<?> properties(@PathVariable("id") DomainProvider domainProvider, LithiumTokenUtil tokenUtil) {
		try {
			DomainValidationUtil.validate(domainProvider.getDomain().getName(), "SMS_CONFIG", tokenUtil);
			return Response.<List<DomainProviderProperty>>builder()
					.data(domainProviderService.propertiesNoDefaults(domainProvider.getId()))
					.status(Status.OK)
					.build();
		} catch (Exception e) {
			return Response.<List<DomainProviderProperty>>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@DeleteMapping("/{domainProviderId}/prop/{domainProviderPropertyId}")
	public Response<?> deleteProperty(
		@PathVariable("domainProviderId") DomainProvider domainProvider,
		@PathVariable("domainProviderPropertyId") DomainProviderProperty domainProviderProperty,
		LithiumTokenUtil tokenUtil
	) {
		try {
			DomainValidationUtil.validate(domainProvider.getDomain().getName(), "SMS_CONFIG", tokenUtil);
			return Response.<DomainProviderProperty>builder()
					.data(domainProviderService.removeProperty(domainProviderProperty))
					.status(Status.OK)
					.build();
		} catch (Exception e) {
			return Response.<DomainProviderProperty>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/update")
	public Response<?> update(@RequestBody DomainProvider domainProvider, LithiumTokenUtil tokenUtil) {
		try {
			DomainValidationUtil.validate(domainProvider.getDomain().getName(), "SMS_CONFIG", tokenUtil);
			return Response.<DomainProvider>builder().data(domainProviderService.save(domainProvider)).status(Status.OK).build();
		} catch (Exception e) {
			return Response.<DomainProvider>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@DeleteMapping("/{domainProviderId}")
	public Response<?> delete(
		@PathVariable("domainProviderId") DomainProvider domainProvider,
		LithiumTokenUtil tokenUtil
	) {
		try {
			DomainValidationUtil.validate(domainProvider.getDomain().getName(), "SMS_CONFIG", tokenUtil);
			return Response.<DomainProvider>builder()
					.data(domainProviderService.delete(domainProvider))
					.status(Status.OK)
					.build();
		} catch (Exception e) {
			return Response.<DomainProvider>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PutMapping("/update/multiple")
	public Response<?> updateMultiple(
		@RequestBody List<DomainProvider> domainProviders,
		LithiumTokenUtil tokenUtil
	) {
		try {
			for (DomainProvider domainProvider: domainProviders) {
				DomainValidationUtil.validate(domainProvider.getDomain().getName(), "SMS_CONFIG", tokenUtil);
			}
		} catch (Exception e) {
			return Response.<List<DomainProvider>>builder()
					.status(Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage())
					.build();
		}
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