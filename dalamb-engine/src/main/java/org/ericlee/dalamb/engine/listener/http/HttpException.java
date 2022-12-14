package org.ericlee.dalamb.engine.listener.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpException extends RuntimeException {
    private int status;
    private String message;
}
