package com.example.search_engine.services.searchLogic;

import com.example.search_engine.dto.search.SearchResponse;
import com.example.search_engine.dto.search.SearchResultDTO;
import com.example.search_engine.dto.simpleResponses.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchResponseServiceImpl implements SearchResponseService {

    private final SearchService searchService;

    @Override
    public SimpleResponse getSearchResponse(String searchQueue, int offset, int limit, String siteUrl) {

        SearchResponse response = new SearchResponse();
        response.setResult(true);

        List<SearchResultDTO> resultDTOS = searchService.getResultDTOs(searchQueue, siteUrl);

        if (resultDTOS.isEmpty()) {
            response.setCount(0);
            response.setData(new ArrayList<>());
        } else {
            response.setCount(resultDTOS.size());
            resultDTOS = applyParams(resultDTOS, offset, limit);
            response.setData(resultDTOS);
        }
        return response;
    }

    private List<SearchResultDTO> applyParams(List<SearchResultDTO> resultDTOS, int offset, int limit) {
        return resultDTOS.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }


}
