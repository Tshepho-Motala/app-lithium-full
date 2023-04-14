package lithium.service.reward.controller.backoffice;

import lithium.service.Response;
import lithium.service.reward.data.entities.RewardRevision;
import lithium.service.reward.data.entities.RewardRevisionType;
import lithium.service.reward.data.entities.RewardRevisionTypeGame;
import lithium.service.reward.data.entities.RewardRevisionTypeValue;
import lithium.service.reward.dto.SimpleRewardTypeGame;
import lithium.service.reward.dto.SimpleRewardTypeValue;
import lithium.service.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/backoffice/reward-revisions")
public class BackofficeRewardRevisionController {

  private final RewardService rewardService;

  private final ModelMapper modelMapper;

  @GetMapping(path = "/{revisionId}")
  public Response<lithium.service.reward.client.dto.RewardRevision> listRevisions(@PathVariable(value = "revisionId") Long revisionId){
    try {
      RewardRevision rewardRevision = rewardService.findRevision(revisionId);
      return Response.<lithium.service.reward.client.dto.RewardRevision>builder()
            .data(modelMapper.map(rewardRevision, lithium.service.reward.client.dto.RewardRevision.class))
            .status(OK)
            .build();
    } catch (Exception exception){
      log.error(exception.getMessage(), exception);
      return Response.<lithium.service.reward.client.dto.RewardRevision>builder()
            .status(INTERNAL_SERVER_ERROR)
            .message(exception.getMessage())
            .build();
    }
  }

  @GetMapping(path = "/{revisionId}/revision-types")
  public Response<List<lithium.service.reward.client.dto.RewardRevisionType>> listRewardRevisionTypes(
        @PathVariable(value = "revisionId") RewardRevision rewardRevision) {
    try {
      List<RewardRevisionType> rewardRevisionTypes = rewardService.findRewardTypes(rewardRevision);
      List<lithium.service.reward.client.dto.RewardRevisionType> values = rewardRevisionTypes.stream()
            .map(rewardRevisionTypeValue -> modelMapper.map(rewardRevisionTypeValue, lithium.service.reward.client.dto.RewardRevisionType.class))
            .toList();
      return Response.<List<lithium.service.reward.client.dto.RewardRevisionType>>builder()
            .data(values)
            .status(OK)
            .build();
    } catch (Exception exception) {
      log.error(exception.getMessage(), exception);
      return Response.<List<lithium.service.reward.client.dto.RewardRevisionType>>builder()
            .status(INTERNAL_SERVER_ERROR)
            .message(exception.getMessage())
            .build();
    }
  }

  //Changed this to a post request because of item 25 https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/2438791178/Lithium+Code+Quality+Guidelines
  @PostMapping(path = "/{revisionId}/revision-types/{rewardRevisionTypeId}/revision-type-values")
  public Response<List<lithium.service.reward.client.dto.RewardRevisionTypeValue>> listRewardRevisionTypeValues(
        @PathVariable(value = "revisionId") RewardRevision rewardRevision,
        @PathVariable(value = "rewardRevisionTypeId") RewardRevisionType rewardRevisionType) {
    try {
      List<RewardRevisionTypeValue> rewardRevisionTypeValues = rewardService.findByRewardRevisionType(rewardRevisionType.getId());
      List<lithium.service.reward.client.dto.RewardRevisionTypeValue> values = rewardRevisionTypeValues.stream()
            .map(rewardRevisionTypeValue -> modelMapper.map(rewardRevisionTypeValue, lithium.service.reward.client.dto.RewardRevisionTypeValue.class))
            .toList();
      return Response.<List<lithium.service.reward.client.dto.RewardRevisionTypeValue>>builder()
            .data(values)
            .status(OK)
            .build();
    } catch (Exception exception) {
      log.error(exception.getMessage(), exception);
      return Response.<List<lithium.service.reward.client.dto.RewardRevisionTypeValue>>builder()
            .status(INTERNAL_SERVER_ERROR)
            .message(exception.getMessage())
            .build();
    }
  }

