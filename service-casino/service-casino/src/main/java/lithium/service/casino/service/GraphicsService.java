package lithium.service.casino.service;

import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.casino.data.entities.Graphic;
import lithium.service.casino.data.repositories.GraphicRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GraphicsService {
	@Autowired
	private GraphicRepository graphicRepo;
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public Graphic saveGraphic(byte[] data, String fileType) {
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
					.fileType(fileType)
					.build();
			graphic = graphicRepo.save(graphic);
		}
		
		return graphic;
	}
	
	public boolean isGraphicContentEqual(Graphic g, byte[] data) {
		
		if(g.getSize() != data.length) return false;
		
		String md5Hash = calculateMD5(data);
		if(g.getMd5Hash() != calculateMD5(data)) return false;
		
		if(!Arrays.equals(g.getImage(), data)) return false;
		
		return true;
	}
	
	public String calculateMD5(byte[] data) {
		return Hex.encodeHexString(Md5Crypt.md5Crypt(data.clone(), "$1$Game0000").getBytes());
	}

}
