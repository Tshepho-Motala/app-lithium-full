package lithium.service.product.controllers.admin;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.product.data.entities.LocalCurrency;
import lithium.service.product.data.entities.Payout;
import lithium.service.product.data.entities.Product;
import lithium.service.product.data.entities.ProductGraphic;
import lithium.service.product.data.objects.ProductGraphicBasic;
import lithium.service.product.services.GraphicsService;
import lithium.service.product.services.ProductService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/product/admin")
public class AdminProductController {
	@Autowired ProductService productService;
	@Autowired GraphicsService graphicsService;
	
	@GetMapping("/table")
	public DataTableResponse<Product> productTable(@RequestParam("domains") List<String> domains, DataTableRequest request) throws Exception {
		log.trace("catalogTable");
		domains.removeIf(d -> d.isEmpty());
		Page<Product> table = productService.findByDomains(domains, request.getSearchValue(), null, "", request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	@GetMapping("/localcurrency/{id}/table")
	public DataTableResponse<LocalCurrency> localCurrencyTable(@PathVariable("id") Product product, DataTableRequest request) {
		log.trace("localCurrencyTable");
		Page<LocalCurrency> table = productService.findLocalCurrencies(product, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	@GetMapping("/payouts/{id}/table")
	public DataTableResponse<Payout> payoutTable(@PathVariable("id") Product product, DataTableRequest request) {
		log.trace("payoutTable");
		Page<Payout> table = productService.findPayouts(product, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@PostMapping("/add")
	public Response<Product> add(@RequestBody Product productBasic) {
		log.info("Creating Product : "+productBasic);
		Product product = productService.add(productBasic);
		return Response.<Product>builder().data(product).status(Status.OK).build();
	}
	@PostMapping("/edit")
	public Response<Product> edit(@RequestBody Product productBasic) {
		log.info("Saving Product : "+productBasic);
		Product product = productService.edit(productBasic);
		return Response.<Product>builder().data(product).status(Status.OK).build();
	}
	
	@PostMapping("/add/localcurrency")
	public Response<LocalCurrency> addLocalCurrency(@RequestBody LocalCurrency localCurrency) {
		log.info("Creating LocalCurrency : "+localCurrency);
		localCurrency = productService.addLocalCurrency(localCurrency);
		return Response.<LocalCurrency>builder().data(localCurrency).status(Status.OK).build();
	}
	@PostMapping("/edit/localcurrency")
	public Response<LocalCurrency> editLocalCurrency(@RequestBody LocalCurrency localCurrency) {
		log.info("Editing LocalCurrency : "+localCurrency);
		localCurrency = productService.editLocalCurrency(localCurrency);
		return Response.<LocalCurrency>builder().data(localCurrency).status(Status.OK).build();
	}
	@DeleteMapping("/remove/localcurrency/{id}")
	public Response<Void> removeLocalCurrency(@PathVariable("id") LocalCurrency localCurrency) {
		log.info("Deleting LocalCurrency : "+localCurrency);
		productService.removeLocalCurrency(localCurrency);
		return Response.<Void>builder().status(Status.OK).build();
	}
	@PostMapping("/add/payout")
	public Response<Payout> addPayout(@RequestBody Payout payout) {
		log.info("Creating Payout : "+payout);
		payout = productService.addPayout(payout);
		return Response.<Payout>builder().data(payout).status(Status.OK).build();
	}
	@PostMapping("/edit/payout")
	public Response<Payout> editPayout(@RequestBody Payout payout) {
		log.info("Editing Payout : "+payout);
		payout = productService.editPayout(payout);
		return Response.<Payout>builder().data(payout).status(Status.OK).build();
	}
	@DeleteMapping("/remove/payout/{id}")
	public Response<Void> removePayout(@PathVariable("id") Payout payout) {
		log.info("Deleting Payout : "+payout);
		productService.removePayout(payout);
		return Response.<Void>builder().status(Status.OK).build();
	}
	
	@GetMapping("/find/{id}")
	public Response<Product> find(@PathVariable("id") Product product) {
		return Response.<Product>builder().data(product).status(Status.OK).build();
	}
	
	@PostMapping("/enable/{id}")
	public Response<Product> enable(
		@PathVariable("id") Product product
	) {
		product.setEnabled(!product.getEnabled());
		product = productService.save(product);
		return Response.<Product>builder().data(product).status(Status.OK).build();
	}
	
	@PostMapping(value="/graphic/edit", consumes="multipart/form-data")
	public Response<ProductGraphic> editGraphic(
		@RequestParam("productId") Long productId,
		@RequestParam("graphicFunctionName") String graphicFunctionName,
		@RequestParam("deleted") boolean deleted, 
		@RequestParam("enabled") boolean enabled,
		@RequestParam("domainName") String domainName,
		@RequestParam("image") MultipartFile file
	) throws Exception {
//		Game g = gameRepo.findOne(gameId);
		
		ProductGraphicBasic productGraphicBasic = ProductGraphicBasic.builder()
				.deleted(deleted)
				.enabled(enabled)
				.productId(productId)
				.graphicFunctionName(graphicFunctionName.split(",")[0])
				.domainName(domainName.split(",")[0])
				.image(file.getBytes())
				.build();
		
		return saveGameGraphic(productGraphicBasic, null);
	}
	
	@RequestMapping(value="/graphic/remove/{productId}", method=RequestMethod.POST)
	public Response<ProductGraphic> removeGraphic(
		@PathVariable("productId") Long productId,
		@RequestParam(name="graphicFunction", required=false, defaultValue="default") String graphicFunction
	) {
		return graphicsService.removeProductGraphic(productId, graphicFunction);
	}
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	@GetMapping("/graphic/save")
	public Response<ProductGraphic> saveGameGraphic(
		@RequestBody @Valid ProductGraphicBasic productGraphicBasic,
		BindingResult br
	) throws Exception {
		Response<ProductGraphic> gameGraphicResponse = graphicsService.saveProductGraphic(productGraphicBasic);
		
		return gameGraphicResponse;
	}
}