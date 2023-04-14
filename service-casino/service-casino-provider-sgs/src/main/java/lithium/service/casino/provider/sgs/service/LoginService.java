package lithium.service.casino.provider.sgs.service;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.casino.client.objects.response.AccountInfoResponse;
import lithium.service.casino.provider.sgs.data.ErrorCodes;
import lithium.service.casino.provider.sgs.data.MessageTypes;
import lithium.service.casino.provider.sgs.data.request.Request;
import lithium.service.casino.provider.sgs.data.response.Extinfo;
import lithium.service.casino.provider.sgs.data.response.LoginResult;
import lithium.service.casino.provider.sgs.data.response.MethodResponse;
import lithium.service.casino.provider.sgs.data.response.Response;
import lithium.service.casino.provider.sgs.data.response.Result;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginService {
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	protected SGSService sgsService;
	
	public Response<? extends Result<? extends Extinfo>> login(Request request, User user) {
		
		AccountInfoResponse accountInfoResponse = null;
		try {
			accountInfoResponse = sgsService.getCasinoService().handleAccountInfoRequest(user.getDomain().getName()+"/"+user.getUsername(), user.getApiToken());
		} catch (Exception e) {
			log.error("Unable to get player info. User:" + user + " | Response: " + accountInfoResponse, e);
			return sgsService.createErrorResponse(request, ErrorCodes.LOGIN_VALIDATION_FAILED);
		}
		
		LoginResult<Extinfo> loginResult = new LoginResult<>(request.getMethodCall().getCall().getSeq(), request.getMethodCall().getCall().getToken(), user.getUsername(),  accountInfoResponse.getCurrency(), "", "", accountInfoResponse.getBalanceCents()+"", "", "", "", "", "");
		loginResult.setExtinfo(new Extinfo());
		MethodResponse<LoginResult<Extinfo>> methodResponse = new MethodResponse<>(MessageTypes.LOGIN.getTypeName(), new Date(), loginResult);
		Response<LoginResult<Extinfo>> response = new Response<>(methodResponse);
		
		return response;
	}
}
