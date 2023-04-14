package lithium.service.domain.controllers;

import java.util.Calendar;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status475ProviderAuthClientExistException;
import lithium.service.domain.client.exceptions.Status551ProviderAuthClientNotFoundException;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.ProviderAuthClient;
import lithium.service.domain.data.objects.ProviderAuthClientBasic;
import lithium.service.domain.data.repositories.ProviderAuthClientRepository;
import lithium.service.domain.data.specifications.ProviderAuthClientSpecification;
import lithium.service.domain.services.DomainService;
import lithium.service.domain.services.ProviderAuthClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@RestController
@Slf4j
public class ProviderAuthClientController {
	@Autowired ProviderAuthClientService providerAuthClientService;
	@Autowired DomainService domainService;
	@Autowired ChangeLogService changeLogService;
	@Autowired ProviderAuthClientRepository providerAuthClientRepository;

//	@GetMapping("/findCurrentSettings")
//	public Response<List<DomainRevisionLabelValue>> findCurrentSettings(@PathVariable("domainName") String domainName) throws Exception {
//		return Response.<List<DomainRevisionLabelValue>>builder().data(service.findCurrentDomainSettings(domainName)).status(OK).build();
//	}
//
//	@GetMapping("/{domainRevisionId}")
//	public Response<DomainRevision> get(@PathVariable("domainName") String domainName, @PathVariable("domainRevisionId") DomainRevision domainRevision) {
//		return Response.<DomainRevision>builder().data(domainRevision).status(OK).build();
//	}
//

	@GetMapping(value = "/domain/providerauthclient/list")
	public Response<List<ProviderAuthClientBasic>> categoryList(
			@RequestParam(name="domainNames") String[] domainNames,
			Principal principal
	) throws Exception {

		if (domainNames == null || domainNames.length == 0) {
			return Response.<List<ProviderAuthClientBasic>>builder().status(OK).data(Collections.emptyList()).build();
		}

		final List<String> domainList = Arrays.stream(domainNames)
				.filter(p -> p != null && !p.trim().isEmpty())
				.map(p -> domainService.findByName(p))
				.filter(Objects::nonNull)
				.map(Domain::getName)
				.collect(Collectors.toList());

		Specification<ProviderAuthClient> spec = null;

		if (domainList.size() > 0) {
			spec = Specification.where(ProviderAuthClientSpecification.domainNamesIn(domainList));
		} else {
			return Response.<List<ProviderAuthClientBasic>>builder().status(OK).data(Collections.emptyList()).build();
		}

		final List<ProviderAuthClient> page = providerAuthClientRepository.findAll(spec);
		final List<ProviderAuthClientBasic> result = page.stream().map(p -> ProviderAuthClientBasic.builder()
				.id(p.getId())
				.code(p.getCode())
				.description(p.getDescription())
				.guid(p.getGuid())
				.creationDate(p.getCreationDate())
				.build()
		).collect(Collectors.toList());

		return Response.<List<ProviderAuthClientBasic>>builder().status(OK).data(result).build();
	}

	@GetMapping("/domain/providerauthclient/{domainName}/table")
	public DataTableResponse<ProviderAuthClient> table(
		@PathVariable("domainName") String domainName,
		DataTableRequest request
	) {
		Page<ProviderAuthClient> table = providerAuthClientService.table(domainName, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}

	@DeleteMapping("/domain/providerauthclient/{domainName}/delete/{id}")
	public Response<Void> delete(
		@PathVariable("domainName") String domainName,
		@PathVariable("id") ProviderAuthClient providerAuthClient
	) {
		try {
			providerAuthClientService.delete(providerAuthClient);
			return Response.<Void>builder().status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Void>builder().status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/domain/providerauthclient/{domainName}/add")
	public Response<ProviderAuthClient> add(
		@PathVariable("domainName") String domainName,
		@RequestBody ProviderAuthClient providerAuthClient,
		LithiumTokenUtil lithiumTokenUtil,
		Locale locale
	) throws Status475ProviderAuthClientExistException {
		Domain domain = domainService.findByName(domainName);
		providerAuthClient.setDomain(domain);
		providerAuthClient = providerAuthClientService.add(providerAuthClient, lithiumTokenUtil, locale);

		return Response.<ProviderAuthClient>builder().data(providerAuthClient).status(OK).build();
	}

	@PostMapping("/domain/providerauthclient/{domainName}/save")
	public Response<ProviderAuthClient> save(
		@RequestBody ProviderAuthClient providerAuthClient,
		LithiumTokenUtil lithiumTokenUtil,
		Locale locale
	) throws Status475ProviderAuthClientExistException {
		providerAuthClient = providerAuthClientService.save(providerAuthClient, lithiumTokenUtil, locale);
		return Response.<ProviderAuthClient>builder().data(providerAuthClient).status(OK).build();
	}

	@GetMapping("/domain/providerauthclient/{domainName}/findById")
	public Response<ProviderAuthClient> findById(
		@PathVariable("domainName") String domainName,
		@RequestParam("id") ProviderAuthClient providerAuthClient
	) {
		return Response.<ProviderAuthClient>builder().data(providerAuthClient).status(OK).build();
	}

	@GetMapping(value = "/domain/providerauthclient/{domainName}/{id}/changelogs")
	public @ResponseBody
	Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
		Response<ChangeLogs> listLimited = changeLogService.listLimited(ChangeLogRequest.builder()
			.entityRecordId(id)
			.entities(new String[] { "providerAuthClient" })
			.page(p)
			.build()
		);
		return listLimited;
	}

	@TimeThisMethod
	@GetMapping("/system/domain/providerauthclient/{domainName}/find")
	public Response<ProviderAuthClient> find(
		@PathVariable("domainName") String domainName,
		@RequestParam("code") String code
	) throws Status551ProviderAuthClientNotFoundException {
		SW.start("domainService.findByName");
		Domain domain = domainService.findByName(domainName);
		SW.stop();

		SW.start("providerAuthClientService.find");
		ProviderAuthClient duc = providerAuthClientService.find(domain, code);
		SW.stop();

		return Response.<ProviderAuthClient>builder().data(duc).status(OK).build();
	}
}
