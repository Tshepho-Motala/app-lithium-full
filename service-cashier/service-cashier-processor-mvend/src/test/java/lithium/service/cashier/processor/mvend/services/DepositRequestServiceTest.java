package lithium.service.cashier.processor.mvend.services;

import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.mvend.api.schema.deposit.DepositRequest;
import lithium.service.cashier.processor.mvend.context.DepositRequestContext;
import lithium.service.cashier.processor.mvend.services.shared.SharedService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.service.UserApiInternalClientService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import static org.junit.Assert.*;

public class DepositRequestServiceTest {

    private DepositRequestService service = new DepositRequestService();
    private CashierInternalClientService cashierInternalClientService = Mockito.mock(CashierInternalClientService.class);
    private UserApiInternalClientService userApiInternalClientService = Mockito.mock(UserApiInternalClientService.class);
    private SharedService sharedService = Mockito.mock(SharedService.class);
    private LithiumServiceClientFactory services = Mockito.mock(LithiumServiceClientFactory.class);

    @Before
    public void setup() {

        cashierInternalClientService.setServices(services);
        service.setCashierService(cashierInternalClientService);
        service.setSharedService(sharedService);
        service.setUserService(userApiInternalClientService);

    }

    @Test
    public void testAmountIssue() throws Exception {

        DepositRequestContext context = new DepositRequestContext();
        context.setProcessingDmp(DomainMethodProcessor.builder().id(1L).build());
        context.setUserGuid("giid");
        context.setUserGuid("giid");

        for (Long i = 1L; i < 10L; i++) {
            DepositRequest request = new DepositRequest();

            String amount = i.toString() + i.toString() + i.toString() + i.toString() + "." + i.toString() + i.toString();
            Long amountCents = Long.parseLong(i.toString() + i.toString() + i.toString() + i.toString() + i.toString() + i.toString());

            request.setAmount(amount);
            request.setCurrency("NAR");
            request.setProcessor("someprocessor");
            request.setReference("somereference");

            context.setRequest(request);

            service.deposit(context, true);

            assertEquals("Amount in cents should be equal to string", amountCents.longValue(),
                    context.getAmountInCents().longValue());
        }
    }

}