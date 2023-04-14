package lithium.service.cashier.processor.bluem.ideal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.Status400BadRequestException;
import lithium.service.cashier.client.objects.Bank;
import lithium.service.cashier.client.objects.DomainMethodProcessorProperty;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
public class BluemBankListController {

    @Autowired
    private CashierInternalClientService cashier;
    @Autowired
    private ObjectMapper mapper;

	private static final String METHOD_CODE = "bluem-ideal";


	@GetMapping("/public/banks")
    public List<Bank> banks(LithiumTokenUtil token, HttpServletRequest request) throws Exception {

		try {
            DomainMethodProcessorProperty property = cashier.propertyOfFirstEnabledProcessor("banks_available", METHOD_CODE, true,
                token.guid(), token.domainName(), request.getRemoteAddr(), request.getHeader("User-Agent"));

            List<Bank> banksList = mapper.readValue(property.getValue(), mapper.getTypeFactory().constructCollectionType(List.class, Bank.class));
            Collections.sort(banksList);
            return banksList;
        } catch (Status400BadRequestException e) {
            log.warn("Failed to get banks list for Bluem iDeal", e);
            throw new Status400BadRequestException("Payment method is not supported.");
        } catch (Exception ex) {
            log.warn("Failed to get banks list for Bluem iDeal", ex);
            throw new Exception("Failed to get banks list for Bluem iDeal");
        }
    }
}
