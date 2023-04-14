package lithium.service.cashier.processor.wumg.directeller.ws;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import lithium.service.cashier.processor.wumg.directeller.wsdl.ConfirmDeposit;
import lithium.service.cashier.processor.wumg.directeller.wsdl.ConfirmDepositResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.GetStatus;
import lithium.service.cashier.processor.wumg.directeller.wsdl.GetStatus2;
import lithium.service.cashier.processor.wumg.directeller.wsdl.GetStatus2Response;
import lithium.service.cashier.processor.wumg.directeller.wsdl.GetStatusResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitDeposit;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitDepositResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitPayout;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitPayoutResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.UpdateTransaction;
import lithium.service.cashier.processor.wumg.directeller.wsdl.UpdateTransactionResponse;

public class TransactClient extends WebServiceGatewaySupport {
	
	public InitDepositResponse initDeposit(String uri, InitDeposit initDeposit) {
		setDefaultUri(uri);
		InitDepositResponse response = (InitDepositResponse)getWebServiceTemplate().marshalSendAndReceive(initDeposit);
		
		return response;
	}
	
	public ConfirmDepositResponse confirmDeposit(String uri, ConfirmDeposit confirmDeposit) {
		setDefaultUri(uri);
		ConfirmDepositResponse response = (ConfirmDepositResponse)getWebServiceTemplate().marshalSendAndReceive(confirmDeposit);
		
		return response;
	}
	
	public GetStatusResponse getStatus(String uri, GetStatus getStatus) {
		setDefaultUri(uri);
		GetStatusResponse response = (GetStatusResponse)getWebServiceTemplate().marshalSendAndReceive(getStatus);
		
		return response;
	}
	
	public GetStatus2Response getStatus2(String uri, GetStatus2 getStatus) {
		setDefaultUri(uri);
		GetStatus2Response response = (GetStatus2Response)getWebServiceTemplate().marshalSendAndReceive(getStatus);
		
		return response;
	}
	
	public InitPayoutResponse initPayout(String uri, InitPayout initPayout) {
		setDefaultUri(uri);
		InitPayoutResponse response = (InitPayoutResponse)getWebServiceTemplate().marshalSendAndReceive(initPayout);
		
		return response;
	}
	
	public UpdateTransactionResponse updateTransaction(String uri, UpdateTransaction updateTransaction) {
		setDefaultUri(uri);
		UpdateTransactionResponse response = (UpdateTransactionResponse)getWebServiceTemplate().marshalSendAndReceive(updateTransaction);
		
		return response;
	}
}
