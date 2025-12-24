package com.chirper.frontend.application.exception;

import com.chirper.frontend.domain.valueobject.FieldError;

import java.util.List;

/**
 * バリデーションエラー例外
 */
public class ValidationException extends RuntimeException {

    private final List<FieldError> errors;

    public ValidationException(List<FieldError> errors) {
        super("Validation failed: " + errors.size() + " error(s)");
        this.errors = errors;
    }

    public ValidationException(String message, List<FieldError> errors) {
        super(message);
        this.errors = errors;
    }

    public List<FieldError> getErrors() {
        return errors;
    }
}
