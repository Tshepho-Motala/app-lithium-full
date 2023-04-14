package lithium.service.domain.controllers;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.data.entities.DomainRevision;
import lithium.service.domain.data.entities.DomainRevisionLabelValue;
import lithium.service.domain.data.objects.DomainRevisionBasic;
import lithium.service.domain.services.DomainSettingsService;
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

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@RestController
@RequestMapping("/domain/settings/{domainName}")
@Slf4j
public class DomainSettingsController {
	@Autowired DomainSettingsService service;

	@GetMapping("/findCurrentSetting")
	public Response<DomainRevisionLabelValue> findCurrentSetting(
		@PathVariable("domainName") String domainName,
		@RequestParam("settingName") String settingName
	) throws Exception {
		return Response.<DomainRevisionLabelValue>builder().data(service.findCurrentDomainSetting(domainName,
			settingName)).status(OK).build();
	}
	
	@GetMapping("/findCurrentSettings")
	public Response<List<DomainRevisionLabelValue>> findCurrentSettings(@PathVariable("domainName") String domainName) throws Exception {
		return Response.<List<DomainRevisionLabelValue>>builder().data(service.findCurrentDomainSettings(domainName)).status(OK).build();
	}
	
	@GetMapping("/{domainRevisionId}")
	public Response<DomainRevision> get(@PathVariable("domainName") String domainName, @PathVariable("domainRevisionId") DomainRevision domainRevision) {
		return Response.<DomainRevision>builder().data(domainRevision).status(OK).build();
	}
	
	@GetMapping("/history/table")
	public DataTableResponse<DomainRevision> settingsHistoryTable(@PathVariable("domainName") String domainName, DataTableRequest request) {
		Page<DomainRevision> table = service.findSettingsHistoryByDomain(domainName, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@PostMapping("/add")
	public Response<DomainRevision> add(@PathVariable("domainName") String domainName, @RequestBody DomainRevisionBasic domainRevisionBasic) {
		DomainRevision domainRevision = null;
		try {
			domainRevision = service.add(domainRevisionBasic);
			return Response.<DomainRevision>builder().data(domainRevision).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<DomainRevision>builder().data(domainRevision).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
}
