package lithium.service.cashier.processor.cc.trustspay.ws;

import java.util.List;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import lithium.service.cashier.processor.cc.trustspay.data.QueryRequest;
import lithium.service.cashier.processor.cc.trustspay.data.QueryResponse;
import lithium.service.cashier.processor.cc.trustspay.wsdl.GetNormalUserCheckResultList;
import lithium.service.cashier.processor.cc.trustspay.wsdl.GetNormalUserCheckResultListResponse;
import lithium.service.cashier.processor.cc.trustspay.wsdl.GetTestUserCheckResultList;
import lithium.service.cashier.processor.cc.trustspay.wsdl.GetTestUserCheckResultListResponse;
import lithium.service.cashier.processor.cc.trustspay.wsdl.ParamInBean;
import lithium.service.cashier.processor.cc.trustspay.wsdl.ParamOutBean;

public class QueryClient extends WebServiceGatewaySupport {
	
	public QueryResponse query(QueryRequest request, boolean test) throws Exception {
		
		ParamInBean beanIn = new ParamInBean();
		
		beanIn.setGatewayNo(request.getGatewayNo());
		beanIn.setMerNo(request.getMerNo());
		beanIn.setOrderNo(request.getOrderNo());
		beanIn.setSignInfo(request.getSignInfo());

		List<ParamOutBean> beanOutList = null;
		
		if (!test) {
			GetNormalUserCheckResultList wsRequest = new GetNormalUserCheckResultList();
			wsRequest.setArg0(beanIn);
			GetNormalUserCheckResultListResponse wsResponse = (GetNormalUserCheckResultListResponse) getWebServiceTemplate()
					.marshalSendAndReceive(wsRequest, new SoapActionCallback("ICustomerCheckWSService/getNormalUserCheckResultList"));
			beanOutList = wsResponse.getReturns();
		} else {
			GetTestUserCheckResultList wsRequest = new GetTestUserCheckResultList();
			wsRequest.setArg0(beanIn);
			GetTestUserCheckResultListResponse wsResponse = (GetTestUserCheckResultListResponse) getWebServiceTemplate()
					.marshalSendAndReceive(wsRequest, new SoapActionCallback("ICustomerCheckWSService/getTestUserCheckResultList"));
			beanOutList = wsResponse.getReturns();
		}
		
		if (beanOutList == null) throw new Exception("No results from webservice. Is this a valid transaction");
		if (beanOutList.size() != 1) throw new Exception("Did not receive only 1 respone from service " + beanOutList);
		
		ParamOutBean beanOut = beanOutList.get(0);
		
		QueryResponse response = QueryResponse.builder()
				.gatewayNo(beanOut.getGatewayNo())
				.merNo(beanOut.getMerNo())
				.orderNo(beanOut.getOrderNo())
				.tradeNo(beanOut.getTradeNo())
				.tradeDate(beanOut.getTradeDate())
				.tradeAmount(beanOut.getTradeAmount())
				.tradeCurrency(beanOut.getTradeCurrency())
				.sourceWebSite(beanOut.getSourceWebSite())
				.queryResult(beanOut.getQueryResult())
				.build();
		
		return response;
	}
}
