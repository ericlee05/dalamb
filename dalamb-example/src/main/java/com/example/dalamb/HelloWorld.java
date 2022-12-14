package com.example.dalamb;

import com.example.dalamb.dto.request.HelloWorldRequest;
import com.example.dalamb.dto.response.HelloWorldResponse;
import org.ericlee.dalamb.api.annotation.Action;
import org.ericlee.dalamb.api.annotation.Topic;
import org.ericlee.dalamb.api.annotation.request.Required;

@Topic("helloWorld")
public class HelloWorld {
    @Action("hello")
    public HelloWorldResponse hello() { // @Required HelloWorldRequest request
        return HelloWorldResponse.builder()
                .name("someone") // request.getName()
                .message(String.format("To.%s : Hello, World!", "someone")) // request.getName()
                .build();
    }
}
