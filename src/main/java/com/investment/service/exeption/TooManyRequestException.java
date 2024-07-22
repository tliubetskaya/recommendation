package com.investment.service.exeption;

import java.io.Serial;

public class TooManyRequestException extends ApplicationException{

    public static final String TYPE = "TOO_MANY_REQUESTS";
    @Serial
    private static final long serialVersionUID = 3503175149840244306L;
    public TooManyRequestException() {
        super(TYPE);
    }
}
