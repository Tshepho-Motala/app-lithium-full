package lithium.csv.provider.threshold.data;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lithium.service.document.generation.client.objects.CsvContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThresholdDepositHistoryCsv implements CsvContent {

  @CsvBindByName( column = "Player Name" )
  @CsvBindByPosition( position = 0 )
  private String username;
  @CsvBindByName( column = "Domain Name" )
  @CsvBindByPosition( position = 1 )
  private String domainName;
  @CsvBindByName( column = "Account ID" )
  @CsvBindByPosition( position = 2 )
  private String playerId;
  @CsvBindByName( column = "Account Creation Date" )
  @CsvBindByPosition( position = 3 )
  private String accountCreationDate;
  @CsvBindByName( column = "Date" )
  @CsvBindByPosition( position = 4 )
  private String thresholdHitDate;
  @CsvBindByName( column = "Threshold Hit" )
  @CsvBindByPosition( position = 5 )
  private String thresholdHit;
  @CsvBindByName( column = "Loss Limit" )
  @CsvBindByPosition( position = 6 )
  private String lossLimit;
  @CsvBindByName( column = "Loss Limit Used" )
  @CsvBindByPosition( position = 7 )
  private String lossLimitUsed;
  @CsvBindByName( column = "Threshold" )
  @CsvBindByPosition( position = 8 )
  private String threshold;
  @CsvBindByName( column = "Deposit Amount (Period)" )
  @CsvBindByPosition( position = 9 )
  private String depositAmount;
  @CsvBindByName( column = "Withdrawal Amount (Period)" )
  @CsvBindByPosition( position = 10 )
  private String withdrawalAmount;
  @CsvBindByName( column = "Net Life Time Deposit" )
  @CsvBindByPosition( position = 11 )
  private String netLifetimeDepositAmount;
}
