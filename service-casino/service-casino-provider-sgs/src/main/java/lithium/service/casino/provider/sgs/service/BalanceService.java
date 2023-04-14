package lithium.service.casino.provider.sgs.service;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.casino.client.objects.request.BalanceRequest;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.provider.sgs.data.ErrorCodes;
import lithium.service.casino.provider.sgs.data.MessageTypes;
import lithium.service.casino.provider.sgs.data.request.Request;
import lithium.service.casino.provider.sgs.data.response.Extinfo;
import lithium.service.casino.provider.sgs.data.response.GetBalanceResult;
import lithium.service.casino.provider.sgs.data.response.MethodResponse;
import lithium.service.casino.provider.sgs.data.response.Response;
import lithium.service.casino.provider.sgs.data.response.Result;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BalanceService {
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	protected SGSService sgsService;
	
	public Response<? extends Result<? extends Extinfo>> getBalance(Request request, User user) {
		
		BalanceResponse balanceResponse = null;
		try {
			BalanceRequest br = new BalanceRequest();
			br.setDomainName(user.getDomain().getName());
			br.setUserGuid(user.getDomain().getName()+"/"+ user.getUsername());
			balanceResponse = sgsService.getCasinoService().handleBalanceRequest(br);
		} catch (Exception e) {
			log.error("Unable to get player balance. User:" + user + " | Response: " + balanceResponse, e);
			return sgsService.createErrorResponse(request, ErrorCodes.GENERAL_UNSPECIFIED);
		}
		
		GetBalanceResult<Extinfo> getBalanceResult = new GetBalanceResult<>(request.getMethodCall().getCall().getSeq(), request.getMethodCall().getCall().getToken(), balanceResponse.getBalanceCents()+"", "");
		getBalanceResult.setExtinfo(new Extinfo());
		
		MethodResponse<GetBalanceResult<Extinfo>> methodResponse = new MethodResponse<>(MessageTypes.GET_BALANCE.getTypeName(), new Date(), getBalanceResult);
		Response<GetBalanceResult<Extinfo>> response = new Response<>(methodResponse);
		
		return response;
	}
}
