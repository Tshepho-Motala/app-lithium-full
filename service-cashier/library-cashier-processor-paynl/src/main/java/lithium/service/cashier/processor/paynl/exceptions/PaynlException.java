package lithium.service.cashier.processor.paynl.exceptions;

import lombok.Data;

import java.util.List;


@Data
public class PaynlException extends PaynlGeneralException {
    private List<Error> errorsList;

    public PaynlException(List<Error> errorsList, String body){
        super(body);
        this.errorsList = errorsList;
    }
    
}
