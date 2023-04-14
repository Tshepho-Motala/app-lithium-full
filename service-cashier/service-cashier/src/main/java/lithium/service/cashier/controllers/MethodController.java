package lithium.service.cashier.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.data.entities.Method;
import lithium.service.cashier.data.entities.MethodStage;
import lithium.service.cashier.data.entities.MethodStageField;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.MethodService;
import lithium.service.cashier.services.MethodStageFieldService;
import lithium.service.cashier.services.MethodStageService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cashier/m")
public class MethodController {
	@Autowired MethodService methodService;
	@Autowired MethodStageService methodStageService;
	@Autowired MethodStageFieldService methodStageFieldService;
	
	@GetMapping
	@JsonView(Views.Public.class)
	public Response<?> findMethods() {
		log.debug("find all methods");
		return Response.<List<Method>>builder()
			.data(
				methodService.findAll()
			)
			.status(Status.OK)
			.build();
	}
	@GetMapping("/image")
	@JsonView(Views.Image.class)
	public Response<?> findMethodsImage() {
		return findMethods();
	}
	
	@GetMapping("/{methodCode}/fields/{input}/{transactionType}")
	public Response<List<MethodStageField>> getMethodFields(
		@PathVariable("methodCode") String methodCode,
		@PathVariable("input") Boolean input,
		@PathVariable("transactionType") String transactionType
	) {
		List<MethodStageField> fields = new ArrayList<MethodStageField>();
		if (!methodCode.equalsIgnoreCase("none")) {
			Method method = methodService.findByCode(methodCode);
			List<MethodStage> stages = methodStageService.findByMethodAndDeposit(method, transactionType.equalsIgnoreCase("deposit"));
			for (MethodStage stage: stages) {
				if (stage.getNumber() > 0)
					fields.addAll(methodStageFieldService.findByMethodStageAndInput(stage, input));
			}
			fields.parallelStream().forEach(field -> {
				field.getStage().getMethod().setImage(null);
			});
		}
		return Response.<List<MethodStageField>>builder()
				.data(fields)
				.status(Status.OK)
				.build();
	}
}