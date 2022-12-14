package com.example.dalamb;

import com.example.dalamb.dto.request.HelloWorldRequest;
import com.example.dalamb.dto.response.HelloWorldResponse;
import org.ericlee.dalamb.api.annotation.Action;
import org.ericlee.dalamb.api.annotation.Topic;
import org.ericlee.dalamb.api.annotation.request.Body;

@Topic("helloWorld")
public class HelloWorld {
    @Action("hello")
    public HelloWorldResponse hello(@Body HelloWorldRequest request) {
        return HelloWorldResponse.builder()
                .name(request.getName())
                .message(String.format("To.%s : Hello, World!", request.getName()))
                .build();
    }
}
