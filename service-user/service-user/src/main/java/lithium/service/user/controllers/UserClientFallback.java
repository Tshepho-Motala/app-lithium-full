package lithium.service.user.controllers;

import java.util.List;
import java.util.Map;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.UserClient;
import lithium.service.user.client.objects.AuthRequest;
import lithium.service.user.client.objects.User;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
class UserClientFallback implements UserClient {
	@Override
	public Response<User> auth(String domain, String username, String password, String ipAddress, String userAgent, Map<String, String> parameters, String locale) {
		log.warn("fallback called for auth.");
		return Response.<User>builder().status(Response.Status.SERVICE_UNAVAILABLE).build();
	}

  @Override
  public Response<User> auth(AuthRequest request) {
	  log.warn("fallback called for auth.");
    return Response.<User>builder().status(Response.Status.SERVICE_UNAVAILABLE).build();
  }

  @Override
	public Response<User> user(String domain, String username, Map<String, String> parameters) {
		log.warn("fallback called for user.");
		return Response.<User>builder().status(Response.Status.SERVICE_UNAVAILABLE).build();
	}
	@Override
	public Response<User> create(User user) {
		log.warn("fallback called for create.");
		return Response.<User>builder().status(Response.Status.SERVICE_UNAVAILABLE).build();
	}
	@Override
	public DataTableResponse<User> table(String domainName, String drawEcho, Long start, Long length) {
		log.warn("fallback called for table.");
		return null;
	}

  @Override
  public Response<List<User>> findByGuids(String domainName, List<String> userGuids) {
    log.warn("fallback called for findByGuids.");
    return null;
  }


  @Override
	public Response<String> delete(String domain, Map<String, String> extraParameters) {
		log.warn("fallback called for delete.");
		return Response.<String>builder().status(Response.Status.SERVICE_UNAVAILABLE).build();
	}

	@Override
	public DataTableResponse<User> table(String domainName, String drawEcho, Long start, Long length, String[] domainNames, Boolean players, String labelNameString, String labelValueString) {
		log.warn("fallback called for table with lots of parameters.");
		return null;
	}
}
