
import DepositLimitInterface, {
  LossLimitThresholdInterface
} from "@/core/interface/player/PlayerProtectionInterface";

export default interface PlayerProtectionProviderInterface{

  getDepositLimits(): Promise<DepositLimitInterface[]>
  getLossLimitThresholds() :Promise<LossLimitThresholdInterface[]>
}
