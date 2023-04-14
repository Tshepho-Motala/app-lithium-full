package lithium.service.cashier.controllers.internal;

import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.cashier.client.objects.Limits;
import lithium.service.cashier.client.objects.ProcessedProcessorProperty;
import lithium.service.cashier.client.objects.transaction.dto.Processor;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.data.repositories.DomainMethodProcessorRepository;
import lithium.service.cashier.exceptions.MoreThanOneMethodWithCodeException;
import lithium.service.cashier.exceptions.NoMethodWithCodeException;
import lithium.service.cashier.services.AccessRuleService;
import lithium.service.cashier.services.CashierFrontendService;
import lithium.service.cashier.services.DomainMethodProcessorService;
import lithium.service.cashier.services.DomainMethodService;
import lithium.service.cashier.services.InternalMethodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class PropertiesController {

    @Autowired
    private InternalMethodsService internalMethodsService;

    @Autowired
    DomainMethodProcessorRepository dmpRepository;
    @Autowired
    private CashierFrontendService cashierFrontendService;
    @Autowired
    DomainMethodService dmService;

    @Autowired
    DomainMethodProcessorService domainMethodProcessorsService;

    @Autowired
    AccessRuleService accessRuleService;

	@Autowired
	private DomainMethodService domainMethodService;

    @TimeThisMethod
    @GetMapping("/internal/processorByMethodCodeAndProcessorDescription")
    public Response<lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor> processorPropertiesByMethodNameAndProcessorDescription(
            @RequestParam String domainName, @RequestParam String methodCode, @RequestParam boolean deposit,
            @RequestParam String processorDescription) throws Exception {

        DomainMethodProcessor dmpEntity =
                dmpRepository.findByDomainMethodDomainNameAndDomainMethodDepositAndDomainMethodMethodCodeAndDescriptionAndEnabledAndDomainMethodEnabledAndProcessorEnabled(
                domainName, deposit, methodCode, processorDescription, true, true, true);

//        if (dmp == null) throw new Exception("Processor not found");
        if (dmpEntity == null) {
            return Response.<lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor>builder()
                    .status(Response.Status.NOT_FOUND).build();
        }

        lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor dmp =
                new lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor();

        dmp.setProcessor(getLimits(dmpEntity.getLimits()));
        dmp.setAccessRule(dmpEntity.getAccessRule());
        dmp.setDeleted(dmpEntity.getDeleted());
        dmp.setEnabled(dmpEntity.getEnabled());
        dmp.setId(dmpEntity.getId());

        dmp.setWeight(dmpEntity.getWeight());
        dmp.setProperties(new HashMap<>());

        List<ProcessedProcessorProperty> properties = internalMethodsService.findProcessedProcessorProperties(dmpEntity);

        if (properties != null) {
            for (ProcessedProcessorProperty property : properties) {
                dmp.getProperties().put(property.getName(), property.getValue());
            }
        }

        return Response.<lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor>builder()
                .data(dmp).status(Response.Status.OK).build();
    }

    private Processor getLimits(lithium.service.cashier.data.entities.Limits entityLimits) {

        Processor processor = new Processor();
        Limits limits = new Limits();
        limits.setId(entityLimits.getId());
        limits.setMinAmount(entityLimits.getMinAmount());
        limits.setMaxAmount(entityLimits.getMaxAmount());
        limits.setMaxAmountDay(entityLimits.getMaxAmountDay());
        limits.setMaxAmountWeek(entityLimits.getMaxAmountWeek());
        limits.setMaxAmountMonth(entityLimits.getMaxAmountMonth());
        limits.setMaxTransactionsDay(entityLimits.getMaxTransactionsDay());
        limits.setMaxTransactionsWeek(entityLimits.getMaxTransactionsWeek());
        limits.setMaxAmountMonth(entityLimits.getMaxTransactionsMonth());
        processor.setLimits(limits);

        return processor;
    }

    @TimeThisMethod
    @GetMapping("/internal/processor-properties-by-method-code-and-user-data")
    public Response<List<DomainMethodProcessorProperty>> propertiesOfFirstEnabledProcessor(
            @RequestParam("methodCode")  String methodCode, @RequestParam("deposit")  boolean deposit,
            @RequestParam("userGuid")  String userGuid, @RequestParam("domainName")  String domainName,
            @RequestParam("ipAddress")  String ipAddress, @RequestParam("userAgent")  String userAgent) {
        try {
            DomainMethodProcessor processor = cashierFrontendService.firstEnabledProcessor(domainName, methodCode, deposit,
                            userGuid, ipAddress, userAgent);
            List<DomainMethodProcessorProperty> properties = domainMethodProcessorsService.properties(processor.getId());
            return Response.<List<DomainMethodProcessorProperty>>builder()
                    .data(properties).status(Response.Status.OK).build();
        } catch (NoMethodWithCodeException | MoreThanOneMethodWithCodeException e) {
            log.info("Method with code " + methodCode + " is not configured/disabled for domain: " + domainName + ". Error:" + e.getMessage());
            return Response.<List<DomainMethodProcessorProperty>>builder()
                    .data(null).status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Failed to get processor properties for domainName " + domainName + " method " + (deposit ? "deposit" : "withdrawal") +
                    " methodCode " + methodCode + " userGuid " + userGuid + " ipAddress " + ipAddress + " userAgent" + userAgent + "Exception: " + e.getMessage(), e);
            return Response.<List<DomainMethodProcessorProperty>>builder()
                    .data(null).status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

	@TimeThisMethod
	@GetMapping("/internal/processor-properties-of-first-enabled-by-method-code")
	public Response<Map<String, String>> propertiesOfFirstEnabledProcessorByMethodCode(
			@RequestParam String domainName, @RequestParam String methodCode, @RequestParam boolean deposit ) throws Exception {

		List<DomainMethodProcessor> dmpEntities =
				dmpRepository.findByDomainMethodDomainNameAndDomainMethodDepositAndDomainMethodMethodCodeAndEnabledAndDomainMethodEnabledAndProcessorEnabled(
						domainName, deposit, methodCode, true, true, true);
		DomainMethodProcessor dmpEntity = dmpEntities.stream().findFirst().get();
		Map<String, String> dmpProps = new HashMap<>();
		if (dmpEntity == null) {
			return Response.<Map<String, String>>builder()
					.data(dmpProps)
					.status(Response.Status.NOT_FOUND).build();
		}
		List<ProcessedProcessorProperty> properties = internalMethodsService.findProcessedProcessorProperties(dmpEntity);
		if (properties != null) {
			properties.stream().forEach(processedProcessorProperty -> {
				dmpProps.put(processedProcessorProperty.getName(), processedProcessorProperty.getValue());
			});
		}
		return Response.<Map<String, String>>builder()
				.data(dmpProps).status(Response.Status.OK).build();
	}
}
