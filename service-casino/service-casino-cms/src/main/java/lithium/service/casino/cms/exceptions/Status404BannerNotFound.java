package lithium.service.casino.cms.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

/**
 * Created by Mahlori Ngobeni on 03/08/2022
 */
public class Status404BannerNotFound extends NotRetryableErrorCodeException {

    private static final int code = 404;

    public Status404BannerNotFound(String message) {
        super(code, message, NotRetryableErrorCodeException.class.getCanonicalName());
    }
}
