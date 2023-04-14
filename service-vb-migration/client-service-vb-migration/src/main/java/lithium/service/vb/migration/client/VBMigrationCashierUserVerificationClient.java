package lithium.service.vb.migration.client;

import lithium.exceptions.Status500InternalServerErrorException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-vb-migration")
public interface VBMigrationCashierUserVerificationClient {
    @RequestMapping(method = RequestMethod.GET,
            value = "/system/cashier-user-verification/has-user-made-cashier-transactions")
    boolean hasUserMadeCashierTransactions(@RequestParam("customerId") String customerId)
            throws Status500InternalServerErrorException;

    @RequestMapping(method = RequestMethod.GET,
            value = "/system/cashier-user-verification/has-user-made-cashier-transactions-using-provider")
    boolean hasUserMadeCashierTransactions(@RequestParam("customerId") String customerId,
            @RequestParam("providerName") String providerName) throws Status500InternalServerErrorException;
}
