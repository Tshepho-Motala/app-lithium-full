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

import lithium.service.affiliate.provider.data.entities.AdResource;
import lithium.service.affiliate.provider.data.entities.CampaignAd;
import lithium.service.affiliate.provider.data.repositories.AdResourceRepository;
import lithium.service.affiliate.provider.data.repositories.CampaignAdRepository;
import lithium.service.stats.client.objects.StatEntry;
import lithium.service.stats.client.stream.StatsStream;

@Controller
@RequestMapping("/caserve")
public class CampaignAdServeController {
	
	@Autowired StatsStream statsStream;
	@Autowired CampaignAdRepository repository;
	@Autowired AdResourceRepository adResourceRepository;

	@RequestMapping("/{caGuid}/resources/")
	public @ResponseBody void entryPoint(@PathVariable String caGuid, HttpServletRequest request, HttpServletResponse response) throws Exception {

		CampaignAd cad = repository.findByGuid(caGuid);
		if (cad == null) throw new Exception("CampaignAd not found: " + caGuid);
		
		resources(caGuid, cad.getAd().getCurrent().getEntryPoint(), request, response);
		// FIXME: This would fail with a NPE if ever used due to not having a domain set. Commenting out for now.
		//statsStream.register(StatEntry.builder().name("stats.ca." + caGuid + ".view").ipAddress(request.getRemoteAddr()).userAgent(request.getHeader("User-Agent")).build());
	}
	
	@RequestMapping("/{caGuid}/resources/{filename:.+}")
	public @ResponseBody void resources(@PathVariable String caGuid, @PathVariable String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CampaignAd cad = repository.findByGuid(caGuid);
		if (cad == null) throw new Exception("CampaignAd not found: " + caGuid);
		
		AdResource resource = adResourceRepository.findByAdIdAndFilename(cad.getAd().getId(), filename);
		if (resource == null) throw new Exception("Resource not found: " + cad.toString() + " " + filename);
		
		response.setContentType(resource.getContentType());
		
		IOUtils.copy(new ByteArrayInputStream(resource.getData()), response.getOutputStream());
	}

	@RequestMapping("/{caGuid}/click")
	public @ResponseBody void click(@PathVariable String caGuid, HttpServletRequest request, HttpServletResponse response) throws Exception {

		CampaignAd cad = repository.findByGuid(caGuid);
		if (cad == null) throw new Exception("CampaignAd not found: " + caGuid);

		// FIXME: This would fail with a NPE if ever used due to not having a domain set. Commenting out for now.
		//statsStream.register(StatEntry.builder().name("stats.ca." + caGuid + ".click").ipAddress(request.getRemoteAddr()).userAgent(request.getHeader("User-Agent")).build());

		response.sendRedirect(cad.getAd().getCurrent().getTargetUrl());
	}

}
