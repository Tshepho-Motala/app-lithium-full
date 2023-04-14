package lithium.service.casino.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

@FeignClient(name="service-casino", path="/casino/frb")
public interface CasinoFrbClient {

		@RequestMapping(value = "/awardbonus")
		public @ResponseBody AwardBonusResponse handleAwardBonus(
			@RequestBody AwardBonusRequest request
		) throws Exception;
		
		@RequestMapping(value = "/checkbonus")
		public @ResponseBody CheckBonusResponse handleCheckBonus(
			@RequestBody CheckBonusRequest request
		) throws Exception;
		
		@RequestMapping(value = "/cancelbonus")
		public @ResponseBody CancelBonusResponse handleCancelBonus(
			@RequestBody CancelBonusRequest request
		) throws Exception;

		@RequestMapping(value = "/getbonusinfo")
		public @ResponseBody GetBonusInfoResponse handleGetBonusInfo(
			@RequestBody GetBonusInfoRequest request
		) throws Exception;
		
		@RequestMapping(value = "/gamebetconfig")
		public @ResponseBody GameBetConfigResponse handleGetGameBetConfig(
			@RequestBody GameBetConfigRequest request
		) throws Exception;
		
		@RequestMapping(value = "/updateexternalid")
		public void updateExternalBonusId(
			@RequestBody UpdateBonusIdResponse request
		);
}