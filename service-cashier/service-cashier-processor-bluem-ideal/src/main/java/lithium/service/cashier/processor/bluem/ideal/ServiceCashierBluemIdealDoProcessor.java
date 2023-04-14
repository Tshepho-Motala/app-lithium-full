package lithium.service.cashier.processor.bluem.ideal;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.bluem.exceptions.BluemValidatePaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ServiceCashierBluemIdealDoProcessor extends DoProcessorAdapter {

	@Autowired
	BluemIdealService bluemIdealService;

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		return bluemIdealService.initiateDeposit(request, response, context, rest);
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		try {
			return bluemIdealService.verifyDeposit(request, response, null);
		} catch (BluemValidatePaymentException ex) {
			return DoProcessorResponseStatus.NOOP;
		}
	}
}
