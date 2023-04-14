package lithium.service.casino.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.config.ServiceCasinoConfigurationProperties;
import lithium.service.casino.data.entities.Winner;
import lithium.service.casino.data.entities.WinnerAugmentation;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.casino.service.WinnerFeedService;

@RestController
@EnableConfigurationProperties(ServiceCasinoConfigurationProperties.class)
// no tight coupling between client and service (so no implementation or
// dependence on service-client
public class WinnerController {
	@Autowired
	WinnerFeedService winnerFeedService;
	@Autowired DomainRepository domainRepository;
	@Autowired ModelMapper mapper;
	
	@RequestMapping("/casino/winner/list")
	public Response<List<Winner>> handleWinnerListRequest(@RequestParam("domainName") String domainName) throws Exception {
		return Response.<List<Winner>>builder().status(Status.OK).data(winnerFeedService.getWinnersList(domainName)).build();
	}
	
	@RequestMapping("/casino/winner/add")
	public void handleWinnerAddRequest(@RequestParam("domainName") String domainName, @RequestParam("username") String username, @RequestParam("amount") Long amount, @RequestParam("gameName") String gameName) throws Exception {
		winnerFeedService.addExtraWinner(WinnerAugmentation.builder().amount(amount).domain(domainRepository.findByName(domainName)).gameName(gameName).userName(username).build());
		return;
	}
	
	@RequestMapping("/casino/winner/v2/add")
	public Response<Boolean> handleWinnerAddRequest(@RequestBody lithium.service.casino.client.data.Winner winner) throws Exception {
		winnerFeedService.addWinner(winner);
		return Response.<Boolean>builder().status(Status.OK).build();
	}
}
