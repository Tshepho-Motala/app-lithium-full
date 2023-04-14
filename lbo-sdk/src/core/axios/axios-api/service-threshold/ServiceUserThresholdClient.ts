import AxiosApiClient from "@/core/axios/AxiosApiClient";
import {DomainItemInterface} from "@/plugin/cms/models/DomainItem";
import {TableContract} from "@/core/axios/axios-api/generic/TableContract";
import {
  DepositLimitThresholdInterface,
  LossLimitThresholdInterface
} from "@/core/interface/player/PlayerProtectionInterface";

export default class ServiceUserThresholdClient extends AxiosApiClient{

  localPrefix: string = 'service-user-threshold/backoffice/threshold/warnings/'
  livePrefix: string = 'services/service-user-threshold/backoffice/threshold/warnings/'


  getLossLimitThreshold(domains: string[] ,startDate: string,endDate:string): Promise<TableContract<LossLimitThresholdInterface> | null> {
    const domainString = domains.join(',')
    return this.postWithParameters(
        {
          domains: domainString,
          playerGuid:'',
          typeName:'LOSS_LIMIT',
          granularity:'',
          dateStart:startDate,
          dateEnd:endDate,
          draw: '0',
          start:'0',
          length:'5000'
        },
        domains[0]+'/v1/find'
    )
  }

  getDepositLimitThreshold(domains: string[] ,startDate: string,endDate:string): Promise<TableContract<DepositLimitThresholdInterface> | null> {
    const domainString = domains.join(',')
    return this.postWithParameters(
        {
          domains: domainString,
          playerGuid:'',
          typeName:'DEPOSIT_LIMIT',
          granularity:'',
          dateStart:startDate,
          dateEnd:endDate,
          draw: '0',
          start:'0',
          length:'5000'
        },
        domains[0]+'/v1/find'
    )
  }

}
