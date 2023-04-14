package lithium.service.cashier.processor.paynl.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;


@Data
public class PaynlGeneralException extends Exception{
    private String body;
    private String message;
    private HttpStatus httpStatus;
    
    public PaynlGeneralException(){}
    
    public PaynlGeneralException(String body) {
        this.body = body;
    }

    public PaynlGeneralException(HttpStatus httpStatus){
        this.httpStatus = httpStatus;
    }
    
}
