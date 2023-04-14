package lithium.service.cashier.processor.mvend;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.mvend.context.DoProcessorWithdrawContext;
import lithium.service.cashier.processor.mvend.services.DoProcessorStage2WithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class DoProcessor extends DoProcessorAdapter {

	@Autowired
	DoProcessorStage2WithdrawService service;

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		throw new Exception("Not yet implemented");
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		try {
			response.setProcessorReference(request.stageInputData(1, "mvend_reference"));
			return DoProcessorResponseStatus.NEXTSTAGE;
		} finally {
			log.info("withdrawStage1 " + request + " " + response);
		}
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		DoProcessorWithdrawContext withdrawContext = new DoProcessorWithdrawContext();
		try {
			withdrawContext.setRequest(request);
			withdrawContext.setResponse(response);
			withdrawContext.setGroupRef(request.getUser().getDomain());
			service.doWithdraw(withdrawContext, rest);
			return DoProcessorResponseStatus.NOOP;
		} finally {
			log.info("withdrawStage2 " + withdrawContext);
		}
	}


}
