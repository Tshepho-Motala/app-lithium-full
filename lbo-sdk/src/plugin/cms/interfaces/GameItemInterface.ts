export interface GameItemResponseInterface {
    data: GameItemInterface[];
    data2?: any;
    message?: any;
    status: number;
    successful: boolean;
}

export default interface GameItemInterface {
    id: number;
    name: string;
    commercialName: string;
    domain: GameItemDomainInterface;
    providerGameId: string;
    enabled: boolean;
    visible: boolean;
    locked: boolean;
    lockedMessage?: any;
    hasLockImage: boolean;
    guid: string;
    description: string;
    rtp?: number;
    providerGuid: string;
    freeSpinEnabled?: boolean;
    freeSpinValueRequired?: boolean;
    freeSpinPlayThroughEnabled?: boolean;
    casinoFreeBetEnabled?: boolean;
    gameCurrency: GameItemCurrencyInterface;
    gameSupplier: GameItemSupplierInterface;
    gameType: GameItemTypeInterface;
    labels: GameItemLabelsInterface;
    progressiveJackpot: boolean;
    networkedJackpotPool: boolean;
    localJackpotPool: boolean;
    freeGame: boolean;
    cdnImageUrl: string;
}

export interface GameItemDomainInterface {
    id: number;
    version: number;
    name: string;
}

export interface GameItemCurrencyInterface {
    currencyCode: string;
    minimumAmountCents: number;
}

export interface GameItemSupplierInterface {
    id: number;
    version: number;
    domain: GameItemDomainInterface;
    name: string;
    deleted: boolean;
}

export interface GameItemOsInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemTagInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemNullInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemTypeInterface {
    id: number;
    version: number;
    domain: GameItemDomainInterface;
    name: string;
    deleted: boolean;
}

export interface GameItemTypeAltInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemContentProviderInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemVolatilityInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemBrandedInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemGenreInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemMarketInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemRtpInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemReleaseDateInterface {
    name: string;
    value: string;
    domainName: string;
    enabled: boolean;
    deleted: boolean;
}

export interface GameItemLabelsInterface {
    os: GameItemOsInterface;
    TAG: GameItemTagInterface;
    null: GameItemNullInterface;
    'Game Type': GameItemTypeAltInterface;
    'Content Provider': GameItemContentProviderInterface;
    Volatility: GameItemVolatilityInterface;
    Branded: GameItemBrandedInterface;
    Genre: GameItemGenreInterface;
    Market: GameItemMarketInterface;
    RTP: GameItemRtpInterface;
    'Release Date': GameItemReleaseDateInterface;
}


