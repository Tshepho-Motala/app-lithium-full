package lithium.service.product.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.product.data.entities.Product;
import lithium.service.product.data.entities.ProductGraphic;
import lithium.service.product.services.GraphicsService;
import lithium.service.product.services.ProductService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {
	@Autowired ProductService productService;
	@Autowired GraphicsService graphicsService;
	
	@GetMapping("/{domainName}")
	public Response<List<Product>> listByDomain(
		@PathVariable String domainName,
		HttpServletRequest servletRequest
	) throws Exception {
		log.info("Product List for : "+domainName);
		List<Product> list = productService.listByDomain(domainName, servletRequest.getRemoteAddr());
		log.info("List : "+list);
		return Response.<List<Product>>builder().data(list).status(Status.OK).build();
	}
	
	//Handy image streaming examples at http://www.baeldung.com/spring-mvc-image-media-data
	@RequestMapping(value = "/graphic/view/{domainName}", method = RequestMethod.GET, produces=MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<byte[]> getImageAsResponseEntity(
		@PathVariable("domainName") String domainName,
		@RequestParam(name="guid", required=true) String productGuid,
		@RequestParam(name="function", required=false, defaultValue="default") String graphicFunction
	) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).noTransform().cachePublic().getHeaderValue());
		headers.setContentType(MediaType.IMAGE_PNG);
		
		ProductGraphic pg = graphicsService.findProductGraphic(productGuid, domainName, graphicFunction);
		if (pg != null) {
			log.debug("found graphic in DB returning it.");
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(pg.getGraphic().getImage(), headers, HttpStatus.OK);
			return responseEntity;
		}
		
		byte[] blankImage = graphicsService.getBlankImage();
		if (blankImage != null) {
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(blankImage, headers, HttpStatus.OK);
			return responseEntity;
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@GetMapping("/find")
	public Response<Product> findByDomainAndGuid(
		@RequestParam(name="domainName", required=true) String domainName,
		@RequestParam(name="guid", required=true) String guid,
		@RequestParam(name="ipAddr", required=true) String ipAddr,
		HttpServletRequest servletRequest
	) throws Exception {
//		ipAddr = "196.22.242.140"; // SA
		if ((ipAddr!=null) && (ipAddr.contains(","))) {
			ipAddr = ipAddr.substring(0, ipAddr.indexOf(","));
		}
		log.info("Product for : "+domainName+" :: "+guid+" :: "+ipAddr);
		Product product = productService.find(domainName, guid, ipAddr);
		log.info("Product : "+product);
		return Response.<Product>builder().data(product).status(Status.OK).build();
	}
	
	@GetMapping("/{domainName}/table")
	public DataTableResponse<Product> productTable(
		@PathVariable String domainName,
		DataTableRequest request,
		HttpServletRequest servletRequest
	) throws Exception {
		log.debug("Received product list request from "+servletRequest.getRemoteAddr());
		Page<Product> table = productService.findByDomains(Arrays.asList(domainName), request.getSearchValue(), true, servletRequest.getRemoteAddr(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
}