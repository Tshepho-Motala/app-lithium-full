package lithium.service.raf.controllers.admin;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.raf.data.entities.Configuration;
import lithium.service.raf.data.objects.AutoConvertRequest;
import lithium.service.raf.data.objects.AutoConvertResult;
import lithium.service.raf.services.ConfigurationService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/{domainName}/configuration")
@Slf4j
public class ConfigurationController {
	@Autowired ConfigurationService service;
	
	@GetMapping
	public Response<Configuration> get(@PathVariable("domainName") String domainName) {
		Configuration config = null;
		try {
			config = service.findOrCreate(domainName);
			return Response.<Configuration>builder().data(config).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Configuration>builder().status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping
	public Response<Configuration> modifyReferralBonuses(@PathVariable("domainName") String domainName, @RequestBody Configuration c) throws Exception {
		Configuration config = null;
		try {
			config = service.modifyConfiguration(c);
			return Response.<Configuration>builder().data(config).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Configuration>builder().status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/player-auto-convert")
	public Response<AutoConvertResult> enableReferralAutoConvert(@PathVariable("domainName") String domainName,@RequestBody AutoConvertRequest request){
		AutoConvertResult convertResult = null;
		try {
			convertResult = service.enableAutoConvertPlayer(domainName,request);
			return Response.<AutoConvertResult>builder().message(convertResult.getMessage()).data(convertResult).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<AutoConvertResult>builder().data(convertResult).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}
