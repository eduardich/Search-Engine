package com.example.search_engine.dto.simpleResponses;

import lombok.Data;

@Data
public class ErrorResponse implements SimpleResponse {

    boolean result = false;
    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }



}
