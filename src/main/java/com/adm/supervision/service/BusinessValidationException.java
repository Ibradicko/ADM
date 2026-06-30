package com.adm.supervision.service;

import com.adm.supervision.web.rest.errors.BadRequestAlertException;

public class BusinessValidationException extends BadRequestAlertException {

    public BusinessValidationException(String defaultMessage) {
        super(defaultMessage, "workflow", "businessValidation");
    }

    public BusinessValidationException(String entityName, String errorKey, String defaultMessage) {
        super(defaultMessage, entityName, errorKey);
    }
}
