package lithium.service.cashier.processor.paystack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.Status400BadRequestException;
import lithium.service.Response;
import lithium.service.cashier.client.internal.BanksLookupClient;
import lithium.service.cashier.client.objects.Bank;
import lithium.service.cashier.client.objects.DomainMethodProcessorProperty;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.paystack.api.schema.banklist.PaystackBank;
import lithium.service.cashier.processor.paystack.services.WithdrawService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class BankListController implements BanksLookupClient {

    private CashierInternalClientService cashier;
    private WithdrawService withdrawService;
    private RestTemplate restTemplate;
    private ObjectMapper mapper;

	private static final String METHOD_CODE = "paystack";
	private static final String USSD_METHOD_CODE = "paystackussd";

	@Autowired
    public BankListController(@Qualifier("lithium.rest") RestTemplateBuilder builder, CashierInternalClientService cashier, WithdrawService withdrawService, ObjectMapper mapper) {
        this.restTemplate = builder.build();
        this.cashier = cashier;
        this.withdrawService = withdrawService;
        this.mapper = mapper;
    }

    @GetMapping("/public/withdraw/banks")
    public List<PaystackBank> payWithBanks(LithiumTokenUtil token, HttpServletRequest request) throws Exception {

        try {
            Map<String, String> propertiesMap = cashier.propertiesMapOfFirstEnabledProcessor(METHOD_CODE, false,
                    token.guid(), token.domainName(), request.getRemoteAddr(), request.getHeader("User-Agent"));

            String banksUrl = propertiesMap.get("withdraw_banks_url");
            String secretKey = propertiesMap.get("secret_key");

            if (banksUrl == null || banksUrl.isEmpty())
                throw new Exception("banks_url property is not configured for current processor");
            if (secretKey == null || secretKey.isEmpty())
                throw new Exception("secret_key property is not configured for current processor");

            return withdrawService.getBankList(banksUrl, secretKey, restTemplate);

        } catch (Status400BadRequestException e) {
            log.warn("Failed to get banks list for paystack: " + e.getMessage());
            throw new Status400BadRequestException("Payment method is not supported.");
        } catch (Exception ex) {
            log.warn("Failed to get banks list for paystack: " + ExceptionUtils.getRootCauseMessage(ex));
            throw new Exception("Failed to get banks list for paysatck");
        }
    }

    @GetMapping("/public/deposit/ussd/banks")
    public List<PaystackBank> ussdBanks(LithiumTokenUtil token, HttpServletRequest request) throws Exception {

        try {
            DomainMethodProcessorProperty property = cashier.propertyOfFirstEnabledProcessor("ussd_banks_list",
                    USSD_METHOD_CODE, true, token.guid(), token.domainName(), request.getRemoteAddr(), request.getHeader("User-Agent"));

            List<PaystackBank> banksList = mapper.readValue(property.getValue(), mapper.getTypeFactory().constructCollectionType(List.class, PaystackBank.class));
            Collections.sort(banksList);
            return banksList;
        } catch (Status400BadRequestException e) {
            log.warn("Failed to get banks list for paystackussd: " + ExceptionUtils.getRootCauseMessage(e));
            throw new Status400BadRequestException("Payment method is not supported.");
        } catch (Exception ex) {
            log.warn("Failed to get banks list for paystackussd: " + ExceptionUtils.getRootCauseMessage(ex));
            throw new Exception("Failed to get banks list for ussd deposit");
        }
    }

    @Override
    @RequestMapping(path = "/system/banks", method = RequestMethod.POST)
    public Response<List<Bank>> banks(@RequestBody Map<String, String> processorProperties) throws Exception {
        String banksUrl = processorProperties.get("withdraw_banks_url");
        String secretKey = processorProperties.get("secret_key");
        if (banksUrl == null || banksUrl.isEmpty())
            throw new Exception("banks_url property is not configured for current processor");
        if (secretKey == null || secretKey.isEmpty())
            throw new Exception("secret_key property is not configured for current processor");
        List<lithium.service.cashier.client.objects.Bank> banks = withdrawService.getBankList(banksUrl, secretKey, restTemplate).stream()
                .map(b -> mapper.convertValue(b, lithium.service.cashier.client.objects.Bank.class))
                .collect(Collectors.toList());
        return Response.<List<lithium.service.cashier.client.objects.Bank>>builder().data(banks).build();
    }
}
