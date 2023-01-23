package com.example.search_engine.services;

import com.example.search_engine.dto.simpleResponses.ErrorResponse;
import com.example.search_engine.dto.simpleResponses.SimpleResponse;
import com.example.search_engine.dto.simpleResponses.SuccessResponse;
import org.springframework.stereotype.Service;

@Service
public class SimpleResponseServiceImpl implements SimpleResponseService {

    @Override
    public SimpleResponse getSuccessResponse() {
        return new SuccessResponse();
    }

    @Override
    public SimpleResponse getErrorResponse(String errorInfo) {
        return new ErrorResponse(errorInfo);
    }
}
