package lithium.service.leaderboard.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.leaderboard.client.objects.LeaderboardBasic;
import lithium.service.leaderboard.client.objects.LeaderboardConversionBasic;
import lithium.service.leaderboard.data.entities.Entry;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardConversion;
import lithium.service.leaderboard.data.entities.LeaderboardHistory;
import lithium.service.leaderboard.data.entities.LeaderboardPlaceNotification;
import lithium.service.leaderboard.services.LeaderboardHistoryService;
import lithium.service.leaderboard.services.LeaderboardService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/leaderboard/admin")
public class AdminLeaderboardController {
	@Autowired LeaderboardService leaderboardService;
	@Autowired LeaderboardHistoryService leaderboardHistoryService;
	
	@GetMapping("/table")
	public DataTableResponse<Leaderboard> leaderboardTable(@RequestParam("domains") List<String> domains, DataTableRequest request) {
		Page<Leaderboard> table = leaderboardService.findByDomains("", domains, request.getSearchValue(), null, null, request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/recurrence/{id}")
	public Response<List<LeaderboardHistory>> recurrence(
		@PathVariable("id") Leaderboard leaderboard
	) {
		log.debug("recurrence :: "+leaderboard);
		return Response.<List<LeaderboardHistory>>builder().data(leaderboardHistoryService.nextInstances(leaderboard)).status(Status.OK).build();
	}
	
	@GetMapping("/conversion/{id}/table")
	public DataTableResponse<LeaderboardConversion> leaderboardConversionTable(
		@PathVariable("id") Leaderboard leaderboard,
		DataTableRequest request
	) {
		Page<LeaderboardConversion> table = leaderboardService.findConversions(leaderboard, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	@GetMapping("/notification/{id}/table")
	public DataTableResponse<LeaderboardPlaceNotification> leaderboardNotificationTable(
		@PathVariable("id") Leaderboard leaderboard,
		DataTableRequest request
	) {
		Page<LeaderboardPlaceNotification> table = leaderboardService.findNotifications(leaderboard, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/history/{id}/table")
	public DataTableResponse<LeaderboardHistory> leaderboardHistoryTable(
		@PathVariable("id") Leaderboard leaderboard,
		DataTableRequest request
	) {
		Page<LeaderboardHistory> table = leaderboardHistoryService.searchHistory(leaderboard, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	@GetMapping("/history/{id}/places/table")
	public DataTableResponse<Entry> leaderboardHistoryPlacesTable(
		@PathVariable("id") LeaderboardHistory leaderboardHistory,
		DataTableRequest request
	) {
		Page<Entry> table = leaderboardService.findHistoryPlaces(leaderboardHistory, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@PostMapping("/optout")
	public Response<Void> optout(
		@RequestParam(name="guid") String guid
	) {
		leaderboardService.optout(guid);
		return Response.<Void>builder().status(Status.OK).build();
	}
	@PostMapping("/toggle/{id}")
	public Response<Leaderboard> toggle(
		@PathVariable("id") Leaderboard leaderboard
	) {
		leaderboard.setVisible(!leaderboard.getVisible());
		leaderboard = leaderboardService.save(leaderboard);
		return Response.<Leaderboard>builder().data(leaderboard).status(Status.OK).build();
	}
	@PostMapping("/enable/{id}")
	public Response<Leaderboard> enable(
		@PathVariable("id") Leaderboard leaderboard
	) {
		leaderboard.setEnabled(!leaderboard.getEnabled());
		leaderboard = leaderboardService.save(leaderboard);
		return Response.<Leaderboard>builder().data(leaderboard).status(Status.OK).build();
	}
	
	@PostMapping("/edit/{id}")
	public Response<Leaderboard> edit(
		@PathVariable("id") Leaderboard leaderboard,
		@RequestBody LeaderboardBasic leaderboardBasic
	) {
		log.debug("Edit LB : "+leaderboardBasic);
		leaderboard = leaderboardService.edit(leaderboard, leaderboardBasic);
		if (leaderboard == null) {
			return Response.<Leaderboard>builder().status(Status.EXISTS).build();
		}
		return Response.<Leaderboard>builder().data(leaderboard).status(Status.OK).build();
	}
	
	@PostMapping("/add")
	public Response<Leaderboard> add(@RequestBody LeaderboardBasic leaderboardBasic) {
		log.debug("Creating LB : "+leaderboardBasic);
		Leaderboard leaderboard = leaderboardService.add(leaderboardBasic);
		if (leaderboard == null) {
			return Response.<Leaderboard>builder().status(Status.EXISTS).build();
		}
		return Response.<Leaderboard>builder().data(leaderboard).status(Status.OK).build();
	}
	
	@PostMapping("/add/conversion")
	public Response<LeaderboardConversion> addConversion(
		@RequestBody LeaderboardConversionBasic lbc
	) {
		LeaderboardConversion leaderboardConversion = leaderboardService.addConversion(lbc);
		return Response.<LeaderboardConversion>builder().data(leaderboardConversion).status(Status.OK).build();
	}
	@PostMapping("/edit/conversion")
	public Response<LeaderboardConversion> editConversion(
		@RequestBody LeaderboardConversionBasic lbc
	) {
		LeaderboardConversion leaderboardConversion = leaderboardService.editConversion(lbc);
		return Response.<LeaderboardConversion>builder().data(leaderboardConversion).status(Status.OK).build();
	}
	
	@PostMapping("/add/{id}/notification")
	public Response<LeaderboardPlaceNotification> addNotification(
		@PathVariable("id") Leaderboard leaderboard,
		@RequestBody LeaderboardPlaceNotification lpn
	) {
		lpn.setLeaderboard(leaderboard);
		log.debug("Creating LB notification : "+lpn);
		LeaderboardPlaceNotification leaderboardConversion = leaderboardService.addNotification(lpn);
//		Leaderboard leaderboard = leaderboardService.add(leaderboardBasic);
//		if (leaderboardConversion == null) {
//			return Response.<LeaderboardConversion>builder().status(Status.EXISTS).build();
//		}
		return Response.<LeaderboardPlaceNotification>builder().data(leaderboardConversion).status(Status.OK).build();
	}
	@PostMapping("/edit/{id}/notification")
	public Response<LeaderboardPlaceNotification> editNotification(
		@PathVariable("id") Leaderboard leaderboard,
		@RequestBody LeaderboardPlaceNotification lpn
	) {
		log.debug("Edit LB notification : "+lpn);
		if (leaderboardService.findByLeaderboardAndRank(leaderboard, lpn.getRank()) != null) {
			return Response.<LeaderboardPlaceNotification>builder().status(Status.CONFLICT).build();
		}
		LeaderboardPlaceNotification leaderboardConversion = leaderboardService.editNotification(lpn);
		return Response.<LeaderboardPlaceNotification>builder().data(leaderboardConversion).status(Status.OK).build();
	}
	
	@GetMapping("/find/{id}")
	public Response<Leaderboard> find(@PathVariable("id") Leaderboard leaderboard) {
		log.debug("Found : "+leaderboard);
		return Response.<Leaderboard>builder().data(leaderboard).status(Status.OK).build();
	}
	@GetMapping("/history/find/{id}")
	public Response<LeaderboardHistory> findHistory(@PathVariable("id") LeaderboardHistory leaderboardHistory) {
		log.debug("Found : "+leaderboardHistory);
		return Response.<LeaderboardHistory>builder().data(leaderboardHistory).status(Status.OK).build();
	}
}