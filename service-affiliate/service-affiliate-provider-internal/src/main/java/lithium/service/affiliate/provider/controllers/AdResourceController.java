package lithium.service.affiliate.provider.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.affiliate.provider.data.entities.Ad;
import lithium.service.affiliate.provider.data.entities.AdResource;
import lithium.service.affiliate.provider.data.repositories.AdRepository;
import lithium.service.affiliate.provider.data.repositories.AdResourceRepository;

@RestController
@RequestMapping("/ads/{adId}/resources")
public class AdResourceController {
	
	@Autowired AdRepository adRepository;
	@Autowired AdResourceRepository adResourceRepository;

	@RequestMapping("/{filename:.+}")
	public @ResponseBody void resources(@PathVariable Long adId, @PathVariable String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Ad ad = adRepository.findOne(adId);
		if (ad == null) throw new Exception("Ad not found");
		
		AdResource resource = adResourceRepository.findByAdIdAndFilename(ad.getId(), filename);
		if (resource == null) throw new Exception("Resource not found");
		
		response.setContentType(resource.getContentType());
		
		IOUtils.copy(new ByteArrayInputStream(resource.getData()), response.getOutputStream());
	}
}
