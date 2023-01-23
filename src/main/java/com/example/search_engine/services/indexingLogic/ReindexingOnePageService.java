package com.example.search_engine.services.indexingLogic;

import com.example.search_engine.dto.simpleResponses.SimpleResponse;

public interface ReindexingOnePageService {
    SimpleResponse reindexUrl(String url);

}
