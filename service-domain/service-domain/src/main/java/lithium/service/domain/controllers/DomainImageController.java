package lithium.service.domain.controllers;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.DomainImage;
import lithium.service.domain.data.repositories.DomainImageRepository;

@RestController
@RequestMapping("/domain/{domainId}/image/{name}")
public class DomainImageController {

	@Autowired
	private DomainImageRepository repository;
	
	@GetMapping
	public @ResponseBody void get(@PathVariable("domainId") Domain domain, @PathVariable("name") String name, HttpServletResponse response) throws IOException {
		DomainImage image = repository.findByDomainIdAndName(domain.getId(), name.toLowerCase());
		if (image != null) {
			response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			OutputStream os = response.getOutputStream();
			os.write(image.getPicture());
			response.flushBuffer();
		}
	}

	@PostMapping
	public Response<String> save(@PathVariable("domainId") Domain domain, @PathVariable("name") String name, @RequestParam("file") MultipartFile file) throws Exception {
		DomainImage image = repository.findByDomainIdAndName(domain.getId(), name);
		if (image != null) {
			image.setPicture(file.getBytes());
			repository.save(image);
		} else {
			image = DomainImage.builder()
			.name(name.toLowerCase())
			.picture(file.getBytes())
			.domain(domain)
			.build();
			repository.save(image);
		}
		return Response.<String>builder().status(Status.OK).build();
	}
	
	@DeleteMapping
	public void delete(@PathVariable("domainId") Domain domain, @PathVariable("name") String name) throws Exception {
		repository.delete(repository.findByDomainIdAndName(domain.getId(), name.toLowerCase()));
	}
}