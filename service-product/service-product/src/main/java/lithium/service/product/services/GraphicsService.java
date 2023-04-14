package lithium.service.product.services;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.product.data.entities.Domain;
import lithium.service.product.data.entities.Graphic;
import lithium.service.product.data.entities.GraphicFunction;
import lithium.service.product.data.entities.Product;
import lithium.service.product.data.entities.ProductGraphic;
import lithium.service.product.data.objects.ProductGraphicBasic;
import lithium.service.product.data.repositories.GraphicFunctionRepository;
import lithium.service.product.data.repositories.GraphicRepository;
import lithium.service.product.data.repositories.ProductGraphicRepository;
import lithium.service.product.data.repositories.ProductRepository;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GraphicsService {
	@Autowired GraphicFunctionRepository graphicFunctionRepository;
	@Autowired ProductGraphicRepository productGraphicRepository;
	@Autowired ProductRepository productRepository;
	@Autowired GraphicRepository graphicRepository;
	
	@Autowired DomainService domainService;
	
	@Autowired ApplicationContext appContext;
	
	@Value("${lithium.service.product.blank-image-url:classpath:/images/200x200_blank.png}")
	private String blankImageUrl;
	
	private GraphicFunction findGraphicFunctionByName(String graphicFunction) {
		return graphicFunctionRepository.findByName(graphicFunction);
	}
	public ProductGraphic findProductGraphic(String productGuid, String domainName, String graphicFunction) {
		Domain domain = domainService.findOrCreate(domainName);
		Product product = productRepository.findByGuidAndDomain(productGuid, domain);
		return findProductGraphic(product, graphicFunction);
	}
	public ProductGraphic findProductGraphic(Product product, String graphicFunction) {
		GraphicFunction gf = findGraphicFunctionByName(graphicFunction);
		return productGraphicRepository.findByProductAndGraphicFunctionIdAndEnabledTrueAndDeletedFalse(product, gf.getId());
	}
	public byte[] getBlankImage() {
		return getImage(blankImageUrl);
	}
	private byte[] getImage(String url) {
		Resource r = appContext.getResource(url);
		byte[] blankImage = null;
		if (r.exists()) {
			try {
				blankImage = IOUtils.toByteArray(r.getInputStream());
			} catch (IOException e) {
				log.warn("Unable to read image from: " + r.getDescription());
			}
		}
		return blankImage;
	}
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	private Graphic saveGraphic(byte[] data) {
		String md5Hash = calculateMD5(data);
		
		//Find graphic if it is the same
		Iterable<Graphic> graphics = graphicRepository.findBySizeAndMd5HashAndDeletedFalse(data.length, md5Hash);
		Graphic graphic = null;
		for (Graphic tmpGraphic: graphics) {
			if (Arrays.equals(tmpGraphic.getImage(), data)) {
				graphic = tmpGraphic;
			}
		}
		//No matching graphics, make new one
		if (graphic == null) {
			graphic = Graphic.builder()
			.deleted(false)
			.image(data)
			.size(data.length)
			.md5Hash(md5Hash)
			.build();
			graphic = graphicRepository.save(graphic);
		}
		
		return graphic;
	}
	
	public Response<ProductGraphic> saveProductGraphic(ProductGraphicBasic productGraphicBasic) throws Exception {
		Product product = productRepository.findOne(productGraphicBasic.getProductId());
		
		if (product == null || !(product.getDomain().getName().contentEquals(productGraphicBasic.getDomainName()))) {
			return Response.<ProductGraphic>builder().status(Status.NOT_FOUND).build();
		}
		GraphicFunction graphicsFunction = findOrCreateGraphicFunction(productGraphicBasic.getGraphicFunctionName());
		
		ProductGraphic productGraphic = productGraphicRepository.findByProductAndGraphicFunctionIdAndEnabledTrueAndDeletedFalse(product, graphicsFunction.getId());
		
		//Check if image is really different
		if (productGraphic != null) {
			if (isGraphicContentEqual(productGraphic.getGraphic(), productGraphicBasic.getImage())) {
				//Handle case where only enable and deleted got changed
				if (productGraphic.getProduct().getId() == productGraphicBasic.getProductId()) {
					productGraphic.setDeleted(productGraphicBasic.isDeleted());
					productGraphic.setEnabled(productGraphicBasic.isEnabled());
					productGraphic = productGraphicRepository.save(productGraphic);
				} else {
					productGraphic = productGraphic.toBuilder()
					.id(null)
					.product(product)
					.deleted(productGraphicBasic.isDeleted())
					.enabled(productGraphicBasic.isEnabled())
					.build();
					productGraphic = productGraphicRepository.save(productGraphic);
				}
				return Response.<ProductGraphic>builder().data(productGraphic).status(Status.OK).build();
			} else {
				//Found a graphic but it is different
				if (productGraphic.getProduct().getId() == productGraphicBasic.getProductId()) {
					productGraphic.setDeleted(true);
					productGraphic.setEnabled(false);
					productGraphic = productGraphicRepository.save(productGraphic);
				}
			}
		}
		
		//Nothing found, write new product graphic
		Graphic graphic = saveGraphic(productGraphicBasic.getImage());
		productGraphic = ProductGraphic.builder()
				.deleted(productGraphicBasic.isDeleted())
				.enabled(productGraphicBasic.isEnabled())
				.product(product)
				.graphic(graphic)
				.graphicFunction(graphicsFunction)
				.build();
		productGraphic = productGraphicRepository.save(productGraphic);
		
		if (!graphicSanityCheck(productGraphic)) {
			return Response.<ProductGraphic>builder().status(Status.CONFLICT).build();
		}
		return Response.<ProductGraphic>builder().data(productGraphic).status(Status.OK).build();
	}
	
	public Response<ProductGraphic> removeProductGraphic(Long productId, String graphicFunction) {
		Product product = productRepository.findOne(productId);
		GraphicFunction graphicsFunction = findOrCreateGraphicFunction(graphicFunction);
		ProductGraphic productGraphic = productGraphicRepository.findByProductAndGraphicFunctionIdAndEnabledTrueAndDeletedFalse(product, graphicsFunction.getId());
		
		if (productGraphic != null) {
			productGraphic.setDeleted(true);
			productGraphic.setEnabled(false);
			productGraphicRepository.save(productGraphic);
		}
		
		return Response.<ProductGraphic>builder().status(Status.OK).data(productGraphic).build();
	}
	
	@Synchronized
	private boolean graphicSanityCheck(ProductGraphic pg) {
		try {
			productGraphicRepository.findByProductAndGraphicFunctionIdAndEnabledTrueAndDeletedFalse(pg.getProduct(), pg.getGraphicFunction().getId());
			return true;
		} catch (Exception e) {
			log.error("Problem returning saved graphic, possible duplicate, removing", e);
			pg.setDeleted(true);
			pg.setEnabled(false);
			productGraphicRepository.save(pg);
		}
		return false;
	}
	
	private GraphicFunction findOrCreateGraphicFunction(String graphicFunctionName) {
		GraphicFunction f = graphicFunctionRepository.findByName(graphicFunctionName);
		if(f == null) {
			f = GraphicFunction.builder()
			.name(graphicFunctionName)
			.enabled(true)
			.build();
			f = graphicFunctionRepository.save(f);
		}
		return f;
	}
	
	private boolean isGraphicContentEqual(Graphic g, byte[] data) {
		if (g.getSize() != data.length) {
			log.debug("Size difference, return false.");
			return false;
		}
		String md5Hash = calculateMD5(data);
		if (!g.getMd5Hash().equals(md5Hash)) {
			log.debug("md5hash differs, return false.");
			log.debug(g.getMd5Hash()+" vs "+md5Hash);
			return false;
		}
		if (!Arrays.equals(g.getImage(), data)) {
			log.debug("Arrays differ, return false.");
			return false;
		}
		log.debug("Content is equal, return true.");
		return true;
	}
	
	private String calculateMD5(byte[] data) {
		return Hex.encodeHexString(Md5Crypt.md5Crypt(data.clone(), "$1$Game0000").getBytes());
	}
}