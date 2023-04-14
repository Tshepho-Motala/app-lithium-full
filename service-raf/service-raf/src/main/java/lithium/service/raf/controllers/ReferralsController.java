package lithium.service.raf.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.raf.data.entities.Referral;
import lithium.service.raf.services.ReferralService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/referrals")
@Slf4j
public class ReferralsController {
	@Autowired ReferralService service;
	
	@GetMapping(value = "/table")
	public DataTableResponse<Referral> findByReferrerTable(
		@RequestParam(name="guid") String playerGuid,
		DataTableRequest request,
		Principal principal
	) throws Exception {
		log.info("findByReferrerTable : "+playerGuid);
		return new DataTableResponse<>(request, service.findByReferrer(playerGuid, request));
	}
}
