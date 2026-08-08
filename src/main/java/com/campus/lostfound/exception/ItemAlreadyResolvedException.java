package com.campus.lostfound.exception;

public class ItemAlreadyResolvedException extends RuntimeException {
    public ItemAlreadyResolvedException(String message) {
        super(message);
    }
}
