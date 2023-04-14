package lithium.service.cashier.processor.flutterwave.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.processor.flutterwave.api.v2.schema.FlutterWaveWebhookRequestV2;
import lithium.service.cashier.processor.flutterwave.api.v2.schema.FlutterWaveWebhookTransfer;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveChargesData;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveWebhookRequest;
import lithium.service.cashier.processor.flutterwave.api.v3.schema.FlutterWaveWithdrawData;
import lithium.service.cashier.processor.flutterwave.services.DepositService;
import lithium.service.cashier.processor.flutterwave.services.WithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CallbackController {

	@Autowired
	WithdrawService withdrawService;

	@Autowired
	DepositService depositService;

	@Autowired
	private ObjectMapper mapper;

	@PostMapping("/public/webhook")
	public void webhook(@RequestBody String data) throws Exception {

		log.info("Webhook is called: " + data);

		//handle old webhook request. Should be removed when fixed on FW side
		FlutterWaveWebhookRequest request = convertFromV2Webhook(data);

		if (request == null) {
            request = mapper.readValue(data, FlutterWaveWebhookRequest.class);
        }

        log.info("Converted to Object" + request.toString());

        String eventType = request.getEventType();

        if (eventType != null && eventType.equals("Transfer")) {
            withdrawService.proceedWithdrawWebhook(data, request);
        } else {
            depositService.processWebhook(mapper.readValue(request.getData().toString(), FlutterWaveChargesData.class));
        }
    }

    private FlutterWaveWebhookRequest convertFromV2Webhook(String data) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        FlutterWaveWebhookRequestV2 oldRequest = mapper.readValue(data, FlutterWaveWebhookRequestV2.class);

        if (oldRequest == null)
            return null;

        FlutterWaveWebhookRequest newRequest = null;

        //check for old transfer webhook
        if (oldRequest.getEventType() != null && oldRequest.getEventType().equalsIgnoreCase("Transfer")
        && oldRequest.getTransfer() != null) {
            FlutterWaveWebhookTransfer transfer = oldRequest.getTransfer();

            newRequest = FlutterWaveWebhookRequest.builder()
                    .eventType("Transfer")
                    .data(mapper.valueToTree(FlutterWaveWithdrawData.builder()
                            .id(transfer.getId())
                            .reference(transfer.getReference())
                            .status(transfer.getStatus())
                            .build()))
                    .build();
        }
        //check for old payment webhook
        if (oldRequest.getId() != null) {
            newRequest = FlutterWaveWebhookRequest.builder().
                    data(mapper.valueToTree(FlutterWaveChargesData.builder()
                            .id(oldRequest.getId())
                            .status(oldRequest.getStatus())
                            .tx_ref(oldRequest.getTxRef())
                            .build()))
                    .build();
        }
        log.info("Webhook request is recognized as old one FW doc V2" + oldRequest.toString());

        return newRequest;
    }
}
