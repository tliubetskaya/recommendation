package com.investment.service.exeption;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ApplicationException extends RuntimeException {

    public static final String TYPE = "INTERNAL_SERVER_ERROR";

    private final String type;

    @Serial
    private static final long serialVersionUID = -37302152362691556L;

    public ApplicationException(final Throwable exception) {
        super(exception);
        this.type = TYPE;
    }

    public ApplicationException(final String type) {
        this.type = TYPE;
    }
}
