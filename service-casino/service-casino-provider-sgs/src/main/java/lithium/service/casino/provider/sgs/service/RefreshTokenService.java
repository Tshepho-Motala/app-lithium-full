package lithium.service.casino.provider.sgs.service;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.casino.provider.sgs.data.MessageTypes;
import lithium.service.casino.provider.sgs.data.request.Request;
import lithium.service.casino.provider.sgs.data.response.Extinfo;
import lithium.service.casino.provider.sgs.data.response.MethodResponse;
import lithium.service.casino.provider.sgs.data.response.RefreshtokenResult;
import lithium.service.casino.provider.sgs.data.response.Response;
import lithium.service.casino.provider.sgs.data.response.Result;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefreshTokenService {
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	protected SGSService sgsService;
	
	public Response<? extends Result<? extends Extinfo>> refreshToken(Request request, User user) {
		//Just return current token for now
		
		RefreshtokenResult<Extinfo> refreshTokenResult = new RefreshtokenResult<>(request.getMethodCall().getCall().getSeq(), request.getMethodCall().getCall().getToken());
		refreshTokenResult.setExtinfo(new Extinfo());
		
		MethodResponse<RefreshtokenResult<Extinfo>> methodResponse = new MethodResponse<>(MessageTypes.REFRESH_TOKEN.getTypeName(), new Date(), refreshTokenResult);
		Response<RefreshtokenResult<Extinfo>> response = new Response<>(methodResponse);
		
		return response;
	}
}
