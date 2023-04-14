package lithium.service.raf.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.raf.data.entities.Click;
import lithium.service.raf.services.ClickService;
import lithium.tokens.LithiumTokenUtil;

@RestController
@RequestMapping("/admin/click")
public class ClickAdminController {
	@Autowired ClickService service;
	
	@GetMapping("/table")
	public DataTableResponse<Click> clicksTable(
		@RequestParam("domainName") String domainName,
		LithiumTokenUtil tokenUtil,
		DataTableRequest request
	) {
		Page<Click> table = service.findByDomain(domainName,
				request.getSearchValue(), request.getPageRequest(), tokenUtil);
		return new DataTableResponse<>(request, table);
	}
}
