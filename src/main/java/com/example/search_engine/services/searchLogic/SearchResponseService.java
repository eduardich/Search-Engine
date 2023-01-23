package com.example.search_engine.services.searchLogic;

import com.example.search_engine.dto.simpleResponses.SimpleResponse;

public interface SearchResponseService {

    SimpleResponse getSearchResponse(String searchQueue, int offset, int limit, String siteUrl);

}
