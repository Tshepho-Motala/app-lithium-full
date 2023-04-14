package lithium.service.cashier.mock.paynl.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum WebhookAction {
    PENDING("pending", "PENDING (20)", "Transaction is created and ready for processing.", "Cancel"),
    PAYOUT_ADD("payout:add", "PENDING (20)", "Payout is added to the processing engine,", "Cancel"),
    PAYOUT_SEND("payout:send", "PENDING (50)", "Payout is send to the clearing house.", ""),
    PAYOUT_RECEIVED("payout:received", "PAID (100)", "Payout is settle between the Payee and the Payer.", ""),
    NEW_PPT("new_ppt", "PAID (100)", "Confirmation that the payment Order is completed", ""),
    PAYOUT_REJECTED("payout:rejected", "DENIED (-63)", "Payout can't be performed due to blockes at the clearing house", ""),
    PAYOUT_STORNO("payout:storno", "CANCEL (-XX)", "Payout is performed but returned to the clearing house", ""),
    PAYOUT_FAILED("payout:failed", "FAILURE (-60)", "Payout is not successfull send to the clearing house due to invalid data.", "");

    @Getter
    private String call;
    @Getter
    private String paymentState;
    @Getter
    private String description;
    @Getter
    private String actionsPossible;
    
    public static WebhookAction getWebhookAction(String call){
        for (WebhookAction action : WebhookAction.values()){
            if (action.getCall().equals(call)) return action;
        }
        return null;
    }
}
