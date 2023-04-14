
import PlayerProtectionProviderInterface
  from "@/core/interface/provider/PlayerProtectionProviderInterface";
import DepositLimitInterface, {
  LossLimitThresholdInterface
} from "@/core/interface/player/PlayerProtectionInterface";

export default class PlayerProtectionProviderMock implements PlayerProtectionProviderInterface{
  getDepositLimits(): Promise<DepositLimitInterface[]> {
    return new Promise((res, rej) => {
      setTimeout(() => {
         let depositLimits: DepositLimitInterface[] =[
            {
              playerName: 'LBO Master',
              playerAccountId:'12345',
              creationDate:'2023-02-08',
              depositsInWeek:'50',
              dateTimeTriggerHit:'2023-02-08',
              weeklyLossLimit:'60',
              weeklyLossLimitUsed:'50'
        }]
        res(depositLimits)
      }, 1500)
    })
  }

  getLossLimitThresholds(): Promise<LossLimitThresholdInterface[]> {
    return Promise.resolve([]);
  }


}
