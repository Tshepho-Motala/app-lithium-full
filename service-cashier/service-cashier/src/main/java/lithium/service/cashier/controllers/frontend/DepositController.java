package lithium.service.cashier.controllers.frontend;

import lithium.service.cashier.client.frontend.DoRequest;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.exceptions.MoreThanOneMethodWithCodeException;
import lithium.service.cashier.exceptions.NoMethodWithCodeException;
import lithium.service.cashier.machine.DoMachine;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/frontend/deposit/v2")
public class DepositController {

    @Autowired WebApplicationContext beanContext;

    @RequestMapping
    public DoResponse request(
            @RequestParam String methodCode,
            @RequestBody DoRequest request,
            LithiumTokenUtil token,
            HttpServletRequest httpServletRequest
    ) throws NoMethodWithCodeException, MoreThanOneMethodWithCodeException {
        DoMachine machine = beanContext.getBean(DoMachine.class);
        return machine.run(methodCode, token, request, "deposit", httpServletRequest);
    }

}