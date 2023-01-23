package com.example.search_engine.dto.simpleResponses;

import lombok.Data;

@Data
public class SuccessResponse implements SimpleResponse {

    boolean result = true;
}
