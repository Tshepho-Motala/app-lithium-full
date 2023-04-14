package lithium.service.casino.provider.roxor.context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.casino.client.data.BalanceAdjustmentResponseComponent;
import lithium.service.casino.provider.roxor.api.schema.SuccessResponse;
import lithium.service.casino.provider.roxor.api.schema.gameplay.GamePlayRequest;
import lithium.service.casino.provider.roxor.api.schema.gameplay.OperationTypeEnum;
import lithium.service.casino.provider.roxor.storage.entities.GamePlay;
import lithium.service.casino.provider.roxor.storage.entities.Operation;
import lithium.service.casino.provider.roxor.storage.entities.RewardBonusMap;
import lithium.service.domain.client.objects.Domain;
import lithium.service.reward.client.dto.PlayerRewardHistoryStatus;
import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import lithium.service.user.client.objects.LoginEvent;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude={"existingOperationEntityList", "operationEntityList", "gamePlayRequestEntity", "request", "response", "gamePlayEntity"})
@JsonIgnoreProperties(value = { "allowNegativeBalances" })
public class GamePlayContext {
    private String userGuid;
    private String userApiToken;
    private String gamePlayId;
    private String sessionKey;
    private String locale;
    private LoginEvent loginEvent;
    private Domain domain;
    private lithium.service.games.client.objects.Game lithiumGame;
    private lithium.service.casino.provider.roxor.storage.entities.GamePlayRequest gamePlayRequestEntity;
    private String requestJsonString;
    private GamePlayRequest request;
    private SuccessResponse response;
    private Long balanceAfter;
    private GamePlay gamePlayEntity;
    private List<Operation> existingOperationEntityList = new ArrayList<>();
    private List<Operation> operationEntityList = new ArrayList<>();
    private List<BalanceAdjustmentResponseComponent> operationOutcomeList = new ArrayList<>();
    private Boolean roxorFinishPresent = Boolean.FALSE;
    private Boolean roxorFinishWinPresent = Boolean.FALSE;
    private Boolean roxorTransferDebitExists = Boolean.FALSE;
    private Boolean transferCancelExists = Boolean.FALSE;
    private Boolean freePlayExists = Boolean.FALSE;
    private Boolean freePlayCancelExists = Boolean.FALSE;
    private String gamePlayRequestErrorReason;
    private Boolean allowNegativeBalances = Boolean.FALSE;
    private RewardBonusMap rewardBonusMap; //TODO: Deprecated?
    private PlayerRewardTypeHistory playerRewardTypeHistory;
    private Operation freePlayOperation;
    private Boolean setFreePlayValue = Boolean.FALSE;
    private Long externalTimestamp;
    private Boolean shouldCompleteBetRound = Boolean.TRUE;
    private Boolean playerBalanceCheck = Boolean.TRUE;
}
