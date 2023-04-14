package lithium.service.cashier.processor.flutterwave.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.Status400BadRequestException;
import lithium.service.Response;
import lithium.service.cashier.client.exceptions.Status500CashierInternalSystemClientException;
import lithium.service.cashier.client.internal.BanksLookupClient;
import lithium.service.cashier.client.objects.DomainMethodProcessorProperty;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.flutterwave.data.Bank;
import lithium.service.cashier.processor.flutterwave.services.WithdrawService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class BankListController implements BanksLookupClient {

    @Autowired
    private CashierInternalClientService cashier;
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private ObjectMapper mapper;

	private static final String METHOD_CODE = "flutterwave";
	private static final String USSD_METHOD_CODE = "flutterwaveussd";

	@GetMapping("/public/deposit/ussd/banks")
    public List<Bank> banks(LithiumTokenUtil token, HttpServletRequest request) throws Exception {

		try {
            DomainMethodProcessorProperty property = cashier.propertyOfFirstEnabledProcessor("banks_available",
                    USSD_METHOD_CODE, true, token.guid(), token.domainName(), request.getRemoteAddr(), request.getHeader("User-Agent"));

            List<Bank> banksList = mapper.readValue(property.getValue(), mapper.getTypeFactory().constructCollectionType(List.class, Bank.class));
            Collections.sort(banksList);
            return banksList;
        } catch (Status400BadRequestException e) {
            log.warn("Failed to get banks list for flutterwaveussd", e);
            throw new Status400BadRequestException("Payment method is not supported.");
        } catch (Exception ex) {
            log.warn("Failed to get banks list for flutterwaveussd", ex);
            throw new Exception("Failed to get banks list for ussd deposit");
        }
    }

	@GetMapping("/public/withdraw/banks/{country}")
    public List<Bank> withdrawBanks(@PathVariable("country") String country, LithiumTokenUtil token,
                                    HttpServletRequest request) throws Exception {

		try {
            Map<String, String> propertiesMap = cashier.propertiesMapOfFirstEnabledProcessor(METHOD_CODE, false,
                    token.guid(), token.domainName(), request.getRemoteAddr(), request.getHeader("User-Agent"));

            String banksUrl = propertiesMap.get("withdraw_banks_url");
            String secretKey = propertiesMap.get("secret_key");

            if (banksUrl == null || banksUrl.isEmpty())
                throw new Exception("banks_url property is not configured for current processor");
            if (secretKey == null || secretKey.isEmpty())
                throw new Exception("secret_key property is not configured for current processor");

            return withdrawService.getBankList(country, banksUrl, secretKey);

        } catch (Status400BadRequestException e) {
            log.warn("Failed to get banks list for flutterwave withdraw. For country: " + country, e);
            throw new Status400BadRequestException("Payment method is not supported.");
        } catch (Exception ex) {
            log.warn("Failed to get banks list for flutterwave withdraw. For country: " + country, ex);
            throw new Status500CashierInternalSystemClientException("Failed to get banks list for flutterwave withdraw");
        }
    }

    @Override
    @RequestMapping(path = "/system/banks", method = RequestMethod.POST)
    public Response<List<lithium.service.cashier.client.objects.Bank>> banks(@RequestBody Map<String, String> processorProperties) throws Exception {
        String country = processorProperties.get("country_code");
        String banksUrl = processorProperties.get("withdraw_banks_url");
        String secretKey = processorProperties.get("secret_key");
        if (banksUrl == null || banksUrl.isEmpty())
            throw new Exception("banks_url property is not configured for current processor");
        if (secretKey == null || secretKey.isEmpty())
            throw new Exception("secret_key property is not configured for current processor");
        List<lithium.service.cashier.client.objects.Bank> banks = withdrawService.getBankList(country, banksUrl, secretKey).stream()
                .map(b -> mapper.convertValue(b, lithium.service.cashier.client.objects.Bank.class))
                .collect(Collectors.toList());
        return Response.<List<lithium.service.cashier.client.objects.Bank>>builder().data(banks).build();
    }
}
