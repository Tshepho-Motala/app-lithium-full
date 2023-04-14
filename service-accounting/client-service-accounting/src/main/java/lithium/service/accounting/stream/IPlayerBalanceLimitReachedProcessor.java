package lithium.service.accounting.stream;

import lithium.service.accounting.objects.PlayerBalanceLimitReachedEvent;

public interface IPlayerBalanceLimitReachedProcessor {

	public void onPlayerBalanceLimitReached(final PlayerBalanceLimitReachedEvent request) throws Exception;
}
