package lithium.service.accounting.provider.internal.controllers;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.repositories.DomainRepository;
import lithium.service.accounting.provider.internal.data.repositories.PeriodRepository;
import lithium.service.accounting.provider.internal.services.PeriodService;

@RestController
@RequestMapping("/period")
public class PeriodController {

	@Autowired PeriodService periodService;
	@Autowired DomainRepository domainRepository;
	@Autowired PeriodRepository periodRepository;

	@RequestMapping("/find")
	public ResponseEntity<Period> find(
			@RequestParam String domainName,
			@RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) DateTime date, 
			@RequestParam int granularity) {
		Domain domain = domainRepository.findByName(domainName);
		if (domain == null)	return new ResponseEntity<Period>(HttpStatus.NOT_FOUND);
		Period period = periodService.findOrCreatePeriod(date, domain, granularity);
		return new ResponseEntity<Period>(period, HttpStatus.OK);
	}

	@RequestMapping("/findbyoffset")
	public Response<Period> findByOffset(
			@RequestParam String domainName, 
			@RequestParam int granularity,
			@RequestParam int offset) {
		Domain domain = domainRepository.findByName(domainName);
		if (domain == null) return Response.<Period>builder().status(Status.NOT_FOUND).build();
		Period period = periodService.findOrCreatePeriodByOffset(offset, domain, granularity);
		return Response.<Period>builder().data(period).status(Status.OK).build();
	}

//	@RequestMapping("/{id}/close")
//	public void close(@PathVariable Long id) {
//		Period period = periodRepository.findOne(id);
//		if (period == null) throw new RuntimeException("Invalid period id " + id);
//		periodService.closePeriod(period);
//	}
}
