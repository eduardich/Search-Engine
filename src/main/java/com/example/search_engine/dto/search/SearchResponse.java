package com.example.search_engine.dto.search;

import com.example.search_engine.dto.simpleResponses.SimpleResponse;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponse implements SimpleResponse {

    boolean result;
    int count;
    List<SearchResultDTO> data;

}
