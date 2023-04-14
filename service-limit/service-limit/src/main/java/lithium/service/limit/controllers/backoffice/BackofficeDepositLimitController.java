package lithium.service.limit.controllers.backoffice;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status100InvalidInputDataException;
import lithium.service.limit.client.exceptions.Status480PendingDepositLimitException;
import lithium.service.limit.client.exceptions.Status481DomainDepositLimitDisabledException;
import lithium.service.limit.client.exceptions.Status499EmptySupposedDepositLimitException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.data.entities.PlayerLimit;
import lithium.service.limit.services.DepositLimitService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/backoffice/depositlimit/{domainName}")
public class BackofficeDepositLimitController {
	@Autowired DepositLimitService depositLimitService;

	@GetMapping
	public Response<List<PlayerLimit>> get(
		@PathVariable("domainName") String domainName,
		@RequestParam("playerGuid") String playerGuid,
		Locale locale
	) {
		log.debug("Backoffice request for : "+playerGuid+" deposit limits.");
		try {
			DomainValidationUtil.validate(domainName, playerGuid.split("/")[0]);
			return Response.<List<PlayerLimit>>builder().data(depositLimitService.findAll(playerGuid, locale)).status(Response.Status.OK).build();
		} catch (Status550ServiceDomainClientException | Status481DomainDepositLimitDisabledException e) {
			return Response.<List<PlayerLimit>>builder().status(Response.Status.CUSTOM.fromId(e.getCode())).message(e.getMessage()).build();
		} catch (Status500InternalServerErrorException e) {
			return Response.<List<PlayerLimit>>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}

	@GetMapping(path = "/pending")
	public Response<List<PlayerLimit>> getPending(
		@PathVariable("domainName") String domainName,
		@RequestParam("playerGuid") String playerGuid,
		Locale locale
	) {
		log.debug("Backoffice request for : "+playerGuid+" pending deposit limits.");
		try {
			DomainValidationUtil.validate(domainName, playerGuid.split("/")[0]);
			return Response.<List<PlayerLimit>>builder().data(depositLimitService.findAllPending(playerGuid, locale)).status(Response.Status.OK).build();
		} catch (Status550ServiceDomainClientException | Status481DomainDepositLimitDisabledException e) {
			return Response.<List<PlayerLimit>>builder().status(Response.Status.CUSTOM.fromId(e.getCode())).message(e.getMessage()).build();
		} catch (Status500InternalServerErrorException e) {
			return Response.<List<PlayerLimit>>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	@GetMapping(path = "/supposed")
	public Response<List<PlayerLimit>> getSupposed(
			@PathVariable("domainName") String domainName,
			@RequestParam("playerGuid") String playerGuid,
			Locale locale
	) {
		log.debug("Backoffice request for : "+playerGuid+" supposed deposit limits.");
		try {
			DomainValidationUtil.validate(domainName, playerGuid.split("/")[0]);
			return Response.<List<PlayerLimit>>builder().data(depositLimitService.findAllSupposed(playerGuid, locale)).status(Response.Status.OK).build();
		} catch (Status550ServiceDomainClientException | Status481DomainDepositLimitDisabledException e) {
			return Response.<List<PlayerLimit>>builder().status(Response.Status.CUSTOM.fromId(e.getCode())).message(e.getMessage()).build();
		} catch (Status500InternalServerErrorException e) {
			return Response.<List<PlayerLimit>>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	@PostMapping(path = "/supposed")
	public Response<List<PlayerLimit>> applySupposed(
			@PathVariable("domainName") String domainName,
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam(name="granularity") Integer granularity,
			LithiumTokenUtil token,
			Locale locale
	)  {
		log.debug("Backoffice request for apply : "+playerGuid+" supposed deposit limits.");
		try {
			DomainValidationUtil.validate(domainName, playerGuid.split("/")[0]);
			depositLimitService.proceedSupposedLimit(playerGuid, Granularity.fromGranularity(granularity),true, locale, token);
		} catch (Status500LimitInternalSystemClientException | Status499EmptySupposedDepositLimitException e) {
			return Response.<List<PlayerLimit>>builder().status(Response.Status.CUSTOM.fromId(e.getCode())).message(e.getMessage()).build();
		} catch (Status500InternalServerErrorException e) {
			return Response.<List<PlayerLimit>>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
		return Response.<List<PlayerLimit>>builder().status(Response.Status.OK).build();
	}

	@DeleteMapping("/pending")
	public void removePending(
		@PathVariable("domainName") String domainName,
		@RequestParam(name="playerGuid") String playerGuid,
		@RequestParam(name="granularity") Granularity granularity,
		LithiumTokenUtil tokenUtil
	) throws Status500InternalServerErrorException {
		log.trace("Admin request to remove pending deposit limits for "+playerGuid+".");
		DomainValidationUtil.validate(domainName, playerGuid.split("/")[0]);
		depositLimitService.removePending(playerGuid, granularity, tokenUtil.guid());
	}
	@DeleteMapping("/supposed")
	public void removeSupposed(
			@PathVariable("domainName") String domainName,
			@RequestParam(name="playerGuid") String playerGuid,
			@RequestParam(name="granularity") Granularity granularity,
			LithiumTokenUtil tokenUtil
	) throws Status500InternalServerErrorException {
		log.trace("Admin request to remove supposed deposit limits for "+playerGuid+".");
		DomainValidationUtil.validate(domainName, playerGuid.split("/")[0]);
		depositLimitService.removeSupposed(playerGuid, granularity, tokenUtil.guid());
	}

	@DeleteMapping
	public void remove(
		@PathVariable("domainName") String domainName,
		@RequestParam(name="playerGuid") String playerGuid,
		@RequestParam(name="granularity") Granularity granularity,
		LithiumTokenUtil tokenUtil
	) throws Status500InternalServerErrorException {
		log.trace("Admin request to remove deposit limits for "+playerGuid+".");
		DomainValidationUtil.validate(domainName, playerGuid.split("/")[0]);
		depositLimitService.remove(playerGuid, granularity, tokenUtil.guid());
	}

	@PostMapping
	public Response<PlayerLimit> save(
		@PathVariable("domainName") String domainName,
		@RequestParam(name="playerGuid") String playerGuid,
		@RequestParam(name="granularity") Granularity granularity,
		@RequestParam(name="amount") String amount,
		Locale locale,
		LithiumTokenUtil util
	) {
		try {
			DomainValidationUtil.validate(domainName, playerGuid.split("/")[0]);
			return Response.<PlayerLimit>builder().data(depositLimitService.saveBO(playerGuid, granularity, amount, util.guid(), locale, util)).status(Response.Status.OK).build();
		} catch (Status100InvalidInputDataException | Status500LimitInternalSystemClientException | Status550ServiceDomainClientException | Status481DomainDepositLimitDisabledException | Status480PendingDepositLimitException e) {
			return Response.<PlayerLimit>builder().status(Response.Status.CUSTOM.fromId(e.getCode())).message(e.getMessage()).build();
		} catch (Status500InternalServerErrorException e) {
			return Response.<PlayerLimit>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
}
