package lithium.service.casino.test;

import lithium.service.Response;
import lithium.service.casino.client.objects.request.BetRequest;

import java.util.Collections;
import java.util.Map;

public class CasinoTestData {

    public static Response<Map<String, Long>> balances(long mainBalance) {
        Map<String, Long> balances = Collections.singletonMap("PLAYER_BALANCE", mainBalance);
        return Response.<Map<String, Long>>builder().data(balances).status(Response.Status.OK).build();
    }

    public static BetRequest createBetRequest(String domainName, String currency, String userGuid) {
        BetRequest betRequest = new BetRequest();
        betRequest.setDomainName(domainName);
        betRequest.setCurrencyCode(currency);
        betRequest.setProviderGuid("service-casino-provider-someprovider");
        betRequest.setGameGuid("service-casino-provider-someprovider-111");
        betRequest.setUserGuid(userGuid);
        return betRequest;
    }
}
