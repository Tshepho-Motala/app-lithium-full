package lithium.service.cashier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.Image;
import lithium.service.cashier.data.repositories.ImageRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageService {
	@Autowired
	private ImageRepository imageRepository;
	
	public Image create(String filename, String filetype, byte[] imageData) {
		Image image = imageRepository.save(
			Image.builder()
			.filename(filename)
			.filetype(filetype)
			.filesize((long)imageData.length)
			.base64(imageData)
			.build()
		);
		log.info("Saved new Image : "+image);
		return image;
	}
	
	public Image update(Image image) {
		return imageRepository.save(image);
	}
	
	public Image save(Image image) {
		return imageRepository.save(image);
	}
}
