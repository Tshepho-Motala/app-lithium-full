package lithium.service.reward.controller.backoffice;

import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.reward.client.dto.RewardRevision;
import lithium.service.reward.controller.ValidationController;
import lithium.service.reward.data.entities.Reward;
import lithium.service.reward.dto.requests.CreateRewardRequest;
import lithium.service.reward.dto.requests.RewardEditRequest;
import lithium.service.reward.service.RewardService;
import lithium.tokens.LithiumTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;
import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@Slf4j
@RestController
@RequestMapping("/backoffice/{domainName}/rewards")
@RequiredArgsConstructor
public class BackofficeRewardController extends ValidationController {

    private final RewardService rewardService;

    private final ModelMapper modelMapper;

    @PostMapping
    public Reward create(@Valid @RequestBody CreateRewardRequest createRewardRequest, LithiumTokenUtil util) {
        return rewardService.create(createRewardRequest, util);
    }

    //Changed this to a post request because of item 25 https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/2438791178/Lithium+Code+Quality+Guidelines
    @GetMapping(path = "/{id}")
    public Reward findReward(@PathVariable(value = "id") Long id) {
       return rewardService.findReward(id);
    }

    //Changed this to a post request because of item 25 https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/2438791178/Lithium+Code+Quality+Guidelines
    @GetMapping(path = "/{id}/revisions")
    public Response<List<RewardRevision>> findRewardRevisions(@PathVariable(value = "id") Long id) {
        Reward reward = rewardService.findReward(id);
        List<RewardRevision> revisions = rewardService.findRevisions(reward)
              .stream()
              .map(rewardRevision -> modelMapper.map(rewardRevision, lithium.service.reward.client.dto.RewardRevision.class))
              .toList();
        return Response.<List<RewardRevision>>builder()
              .data(revisions)
              .status(OK)
              .build();
    }

    @GetMapping("/{id}/modify")
    public Response<lithium.service.reward.client.dto.Reward> modify(@PathVariable("id") Reward reward, LithiumTokenUtil tokenUtil) {
        try {
            reward = rewardService.modify(reward, tokenUtil.guid());
            return Response.<lithium.service.reward.client.dto.Reward>builder()
                  .data(modelMapper.map(reward, lithium.service.reward.client.dto.Reward.class))
                  .status(OK)
                  .build();
        } catch (Exception e) {
            log.error(e.getMessage(),  e);
            return Response.<lithium.service.reward.client.dto.Reward>builder()
                  .data(modelMapper.map(reward, lithium.service.reward.client.dto.Reward.class))
                  .status(INTERNAL_SERVER_ERROR)
                  .message(e.getMessage())
                  .build();
        }
    }

    @PostMapping("/{id}/modify")
    public Response<lithium.service.reward.client.dto.Reward> modify(
          @PathVariable("id") Reward reward,
          @RequestBody RewardEditRequest rewardEditRequest
    ) {
        try {
            reward = rewardService.modify(reward, rewardEditRequest);
            return Response.<lithium.service.reward.client.dto.Reward>builder()
                  .data(modelMapper.map(reward, lithium.service.reward.client.dto.Reward.class))
                  .status(OK)
                  .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.<lithium.service.reward.client.dto.Reward>builder()
                  .data(modelMapper.map(reward, lithium.service.reward.client.dto.Reward.class))
                  .status(INTERNAL_SERVER_ERROR)
                  .message(e.getMessage())
                  .build();
        }
    }

    @PostMapping("/{id}/modify-and-save-current")
    public Response<lithium.service.reward.client.dto.Reward> modifyAndSaveCurrent(
          @PathVariable("id") Reward reward,
          @RequestBody RewardEditRequest rewardEditRequest
    ) {
        try {
            reward = rewardService.modifyAndSaveCurrent(reward, rewardEditRequest);
            return Response.<lithium.service.reward.client.dto.Reward>builder()
                  .data(modelMapper.map(reward, lithium.service.reward.client.dto.Reward.class))
                  .status(OK)
                  .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.<lithium.service.reward.client.dto.Reward>builder()
                  .data(modelMapper.map(reward, lithium.service.reward.client.dto.Reward.class))
                  .status(INTERNAL_SERVER_ERROR)
                  .message(e.getMessage())
                  .build();
        }
    }

    @PostMapping(path = "/{id}/save-current")
    public Response<lithium.service.reward.client.dto.Reward> saveCurrent(@PathVariable("id") Reward reward) {
        try {
            reward = rewardService.saveCurrent(reward);
            return Response.<lithium.service.reward.client.dto.Reward>builder()
                  .data(modelMapper.map(reward, lithium.service.reward.client.dto.Reward.class))
                  .status(OK)
                  .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.<lithium.service.reward.client.dto.Reward>builder()
                  .data(modelMapper.map(reward, lithium.service.reward.client.dto.Reward.class))
                  .status(INTERNAL_SERVER_ERROR)
                  .message(e.getMessage())
                  .build();
        }
    }

    @PostMapping("/{id}/cancel")
    public Response<Boolean> cancel(
        @PathVariable("id") Reward reward
    ){
        log.debug("Request to cancel reward: {}", reward.toShortString());
        try {
            rewardService.cancel(reward);
        } catch (Exception e) {
            log.error("Could not cancel reward: {}", reward.toShortString(), e);
            return Response.<Boolean>builder()
                .data(Boolean.FALSE.booleanValue())
                .status(INTERNAL_SERVER_ERROR)
                .build();
        }
        return Response.<Boolean>builder()
            .data(Boolean.TRUE.booleanValue())
            .status(OK)
            .build();
    }

    @GetMapping("/{id}/changelogs")
    public Response<ChangeLogs> changelogs(@PathVariable("id") Reward reward, @RequestParam(required = false, defaultValue = "0") Integer p) throws Exception {
        return rewardService.changelogs(reward, new String[] {"reward"},  p);
    }
}
