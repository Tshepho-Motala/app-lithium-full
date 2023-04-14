package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status415BonusPrerequisitesNotMetException extends ErrorCodeException {
    public static final int CODE = 415;
    public Status415BonusPrerequisitesNotMetException(String prerequisiteBonusName) {
        super(CODE, "Bonus prerequisites not met. Prerequisite bonus required: " + prerequisiteBonusName, Status415BonusPrerequisitesNotMetException.class.getCanonicalName());
    }
}
