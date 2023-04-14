package lithium.service.cashier.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.ProcessorType;
import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.ProcessorService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cashier/p")
public class ProcessorController {
	@Autowired
	private ProcessorService processorService;
	
	@JsonView(Views.Public.class)
	@GetMapping("/method/{methodId}/{type}")
	public Response<?> processors(
		@PathVariable("methodId") Long methodId,
		@PathVariable("type") String type
	) {
		log.debug("Finding processors for methodId : "+methodId+" type : "+type);
		return Response.<List<Processor>>builder()
			.data(
				processorService.findByMethodId(methodId, ProcessorType.fromValue(type))
			)
			.status(Status.OK)
			.build();
	}
	
	@JsonView(Views.Image.class)
	@GetMapping("/method/{methodId}/{type}/image")
	public Response<?> processorsImage(
		@PathVariable("methodId") Long methodId,
		@PathVariable("type") String type
	) {
		return processors(methodId, type);
	}
}