  //Changed this to a post request because of item 25 https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/2438791178/Lithium+Code+Quality+Guidelines
  @PostMapping(path = "/{revisionId}/revision-types/{rewardRevisionTypeId}/revision-type-games")
  public Response<List<lithium.service.reward.client.dto.RewardRevisionTypeGame>> listRewardRevisionTypeGames(
        @PathVariable(value = "revisionId") RewardRevision rewardRevision,
        @PathVariable(value = "rewardRevisionTypeId") RewardRevisionType rewardRevisionType) {
    try {
      List<RewardRevisionTypeGame> rewardRevisionTypeGames = rewardService.findGamesByRewardRevisionType(rewardRevisionType.getId());
      List<lithium.service.reward.client.dto.RewardRevisionTypeGame> values = rewardRevisionTypeGames.stream()
            .map(rewardRevisionTypeValue -> modelMapper.map(rewardRevisionTypeValue, lithium.service.reward.client.dto.RewardRevisionTypeGame.class))
            .toList();
      return Response.<List<lithium.service.reward.client.dto.RewardRevisionTypeGame>>builder()
            .data(values)
            .status(OK)
            .build();
    } catch (Exception exception) {
      log.error(exception.getMessage(), exception);
      return Response.<List<lithium.service.reward.client.dto.RewardRevisionTypeGame>>builder()
            .status(INTERNAL_SERVER_ERROR)
            .message(exception.getMessage())
            .build();
    }
  }

//  @PostMapping(path = "/{revisionId}/revision-type-values")
//  public Response<lithium.service.reward.client.dto.RewardRevisionTypeValue> addRewardTypeValue(@PathVariable(value = "revisionId") RewardRevision rewardRevision,
//                                                                                                @RequestBody SimpleRewardTypeValue simpleRewardTypeValue) {
//    try {
//      RewardRevisionTypeValue rewardRevisionTypeValue = rewardService.saveRewardRevisionTypeValue(rewardRevision, simpleRewardTypeValue);
//      return Response.<lithium.service.reward.client.dto.RewardRevisionTypeValue>builder()
//            .data(modelMapper.map(rewardRevisionTypeValue, lithium.service.reward.client.dto.RewardRevisionTypeValue.class))
//            .status(OK)
//            .build();
//    } catch (Exception e) {
//      log.error(e.getMessage(), e);
//      return Response.<lithium.service.reward.client.dto.RewardRevisionTypeValue>builder()
//            .status(INTERNAL_SERVER_ERROR)
//            .message(e.getMessage())
//            .build();
//    }
//  }
//
//  @PostMapping(path = "/{revisionId}/revision-type-games")
//  public Response<lithium.service.reward.client.dto.RewardRevisionTypeGame> addRewardTypeGame(@PathVariable(value = "revisionId") RewardRevision rewardRevision,
//                                                                                              @RequestBody SimpleRewardTypeGame simpleRewardTypeGame) {
//    try {
//      RewardRevisionTypeGame rewardRevisionTypeGame = rewardService.saveRewardRevisionTypeGame(rewardRevision, simpleRewardTypeGame);
//      return Response.<lithium.service.reward.client.dto.RewardRevisionTypeGame>builder()
//            .data(modelMapper.map(rewardRevisionTypeGame, lithium.service.reward.client.dto.RewardRevisionTypeGame.class))
//            .status(OK)
//            .build();
//    } catch (Exception e) {
//      log.error(e.getMessage(), e);
//      return Response.<lithium.service.reward.client.dto.RewardRevisionTypeGame>builder()
//            .status(INTERNAL_SERVER_ERROR)
//            .message(e.getMessage())
//            .build();
//    }
//  }

  @DeleteMapping(path = "/{revisionId}/revision-types/{rewardRevisionTypeId}/revision-type-games/{guid}")
  public Response<Boolean> removeRevisionTypeGame(@PathVariable(value = "revisionId") RewardRevision rewardRevision,
                                                  @PathVariable(value = "rewardRevisionTypeId" ) RewardRevisionType rewardRevisionType,
                                                  @PathVariable(value = "guid") String gameGuid) {
    try {
      return Response.<Boolean>builder()
            .data(rewardService.removeRevisionTypeGame(rewardRevisionType, gameGuid))
            .status(OK)
            .build();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.<Boolean>builder()
            .status(INTERNAL_SERVER_ERROR)
            .message(e.getMessage())
            .build();
    }
  }

  @DeleteMapping(path = "/{revisionId}/revision-types/{rewardRevisionTypeId}/revision-type-values/{rewardRevisionTypeValueId}")
  public Response<Boolean> removeRevisionTypeValue(@PathVariable(value = "revisionId") RewardRevision rewardRevision,
                                                  @PathVariable(value = "rewardRevisionTypeId" ) RewardRevisionType rewardRevisionType,
                                                  @PathVariable(value = "rewardRevisionTypeValueId") Long rewardRevisionTypeValueId) {
    try {
      return Response.<Boolean>builder()
            .data(rewardService.removeRevisionTypeValue(rewardRevisionType, rewardRevisionTypeValueId))
            .status(OK)
            .build();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.<Boolean>builder()
            .status(INTERNAL_SERVER_ERROR)
            .message(e.getMessage())
            .build();
    }
  }
}