package lithium.service.games.services;

import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.games.data.entities.Graphic;
import lithium.service.games.data.entities.GraphicFunction;
import lithium.service.games.data.repositories.GraphicFunctionRepository;
import lithium.service.games.data.repositories.GraphicRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GraphicsService {
	@Autowired
	private GraphicFunctionRepository graphicFunctionRepo;
	@Autowired
	private GraphicRepository graphicRepo;

	public static final String GRAPHIC_FUNCTION_CDN_EXTERNAL = "CDN_EXTERNAL";
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public Graphic saveGraphic(byte[] data) {
		String md5Hash = calculateMD5(data);
		
		//Find graphic if it is the same
		Iterable<Graphic> graphics = graphicRepo.findBySizeAndMd5HashAndDeletedFalse(data.length, md5Hash);
		Graphic graphic = null;
		for (Graphic tmpGraphic: graphics) {
			if (Arrays.equals(tmpGraphic.getImage(), data)) {
				graphic = tmpGraphic;
			}
		}
		//No matching graphics, make new one
		if(graphic == null) {
			graphic = Graphic.builder()
					.deleted(false)
					.image(data)
					.size(data.length)
					.md5Hash(md5Hash)
					.build();
			graphic = graphicRepo.save(graphic);
		}
		
		return graphic;
	}
	
	public GraphicFunction findOrCreateGraphicFunction(String graphicFunctionName) {
		GraphicFunction f = graphicFunctionRepo.findByName(graphicFunctionName);
		if(f == null) {
			f = GraphicFunction.builder()
					.name(graphicFunctionName)
					.enabled(true)
					.build();
			f = graphicFunctionRepo.save(f);
		}
		return f;
	}
	
	public boolean isGraphicContentEqual(Graphic g, byte[] data) {
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
	
	public String calculateMD5(byte[] data) {
		return Hex.encodeHexString(Md5Crypt.md5Crypt(data.clone(), "$1$Game0000").getBytes());
	}

}
