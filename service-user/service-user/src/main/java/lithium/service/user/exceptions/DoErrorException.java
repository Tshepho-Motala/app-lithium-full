package lithium.service.user.exceptions;

import com.netflix.client.ClientException;

public class DoErrorException extends ClientException {
    private static final long serialVersionUID = -6198030867401174526L;

    public DoErrorException(String message) {
        super(message);
    }

    public DoErrorException(String message, Throwable chainedException) {
        super(message, chainedException);
    }

    public DoErrorException(Throwable chainedException) {
        super(chainedException);
    }
}