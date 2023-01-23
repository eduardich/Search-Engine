package com.example.search_engine.services.searchLogic;


import com.example.search_engine.dto.search.SearchResultDTO;

import java.util.List;

public interface SearchService {

    List<SearchResultDTO> getResultDTOs(String searchStr, String siteUrl);


}
