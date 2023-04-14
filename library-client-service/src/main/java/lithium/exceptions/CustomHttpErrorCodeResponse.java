package lithium.exceptions;

import lombok.Data;

@Data
public class CustomHttpErrorCodeResponse {

    private String message;
    private int status;
    private String error;
    private String error_description;
    private String exception;
    private String errorCode; // This is used internally to resolve exception types and rethrow the original type

    public static CustomHttpErrorCodeResponse from(ErrorCodeException exception){
        CustomHttpErrorCodeResponse httpErrorResponse = new CustomHttpErrorCodeResponse();
        httpErrorResponse.setErrorCode(exception.getErrorCode());
        httpErrorResponse.setMessage(exception.getMessage());
        httpErrorResponse.setStatus(exception.getCode());
        httpErrorResponse.setError("errorcodeexception_" + exception.getCode());
        httpErrorResponse.setError_description(exception.getMessage());
        httpErrorResponse.setException(exception.getClass().getName());
        return httpErrorResponse;
    }
}
