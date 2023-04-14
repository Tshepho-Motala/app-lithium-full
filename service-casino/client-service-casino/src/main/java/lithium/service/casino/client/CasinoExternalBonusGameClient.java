package lithium.service.casino.client;

import lithium.service.Response;
import lithium.service.casino.client.objects.request.AwardBonusRequest;
import lithium.service.casino.client.objects.request.CancelBonusRequest;
import lithium.service.casino.client.objects.request.CheckBonusRequest;
import lithium.service.casino.client.objects.request.GameBetConfigRequest;
import lithium.service.casino.client.objects.request.GetBonusInfoRequest;
import lithium.service.casino.client.objects.response.AwardBonusResponse;
import lithium.service.casino.client.objects.response.CancelBonusResponse;
import lithium.service.casino.client.objects.response.CheckBonusResponse;
import lithium.service.casino.client.objects.response.GameBetConfigResponse;
import lithium.service.casino.client.objects.response.GetBonusInfoResponse;
import lithium.service.casino.client.objects.response.UpdateBonusIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name="service-casino", path="/casino/externalBonusGame")
public interface CasinoExternalBonusGameClient {

		@RequestMapping(method = RequestMethod.GET, value = "/generateExternalBonusLink")
		public Response<String> generateLink(
				@RequestParam("playerGuid") String playerGuid, @RequestParam("campaignId") Long campaignId
		) throws Exception;

		//TODO: Add some retrieval mechanism for previously generated links. This will be nice to have if link is not stored in bonus allocation.
}
