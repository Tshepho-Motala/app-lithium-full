package lithium.service.reward.controller.backoffice;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lithium.service.client.datatable.DataTablePostRequest;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.reward.client.dto.PlayerRewardComponentStatus;
import lithium.service.reward.client.dto.PlayerRewardHistoryBO;
import lithium.service.reward.client.dto.PlayerRewardHistoryStatus;
import lithium.service.reward.object.PlayerRewardHistoryQuery;
import lithium.service.reward.service.PlayerRewardHistoryService;
import lithium.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping( "/backoffice/{domainName}/rewards/player" )
public class BackofficePlayerRewardHistory {

  private final PlayerRewardHistoryService playerRewardHistoryService;

  //Changed this to a post request because of item 25 https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/2438791178/Lithium+Code+Quality+Guidelines
  @PostMapping( path = "/all-domain" )
  public DataTableResponse<PlayerRewardHistoryBO> allRewardsDomain(@PathVariable( "domainName" ) String domainName,
      @RequestBody PlayerRewardHistoryQuery query,DataTablePostRequest request)
  {

    if(StringUtil.isEmpty(query.getDomainName())) {
      query.setDomainName(domainName);
    }
    return new DataTableResponse<>(request,
        playerRewardHistoryService.findAllPlayerRewardHistoryRecordsPaged(query, request));
  }

  @GetMapping( path = "/history-status" )
  public List<PlayerRewardHistoryStatus> allPlayerRewardHistoryStatuses(@PathVariable( "domainName" ) String domainName) {
    return Arrays.asList(PlayerRewardHistoryStatus.values());
  }

  @GetMapping( path = "/history-type-status" )
  public List<PlayerRewardComponentStatus> allPlayerRewardHistoryTypeStatuses(@PathVariable( "domainName" ) String domainName) {
    return Arrays.asList(PlayerRewardComponentStatus.values());
  }

  //Changed this to a post request because of item 25 https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/2438791178/Lithium+Code+Quality+Guidelines
  @PostMapping( path = "/all-player" )
  public DataTableResponse<PlayerRewardHistoryBO> allRewardsPlayer(@PathVariable( "domainName" ) String domainName,
                                                                   PlayerRewardHistoryQuery query,
                                                                   DataTablePostRequest request)
  {

    return new DataTableResponse<>(request,
        playerRewardHistoryService.findAllPlayerRewardHistoryRecordsPaged(query, request));
  }

  @PostMapping( path = "/{playerRewardHistoryId}/cancel-reward" )
  public boolean cancelPlayerReward(
      @PathVariable( "domainName" ) String domainName,
      @PathVariable( value = "playerRewardHistoryId" ) Long playerRewardHistoryId,
      @RequestParam( "playerGuid" ) String playerGuid
  ) {
    return playerRewardHistoryService.cancelPlayerReward(playerGuid, playerRewardHistoryId);
  }

  @PostMapping( path = "/{playerRewardTypeHistoryId}/cancel-reward-type" )
  public boolean cancelPlayerRewardType(
      @PathVariable( "domainName" ) String domainName,
      @PathVariable( value = "playerRewardTypeHistoryId" ) Long playerRewardTypeHistoryId,
      @RequestParam( "playerGuid" ) String playerGuid
  ) {
    return playerRewardHistoryService.cancelPlayerRewardType(playerGuid, playerRewardTypeHistoryId, true);
  }
}
