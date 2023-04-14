package lithium.service.cashier.processor.interswitch.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.access.client.AccessService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.interswitch.client.ICommandExecutor;
import lithium.service.cashier.processor.interswitch.services.CustomerInformationExecutor;
import lithium.service.cashier.processor.interswitch.services.PayDirectDepositService;
import lithium.service.cashier.processor.interswitch.services.PaymentNotificationExecutor;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@Slf4j
public class PayDirectController {

    @Autowired
    private LithiumServiceClientFactory serviceFactory;

    @Autowired
    private UserApiInternalClientService userApiInternalClientService;

    @Autowired
    private CachingDomainClientService cachingDomainClientService;

    @Autowired
    private PayDirectDepositService payDirectDepositService;

    @Autowired
    private CashierInternalClientService cashierService;

    @PostMapping(path = "/public/{domainName}/paydirect",
            consumes = {MediaType.TEXT_XML_VALUE},
            produces = {MediaType.TEXT_XML_VALUE})
    public ResponseEntity<Object> resolveRequest(
            HttpServletRequest webRequest,
            @PathVariable("domainName") String domainName,
            @RequestBody String request) {

        log.debug("Started Paydirect/Quickteller request="+request);
        ICommandExecutor commandExecutor;
        try {
            commandExecutor = getCommandExecutor(request, webRequest);
        } catch (Exception ex) {
            log.error("Cant define command execuror for request="+request, ex);
            return new ResponseEntity<>(null, BAD_REQUEST);
        }
        if (commandExecutor == null) {
            log.error("Cant define command execuror for request="+request);
            return new ResponseEntity<>(null, BAD_REQUEST);
        }
        User user = commandExecutor.getAllowedUser(domainName);
        if (user == null) {
            log.warn("Cant find user for request="+request);
            return new ResponseEntity<>(commandExecutor.buildErrorMessage("User not found"), HttpStatus.OK);
        }
		try {
			List<String> processorCodes = commandExecutor.resolveProcessorCodes(request) ;
			String allowedProcessorCode = payDirectDepositService.checkAllowedProcessorCode(webRequest, domainName, user, processorCodes);
			return new ResponseEntity<>(commandExecutor.executeCommand(allowedProcessorCode), HttpStatus.OK);
		} catch (Status401UnAuthorisedException e) {
			log.warn("Cant autorize request for domain=" + domainName + ", request=" + request);
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		} catch (Status500InternalServerErrorException | Status550ServiceDomainClientException | Status500LimitInternalSystemClientException ex) {
			log.error("Error during execure Paydirect/Quickteller request="+request, ex);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    private ICommandExecutor getCommandExecutor(String request, HttpServletRequest webRequest) throws Exception {
        if (request.contains("<PaymentNotificationRequest")) {
            PaymentNotificationExecutor paymentNotificationExecutor = new PaymentNotificationExecutor(request, webRequest);
            paymentNotificationExecutor.setPayDirectDepositService(payDirectDepositService);
            paymentNotificationExecutor.setUserApiInternalClientService(userApiInternalClientService);
            paymentNotificationExecutor.setCachingDomainClientService(cachingDomainClientService);
            paymentNotificationExecutor.setCashierService(cashierService);
            paymentNotificationExecutor.setServiceFactory(serviceFactory);
            return paymentNotificationExecutor;
        } else if (request.contains("<CustomerInformationRequest")) {
            CustomerInformationExecutor customerInformationExecutor = new CustomerInformationExecutor(request, webRequest);
            customerInformationExecutor.setPayDirectDepositService(payDirectDepositService);
            customerInformationExecutor.setUserApiInternalClientService(userApiInternalClientService);
            return customerInformationExecutor;
        }
        return null;
    }
}
