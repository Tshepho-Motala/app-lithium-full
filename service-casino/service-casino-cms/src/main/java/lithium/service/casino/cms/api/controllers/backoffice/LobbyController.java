package lithium.service.casino.cms.api.controllers.backoffice;

import lithium.service.Response;
import lithium.service.casino.cms.api.objects.LobbyRequest;
import lithium.service.casino.cms.api.objects.PageBannerRequest;
import lithium.service.casino.cms.exceptions.Status404BannerNotFound;
import lithium.service.casino.cms.exceptions.Status428UnpublishedLobbyException;
import lithium.service.casino.cms.services.LobbyService;
import lithium.service.casino.cms.storage.entities.Banner;
import lithium.service.casino.cms.storage.entities.Lobby;
import lithium.service.casino.cms.storage.entities.LobbyRevision;
import lithium.service.casino.cms.storage.entities.PageBanner;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.UserApiInternalClient;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/backoffice/{domainName}/lobby/{id}")
@Slf4j
public class LobbyController {
	@Autowired
	private LobbyService service;
	@Autowired
	private LithiumServiceClientFactory serviceFactory;

	@GetMapping
	private Response<Lobby> find(@PathVariable("domainName") String domainName, @PathVariable("id") Lobby lobby) {
		return Response.<Lobby>builder().data(lobby).status(Response.Status.OK).build();
	}

	@GetMapping("/revision/{lobbyRevisionId}")
	private Response<LobbyRevision> revisions(
		@PathVariable("domainName") String domainName,
		@PathVariable("id") Lobby lobby,
		@PathVariable("lobbyRevisionId") LobbyRevision lobbyRevision
	) {
		return Response.<LobbyRevision>builder().data(lobbyRevision).status(Response.Status.OK).build();
	}

	@GetMapping("/revisions")
	private DataTableResponse<LobbyRevision> revisions(
		@PathVariable("domainName") String domainName,
		@PathVariable("id") Lobby lobby,
		DataTableRequest request
	) throws Exception {
		Page<LobbyRevision> revisionsByLobby = service.findRevisionsByLobby(lobby, request.getPageRequest());
		List<String> guids = LobbyService.buildUserGuids(revisionsByLobby);
		DataTableResponse<LobbyRevision> lobbyRevisionDataTableResponse = new DataTableResponse<>(request, revisionsByLobby);

		UserApiInternalClient client = this.serviceFactory.target(UserApiInternalClient.class, true);
		Response<List<lithium.service.user.client.objects.User>> response = client.getUsers(guids);
		service.setLobbyRevisionUserData(response.getData(), revisionsByLobby.getContent());

		return lobbyRevisionDataTableResponse;
	}

	@GetMapping("/modify")
	private Response<Lobby> modify(@PathVariable("id") Lobby lobby, LithiumTokenUtil tokenUtil) {
		try {
			lobby = service.modify(lobby, tokenUtil.guid());
			return Response.<Lobby>builder().data(lobby).status(Response.Status.OK).build();
		} catch (Exception e) {
			return Response.<Lobby>builder().message(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/modify")
	private Response<Lobby> modify(@PathVariable("id") Lobby lobby, @RequestBody LobbyRequest request,
	        LithiumTokenUtil tokenUtil) {
		try {
			lobby = service.modify(lobby, request, tokenUtil.guid());
			return Response.<Lobby>builder().data(lobby).status(Response.Status.OK).build();
		} catch (Exception e) {
			return Response.<Lobby>builder().message(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/modifyAndSaveCurrent")
	private Response<Lobby> modifyAndSaveCurrent(@PathVariable("id") Lobby lobby, @RequestBody LobbyRequest request,
			LithiumTokenUtil tokenUtil) {
		try {
			lobby = service.modifyAndSaveCurrent(lobby, request, tokenUtil.guid());
			return Response.<Lobby>builder().data(lobby).status(Response.Status.OK).build();
		} catch (Exception e) {
			return Response.<Lobby>builder().message(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/banners/{bannerId}/add-page-banner-list")
	public Response<List<PageBanner>> addBanners(@PathVariable("domainName") String domainName,
												 @PathVariable("id") Lobby lobby,
												 @PathVariable("bannerId") Banner banner,
												 @RequestBody List<PageBanner> pageBanners) {
		pageBanners.stream().forEach(pageBanner -> {
			pageBanner.setBanner(banner);
			pageBanner.setLobby(lobby);
		});
		pageBanners = service.addPageBanners(pageBanners);
		return Response.<List<PageBanner>>builder().data(pageBanners).status(Response.Status.OK).build();
	}

	@PostMapping("/banners/update-positions")
	public Response<List<PageBanner>> updatePageBannersPosition(@PathVariable("domainName") String domainName,
																@PathVariable("id") Lobby lobby,
																@RequestBody List<PageBanner> pageBanners) {
		Map<Long, Integer> idPositionMap = pageBanners.stream().collect(Collectors.toMap(PageBanner::getId, PageBanner::getPosition));
		List<PageBanner> pageBannerResults = service.updatePageBannerPositions(idPositionMap);
		return Response.<List<PageBanner>>builder().data(pageBannerResults).status(Response.Status.OK).build();
	}

	@PostMapping("/banners/remove-from-page/{pageBannerId}")
	public Response<Void> removePageBanner(@PathVariable("domainName") String domainName,
																@PathVariable("id") Lobby lobby,
																@PathVariable("pageBannerId") Long pageBannerId) {
		service.removePageBanner(pageBannerId);
		return Response.<Void>builder().status(Response.Status.OK).build();
	}

	@PostMapping("/banners/{bannerId}/add-page-banner")
	public Response<PageBanner> addPageBanner(@PathVariable("domainName") String domainName,
											  @PathVariable("id") Long id,
											  @PathVariable("bannerId") Banner banner,
											  @RequestBody PageBanner pageBanner) {
		Lobby lobby = service.findLobbyById(id).orElseThrow(Status428UnpublishedLobbyException::new);
		pageBanner.setBanner(banner);
		pageBanner.setLobby(lobby);
		pageBanner = service.addPageBanner(domainName, pageBanner);
		return Response.<PageBanner>builder().data(pageBanner).status(Response.Status.OK).build();
	}

	@PostMapping("banners/get-page-banners")
	public Response<List<PageBanner>> getBannersByDomain(@PathVariable String domainName, @PathVariable Long id, @RequestBody PageBannerRequest pageBannerRequest) throws Status550ServiceDomainClientException {
		List<PageBanner> pageBanners = service.retrievePageBanners(domainName, pageBannerRequest.getPrimaryNavCode(), pageBannerRequest.getSecondaryNavCode(), pageBannerRequest.getChannel());
		return Response.<List<PageBanner>>builder().data(pageBanners).status(Response.Status.OK).build();
	}
}
