package lithium.service.reward.controller.frontend;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import lithium.service.reward.client.dto.PlayerRewardHistoryFE;
import lithium.service.reward.client.dto.PlayerRewardHistoryStatus;
import lithium.service.reward.client.dto.PlayerRewardComponentHistoryFE;
import lithium.service.reward.dto.requests.AcceptPendingRewardFE;
import lithium.service.reward.dto.requests.RewardRequestFE;
import lithium.service.reward.service.GiveRewardService;
import lithium.service.reward.service.PlayerRewardHistoryService;
import lithium.tokens.LithiumTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/frontend/player")
@RequiredArgsConstructor
public class PlayerRewardController {

    private final GiveRewardService giveRewardService;
    private final PlayerRewardHistoryService playerRewardHistoryService;

    @PostMapping("/accept-pending-reward-component")
    public PlayerRewardComponentHistoryFE acknowledgePendingReward(
        @Valid @RequestBody AcceptPendingRewardFE acceptPendingReward,
        Locale locale,
        LithiumTokenUtil tokenUtil
    ) {
        return giveRewardService.acceptReward(acceptPendingReward.getId(), acceptPendingReward.isAccept(), locale, tokenUtil);
    }

    @PostMapping("/find/pending/v1")
    public Page<PlayerRewardHistoryFE> findPending(
        @RequestBody RewardRequestFE requestFE,
        Locale locale,
        LithiumTokenUtil tokenUtil
    ) {
        Integer pageSize = Optional.ofNullable(requestFE.getPageSize()).orElse(1000);
        Integer page = Optional.ofNullable(requestFE.getPage()).orElse(0);

        return playerRewardHistoryService.findPlayerRewardHistoryWithStatus(tokenUtil,pageSize , page, PlayerRewardHistoryStatus.PENDING);
    }

    @PostMapping("/list/pending/v1")
    public List<PlayerRewardHistoryFE> listPending(
        Locale locale,
        LithiumTokenUtil tokenUtil
    ) {
        return playerRewardHistoryService.listPlayerRewardHistoryWithStatus(tokenUtil, PlayerRewardHistoryStatus.PENDING);
    }

    @PostMapping("/find/active/v1")
    public Page<PlayerRewardHistoryFE> findActive(
            @RequestBody RewardRequestFE requestFE,
            Locale locale,
            LithiumTokenUtil tokenUtil
    ) {
        Integer pageSize = Optional.ofNullable(requestFE.getPageSize()).orElse(1000);
        Integer page = Optional.ofNullable(requestFE.getPage()).orElse(0);

        return playerRewardHistoryService.findPlayerRewardHistoryWithStatus(tokenUtil,pageSize , page, PlayerRewardHistoryStatus.AWARDED);
    }

    @PostMapping("/list/active/v1")
    public List<PlayerRewardHistoryFE> listActive(
            Locale locale,
            LithiumTokenUtil tokenUtil
    ) {
        return playerRewardHistoryService.listPlayerRewardHistoryWithStatus(tokenUtil, PlayerRewardHistoryStatus.AWARDED);
    }

    @PostMapping("/find/completed/v1")
    public Page<PlayerRewardHistoryFE> findCompleted(@RequestBody RewardRequestFE requestFE,
        Locale locale,
        LithiumTokenUtil tokenUtil
    ) {
        Integer pageSize = Optional.ofNullable(requestFE.getPageSize()).orElse(1000);
        Integer page = Optional.ofNullable(requestFE.getPage()).orElse(0);

        return playerRewardHistoryService.findPlayerRewardHistoryWithStatus(tokenUtil, pageSize, page, PlayerRewardHistoryStatus.REDEEMED);
    }

    @PostMapping("/list/completed/v1")
    public List<PlayerRewardHistoryFE> listCompleted(
        Locale locale,
        LithiumTokenUtil tokenUtil
    ) {
        return playerRewardHistoryService.listPlayerRewardHistoryWithStatus(tokenUtil, PlayerRewardHistoryStatus.REDEEMED);
    }

    @PostMapping("/reward-components/v1")
    public Page<PlayerRewardComponentHistoryFE> rewardComponents(@RequestBody RewardRequestFE requestFE, LithiumTokenUtil util) {
        return playerRewardHistoryService.findRewardComponents(requestFE, util);
    }
}
