package lithium.service.games.controllers.backoffice;

import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.games.data.entities.GameSupplier;
import lithium.service.games.services.GameSupplierService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/{domainName}/game-supplier/{id}")
@Slf4j
public class BackofficeGameSupplierController {
	@Autowired private GameSupplierService service;

	@GetMapping
	public Response<GameSupplier> get(@PathVariable("domainName") String domainName,
	        @PathVariable("id") GameSupplier gameSupplier, LithiumTokenUtil tokenUtil) {
		try {
			DomainValidationUtil.validate(domainName, gameSupplier.getDomain().getName());
			if (gameSupplier.getDeleted().booleanValue())
				throw new Status500InternalServerErrorException("Game supplier has been deleted");
			return Response.<GameSupplier>builder().data(gameSupplier).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to retrieve GameSupplier [domainName="+domainName+", gameSupplier="+gameSupplier
					+", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
			return Response.<GameSupplier>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();
		}
	}

	@PostMapping("/update")
	public Response<GameSupplier> update(@PathVariable("domainName") String domainName,
	        @PathVariable("id") GameSupplier gameSupplier, @RequestBody GameSupplier update,
	        LithiumTokenUtil tokenUtil) {
		try {
			DomainValidationUtil.validate(domainName, gameSupplier.getDomain().getName());
			service.update(domainName, gameSupplier, update, tokenUtil.guid());
			return Response.<GameSupplier>builder().data(gameSupplier).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to update GameSupplier [domainName="+domainName+", gameSupplier="+gameSupplier
					+", update="+update+", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
			return Response.<GameSupplier>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();
		}
	}

	@GetMapping("/changelogs")
	private @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
		return service.changeLogs(id, p);
	}
}
