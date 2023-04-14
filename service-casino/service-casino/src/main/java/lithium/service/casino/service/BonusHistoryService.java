package lithium.service.casino.service;

import lithium.service.casino.api.frontend.schema.BonusHistoryResponse;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.repositories.PlayerBonusHistoryRepository;
import lithium.service.client.datatable.DataTableRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BonusHistoryService {

    @Autowired
    PlayerBonusHistoryRepository pbhRepository;

    public Page<BonusHistoryResponse> findByPlayerBonusPlayerGuid(String guid, DataTableRequest request) {

        Page<PlayerBonusHistory> pbhPage;
        pbhPage = pbhRepository.findByPlayerBonusPlayerGuid(guid, request.getPageRequest());

        List<BonusHistoryResponse> response = new ArrayList<>();
        for (PlayerBonusHistory pbh: pbhPage.getContent()){
            BonusHistoryResponse bhResponse = BonusHistoryResponse.builder()
                    .startedDate(pbh.getStartedDate())
                    .bonusType(pbh.getBonus().getBonusType())
                    .bonusTriggerType(pbh.getBonus().getBonusTriggerType())
                    .bonusCode(pbh.getBonus().getBonusCode())
                    .bonusName(pbh.getBonus().getBonusName())
                    .amountCents(pbh.getCustomFreeMoneyAmountCents())
                    .description((pbh.getDescription()))
                    .requestId(pbh.getRequestId())
                    .build();
            response.add(bhResponse);
        }
        final Page<BonusHistoryResponse> resultPage = new PageImpl<>(response, request.getPageRequest(), pbhPage.getTotalElements());

        return resultPage;
    }

    public boolean bonusCodeExistOnPlayerGuid(String bonusCode, String playerGuid) {
        List<PlayerBonusHistory> playerBonusHistoryList = pbhRepository.findByPlayerBonusPlayerGuidAndBonusBonusCode(playerGuid, bonusCode);
        return !playerBonusHistoryList.isEmpty();
    }
}
