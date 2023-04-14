import GameItemInterface from "@/plugin/cms/interfaces/GameItemInterface"
import CurrencyInterface from "@/plugin/cms/interfaces/CurrencyInterface"
import GameSupplierInterface from "@/plugin/cms/interfaces/GameSupplierInterface"

export default interface ProgressiveJackpotBalanceInterface {
    id

    version

    progressiveId: string

    amount: number

    wonByAmount: number

    game: GameItemInterface

    currency: CurrencyInterface

    GameSupplier: GameSupplierInterface

}

export interface ProgressiveJackpotGameBalanceInterface {
    id

    version

    progressiveId: string

    amount: number

    wonByAmount: number

    game: GameItemInterface

    currency: CurrencyInterface
}

export interface ProgressiveJackpotFeedInterface {

    id: number

    version: number

    registeredOn: Date

    lastUpdatedOn: Date

    gameSupplier: GameSupplierInterface

}
