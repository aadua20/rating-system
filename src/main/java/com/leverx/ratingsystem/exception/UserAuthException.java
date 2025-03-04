package com.leverx.ratingsystem.exception;


import com.leverx.ratingsystem.util.MessageFormatter;

public class UserAuthException extends RuntimeException {

    public UserAuthException(String message, Object... args) {
        super(MessageFormatter.format(message, args));
    }
}
