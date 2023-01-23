package com.example.search_engine.services;

import com.example.search_engine.dto.simpleResponses.SimpleResponse;

public interface SimpleResponseService {

    SimpleResponse getSuccessResponse();

    SimpleResponse getErrorResponse(String errorInfo);
}
