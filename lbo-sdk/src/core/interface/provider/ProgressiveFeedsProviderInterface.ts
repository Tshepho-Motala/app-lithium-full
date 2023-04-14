import ProgressiveJackpotBalanceInterface, {ProgressiveJackpotGameBalanceInterface} from "@/plugin/cms/interfaces/ProgressiveJackpotBalanceInterface"

export default interface ProgressiveFeedsProviderInterface {

    findProgressiveJackpotGameFeedsByDomain(domainName: string): Promise<ProgressiveJackpotGameBalanceInterface[]>

    findProgressiveJackpotFeedsByDomain(domainName: string): Promise<ProgressiveJackpotBalanceInterface[]>

}