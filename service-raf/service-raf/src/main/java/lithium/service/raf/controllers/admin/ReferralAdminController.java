package lithium.service.raf.controllers.admin;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.raf.data.entities.Referral;
import lithium.service.raf.services.ReferralService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/referral")
@Slf4j
public class ReferralAdminController {
	@Autowired ReferralService service;
	
	@GetMapping("/findByPlayerGuid/{domainName}")
	public Response<Referral> findByPlayerGuid(@PathVariable("domainName") String domainName, @RequestParam("userName") String userName) {
		Referral referral = null;
		try {
			referral = service.findByPlayerGuid(domainName, userName);
			return Response.<Referral>builder().data(referral).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Referral>builder().data(referral).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/table/signups")
	public DataTableResponse<Referral> signupsTable(
		@RequestParam("domainName") String domainName,
		LithiumTokenUtil tokenUtil,
		DataTableRequest request
	) {
		Page<Referral> table = service.findByDomain(false, domainName,
				request.getSearchValue(), request.getPageRequest(), tokenUtil);
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/table/conversions")
	public DataTableResponse<Referral> conversionsTable(
		@RequestParam("domainName") String domainName,
		LithiumTokenUtil tokenUtil,
		DataTableRequest request
	) {
		Page<Referral> table = service.findByDomain(true, domainName,
				request.getSearchValue(), request.getPageRequest(), tokenUtil);
		return new DataTableResponse<>(request, table);
	}
}
