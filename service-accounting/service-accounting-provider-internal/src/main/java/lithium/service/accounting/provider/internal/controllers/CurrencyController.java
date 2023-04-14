package lithium.service.accounting.provider.internal.controllers;

import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.repositories.CurrencyRepository;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {
	@Autowired CurrencyRepository repository;
	
	@GetMapping("/findByCode/{code}")
	public Response<Currency> findByCode(@PathVariable("code") String code) {
		return Response.<Currency>builder().data(repository.findByCode(code.toUpperCase())).status(OK).build();
	}
	
	@GetMapping("/all")
	public Response<Iterable<Currency>> all() {
		return Response.<Iterable<Currency>>builder().status(OK).data(repository.findAll()).build();
	}
	
	@GetMapping("/search/{currency}")
	public Response<Iterable<Currency>> allCurrenciesSearch(
		@PathVariable("currency") String currencyCode
	) {
		return Response.<Iterable<Currency>>builder().data(
			repository.findByCodeStartingWithOrderByCode(currencyCode)
		).build();
	}
}
