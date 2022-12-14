package com.example.dalamb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HelloWorldResponse {
    private String name;
    private String message;
}
