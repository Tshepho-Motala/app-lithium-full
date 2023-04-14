package lithium.service.cashier.mock.upay.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.cashier.mock.upay.Configuration;
import lithium.service.cashier.processor.upay.btc.data.LoadFromBitcoinRequest;
import lithium.service.cashier.processor.upay.btc.data.LoadFromBitcoinResponse;
import lithium.service.cashier.processor.upay.upay.data.FinishTransferRequest;
import lithium.service.cashier.processor.upay.upay.data.FinishTransferResponse;
import lithium.service.cashier.processor.upay.upay.data.GetTransactionStatusRequest;
import lithium.service.cashier.processor.upay.upay.data.GetTransactionStatusResponse;
import lithium.service.cashier.processor.upay.upay.data.InitializeTransferRequest;
import lithium.service.cashier.processor.upay.upay.data.InitializeTransferResponse;
import lithium.service.cashier.processor.upay.upay.data.TransferAccountToAccountRequest;
import lithium.service.cashier.processor.upay.upay.data.TransferAccountToAccountResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UpayMockAPIController {
	
	@Autowired Configuration config;
	
	@RequestMapping("/api/merchant/v/1.0/function/initialize_transfer")
	public InitializeTransferResponse initializeTransfer(InitializeTransferRequest request) throws Exception {
		log.info("Initialize transfer request " + request.toString());
		return InitializeTransferResponse.builder()
				.status("success")
				.msg("Transfer initialized")
				.orderId(request.getOrderId())
				.tokenNumber("ABCD")
				.hash("a64fb511c885f9aeff211f7bfefc5648")
				.build();
	}
	
	@RequestMapping("/api/merchant/v/1.0/function/finish_transfer")
	public FinishTransferResponse finishTransfer(FinishTransferRequest request) throws Exception {
		log.info("Finish transfer request " + request.toString());
		return FinishTransferResponse.builder()
				.status("error")
				.code("517")
				.transactionId("10201")
				.build();
	}
	
	@RequestMapping("/api/merchant/v/1.0/function/get_transaction_status")
	public GetTransactionStatusResponse getTransactionStatus(GetTransactionStatusRequest request) throws Exception {
		log.info("Get transactions status request " + request.toString());
		return GetTransactionStatusResponse.builder()
				.status("success")
				.transactionStatus("C")
				.build();
	}
	
	
	@RequestMapping("/api/merchant/v/1.0/function/transfer_a_to_a")
	public TransferAccountToAccountResponse transferAccountToAccount(TransferAccountToAccountRequest request) throws Exception {
		log.info("TransferAccountToAccount request " + request.toString());
		return TransferAccountToAccountResponse.builder()
				.status("success")
				.transactionId(new Long(new Date().getTime()).toString())
				.build();
	}
	
	@RequestMapping("/api/merchant/v/1.0/function/load_from_bitcoin")
	public LoadFromBitcoinResponse loadFromBitcoin(LoadFromBitcoinRequest request) throws Exception {
		log.info("LoadFromBitcoin " + request.toString());
		return LoadFromBitcoinResponse.builder()
				.bitCoinAddress("1812xUMpCLvEdJgJxEy4XYTTDBsXVRquxM")
				.bitCoinAmount("2812.22")
				.fundsLoadsId("100")
				.status("success")
				.build();
	}
}
