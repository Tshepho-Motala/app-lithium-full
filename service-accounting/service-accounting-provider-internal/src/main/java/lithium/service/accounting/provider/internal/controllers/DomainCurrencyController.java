package lithium.service.accounting.provider.internal.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.DomainCurrencyBasic;
import lithium.service.accounting.provider.internal.data.entities.DomainCurrency;
import lithium.service.accounting.provider.internal.services.DomainCurrencyService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/currencies/domain/{domainName}")
@Slf4j
public class DomainCurrencyController {
	@Autowired DomainCurrencyService domainCurrencyService;

	@GetMapping("/table")
	public DataTableResponse<DomainCurrency> table(
		@PathVariable("domainName") String domainName,
		@RequestParam(name="c", required=false) String currency,
		DataTableRequest request
	) throws Exception {
		Page<DomainCurrency> table = domainCurrencyService.findByDomain(
			domainName,
			currency,
			request.getSearchValue(),
			request.getPageRequest()
		);
		return new DataTableResponse<>(request, table);
	}

	@GetMapping("/{id}")
	public Response<DomainCurrency> viewCurrency(@PathVariable("id") DomainCurrency dc) {
		return Response.<DomainCurrency>builder().status(OK).data(dc).build();
	}

	@PostMapping("/save")
	public Response<DomainCurrency> save(
		@PathVariable("domainName") String domainName,
		@RequestBody DomainCurrencyBasic dcb
	) {
		DomainCurrency domainCurrency = null;
		try {
			domainCurrency = domainCurrencyService.save(
				domainName, 
				dcb.getCode(),
				dcb.getName(),
				dcb.getDescription(),
				dcb.getSymbol(),
				dcb.getDivisor()
			);
			return Response.<DomainCurrency>builder().status(OK).data(domainCurrency).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<DomainCurrency>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}

	@PostMapping("/{id}/setAsDefault")
	public Response<DomainCurrency> setAsDefault(@PathVariable("domainName") String domainName, @PathVariable("id") DomainCurrency dc) {
		try {
			dc = domainCurrencyService.setAsDefault(dc, true);
			return Response.<DomainCurrency>builder().status(OK).data(dc).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<DomainCurrency>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}

	@DeleteMapping("/delete/{id}")
	public Response<DomainCurrency> delete(
		@PathVariable("domainName") String domainName,
		@PathVariable("id") DomainCurrency domainCurrency
	) {
		try {
			if (domainCurrency.getCurrency().isReal()) return Response.<DomainCurrency>builder().status(Status.FORBIDDEN).build();
			domainCurrencyService.delete(domainCurrency);
			return Response.<DomainCurrency>builder().status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<DomainCurrency>builder().status(INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/list")
	public Response<List<DomainCurrency>> list(@PathVariable("domainName") String domainName) {
		return Response.<List<DomainCurrency>>builder().data(domainCurrencyService.findByDomain(domainName)).status(OK).build();
	}
}
