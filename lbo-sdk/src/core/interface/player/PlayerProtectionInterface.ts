export default  interface PlayerProtectionInterface{

}
export  interface DepositLimitThresholdInterface{
  playerName: string
  playerAccountId: string
  creationDate:string
  depositsInWeek:string
  dateTimeTriggerHit:string
  weeklyLossLimit:string
  weeklyLossLimitUsed:string
}
export  interface LossLimitUser{
  guid:string
  name:string
}
export   interface LossLimitThresholdRevision{
  percentage:number
  granularity:number
}
export interface LossLimitThresholdInterface{

   thresholdHitDate :string
   dailyLimit:string
   dailyLimitUsed:string
   weeklyLimit:string
   weeklyLimitUsed:string
   monthlyLimit:string
   monthlyLimitUsed:string
   amount:string
   user:LossLimitUser
  thresholdRevision:LossLimitThresholdRevision
  thresholdHit:string
}


