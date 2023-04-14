export interface PlayerRewardHistoryContract {
    id: number;
    awardedDate: string;
    redeemedDate: string;
    expiryDate: string;
    playerGuid: string;
    rewardCode: string;
    rewardName: string;
    status: string;
    cancellable: boolean
    rewardTypes: PlayerRewardTypeHistoryListContract;
}

export interface PlayerRewardTypeHistoryContract {
    id: number;
    status: number;
    rewardTypeName: string;
    playerRewardHistoryId: number;
    rewardName: string;
    rewardCode: string;
    playerGuid: string;
    awardedOn: string;
    created: string;
    typeCounter: number;
    cancellable: boolean
}

export interface PlayerRewardHistoryQueryContract {
    domainName: string,
    historyStatus: string[];
    rewardCode: string;
    awardedDateFrom: string;
    awardedDateTo: string;
    redeemedDateFrom: string;
    redeemedDateTo: string;
    expiryDateFrom: string;
    expiryDateTo: string;
    playerGuid: string;
}

export interface PlayerRewardHistoryListContract extends Array<PlayerRewardHistoryContract> {}
export interface PlayerRewardTypeHistoryListContract extends Array<PlayerRewardTypeHistoryContract> {}
