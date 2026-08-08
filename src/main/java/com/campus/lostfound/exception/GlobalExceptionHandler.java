package com.campus.lostfound.exception;

import com.campus.lostfound.dto.ErrorResponse;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class GlobalExceptionHandler {
    public static void register(Javalin app) {
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(new ErrorResponse(e.getMessage()));
        });

        app.exception(ItemNotFoundException.class, (e, ctx) -> {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(new ErrorResponse(e.getMessage()));
        });

        app.exception(ItemAlreadyResolvedException.class, (e, ctx) -> {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(new ErrorResponse(e.getMessage()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.json(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        });
    }
}
