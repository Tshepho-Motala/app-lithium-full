package lithium.service.cashier.client.objects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AccountType {

    PLAYER_BALANCE ("PLAYER_BALANCE"),
    MANUAL_BALANCE_ADJUST("MANUAL_BALANCE_ADJUST");
    
    @Getter
    public String code;
    
}
