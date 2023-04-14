package lithium.service.leaderboard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.leaderboard.services.LeaderboardService;

//@Slf4j
@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {
	@Autowired LeaderboardService leaderboardService;
	
//	@GetMapping("/{domainName}")
//	public Response<List<LeaderboardHistory>> listByDomain(
//		@PathVariable String domainName
//	) {
//		log.info("Leaderboard List for : "+domainName);
//		List<LeaderboardHistory> list = leaderboardService.listByDomain(domainName, true);
//		log.info("List : "+list);
//		return Response.<List<LeaderboardHistory>>builder().data(list).status(Status.OK).build();
//	}
	
//	@GetMapping("/{domainName}/p")
//	public Response<List<LeaderboardProjection>> listProjectionByDomain(
//		@PathVariable String domainName
//	) {
//		log.info("Leaderboard Projection List for : "+domainName);
//		List<LeaderboardProjection> list = leaderboardService.findProjectionByDomain(domainName);
//		log.info("List : "+list);
//		return Response.<List<LeaderboardProjection>>builder().data(list).status(Status.OK).build();
//	}
//	
//	@GetMapping("/{leaderboardId}/pt")
//	public Response<LeaderboardEntries> leaderboardProjectionTable(
//		@PathVariable("leaderboardId") Leaderboard leaderboard,
//		@RequestParam(name="username") String username,
//		@RequestParam(name="period") Integer period
//	) {
//		if (leaderboard == null) return Response.<LeaderboardEntries>builder().status(Status.NOT_FOUND).build();
//		LeaderboardEntries table = leaderboardService.findEntriesFromLeaderboard(leaderboard, username, period);
//		
//		return Response.<LeaderboardEntries>builder().data(table).status(Status.OK).build();
//	}
	
//	@GetMapping("/{domainName}/t")
//	public List<Leaderboard> leaderboardTableList(
//		@PathVariable String domainName,
//		@RequestParam(name="username") String username
//	) {
//		Pageable limit = PageRequest.of(0,10);
//		Page<Leaderboard> table = leaderboardService.findByDomains(username, Arrays.asList(domainName), "", true, true, limit);
//		List<Leaderboard> list = new ArrayList<Leaderboard>();
//		table.forEach(list::add);
//		return list;
//	}
//	
//	@GetMapping("/{domainName}/table")
//	public DataTableResponse<Leaderboard> leaderboardTable(
//		@PathVariable String domainName,
//		@RequestParam(name="username") String username,
//		DataTableRequest request
//	) {
//		Page<Leaderboard> table = leaderboardService.findByDomains(username, Arrays.asList(domainName), request.getSearchValue(), true, true, request.getPageRequest());
//		return new DataTableResponse<>(request, table);
//	}
}
