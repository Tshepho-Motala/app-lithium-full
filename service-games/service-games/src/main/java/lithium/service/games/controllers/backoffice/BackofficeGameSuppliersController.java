package lithium.service.games.controllers.backoffice;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.games.data.entities.GameSupplier;
import lithium.service.games.services.GameSupplierService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backoffice/{domainName}/game-suppliers")
@Slf4j
public class BackofficeGameSuppliersController {
	@Autowired private GameSupplierService service;

	@GetMapping("/find-by-domain")
	public Response<List<GameSupplier>> findByDomain(@PathVariable("domainName") String domainName) {
		return Response.<List<GameSupplier>>builder().data(service.findByDomain(domainName)).status(Response.Status.OK)
				.build();
	}

	@GetMapping("/table")
	public DataTableResponse<GameSupplier> table(@PathVariable("domainName") String domainName,
			@RequestParam(name = "deleted", required = false, defaultValue = "false") Boolean deleted,
			DataTableRequest request) {
		Page<GameSupplier> result = service.findByDomain(domainName, deleted, request.getSearchValue(),
				request.getPageRequest());
		return new DataTableResponse<>(request, result);
	}

	@PostMapping("/add")
	public Response<GameSupplier> add(@PathVariable("domainName") String domainName,
	        @RequestBody GameSupplier gameSupplier, LithiumTokenUtil tokenUtil) {
		try {
			gameSupplier = service.add(domainName, gameSupplier, tokenUtil.guid());
			return Response.<GameSupplier>builder().data(gameSupplier).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to add GameSupplier [domainName="+domainName+", gameSupplier="+gameSupplier
					+", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
			return Response.<GameSupplier>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();
		}
	}
}
