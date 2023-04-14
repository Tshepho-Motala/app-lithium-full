package lithium.service.affiliate.provider.controllers;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lithium.service.affiliate.provider.data.entities.Ad;
import lithium.service.affiliate.provider.data.entities.AdResource;
import lithium.service.affiliate.provider.data.repositories.AdRepository;
import lithium.service.affiliate.provider.data.repositories.AdResourceRepository;
import lithium.service.stats.client.objects.StatEntry;
import lithium.service.stats.client.stream.StatsStream;

@Controller
@RequestMapping("/adserve")
public class AdServeController {
	
	@Autowired StatsStream statsStream;
	@Autowired AdRepository adRepository;
	@Autowired AdResourceRepository adResourceRepository;

	@RequestMapping("/ad/{adGuid}/resources/")
	public @ResponseBody void entryPoint(@PathVariable String adGuid, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Ad ad = adRepository.findByGuid(adGuid);
		if (ad == null) throw new Exception("Ad not found: " + adGuid);
		
		resources(adGuid, ad.getCurrent().getEntryPoint(), request, response);

		// FIXME: This would fail with a NPE if ever used due to not having a domain set. Commenting out for now.
		//statsStream.register(StatEntry.builder().name("stats.ad." + adGuid).ipAddress(request.getRemoteAddr()).userAgent(request.getHeader("User-Agent")).build());
	}
	
	@RequestMapping("/ad/{adGuid}/resources/{filename:.+}")
	public @ResponseBody void resources(@PathVariable String adGuid, @PathVariable String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Ad ad = adRepository.findByGuid(adGuid);
		if (ad == null) throw new Exception("Ad not found: " + adGuid);
		
		AdResource resource = adResourceRepository.findByAdIdAndFilename(ad.getId(), filename);
		if (resource == null) throw new Exception("Resource not found: " + ad.toString() + " " + filename);
		
		response.setContentType(resource.getContentType());
		
		IOUtils.copy(new ByteArrayInputStream(resource.getData()), response.getOutputStream());
	}

	@RequestMapping("/ad/{adGuid}/click")
	public @ResponseBody void click(@PathVariable String adGuid, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Ad ad = adRepository.findByGuid(adGuid);
		if (ad == null) throw new Exception("Ad not found");

		// FIXME: This would fail with a NPE if ever used due to not having a domain set. Commenting out for now.
		//statsStream.register(StatEntry.builder().name("stats.ad." + adGuid + ".click").ipAddress(request.getRemoteAddr()).userAgent(request.getHeader("User-Agent")).build());

		response.sendRedirect(ad.getCurrent().getTargetUrl());
	}

}
